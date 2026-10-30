package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * v0.17.0 Collage Maker engine (his 10-section spec). Pure bitmap math - no Android services,
 * fully offline, JVM-testable. ONE draw path feeds the editing preview and the export, so what
 * he sees is what he saves (his standing rule). Proportions are always kept: photos fill their
 * section with a centre-crop (never stretched); zoom >= 1 only ever crops MORE.
 *
 * Memory: callers keep editing bitmaps small (<=1024px); export re-decodes originals per section.
 */
object CollageMaker {

    const val MIN_PHOTOS = 2
    const val MAX_PHOTOS = 20

    /** One placed photo: the bitmap plus its adjustments inside its own section only. */
    class Photo(
        var bitmap: Bitmap,
        var zoom: Float = 1f,        // 1 = fill the section; >1 zooms in (crops more)
        var panX: Float = 0f,        // -1..1 across the available overflow
        var panY: Float = 0f,
        var rotation: Float = 0f,    // degrees clockwise
        var flipH: Boolean = false,
        var flipV: Boolean = false,
        var borderPx: Int = -1,      // -1 = use the collage-wide frame from Style
        var borderColor: Int = 0xFFFFFFFF.toInt()
    )

    /** A photo slot, as fractions of the canvas (0..1), before gap/margin insets. */
    class Section(val left: Float, val top: Float, val right: Float, val bottom: Float) {
        fun width() = right - left
        fun height() = bottom - top
    }

    enum class LayoutFamily { GRID, MIXED, VERTICAL, HORIZONTAL }

    /** Everything that styles the whole collage. bgColor = 0 means transparent (PNG/WebP). */
    class Style(
        var gapPx: Int = 8,
        var marginPx: Int = 0,
        var radiusPx: Int = 0,
        var borderPx: Int = 0,
        var borderColor: Int = 0xFFFFFFFF.toInt(),
        var bgColor: Int = 0xFFFFFFFF.toInt(),
        var bgGradient: IntArray? = null   // [top, bottom]; wins over bgColor when set
    )

    // ---------- Layout library ----------

    /** Preferred (cols, rows) for GRID at his named counts; orientation swaps on wide canvases. */
    private fun gridShape(n: Int, canvasW: Int, canvasH: Int): Pair<Int, Int> {
        val wide = canvasW > canvasH
        val named: Pair<Int, Int>? = when (n) {
            2 -> if (wide) Pair(2, 1) else Pair(1, 2)
            3 -> if (wide) Pair(3, 1) else Pair(1, 3)
            4 -> Pair(2, 2)
            6 -> if (wide) Pair(3, 2) else Pair(2, 3)
            8 -> if (wide) Pair(4, 2) else Pair(2, 4)
            9 -> Pair(3, 3)
            12 -> if (wide) Pair(4, 3) else Pair(3, 4)
            16 -> Pair(4, 4)
            else -> null
        }
        if (named != null) return named
        // Fallback for the in-between counts: near-square uniform grid, extra cells stay background.
        val aspect = canvasW.toFloat() / canvasH.toFloat()
        val cols = ceil(sqrt(n * aspect)).toInt().coerceIn(1, n)
        val rows = ceil(n.toFloat() / cols).toInt()
        return Pair(cols, rows)
    }

    private fun gridSections(n: Int, canvasW: Int, canvasH: Int): List<Section> {
        val (cols, rows) = gridShape(n, canvasW, canvasH)
        val out = ArrayList<Section>(n)
        for (i in 0 until n) {
            val r = i / cols
            val c = i % cols
            if (r >= rows) break   // more photos than cells cannot happen (rows*cols >= n), but stay safe
            out.add(Section(c.toFloat() / cols, r.toFloat() / rows, (c + 1).toFloat() / cols, (r + 1).toFloat() / rows))
        }
        return out
    }

