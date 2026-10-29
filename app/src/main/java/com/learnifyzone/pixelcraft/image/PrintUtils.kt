package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Print Size & DPI engine. All math is in inches internally; presets carry exact
 * metric or inch definitions. Everything here is pure/host-testable except renderPrint,
 * which is exercised through Robolectric native graphics.
 */
object PrintUtils {
    /** Light-grey background for transparent images - clearly visible against white (#E0E0E0 - Material grey 300, the clearly visible end of his soft-grey range; the old #F2F2F2 was
     *  indistinguishable from White, his v0.15.10 bug). ONE constant shared by the preview and the exporter so the
     *  two paths can never drift apart again. */
    const val LIGHT_GREY_BG: Int = 0xFFE0E0E0.toInt()


    enum class Quality { GOOD, ACCEPTABLE, LOW }

    data class Preset(val name: String, val wIn: Double, val hIn: Double, val desc: String, val group: String)

    private fun mm(v: Double) = v / 25.4

    val presets: List<Preset> = listOf(
        // Paper sizes
        Preset("A2", mm(420.0), mm(594.0), "420 × 594 mm", "Paper sizes"),
        Preset("A3", mm(297.0), mm(420.0), "297 × 420 mm", "Paper sizes"),
        Preset("A4", mm(210.0), mm(297.0), "210 × 297 mm", "Paper sizes"),
        Preset("A5", mm(148.0), mm(210.0), "148 × 210 mm", "Paper sizes"),
        Preset("A6", mm(105.0), mm(148.0), "105 × 148 mm", "Paper sizes"),
        // US paper sizes
        Preset("US Letter", 8.5, 11.0, "8.5 × 11 in", "Paper sizes (US)"),
        Preset("US Legal", 8.5, 14.0, "8.5 × 14 in", "Paper sizes (US)"),
        Preset("Tabloid", 11.0, 17.0, "11 × 17 in", "Paper sizes (US)"),
        // Photo sizes
        Preset("Wallet", 2.5, 3.5, "2.5 × 3.5 in", "Photo sizes"),
        Preset("4 × 6", 4.0, 6.0, "Standard photo", "Photo sizes"),
        Preset("5 × 7", 5.0, 7.0, "Framed photo", "Photo sizes"),
        Preset("8 × 10", 8.0, 10.0, "Portrait or artwork", "Photo sizes"),
        Preset("11 × 14", 11.0, 14.0, "Larger wall print", "Photo sizes"),
        Preset("16 × 20", 16.0, 20.0, "Large wall print", "Photo sizes"),
        // Square prints
        Preset("5 × 5", 5.0, 5.0, "Square print", "Square"),
        Preset("8 × 8", 8.0, 8.0, "Square print", "Square"),
        Preset("12 × 12", 12.0, 12.0, "Square print", "Square"),
        // ID / passport photos
        Preset("2 × 2 in", 2.0, 2.0, "US / India passport photo (51 × 51 mm)", "ID / Passport"),
        Preset("35 × 45 mm", mm(35.0), mm(45.0), "EU / UK / Schengen passport photo", "ID / Passport"),
        // Posters
        Preset("12 × 18", 12.0, 18.0, "Small poster", "Posters"),
        Preset("18 × 24", 18.0, 24.0, "Medium poster", "Posters"),
        Preset("24 × 36", 24.0, 36.0, "Large poster", "Posters")
    )

    val ppiOptions = listOf(150, 200, 300, 600)

    /** Convert a value in the given unit to inches. Unit is "in", "cm" or "mm". */
    fun toInches(value: Double, unit: String): Double = when (unit) {
        "cm" -> value / 2.54
        "mm" -> value / 25.4
        else -> value
    }

    /** Convert inches to the given display unit. */
    fun fromInches(inches: Double, unit: String): Double = when (unit) {
        "cm" -> inches * 2.54
        "mm" -> inches * 25.4
        else -> inches
    }

    /** Required pixels = print size in inches x PPI (rounded). */
    fun requiredPixels(wIn: Double, hIn: Double, ppi: Int): IntArray =
        intArrayOf((wIn * ppi).roundToInt(), (hIn * ppi).roundToInt())

