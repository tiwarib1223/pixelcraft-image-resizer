package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.CropMath
import com.learnifyzone.pixelcraft.image.SocialSizes
import com.learnifyzone.pixelcraft.image.Transforms
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Pixel-level checks for the Social Media Sizes export bakes. Same lesson as the
 *  Circle Cutter: verify the real output bitmap on native graphics, not just that
 *  the UI renders. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SocialSizesTest {

    /** 200x100 bitmap, left half red, right half blue. */
    private fun halfHalf(): Bitmap {
        val bmp = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.RED)
        c.clipRect(100, 0, 200, 100)
        c.drawColor(Color.BLUE)
        return bmp
    }

    @Test
    fun maxRectForRatioCentersSquareOnLandscape() {
        val r = CropMath.maxRectForRatio(1f, 200, 100)
        assertEquals(0.25f, r[0], 0.001f)
        assertEquals(0f, r[1], 0.001f)
        assertEquals(0.75f, r[2], 0.001f)
        assertEquals(1f, r[3], 0.001f)
    }

    @Test
    fun maxRectForRatioCentersWideOnSquare() {
        val r = CropMath.maxRectForRatio(2f, 100, 100)
        assertEquals(0f, r[0], 0.001f)
        assertEquals(0.25f, r[1], 0.001f)
        assertEquals(1f, r[2], 0.001f)
        assertEquals(0.75f, r[3], 0.001f)
    }

    @Test
    fun socialCropExportsExactTargetPixels() {
        val src = halfHalf()
        val rect = CropMath.toPixels(floatArrayOf(0f, 0f, 0.5f, 1f), 200, 100) // left half = red
        val out = Transforms.socialCrop(src, rect, 50, 50)
        assertEquals(50, out.width)
        assertEquals(50, out.height)
        assertEquals(255, Color.alpha(out.getPixel(25, 25)))
        assertEquals(255, Color.red(out.getPixel(25, 25)))
        assertEquals(0, Color.blue(out.getPixel(25, 25)))
    }

    @Test
    fun socialPositionCoversWholeFrameAndPans() {
        val src = halfHalf()
        val left = Transforms.socialPosition(src, 100, 100, 1f, -1f, 0f)
        assertEquals(255, Color.alpha(left.getPixel(2, 2)))   // no empty corners
        assertEquals(255, Color.alpha(left.getPixel(97, 97)))
        assertEquals(255, Color.red(left.getPixel(50, 50)))   // pan left shows the red half
        assertEquals(0, Color.blue(left.getPixel(50, 50)))
        val right = Transforms.socialPosition(src, 100, 100, 1f, 1f, 0f)
        assertEquals(255, Color.blue(right.getPixel(50, 50))) // pan right shows the blue half
        assertEquals(0, Color.red(right.getPixel(50, 50)))
    }

    @Test
    fun socialFitWhiteFillsLetterboxWithWhite() {
        val out = Transforms.socialFit(halfHalf(), 100, 100, Transforms.SocialFill.WHITE, Color.WHITE)
        assertEquals(100, out.width)
        assertEquals(100, out.height)
        val corner = out.getPixel(2, 2)
        assertEquals(255, Color.alpha(corner))
        assertEquals(255, Color.red(corner))
        assertEquals(255, Color.green(corner))
        assertEquals(255, Color.blue(corner))
        val center = out.getPixel(25, 50) // photo area, left (red) half
        assertEquals(255, Color.red(center))
        assertEquals(0, Color.blue(center))
    }

    @Test
    fun socialFitTransparentLeavesCornerClear() {
        val out = Transforms.socialFit(halfHalf(), 100, 100, Transforms.SocialFill.TRANSPARENT, 0)
        assertEquals(0, Color.alpha(out.getPixel(2, 2)))
        assertEquals(255, Color.alpha(out.getPixel(50, 50)))
    }

    @Test
    fun socialFitBlurCornerIsOpaque() {
        val out = Transforms.socialFit(halfHalf(), 100, 100, Transforms.SocialFill.BLUR, 0)
        assertEquals(255, Color.alpha(out.getPixel(2, 2)))
    }

    @Test
    fun socialPositionReachesFarCornersWhenZoomed() {
        // Quadrants: TL red, TR blue, BL green, BR yellow
        val src = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
        val c = Canvas(src)
        val p = android.graphics.Paint()
        p.color = Color.RED; c.drawRect(0f, 0f, 100f, 50f, p)
        p.color = Color.BLUE; c.drawRect(100f, 0f, 200f, 50f, p)
        p.color = Color.GREEN; c.drawRect(0f, 50f, 100f, 100f, p)
        p.color = Color.YELLOW; c.drawRect(100f, 50f, 200f, 100f, p)
        // zoom 2 on a 50x50 target: pan must reach ALL four corners
        val tl = Transforms.socialPosition(src, 50, 50, 2f, -1f, -1f)
        assertEquals(255, Color.red(tl.getPixel(25, 25))); assertEquals(0, Color.green(tl.getPixel(25, 25))); assertEquals(0, Color.blue(tl.getPixel(25, 25)))
        val tr = Transforms.socialPosition(src, 50, 50, 2f, 1f, -1f)
        assertEquals(0, Color.red(tr.getPixel(25, 25))); assertEquals(255, Color.blue(tr.getPixel(25, 25)))
        val bl = Transforms.socialPosition(src, 50, 50, 2f, -1f, 1f)
        assertEquals(255, Color.green(bl.getPixel(25, 25))); assertEquals(0, Color.blue(bl.getPixel(25, 25)))
        val br = Transforms.socialPosition(src, 50, 50, 2f, 1f, 1f)
        assertEquals(255, Color.red(br.getPixel(25, 25))); assertEquals(255, Color.green(br.getPixel(25, 25)))
    }

    @Test
    fun officialTableHasEightPlatformsAndKeySizes() {
        assertEquals(8, SocialSizes.platforms.size)
        val ig = SocialSizes.platforms.first { it.name == "Instagram" }
        assertTrue(ig.categories.flatMap { it.sizes }.any { it.w == 1080 && it.h == 1920 })
        val yt = SocialSizes.platforms.first { it.name == "YouTube" }
        assertTrue(yt.categories.flatMap { it.sizes }.any { it.w == 2560 && it.h == 1440 && it.note.contains("1546") })
        val li = SocialSizes.platforms.first { it.name == "LinkedIn" }
        assertTrue(li.categories.flatMap { it.sizes }.any { it.w == 1584 && it.h == 396 })
        val wa = SocialSizes.platforms.first { it.name == "WhatsApp" }
        assertTrue(wa.categories.flatMap { it.sizes }.any { it.w == 1080 && it.h == 1920 })
    }
}