    /** Hand-set asymmetric templates for 2-9 photos; a featured grid for 10-20. */
    private fun mixedSections(n: Int, canvasW: Int, canvasH: Int): List<Section> {
        when (n) {
            2 -> return listOf(
                Section(0f, 0f, 0.63f, 1f),
                Section(0.63f, 0f, 1f, 1f))
            3 -> return listOf(
                Section(0f, 0f, 0.63f, 1f),
                Section(0.63f, 0f, 1f, 0.5f),
                Section(0.63f, 0.5f, 1f, 1f))
            4 -> return listOf(
                Section(0f, 0f, 0.63f, 0.63f),
                Section(0.63f, 0f, 1f, 0.63f),
                Section(0f, 0.63f, 0.5f, 1f),
                Section(0.5f, 0.63f, 1f, 1f))
            5 -> return listOf(
                Section(0f, 0f, 0.6f, 0.6f),
                Section(0.6f, 0f, 1f, 0.5f),
                Section(0.6f, 0.5f, 1f, 1f),
                Section(0f, 0.6f, 0.5f, 1f),
                Section(0.5f, 0.6f, 1f, 1f))
            6 -> return listOf(
                Section(0f, 0f, 0.5f, 0.66f),
                Section(0.5f, 0f, 1f, 0.33f),
                Section(0.5f, 0.33f, 1f, 0.66f),
                Section(0f, 0.66f, 0.34f, 1f),
                Section(0.34f, 0.66f, 0.67f, 1f),
                Section(0.67f, 0.66f, 1f, 1f))
        }
        // 7-20: featured grid - the first cell spans 2x2, the rest fill row by row.
        // Shape the grid for n+3 cells: the featured photo occupies 4 cells for 1 photo,
        // so a grid shaped for exactly n would run out of cells and drop photos.
        val (cols, rows0) = gridShape(n + 3, canvasW, canvasH)
        val rows = max(rows0, 2)
        if (cols < 2 || rows < 2 || n < 5) return gridSections(n, canvasW, canvasH)
        val covered = Array(rows) { BooleanArray(cols) }
        val out = ArrayList<Section>(n)
        // Featured first photo.
        covered[0][0] = true; covered[0][1] = true; covered[1][0] = true; covered[1][1] = true
        out.add(Section(0f, 0f, 2f / cols, 2f / rows))
        for (i in 1 until n) {
            var placed = false
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (!covered[r][c]) {
                        covered[r][c] = true
                        out.add(Section(c.toFloat() / cols, r.toFloat() / rows, (c + 1).toFloat() / cols, (r + 1).toFloat() / rows))
                        placed = true
                        break
                    }
                }
                if (placed) break
            }
            if (!placed) return gridSections(n, canvasW, canvasH)   // ran out of cells - plain grid is better than dropping photos
        }
        return out
    }

    /** The layout library entry point: exactly [count] sections, all inside the unit square. */
    fun sections(family: LayoutFamily, count: Int, canvasW: Int, canvasH: Int): List<Section> {
        val n = count.coerceIn(MIN_PHOTOS, MAX_PHOTOS)
        return when (family) {
            LayoutFamily.GRID -> gridSections(n, canvasW, canvasH)
            LayoutFamily.MIXED -> mixedSections(n, canvasW, canvasH)
            LayoutFamily.VERTICAL -> (0 until n).map { Section(0f, it.toFloat() / n, 1f, (it + 1).toFloat() / n) }
            LayoutFamily.HORIZONTAL -> (0 until n).map { Section(it.toFloat() / n, 0f, (it + 1).toFloat() / n, 1f) }
        }
    }

    // ---------- Rendering ----------

    /**
     * Draw the whole collage at (canvasW x canvasH). Sections are inset by the margin (outside)
     * and half the gap (between neighbours). Every photo keeps its proportions: the draw scale
     * covers the section (plus the rotated bounding box when rotated), times the user's zoom.
     */
    fun render(photos: List<Photo>, sections: List<Section>, canvasW: Int, canvasH: Int, style: Style): Bitmap =
        renderSections({ photos[it] }, photos.size, sections, canvasW, canvasH, style)

    /**
     * Same output as [render], but each photo is produced lazily by [photoAt] right before its
     * section is drawn - hi-res export can decode originals one at a time instead of holding all
     * of them in memory. Callers recycle any bitmaps they created; this never recycles.
     */
    fun renderSections(photoAt: (Int) -> Photo, photoCount: Int, sections: List<Section>, canvasW: Int, canvasH: Int, style: Style): Bitmap {
        val out = Bitmap.createBitmap(canvasW, canvasH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val grad = style.bgGradient
        if (grad != null && grad.size >= 2) {
            val p = Paint()
            p.shader = LinearGradient(0f, 0f, 0f, canvasH.toFloat(), grad[0], grad[1], Shader.TileMode.CLAMP)
            canvas.drawRect(0f, 0f, canvasW.toFloat(), canvasH.toFloat(), p)
        } else {
            canvas.drawColor(style.bgColor)
        }
        val m = style.marginPx.toFloat().coerceAtLeast(0f)
        val halfGap = style.gapPx.toFloat().coerceAtLeast(0f) / 2f
        val areaW = canvasW - 2 * m
        val areaH = canvasH - 2 * m
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val count = min(photoCount, sections.size)
        for (i in 0 until count) {
            val s = sections[i]
            val rect = RectF(
                m + s.left * areaW + halfGap,
                m + s.top * areaH + halfGap,
                m + s.right * areaW - halfGap,
                m + s.bottom * areaH - halfGap)
            if (rect.width() < 2f || rect.height() < 2f) continue
            drawPhoto(canvas, photoAt(i), rect, style, paint)
        }
        return out
    }

    private fun drawPhoto(canvas: Canvas, photo: Photo, rect: RectF, style: Style, paint: Paint) {
        val pw = photo.bitmap.width.toFloat()
        val ph = photo.bitmap.height.toFloat()
        val rad = Math.toRadians(photo.rotation.toDouble())
        val cosT = abs(cos(rad)).toFloat()
        val sinT = abs(sin(rad)).toFloat()
        // Axis-aligned size of the rotated photo, used for pan overflow.
        val needW = pw * cosT + ph * sinT
        val needH = pw * sinT + ph * cosT
        // Cover: every section corner must stay inside the rotated photo so no wedge can show.
        // (Bounding-box cover is not enough near 45 degrees, where the photo becomes a diamond.)
        val rw = rect.width(); val rh = rect.height()
        val scale = max(
            (rw * cosT + rh * sinT) / pw,
            (rw * sinT + rh * cosT) / ph
        ) * photo.zoom.coerceAtLeast(0.05f)
        // Free overflow after covering: pan moves inside it, never beyond (no background wedges).
        val overX = (needW * scale - rect.width()).coerceAtLeast(0f)
        val overY = (needH * scale - rect.height()).coerceAtLeast(0f)
        val cx = rect.centerX() + photo.panX.coerceIn(-1f, 1f) * overX / 2f
        val cy = rect.centerY() + photo.panY.coerceIn(-1f, 1f) * overY / 2f

        canvas.save()
        val radius = style.radiusPx.toFloat().coerceIn(0f, min(rect.width(), rect.height()) / 2f)
        if (radius > 0f) {
            val path = Path()
            path.addRoundRect(rect, radius, radius, Path.Direction.CW)
            canvas.clipPath(path)
        } else {
            canvas.clipRect(rect)
        }
        canvas.translate(cx, cy)
        canvas.rotate(photo.rotation)
        canvas.scale(scale * (if (photo.flipH) -1f else 1f), scale * (if (photo.flipV) -1f else 1f))
        canvas.drawBitmap(photo.bitmap, -pw / 2f, -ph / 2f, paint)
        canvas.restore()

        val effBorderPx = if (photo.borderPx >= 0) photo.borderPx else style.borderPx
        val effBorderColor = if (photo.borderPx >= 0) photo.borderColor else style.borderColor
        if (effBorderPx > 0) {
            val bp = Paint(Paint.ANTI_ALIAS_FLAG)
            bp.style = Paint.Style.STROKE
            bp.strokeWidth = effBorderPx.toFloat()
            bp.color = effBorderColor
            val inset = effBorderPx / 2f
            val br = RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset)
            if (radius > 0f) canvas.drawRoundRect(br, radius, radius, bp) else canvas.drawRect(br, bp)
        }
    }
}