    /**
     * Effective PPI if the image were printed at this size: pixels per inch on each
     * axis; the smaller axis limits print quality, so that is what we report.
     */
    fun effectivePpi(imgW: Int, imgH: Int, wIn: Double, hIn: Double): Double {
        if (wIn <= 0 || hIn <= 0) return 0.0
        return min(imgW / wIn, imgH / hIn)
    }

    /**
     * Quality verdict against the target PPI.
     * Good: meets or exceeds the target. Acceptable: within two thirds of the target
     * and still above the 120 PPI absolute floor. Low: below that.
     */
    fun qualityLevel(effectivePpi: Double, targetPpi: Int): Quality = when {
        effectivePpi >= targetPpi -> Quality.GOOD
        effectivePpi >= targetPpi * 2.0 / 3.0 && effectivePpi >= 120.0 -> Quality.ACCEPTABLE
        else -> Quality.LOW
    }

    /** Gap between copies on a multi-copy sheet: 3 mm. */
    const val SHEET_GAP_IN: Double = 3.0 / 25.4

    /** Even margin around a multi-copy sheet grid: 5 mm. */
    const val SHEET_MARGIN_IN: Double = 5.0 / 25.4

    /** Bleed allowance in inches: 3 mm on each side, the common pro-print default. */
    const val BLEED_IN: Double = 3.0 / 25.4

    /** Default white margin when margins are on: 0.25 in each side. */
    const val MARGIN_IN: Double = 0.25

    /**
     * Cap for raster (JPG/PNG) export canvases so huge poster jobs cannot OOM the
     * phone. 48 megapixels ≈ 192 MB as ARGB_8888 with headroom for the source.
     */
    const val MAX_EXPORT_PIXELS: Long = 48_000_000L

    /** Scale the required pixels down to fit the cap, preserving ratio. Returns [w,h]. */
    fun cappedPixels(wPx: Int, hPx: Int): IntArray {
        val total = wPx.toLong() * hPx.toLong()
        if (total <= MAX_EXPORT_PIXELS) return intArrayOf(wPx, hPx)
        val scale = kotlin.math.sqrt(MAX_EXPORT_PIXELS.toDouble() / total)
        return intArrayOf((wPx * scale).roundToInt().coerceAtLeast(1), (hPx * scale).roundToInt().coerceAtLeast(1))
    }

    /**
     * Render the source onto the output canvas.
     * mode "Fit": whole image visible, letterboxed on bg. "Fill": cover the canvas,
     * center-cropped. "Stretch": fill exactly (can distort).
     */
    fun renderPrint(src: Bitmap, outW: Int, outH: Int, mode: String, bg: Int): Bitmap {
        val out = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(bg)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
        val dst = RectF(0f, 0f, outW.toFloat(), outH.toFloat())
        val m = Matrix()
        when (mode) {
            "Fill" -> {
                val s = maxOf(outW.toFloat() / src.width, outH.toFloat() / src.height)
                val dw = src.width * s
                val dh = src.height * s
                m.setRectToRect(
                    RectF(0f, 0f, src.width.toFloat(), src.height.toFloat()),
                    RectF((outW - dw) / 2f, (outH - dh) / 2f, (outW + dw) / 2f, (outH + dh) / 2f),
                    Matrix.ScaleToFit.FILL
                )
            }
            "Stretch" -> m.setRectToRect(
                RectF(0f, 0f, src.width.toFloat(), src.height.toFloat()), dst, Matrix.ScaleToFit.FILL
            )
            else -> {
                val s = min(outW.toFloat() / src.width, outH.toFloat() / src.height)
                val dw = src.width * s
                val dh = src.height * s
                m.setRectToRect(
                    RectF(0f, 0f, src.width.toFloat(), src.height.toFloat()),
                    RectF((outW - dw) / 2f, (outH - dh) / 2f, (outW + dw) / 2f, (outH + dh) / 2f),
                    Matrix.ScaleToFit.FILL
                )
            }
        }
        canvas.drawBitmap(src, m, paint)
        return out
    }

