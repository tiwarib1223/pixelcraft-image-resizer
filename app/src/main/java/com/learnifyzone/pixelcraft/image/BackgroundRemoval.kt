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

/**
 * On-device background removal, no upload.
 *
 * Primary engine: U-2-Netp (u2netp.onnx, bundled in assets) - the open segmentation
 * model behind many offline cut-out tools. It produces a full alpha matte, which
 * gives noticeably cleaner edges (fur, hair, product outlines) than a coarse
 * subject mask. The mask is upscaled with bilinear filtering, which feathers the
 * edge instead of leaving a hard staircase.
 *
 * Fallback: ML Kit subject segmentation, if the ONNX engine cannot initialize
 * (e.g. missing native libs on an unusual device). Both run fully offline; ML Kit
 * downloads its small model once on first use.
 *
 * Blocking - call from a background dispatcher.
 */
object BackgroundRemoval {

    private const val MODEL_INPUT = 320

    @Volatile private var ortSession: OrtSession? = null

    @Synchronized
    private fun session(context: Context): OrtSession {
        ortSession?.let { return it }
        val env = OrtEnvironment.getEnvironment()
        val bytes = context.assets.open("models/u2netp.onnx").use { it.readBytes() }
        val options = OrtSession.SessionOptions().apply { setIntraOpNumThreads(4) }
        return env.createSession(bytes, options).also { ortSession = it }
    }

    fun cutOut(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            u2netCutOut(context, bitmap)
        } catch (e: Exception) {
            mlKitCutOut(bitmap)
        }
    }

    private fun u2netCutOut(context: Context, bitmap: Bitmap): Bitmap {
        val sess = session(context)
        val w = MODEL_INPUT
        val h = MODEL_INPUT
        val small = Bitmap.createScaledBitmap(bitmap, w, h, true)
        val px = IntArray(w * h)
        small.getPixels(px, 0, w, 0, 0, w, h)
        small.recycle()
        // ImageNet normalization, CHW float32 - what u2netp was trained with.
        val data = FloatArray(3 * w * h)
        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)
        for (i in px.indices) {
            val c = px[i]
            data[i] = ((((c shr 16) and 0xFF) / 255f) - mean[0]) / std[0]
            data[w * h + i] = ((((c shr 8) and 0xFF) / 255f) - mean[1]) / std[1]
            data[2 * w * h + i] = (((c and 0xFF) / 255f) - mean[2]) / std[2]
        }
        val env = OrtEnvironment.getEnvironment()
        OnnxTensor.createTensor(env, FloatBuffer.wrap(data), longArrayOf(1, 3, h.toLong(), w.toLong())).use { tensor ->
            val inputName = sess.inputNames.first()
            sess.run(mapOf(inputName to tensor)).use { result ->
                val mask = (result.get(0) as OnnxTensor).floatBuffer
                val maskArr = FloatArray(w * h)
                mask.get(maskArr)
                var maxV = 1e-6f
                for (v in maskArr) if (v > maxV) maxV = v
                // Alpha matte at model resolution.
                val matte = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val mp = IntArray(w * h)
                for (i in mp.indices) {
                    val a = ((maskArr[i] / maxV).coerceIn(0f, 1f) * 255f).toInt()
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
