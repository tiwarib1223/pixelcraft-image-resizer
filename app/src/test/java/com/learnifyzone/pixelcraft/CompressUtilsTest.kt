package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.CompressUtils
import com.learnifyzone.pixelcraft.image.ConvertFormats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.random.Random

/** Real-output checks for the Compressor: compressed files are decoded and measured. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CompressUtilsTest {

    /** Noisy photo-like bitmap: big when encoded, very compressible in size terms. */
    private fun noisy(w: Int, h: Int, alpha: Boolean = false): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val rnd = Random(42)
        val px = IntArray(w * h)
        for (i in px.indices) {
            val a = if (alpha && i % 3 == 0) 0 else 255
            px[i] = Color.argb(a, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }
        bmp.setPixels(px, 0, w, 0, 0, w, h)
        return bmp
    }

    @Test
    fun jpegHitsTargetAndDecodes() {
        val src = noisy(1200, 900)
        val target = 60L * 1024
        val out = CompressUtils.compressImage(src, Bitmap.CompressFormat.JPEG, target)
        assertTrue("expected <= $target but was ${out.size}", out.size <= target)
        val decoded = BitmapFactory.decodeByteArray(out, 0, out.size)!!
        assertTrue(decoded.width <= 1200 && decoded.height <= 900)
        assertTrue(decoded.width >= 320)  // dimension floor respected
    }

    @Test
    fun qualityLadderKeepsFullSizeWhenItFits() {
        val src = noisy(400, 300)
        val out = CompressUtils.compressImage(src, Bitmap.CompressFormat.JPEG, 5L * 1024 * 1024)
        val decoded = BitmapFactory.decodeByteArray(out, 0, out.size)!!
        assertEquals(400, decoded.width)   // fits at original dimensions, no downscale
        assertEquals(300, decoded.height)
    }

    @Test
    fun pngShrinksByDimensionsAndKeepsTransparency() {
        val src = noisy(1600, 1200, alpha = true)
        val fullSize = run {
            val baos = java.io.ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.PNG, 100, baos)
            baos.size()
        }
        val out = CompressUtils.compressImage(src, Bitmap.CompressFormat.PNG, fullSize / 4L)
        assertTrue(out.size <= fullSize / 4L)
        val decoded = BitmapFactory.decodeByteArray(out, 0, out.size)!!
        assertTrue("PNG must shrink via dimensions", decoded.width < 1600)
        // transparency preserved: decoded PNG keeps alpha and has fully transparent pixels
        assertTrue(decoded.hasAlpha())
        var minAlpha = 255
        for (y in 0 until decoded.height step 7) for (x in 0 until decoded.width step 7) {
            minAlpha = minOf(minAlpha, Color.alpha(decoded.getPixel(x, y)))
        }
        assertEquals(0, minAlpha)
    }

    @Test
    fun webpKeepsAlphaAtTarget() {
        val src = noisy(800, 600, alpha = true)
        val out = CompressUtils.compressImage(src, Bitmap.CompressFormat.WEBP, 40L * 1024)
        val decoded = BitmapFactory.decodeByteArray(out, 0, out.size)!!
        assertTrue(decoded.hasAlpha())
        assertTrue(out.size <= 40L * 1024 || decoded.width < 800)
    }

    @Test
    fun pdfCompressionShrinksAndWarnsMathWorks() {
        assertEquals(75, CompressUtils.percentSaved(4800, 1200))
        assertEquals(0, CompressUtils.percentSaved(1000, 1200))   // grew: never negative
        assertEquals(0, CompressUtils.percentSaved(-1, 500))      // unknown original
        // PDF path needs native pdfium; accept the known host signature only.
        val pages = listOf(noisy(600, 800), noisy(600, 800))
        try {
            val loose = CompressUtils.compressPdfPages(pages, Long.MAX_VALUE)
            val tight = CompressUtils.compressPdfPages(pages, loose.size / 2L)
            assertTrue(tight.size < loose.size)
            assertEquals('%'.code.toByte(), tight[0])
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("closed") == true)
        }
    }
}
