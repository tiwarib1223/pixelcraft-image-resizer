package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * v0.18.0 Photo Adjustments engine (his spec): 12 sliders, all -100..+100, all non-destructive.
 * Pure bitmap math, fully offline, JVM-testable. ONE apply path feeds the live preview and the
 * export, so what he sees is what he saves.
 *
 * Implementation: tonal sliders (brightness, contrast, exposure, highlights, shadows, fade,
 * temperature, tint) compile into per-channel 256-entry lookup tables - one fast pass, no
 * repeated compression. Colour sliders (saturation, vibrance) run as a ColorMatrix. Detail
 * sliders (sharpness, clarity) run an unsharp-mask convolution; clarity works on a blurred copy
 * so it boosts LOCAL (mid-tone) contrast while sharpness boosts edges.
 *
 * Honest approximations (documented in PROGRESS.md): vibrance approximates skin-protecting smart
 * saturation with a milder saturation curve; clarity's local contrast uses a fixed small radius.
 */
object PhotoAdjust {

    class Params {
        var brightness = 0f; var contrast = 0f; var saturation = 0f; var exposure = 0f
        var highlights = 0f; var shadows = 0f; var temperature = 0f; var tint = 0f
        var sharpness = 0f; var clarity = 0f; var fade = 0f; var vibrance = 0f

        fun isDefault() = listOf(brightness, contrast, saturation, exposure, highlights, shadows,
            temperature, tint, sharpness, clarity, fade, vibrance).all { it == 0f }

        fun copyFrom(o: Params) {
            brightness = o.brightness; contrast = o.contrast; saturation = o.saturation; exposure = o.exposure
            highlights = o.highlights; shadows = o.shadows; temperature = o.temperature; tint = o.tint
            sharpness = o.sharpness; clarity = o.clarity; fade = o.fade; vibrance = o.vibrance
        }

        fun copy(): Params = Params().also { it.copyFrom(this) }

        override fun equals(other: Any?): Boolean =
            other is Params && listOf(brightness, contrast, saturation, exposure, highlights, shadows,
                temperature, tint, sharpness, clarity, fade, vibrance) ==
            listOf(other.brightness, other.contrast, other.saturation, other.exposure, other.highlights,
                other.shadows, other.temperature, other.tint, other.sharpness, other.clarity, other.fade, other.vibrance)

        override fun hashCode(): Int = 0
    }

    private fun clamp8(v: Float): Int = v.roundToInt().coerceIn(0, 255)

    /**
     * Per-channel lookup tables for the tonal sliders. Everything maps input 0..255 -> output
     * 0..255 in one pass; the order inside the LUT is: exposure -> brightness/contrast ->
     * shadows/highlights -> fade -> temperature/tint.
     */
    fun buildLuts(p: Params): Array<IntArray> {
        val r = IntArray(256); val g = IntArray(256); val b = IntArray(256)
        val expGain = 2.0.pow((p.exposure / 100f) * 1.5).toFloat()          // +-100 ~= +-1.5 EV
        val brightAdd = p.brightness * 0.6f                                  // +-60 levels
        val contrastFactor = 1f + p.contrast / 100f                          // 0..2 around 128
        val shadowLift = p.shadows * 0.5f
        val highShift = p.highlights * 0.5f
        val fadeLift = p.fade * 0.35f                                        // raises black point
        val temp = p.temperature * 0.45f                                     // warm: +R -B
        val tint = p.tint * 0.35f                                            // +G / -G (magenta)
        for (i in 0..255) {
            val lum = i / 255f
            var v = i * expGain
            v = (v - 128f) * contrastFactor + 128f + brightAdd
            // shadows affect dark tones, highlights affect bright tones (smooth weights)
            v += shadowLift * (1f - lum) * (1f - lum)
            v += highShift * lum * lum
            // fade: lift blacks, gently cap whites
            v = fadeLift + v * (1f - p.fade / 100f * 0.25f)
            r[i] = clamp8(v + temp)
            g[i] = clamp8(v + tint)
            b[i] = clamp8(v - temp)
        }
        return arrayOf(r, g, b)
    }

    /** Saturation + vibrance as one ColorMatrix. Vibrance = gentler curve (approximation). */
    private fun colourMatrix(p: Params): ColorMatrix? {
        if (p.saturation == 0f && p.vibrance == 0f) return null
        val sat = (p.saturation / 100f).coerceIn(-1f, 1f)
        val vib = (p.vibrance / 100f).coerceIn(-1f, 1f)
        // vibrance scales with a sqrt curve: strong effect on low-sat photos, mild on vivid ones
        val combined = if (sat >= 0f) sat + vib * (1f - sat) * 0.7f else sat + vib * 0.7f
        val s = (1f + combined).coerceIn(0f, 2.5f)
        return ColorMatrix().apply { setSaturation(s) }
    }

    private fun needsDetail(p: Params) = p.sharpness != 0f || p.clarity != 0f

