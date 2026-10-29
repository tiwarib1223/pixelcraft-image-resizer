package com.learnifyzone.pixelcraft

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import com.learnifyzone.pixelcraft.image.CropMath
import org.junit.Test

class ResizeMathTest {
    @Test fun fiftyPercentHalvesDimensions() {
        assertEquals(PixelSize(800, 600), ResizeMath.fromPercent(1600, 1200, 50.0))
    }

    @Test fun customWidthKeepsAspectRatio() {
        assertEquals(PixelSize(1000, 500), ResizeMath.fromWidth(2000, 1000, 1000))
    }

    @Test fun invalidDimensionsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            ResizeMath.fromPercent(0, 100, 50.0)
        }
    }

    @Test fun cropAspectMaxRectIsCenteredAndCorrectRatio() {
        val r = CropMath.maxRectForAspect("1:1", 2000, 1000)
        val wPx = (r[2] - r[0]) * 2000
        val hPx = (r[3] - r[1]) * 1000
        assertEquals(wPx, hPx, 2.0f)
        assertEquals(r[0], 1f - r[2], 0.001f)
        assertEquals(r[1], 1f - r[3], 0.001f)
    }

    @Test fun cropFreeRectDefaultsNearlyFullPhoto() {
        val r = CropMath.maxRectForAspect("Free", 2000, 1000)
        assertEquals(0.05f, r[0], 0.001f)
        assertEquals(0.95f, r[2], 0.001f)
    }

    @Test fun cropPixelsClampToPhotoBounds() {
        val px = CropMath.toPixels(floatArrayOf(-0.2f, 0.1f, 1.4f, 0.9f), 1000, 800)
        assertEquals(0, px[0])
        assertEquals(1000, px[2])
        assertEquals(80, px[1])
        assertEquals(720, px[3])
    }

    @Test fun cropCornerDragKeepsLockedAspect() {
        val r = CropMath.dragCorner(floatArrayOf(0.2f, 0.2f, 0.8f, 0.8f), 3, -0.2f, 0.1f, 1.0f, 1.0f)
        assertEquals(r[2] - r[0], r[3] - r[1], 0.001f)
    }

    @Test fun cropMoveKeepsFrameInsidePhoto() {
        val r = CropMath.move(floatArrayOf(0.1f, 0.1f, 0.6f, 0.6f), 0.8f, -0.5f)
        assertEquals(0.5f, r[0], 0.001f)
        assertEquals(0f, r[1], 0.001f)
        assertEquals(1f, r[2], 0.001f)
        assertEquals(0.5f, r[3], 0.001f)
    }

    @Test fun transformWithZeroAnglesIsIdentity() {
        val q = CropMath.transformedCorners(1000, 800, 0f, 0f, 0f, 1500f)
        assertEquals(0f, q[0], 0.01f); assertEquals(0f, q[1], 0.01f)
        assertEquals(1000f, q[4], 0.01f); assertEquals(800f, q[5], 0.01f)
        assertEquals(1f, CropMath.coverScale(q, 1000, 800), 0.001f)
    }

    @Test fun straighten90NeedsFullZoomCover() {
        // rotating a 2000x1000 photo by 90 degrees makes it 1000x2000,
        // so it must zoom by 2x to cover the original box again
        val q = CropMath.transformedCorners(2000, 1000, 90f, 0f, 0f, 3000f)
        assertEquals(2f, CropMath.coverScale(q, 2000, 1000), 0.05f)
    }

    @Test fun horizontalTiltCompressesOneSide() {
        // tilting around the vertical axis pushes one side away: its projected
        // height shrinks while the near side grows
        val q = CropMath.transformedCorners(1000, 1000, 0f, 30f, 0f, 1500f)
        // corners are TL(0,1) TR(2,3) BR(4,5) BL(6,7)
        val leftH = q[7] - q[1]   // left edge TL->BL
        val rightH = q[5] - q[3]  // right edge TR->BR
        assert(Math.abs(leftH - rightH) > 1f) { "expected asymmetric sides, left=$leftH right=$rightH" }
        assert(CropMath.coverScale(q, 1000, 1000) > 1f)
    }

    @Test fun coverScaleCoversBoxForCombinedAngles() {
        val q = CropMath.transformedCorners(1600, 1200, 14f, 20f, -10f, 2400f)
        val s = CropMath.coverScale(q, 1600, 1200)
        val cx = 800f; val cy = 600f
        val scaled = FloatArray(8)
        for (i in 0..3) {
            scaled[i * 2] = cx + (q[i * 2] - cx) * s
            scaled[i * 2 + 1] = cy + (q[i * 2 + 1] - cy) * s
        }
        assert(CropMath.pointInQuad(0f, 0f, scaled))
        assert(CropMath.pointInQuad(1600f, 0f, scaled))
        assert(CropMath.pointInQuad(1600f, 1200f, scaled))
        assert(CropMath.pointInQuad(0f, 1200f, scaled))
    }
}