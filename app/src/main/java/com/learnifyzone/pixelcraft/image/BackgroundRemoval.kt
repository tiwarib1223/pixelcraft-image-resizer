package com.learnifyzone.pixelcraft.image

import android.content.Context
import android.graphics.Bitmap
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import java.nio.FloatBuffer
import java.util.concurrent.ConcurrentHashMap

/**
 * On-device background removal, no upload.
 *
 * Engine chain, strongest first:
 *  1. U-2-Netp (bundled) - light alpha-matte model, the quality he approved.
 *  2. ML Kit subject segmentation - last resort if the ONNX model cannot init.
 *
 * (v0.8.0 tried the heavier ISNet model first; on his real photos it left
 * background patches, so v0.8.1 reverted to U-2-Netp and the model was removed.)
 *
 * All engines produce an alpha matte that is upscaled with filtering, feathering
 * the edge instead of leaving a hard staircase. Everything runs fully offline.
 *
 * Blocking - call from a background dispatcher.
 */
object BackgroundRemoval {

    private class ModelSpec(
        val asset: String,
        val inputSize: Int,
        val mean: FloatArray,
        val std: FloatArray
    )

    private val U2NETP = ModelSpec("models/u2netp.onnx", 320, floatArrayOf(0.485f, 0.456f, 0.406f), floatArrayOf(0.229f, 0.224f, 0.225f))

    private val sessions = ConcurrentHashMap<String, OrtSession>()

    @Synchronized
    private fun session(context: Context, spec: ModelSpec): OrtSession {
        sessions[spec.asset]?.let { return it }
        val env = OrtEnvironment.getEnvironment()
        val bytes = context.assets.open(spec.asset).use { it.readBytes() }
        val options = OrtSession.SessionOptions().apply { setIntraOpNumThreads(4) }
        val created = env.createSession(bytes, options)
        sessions[spec.asset] = created
        return created
    }

    fun cutOut(context: Context, bitmap: Bitmap): Bitmap {
        for (spec in listOf(U2NETP)) {
            try {
                return onnxCutOut(context, bitmap, spec)
            } catch (e: Exception) {
                // Try the next engine in the chain.
            }
        }
        return mlKitCutOut(bitmap)
    }

    /** Fraction of pixels that are (near-)transparent - 0f..1f. Used to tell a REAL cut from a no-op. */
    fun transparentFraction(bitmap: Bitmap): Float {
        val w = bitmap.width; val h = bitmap.height
        val px = IntArray(w * h)
        bitmap.getPixels(px, 0, w, 0, 0, w, h)
        var clear = 0
        for (c in px) if ((c ushr 24) < 16) clear++
        return clear.toFloat() / px.size
    }

    /**
     * Solid-background remover for logos/icons on a plain colour (his A2Z logo case): the subject
     * engines keep a whole icon card as one "subject", so nothing is cut. Here we sample the four
     * corner colours and flood-remove every edge-connected pixel close to any of them.
     * Returns the cut bitmap and how many pixels were removed.
     */
    fun removeSolidBackground(bitmap: Bitmap, tolerance: Float = 0.14f): Pair<Bitmap, Int> {
        val w = bitmap.width; val h = bitmap.height
        val px = IntArray(w * h)
        bitmap.getPixels(px, 0, w, 0, 0, w, h)
        fun avgPatch(cx: Int, cy: Int): FloatArray {
            var r = 0f; var g = 0f; var b = 0f; var n = 0
            for (dy in -2..2) for (dx in -2..2) {
                val x = (cx + dx).coerceIn(0, w - 1); val y = (cy + dy).coerceIn(0, h - 1)
                val c = px[y * w + x]
                r += ((c shr 16) and 0xFF) / 255f; g += ((c shr 8) and 0xFF) / 255f; b += (c and 0xFF) / 255f
                n++
            }
            return floatArrayOf(r / n, g / n, b / n)
        }
        val corners = listOf(avgPatch(0, 0), avgPatch(w - 1, 0), avgPatch(0, h - 1), avgPatch(w - 1, h - 1))
        fun isBg(i: Int): Boolean {
            val c = px[i]
            if ((c ushr 24) < 16) return true   // already transparent counts as background
            val r = ((c shr 16) and 0xFF) / 255f; val g = ((c shr 8) and 0xFF) / 255f; val b = (c and 0xFF) / 255f
            for (k in corners) {
                val d = (r - k[0]) * (r - k[0]) + (g - k[1]) * (g - k[1]) + (b - k[2]) * (b - k[2])
                if (d <= tolerance * tolerance * 3f) return true
            }
            return false
        }
        val visited = BooleanArray(w * h)
        val stack = IntArray(w * h)
        var top = 0
        fun push(i: Int) { if (!visited[i]) { visited[i] = true; stack[top++] = i } }
        for (x in 0 until w) { if (isBg(x)) push(x); if (isBg((h - 1) * w + x)) push((h - 1) * w + x) }
        for (y in 0 until h) { if (isBg(y * w)) push(y * w); if (isBg(y * w + w - 1)) push(y * w + w - 1) }
        var removed = 0
        while (top > 0) {
            val i = stack[--top]
            if (!isBg(i)) continue
            px[i] = 0
            removed++
            val x = i % w; val y = i / w
            if (x > 0) push(i - 1)
            if (x < w - 1) push(i + 1)
            if (y > 0) push(i - w)
            if (y < h - 1) push(i + w)
        }
        return Pair(Bitmap.createBitmap(px, w, h, Bitmap.Config.ARGB_8888), removed)
    }

