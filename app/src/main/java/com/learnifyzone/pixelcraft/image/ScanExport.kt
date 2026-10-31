package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Document Scanner export geometry (his original Scanner recording, Screen 7): page size,
 * orientation and margins for PDF/PNG/JPG export. Quality maps to a render DPI:
 * Standard 150, High 220, Maximum 300 (honest values, shown in the UI).
 */
object ScanExport {

    /** Page size in inches (portrait). */
    fun pageSizeInches(id: String): Pair<Double, Double>? = when (id) {
        "A4" -> 8.27 to 11.69
        "A5" -> 5.83 to 8.27
        "A3" -> 11.69 to 16.54
        "Letter" -> 8.5 to 11.0
        "Legal" -> 8.5 to 14.0
        else -> null   // "Original" keeps the page's own pixels
    }

    fun dpiFor(quality: String): Int = when (quality) {
        "Standard" -> 150
        "Maximum" -> 300
        else -> 220   // High
    }

    fun jpegQualityFor(quality: String): Int = when (quality) {
        "Standard" -> 82
        "Maximum" -> 97
        else -> 92
    }

    fun marginFraction(margin: String): Float = when (margin) {
        "Small" -> 0.04f
        "Medium" -> 0.08f
        else -> 0f
    }

    /**
     * Canvas pixels for a page. Orientation: "Automatic" matches the page's own shape,
     * "Portrait"/"Landscape" force the sheet. "Original" returns the page's own pixels.
     */
    fun sheetPixels(sizeId: String, orientation: String, pageW: Int, pageH: Int, quality: String): Pair<Int, Int>? {
        val sizeIn = pageSizeInches(sizeId) ?: return (pageW to pageH)
        val dpi = dpiFor(quality)
        var w = (sizeIn.first * dpi).roundToInt()
        var h = (sizeIn.second * dpi).roundToInt()
        val landscape = when (orientation) {
            "Portrait" -> false
            "Landscape" -> true
            else -> pageW > pageH   // Automatic follows the page
        }
        if (landscape) { val t = w; w = h; h = t }
        return w to h
    }

    /** Render a page onto a white sheet, fitted inside the margins without distortion. */
    fun renderPageOnSheet(page: Bitmap, sizeId: String, orientation: String, margin: String, quality: String, customMarginMm: Float = 0f): Bitmap {
        val (w, h) = sheetPixels(sizeId, orientation, page.width, page.height, quality) ?: (page.width to page.height)
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(Color.WHITE)
        // Custom margin is physical millimetres, converted to pixels at the export DPI (honest units).
        val m = if (margin == "Custom") (customMarginMm / 25.4f) * dpiFor(quality) else (w * marginFraction(margin))
        val availW = w - 2 * m; val availH = h - 2 * m
        val scale = min(availW / page.width, availH / page.height)
        val dw = page.width * scale; val dh = page.height * scale
        val left = (w - dw) / 2f; val top = (h - dh) / 2f
        canvas.drawBitmap(page, null, RectF(left, top, left + dw, top + dh), null)
        return out
    }
}
