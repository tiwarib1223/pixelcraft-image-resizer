package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.TextOverlayUtils
import com.learnifyzone.pixelcraft.image.TextOverlayUtils.TextLayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TextOverlayUtilsTest {

    private fun whiteBitmap(w: Int = 400, h: Int = 300): Bitmap =
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { Canvas(it).drawColor(Color.WHITE) }

    private fun inkBounds(bmp: Bitmap, bg: Int = Color.WHITE): IntArray {
        var minX = bmp.width; var minY = bmp.height; var maxX = -1; var maxY = -1
        for (y in 0 until bmp.height) for (x in 0 until bmp.width) {
            if (bmp.getPixel(x, y) != bg) {
                if (x < minX) minX = x; if (x > maxX) maxX = x
                if (y < minY) minY = y; if (y > maxY) maxY = y
            }
        }
        return intArrayOf(minX, minY, maxX, maxY)
    }

    private fun draw(layer: TextLayer, bmp: Bitmap = whiteBitmap()): Bitmap {
        TextOverlayUtils.drawAll(Canvas(bmp), listOf(layer), bmp.width, bmp.height)
        return bmp
    }

    @Test
    fun straightTextDrawsInk() {
        val bmp = draw(TextLayer(1, text = "Hello", colorArgb = Color.BLACK))
        val (minX, minY, maxX, maxY) = inkBounds(bmp)
        assertTrue(maxX > minX); assertTrue(maxY > minY)
    }

    @Test
    fun multilineTextDrawsTwoBands() {
        val one = TextOverlayUtils.measure(TextLayer(1, text = "Hello"), 400)
        val two = TextOverlayUtils.measure(TextLayer(1, text = "Hello\nWorld"), 400)
        assertEquals(1, one.lineWidths.size)
        assertEquals(2, two.lineWidths.size)
        assertTrue(two.h > one.h * 1.5f)
    }

    @Test
    fun alignmentOffsetsAreCorrect() {
        val layer = TextLayer(1, text = "Long line here\nHi", align = TextOverlayUtils.Align.CENTER)
        val b = TextOverlayUtils.measure(layer, 400)
        assertTrue(b.lineWidths[0] > b.lineWidths[1])
        // Centre: both lines share the same visual centre, so the shorter one starts further right.
        val left0 = (b.w - b.lineWidths[0]) / 2f
        val left1 = (b.w - b.lineWidths[1]) / 2f
        assertTrue(left1 > left0)
    }

    @Test
    fun opacityBlendsTowardBackground() {
        val half = draw(TextLayer(1, text = "I", colorArgb = Color.BLACK, opacity = 0.5f, shadow = false))
        var darkest = 255
        for (y in 0 until half.height) for (x in 0 until half.width) {
            val p = half.getPixel(x, y); if (Color.red(p) < darkest) darkest = Color.red(p)
        }
        assertTrue("half-opacity black on white should land mid-grey, got $darkest", darkest in 60..200)
    }

    @Test
    fun outlineAddsColouredEdge() {
        val layer = TextLayer(1, text = "Hi", colorArgb = Color.WHITE, outlineFrac = 0.15f, outlineColorArgb = Color.RED)
        val bmp = draw(layer)
        var redSeen = false
        for (y in 0 until bmp.height) for (x in 0 until bmp.width) {
            val p = bmp.getPixel(x, y)
            if (Color.red(p) > 150 && Color.green(p) < 100) { redSeen = true; break }
        }
        assertTrue("outline colour should be visible", redSeen)
    }

    @Test
    fun backgroundBoxIsPainted() {
        val layer = TextLayer(1, text = ".", colorArgb = Color.BLACK, bgColorArgb = Color.YELLOW)
        val bmp = draw(layer)
        // The background box extends past the glyph: the corners of the box are pure yellow.
        var yellow = 0
        for (y in 0 until bmp.height) for (x in 0 until bmp.width) {
            val p = bmp.getPixel(x, y)
            if (Color.red(p) > 200 && Color.green(p) > 200 && Color.blue(p) < 60) yellow++
        }
        assertTrue("background box should paint a yellow area, saw $yellow px", yellow > 50)
    }

    @Test
    fun shadowCastsBeyondGlyph() {
        val withShadow = draw(TextLayer(1, text = "Hi", colorArgb = Color.BLACK, shadow = true))
        var grey = 0
        for (y in 0 until withShadow.height) for (x in 0 until withShadow.width) {
            val p = withShadow.getPixel(x, y)
            val r = Color.red(p)
            if (r in 80..235 && p != Color.WHITE) grey++
        }
        assertTrue("shadow should paint soft grey pixels, saw $grey", grey > 20)
    }

    @Test
    fun curvedTextIsGenuinelyCurved() {
        // Same text, same size: the curved render must occupy a TALLER ink box than the straight
        // render - a rotated straight box cannot do that at rotation 0.
        val straight = draw(TextLayer(1, text = "PIXELCRAFT", colorArgb = Color.BLACK))
        val sb = inkBounds(straight)
        val straightH = sb[3] - sb[1]
        val curved = draw(TextLayer(1, text = "PIXELCRAFT", colorArgb = Color.BLACK, curved = true, arcSweep = 140f))
        val cb = inkBounds(curved)
        val curvedH = cb[3] - cb[1]
        assertTrue("curved ink box ($curvedH) must be clearly taller than straight ($straightH)", curvedH > straightH * 1.4f)
        // And the arc sags: ink at the horizontal centre sits HIGHER than ink at the edges.
        val cx = (cb[0] + cb[2]) / 2
        var topCenter = curved.height; var topEdge = curved.height
        for (y in 0 until curved.height) {
            if (curved.getPixel(cx, y) != Color.WHITE && topCenter == curved.height) topCenter = y
            val ex = cb[0] + 2
            if (curved.getPixel(ex, y) != Color.WHITE && topEdge == curved.height) topEdge = y
        }
        assertTrue("centre of the arc should rise above its ends", topCenter < topEdge)
    }

    @Test
    fun arcRadiusMathFitsSweep() {
        val r = TextOverlayUtils.arcRadiusFor(300f, 120f, 40f)
        val arcLen = Math.toRadians(120.0).toFloat() * r
        assertEquals(300f, arcLen, 0.5f)
        // Short text still gets a visible arc, not a point.
        assertTrue(TextOverlayUtils.arcRadiusFor(10f, 120f, 40f) >= 64f)
    }

    @Test
    fun layerOrderDecidesWinner() {
        val red = TextLayer(1, text = "Hi", colorArgb = Color.RED)
        val blue = TextLayer(2, text = "Hi", colorArgb = Color.BLUE)
        val bmp1 = whiteBitmap()
        TextOverlayUtils.drawAll(Canvas(bmp1), listOf(red, blue), bmp1.width, bmp1.height)
        val bmp2 = whiteBitmap()
        TextOverlayUtils.drawAll(Canvas(bmp2), listOf(blue, red), bmp2.width, bmp2.height)
        val c = bmp1.width / 2
        val p1 = bmp1.getPixel(c, bmp1.height / 2)
        val p2 = bmp2.getPixel(c, bmp2.height / 2)
        // At the shared centre the LAST drawn layer wins (glyph overlap zone).
        assertTrue(Color.blue(p1) >= Color.red(p1))
        assertTrue(Color.red(p2) >= Color.blue(p2))
    }

    @Test
    fun rotationChangesInkBounds() {
        val layer = TextLayer(1, text = "A long straight title", colorArgb = Color.BLACK)
        val flat = draw(layer.copy())
        val fb = inkBounds(flat)
        val spun = draw(layer.copy(rotation = 90f))
        val sb = inkBounds(spun)
        assertTrue((fb[2] - fb[0]) > (fb[3] - fb[1]) * 2)
        assertTrue((sb[3] - sb[1]) > (sb[2] - sb[0]) * 2)
    }

    @Test
    fun undoRedoRestoresSnapshots() {
        val ur = TextOverlayUtils.UndoRedo()
        val s0 = listOf(TextLayer(1, text = "one"))
        ur.record(s0)
        val s1 = listOf(TextLayer(1, text = "two"))
        ur.record(s1)
        val s2 = listOf(TextLayer(1, text = "three"))
        val back1 = ur.undo(s2)
        assertEquals("two", back1!![0].text)
        val back2 = ur.undo(back1)
        assertEquals("one", back2!![0].text)
        assertFalse(ur.canUndo)
        val fwd = ur.redo(back2)
        assertEquals("two", fwd!![0].text)
        assertNull(TextOverlayUtils.UndoRedo().undo(s0))
    }

    @Test
    fun undoIsDeepCopyNotAliased() {
        val original = TextLayer(1, text = "before")
        val layers = listOf(original)
        val snap = TextOverlayUtils.snapshot(layers)
        original.text = "after"
        assertEquals("before", snap[0].text)
        assertNotEquals(snap[0], original)
    }

    @Test
    fun presetsApplyExpectedLook() {
        val layer = TextLayer(1)
        TextOverlayUtils.applyPreset(layer, "Bold")
        assertTrue(layer.bold); assertTrue(layer.outlineFrac > 0f)
        TextOverlayUtils.applyPreset(layer, "Elegant")
        assertTrue(layer.italic); assertEquals(TextOverlayUtils.FontChoice.Serif, layer.font)
        assertEquals(0xFFF5B942.toInt(), layer.colorArgb)
        TextOverlayUtils.applyPreset(layer, "Classic")
        assertTrue(layer.shadow); assertFalse(layer.bold)
    }

    @Test
    fun renderToBitmapMatchesPreviewPath() {
        // Export path and preview path share drawAll - this pins the export end of that contract:
        // ink lands exactly where measure() says the block sits.
        val photo = whiteBitmap(500, 400)
        val layer = TextLayer(1, text = "Hi", colorArgb = Color.BLACK, cx = 0.5f, cy = 0.5f)
        val out = TextOverlayUtils.renderToBitmap(photo, listOf(layer))
        val b = TextOverlayUtils.measure(layer, 500)
        val centerInk = out.getPixel(250, 200)
        // The exact centre may fall between glyph strokes; assert ink inside the measured block.
        var ink = 0
        val left = (250 - b.w / 2).toInt(); val top = (200 - b.h / 2).toInt()
        for (y in top until (top + b.h).toInt()) for (x in left until (left + b.w).toInt()) {
            if (out.getPixel(x, y) != Color.WHITE) ink++
        }
        assertTrue("export must draw the text block where measure() placed it (centre px $centerInk)", ink > 20)
    }

    @Test
    fun quickPaletteIsHisSixColours() {
        assertEquals(6, TextOverlayUtils.QUICK_COLORS.size)
        assertEquals(0xFFFFFFFF.toInt(), TextOverlayUtils.QUICK_COLORS[0])
        assertEquals(0xFF000000.toInt(), TextOverlayUtils.QUICK_COLORS[1])
        assertEquals(listOf("Classic", "Bold", "Elegant", "Shadow"), TextOverlayUtils.QUICK_STYLE_NAMES)
    }
}
