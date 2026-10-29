package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Bitmap transforms for the Process step: Fit, Crop and Stretch when the
 * target size has a different aspect ratio than the photo, plus thumbnail
 * helpers for the live dialog previews. All on-device, no libraries.
 */
object Transforms {

    enum class FitPad(val label: String) { BLUR("Blur"), WHITE("White"), BLACK("Black") }

    /** 3x3 anchor grid for choosing which part of the photo a crop keeps. */
    enum class CropAnchor(val label: String, val fx: Float, val fy: Float) {
        TOP_LEFT("Top left", 0f, 0f), TOP_CENTER("Top", 0.5f, 0f), TOP_RIGHT("Top right", 1f, 0f),
        CENTER_LEFT("Left", 0f, 0.5f), CENTER("Center", 0.5f, 0.5f), CENTER_RIGHT("Right", 1f, 0.5f),
        BOTTOM_LEFT("Bottom left", 0f, 1f), BOTTOM_CENTER("Bottom", 0.5f, 1f), BOTTOM_RIGHT("Bottom right", 1f, 1f)
    }

    /** True when both sizes have (nearly) the same aspect ratio. */
    fun sameAspect(w1: Int, h1: Int, w2: Int, h2: Int): Boolean {
        if (w1 <= 0 || h1 <= 0 || w2 <= 0 || h2 <= 0) return true
        val r1 = w1.toDouble() / h1
        val r2 = w2.toDouble() / h2
        return abs(r1 - r2) / max(r1, r2) < 0.005
    }

    fun stretch(src: Bitmap, w: Int, h: Int): Bitmap =
        Bitmap.createScaledBitmap(src, w, h, true)

    /**
     * Fit: scale the photo to fit INSIDE the target, pad the remaining area.
     * Padding is a blurred enlargement of the photo, or plain white / black.
     */
    fun fit(src: Bitmap, w: Int, h: Int, pad: FitPad): Bitmap {
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        when (pad) {
            FitPad.WHITE -> canvas.drawColor(Color.WHITE)
            FitPad.BLACK -> canvas.drawColor(Color.BLACK)
            FitPad.BLUR -> canvas.drawBitmap(stretch(blurred(src), w, h), 0f, 0f, null)
        }
        val scale = min(w.toFloat() / src.width, h.toFloat() / src.height)
        val dw = max(1, (src.width * scale).roundToInt())
        val dh = max(1, (src.height * scale).roundToInt())
        val fg = stretch(src, dw, dh)
        canvas.drawBitmap(fg, (w - dw) / 2f, (h - dh) / 2f, null)
        return out
    }

    /** Largest rectangle of the target aspect inside the source, at the chosen anchor. */
    fun cropRect(srcW: Int, srcH: Int, targetW: Int, targetH: Int, anchor: CropAnchor): IntArray {
        val targetRatio = targetW.toDouble() / targetH
        var rw = srcW
        var rh = (rw / targetRatio).roundToInt()
        if (rh > srcH) {
            rh = srcH
            rw = (rh * targetRatio).roundToInt()
        }
        rw = rw.coerceIn(1, srcW)
        rh = rh.coerceIn(1, srcH)
        val left = ((srcW - rw) * anchor.fx).roundToInt().coerceIn(0, srcW - rw)
        val top = ((srcH - rh) * anchor.fy).roundToInt().coerceIn(0, srcH - rh)
        return intArrayOf(left, top, rw, rh)
    }

    /** Crop: keep the anchored region of the photo, then scale it to the exact target. */
    fun crop(src: Bitmap, targetW: Int, targetH: Int, anchor: CropAnchor): Bitmap {
        val (left, top, rw, rh) = cropRect(src.width, src.height, targetW, targetH, anchor)
        val cut = Bitmap.createBitmap(src, left, top, rw, rh)
        return stretch(cut, targetW, targetH)
    }

    /** Cheap blur: shrink hard, then re-enlarge. Good enough for padding backgrounds. */
    fun blurred(src: Bitmap): Bitmap {
        val smallW = 32
        val smallH = max(1, (32.0 * src.height / src.width).roundToInt())
        val small = Bitmap.createScaledBitmap(src, smallW, smallH, true)
        return Bitmap.createScaledBitmap(small, src.width, src.height, true)
    }