    /** Placement rect (in page pixels) of the image inside the page for preview math. */
    fun placementRect(pageW: Float, pageH: Float, marginPx: Float, imgAspect: Float, mode: String): RectF {
        val availW = pageW - marginPx * 2
        val availH = pageH - marginPx * 2
        if (availW <= 0 || availH <= 0) return RectF(0f, 0f, pageW, pageH)
        return when (mode) {
            "Fill" -> {
                val s = maxOf(availW / imgAspect, availH)
                val dw = imgAspect * s
                val dh = s
                RectF((pageW - dw) / 2f, (pageH - dh) / 2f, (pageW + dw) / 2f, (pageH + dh) / 2f)
            }
            "Stretch" -> RectF(marginPx, marginPx, pageW - marginPx, pageH - marginPx)
            else -> {
                val s = min(availW / imgAspect, availH)
                val dw = imgAspect * s
                val dh = s
                RectF((pageW - dw) / 2f, (pageH - dh) / 2f, (pageW + dw) / 2f, (pageH + dh) / 2f)
            }
        }
    }

    /**
     * How many copies of a cell fit on a sheet in a neat grid with even gaps and
     * margins. Returns [cols, rows]; 0 when nothing fits.
     */
    fun gridFit(sheetWIn: Double, sheetHIn: Double, cellWIn: Double, cellHIn: Double, gapIn: Double, marginIn: Double): IntArray {
        val availW = sheetWIn - marginIn * 2 + gapIn
        val availH = sheetHIn - marginIn * 2 + gapIn
        if (availW <= 0 || availH <= 0 || cellWIn <= 0 || cellHIn <= 0) return intArrayOf(0, 0)
        val cols = kotlin.math.floor(availW / (cellWIn + gapIn)).toInt()
        val rows = kotlin.math.floor(availH / (cellHIn + gapIn)).toInt()
        return intArrayOf(maxOf(0, cols), maxOf(0, rows))
    }

    /**
     * Render a multi-copy sheet: white page, copies centered in a grid with even
     * gaps, thin cutting guides around each copy. Copies are Fill-cropped to the
     * exact ID photo size.
     */
    fun renderSheet(src: Bitmap, sheetWPx: Int, sheetHPx: Int, cellWPx: Int, cellHPx: Int, cols: Int, rows: Int, gapPx: Int, guides: Boolean, count: Int = Int.MAX_VALUE): Bitmap {
        val out = Bitmap.createBitmap(sheetWPx, sheetHPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(Color.WHITE)
        if (cols <= 0 || rows <= 0) return out
        val gridW = cols * cellWPx + (cols - 1) * gapPx
        val gridH = rows * cellHPx + (rows - 1) * gapPx
        val startX = (sheetWPx - gridW) / 2f
        val startY = (sheetHPx - gridH) / 2f
        val cell = renderPrint(src, cellWPx, cellHPx, "Fill", Color.WHITE)
        val guidePaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = maxOf(1f, sheetWPx / 1000f)
        }
        val total = minOf(count, cols * rows)
        for (i in 0 until total) {
            val r = i / cols
            val c = i % cols
            val x = startX + c * (cellWPx + gapPx)
            val y = startY + r * (cellHPx + gapPx)
            canvas.drawBitmap(cell, x, y, null)
            if (guides) canvas.drawRect(x, y, x + cellWPx, y + cellHPx, guidePaint)
        }
        return out
    }

    fun megapixels(wPx: Int, hPx: Int): String {
        val mp = wPx.toDouble() * hPx / 1_000_000.0
        return (Math.round(mp * 10) / 10.0).toString()
    }

    fun fmtDim(inches: Double, unit: String): String {
        val v = fromInches(inches, unit)
        val rounded = Math.round(v * 100) / 100.0
        return if (rounded == Math.floor(rounded)) rounded.toInt().toString() else rounded.toString()
    }

    val TRANSPARENT_BG_DEFAULT: Int = Color.WHITE
}
