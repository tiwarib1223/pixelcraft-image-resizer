package com.learnifyzone.pixelcraft

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
}
