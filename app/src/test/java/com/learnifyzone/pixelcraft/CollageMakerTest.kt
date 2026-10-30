package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import com.learnifyzone.pixelcraft.image.CollageMaker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs
import kotlin.math.min

/**
 * v0.17.0 Collage Maker engine tests (his spec sections 3, 5, 6, 9): layout counts + bounds,
 * and rendered pixels proving gap/background/radius/border/zoom-cover/gradient/transparency.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CollageMakerTest {

    private fun solid(w: Int, h: Int, argb: Int): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        b.eraseColor(argb)
        return b
    }

    private fun close(c1: Int, c2: Int, tol: Int = 12): Boolean {
        val dr = abs(((c1 shr 16) and 0xFF) - ((c2 shr 16) and 0xFF))
        val dg = abs(((c1 shr 8) and 0xFF) - ((c2 shr 8) and 0xFF))
        val db = abs((c1 and 0xFF) - (c2 and 0xFF))
        return dr <= tol && dg <= tol && db <= tol
    }

    // ---------- Layout library ----------

    @Test
    fun sections_namedCountsAndBounds() {
        for (n in 2..20) {
            for (f in CollageMaker.LayoutFamily.values()) {
                val s = CollageMaker.sections(f, n, 1080, 1080)
                assertEquals(f.name + " count " + n, n, s.size)
                for (sec in s) {
                    assertTrue("left " + sec.left, sec.left >= 0f && sec.left < sec.right)
                    assertTrue("top " + sec.top, sec.top >= 0f && sec.top < sec.bottom)
                    assertTrue("right " + sec.right, sec.right <= 1.0001f)
                    assertTrue("bottom " + sec.bottom, sec.bottom <= 1.0001f)
                }
            }
        }
    }

    @Test
    fun sections_gridFourQuarters() {
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.GRID, 4, 1080, 1080)
        assertEquals(4, s.size)
        assertTrue(abs(s[0].left) < 0.001f && abs(s[0].right - 0.5f) < 0.001f)
        assertTrue(abs(s[3].left - 0.5f) < 0.001f && abs(s[3].bottom - 1f) < 0.001f)
    }

    @Test
    fun sections_verticalStackAndHorizontalRow() {
        val v = CollageMaker.sections(CollageMaker.LayoutFamily.VERTICAL, 3, 1080, 1920)
        assertEquals(3, v.size)
        assertTrue(abs(v[1].top - 1f / 3f) < 0.001f)
        val h = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1920, 1080)
        assertEquals(2, h.size)
        assertTrue(abs(h[1].left - 0.5f) < 0.001f)
    }

    @Test
    fun sections_mixedThreeIsBigLeft() {
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.MIXED, 3, 1080, 1080)
        assertEquals(3, s.size)
        assertTrue("big left section", s[0].width() > 0.6f && s[0].height() > 0.95f)
        assertTrue("stacked right", abs(s[1].bottom - 0.5f) < 0.001f && abs(s[2].top - 0.5f) < 0.001f)
    }

    @Test
    fun sections_mixedTwelveFeatured() {
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.MIXED, 12, 1080, 1080)
        assertEquals(12, s.size)
        // grid is shaped for n+3 = 15 cells (4x4 on square): featured cell spans 2x2
        assertTrue("featured first cell spans", s[0].width() > 2f / 4f - 0.01f && s[0].height() > 2f / 4f - 0.01f)
    }

    // ---------- Rendering ----------

    private fun twoPhotos(): List<CollageMaker.Photo> = listOf(
        CollageMaker.Photo(solid(400, 300, 0xFFE91E63.toInt())),
        CollageMaker.Photo(solid(300, 400, 0xFF2266CC.toInt())))

    @Test
    fun render_gapShowsBackgroundAndPhotosFill() {
        val style = CollageMaker.Style(gapPx = 40, bgColor = 0xFFFFFFFF.toInt())
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1000, 500)
        val out = CollageMaker.render(twoPhotos(), s, 1000, 500, style)
        // The gap column in the middle is pure background.
        assertTrue("gap is white", close(out.getPixel(500, 250), 0xFFFFFFFF.toInt()))
        // Well inside each section: the photo's own colour (proportions kept via centre-crop).
        assertTrue("left photo", close(out.getPixel(200, 250), 0xFFE91E63.toInt()))
        assertTrue("right photo", close(out.getPixel(800, 250), 0xFF2266CC.toInt()))
    }

    @Test
    fun render_radiusRoundsCorners() {
        val style = CollageMaker.Style(gapPx = 0, radiusPx = 120, bgColor = 0xFFFFFFFF.toInt())
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1000, 500)
        val out = CollageMaker.render(twoPhotos(), s, 1000, 500, style)
        // A few px inside the left section's top-left corner: rounded away -> background.
        assertTrue("corner rounded to bg", close(out.getPixel(6, 6), 0xFFFFFFFF.toInt()))
        // The section centre stays the photo.
        assertTrue("centre is photo", close(out.getPixel(250, 250), 0xFFE91E63.toInt()))
    }

    @Test
    fun render_borderStrokesSection() {
        val style = CollageMaker.Style(gapPx = 0, borderPx = 16, borderColor = 0xFF00AA00.toInt(), bgColor = 0xFFFFFFFF.toInt())
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1000, 500)
        val out = CollageMaker.render(twoPhotos(), s, 1000, 500, style)
        // On the left edge of the left section the border colour paints.
        assertTrue("border visible", close(out.getPixel(8, 250), 0xFF00AA00.toInt(), 40))
    }

    @Test
    fun render_perPhotoBorderOverridesStyle() {
        // v0.17.1: a photo's own frame wins over the collage-wide frame (and -1 means "use style").
        val photos = listOf(
            CollageMaker.Photo(solid(400, 300, 0xFFE91E63.toInt())).apply { borderPx = 16; borderColor = 0xFF0000FF.toInt() },
            CollageMaker.Photo(solid(300, 400, 0xFF2266CC.toInt()))   // borderPx = -1 -> no style frame here
        )
        val style = CollageMaker.Style(gapPx = 0, borderPx = 0, bgColor = 0xFFFFFFFF.toInt())
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1000, 500)
        val out = CollageMaker.render(photos, s, 1000, 500, style)
        assertTrue("per-photo border paints", close(out.getPixel(8, 250), 0xFF0000FF.toInt(), 40))
        // Right photo has no frame: 2px inside its left edge is the photo, not a border.
        assertTrue("no border on the other photo", close(out.getPixel(502, 250), 0xFF2266CC.toInt()))
    }

    @Test
    fun render_marginInsetsWholeCollage() {
        // v0.17.1: margins move every section in from the canvas edge; the ring shows the background.
        val style = CollageMaker.Style(gapPx = 0, marginPx = 50, bgColor = 0xFF112233.toInt())
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1000, 500)
        val out = CollageMaker.render(twoPhotos(), s, 1000, 500, style)
        assertTrue("margin ring shows background", close(out.getPixel(25, 250), 0xFF112233.toInt()))
        assertTrue("inside margin is photo", close(out.getPixel(60, 250), 0xFFE91E63.toInt()))
    }

    @Test
    fun render_zoomKeepsCoverNoBackgroundBleed() {
        val photos = listOf(CollageMaker.Photo(solid(400, 300, 0xFFE91E63.toInt())).apply { zoom = 2.5f; panX = 1f; panY = -1f })
        val style = CollageMaker.Style(gapPx = 0, bgColor = 0xFFFFFFFF.toInt())
        val out = CollageMaker.render(photos, listOf(CollageMaker.Section(0f, 0f, 1f, 1f)), 800, 800, style)
        // Extreme pan at high zoom must still cover every sampled point inside the section.
        for (x in intArrayOf(30, 400, 770)) for (y in intArrayOf(30, 400, 770)) {
            assertTrue("covered at $x,$y", close(out.getPixel(x, y), 0xFFE91E63.toInt()))
        }
    }

    @Test
    fun render_rotationCoversCorners() {
        val photos = listOf(CollageMaker.Photo(solid(500, 300, 0xFF2266CC.toInt())).apply { rotation = 45f })
        val style = CollageMaker.Style(gapPx = 0, bgColor = 0xFFFFFFFF.toInt())
        val out = CollageMaker.render(photos, listOf(CollageMaker.Section(0f, 0f, 1f, 1f)), 600, 600, style)
        // Rotated 45 degrees the scale must grow so no corner shows background.
        for (p in intArrayOf(20, 300, 580)) for (q in intArrayOf(20, 300, 580)) {
            assertTrue("no wedge at $p,$q", close(out.getPixel(p, q), 0xFF2266CC.toInt()))
        }
    }

    @Test
    fun render_transparentBackgroundKeepsAlpha() {
        val style = CollageMaker.Style(gapPx = 60, bgColor = 0)
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1000, 500)
        val out = CollageMaker.render(twoPhotos(), s, 1000, 500, style)
        assertEquals("gap keeps alpha 0", 0, (out.getPixel(500, 250) ushr 24))
        assertEquals("photo fully opaque", 255, (out.getPixel(200, 250) ushr 24))
    }

    @Test
    fun render_gradientBackground() {
        val style = CollageMaker.Style(gapPx = 60, bgGradient = intArrayOf(0xFFFF0000.toInt(), 0xFF0000FF.toInt()))
        val s = CollageMaker.sections(CollageMaker.LayoutFamily.HORIZONTAL, 2, 1000, 500)
        val out = CollageMaker.render(twoPhotos(), s, 1000, 500, style)
        val top = out.getPixel(500, 20)
        val bottom = out.getPixel(500, 480)
        assertTrue("top is reddish", ((top shr 16) and 0xFF) > 150)
        assertTrue("bottom is bluish", (bottom and 0xFF) > 150)
    }

    @Test
    fun render_flipMirrorsPixels() {
        // Left half red, right half blue: flipped horizontally the colours swap sides.
        val b = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
        val px = IntArray(200 * 100) { i -> if ((i % 200) < 100) 0xFFFF0000.toInt() else 0xFF0000FF.toInt() }
        b.setPixels(px, 0, 200, 0, 0, 200, 100)
        val style = CollageMaker.Style(gapPx = 0, bgColor = 0xFFFFFFFF.toInt())
        val out = CollageMaker.render(listOf(CollageMaker.Photo(b).apply { flipH = true }), listOf(CollageMaker.Section(0f, 0f, 1f, 1f)), 400, 200, style)
        assertTrue("left is now blue", close(out.getPixel(60, 100), 0xFF0000FF.toInt()))
        assertTrue("right is now red", close(out.getPixel(340, 100), 0xFFFF0000.toInt()))
    }
}
