package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Pixel-level checks for the Circle Cutter export mask (his v0.10.0 report:
 *  "it should crop in selected area. Now it not crop"). Uses native graphics so
 *  Canvas/Path/PorterDuff run for real, exactly like on his phone. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ShapeCutoutTest {

    private fun cutout(shape: String): Bitmap {
        val src = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        src.eraseColor(Color.RED)
        val m = Class.forName("com.learnifyzone.pixelcraft.MainActivityKt")
            .getDeclaredMethod("shapeCutout", Bitmap::class.java, FloatArray::class.java, String::class.java, Int::class.javaPrimitiveType)
        m.isAccessible = true
        return m.invoke(null, src, floatArrayOf(0f, 0f, 1f, 1f), shape, 100) as Bitmap
    }

    @Test
    fun circleMasksCornersToTransparent() {
        val out = cutout("Circle")
        assertEquals(100, out.width)
        assertEquals(0, Color.alpha(out.getPixel(2, 2)))       // corner outside circle
        assertEquals(0, Color.alpha(out.getPixel(97, 97)))     // opposite corner
        assertEquals(255, Color.alpha(out.getPixel(50, 50)))   // centre
        assertEquals(255, Color.alpha(out.getPixel(50, 3)))    // top edge midpoint inside circle
    }

    @Test
    fun heartMasksTopNotchAndCorners() {
        val out = cutout("Heart")
        assertEquals(0, Color.alpha(out.getPixel(50, 3)))      // top notch between lobes
        assertEquals(0, Color.alpha(out.getPixel(2, 2)))       // corner
        assertEquals(255, Color.alpha(out.getPixel(50, 60)))   // body
        assertEquals(255, Color.alpha(out.getPixel(50, 85)))   // bottom point
    }

    @Test
    fun roundedMasksCornersOnly() {
        val out = cutout("Rounded")
        assertEquals(0, Color.alpha(out.getPixel(2, 2)))       // rounded corner
        assertEquals(255, Color.alpha(out.getPixel(50, 2)))    // edge midpoint kept
        assertEquals(255, Color.alpha(out.getPixel(50, 50)))
    }

    @Test
    fun squareKeepsFullFrame() {
        val out = cutout("Square")
        assertEquals(255, Color.alpha(out.getPixel(2, 2)))
        assertEquals(Color.RED, out.getPixel(50, 50) or (0xFF shl 24))
    }
}
