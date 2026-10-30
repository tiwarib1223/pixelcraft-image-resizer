package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

/**
 * Photo filters (his v0.16.5 request "Its possible to add Filter"). ColorMatrix-based so they are
 * fast, work fully on-device, and render identically in the live preview and the export.
 */
object FilterUtils {

    enum class FilterChoice(val id: String, val label: String) {
        Normal("normal", "Normal"),
        Mono("mono", "B&W"),
        Warm("warm", "Warm"),
        Cool("cool", "Cool"),
        Sepia("sepia", "Vintage"),
        Bright("bright", "Bright"),
        Fade("fade", "Fade"),
        Vivid("vivid", "Vivid")
    }

    fun matrixFor(id: String): ColorMatrix? = when (id) {
        "mono" -> ColorMatrix().apply { setSaturation(0f) }
        "warm" -> ColorMatrix(
            floatArrayOf(
                1.12f, 0f, 0f, 0f, 10f,
                0f, 1.02f, 0f, 0f, 4f,
                0f, 0f, 0.88f, 0f, -8f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "cool" -> ColorMatrix(
            floatArrayOf(
                0.92f, 0f, 0f, 0f, -6f,
                0f, 1f, 0f, 0f, 2f,
                0f, 0f, 1.12f, 0f, 12f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "sepia" -> ColorMatrix(
            floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "bright" -> ColorMatrix(
            floatArrayOf(
                1.1f, 0f, 0f, 0f, 18f,
                0f, 1.1f, 0f, 0f, 18f,
                0f, 0f, 1.1f, 0f, 18f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "fade" -> ColorMatrix(
            floatArrayOf(
                0.85f, 0f, 0f, 0f, 24f,
                0f, 0.85f, 0f, 0f, 24f,
                0f, 0f, 0.85f, 0f, 24f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "vivid" -> ColorMatrix().apply { setSaturation(1.45f) }
        else -> null
    }

    /** Paint carrying the filter for live drawing, or null for Normal. */
    fun filterPaint(id: String): Paint? {
        val m = matrixFor(id) ?: return null
        return Paint(Paint.ANTI_ALIAS_FLAG).apply { colorFilter = ColorMatrixColorFilter(m) }
    }

    /** New bitmap with the filter baked in (filter thumbnails and export). */
    fun applyFilter(src: Bitmap, id: String): Bitmap {
        val p = filterPaint(id) ?: return src.copy(src.config ?: Bitmap.Config.ARGB_8888, false)
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        Canvas(out).drawBitmap(src, 0f, 0f, p)
        return out
    }
}
