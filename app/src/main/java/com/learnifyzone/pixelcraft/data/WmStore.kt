package com.learnifyzone.pixelcraft.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Add Watermark project library ("Recent Projects", his original Watermark recording, Screen 12).
 *
 * Every saved project is a folder on internal storage: filesDir/watermark_projects/<projectId>/
 * with meta.json (name, kind, image count, full design settings) and, once the editors save them,
 * private copies of the working images. The shared index.json lists project ids, newest first.
 *
 * His spec: "Save project settings locally. Do not upload user photographs to a server." and
 * "Never overwrite original photographs." Gallery originals are never touched; everything the
 * watermark tool edits lives in this private folder. Fully on-device, no account, no network.
 */
object WmStore {

    class ProjectMeta(
        val id: String,
        var name: String,
        var kind: String = "text",           // "text" or "logo"
        var imageCount: Int = 0,
        var updatedAt: Long = System.currentTimeMillis(),
        var settingsJson: String = "{}"      // full watermark design state; the editors own this schema
    ) {
        fun toJson(): JSONObject = JSONObject().apply {
            put("id", id); put("name", name); put("kind", kind)
            put("imageCount", imageCount); put("updatedAt", updatedAt); put("settingsJson", settingsJson)
        }
        companion object {
            fun fromJson(o: JSONObject): ProjectMeta = ProjectMeta(
                o.optString("id"), o.optString("name", "Watermark project"),
                o.optString("kind", "text"), o.optInt("imageCount", 0),
                o.optLong("updatedAt", 0L), o.optString("settingsJson", "{}")
            )
        }
    }

    private fun root(context: Context): File = File(context.filesDir, "watermark_projects").apply { mkdirs() }
    private fun indexFile(context: Context): File = File(root(context), "index.json")

    private fun readIndex(context: Context): MutableList<String> {
        val f = indexFile(context)
        if (!f.exists()) return mutableListOf()
        return runCatching {
            val arr = JSONArray(f.readText())
            (0 until arr.length()).map { arr.getString(it) }.toMutableList()
        }.getOrElse { mutableListOf() }
    }

    private fun writeIndex(context: Context, ids: List<String>) {
        indexFile(context).writeText(JSONArray().apply { ids.forEach { put(it) } }.toString())
    }

    fun listProjects(context: Context): List<ProjectMeta> {
        return readIndex(context).mapNotNull { id ->
            val f = File(root(context), "$id/meta.json")
            if (!f.exists()) null else runCatching { ProjectMeta.fromJson(JSONObject(f.readText())) }.getOrNull()
        }.sortedByDescending { it.updatedAt }
    }

    fun saveProject(context: Context, meta: ProjectMeta) {
        val dir = File(root(context), meta.id).apply { mkdirs() }
        File(dir, "meta.json").writeText(meta.toJson().toString())
        val ids = readIndex(context)
        ids.remove(meta.id); ids.add(0, meta.id)
        writeIndex(context, ids)
    }

    fun loadProject(context: Context, id: String): ProjectMeta? {
        val f = File(root(context), "$id/meta.json")
        return if (!f.exists()) null else runCatching { ProjectMeta.fromJson(JSONObject(f.readText())) }.getOrNull()
    }

    fun deleteProject(context: Context, id: String) {
        File(root(context), id).deleteRecursively()
        val ids = readIndex(context); ids.remove(id); writeIndex(context, ids)
    }

    fun renameProject(context: Context, id: String, newName: String) {
        val meta = loadProject(context, id) ?: return
        meta.name = newName
        saveProject(context, meta)
    }

    fun duplicateProject(context: Context, id: String): String? {
        val meta = loadProject(context, id) ?: return null
        val newId = "wm_" + java.util.UUID.randomUUID().toString().take(8)
        val copy = ProjectMeta(newId, meta.name + " copy", meta.kind, meta.imageCount, System.currentTimeMillis(), meta.settingsJson)
        saveProject(context, copy)
        return newId
    }

    fun storageBytes(context: Context): Long = root(context).walkBottomUp().filter { it.isFile }.sumOf { it.length() }
}
