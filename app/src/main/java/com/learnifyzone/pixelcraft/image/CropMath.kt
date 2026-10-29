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

    /** Convert the normalized frame to clamped pixel bounds (l, t, r, b). */
    fun toPixels(rect: FloatArray, bmpW: Int, bmpH: Int): IntArray {
        val l = (rect[0] * bmpW).roundToInt().coerceIn(0, bmpW - 1)
        val t = (rect[1] * bmpH).roundToInt().coerceIn(0, bmpH - 1)
        val r = (rect[2] * bmpW).roundToInt().coerceIn(l + 1, bmpW)
        val b = (rect[3] * bmpH).roundToInt().coerceIn(t + 1, bmpH)
        return intArrayOf(l, t, r, b)
    }
}
