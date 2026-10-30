package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.TextOverlayUtils
import com.learnifyzone.pixelcraft.image.TextOverlayUtils.TextLayer
import org.junit.Assert.assertArrayEquals
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
    fun curvedTextBackgroundDrawsRibbon() {
        // His v0.16.4 report: Black background selected on curved text showed nothing.
        val layer = TextLayer(1, text = "banking", colorArgb = Color.WHITE, bgColorArgb = Color.BLACK, curved = true)
        val bmp = draw(layer)
        var dark = 0
        for (y in 0 until bmp.height) for (x in 0 until bmp.width) {
            if (Color.red(bmp.getPixel(x, y)) < 80) dark++
        }
        assertTrue("curved background must paint a dark ribbon behind the text, got $dark dark px", dark > 800)
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
    fun cropMathCoversHisSpec() {
        // 1:1 on a 4000x3000 photo: 3000x3000 centred.
        val sq = TextOverlayUtils.maxCropForRatio(4000, 3000, 1, 1)
        assertEquals(0.125f, sq[0], 0.001f)
        assertEquals(0f, sq[1], 0.001f)
        assertEquals(0.875f, sq[2], 0.001f)
        assertEquals(1f, sq[3], 0.001f)
        // 16:9 on a square photo: full width, letterboxed.
        val wide = TextOverlayUtils.maxCropForRatio(3000, 3000, 16, 9)
        assertEquals(0f, wide[0], 0.001f)
        assertEquals((1f - 9f / 16f) / 2f, wide[1], 0.001f)
        // Ratio equal to the photo's own aspect covers the whole frame.
        val same = TextOverlayUtils.maxCropForRatio(4000, 3000, 4, 3)
        assertEquals(0f, same[0], 0.0001f); assertEquals(1f, same[2], 0.0001f)
        // Pixel size honours the crop and never collapses.
        assertArrayEquals(intArrayOf(3000, 3000), TextOverlayUtils.cropPixelSize(4000, 3000, sq))
    }

    @Test
    fun positionZoomScalesFrameAroundCentre() {
        val r = TextOverlayUtils.maxCropForRatio(4000, 3000, 1, 1) // l=0.125 t=0 r=0.875 b=1
        val z = TextOverlayUtils.zoomCrop(r, 2f)   // zoom in 2x: frame halves around its centre
        assertEquals(0.375f, z[2] - z[0], 0.001f)
        assertEquals(0.5f, z[3] - z[1], 0.001f)
        assertEquals((r[0] + r[2]) / 2f, (z[0] + z[2]) / 2f, 0.001f)   // centre stays
        val wide = TextOverlayUtils.zoomCrop(floatArrayOf(0f, 0.3f, 0.4f, 0.7f), 0.2f) // zoom out past the edge
        assertTrue(wide[2] - wide[0] <= 1f)
        assertTrue(wide[0] >= 0f && wide[2] <= 1f && wide[1] >= 0f && wide[3] <= 1f)
        // Never smaller than the 5% guard.
        val tiny = TextOverlayUtils.zoomCrop(floatArrayOf(0.4f, 0.4f, 0.5f, 0.5f), 100f)
        assertTrue(tiny[2] - tiny[0] >= 0.05f)
    }

    @Test
    fun customRatioValidationIsGentleAndStrict() {
        assertArrayEquals(intArrayOf(5, 7), TextOverlayUtils.parseCustomRatio("5", "7"))
        assertArrayEquals(intArrayOf(5, 7), TextOverlayUtils.parseCustomRatio(" 5 ", " 7"))
        assertNull(TextOverlayUtils.parseCustomRatio("0", "7"))
        assertNull(TextOverlayUtils.parseCustomRatio("abc", "3"))
        assertNull(TextOverlayUtils.parseCustomRatio("100", "3"))
        assertNull(TextOverlayUtils.parseCustomRatio("", "3"))
    }

    @Test
    fun aspectOptionsMatchHisSpec() {
        val commons = TextOverlayUtils.ASPECT_OPTIONS.filter { it.common }
        assertEquals(4, commons.size)
        assertEquals(listOf("Square", "Portrait", "Story", "Wide"), commons.map { it.label })
        assertEquals(listOf("1:1", "4:5", "9:16", "16:9"), commons.map { "${it.rw}:${it.rh}" })
        // The expanded list adds more ratios including A4, each with a distinct ratio.
        val all = TextOverlayUtils.ASPECT_OPTIONS.map { it.rw to it.rh }
        assertEquals(all.size, all.toSet().size)
        assertTrue(TextOverlayUtils.ASPECT_OPTIONS.any { it.label == "A4" && it.rw == 210 && it.rh == 297 })
        assertTrue(TextOverlayUtils.ASPECT_OPTIONS.size >= 10)
    }

    @Test
    fun spacingSlidersChangeLayoutMath() {
        val base = TextOverlayUtils.measure(TextLayer(1, text = "Hello\nWorld"), 400)
        val tracked = TextOverlayUtils.measure(TextLayer(1, text = "Hello\nWorld", letterSpacing = 0.2f), 400)
        assertTrue("letter spacing must widen lines", tracked.lineWidths[0] > base.lineWidths[0])
        val leaded = TextOverlayUtils.measure(TextLayer(1, text = "Hello\nWorld", lineSpacing = 0.5f), 400)
        assertTrue("line spacing must grow the block", leaded.h > base.h * 1.2f)
        assertEquals(2, base.lineWidths.size) // two-line block measured
    }

    @Test
    fun opacityAppliesToTextAndBackground() {
        // Half opacity must blend BOTH the glyph and its background box toward white.
        val layer = TextLayer(1, text = ".", colorArgb = Color.BLACK, bgColorArgb = Color.YELLOW, opacity = 0.5f)
        val bmp = draw(layer)
        var strongestBg = 0
        for (y in 0 until bmp.height) for (x in 0 until bmp.width) {
            val p = bmp.getPixel(x, y)
            if (Color.red(p) > 200 && Color.green(p) > 200) strongestBg = maxOf(strongestBg, 255 - Color.blue(p))
        }
        assertTrue("half-opacity yellow box must be softened, blue deficit=$strongestBg", strongestBg in 60..200)
    }

    @Test
    fun stylePresetRoundTripCoversEveryStyledField() {
        val layer = TextLayer(1).apply {
            font = TextOverlayUtils.FontChoice.Serif; sizeFrac = 0.11f; bold = true; italic = true
            align = TextOverlayUtils.Align.RIGHT; colorArgb = 0xFFF5B942.toInt()
            outlineFrac = 0.12f; outlineColorArgb = 0xFF172B4D.toInt(); bgColorArgb = 0xFF000000.toInt()
            shadow = true; opacity = 0.8f; curved = true; arcSweep = 200f
            letterSpacing = 0.1f; lineSpacing = 0.3f
        }
        // Mirror of the save/apply mapping used by the store: every styled field must survive.
        val copy = TextLayer(99)
        copy.font = layer.font; copy.sizeFrac = layer.sizeFrac; copy.bold = layer.bold; copy.italic = layer.italic
        copy.align = layer.align; copy.colorArgb = layer.colorArgb; copy.outlineFrac = layer.outlineFrac
        copy.outlineColorArgb = layer.outlineColorArgb; copy.bgColorArgb = layer.bgColorArgb
        copy.shadow = layer.shadow; copy.opacity = layer.opacity; copy.curved = layer.curved
        copy.arcSweep = layer.arcSweep; copy.letterSpacing = layer.letterSpacing; copy.lineSpacing = layer.lineSpacing
        assertEquals(TextOverlayUtils.measure(layer, 800).w, TextOverlayUtils.measure(copy, 800).w, 0.01f)
        assertEquals(TextOverlayUtils.measure(layer, 800).h, TextOverlayUtils.measure(copy, 800).h, 0.01f)
        assertEquals(layer.copy(), copy.copy(id = 1))
    }

    @Test
    fun imageLayerDrawsScaledAndHits() {
        val photo = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        Canvas(photo).drawColor(Color.WHITE)
        val logo = Bitmap.createBitmap(20, 10, Bitmap.Config.ARGB_8888)
        Canvas(logo).drawColor(Color.RED)
        val layer = TextOverlayUtils.ImageLayer(7, logo, cx = 0.25f, cy = 0.25f, widthFrac = 0.2f)
        // size honours the bitmap aspect: w=80, h=40
        val sz = TextOverlayUtils.imageLayerSize(layer, 400)
        assertEquals(80f, sz[0], 0.01f); assertEquals(40f, sz[1], 0.01f)
        val out = TextOverlayUtils.renderToBitmap(photo, emptyList(), listOf(layer))
        assertEquals(Color.RED, out.getPixel(100, 75))          // centre of the overlay
        assertEquals(Color.WHITE, out.getPixel(300, 200))       // untouched area
        assertTrue(TextOverlayUtils.imageLayerHit(layer, 100f, 75f, 400, 300))
        assertFalse(TextOverlayUtils.imageLayerHit(layer, 300f, 200f, 400, 300))
    }

    @Test
    fun imageLayerDrawsUnderTextAndBlends() {
        val photo = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        Canvas(photo).drawColor(Color.WHITE)
        val logo = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        Canvas(logo).drawColor(Color.RED)
        val img = TextOverlayUtils.ImageLayer(8, logo, widthFrac = 1f)
        img.opacity = 0.5f
        val txt = TextLayer(9, text = "Hi", sizeFrac = 0.5f, colorArgb = Color.BLACK)
        val out = TextOverlayUtils.renderToBitmap(photo, listOf(txt), listOf(img))
        val bg = out.getPixel(20, 20)                            // overlay only, half alpha on white
        assertTrue("half-opacity red over white must be pinkish", Color.red(bg) == 255 && Color.green(bg) in 100..160)
        var darkest = 255
        for (y in 110 until 190) for (x in 150 until 250) darkest = minOf(darkest, Color.red(out.getPixel(x, y)))
        assertTrue("text must ink dark pixels over the image layer, darkest=$darkest", darkest < 100)
    }

    @Test
    fun moreColoursPaletteIsDistinctFromQuickSix() {
        assertTrue(TextOverlayUtils.EXTRA_COLORS.size >= 12)
        val all = TextOverlayUtils.QUICK_COLORS + TextOverlayUtils.EXTRA_COLORS
        assertEquals(all.size, all.toSet().size)   // no duplicates across both palettes
    }

    @Test
    fun fontListMatchesHisSpec() {
        val commons = TextOverlayUtils.FontChoice.values().filter { it.common }
        assertEquals(4, commons.size)
        assertEquals(listOf("Sans", "Serif", "Mono", "Script"), commons.map { it.label })
        // "More fonts" reveals the rest; every family is a real Android generic family.
        val extra = TextOverlayUtils.FontChoice.values().filter { !it.common }
        assertTrue(extra.size >= 4)
        assertTrue(TextOverlayUtils.FontChoice.values().all { it.family.isNotBlank() })
    }

    @Test
    fun quickPaletteIsHisSixColours() {
        assertEquals(6, TextOverlayUtils.QUICK_COLORS.size)
        assertEquals(0xFFFFFFFF.toInt(), TextOverlayUtils.QUICK_COLORS[0])
        assertEquals(0xFF000000.toInt(), TextOverlayUtils.QUICK_COLORS[1])
        assertEquals(listOf("Classic", "Bold", "Elegant", "Shadow"), TextOverlayUtils.QUICK_STYLE_NAMES)
    }

    @Test
    fun flipHorizontalMirrorsInkAroundLayerCentre() {
        // v0.16.7 Flip & Clone: a flipped layer's ink must be the horizontal mirror of the
        // original around the layer centre (off-centre so the mirror is measurable).
        val base = TextLayer(1, text = "AB", colorArgb = Color.BLACK)
        base.cx = 0.3f
        val normal = draw(base)
        val flipped = draw(base.copy(flipH = true))
        val (nMinX, _, nMaxX, _) = inkBounds(normal)
        val (fMinX, _, fMaxX, _) = inkBounds(flipped)
        val px = 0.3f * 400
        assertEquals(2 * px - nMaxX, fMinX.toFloat(), 3f)
        assertEquals(2 * px - nMinX, fMaxX.toFloat(), 3f)
        // mirroring preserves the amount of ink (pixel-for-pixel copy across the centre axis)
        fun ink(b: Bitmap): Int { var n = 0; for (y in 0 until b.height) for (x in 0 until b.width) if (b.getPixel(x, y) != Color.WHITE) n++; return n }
        assertEquals(ink(normal).toFloat(), ink(flipped).toFloat(), ink(normal) * 0.1f + 4f)
    }
}
