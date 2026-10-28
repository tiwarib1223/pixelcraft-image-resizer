package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions

/**
 * On-device subject cut-out via ML Kit subject segmentation. No network call for the
 * segmentation itself; Play Services downloads the small model once on first use.
 * Blocking - call from a background dispatcher.
 */
object BackgroundRemoval {

    fun cutOut(bitmap: Bitmap): Bitmap {
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
