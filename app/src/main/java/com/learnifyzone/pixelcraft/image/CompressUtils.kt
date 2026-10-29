package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * Target-size compression for the Compress tool.
 *
 * Images: try a quality ladder at the original dimensions first (best quality
 * that fits wins); if even the lowest quality misses the target, step the
 * dimensions down and retry. PNG is lossless, so for PNG only the dimension
 * steps apply. The result is always the smallest file that fits the target,
 * or the smallest achievable file when the target is unreachable.
 *
 * PDF: pages are re-rendered at a resolution ladder (highest resolution that
 * fits the target wins) and rebuilt with PdfDocument.
 */
object CompressUtils {

    private val QUALITY_LADDER = intArrayOf(85, 70, 55, 40, 25, 12)
    private val SCALE_STEPS = floatArrayOf(1f, 0.8f, 0.64f, 0.51f, 0.41f, 0.33f)
    private const val MIN_DIMENSION = 320

    private fun encode(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(format, quality.coerceIn(1, 100), baos)
        return baos.toByteArray()
    }

    /** [format] is the OUTPUT format (sources that are not JPG/PNG/WebP become JPEG). */
    fun compressImage(src: Bitmap, format: Bitmap.CompressFormat, targetBytes: Long): ByteArray {
        val flat = if (format == Bitmap.CompressFormat.JPEG) ConvertFormats.flatten(src, android.graphics.Color.WHITE) else src
        var best: ByteArray? = null
        var current = flat
        for (s in SCALE_STEPS) {
            if (s != 1f) {
                val w = (flat.width * s).toInt()
                val h = (flat.height * s).toInt()
                if (w < MIN_DIMENSION || h < MIN_DIMENSION) break
                current = Bitmap.createScaledBitmap(flat, w, h, true)
            }
            if (format == Bitmap.CompressFormat.PNG) {
                val bytes = encode(current, format, 100)
                if (bytes.size <= targetBytes) return bytes
                if (best == null || bytes.size < best.size) best = bytes
            } else {
                for (q in QUALITY_LADDER) {
                    val bytes = encode(current, format, q)
                    if (bytes.size <= targetBytes) return bytes
                    if (best == null || bytes.size < best.size) best = bytes
                }
            }
        }
        return best ?: encode(current, format, 12)
    }

    /** Rebuilds a PDF from rendered page bitmaps at the highest resolution that fits. */
    fun compressPdfPages(pages: List<Bitmap>, targetBytes: Long): ByteArray {
        var best: ByteArray? = null
        for (s in floatArrayOf(1f, 0.85f, 0.7f, 0.55f, 0.4f)) {
            val scaled = if (s == 1f) pages else pages.map {
                Bitmap.createScaledBitmap(
                    it,
                    (it.width * s).toInt().coerceAtLeast(1),
                    (it.height * s).toInt().coerceAtLeast(1),
                    true
                )
            }
            val bytes = ConvertFormats.encodePdf(scaled, android.graphics.Color.WHITE)
            if (bytes.size <= targetBytes) return bytes
            if (best == null || bytes.size < best.size) best = bytes
        }
        return best ?: ConvertFormats.encodePdf(pages, android.graphics.Color.WHITE)
    }

    /** Whole number 0-100; 0 when there is nothing to save. */
    fun percentSaved(origBytes: Long, newBytes: Long): Int {
        if (origBytes <= 0 || newBytes >= origBytes) return 0
        return (((origBytes - newBytes).toDouble() / origBytes.toDouble()) * 100.0).toInt()
    }
}
