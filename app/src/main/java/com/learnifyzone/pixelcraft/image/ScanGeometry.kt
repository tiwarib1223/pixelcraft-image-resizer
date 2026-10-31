package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Document Scanner crop & perspective engine (his original Scanner recording, Screen 3).
 *
 * A page's crop is stored as four corners (TL, TR, BR, BL), each x/y relative 0..1 in the
 * ROTATED page frame (after the page's 90-degree rotation steps). Perspective correction maps
 * that quad onto a rectangle with Matrix.setPolyToPoly - a true projective transform, so the
 * document is never stretched or distorted the way a naive scale would.
 * Pure bitmap math, fully offline, JVM-testable.
 */
object ScanGeometry {

    /** Default corner inset before auto detection runs. */
    fun defaultQuad(): FloatArray = floatArrayOf(
        0.06f, 0.06f, 0.94f, 0.06f, 0.94f, 0.94f, 0.06f, 0.94f
    )

    fun relToPx(quad: FloatArray, w: Int, h: Int): FloatArray = FloatArray(8) { i ->
        if (i % 2 == 0) quad[i] * w else quad[i] * h
    }

    fun pxToRel(quadPx: FloatArray, w: Int, h: Int): FloatArray = FloatArray(8) { i ->
        if (i % 2 == 0) (quadPx[i] / w).coerceIn(0f, 1f) else (quadPx[i] / h).coerceIn(0f, 1f)
    }

    /** Map a relative quad through a 90-degree rotation of the frame (CW when [cw]). */
    fun rotateQuad90Rel(quad: FloatArray, cw: Boolean): FloatArray = FloatArray(8) { i ->
        val x = quad[i - i % 2]; val y = quad[i - i % 2 + 1]
        if (i % 2 == 0) (if (cw) 1f - y else y) else (if (cw) x else 1f - x)
    }

    fun rotate90(src: Bitmap, cw: Boolean): Bitmap {
        val m = Matrix().apply { postRotate(if (cw) 90f else -90f) }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
    }

    /** Free-angle straighten: rotate around the centre onto a white page-coloured canvas. */
    fun straighten(src: Bitmap, degrees: Float, bgColor: Int = Color.WHITE): Bitmap {
        if (degrees == 0f) return src.copy(Bitmap.Config.ARGB_8888, false)
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(bgColor)
        val m = Matrix().apply { postRotate(degrees, src.width / 2f, src.height / 2f) }
        canvas.drawBitmap(src, m, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        return out
    }

    /** Rotate a relative quad the same way [straighten] rotates the bitmap (about centre). */
    fun rotateQuadFree(quad: FloatArray, degrees: Float, w: Int, h: Int): FloatArray {
        val m = Matrix().apply { postRotate(degrees, w / 2f, h / 2f) }
        val px = relToPx(quad, w, h)
        m.mapPoints(px)
        return pxToRel(px, w, h)
    }

    private fun dist(x1: Float, y1: Float, x2: Float, y2: Float) = hypot((x2 - x1).toDouble(), (y2 - y1).toDouble()).toFloat()

    /**
     * Perspective-corrected crop. [quadRel] corners (TL, TR, BR, BL) relative 0..1 in src.
     * Output size comes from the quad's own edge lengths, capped so memory stays sane.
     */
    fun perspectiveCrop(src: Bitmap, quadRel: FloatArray, maxDim: Int = 4000): Bitmap {
        val q = relToPx(quadRel, src.width, src.height)
        val wTop = dist(q[0], q[1], q[2], q[3]); val wBot = dist(q[6], q[7], q[4], q[5])
        val hL = dist(q[0], q[1], q[6], q[7]); val hR = dist(q[2], q[3], q[4], q[5])
        var outW = max(wTop, wBot).roundToInt().coerceAtLeast(8)
        var outH = max(hL, hR).roundToInt().coerceAtLeast(8)
        val scale = if (max(outW, outH) > maxDim) maxDim.toFloat() / max(outW, outH) else 1f
        outW = (outW * scale).roundToInt().coerceAtLeast(8)
        outH = (outH * scale).roundToInt().coerceAtLeast(8)
        val dst = floatArrayOf(0f, 0f, outW.toFloat(), 0f, outW.toFloat(), outH.toFloat(), 0f, outH.toFloat())
        val m = Matrix()
        m.setPolyToPoly(q, 0, dst, 0, 4)
        val out = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(src, m, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        return out
    }

    /**
     * Automatic page boundary: estimate the background colour from the border, then find the
     * bounding box of everything that differs from it. Honest scope: works on a contrasting
     * background; on a messy background it returns nearly the whole image.
     */
    fun autoBoundary(src: Bitmap): FloatArray {
        val sw = 120
        val sh = (sw.toFloat() * src.height / src.width).roundToInt().coerceAtLeast(1)
        val small = Bitmap.createScaledBitmap(src, sw, sh, true)
        val px = IntArray(sw * sh)
        small.getPixels(px, 0, sw, 0, 0, sw, sh)
        small.recycle()
        // background = average of the four corner blocks
        var br = 0L; var bg = 0L; var bb = 0L; var bn = 0
        val corners = listOf(0 to 0, sw - 8 to 0, 0 to sh - 8, sw - 8 to sh - 8)
        corners.forEach { (cx, cy) ->
            for (y in cy until cy + 8) for (x in cx until cx + 8) {
                val c = px[y.coerceIn(0, sh - 1) * sw + x.coerceIn(0, sw - 1)]
                br += (c shr 16) and 0xFF; bg += (c shr 8) and 0xFF; bb += c and 0xFF; bn++
            }
        }
        br /= bn; bg /= bn; bb /= bn
        val threshold = 30
        var minX = sw; var maxX = -1; var minY = sh; var maxY = -1
        for (y in 0 until sh) {
            val row = y * sw
            for (x in 0 until sw) {
                val c = px[row + x]
                val d = abs(((c shr 16) and 0xFF) - br) + abs(((c shr 8) and 0xFF) - bg) + abs((c and 0xFF) - bb)
                if (d > threshold * 3) {
                    if (x < minX) minX = x; if (x > maxX) maxX = x
                    if (y < minY) minY = y; if (y > maxY) maxY = y
                }
            }
        }
        if (maxX < 0 || (maxX - minX) < sw / 4 || (maxY - minY) < sh / 4) {
            return floatArrayOf(0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f) // nothing clear found: full page
        }
        // tiny inset so the boundary sits on the page, not on the background edge
        val ix = 1f / sw; val iy = 1f / sh
        val l = (minX.toFloat() / sw + ix).coerceIn(0f, 1f)
        val r = ((maxX + 1).toFloat() / sw - ix).coerceIn(0f, 1f)
        val t = (minY.toFloat() / sh + iy).coerceIn(0f, 1f)
        val b = ((maxY + 1).toFloat() / sh - iy).coerceIn(0f, 1f)
        return floatArrayOf(l, t, r, t, r, b, l, b)
    }
}
