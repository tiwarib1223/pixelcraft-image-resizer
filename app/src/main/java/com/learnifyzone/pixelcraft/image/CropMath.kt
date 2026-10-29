package com.learnifyzone.pixelcraft.image

import kotlin.math.roundToInt

/**
 * Pure math for the Crop & Rotate tool's interactive crop frame. The frame is
 * a rectangle in normalized photo coordinates (left, top, right, bottom, each
 * 0..1). No Android imports, so every rule here is unit-tested on the JVM.
 */
object CropMath {

    /** Nearly-full-photo starting frame, small margin so handles are visible. */
    fun defaultRect(): FloatArray = floatArrayOf(0.05f, 0.05f, 0.95f, 0.95f)

    /** Pixel-space width/height ratio for a chip label, or null for Free. */
    fun aspectValue(aspect: String, bmpW: Int, bmpH: Int): Float? = when (aspect) {
        "Original" -> if (bmpH > 0) bmpW.toFloat() / bmpH.toFloat() else null
        "1:1" -> 1f
        "4:3" -> 4f / 3f
        "16:9" -> 16f / 9f
        "9:16" -> 9f / 16f
        else -> null
    }

    /** Largest centered frame of the chosen aspect; Free keeps the default. */
    fun maxRectForAspect(aspect: String, bmpW: Int, bmpH: Int): FloatArray {
        val a = aspectValue(aspect, bmpW, bmpH) ?: return defaultRect()
        val imgAspect = bmpW.toFloat() / bmpH.toFloat()
        var w = 0.95f
        var h = w * imgAspect / a
        if (h > 0.95f) {
            h = 0.95f
            w = h * a / imgAspect
        }
        val l = (1f - w) / 2f
        val t = (1f - h) / 2f
        return floatArrayOf(l, t, l + w, t + h)
    }

    /** Move the whole frame, keeping it fully inside the photo. */
    fun move(rect: FloatArray, dx: Float, dy: Float): FloatArray {
        val w = rect[2] - rect[0]
        val h = rect[3] - rect[1]
        val l = (rect[0] + dx).coerceIn(0f, 1f - w)
        val t = (rect[1] + dy).coerceIn(0f, 1f - h)
        return floatArrayOf(l, t, l + w, t + h)
    }

    /**
     * Drag one corner (0 = top-left, 1 = top-right, 2 = bottom-left,
     * 3 = bottom-right) by a normalized delta. With an aspect lock the frame
     * keeps its pixel ratio, anchored at the opposite corner.
     */
    fun dragCorner(rect: FloatArray, corner: Int, dx: Float, dy: Float, aspect: Float?, imgAspect: Float, minFrac: Float = 0.05f): FloatArray {
        var l = rect[0]
        var t = rect[1]
        var r = rect[2]
        var b = rect[3]
        when (corner) {
            0 -> { l += dx; t += dy }
            1 -> { r += dx; t += dy }
            2 -> { l += dx; b += dy }
            3 -> { r += dx; b += dy }
        }
        if (aspect == null || aspect <= 0f) {
            l = l.coerceIn(0f, r - minFrac); r = r.coerceIn(l + minFrac, 1f)
            t = t.coerceIn(0f, b - minFrac); b = b.coerceIn(t + minFrac, 1f)
            return floatArrayOf(l, t, r, b)
        }
        when (corner) {
            0 -> { // anchored bottom-right
                var w = (r - l).coerceIn(minFrac, r)
                var h = w * imgAspect / aspect
                if (h > b) { h = b; w = h * aspect / imgAspect }
                if (h < minFrac) { h = minFrac; w = (h * aspect / imgAspect).coerceIn(minFrac, r) }
                l = r - w; t = b - h
            }
            1 -> { // anchored bottom-left
                var w = (r - l).coerceIn(minFrac, 1f - l)
                var h = w * imgAspect / aspect
                if (h > b) { h = b; w = h * aspect / imgAspect }
                if (h < minFrac) { h = minFrac; w = (h * aspect / imgAspect).coerceIn(minFrac, 1f - l) }
                r = l + w; t = b - h
            }
            2 -> { // anchored top-right
                var w = (r - l).coerceIn(minFrac, r)
                var h = w * imgAspect / aspect
                if (h > 1f - t) { h = 1f - t; w = h * aspect / imgAspect }
                if (h < minFrac) { h = minFrac; w = (h * aspect / imgAspect).coerceIn(minFrac, r) }
                l = r - w; b = t + h
            }
            else -> { // 3, anchored top-left
                var w = (r - l).coerceIn(minFrac, 1f - l)
                var h = w * imgAspect / aspect
                if (h > 1f - t) { h = 1f - t; w = h * aspect / imgAspect }
                if (h < minFrac) { h = minFrac; w = (h * aspect / imgAspect).coerceIn(minFrac, 1f - l) }
                r = l + w; b = t + h
            }
        }
        return floatArrayOf(l, t, r, b)
    }

