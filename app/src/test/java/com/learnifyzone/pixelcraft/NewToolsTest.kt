package com.learnifyzone.pixelcraft

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
}