    private fun onnxCutOut(context: Context, bitmap: Bitmap, spec: ModelSpec): Bitmap {
        val sess = session(context, spec)
        val w = spec.inputSize
        val h = spec.inputSize
        val small = Bitmap.createScaledBitmap(bitmap, w, h, true)
        val px = IntArray(w * h)
        small.getPixels(px, 0, w, 0, 0, w, h)
        small.recycle()
        val data = FloatArray(3 * w * h)
        for (i in px.indices) {
            val c = px[i]
            data[i] = ((((c shr 16) and 0xFF) / 255f) - spec.mean[0]) / spec.std[0]
            data[w * h + i] = ((((c shr 8) and 0xFF) / 255f) - spec.mean[1]) / spec.std[1]
            data[2 * w * h + i] = (((c and 0xFF) / 255f) - spec.mean[2]) / spec.std[2]
        }
        val env = OrtEnvironment.getEnvironment()
        OnnxTensor.createTensor(env, FloatBuffer.wrap(data), longArrayOf(1, 3, h.toLong(), w.toLong())).use { tensor ->
            val inputName = sess.inputNames.first()
            sess.run(mapOf(inputName to tensor)).use { result ->
                val mask = (result.get(0) as OnnxTensor).floatBuffer
                val maskArr = FloatArray(w * h)
                mask.get(maskArr)
                var minV = Float.MAX_VALUE
                var maxV = -Float.MAX_VALUE
                for (v in maskArr) {
                    if (v < minV) minV = v
                    if (v > maxV) maxV = v
                }
                val range = (maxV - minV).coerceAtLeast(1e-6f)
                // Alpha matte at model resolution.
                val matte = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val mp = IntArray(w * h)
                for (i in mp.indices) {
                    val a = (((maskArr[i] - minV) / range).coerceIn(0f, 1f) * 255f).toInt()
                    mp[i] = a shl 24
                }
                matte.setPixels(mp, 0, w, 0, 0, w, h)
                // Filtered upscale feathers the edge at full photo resolution.
                val fullMask = Bitmap.createScaledBitmap(matte, bitmap.width, bitmap.height, true)
                matte.recycle()
                val fp = IntArray(bitmap.width * bitmap.height)
                fullMask.getPixels(fp, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                fullMask.recycle()
                val src = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(src, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                for (i in src.indices) {
                    src[i] = (src[i] and 0x00FFFFFF) or (((fp[i] ushr 24) and 0xFF) shl 24)
                }
                return Bitmap.createBitmap(src, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            }
        }
    }

    private fun mlKitCutOut(bitmap: Bitmap): Bitmap {
        val options = SubjectSegmenterOptions.Builder()
            .enableForegroundBitmap()
            .build()
        val segmenter = SubjectSegmentation.getClient(options)
        try {
            val result = Tasks.await(segmenter.process(InputImage.fromBitmap(bitmap, 0)))
            result.foregroundBitmap?.let { return it }
            val mask = result.foregroundConfidenceMask
                ?: throw IllegalStateException("No foreground found in this photo")
            mask.rewind()
            val w = bitmap.width
            val h = bitmap.height
            val pixels = IntArray(w * h)
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
            for (i in pixels.indices) {
                val confidence = mask.get()
                if (confidence < 0.5f) pixels[i] = 0
            }
            return Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888)
        } finally {
            segmenter.close()
        }
    }
}
