package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.Color
import com.learnifyzone.pixelcraft.image.PrintUtils
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PrintUtilsTest {

    @Test
    fun requiredPixelsMatchesHisExample() {
        // His spec example: 10 x 8 in at 300 PPI -> 3000 x 2400 px, 7.2 MP
        assertArrayEquals(intArrayOf(3000, 2400), PrintUtils.requiredPixels(10.0, 8.0, 300))
        assertEquals("7.2", PrintUtils.megapixels(3000, 2400))
    }

    @Test
    fun a4At300PpiIsStandard() {
        val a4 = PrintUtils.presets.first { it.name == "A4" }
        val px = PrintUtils.requiredPixels(a4.wIn, a4.hIn, 300)
        assertEquals(2480.0, px[0].toDouble(), 2.0)   // the well-known A4@300 numbers
        assertEquals(3508.0, px[1].toDouble(), 2.0)
    }

    @Test
    fun unitsConvertBothWays() {
        assertEquals(1.0, PrintUtils.toInches(2.54, "cm"), 0.0001)
        assertEquals(1.0, PrintUtils.toInches(25.4, "mm"), 0.0001)
        assertEquals(2.54, PrintUtils.fromInches(1.0, "cm"), 0.0001)
        assertEquals(25.4, PrintUtils.fromInches(1.0, "mm"), 0.0001)
    }

    @Test
    fun effectivePpiUsesLimitingAxis() {
        // 3000x1200 px on 10x8 in: 300 vs 150 PPI -> 150
        assertEquals(150.0, PrintUtils.effectivePpi(3000, 1200, 10.0, 8.0), 0.001)
    }

    @Test
    fun qualityThresholds() {
        assertEquals(PrintUtils.Quality.GOOD, PrintUtils.qualityLevel(300.0, 300))
        assertEquals(PrintUtils.Quality.GOOD, PrintUtils.qualityLevel(450.0, 300))
        assertEquals(PrintUtils.Quality.ACCEPTABLE, PrintUtils.qualityLevel(200.0, 300))  // 2/3 of target
        assertEquals(PrintUtils.Quality.ACCEPTABLE, PrintUtils.qualityLevel(120.0, 150))
        assertEquals(PrintUtils.Quality.LOW, PrintUtils.qualityLevel(100.0, 300))
        assertEquals(PrintUtils.Quality.LOW, PrintUtils.qualityLevel(110.0, 150))        // below absolute floor
    }

    @Test
    fun cappedPixelsKeepsRatioAndCap() {
        val capped = PrintUtils.cappedPixels(10000, 14000)
        assertTrue(capped[0].toLong() * capped[1] <= PrintUtils.MAX_EXPORT_PIXELS)
        assertEquals(10000.0 / 14000.0, capped[0].toDouble() / capped[1], 0.01)
        val small = PrintUtils.cappedPixels(3000, 2400)
        assertArrayEquals(intArrayOf(3000, 2400), small)  // untouched under the cap
    }

    @Test
    fun fitLetterboxesWithBackground() {
        val src = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.RED) }
        val out = PrintUtils.renderPrint(src, 400, 400, "Fit", Color.WHITE)
        assertEquals(Color.WHITE, out.getPixel(5, 5))          // top band is background
        assertEquals(Color.RED, out.getPixel(200, 200))        // center is the image
        assertEquals(Color.WHITE, out.getPixel(200, 395))      // bottom band
    }

    @Test
    fun fillCoversWholeCanvas() {
        val src = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.RED) }
        val out = PrintUtils.renderPrint(src, 400, 400, "Fill", Color.WHITE)
        assertEquals(Color.RED, out.getPixel(5, 5))
        assertEquals(Color.RED, out.getPixel(200, 395))
    }

    @Test
    fun stretchDistortsToCanvas() {
        // left half red, right half blue; stretched onto square -> quarters stay pure
        val src = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(src)
        c.drawRect(0f, 0f, 100f, 100f, android.graphics.Paint().apply { color = Color.RED })
        c.drawRect(100f, 0f, 200f, 100f, android.graphics.Paint().apply { color = Color.BLUE })
        val out = PrintUtils.renderPrint(src, 400, 400, "Stretch", Color.WHITE)
        assertEquals(Color.RED, out.getPixel(100, 200))
        assertEquals(Color.BLUE, out.getPixel(300, 200))
    }

    @Test
    fun placementRectFitCentersWithMargins() {
        val r = PrintUtils.placementRect(400f, 600f, 20f, 2.0f, "Fit") // wide image in tall page
        assertEquals(20f, r.left, 0.5f)
        assertEquals(380f, r.right, 0.5f)
        assertTrue(r.top > 20f && r.bottom < 580f)  // centered vertically
        assertEquals(2.0f, r.width() / r.height(), 0.01f)
    }

    @Test
    fun gridFitCountsCopies() {
        // 2x2 in cells on A4 (8.27x11.69) with 3mm gaps and 5mm margins
        val a4 = PrintUtils.presets.first { it.name == "A4" }
        val fit = PrintUtils.gridFit(a4.wIn, a4.hIn, 2.0, 2.0, PrintUtils.SHEET_GAP_IN, PrintUtils.SHEET_MARGIN_IN)
        assertEquals(3, fit[0])   // 8.27in wide sheet, 2in cells + 3mm gaps + 5mm margins
        assertEquals(5, fit[1])
        // nothing fits when the cell is bigger than the sheet
        assertEquals(0, PrintUtils.gridFit(1.0, 1.0, 2.0, 2.0, 0.1, 0.1)[0])
    }

    @Test
    fun sheetRenderTilesAndDrawsGuides() {
        val src = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.RED) }
        val sheet = PrintUtils.renderSheet(src, 600, 400, 100, 100, 2, 2, 40, true)
        assertEquals(600, sheet.width)
        // centers of the four cells are red
        val gridW = 2 * 100 + 40
        val gridH = 2 * 100 + 40
        val sx = (600 - gridW) / 2
        val sy = (400 - gridH) / 2
        assertEquals(Color.RED, sheet.getPixel(sx + 50, sy + 50))
        assertEquals(Color.RED, sheet.getPixel(sx + 140 + 50, sy + 140 + 50))
        // gap between cells stays white
        assertEquals(Color.WHITE, sheet.getPixel(sx + 120, sy + 50))
        // cutting guide pixels exist on a cell border (light grey)
        val borderPx = sheet.getPixel(sx, sy + 50)
        assertEquals(Color.LTGRAY, borderPx)
    }

    @Test
    fun sheetRenderHonoursExactCount() {
        val src = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.RED) }
        val sheet = PrintUtils.renderSheet(src, 600, 400, 100, 100, 2, 2, 40, true, 3)
        val gridW = 2 * 100 + 40
        val gridH = 2 * 100 + 40
        val sx = (600 - gridW) / 2
        val sy = (400 - gridH) / 2
        assertEquals(Color.RED, sheet.getPixel(sx + 50, sy + 50))          // cell 1
        assertEquals(Color.RED, sheet.getPixel(sx + 140 + 50, sy + 50))    // cell 2
        assertEquals(Color.RED, sheet.getPixel(sx + 50, sy + 140 + 50))    // cell 3
        assertEquals(Color.WHITE, sheet.getPixel(sx + 140 + 50, sy + 140 + 50))  // cell 4 skipped
    }

    @Test
    fun presetsAreExactDefinitions() {
        val letter = PrintUtils.presets.first { it.name == "US Letter" }
        assertEquals(8.5, letter.wIn, 0.0001)
        assertEquals(11.0, letter.hIn, 0.0001)
        val a5 = PrintUtils.presets.first { it.name == "A5" }
        assertEquals(148.0 / 25.4, a5.wIn, 0.0001)
        assertTrue(PrintUtils.presets.any { it.group == "Posters" })
        assertTrue(PrintUtils.presets.any { it.group == "Square" })
        assertTrue(PrintUtils.presets.any { it.name == "Tabloid" && it.wIn == 11.0 && it.hIn == 17.0 })
        assertTrue(PrintUtils.presets.any { it.name == "Wallet" && it.wIn == 2.5 && it.hIn == 3.5 })
        assertTrue(PrintUtils.presets.any { it.name == "16 × 20" })
        assertTrue(PrintUtils.presets.any { it.group == "Paper sizes (US)" })
        val eu = PrintUtils.presets.first { it.name == "35 × 45 mm" }
        assertEquals(35.0 / 25.4, eu.wIn, 0.0001)
        assertEquals(45.0 / 25.4, eu.hIn, 0.0001)
        val us = PrintUtils.presets.first { it.name == "2 × 2 in" }
        assertEquals(2.0, us.wIn, 0.0001)
        assertEquals("ID / Passport", us.group)
        assertEquals(listOf(150, 200, 300, 600), PrintUtils.ppiOptions)
    }

    @Test
    fun lightGreyBackgroundIsVisibleAndApplied() {
        // His v0.15.10 bug: "default and light grey same" - #F2F2F2 was invisible next to White.
        // The shared constant must be a clearly visible grey AND renderPrint must actually paint it.
        assertEquals(0xFFE0E0E0.toInt(), PrintUtils.LIGHT_GREY_BG)
        assertTrue("light grey must be clearly darker than white",
            Color.red(Color.WHITE) - Color.red(PrintUtils.LIGHT_GREY_BG) >= 0x18)

        // Transparent source rendered with the light-grey background: the uncovered area is exactly that grey.
        val src = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888) // fully transparent
        val out = PrintUtils.renderPrint(src, 40, 30, "Fit", PrintUtils.LIGHT_GREY_BG)
        assertEquals(PrintUtils.LIGHT_GREY_BG, out.getPixel(2, 2))
        // And with white it is white - the two options can never look the same again.
        val outW = PrintUtils.renderPrint(src, 40, 30, "Fit", Color.WHITE)
        assertEquals(Color.WHITE, outW.getPixel(2, 2))
        assertTrue(out.getPixel(2, 2) != outW.getPixel(2, 2))
    }
}
