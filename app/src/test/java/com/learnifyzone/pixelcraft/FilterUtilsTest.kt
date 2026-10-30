package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.FilterUtils
import org.junit.Assert.assertEquals
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
class FilterUtilsTest {

    private fun solid(color: Int): Bitmap =
        Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888).also { it.eraseColor(color) }

    @Test
    fun normalIsIdentity() {
        val out = FilterUtils.applyFilter(solid(Color.rgb(120, 80, 40)), "normal")
        assertEquals(Color.rgb(120, 80, 40), out.getPixel(4, 4))
        assertNull(FilterUtils.filterPaint("normal"))
    }

    @Test
    fun monoDesaturates() {
        val out = FilterUtils.applyFilter(solid(Color.rgb(200, 60, 20)), "mono")
        val px = out.getPixel(4, 4)
        assertEquals(Color.red(px), Color.green(px)); assertEquals(Color.green(px), Color.blue(px))
    }

    @Test
    fun everyFilterChangesPixels() {
        val src = solid(Color.rgb(120, 80, 40))
        FilterUtils.FilterChoice.values().filter { it.id != "normal" }.forEach { fc ->
            val px = FilterUtils.applyFilter(src, fc.id).getPixel(4, 4)
            assertNotEquals("${fc.id} must change the photo", Color.rgb(120, 80, 40), px)
        }
    }
}
