package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.exp
import kotlin.math.roundToInt

/**
 * Document Scanner enhancement engine (his original Scanner recording, Screen 4):
 * six page filters + manual adjustments, all non-destructive - every render starts from the
 * stored original page, so switching filters never degrades the image.
 *
 * Standard sliders (brightness, contrast, highlights, shadows, sharpness) reuse the proven
 * PhotoAdjust engine; the two document-specific sliders (Background Whitening, Shadow
 * Reduction) run as one extra per-channel LUT pass in this file. ONE render path feeds the
 * live preview and the export, so what he sees is what he saves.
 */
object ScanEnhance {

    class DocParams {
        var brightness = 0f; var contrast = 0f; var highlights = 0f; var shadows = 0f
        var sharpness = 0f; var whitening = 0f; var shadowReduction = 0f

        fun isDefault() = listOf(brightness, contrast, highlights, shadows, sharpness,
            whitening, shadowReduction).all { it == 0f }

        fun copyFrom(o: DocParams) {
            brightness = o.brightness; contrast = o.contrast; highlights = o.highlights
            shadows = o.shadows; sharpness = o.sharpness; whitening = o.whitening
            shadowReduction = o.shadowReduction
        }

        fun copy(): DocParams = DocParams().also { it.copyFrom(this) }
    }

    /** The six filters from his spec, in his order. */
    val FILTERS = listOf("original", "auto", "colour", "gray", "bw", "photo")

    fun filterLabel(id: String) = when (id) {
        "auto" -> "Auto Enhance"
        "colour" -> "Colour"
        "gray" -> "Grayscale"
        "bw" -> "Black & White"
        "photo" -> "Photo"
        else -> "Original"
    }

    private fun clamp8(v: Float) = v.roundToInt().coerceIn(0, 255)

    private fun drawWithMatrix(src: Bitmap, cm: ColorMatrix): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return out
    }

    private fun applyLut(src: Bitmap, lut: IntArray): Bitmap {
        val w = src.width; val h = src.height
        val px = IntArray(w * h)
        src.getPixels(px, 0, w, 0, 0, w, h)
        for (i in px.indices) {
            val c = px[i]
            px[i] = (c and 0xFF000000.toInt()) or
                (lut[(c shr 16) and 0xFF] shl 16) or
                (lut[(c shr 8) and 0xFF] shl 8) or
                lut[c and 0xFF]
        }
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(px, 0, w, 0, 0, w, h)
        return out
    }

    /** Auto-contrast: stretch the luminance range between the 1st and 99th percentile. */
    private fun autoContrastLut(src: Bitmap): IntArray {
        val sample = Bitmap.createScaledBitmap(src, 96, (96f * src.height / src.width).roundToInt().coerceAtLeast(1), true)
        val n = sample.width * sample.height
        val px = IntArray(n)
        sample.getPixels(px, 0, sample.width, 0, 0, sample.width, sample.height)
        val hist = IntArray(256)
        for (c in px) {
            val r = (c shr 16) and 0xFF; val g = (c shr 8) and 0xFF; val b = c and 0xFF
            hist[(r * 299 + g * 587 + b * 114) / 1000]++
        }
        sample.recycle()
        var lo = 0; var hi = 255
        var acc = 0; val loCut = n / 100; val hiCut = n - loCut
        for (i in 0..255) { acc += hist[i]; if (acc > loCut) { lo = i; break } }
        acc = 0
        for (i in 0..255) { acc += hist[i]; if (acc > hiCut) { hi = i; break } }
        if (hi - lo < 16) { lo = 0; hi = 255 } // nearly flat image: leave it alone
        return IntArray(256) { i -> clamp8((i - lo) * 255f / (hi - lo)) }
    }

    /** Black & White document look: grayscale plus a steep S-curve centred on light grey. */
    private fun bwLut(): IntArray = IntArray(256) { i ->
        clamp8(255f / (1f + exp(-(i - 160f) / 16f)))
    }

    /** The filter step of the pipeline. Returns a NEW bitmap; src is untouched. */
    fun applyFilter(src: Bitmap, id: String): Bitmap = when (id) {
        "auto" -> {
            val stretched = applyLut(src, autoContrastLut(src))
            // mild saturation lift so the page does not look washed out after the stretch
            drawWithMatrix(stretched, ColorMatrix().apply { setSaturation(1.1f) }).also { stretched.recycle() }
        }
        "colour" -> {
            val cm = ColorMatrix().apply { setSaturation(1.15f) }
            // slight contrast: scale around 128
            val c = 1.08f; val t = 128f * (1f - c)
            val boost = ColorMatrix(floatArrayOf(
                c, 0f, 0f, 0f, t,
                0f, c, 0f, 0f, t,
                0f, 0f, c, 0f, t,
                0f, 0f, 0f, 1f, 0f))
            boost.postConcat(cm)
            drawWithMatrix(src, boost)
        }
        "gray" -> drawWithMatrix(src, ColorMatrix().apply { setSaturation(0f) })
        "bw" -> {
            val gray = drawWithMatrix(src, ColorMatrix().apply { setSaturation(0f) })
            applyLut(gray, bwLut()).also { gray.recycle() }
        }
        "photo" -> {
            // gentle: keep photos looking like photos, just a touch of snap
            val c = 1.05f; val t = 128f * (1f - c)
            drawWithMatrix(src, ColorMatrix(floatArrayOf(
                c, 0f, 0f, 0f, t,
                0f, c, 0f, 0f, t,
                0f, 0f, c, 0f, t,
                0f, 0f, 0f, 1f, 0f)).apply { postConcat(ColorMatrix().apply { setSaturation(1.06f) }) })
        }
        else -> src.copy(Bitmap.Config.ARGB_8888, false)
    }

    /**
     * Document-specific tonal pass: Background Whitening pushes near-white paper to pure white,
     * Shadow Reduction lifts dark shadows without touching print. One shared LUT, -100..+100
     * sliders (only 0..+100 is meaningful for these two; negatives are clamped to 0).
     */
    private fun docLut(p: DocParams): IntArray? {
        val w = p.whitening.coerceIn(0f, 100f) / 100f
        val s = p.shadowReduction.coerceIn(0f, 100f) / 100f
        if (w == 0f && s == 0f) return null
        return IntArray(256) { i ->
            var v = i.toFloat()
            if (s > 0f && i < 110) {
                val t = 1f - i / 110f
                v += (110 - i) * s * 0.85f * t
            }
            if (w > 0f && i > 175) {
                val t = ((i - 175) / 80f).coerceIn(0f, 1f)
                v += (255 - v) * w * t * t
            }
            clamp8(v)
        }
    }

    private fun toPhotoParams(p: DocParams): PhotoAdjust.Params {
        val out = PhotoAdjust.Params()
        out.brightness = p.brightness; out.contrast = p.contrast
        out.highlights = p.highlights; out.shadows = p.shadows; out.sharpness = p.sharpness
        return out
    }

    /** Full page pipeline: filter -> standard sliders (PhotoAdjust) -> whitening/shadow LUT. */
    fun render(src: Bitmap, filter: String, p: DocParams): Bitmap {
        var work = applyFilter(src, filter)
        val pp = toPhotoParams(p)
        if (!pp.isDefault()) {
            val adjusted = PhotoAdjust.apply(work, pp)
            if (adjusted !== work) work.recycle()
            work = adjusted
        }
        val lut = docLut(p)
        if (lut != null) {
            val whitened = applyLut(work, lut)
            work.recycle()
            work = whitened
        }
        return work
    }
}
