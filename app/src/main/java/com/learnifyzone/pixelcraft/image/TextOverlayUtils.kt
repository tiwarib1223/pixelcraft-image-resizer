package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import kotlin.math.max

/**
 * Text-on-Photo engine (v0.16.0). ONE rendering path shared by the live preview and the
 * final export - the preview draws through the same drawAll() in image coordinate space,
 * so what he sees on the photo is pixel-identical to what gets saved.
 *
 * Coordinate model: a layer lives in IMAGE pixel space. Its centre is (cx, cy) as a
 * fraction of the image (0..1), its base text size is sizeFrac * imageWidth, and pinch /
 * rotation gestures only change scale and rotation. The preview canvas applies the
 * fit-transform; this code always thinks in image pixels.
 */
object TextOverlayUtils {

    /** common = shown up front (his v0.16.1 spec: 3-4 fonts + a "More fonts" button for the rest). */
    enum class FontChoice(val label: String, val family: String, val common: Boolean) {
        Sans("Sans", "sans-serif", true),
        Serif("Serif", "serif", true),
        Mono("Mono", "monospace", true),
        Script("Script", "cursive", true),
        Condensed("Condensed", "sans-serif-condensed", false),
        Light("Light", "sans-serif-light", false),
        Black("Black", "sans-serif-black", false),
        Casual("Casual", "casual", false),
        Smallcaps("Small Caps", "sans-serif-smallcaps", false)
    }

    enum class Align(val label: String) { LEFT("Left"), CENTER("Centre"), RIGHT("Right") }

    /** The 6-colour quick palette from his mockup: white, black, blue, gold, red/pink, green. */
    val QUICK_COLORS = listOf(
        0xFFFFFFFF.toInt(), 0xFF000000.toInt(), 0xFF42BFEF.toInt(),
        0xFFF5B942.toInt(), 0xFFF95F6A.toInt(), 0xFF3ECF8E.toInt()
    )

    val QUICK_STYLE_NAMES = listOf("Classic", "Bold", "Elegant", "Shadow")

    /** Extended palette behind the "More colours" button (his v0.16.1 spec). */
    val EXTRA_COLORS = listOf(
        0xFF9B5DE5.toInt(), 0xFFFF8A32.toInt(), 0xFF00B8A9.toInt(), 0xFFFFE066.toInt(),
        0xFF172B4D.toInt(), 0xFF8D6E63.toInt(), 0xFF90A4AE.toInt(), 0xFFD4E157.toInt(),
        0xFF0288D1.toInt(), 0xFFAD1457.toInt(), 0xFF689F38.toInt(), 0xFF5E35B1.toInt(),
        0xFFEF6C00.toInt(), 0xFF00838F.toInt(), 0xFFC62828.toInt(), 0xFF546E7A.toInt()
    )

    data class TextLayer(
        val id: Long,
        var text: String = "Your text",
        var font: FontChoice = FontChoice.Sans,
        var sizeFrac: Float = 0.07f,          // base text size as a fraction of image width
        var bold: Boolean = false,
        var italic: Boolean = false,
        var align: Align = Align.CENTER,
        var colorArgb: Int = 0xFFFFFFFF.toInt(),
        var outlineFrac: Float = 0f,          // stroke width as a fraction of text size; 0 = no outline
        var outlineColorArgb: Int = 0xFF000000.toInt(),
        var bgColorArgb: Int = 0,             // 0 = no background box
        var shadow: Boolean = false,
        var opacity: Float = 1f,              // 0..1
        var cx: Float = 0.5f,
        var cy: Float = 0.5f,
        var scale: Float = 1f,                // pinch multiplier
        var rotation: Float = 0f,             // degrees, clockwise
        var curved: Boolean = false,
        var arcSweep: Float = 160f,           // degrees of arc the text spans when curved
        var letterSpacing: Float = 0f,        // em fraction (0..0.3)
        var lineSpacing: Float = 0f           // extra line gap as a fraction (0..1)
    )

    /**
     * Image overlay layer (v0.16.1, his "Add image" spec): a SECOND photo placed on top of the
     * main photo - logo, sticker, overlay. Lives in the same image-pixel coordinate model as
     * text layers, stays fully editable (move/resize/rotate/delete) until export; image layers
     * draw UNDER text layers so captions stay readable on top of a logo.
     */
    class ImageLayer(
        val id: Long,
        val bitmap: Bitmap,
        var cx: Float = 0.5f,
        var cy: Float = 0.5f,
        var widthFrac: Float = 0.4f,   // width as a fraction of image width; height keeps the bitmap's aspect
        var rotation: Float = 0f,      // degrees, clockwise
        var opacity: Float = 1f        // 0..1
    )

