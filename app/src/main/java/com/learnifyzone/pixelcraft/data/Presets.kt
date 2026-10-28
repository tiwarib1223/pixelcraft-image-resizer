package com.learnifyzone.pixelcraft.data

import android.content.Context
import org.json.JSONObject

/** One preset image size from the bundled presets.json data file. */
data class Preset(
    val platform: String,
    val category: String, // "shop" or "social"
    val name: String,
    val width: Int,
    val height: Int,
    val ratio: String,
    val format: String,
    val notes: String,
    val sourceUrl: String,
    val dateVerified: String,
    val status: String // "verified" or "unverified"
)

/**
 * Loads presets from app/src/main/assets/presets.json - the ONE data file for
 * preset sizes. Stage 2B researches official platform documentation and fills
 * it; until then the list can be legitimately empty and the screen says so.
 */
object PresetStore {

    fun load(context: Context): List<Preset> {
        return try {
            val text = context.assets.open("presets.json").bufferedReader().use { it.readText() }
            val root = JSONObject(text)
            val arr = root.getJSONArray("presets")
            val out = mutableListOf<Preset>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    Preset(
                        platform = o.optString("platform"),
                        category = o.optString("category", "social"),
                        name = o.optString("name"),
                        width = o.optInt("width"),
                        height = o.optInt("height"),
                        ratio = o.optString("ratio"),
                        format = o.optString("format"),
                        notes = o.optString("notes"),
                        sourceUrl = o.optString("sourceUrl"),
                        dateVerified = o.optString("dateVerified"),
                        status = o.optString("status", "unverified")
                    )
                )
            }
            out
        } catch (e: Exception) {
            emptyList()
        }
    }
}
