package com.learnifyzone.pixelcraft.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Document Scanner library ("My Scans", his original Scanner recording, Screen 10).
 *
 * Every saved scan is a folder on internal storage: filesDir/scans/<docId>/ with
 *   page_<id>.jpg   - the untouched original page image (camera capture or import)
 *   meta.json       - document + per-page settings (crop quad, rotation, filter, adjustments)
 * and one shared index filesDir/scans/index.json listing all documents.
 *
 * The original source file the user picked is NEVER touched (his spec: "Never delete the
 * original source document"); everything the scanner edits lives in this private folder.
 * Fully on-device, no account, no network.
 */
object ScansStore {

    class PageMeta(
        val id: String,
        var rotation: Int = 0,                 // 0 / 90 / 180 / 270
        var crop: FloatArray? = null,          // 8 floats, x/y of 4 corners relative 0..1, null = full page
        var filter: String = "original",
        var straighten: Float = 0f,            // free-angle tilt correction, degrees
        var brightness: Float = 0f, var contrast: Float = 0f, var highlights: Float = 0f,
        var shadows: Float = 0f, var sharpness: Float = 0f, var whitening: Float = 0f,
        var shadowReduction: Float = 0f
    ) {
        fun toJson(): JSONObject = JSONObject().apply {
            put("id", id); put("rotation", rotation); put("filter", filter); put("straighten", straighten.toDouble())
            put("brightness", brightness.toDouble()); put("contrast", contrast.toDouble())
            put("highlights", highlights.toDouble()); put("shadows", shadows.toDouble())
            put("sharpness", sharpness.toDouble()); put("whitening", whitening.toDouble())
            put("shadowReduction", shadowReduction.toDouble())
            if (crop != null) put("crop", JSONArray().apply { crop!!.forEach { put(it.toDouble()) } })
        }

        companion object {
            fun fromJson(o: JSONObject): PageMeta {
                val p = PageMeta(o.getString("id"))
                p.rotation = o.optInt("rotation", 0)
                p.filter = o.optString("filter", "original")
                p.straighten = o.optDouble("straighten", 0.0).toFloat()
                p.brightness = o.optDouble("brightness", 0.0).toFloat()
                p.contrast = o.optDouble("contrast", 0.0).toFloat()
                p.highlights = o.optDouble("highlights", 0.0).toFloat()
                p.shadows = o.optDouble("shadows", 0.0).toFloat()
                p.sharpness = o.optDouble("sharpness", 0.0).toFloat()
                p.whitening = o.optDouble("whitening", 0.0).toFloat()
                p.shadowReduction = o.optDouble("shadowReduction", 0.0).toFloat()
                val c = o.optJSONArray("crop")
                if (c != null && c.length() == 8) p.crop = FloatArray(8) { c.getDouble(it).toFloat() }
                return p
            }
        }
    }

    class DocMeta(
        val id: String,
        var name: String,
        var folder: String = "",
        var createdAt: Long,
        var modifiedAt: Long,
        val pages: MutableList<PageMeta> = mutableListOf(),
        var coverExport: String? = null        // filename of the last exported PDF/PNG inside the doc folder
    ) {
        fun toJson(): JSONObject = JSONObject().apply {
            put("id", id); put("name", name); put("folder", folder)
            put("createdAt", createdAt); put("modifiedAt", modifiedAt)
            put("coverExport", coverExport ?: JSONObject.NULL)
            put("pages", JSONArray().apply { pages.forEach { put(it.toJson()) } })
        }

        companion object {
            fun fromJson(o: JSONObject): DocMeta {
                val d = DocMeta(
                    o.getString("id"), o.getString("name"), o.optString("folder", ""),
                    o.optLong("createdAt"), o.optLong("modifiedAt"),
                    coverExport = if (o.isNull("coverExport")) null else o.optString("coverExport", null)
                )
                val arr = o.optJSONArray("pages")
                if (arr != null) for (i in 0 until arr.length()) d.pages.add(PageMeta.fromJson(arr.getJSONObject(i)))
                return d
            }
        }
    }

    private fun root(context: Context) = File(context.filesDir, "scans").apply { mkdirs() }
    private fun indexFile(context: Context) = File(root(context), "index.json")

    fun docDir(context: Context, docId: String) = File(root(context), docId).apply { mkdirs() }
    fun pageFile(context: Context, docId: String, pageId: String) = File(docDir(context, docId), "page_$pageId.jpg")

    /** List document ids in the index, newest first. */
    fun listDocIds(context: Context): List<String> {
        val f = indexFile(context)
        if (!f.exists()) return emptyList()
        return try {
            val arr = JSONArray(f.readText())
            (0 until arr.length()).map { arr.getString(it) }
        } catch (e: Exception) { emptyList() }
    }

    fun loadDoc(context: Context, docId: String): DocMeta? {
        val f = File(docDir(context, docId), "meta.json")
        if (!f.exists()) return null
        return try { DocMeta.fromJson(JSONObject(f.readText())) } catch (e: Exception) { null }
    }

    fun saveDoc(context: Context, doc: DocMeta) {
        doc.modifiedAt = System.currentTimeMillis()
        File(docDir(context, doc.id), "meta.json").writeText(doc.toJson().toString())
        val ids = listDocIds(context).filter { it != doc.id }
        indexFile(context).writeText(JSONArray(listOf(doc.id) + ids).toString())
    }

    fun deleteDoc(context: Context, docId: String) {
        docDir(context, docId).deleteRecursively()
        indexFile(context).writeText(JSONArray(listDocIds(context).filter { it != docId }).toString())
    }

    /** Total bytes the library currently uses, for the Settings "Storage usage" row. */
    fun docBytes(context: Context, docId: String): Long =
        docDir(context, docId).listFiles()?.filter { it.isFile }?.sumOf { it.length() } ?: 0L

    fun storageBytes(context: Context): Long =
        root(context).walkBottomUp().filter { it.isFile }.sumOf { it.length() }
}
