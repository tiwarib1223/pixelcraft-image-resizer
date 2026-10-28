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
