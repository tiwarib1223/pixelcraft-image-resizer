package com.learnifyzone.pixelcraft

import kotlin.math.roundToInt

data class PixelSize(val width: Int, val height: Int)

object ResizeMath {
    const val MAX_DIMENSION = 12_000
    const val MAX_PIXELS = 40_000_000L

    fun fromPercent(width: Int, height: Int, percent: Double): PixelSize {
        require(width > 0 && height > 0) { "Image dimensions must be positive." }
        require(percent > 0.0 && percent <= 400.0) { "Percentage must be greater than 0 and no more than 400%." }
        return validate(
            (width * percent / 100.0).roundToInt().coerceAtLeast(1),
            (height * percent / 100.0).roundToInt().coerceAtLeast(1)
        )
    }

    fun fromWidth(width: Int, height: Int, newWidth: Int): PixelSize {
        require(width > 0 && height > 0 && newWidth > 0) { "Dimensions must be positive." }
        val newHeight = (newWidth.toDouble() * height / width).roundToInt().coerceAtLeast(1)
        return validate(newWidth, newHeight)
    }

    fun validate(width: Int, height: Int): PixelSize {
        require(width > 0 && height > 0) { "Width and height must be positive." }
        require(width <= MAX_DIMENSION && height <= MAX_DIMENSION) {
            "Each dimension must be no more than $MAX_DIMENSION pixels."
        }
        require(width.toLong() * height.toLong() <= MAX_PIXELS) {
            "The output is too large for this version. Choose smaller dimensions."
        }
        return PixelSize(width, height)
    }
}
