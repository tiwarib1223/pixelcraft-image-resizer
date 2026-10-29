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

    enum class FontChoice(val label: String) {
        Sans("Sans"), Serif("Serif"), Mono("Mono"), Script("Script")
    }

    enum class Align(val label: String) { LEFT("Left"), CENTER("Centre"), RIGHT("Right") }

    /** The 6-colour quick palette from his mockup: white, black, blue, gold, red/pink, green. */
    val QUICK_COLORS = listOf(
        0xFFFFFFFF.toInt(), 0xFF000000.toInt(), 0xFF42BFEF.toInt(),
        0xFFF5B942.toInt(), 0xFFF95F6A.toInt(), 0xFF3ECF8E.toInt()
    )

    val QUICK_STYLE_NAMES = listOf("Classic", "Bold", "Elegant", "Shadow")

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
        var arcSweep: Float = 160f            // degrees of arc the text spans when curved
    )

    /** Deep snapshot list copy for undo/redo (data-class copy shares nothing mutable here). */
    fun snapshot(layers: List<TextLayer>): List<TextLayer> = layers.map { it.copy() }

    fun typefaceFor(font: FontChoice, bold: Boolean, italic: Boolean): Typeface {
        val style = (if (bold) Typeface.BOLD else 0) or (if (italic) Typeface.ITALIC else 0)
        val family = when (font) {
            FontChoice.Sans -> "sans-serif"
            FontChoice.Serif -> "serif"
            FontChoice.Mono -> "monospace"
            FontChoice.Script -> "cursive"
        }
        return Typeface.create(family, style)
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
        val lineH = (fm.descent - fm.ascent) * 1.08f
        val w = (widths.maxOrNull() ?: 0f) + p.strokeWidth
        val h = lineH * ls.size
        return Block(w, h, p.textSize, lineH, widths)
    }

    /** Draw every layer, optionally with a dashed selection frame (preview only - never exported). */
    fun drawAll(canvas: Canvas, layers: List<TextLayer>, imgW: Int, imgH: Int, selectedId: Long? = null, selectionFrame: Boolean = false) {
        layers.forEach { layer ->
            if (layer.curved) drawCurved(canvas, layer, imgW, imgH) else drawStraight(canvas, layer, imgW, imgH)
            if (selectionFrame && layer.id == selectedId) drawSelection(canvas, layer, imgW, imgH)
        }
    }

    /** Full-resolution export: photo + all text layers, exactly what the preview showed. */
    fun renderToBitmap(photo: Bitmap, layers: List<TextLayer>): Bitmap {
        val out = Bitmap.createBitmap(photo.width, photo.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(out)
        c.drawBitmap(photo, 0f, 0f, null)
        drawAll(c, layers, photo.width, photo.height)
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
        val lineH = (fm.descent - fm.ascent) * 1.08f
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
