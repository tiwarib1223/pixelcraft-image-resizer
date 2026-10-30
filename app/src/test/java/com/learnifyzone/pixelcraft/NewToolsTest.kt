package com.learnifyzone.pixelcraft

import com.learnifyzone.pixelcraft.image.BackgroundRemoval
import org.junit.Assert.assertTrue

import android.graphics.Bitmap
import com.learnifyzone.pixelcraft.image.Transforms
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** v0.16.9 New Tools: the 2-9 photo collage grid lays every photo into its own cell. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NewToolsTest {

    private fun solid(w: Int, h: Int, c: Int): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        b.eraseColor(c)
        return b
    }

    @Test
    fun collageGridFivePhotosFillsFiveCells() {
        val colors = listOf(0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFFFFFF00.toInt(), 0xFFFF00FF.toInt())
        val photos = colors.map { solid(100, 100, it) }
        val out = Transforms.collageGrid(photos, 300, 300, 0, 0xFFFFFFFF.toInt())
        // 2 cols x 3 rows: cell w=150 h=100
        val centers = listOf(75 to 50, 225 to 50, 75 to 150, 225 to 150, 75 to 250)
        centers.forEachIndexed { i, (x, y) ->
            assertEquals("cell $i shows photo $i", colors[i], out.getPixel(x, y))
        }
        assertEquals("unused 6th cell stays background", 0xFFFFFFFF.toInt(), out.getPixel(225, 250))
    }

    @Test
    fun collageGridNinePhotosAllCells() {
        val colors = (0 until 9).map { 0xFF101010.toInt() + it * 0x000F0F00 }
        val photos = colors.map { solid(60, 60, it) }
        val out = Transforms.collageGrid(photos, 300, 300, 0, 0xFFFFFFFF.toInt())
        for (i in 0 until 9) {
            val cx = (i % 3) * 100 + 50
            val cy = (i / 3) * 100 + 50
            assertEquals("cell $i shows photo $i", colors[i], out.getPixel(cx, cy))
        }
    }

    @Test
    fun removeSolidBackground_logoOnWhite() {
        // v0.16.10 (his "not working" report): a logo card on a plain white background must lose
        // the background even when the subject engine keeps the whole card.
        val w = 120; val h = 80
        val px = IntArray(w * h) { 0xFFFFFFFF.toInt() }
        for (y in 25..54) for (x in 40..79) px[y * w + x] = 0xFF2266CC.toInt()   // blue logo block
        val src = Bitmap.createBitmap(px, w, h, Bitmap.Config.ARGB_8888)
        val (cut, removed) = BackgroundRemoval.removeSolidBackground(src)
        assertTrue("expected the white field to be removed, removed=" + removed, removed > w * h / 2)
        assertEquals(0, (cut.getPixel(2, 2) ushr 24))                 // corner now transparent
        assertEquals(255, (cut.getPixel(60, 40) ushr 24))             // logo kept
        assertEquals(0xFF2266CC.toInt(), cut.getPixel(60, 40))
    }

    @Test
    fun removeSolidBackground_busyPhotoKeepsEverything() {
        // A photo-like image (every pixel a different colour) has no plain background: the flood
        // remover must NOT eat it (the subject engine's result stands).
        val w = 60; val h = 40
        var seed = 7
        val px = IntArray(w * h) {
            seed = (seed * 1103515245 + 12345) and 0x7FFFFFFF
            0xFF000000.toInt() or (seed and 0xFFFFFF)
        }
        val src = Bitmap.createBitmap(px, w, h, Bitmap.Config.ARGB_8888)
        val (_, removed) = BackgroundRemoval.removeSolidBackground(src)
        assertTrue("expected almost nothing removed, removed=" + removed, removed < w * h / 20)
    }

    @Test
    fun transparentFraction_counts() {
        val px = IntArray(100) { 0xFFFFFFFF.toInt() }
        for (i in 0..24) px[i] = 0x00FFFFFF
        val b = Bitmap.createBitmap(px, 10, 10, Bitmap.Config.ARGB_8888)
        val f = BackgroundRemoval.transparentFraction(b)
        assertTrue("fraction=" + f, Math.abs(f - 0.25f) < 0.001f)
    }
}
