package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.ShopSizes
import com.learnifyzone.pixelcraft.image.Transforms
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Pixel-level checks for the Online Shop Sizes collage + table. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ShopSizesTest {

    private fun solid(color: Int): Bitmap {
        val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(color)
        return bmp
    }

    @Test
    fun collageTwoPhotosSideBySideOnWideCanvas() {
        val out = Transforms.collage(listOf(solid(Color.RED), solid(Color.BLUE)), 200, 100, 0, Color.WHITE)
        assertEquals(200, out.width)
        assertEquals(100, out.height)
        val left = out.getPixel(50, 50)
        assertEquals(255, Color.red(left)); assertEquals(0, Color.blue(left))
        val right = out.getPixel(150, 50)
        assertEquals(0, Color.red(right)); assertEquals(255, Color.blue(right))
    }

    @Test
    fun collageTwoPhotosStackedOnTallCanvas() {
        val out = Transforms.collage(listOf(solid(Color.RED), solid(Color.BLUE)), 100, 200, 0, Color.WHITE)
        val top = out.getPixel(50, 50)
        assertEquals(255, Color.red(top)); assertEquals(0, Color.blue(top))
        val bottom = out.getPixel(50, 150)
        assertEquals(0, Color.red(bottom)); assertEquals(255, Color.blue(bottom))
    }

    @Test
    fun collageFourPhotosQuadrants() {
        val out = Transforms.collage(
            listOf(solid(Color.RED), solid(Color.BLUE), solid(Color.GREEN), solid(Color.YELLOW)),
            100, 100, 0, Color.WHITE)
        assertEquals(255, Color.red(out.getPixel(25, 25)))      // TL red
        assertEquals(255, Color.blue(out.getPixel(75, 25)))     // TR blue
        assertEquals(255, Color.green(out.getPixel(25, 75)))    // BL green
        val br = out.getPixel(75, 75)
        assertEquals(255, Color.red(br)); assertEquals(255, Color.green(br)) // BR yellow
    }

    @Test
    fun collageGapShowsBackground() {
        val out = Transforms.collage(listOf(solid(Color.RED), solid(Color.BLUE)), 200, 100, 10, Color.WHITE)
        val gapPixel = out.getPixel(100, 50)
        assertEquals(255, Color.red(gapPixel)); assertEquals(255, Color.green(gapPixel)); assertEquals(255, Color.blue(gapPixel))
    }

    @Test
    fun shopTableHasElevenPlatformsAndKeySizes() {
        assertEquals(11, ShopSizes.platforms.size)
        assertEquals(listOf("Etsy", "Shopify", "Amazon", "eBay", "Gumroad"), ShopSizes.mainShops)
        val gum = ShopSizes.platforms.first { it.name == "Gumroad" }
        val gumSizes = gum.categories.flatMap { it.sizes }
        assertTrue(gumSizes.any { it.w == 1280 && it.h == 720 })
        assertTrue(gumSizes.any { it.w == 600 && it.h == 600 })
        val etsy = ShopSizes.platforms.first { it.name == "Etsy" }
        assertTrue(etsy.categories.flatMap { it.sizes }.any { it.w == 1600 && it.h == 400 })
        val amz = ShopSizes.platforms.first { it.name == "Amazon" }
        assertTrue(amz.categories.flatMap { it.sizes }.any { it.w == 3000 && it.h == 600 })
        val wix = ShopSizes.platforms.first { it.name == "Wix Stores" }
        assertTrue(wix.categories.flatMap { it.sizes }.any { it.w == 3000 && it.h == 3000 })
        // every platform offers the collage category
        ShopSizes.platforms.forEach { p ->
            assertTrue(p.name + " missing collage", p.categories.any { it.label == "Product Collage" })
        }
    }
}