    /** Drawn size of an image layer in image pixels: [w, h]. */
    fun imageLayerSize(layer: ImageLayer, imgW: Int): FloatArray {
        val w = layer.widthFrac * imgW
        val h = if (layer.bitmap.width > 0) w * layer.bitmap.height / layer.bitmap.width else w
        return floatArrayOf(w, h)
    }

    /** Draw one image layer (rotates around its centre, alpha = opacity). */
    fun drawImageLayer(canvas: Canvas, layer: ImageLayer, imgW: Int, imgH: Int) {
        val (w, h) = imageLayerSize(layer, imgW)
        if (w <= 0f || h <= 0f) return
        val px = layer.cx * imgW
        val py = layer.cy * imgH
        canvas.save()
        canvas.rotate(layer.rotation, px, py)
        val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
        }
        val dst = RectF(px - w / 2f, py - h / 2f, px + w / 2f, py + h / 2f)
        canvas.drawBitmap(layer.bitmap, null, dst, p)
        canvas.restore()
    }

    /** Hit test in image pixels (rotation-aware, generous 12px pad) - mirrors textLayerAt behaviour. */
    fun imageLayerHit(layer: ImageLayer, x: Float, y: Float, imgW: Int, imgH: Int): Boolean {
        val (w, h) = imageLayerSize(layer, imgW)
        val px = layer.cx * imgW
        val py = layer.cy * imgH
        val rad = Math.toRadians((-layer.rotation).toDouble())
        val dx = x - px
        val dy = y - py
        val rx = (dx * kotlin.math.cos(rad) - dy * kotlin.math.sin(rad)).toFloat()
        val ry = (dx * kotlin.math.sin(rad) + dy * kotlin.math.cos(rad)).toFloat()
        val pad = 12f
        return kotlin.math.abs(rx) <= w / 2f + pad && kotlin.math.abs(ry) <= h / 2f + pad
    }

    private fun drawImageSelection(canvas: Canvas, layer: ImageLayer, imgW: Int, imgH: Int) {
        val (w, h) = imageLayerSize(layer, imgW)
        val px = layer.cx * imgW
        val py = layer.cy * imgH
        val pad = w * 0.06f + 6f
        canvas.save()
        canvas.rotate(layer.rotation, px, py)
        val frame = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, imgW * 0.004f)
            color = 0xFF42BFEF.toInt()
            pathEffect = DashPathEffect(floatArrayOf(14f, 10f), 0f)
        }
        canvas.drawRect(px - w / 2f - pad, py - h / 2f - pad, px + w / 2f + pad, py + h / 2f + pad, frame)
        canvas.restore()
    }

    // ---- Canvas-size cutter (v0.16.1, his spec: 4 common sizes + a "More sizes" button with all sizes + custom) ----

    data class AspectOption(val label: String, val rw: Int, val rh: Int, val common: Boolean)

    val ASPECT_OPTIONS = listOf(
        AspectOption("Square", 1, 1, true),
        AspectOption("Portrait", 4, 5, true),
        AspectOption("Story", 9, 16, true),
        AspectOption("Wide", 16, 9, true),
        AspectOption("Classic", 3, 2, false),
        AspectOption("Tall", 2, 3, false),
        AspectOption("Screen", 5, 4, false),
        AspectOption("Standard", 4, 3, false),
        AspectOption("Upright", 3, 4, false),
        AspectOption("A4", 210, 297, false)
    )

    /** Largest rect of ratio rw:rh that fits the photo, centred, as [l,t,r,b] fractions (0..1). */
    fun maxCropForRatio(imgW: Int, imgH: Int, rw: Int, rh: Int): FloatArray {
        val a = rw.toFloat() / rh.toFloat()
        val imgAspect = imgW.toFloat() / imgH.toFloat()
        var w = 1f
        var h = w * imgAspect / a
        if (h > 1f) {
            h = 1f
            w = h * a / imgAspect
        }
        val l = (1f - w) / 2f
        val t = (1f - h) / 2f
        return floatArrayOf(l, t, l + w, t + h)
    }

    /** Pixel size of a crop rect on the photo (never below 16px on a side). */
    fun cropPixelSize(imgW: Int, imgH: Int, rect: FloatArray): IntArray = intArrayOf(
        ((rect[2] - rect[0]) * imgW).toInt().coerceIn(16, imgW),
        ((rect[3] - rect[1]) * imgH).toInt().coerceIn(16, imgH)
    )

    /** Pinch-zoom the crop frame around its centre (his Position interaction: zoom + free pan). */
    fun zoomCrop(rect: FloatArray, zoom: Float): FloatArray {
        val cx = (rect[0] + rect[2]) / 2f
        val cy = (rect[1] + rect[3]) / 2f
        val w = ((rect[2] - rect[0]) / zoom).coerceIn(0.05f, 1f)
        val h = ((rect[3] - rect[1]) / zoom).coerceIn(0.05f, 1f)
        val l = (cx - w / 2f).coerceIn(0f, 1f - w)
        val t = (cy - h / 2f).coerceIn(0f, 1f - h)
        return floatArrayOf(l, t, l + w, t + h)
    }

    /** Custom ratio entry: two whole numbers 1..99 ("5" and "7" -> 5:7). Null when invalid. */
    fun parseCustomRatio(wText: String, hText: String): IntArray? {
        val w = wText.trim().toIntOrNull() ?: return null
        val h = hText.trim().toIntOrNull() ?: return null
        if (w !in 1..99 || h !in 1..99) return null
        return intArrayOf(w, h)
    }

    /** Deep snapshot list copy for undo/redo (data-class copy shares nothing mutable here). */
    fun snapshot(layers: List<TextLayer>): List<TextLayer> = layers.map { it.copy() }

    fun typefaceFor(font: FontChoice, bold: Boolean, italic: Boolean): Typeface {
        val style = (if (bold) Typeface.BOLD else 0) or (if (italic) Typeface.ITALIC else 0)
        return Typeface.create(font.family, style)
    }

    /** Radius so that textWidth spans sweepDeg of the circle; floored so short text still arcs visibly. */
    fun arcRadiusFor(textWidthPx: Float, sweepDeg: Float, textSizePx: Float): Float {
        val sweepRad = Math.toRadians(sweepDeg.coerceIn(30.0f, 350.0f).toDouble()).toFloat()
        return max(textWidthPx / sweepRad, textSizePx * 1.6f)
    }

    private fun effSize(layer: TextLayer, imgW: Int): Float =
        (layer.sizeFrac * imgW * layer.scale).coerceAtLeast(6f)

    private fun fillPaint(layer: TextLayer, imgW: Int): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = layer.colorArgb
            alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
            textSize = effSize(layer, imgW)
            typeface = typefaceFor(layer.font, layer.bold, layer.italic)
            letterSpacing = layer.letterSpacing
            if (layer.shadow) setShadowLayer(textSize * 0.14f, textSize * 0.05f, textSize * 0.07f, 0x99000000.toInt())
        }

    private fun lines(layer: TextLayer): List<String> =
        layer.text.split("\n").let { if (it.isEmpty()) listOf("") else it }

    /** Measured block geometry in image pixels (unrotated, unscaled position aside). */
    data class Block(val w: Float, val h: Float, val textSize: Float, val lineH: Float, val lineWidths: List<Float>)

    fun measure(layer: TextLayer, imgW: Int): Block {
        val p = fillPaint(layer, imgW)
        val ls = lines(layer)
        val widths = ls.map { p.measureText(it) }
        val fm = p.fontMetrics
        val lineH = (fm.descent - fm.ascent) * (1.08f + layer.lineSpacing)
        val w = (widths.maxOrNull() ?: 0f) + p.strokeWidth
        val h = lineH * ls.size
        return Block(w, h, p.textSize, lineH, widths)
    }

    /** Draw every layer - image overlays first (under text), then text - optionally with a dashed selection frame (preview only - never exported). */
    fun drawAll(
        canvas: Canvas, layers: List<TextLayer>, imgW: Int, imgH: Int,
        selectedId: Long? = null, selectionFrame: Boolean = false,
        imageLayers: List<ImageLayer> = emptyList(), selectedImageId: Long? = null
    ) {
        imageLayers.forEach { layer ->
            drawImageLayer(canvas, layer, imgW, imgH)
            if (selectionFrame && layer.id == selectedImageId) drawImageSelection(canvas, layer, imgW, imgH)
        }
        layers.forEach { layer ->
            if (layer.curved) drawCurved(canvas, layer, imgW, imgH) else drawStraight(canvas, layer, imgW, imgH)
            if (selectionFrame && layer.id == selectedId) drawSelection(canvas, layer, imgW, imgH)
        }
    }

    /** Full-resolution export: photo + image overlays + all text layers, exactly what the preview showed. */
    fun renderToBitmap(photo: Bitmap, layers: List<TextLayer>, imageLayers: List<ImageLayer> = emptyList()): Bitmap {
        val out = Bitmap.createBitmap(photo.width, photo.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(out)
        c.drawBitmap(photo, 0f, 0f, null)
        drawAll(c, layers, photo.width, photo.height, imageLayers = imageLayers)
        return out
    }

    private fun drawStraight(canvas: Canvas, layer: TextLayer, imgW: Int, imgH: Int) {
        val b = measure(layer, imgW)
        val px = layer.cx * imgW
        val py = layer.cy * imgH
        canvas.save()
        canvas.rotate(layer.rotation, px, py)
        val left = px - b.w / 2f
        val top = py - b.h / 2f
        val p = fillPaint(layer, imgW)
        val fm = p.fontMetrics
        val pad = b.textSize * 0.18f
        if (layer.bgColorArgb != 0) {
            val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = layer.bgColorArgb
                alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
            }
            canvas.drawRect(left - pad, top - pad, left + b.w + pad, top + b.h + pad, bg)
        }
        val ls = lines(layer)
        ls.forEachIndexed { i, line ->
            val lineW = b.lineWidths[i]
            val x = when (layer.align) {
                Align.LEFT -> left
                Align.CENTER -> left + (b.w - lineW) / 2f
                Align.RIGHT -> left + (b.w - lineW)
            }
            val baseline = top - fm.ascent + i * b.lineH
            if (layer.outlineFrac > 0f) {
                val stroke = Paint(p).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = layer.outlineFrac * b.textSize
                    color = layer.outlineColorArgb
                    alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
                    strokeJoin = Paint.Join.ROUND
                }
                canvas.drawText(line, x, baseline, stroke)
            }
            canvas.drawText(line, x, baseline, p)
        }
        canvas.restore()
    }

    private fun drawCurved(canvas: Canvas, layer: TextLayer, imgW: Int, imgH: Int) {
        val px = layer.cx * imgW
        val py = layer.cy * imgH
        val p = fillPaint(layer, imgW)
        val fm = p.fontMetrics
        val lineH = (fm.descent - fm.ascent) * (1.08f + layer.lineSpacing)
        canvas.save()
        canvas.rotate(layer.rotation, px, py)
        lines(layer).forEachIndexed { i, line ->
            if (line.isEmpty()) return@forEachIndexed
            val textW = p.measureText(line)
            // Circle centre sits one radius below the layer centre, so the arc's apex is at the layer centre.
            val r = arcRadiusFor(textW, layer.arcSweep, p.textSize) + i * lineH
            val oval = RectF(px - r, py + r - r + i * 0f, px + r, py + 3f * r)
            // oval top must be at py + i*lineH*0 ... circle centre = py + r: top = py. Stack later lines outward.
            val cy = py + r
            oval.set(px - r, cy - r, px + r, cy + r)
            val start = 270f - layer.arcSweep / 2f
            val path = Path()
            path.addArc(oval, start, layer.arcSweep)
            val arcLen = Math.toRadians(layer.arcSweep.toDouble()).toFloat() * r
            val hOff = when (layer.align) {
                Align.LEFT -> 0f
                Align.CENTER -> ((arcLen - textW) / 2f).coerceAtLeast(0f)
                Align.RIGHT -> (arcLen - textW).coerceAtLeast(0f)
            }
            if (layer.bgColorArgb != 0 || layer.outlineFrac > 0f) {
                // Outlined pass first so the fill sits crisply on top.
                if (layer.outlineFrac > 0f) {
                    val stroke = Paint(p).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = layer.outlineFrac * p.textSize
                        color = layer.outlineColorArgb
                        alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
                        strokeJoin = Paint.Join.ROUND
                    }
                    canvas.drawTextOnPath(line, path, hOff, 0f, stroke)
                }
            }
            canvas.drawTextOnPath(line, path, hOff, 0f, p)
        }
        canvas.restore()
    }

    private fun drawSelection(canvas: Canvas, layer: TextLayer, imgW: Int, imgH: Int) {
        val b = measure(layer, imgW)
        val px = layer.cx * imgW
        val py = layer.cy * imgH
        val pad = b.textSize * 0.3f
        val halfW: Float
        val halfH: Float
        if (layer.curved) {
            // Arc spans roughly textWidth wide and (1-cos(sweep/2))*r + textSize high.
            val textW = b.lineWidths.maxOrNull() ?: 0f
            val r = arcRadiusFor(textW, layer.arcSweep, b.textSize)
            val sag = r * (1f - kotlin.math.cos(Math.toRadians((layer.arcSweep / 2f).toDouble())).toFloat())
            halfW = minOf(textW, Math.toRadians(layer.arcSweep.toDouble()).toFloat() * r) / 2f + pad
            halfH = (sag + b.textSize) / 2f + pad
        } else {
            halfW = b.w / 2f + pad
            halfH = b.h / 2f + pad
        }
        canvas.save()
        canvas.rotate(layer.rotation, px, py + if (layer.curved) -halfH + pad else 0f)
        val frame = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, imgW * 0.004f)
            color = 0xFF42BFEF.toInt()
            pathEffect = DashPathEffect(floatArrayOf(14f, 10f), 0f)
        }
        val cyOff = if (layer.curved) -halfH + pad else 0f
        canvas.drawRect(px - halfW, py + cyOff - halfH, px + halfW, py + cyOff + halfH, frame)
        canvas.restore()
    }

    /** Apply a Quick Text Style preset to a layer (from his mockup: Classic, Bold, Elegant, Shadow). */
    fun applyPreset(layer: TextLayer, name: String) {
        when (name) {
            "Classic" -> {
                layer.font = FontChoice.Sans; layer.bold = false; layer.italic = false
                layer.colorArgb = 0xFFFFFFFF.toInt(); layer.shadow = true
                layer.outlineFrac = 0f; layer.bgColorArgb = 0; layer.opacity = 1f
            }
            "Bold" -> {
                layer.font = FontChoice.Sans; layer.bold = true; layer.italic = false
                layer.colorArgb = 0xFFFFFFFF.toInt(); layer.outlineFrac = 0.10f
                layer.outlineColorArgb = 0xFF172B4D.toInt(); layer.shadow = false
                layer.bgColorArgb = 0; layer.opacity = 1f
            }
            "Elegant" -> {
                layer.font = FontChoice.Serif; layer.bold = false; layer.italic = true
                layer.colorArgb = 0xFFF5B942.toInt(); layer.shadow = false
                layer.outlineFrac = 0f; layer.bgColorArgb = 0; layer.opacity = 1f
            }
            "Shadow" -> {
                layer.font = FontChoice.Sans; layer.bold = true; layer.italic = false
                layer.colorArgb = 0xFF42BFEF.toInt(); layer.shadow = true
                layer.outlineFrac = 0f; layer.bgColorArgb = 0; layer.opacity = 1f
            }
        }
    }

    /** Snapshot-based undo/redo for the layer list (his point 6). Cap keeps memory flat. */
    class UndoRedo(private val cap: Int = 60) {
        private val undoStack = ArrayDeque<List<TextLayer>>()
        private val redoStack = ArrayDeque<List<TextLayer>>()
        val canUndo: Boolean get() = undoStack.isNotEmpty()
        val canRedo: Boolean get() = redoStack.isNotEmpty()
        val undoCount: Int get() = undoStack.size

        /** Call BEFORE applying a change; [current] is the pre-change snapshot. */
        fun record(current: List<TextLayer>) {
            undoStack.addLast(snapshot(current))
            while (undoStack.size > cap) undoStack.removeFirst()
            redoStack.clear()
        }

        /** Returns the state to restore, remembering [current] for redo. Null when nothing to undo. */
        fun undo(current: List<TextLayer>): List<TextLayer>? {
            if (undoStack.isEmpty()) return null
            redoStack.addLast(snapshot(current))
            return undoStack.removeLast()
        }

        fun redo(current: List<TextLayer>): List<TextLayer>? {
            if (redoStack.isEmpty()) return null
            undoStack.addLast(snapshot(current))
            return redoStack.removeLast()
        }

        fun clear() { undoStack.clear(); redoStack.clear() }
    }
}