    /** Downscale a copy for fast dialog/preview thumbnails. */
    fun thumbnail(src: Bitmap, maxDim: Int = 360): Bitmap {
        val longest = max(src.width, src.height)
        if (longest <= maxDim) return src
        val scale = maxDim.toFloat() / longest
        return Bitmap.createScaledBitmap(
            src,
            max(1, (src.width * scale).roundToInt()),
            max(1, (src.height * scale).roundToInt()),
            true
        )
    }

    /** Scale a target size down so its longest side is at most maxDim (for previews). */
    fun scaledTarget(w: Int, h: Int, maxDim: Int = 360): Pair<Int, Int> {
        val longest = max(w, h)
        if (longest <= maxDim) return w to h
        val scale = maxDim.toDouble() / longest
        return max(1, (w * scale).roundToInt()) to max(1, (h * scale).roundToInt())
    }

    /** Rotate 90 degrees clockwise. */
    fun rotateRight(src: Bitmap): Bitmap {
        val m = android.graphics.Matrix().apply { postRotate(90f) }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
    }

    /** Rotate 90 degrees counter-clockwise. */
    fun rotateLeft(src: Bitmap): Bitmap {
        val m = android.graphics.Matrix().apply { postRotate(-90f) }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
    }

    /** Mirror horizontally (left-right flip). */
    fun flipH(src: Bitmap): Bitmap {
        val m = android.graphics.Matrix().apply { postScale(-1f, 1f) }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
    }

    /** Mirror vertically (top-bottom flip). */
    fun flipV(src: Bitmap): Bitmap {
        val m = android.graphics.Matrix().apply { postScale(1f, -1f) }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
    }

    /** Crop a pixel rectangle (left, top, right, bottom) out of the photo. */

    // ---- Social Media Sizes bakes (v0.11.0) ----

    /** Social "Crop" mode: crop the framed region, scale to the exact target. */
    fun socialCrop(src: Bitmap, rectPx: IntArray, tw: Int, th: Int): Bitmap =
        Bitmap.createScaledBitmap(cropRegion(src, rectPx), tw, th, true)

