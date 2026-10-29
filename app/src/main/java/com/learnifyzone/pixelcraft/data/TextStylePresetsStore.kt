package com.learnifyzone.pixelcraft.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Saved "Text on Photo" styles (his v0.16.1 request: save the current text style as a
 * reusable preset, surfaced in the Presets tab). Stored in SharedPreferences as JSON -
 * fully on-device, no permissions.
 */
data class TextStylePreset(
    val id: Long,
    val name: String,
    val font: String,
    val sizeFrac: Float,
    val bold: Boolean,
    val italic: Boolean,
    val align: String,
    val colorArgb: Int,
    val outlineFrac: Float,
    val outlineColorArgb: Int,
    val bgColorArgb: Int,
    val shadow: Boolean,
    val opacity: Float,
    val curved: Boolean,
    val arcSweep: Float,
    val letterSpacing: Float,
    val lineSpacing: Float,
    val createdAt: Long
)

object TextStylePresetsStore {

    private const val PREFS = "text_style_presets"
    private const val KEY = "styles"

    fun load(context: Context): List<TextStylePreset> {
        return try {
            val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
            val arr = JSONArray(raw)
            val out = mutableListOf<TextStylePreset>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    TextStylePreset(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        font = o.optString("font", "Sans"),
                        sizeFrac = o.optDouble("sizeFrac", 0.07).toFloat(),
                        bold = o.optBoolean("bold"),
                        italic = o.optBoolean("italic"),
                        align = o.optString("align", "CENTER"),
                        colorArgb = o.optInt("colorArgb", -1),
                        outlineFrac = o.optDouble("outlineFrac", 0.0).toFloat(),
                        outlineColorArgb = o.optInt("outlineColorArgb", -16777216),
                        bgColorArgb = o.optInt("bgColorArgb", 0),
                        shadow = o.optBoolean("shadow"),
                        opacity = o.optDouble("opacity", 1.0).toFloat(),
                        curved = o.optBoolean("curved"),
                        arcSweep = o.optDouble("arcSweep", 160.0).toFloat(),
                        letterSpacing = o.optDouble("letterSpacing", 0.0).toFloat(),
                        lineSpacing = o.optDouble("lineSpacing", 0.0).toFloat(),
                        createdAt = o.optLong("createdAt")
                    )
                )
            }
            out.sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun persist(context: Context, list: List<TextStylePreset>) {
        val arr = JSONArray()
        list.forEach { p ->
            arr.put(
                JSONObject()
                    .put("id", p.id).put("name", p.name).put("font", p.font)
                    .put("sizeFrac", p.sizeFrac.toDouble()).put("bold", p.bold).put("italic", p.italic)
                    .put("align", p.align).put("colorArgb", p.colorArgb)
                    .put("outlineFrac", p.outlineFrac.toDouble()).put("outlineColorArgb", p.outlineColorArgb)
                    .put("bgColorArgb", p.bgColorArgb).put("shadow", p.shadow).put("opacity", p.opacity.toDouble())
                    .put("curved", p.curved).put("arcSweep", p.arcSweep.toDouble())
                    .put("letterSpacing", p.letterSpacing.toDouble()).put("lineSpacing", p.lineSpacing.toDouble())
                    .put("createdAt", p.createdAt)
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }

    fun add(context: Context, preset: TextStylePreset) {
        persist(context, (listOf(preset) + load(context)).take(40))
    }

    fun remove(context: Context, id: Long) {
        persist(context, load(context).filterNot { it.id == id })
    }
}
