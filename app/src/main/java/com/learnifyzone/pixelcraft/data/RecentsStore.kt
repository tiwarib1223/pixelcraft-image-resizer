package com.learnifyzone.pixelcraft.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

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
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = list(context).map { it.toString() }.toMutableList()
        current.remove(uri.toString())
        current.add(0, uri.toString())
        prefs.edit().putString(KEY_ORDER, current.take(MAX).joinToString("\n")).apply()
    }

    fun list(context: Context): List<Uri> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ordered = prefs.getString(KEY_ORDER, "") ?: ""
        if (ordered.isBlank()) return emptyList()
        return ordered.split("\n").filter { it.isNotBlank() }.map { Uri.parse(it) }
    }

    /** Small thumbnail for the recents row; null when the photo is no longer readable. */
    fun loadThumbnail(context: Context, uri: Uri, maxDim: Int = 256): Bitmap? {
        return try {
            val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, bounds)
            } ?: return null
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > maxDim) sample *= 2
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(
                    it, null,
                    android.graphics.BitmapFactory.Options().apply { inSampleSize = sample }
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
