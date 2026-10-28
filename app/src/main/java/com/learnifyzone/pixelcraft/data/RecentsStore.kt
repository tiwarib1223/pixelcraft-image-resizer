package com.learnifyzone.pixelcraft.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * "Recently edited": remembers photos the user opened in PixelCraft, using
 * persisted photo-picker read grants. No storage permission is ever requested;
 * the list stays on the phone (SharedPreferences, most recent first, max 12).
 */
object RecentsStore {

    private const val PREFS = "pixelcraft_recents"
    private const val KEY_ORDER = "order"
    private const val MAX = 12

    fun add(context: Context, uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            // Some providers do not offer persistable grants; then we simply
            // do not remember the photo. Editing still works.
            return
        }
        // Cache a small thumbnail in app-private storage so the "Recently edited"
        // row always has an image, even when the picker grant can no longer be
        // read in a later session.
        runCatching { cacheThumbnail(context, uri) }
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = list(context).map { it.toString() }.toMutableList()
        current.remove(uri.toString())
        current.add(0, uri.toString())
        val kept = current.take(MAX)
        current.drop(MAX).forEach { dropped -> File(thumbDir(context), thumbName(Uri.parse(dropped))).delete() }
        prefs.edit().putString(KEY_ORDER, kept.joinToString("\n")).apply()
    }

    private fun thumbDir(context: Context): File = File(context.filesDir, "recents_thumbs").apply { mkdirs() }

    private fun thumbName(uri: Uri): String = "t_" + uri.toString().hashCode().toString(16) + ".jpg"

    private fun decodeSampled(context: Context, uri: Uri, maxDim: Int): Bitmap? {
        val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        } ?: return null
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / sample > maxDim) sample *= 2
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(
                it, null,
                android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }
            )
        }
    }

    private fun cacheThumbnail(context: Context, uri: Uri, maxDim: Int = 256) {
        val bmp = decodeSampled(context, uri, maxDim) ?: return
        try {
            FileOutputStream(File(thumbDir(context), thumbName(uri))).use {
                bmp.compress(Bitmap.CompressFormat.JPEG, 85, it)
            }
        } finally {
            bmp.recycle()
        }
    }

    /**
     * Cache the thumbnail from an image file the app already holds (for example a
     * batch-resized PNG in the cache dir), so "Recently edited" shows the edited
     * version even when the original picker grant can no longer be read.
     */
    fun cacheThumbnailFromFile(context: Context, uri: Uri, file: File, maxDim: Int = 256) {
        try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > maxDim) sample *= 2
            val bmp = BitmapFactory.decodeFile(
                file.absolutePath,
                BitmapFactory.Options().apply { inSampleSize = sample }
            ) ?: return
            try {
                FileOutputStream(File(thumbDir(context), thumbName(uri))).use {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 85, it)
                }
            } finally {
                bmp.recycle()
            }
        } catch (e: Exception) {
            // A missing thumbnail only means a placeholder tile; never fail the caller.
        }
    }

    /** Cache the thumbnail from a bitmap the app already holds (the caller keeps ownership). */
    fun cacheThumbnailFromBitmap(context: Context, uri: Uri, bitmap: Bitmap, maxDim: Int = 256) {
        try {
            val scale = maxDim.toFloat() / maxOf(bitmap.width, bitmap.height)
            val thumb = if (scale < 1f) Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt().coerceAtLeast(1),
                (bitmap.height * scale).toInt().coerceAtLeast(1),
                true
            ) else bitmap
            try {
                FileOutputStream(File(thumbDir(context), thumbName(uri))).use {
                    thumb.compress(Bitmap.CompressFormat.JPEG, 85, it)
                }
            } finally {
                if (thumb !== bitmap) thumb.recycle()
            }
        } catch (e: Exception) {
            // Placeholder tile only; never fail the caller.
        }
    }

    /** Forget a photo whose source can no longer be read (dead picker grant, deleted file). */
    fun remove(context: Context, uri: Uri) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val kept = list(context).map { it.toString() }.filter { it != uri.toString() }
        prefs.edit().putString(KEY_ORDER, kept.joinToString("\n")).apply()
        File(thumbDir(context), thumbName(uri)).delete()
    }

    fun list(context: Context): List<Uri> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ordered = prefs.getString(KEY_ORDER, "") ?: ""
        if (ordered.isBlank()) return emptyList()
        return ordered.split("\n").filter { it.isNotBlank() }.map { Uri.parse(it) }
    }

    /**
     * Small thumbnail for the recents row; null when the photo is no longer readable.
     * Three layers, fastest and most reliable first:
     * 1. the app-private cached copy (works even when the picker grant is gone);
     * 2. Android's own MediaStore thumbnailer (no permission needed while the URI is
     *    readable; uses the system thumbnail cache, no network for local photos);
     * 3. a raw decode of the source as the last resort.
     */
    fun loadThumbnail(context: Context, uri: Uri, maxDim: Int = 256): Bitmap? {
        try {
            val cached = File(thumbDir(context), thumbName(uri))
            if (cached.exists()) {
                val bmp = BitmapFactory.decodeFile(cached.absolutePath)
                if (bmp != null) return bmp
            }
        } catch (e: Exception) {
            // Fall through to the next layer.
        }
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            try {
                return context.contentResolver.loadThumbnail(uri, android.util.Size(maxDim, maxDim), null)
            } catch (e: Exception) {
                // Fall through to the raw decode.
            }
        }
        return try {
            decodeSampled(context, uri, maxDim)
        } catch (e: Exception) {
            null
        }
    }
}
