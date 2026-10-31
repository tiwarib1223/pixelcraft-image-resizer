package com.learnifyzone.pixelcraft.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * One auto-saved editing session: the photo plus the resize settings the user
 * had entered. Drafts are saved automatically when leaving the editor without
 * exporting, and reopen exactly where the user left off. On-device only
 * (SharedPreferences), most recent first, max 12.
 */
data class Draft(
    val sourceUri: String,
    val width: String,
    val height: String,
    val lockRatio: Boolean,
    val quality: Int,
    val updatedAt: Long,
    val tool: String = "Resize Image"
)

object DraftsStore {

    private const val PREFS = "pixelcraft_drafts"
    private const val KEY = "drafts"
    private const val MAX = 12

    fun upsert(context: Context, draft: Draft) {
        val all = list(context).filter { it.sourceUri != draft.sourceUri }.toMutableList()
        all.add(0, draft)
        save(context, all.take(MAX))
    }

    fun remove(context: Context, sourceUri: String) {
        save(context, list(context).filter { it.sourceUri != sourceUri })
    }

    fun list(context: Context): List<Draft> {
        return try {
            val text = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
            val arr = JSONArray(text)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Draft(
                    sourceUri = o.getString("sourceUri"),
                    width = o.optString("width"),
                    height = o.optString("height"),
                    lockRatio = o.optBoolean("lockRatio", true),
                    quality = o.optInt("quality", 90),
                    tool = o.optString("tool", "Resize Image"),
                    updatedAt = o.optLong("updatedAt")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun save(context: Context, drafts: List<Draft>) {
        val arr = JSONArray()
        drafts.forEach { d ->
            arr.put(JSONObject().apply {
                put("sourceUri", d.sourceUri)
                put("width", d.width)
                put("height", d.height)
                put("lockRatio", d.lockRatio)
                put("quality", d.quality)
                put("updatedAt", d.updatedAt)
                put("tool", d.tool)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }
}