    /** Social "Position" mode: cover-fill the target frame. panX/panY are -1..1
     *  fractions of the spare slack, zoom >= 1. The frame is always fully covered. */
    fun socialPosition(src: Bitmap, tw: Int, th: Int, zoom: Float, panX: Float, panY: Float): Bitmap {
        val out = Bitmap.createBitmap(tw, th, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val base = max(tw.toFloat() / src.width, th.toFloat() / src.height) * zoom.coerceIn(1f, 5f)
        val dw = src.width * base
        val dh = src.height * base
        val left = (tw - dw) / 2f - panX.coerceIn(-1f, 1f) * ((dw - tw) / 2f)
        val top = (th - dh) / 2f - panY.coerceIn(-1f, 1f) * ((dh - th) / 2f)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(src, null, RectF(left, top, left + dw, top + dh), paint)
        return out
    }

    enum class SocialFill { BLUR, WHITE, CUSTOM, TRANSPARENT }

    /** Social "Background" (fit) mode: the whole photo inside the target frame.
     *  BLUR pads with a blurred fill, WHITE/CUSTOM fill solid, TRANSPARENT keeps
     *  real alpha for PNG/WebP. */
    fun socialFit(src: Bitmap, tw: Int, th: Int, fill: SocialFill, customColor: Int): Bitmap {
        val out = Bitmap.createBitmap(tw, th, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        when (fill) {
            SocialFill.BLUR -> canvas.drawBitmap(stretch(blurred(src), tw, th), 0f, 0f, null)
            SocialFill.WHITE -> canvas.drawColor(Color.WHITE)
            SocialFill.CUSTOM -> canvas.drawColor(customColor)
            SocialFill.TRANSPARENT -> {}
        }
        val scale = min(tw.toFloat() / src.width, th.toFloat() / src.height)
        val w = (src.width * scale).coerceAtLeast(1f)
        val h = (src.height * scale).coerceAtLeast(1f)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(src, null, RectF((tw - w) / 2f, (th - h) / 2f, (tw + w) / 2f, (th + h) / 2f), paint)
        return out
    }

    /** Product collage: 2-4 photos cover-filled into a grid on one canvas.
     *  Layout adapts to the canvas shape: wide canvas splits left/right,
     *  tall canvas splits top/bottom. */
    fun collage(photos: List<Bitmap>, tw: Int, th: Int, gapPx: Int, bgColor: Int): Bitmap {
        val out = Bitmap.createBitmap(tw, th, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(bgColor)
        val n = photos.size.coerceIn(2, 4)
        val g = gapPx.toFloat()
        val cells = when {
            n == 2 && tw >= th -> listOf(
                RectF(0f, 0f, (tw - g) / 2f, th.toFloat()),
                RectF((tw + g) / 2f, 0f, tw.toFloat(), th.toFloat()))
            n == 2 -> listOf(
                RectF(0f, 0f, tw.toFloat(), (th - g) / 2f),
                RectF(0f, (th + g) / 2f, tw.toFloat(), th.toFloat()))
            n == 3 && tw >= th -> listOf(
                RectF(0f, 0f, (2 * tw - g) / 3f, th.toFloat()),
                RectF((2 * tw + 2 * g) / 3f, 0f, tw.toFloat(), (th - g) / 2f),
                RectF((2 * tw + 2 * g) / 3f, (th + g) / 2f, tw.toFloat(), th.toFloat()))
            n == 3 -> listOf(
                RectF(0f, 0f, tw.toFloat(), (2 * th - g) / 3f),
                RectF(0f, (2 * th + 2 * g) / 3f, (tw - g) / 2f, th.toFloat()),
                RectF((tw + g) / 2f, (2 * th + 2 * g) / 3f, tw.toFloat(), th.toFloat()))
            else -> listOf(
                RectF(0f, 0f, (tw - g) / 2f, (th - g) / 2f),
                RectF((tw + g) / 2f, 0f, tw.toFloat(), (th - g) / 2f),
                RectF(0f, (th + g) / 2f, (tw - g) / 2f, th.toFloat()),
                RectF((tw + g) / 2f, (th + g) / 2f, tw.toFloat(), th.toFloat()))
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        photos.take(n).forEachIndexed { i, photo ->
            val cell = cells[i]
            val base = max(cell.width() / photo.width, cell.height() / photo.height)
            val dw = photo.width * base
            val dh = photo.height * base
            val left = cell.left + (cell.width() - dw) / 2f
            val top = cell.top + (cell.height() - dh) / 2f
            canvas.save()
            canvas.clipRect(cell)
            canvas.drawBitmap(photo, null, RectF(left, top, left + dw, top + dh), paint)
            canvas.restore()
        }
        return out
    }

    fun cropRegion(src: Bitmap, rectPx: IntArray): Bitmap =
        Bitmap.createBitmap(src, rectPx[0], rectPx[1], rectPx[2] - rectPx[0], rectPx[3] - rectPx[1])

    /**
     * Bake straighten + horizontal/vertical tilt into a new bitmap the size of
     * the original. The transform is the same projective math the Crop & Rotate
     * preview shows (CropMath.transformedCorners + coverScale), so what he sees
     * is exactly what exports. Cover-zoom guarantees no empty corner wedges.
     */
    fun adjustPerspective(src: Bitmap, straightDeg: Float, tiltHDeg: Float, tiltVDeg: Float): Bitmap {
        val w = src.width
        val h = src.height
        val focal = 1.5f * max(w, h)
        val quad = CropMath.transformedCorners(w, h, straightDeg, tiltHDeg, tiltVDeg, focal)
        val scale = CropMath.coverScale(quad, w, h)
        val cx = w / 2f
        val cy = h / 2f
        val dst = FloatArray(8)
        for (i in 0..3) {
            dst[i * 2] = cx + (quad[i * 2] - cx) * scale
            dst[i * 2 + 1] = cy + (quad[i * 2 + 1] - cy) * scale
        }
        val srcPts = floatArrayOf(0f, 0f, w.toFloat(), 0f, w.toFloat(), h.toFloat(), 0f, h.toFloat())
        val m = android.graphics.Matrix()
        m.setPolyToPoly(srcPts, 0, dst, 0, 4)
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(src, m, paint)
        return out
    }
}