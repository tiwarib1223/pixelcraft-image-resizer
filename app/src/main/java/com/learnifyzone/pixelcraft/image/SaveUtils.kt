package com.learnifyzone.pixelcraft.image

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

/** Image formats for export decisions. PDF is export-only (never a source format). */
enum class ExportFormat(val extension: String, val mimeType: String) {
    JPEG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp"),
    PDF("pdf", "application/pdf"),
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

    /**
     * Resolve a picked photo URI to the real MediaStore item URI that MediaStore.createWriteRequest
     * accepts. Photo-picker URIs (content://media/picker/...) and media document URIs
     * (content://com.android.providers.media.documents/document/image:<id>) both carry the MediaStore
     * row id as their trailing number; the system consent dialog only works with
     * content://media/<volume>/images/media/<id>. Returns null when no media id can be found.
     */
    fun replaceTargetFor(context: Context, uri: Uri): Uri? {
        if (uri.scheme != "content") return null
        val authority = uri.authority ?: return null
        if (authority == MediaStore.AUTHORITY) {
            val isPicker = uri.pathSegments.any { it.startsWith("picker") }
            return if (isPicker) mediaIdToUri(uri.lastPathSegment) else uri
        }
        // Media document URIs: content://com.android.providers.<x>.documents/document/image:<id>
        // (checked with pure string logic - DocumentsContract.isDocumentUri queries the PackageManager,
        // which has no providers on the test host)
        val segments = uri.pathSegments
        if (authority.startsWith("com.android.providers") && authority.endsWith(".documents") && segments.firstOrNull() == "document") {
            // document id looks like "image:12345" (or "msf:12345" on some phones)
            return mediaIdToUri(segments.getOrNull(1)?.substringAfterLast(':'))
        }
        return mediaIdToUri(uri.lastPathSegment)
    }

    private fun mediaIdToUri(segment: String?): Uri? =
        segment?.toLongOrNull()?.let { ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it) }
}