    /** Largest centered normalized frame with the given width/height ratio. */
    fun maxRectForRatio(ratio: Float, bmpW: Int, bmpH: Int): FloatArray {
        val img = bmpW.toFloat() / bmpH.toFloat()
        return if (img > ratio) {
            val w = ratio / img
            floatArrayOf((1f - w) / 2f, 0f, (1f + w) / 2f, 1f)
        } else {
            val h = img / ratio
            floatArrayOf(0f, (1f - h) / 2f, 1f, (1f + h) / 2f)
        }
    }

    /** Convert the normalized frame to clamped pixel bounds (l, t, r, b). */
    fun toPixels(rect: FloatArray, bmpW: Int, bmpH: Int): IntArray {
        val l = (rect[0] * bmpW).roundToInt().coerceIn(0, bmpW - 1)
        val t = (rect[1] * bmpH).roundToInt().coerceIn(0, bmpH - 1)
        val r = (rect[2] * bmpW).roundToInt().coerceIn(l + 1, bmpW)
        val b = (rect[3] * bmpH).roundToInt().coerceIn(t + 1, bmpH)
        return intArrayOf(l, t, r, b)
    }

    // ---- Straighten + perspective tilt (v0.9.1) ----

    /**
     * The photo's four corners (TL, TR, BR, BL) after straighten (2D rotation
     * by straightDeg), horizontal tilt (3D rotation about the vertical axis by
     * tiltHDeg) and vertical tilt (about the horizontal axis by tiltVDeg),
     * projected by a pinhole camera at distance focal. Angles in degrees.
     * Returned in photo pixel coordinates (origin top-left).
     */
    fun transformedCorners(w: Int, h: Int, straightDeg: Float, tiltHDeg: Float, tiltVDeg: Float, focal: Float): FloatArray {
        val cx = w / 2f
        val cy = h / 2f
        val a = Math.toRadians(straightDeg.toDouble())
        val b = Math.toRadians(tiltHDeg.toDouble())
        val g = Math.toRadians(tiltVDeg.toDouble())
        val ca = kotlin.math.cos(a); val sa = kotlin.math.sin(a)
        val cb = kotlin.math.cos(b); val sb = kotlin.math.sin(b)
        val cg = kotlin.math.cos(g); val sg = kotlin.math.sin(g)
        val out = FloatArray(8)
        val xs = floatArrayOf(-cx, cx, cx, -cx)
        val ys = floatArrayOf(-cy, -cy, cy, cy)
        for (i in 0..3) {
            // rotate Z (straighten, in-plane)
            val x1 = xs[i] * ca - ys[i] * sa
            val y1 = xs[i] * sa + ys[i] * ca
            // rotate Y (horizontal tilt)
            val x2 = (x1 * cb).toFloat()
            val z2 = (-x1 * sb).toFloat()
            val y2 = y1
            // rotate X (vertical tilt)
            val y3 = (y2 * cg - z2 * sg).toFloat()
            val z3 = (y2 * sg + z2 * cg).toFloat()
            // pinhole projection from z = -focal
            val s = focal / (focal + z3)
            out[i * 2] = (x2 * s).toFloat() + cx
            out[i * 2 + 1] = y3 * s + cy
        }
        return out
    }

    /** True when point p is inside the convex quad (TL, TR, BR, BL order). */
    fun pointInQuad(px: Float, py: Float, quad: FloatArray): Boolean {
        var sign = 0
        for (i in 0..3) {
            val ax = quad[i * 2]; val ay = quad[i * 2 + 1]
            val bx = quad[((i + 1) % 4) * 2]; val by = quad[((i + 1) % 4) * 2 + 1]
            val cross = (bx - ax) * (py - ay) - (by - ay) * (px - ax)
            if (cross != 0f) {
                val s = if (cross > 0) 1 else -1
                if (sign == 0) sign = s else if (sign != s) return false
            }
        }
        return true
    }

    /**
     * The uniform zoom about the photo center that makes the transformed quad
     * fully cover the original photo box, so no empty wedges can appear.
     * 1f when nothing is transformed. Binary search, ~40 iterations.
     */
    fun coverScale(quad: FloatArray, w: Int, h: Int): Float {
        val cx = w / 2f
        val cy = h / 2f
        fun covers(scale: Float): Boolean {
            val q = FloatArray(8)
            for (i in 0..3) {
                q[i * 2] = cx + (quad[i * 2] - cx) * scale
                q[i * 2 + 1] = cy + (quad[i * 2 + 1] - cy) * scale
            }
            return pointInQuad(0f, 0f, q) && pointInQuad(w.toFloat(), 0f, q) &&
                pointInQuad(w.toFloat(), h.toFloat(), q) && pointInQuad(0f, h.toFloat(), q)
        }
        if (covers(1f)) return 1f
        var lo = 1f
        var hi = 2f
        while (!covers(hi) && hi < 64f) { lo = hi; hi *= 2f }
        repeat(40) {
            val mid = (lo + hi) / 2f
            if (covers(mid)) hi = mid else lo = mid
        }
        return hi
    }
}