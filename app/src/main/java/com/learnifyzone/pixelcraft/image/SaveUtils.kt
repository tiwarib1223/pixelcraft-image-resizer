package com.learnifyzone.pixelcraft.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build

/** Image formats for export decisions. Format picker UI arrives in Stage 3. */
enum class ExportFormat(val extension: String, val mimeType: String) {
    JPEG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp"),
    UNKNOWN("", "");

    fun compressFormat(): Bitmap.CompressFormat = when (this) {
        JPEG -> Bitmap.CompressFormat.JPEG
        WEBP ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY
            else @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.PNG
    }

    companion object {
        fun of(resolver: ContentResolver, uri: Uri): ExportFormat =
            when (resolver.getType(uri)) {
                "image/jpeg", "image/jpg" -> JPEG
                "image/png" -> PNG
                "image/webp" -> WEBP
                else -> UNKNOWN
            }
    }
}

object SaveUtils {

    /** True when "Replace original" can be offered for this source. */
    fun canReplaceOriginal(isCameraCapture: Boolean, format: ExportFormat): Boolean =
        Build.VERSION.SDK_INT >= 30 && !isCameraCapture && format != ExportFormat.UNKNOWN
}
