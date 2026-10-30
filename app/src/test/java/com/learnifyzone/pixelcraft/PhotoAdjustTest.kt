package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import com.learnifyzone.pixelcraft.image.PhotoAdjust
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** v0.18.0 Photo Adjustments engine: every slider moves the right pixels the right way. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PhotoAdjustTest {

    private fun solid(w: Int, h: Int, color: Int): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        b.eraseColor(color)
        return b
    }

    private fun gradient(): Bitmap {
        val b = Bitmap.createBitmap(256, 1, Bitmap.Config.ARGB_8888)
        val px = IntArray(256) { i -> (0xFF shl 24) or (i shl 16) or (i shl 8) or i }
        b.setPixels(px, 0, 256, 0, 0, 256, 1)
        return b
    }

    private fun lum(c: Int) = (((c shr 16) and 0xFF) + ((c shr 8) and 0xFF) + (c and 0xFF)) / 3

    private fun close(a: Int, b: Int, tol: Int = 12): Boolean {
        fun d(x: Int, y: Int) = Math.abs(x - y) <= tol
        return d((a shr 16) and 0xFF, (b shr 16) and 0xFF) && d((a shr 8) and 0xFF, (b shr 8) and 0xFF) && d(a and 0xFF, b and 0xFF)
    }

    @Test
    fun defaultParamsReturnIdenticalPixels() {
        val src = gradient()
        val out = PhotoAdjust.apply(src, PhotoAdjust.Params())
        for (x in intArrayOf(0, 64, 128, 255)) assertEquals(src.getPixel(x, 0), out.getPixel(x, 0))
        assertTrue("source untouched", src.getPixel(10, 0) != 0)
    }

    @Test
    fun brightnessRaisesLuminance() {
        val out = PhotoAdjust.apply(solid(8, 8, 0xFF808080.toInt()), PhotoAdjust.Params().apply { brightness = 50f })
        assertTrue(lum(out.getPixel(4, 4)) > 128 + 20)
    }

    @Test
    fun contrastSpreadsAroundMidpoint() {
        val src = gradient()
        val out = PhotoAdjust.apply(src, PhotoAdjust.Params().apply { contrast = 60f })
        assertTrue("darks darker", (out.getPixel(60, 0) and 0xFF) < 60)
        assertTrue("brights brighter", (out.getPixel(196, 0) and 0xFF) > 196)
    }

    @Test
    fun saturationZeroIsGreyAndBoostRaisesSpread() {
        val src = solid(8, 8, 0xFFE04040.toInt())
        val grey = PhotoAdjust.apply(src, PhotoAdjust.Params().apply { saturation = -100f })
        val c = grey.getPixel(4, 4)
        assertTrue("desaturated to grey", close(c, 0xFF808080.toInt(), 30))
        val boosted = PhotoAdjust.apply(src, PhotoAdjust.Params().apply { saturation = 60f })
        val cb = boosted.getPixel(4, 4)
        val spread = ((cb shr 16) and 0xFF) - (cb and 0xFF)
        assertTrue("more saturated", spread > 0xE0 - 0x40)
    }

    @Test
    fun exposureDoublesLight() {
        val out = PhotoAdjust.apply(solid(8, 8, 0xFF404040.toInt()), PhotoAdjust.Params().apply { exposure = 67f })
        assertTrue("exposure brightens", lum(out.getPixel(4, 4)) > 0x60)
    }

    @Test
    fun shadowsLiftDarksMoreThanBrights() {
        val src = gradient()
        val out = PhotoAdjust.apply(src, PhotoAdjust.Params().apply { shadows = 80f })
        val darkDelta = (out.getPixel(30, 0) and 0xFF) - 30
        val brightDelta = (out.getPixel(230, 0) and 0xFF) - 230
        assertTrue("shadows lift darks", darkDelta > 20)
        assertTrue("brights barely move", brightDelta < darkDelta / 3)
    }

    @Test
    fun highlightsMoveBrightsMoreThanDarks() {
        val src = gradient()
        val out = PhotoAdjust.apply(src, PhotoAdjust.Params().apply { highlights = -80f })
        val darkDelta = Math.abs((out.getPixel(30, 0) and 0xFF) - 30)
        val brightDelta = (out.getPixel(230, 0) and 0xFF) - 230
        assertTrue("highlights pull brights down", brightDelta < -15)
        assertTrue("darks barely move", darkDelta < Math.abs(brightDelta) / 3)
    }

    @Test
    fun temperatureWarmsAndCools() {
        val warm = PhotoAdjust.apply(solid(8, 8, 0xFF808080.toInt()), PhotoAdjust.Params().apply { temperature = 60f }).getPixel(4, 4)
        assertTrue("warm adds red", ((warm shr 16) and 0xFF) > 0x80 + 15)
        assertTrue("warm cuts blue", (warm and 0xFF) < 0x80 - 15)
        val cool = PhotoAdjust.apply(solid(8, 8, 0xFF808080.toInt()), PhotoAdjust.Params().apply { temperature = -60f }).getPixel(4, 4)
        assertTrue("cool adds blue", (cool and 0xFF) > 0x80 + 15)
    }

    @Test
    fun tintShiftsGreen() {
        val t = PhotoAdjust.apply(solid(8, 8, 0xFF808080.toInt()), PhotoAdjust.Params().apply { tint = 80f }).getPixel(4, 4)
        assertTrue("tint adds green", ((t shr 8) and 0xFF) > 0x80 + 20)
    }

    @Test
    fun fadeLiftsBlackPoint() {
        val out = PhotoAdjust.apply(solid(8, 8, 0xFF202020.toInt()), PhotoAdjust.Params().apply { fade = 80f })
        assertTrue("blacks lifted", lum(out.getPixel(4, 4)) > 0x20 + 15)
    }

    @Test
    fun sharpnessBoostsEdges() {
        // black/white edge: sharpen must create overshoot (ringing) at the boundary
        val b = Bitmap.createBitmap(40, 20, Bitmap.Config.ARGB_8888)
        val px = IntArray(40 * 20) { i -> if ((i % 40) < 20) 0xFF202020.toInt() else 0xFFE0E0E0.toInt() }
        b.setPixels(px, 0, 40, 0, 0, 40, 20)
        val out = PhotoAdjust.apply(b, PhotoAdjust.Params().apply { sharpness = 100f })
        val darkSide = out.getPixel(18, 10) and 0xFF
        val lightSide = out.getPixel(21, 10) and 0xFF
        assertTrue("dark side of edge darkens (overshoot)", darkSide < 0x20)
        assertTrue("light side of edge brightens", lightSide > 0xE0 - 25)
    }

    @Test
    fun clarityChangesMidtoneContrast() {
        // A smooth S-curve (tanh): blur differs from the source where curvature is non-zero,
        // so clarity must STEEPEN the curve across the midtone region.
        val b = Bitmap.createBitmap(60, 1, Bitmap.Config.ARGB_8888)
        val px = IntArray(60) { i ->
            val v = (128 + 60 * Math.tanh((i - 30) / 6.0)).toInt().coerceIn(0, 255)
            (0xFF shl 24) or (v shl 16) or (v shl 8) or v
        }
        b.setPixels(px, 0, 60, 0, 0, 60, 1)
        val inGap = (b.getPixel(32, 0) and 0xFF) - (b.getPixel(28, 0) and 0xFF)
        val out = PhotoAdjust.apply(b, PhotoAdjust.Params().apply { clarity = 100f })
        val outGap = (out.getPixel(32, 0) and 0xFF) - (out.getPixel(28, 0) and 0xFF)
        assertTrue("midtone gap widened ($inGap -> $outGap)", outGap > inGap + 2)
    }

    @Test
    fun vibranceBoostsSaturationMildly() {
        val dull = solid(8, 8, 0xFF9A7070.toInt())
        val out = PhotoAdjust.apply(dull, PhotoAdjust.Params().apply { vibrance = 100f })
        val c = out.getPixel(4, 4)
        val spread = ((c shr 16) and 0xFF) - (c and 0xFF)
        assertTrue("vibrance increases colour spread", spread > 0x9A - 0x70)
    }

    @Test
    fun independentSlidersCompose() {
        // brightness + temperature together: both effects visible
        val out = PhotoAdjust.apply(solid(8, 8, 0xFF606060.toInt()), PhotoAdjust.Params().apply { brightness = 40f; temperature = 40f })
        val c = out.getPixel(4, 4)
        assertTrue("brighter", lum(c) > 0x60 + 15)
        assertTrue("warmer", ((c shr 16) and 0xFF) > (c and 0xFF))
    }

    @Test
    fun paramsCopyAndDefault() {
        val p = PhotoAdjust.Params()
        assertTrue(p.isDefault())
        p.brightness = 10f
        assertFalse(p.isDefault())
        val q = p.copy()
        assertEquals(p, q)
        q.brightness = 0f
        assertFalse(p == q)
    }
}
