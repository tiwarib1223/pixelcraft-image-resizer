package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.TextOverlayUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * v0.16.8 Text Effects Pack (his "Yes all 8"): gradient direction, neon glow spread,
 * hollow centre, template field mapping, sticker bitmap ink - all through the shared draw path.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TextEffectsTest {

    private fun render(layer: TextOverlayUtils.TextLayer, w: Int = 400, h: Int = 400): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        TextOverlayUtils.drawAll(Canvas(bmp), listOf(layer), w, h)
        return bmp
    }

    private fun base(text: String): TextOverlayUtils.TextLayer {
        val l = TextOverlayUtils.TextLayer(id = 1L)
        l.text = text
        l.sizeFrac = 0.5f
        l.bold = true
        return l
    }

    private fun inkCount(b: Bitmap, alphaMin: Int): Int {
        var n = 0
        for (y in 0 until b.height step 2) for (x in 0 until b.width step 2) {
            if (Color.alpha(b.getPixel(x, y)) > alphaMin) n++
        }
        return n
    }

    @Test
    fun gradientPaintsTopStartBottomEnd() {
        val l = base("I")
        l.effect = TextOverlayUtils.TextEffect.GRADIENT
        l.gradientStartArgb = 0xFFFF0000.toInt()
        l.gradientEndArgb = 0xFF0000FF.toInt()
        val bmp = render(l)
        var top = -1
        var bottom = -1
        for (y in 0 until 400) {
            if (Color.alpha(bmp.getPixel(200, y)) > 100) { if (top < 0) top = y; bottom = y }
        }
        assertTrue("glyph found on centre column", top in 0..398 && bottom > top + 20)
        val topC = bmp.getPixel(200, top + 2)
        val botC = bmp.getPixel(200, bottom - 2)
        assertTrue("top is red-ish (start)", Color.red(topC) > Color.blue(topC))
        assertTrue("bottom is blue-ish (end)", Color.blue(botC) > Color.red(botC))
    }

    @Test
    fun neonGlowExtendsBeyondPlainFill() {
        val plain = render(base("O"))
        val neonL = base("O")
        neonL.effect = TextOverlayUtils.TextEffect.NEON
        val neon = render(neonL)
        val p = inkCount(plain, 40)
        val g = inkCount(neon, 40)
        assertTrue("neon glow adds ink beyond the fill ($g vs $p)", g > p + 30)
    }

    @Test
    fun hollowLeavesCentreEmpty() {
        val l = base("O")
        l.effect = TextOverlayUtils.TextEffect.HOLLOW
        val bmp = render(l)
        assertEquals("hollow centre stays transparent", 0, Color.alpha(bmp.getPixel(200, 195)))
        assertTrue("hollow ring drawn", inkCount(bmp, 100) > 20)
    }

    @Test
    fun templatesSetExpectedFields() {
        val l = base("Hello")
        TextOverlayUtils.applyTemplate(l, "Neon Pink")
        assertEquals(TextOverlayUtils.TextEffect.NEON, l.effect)
        assertEquals(0xFFFFFFFF.toInt(), l.colorArgb)
        assertEquals(0xFFF95F6A.toInt(), l.glowColorArgb)
        assertTrue(l.bold)
        TextOverlayUtils.applyTemplate(l, "Hollow Gold")
        assertEquals(TextOverlayUtils.TextEffect.HOLLOW, l.effect)
        assertEquals(0xFFF5B942.toInt(), l.colorArgb)
        TextOverlayUtils.applyTemplate(l, "Sunset Fade")
        assertEquals(TextOverlayUtils.TextEffect.GRADIENT, l.effect)
        assertEquals(0xFFFF8A32.toInt(), l.gradientStartArgb)
        assertEquals(0xFFF95F6A.toInt(), l.gradientEndArgb)
    }

    @Test
    fun stickerBitmapsHaveInkAndTransparency() {
        TextOverlayUtils.STICKERS.forEach { st ->
            val b = TextOverlayUtils.makeStickerBitmap(st.kind, 128, st.colorArgb)
            var ink = 0
            for (y in 0 until 128) for (x in 0 until 128) if (Color.alpha(b.getPixel(x, y)) > 100) ink++
            assertTrue(st.label + " has ink", ink > 500)
            assertTrue(st.label + " corner stays transparent", Color.alpha(b.getPixel(2, 2)) == 0)
        }
    }
}