    /** Unsharp mask: blurred copy, then src + amount * (src - blur). Radius is fixed and small. */
    private fun detailPass(src: Bitmap, p: Params): Bitmap {
        val w = src.width; val h = src.height
        // box blur (two passes) - fast enough and Robolectric-safe
        val blur = boxBlur(src, radius = if (p.clarity != 0f) 9 else 3)
        val a = IntArray(w * h); val bl = IntArray(w * h)
        src.getPixels(a, 0, w, 0, 0, w, h)
        blur.getPixels(bl, 0, w, 0, 0, w, h)
        val sharpAmt = p.sharpness / 100f * 0.9f
        val clarityAmt = p.clarity / 100f * 0.6f
        for (i in a.indices) {
            val ca = a[i]; val cb = bl[i]
            val amount = if (clarityAmt != 0f) clarityAmt else sharpAmt
            val edgeSharp = if (clarityAmt != 0f) sharpAmt else 0f
            // When both run, clarity uses the wide blur and sharpness still wants edges; with one
            // blur radius available we weight them together (approximation, documented).
            val total = if (clarityAmt != 0f) clarityAmt + edgeSharp else amount
            var rr = ((ca shr 16) and 0xFF) + (((ca shr 16) and 0xFF) - ((cb shr 16) and 0xFF)) * total
            var gg = ((ca shr 8) and 0xFF) + (((ca shr 8) and 0xFF) - ((cb shr 8) and 0xFF)) * total
            var bb = (ca and 0xFF) + ((ca and 0xFF) - (cb and 0xFF)) * total
            a[i] = (ca and 0xFF000000.toInt()) or
                (rr.roundToInt().coerceIn(0, 255) shl 16) or
                (gg.roundToInt().coerceIn(0, 255) shl 8) or
                bb.roundToInt().coerceIn(0, 255)
        }
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(a, 0, w, 0, 0, w, h)
        blur.recycle()
        return out
    }

    private fun boxBlur(src: Bitmap, radius: Int): Bitmap {
        val w = src.width; val h = src.height
        val srcPx = IntArray(w * h)
        src.getPixels(srcPx, 0, w, 0, 0, w, h)
        val tmp = IntArray(w * h)
        val out = IntArray(w * h)
        // horizontal
        for (y in 0 until h) {
            var rs = 0; var gs = 0; var bs = 0
            val row = y * w
            for (x in -radius..radius) {
                val c = srcPx[row + x.coerceIn(0, w - 1)]
                rs += (c shr 16) and 0xFF; gs += (c shr 8) and 0xFF; bs += c and 0xFF
            }
            val win = 2 * radius + 1
            for (x in 0 until w) {
                tmp[row + x] = (0xFF shl 24) or ((rs / win) shl 16) or ((gs / win) shl 8) or (bs / win)
                val addC = srcPx[row + (x + radius + 1).coerceIn(0, w - 1)]
                val subC = srcPx[row + (x - radius).coerceIn(0, w - 1)]
                rs += ((addC shr 16) and 0xFF) - ((subC shr 16) and 0xFF)
                gs += ((addC shr 8) and 0xFF) - ((subC shr 8) and 0xFF)
                bs += (addC and 0xFF) - (subC and 0xFF)
            }
        }
        // vertical
        for (x in 0 until w) {
            var rs = 0; var gs = 0; var bs = 0
            for (y in -radius..radius) {
                val c = tmp[y.coerceIn(0, h - 1) * w + x]
                rs += (c shr 16) and 0xFF; gs += (c shr 8) and 0xFF; bs += c and 0xFF
            }
            val win = 2 * radius + 1
            for (y in 0 until h) {
                out[y * w + x] = (0xFF shl 24) or ((rs / win) shl 16) or ((gs / win) shl 8) or (bs / win)
                val addC = tmp[(y + radius + 1).coerceIn(0, h - 1) * w + x]
                val subC = tmp[(y - radius).coerceIn(0, h - 1) * w + x]
                rs += ((addC shr 16) and 0xFF) - ((subC shr 16) and 0xFF)
                gs += ((addC shr 8) and 0xFF) - ((subC shr 8) and 0xFF)
                bs += (addC and 0xFF) - (subC and 0xFF)
            }
        }
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bmp.setPixels(out, 0, w, 0, 0, w, h)
        return bmp
    }

    /**
     * Apply every slider to [src] and return a NEW bitmap (src is untouched - non-destructive).
     * Pipeline: detail (unsharp) -> colour matrix -> tonal LUTs.
     */
    fun apply(src: Bitmap, p: Params): Bitmap {
        if (p.isDefault()) return src.copy(Bitmap.Config.ARGB_8888, false)
        var work = if (needsDetail(p)) detailPass(src, p) else src.copy(Bitmap.Config.ARGB_8888, false)
        val cm = colourMatrix(p)
        if (cm != null) {
            val out = Bitmap.createBitmap(work.width, work.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(out)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            paint.colorFilter = ColorMatrixColorFilter(cm)
            canvas.drawBitmap(work, 0f, 0f, paint)
            work = out
        }
        val luts = buildLuts(p)
        val w = work.width; val h = work.height
        val px = IntArray(w * h)
        work.getPixels(px, 0, w, 0, 0, w, h)
        val lr = luts[0]; val lg = luts[1]; val lb = luts[2]
        for (i in px.indices) {
            val c = px[i]
            px[i] = (c and 0xFF000000.toInt()) or
                (lr[(c shr 16) and 0xFF] shl 16) or
                (lg[(c shr 8) and 0xFF] shl 8) or
                lb[c and 0xFF]
        }
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(px, 0, w, 0, 0, w, h)
        return out
    }
}
