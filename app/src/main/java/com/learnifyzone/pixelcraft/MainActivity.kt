package com.learnifyzone.pixelcraft

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.learnifyzone.pixelcraft.data.Draft
import com.learnifyzone.pixelcraft.data.DraftsStore
import com.learnifyzone.pixelcraft.data.Preset
import com.learnifyzone.pixelcraft.data.PresetStore
import com.learnifyzone.pixelcraft.data.RecentsStore
import com.learnifyzone.pixelcraft.image.ExportFormat
import com.learnifyzone.pixelcraft.image.SaveUtils
import com.learnifyzone.pixelcraft.image.Transforms
import com.learnifyzone.pixelcraft.image.Transforms.CropAnchor
import com.learnifyzone.pixelcraft.image.Transforms.FitPad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DecimalFormat
import kotlin.math.roundToInt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SkyBlue = Color(0xFF42BFEF)
private val Orange = Color(0xFFFF8A32)
private val DeepNavy = Color(0xFF172B4D)
private val PaleBlue = Color(0xFFF0FAFF)
private val SoftGrey = Color(0xFFF3F6F9)
private val Slate = Color(0xFF66758A)
private val BorderGrey = Color(0xFFE0E8EE)
private val SuccessGreen = Color(0xFF1E9E50)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PixelCraftApp() }
    }
}

private enum class MainTab { Home, MyPhotos, Presets, Settings }
private enum class Screen { Home, Editor, Export, Print, Success, QueueOverview, QueueResult }
private enum class Tool(val label: String) { Resize("Resize"), Presets("Presets"), Crop("Crop"), Background("Background"), Format("Format") }
private enum class AdaptMode(val label: String) { FIT("Fit"), CROP("Crop"), STRETCH("Stretch") }
private data class Feature(val title: String, val description: String, val icon: String, val active: Boolean = false)
private data class AdaptRequest(val width: Int, val height: Int)

@Composable
private fun PixelCraftApp() {
    var darkMode by rememberSaveable { mutableStateOf(false) }
    val colors = if (darkMode) darkColorScheme(primary = SkyBlue, secondary = Orange, background = Color(0xFF101A2B), surface = Color(0xFF17243A), onPrimary = DeepNavy) else lightColorScheme(primary = SkyBlue, secondary = Orange, background = Color.White, surface = Color.White, onPrimary = DeepNavy, onBackground = DeepNavy, onSurface = DeepNavy)
    MaterialTheme(colorScheme = colors) {
        PixelCraftRoot(darkMode = darkMode, onDarkMode = { darkMode = it })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PixelCraftRoot(darkMode: Boolean, onDarkMode: (Boolean) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }
    var tool by rememberSaveable { mutableStateOf(Tool.Resize) }

    var sourceUri by remember { mutableStateOf<Uri?>(null) }
    var sourceIsCamera by rememberSaveable { mutableStateOf(false) }
    var sourceFormat by remember { mutableStateOf(ExportFormat.UNKNOWN) }
    var sourceBytes by rememberSaveable { mutableLongStateOf(-1L) }
    var workingBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var estimatedBytes by rememberSaveable { mutableLongStateOf(-1L) }

    var inputWidth by rememberSaveable { mutableStateOf("") }
    var inputHeight by rememberSaveable { mutableStateOf("") }
    var customPercent by rememberSaveable { mutableStateOf("") }
    var percentError by rememberSaveable { mutableStateOf("") }
    var lockRatio by rememberSaveable { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var busyLabel by rememberSaveable { mutableStateOf("") }
    var beforeAfter by rememberSaveable { mutableStateOf(false) }
    var adaptRequest by remember { mutableStateOf<AdaptRequest?>(null) }

    var exportName by rememberSaveable { mutableStateOf("pixelcraft-image") }
    var quality by rememberSaveable { mutableIntStateOf(90) }
    var savedName by rememberSaveable { mutableStateOf("") }
    var savedSize by rememberSaveable { mutableStateOf("") }

    var recents by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val recentThumbs = remember { mutableStateMapOf<String, ImageBitmap?>() }
    var pendingPreset by remember { mutableStateOf<Preset?>(null) }
    var pendingDraft by remember { mutableStateOf<Draft?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var legacySaveAfterGrant by rememberSaveable { mutableStateOf(false) }
    var photoQueue by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var queueIndex by rememberSaveable { mutableIntStateOf(0) }
    val queueThumbs = remember { mutableStateMapOf<String, ImageBitmap?>() }
    var queueTargetW by rememberSaveable { mutableStateOf("") }
    var queueTargetH by rememberSaveable { mutableStateOf("") }
    var queueNamePattern by rememberSaveable { mutableStateOf("") }
    val queueInfo = remember { mutableStateMapOf<String, QueueItemInfo>() }
    var showDimensionsSheet by rememberSaveable { mutableStateOf(false) }
    var lastUsedTarget by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var batchProgress by remember { mutableStateOf<BatchProgress?>(null) }
    var batchSaveNote by remember { mutableStateOf<String?>(null) }
    val processedResults = remember { mutableStateMapOf<String, ProcessedResult>() }
    var pendingLaunch by remember { mutableStateOf<String?>(null) }
    var chosenFormat by rememberSaveable { mutableStateOf("original") }
    var sizeUnit by rememberSaveable { mutableStateOf("px") }
    var resizeDpi by rememberSaveable { mutableStateOf("300") }

    fun clearProcessedResults() {
        processedResults.values.forEach { runCatching { File(it.cachePath).delete() } }
        processedResults.clear()
    }

    fun sourceOutFormat(): ExportFormat = if (sourceFormat == ExportFormat.UNKNOWN) ExportFormat.JPEG else sourceFormat

    fun outFormat(): ExportFormat = when (chosenFormat) {
        "original" -> sourceOutFormat()
        else -> runCatching { ExportFormat.valueOf(chosenFormat) }.getOrDefault(ExportFormat.JPEG)
    }

    fun currentDpi(): Double = resizeDpi.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 } ?: 300.0


    fun invalidateProcessed() {
        processedBitmap = null
        estimatedBytes = -1L
        beforeAfter = false
    }

    fun convertInputsToUnit(from: String, to: String) {
        if (from == to) return
        val dpi = currentDpi()
        val wPx = parseToPx(inputWidth, from, dpi)
        val hPx = parseToPx(inputHeight, from, dpi)
        sizeUnit = to
        inputWidth = wPx?.let { fmtUnit(it.toDouble(), to, dpi) } ?: ""
        inputHeight = hPx?.let { fmtUnit(it.toDouble(), to, dpi) } ?: ""
        invalidateProcessed()
    }

    fun applyTarget(width: Int, height: Int) {
        val dpi = currentDpi()
        inputWidth = fmtUnit(width.toDouble(), sizeUnit, dpi)
        inputHeight = fmtUnit(height.toDouble(), sizeUnit, dpi)
        invalidateProcessed()
    }

    fun applyPercent(percent: Double) {
        val src = workingBitmap ?: return
        val size = runCatching { ResizeMath.fromPercent(src.width, src.height, percent) }.getOrElse {
            scope.launch { snackbar.showSnackbar(it.message ?: "Invalid percentage") }
            return
        }
        applyTarget(size.width, size.height)
    }

    fun loadPhoto(uri: Uri, isCamera: Boolean, fromQueue: Boolean = false) {
        if (!fromQueue) {
            photoQueue = emptyList()
            queueIndex = 0
            clearProcessedResults()
        }
        val keepTarget = fromQueue && queueIndex > 0
        busy = true
        busyLabel = "Loading photo…"
        scope.launch {
            try {
                val loaded = withContext(Dispatchers.IO) { decodeOrientedBitmap(context, uri) }
                val format = if (isCamera) ExportFormat.JPEG
                    else withContext(Dispatchers.IO) { ExportFormat.of(context.contentResolver, uri) }
                val bytes = withContext(Dispatchers.IO) { querySourceSize(context, uri) }
                workingBitmap = loaded
                processedBitmap = null
                estimatedBytes = -1L
                sourceUri = uri
                sourceIsCamera = isCamera
                sourceFormat = format
                sourceBytes = bytes
                if (!keepTarget) {
                    applyTarget(loaded.width, loaded.height)
                    customPercent = ""
                    percentError = ""
                }
                exportName = if (fromQueue && queueNamePattern.isNotBlank())
                    "${queueNamePattern.trim()}-${queueIndex + 1}"
                else suggestBaseName(loaded.width, loaded.height)
                beforeAfter = false
                if (!isCamera) withContext(Dispatchers.IO) {
                    RecentsStore.add(context, uri)
                    // Cache from the bitmap that just decoded, never a second read of the URI.
                    runCatching { RecentsStore.cacheThumbnailFromBitmap(context, uri, loaded) }
                }
                pendingPreset?.let { preset ->
                    applyTarget(preset.width, preset.height)
                    pendingPreset = null
                }
                pendingDraft?.let { draft ->
                    val dw = draft.width.toIntOrNull()
                    val dh = draft.height.toIntOrNull()
                    if (dw != null && dh != null) applyTarget(dw, dh)
                    lockRatio = draft.lockRatio
                    quality = draft.quality
                    pendingDraft = null
                    invalidateProcessed()
                }
                if (fromQueue && queueTargetW.isNotBlank() && queueTargetH.isNotBlank()) {
                    val sw = queueTargetW.toIntOrNull()
                    val sh = queueTargetH.toIntOrNull()
                    if (sw != null && sh != null) applyTarget(sw, sh)
                }
                processedResults[uri.toString()]?.let { stored ->
                    val cached = withContext(Dispatchers.IO) { BitmapFactory.decodeFile(stored.cachePath) }
                    if (cached != null) {
                        applyTarget(stored.width, stored.height)
                        processedBitmap = cached
                        estimatedBytes = stored.bytes
                        beforeAfter = true
                    }
                }
                screen = Screen.Editor
                tool = Tool.Resize
                // Clear busy BEFORE the snackbar: showSnackbar suspends until dismissal,
                // so gating busy on it left the Process button spinning for ~4s after every load.
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Photo loaded (${loaded.width} × ${loaded.height} px).")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't open this image. Try another photo.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    // Test hook (JVM UI tests): a file path in this intent extra pre-loads that photo,
    // skipping the system photo picker, which cannot run under Robolectric.
    LaunchedEffect(Unit) {
        val testPath = (context as? Activity)?.intent?.getStringExtra("pixelcraft.test.photo")
        if (!testPath.isNullOrBlank() && sourceUri == null && workingBitmap == null) {
            loadPhoto(Uri.fromFile(File(testPath)), isCamera = false)
        }
    }

    fun encodeProcessed(format: ExportFormat = outFormat()): ByteArray {
        val processed = processedBitmap ?: throw IllegalStateException("Nothing processed yet")
        return encodeImage(context, sourceUri, processed, format, quality)
    }

    fun runProcess(targetW: Int, targetH: Int, mode: AdaptMode, anchor: CropAnchor, pad: FitPad) {
        val src = workingBitmap ?: return
        busy = true
        busyLabel = "Processing…"
        batchProgress = BatchProgress("Processing", 1, 1, 10)
        adaptRequest = null
        lastUsedTarget = targetW to targetH
        saveLastUsedTarget(context, targetW, targetH)
        scope.launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    when (mode) {
                        AdaptMode.FIT -> Transforms.fit(src, targetW, targetH, pad)
                        AdaptMode.CROP -> Transforms.crop(src, targetW, targetH, anchor)
                        AdaptMode.STRETCH -> Transforms.stretch(src, targetW, targetH)
                    }
                }
                processedBitmap = result
                batchProgress = BatchProgress("Processing", 1, 1, 60)
                exportName = suggestBaseName(targetW, targetH)
                beforeAfter = true
                batchProgress = BatchProgress("Processing", 1, 1, 90)
                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(result, outFormat(), quality) }
                // Straight to the result page (preview, before/after sizes, export options) -
                // closing the size sheet must not drop the user back on the editor.
                screen = Screen.Export
                snackbar.showSnackbar("Processed: $targetW × $targetH px.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't process this image. Try smaller dimensions.")
            } finally {
                busy = false
                busyLabel = ""
                batchProgress = null
            }
        }
    }

    fun runFitToFileSize(targetBytes: Long) {
        val src = workingBitmap ?: return
        busy = true
        busyLabel = "Processing…"
        batchProgress = BatchProgress("Processing", 1, 1, 10)
        scope.launch {
            try {
                val fit = withContext(Dispatchers.Default) {
                    fitToFileSize(src, outFormat(), quality, targetBytes) { pct ->
                        batchProgress = BatchProgress("Processing", 1, 1, pct.coerceIn(5, 95))
                    }
                }
                applyTarget(fit.first.width, fit.first.height)
                processedBitmap = fit.first
                estimatedBytes = fit.second
                exportName = suggestBaseName(fit.first.width, fit.first.height)
                beforeAfter = true
                lastUsedTarget = fit.first.width to fit.first.height
                saveLastUsedTarget(context, fit.first.width, fit.first.height)
                screen = Screen.Export
                snackbar.showSnackbar("Processed: ${fit.first.width} × ${fit.first.height} px · ${formatBytes(fit.second)}.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't reach that file size. Try a larger target.")
            } finally {
                busy = false
                busyLabel = ""
                batchProgress = null
            }
        }
    }

    fun runBatchSave() {
        val uris = photoQueue
        if (uris.isEmpty()) return
        busy = true
        busyLabel = "Saving…"
        val total = uris.size
        batchProgress = BatchProgress("Saving", 0, total, 0)
        scope.launch {
            var saved = 0
            var failed = 0
            var totalBytes = 0L
            uris.forEachIndexed { i, uri ->
                batchProgress = BatchProgress("Saving", i + 1, total, (i * 100) / total)
                val stored = processedResults[uri.toString()]
                if (stored == null) {
                    failed++
                } else {
                    try {
                        totalBytes += withContext(Dispatchers.IO) {
                            val bmp = BitmapFactory.decodeFile(stored.cachePath)
                                ?: throw IllegalStateException("Processed image is missing")
                            try {
                                val fmt = if (chosenFormat == "original") {
                                    val probed = ExportFormat.of(context.contentResolver, uri)
                                    if (probed == ExportFormat.UNKNOWN || probed == ExportFormat.PDF) ExportFormat.JPEG else probed
                                } else outFormat()
                                val encoded = encodeImage(context, uri, bmp, fmt, quality)
                                val base = if (queueNamePattern.isNotBlank()) "${queueNamePattern.trim()}-${i + 1}"
                                    else suggestBaseName(stored.width, stored.height)
                                val name = buildFileName(base, fmt)
                                if (fmt == ExportFormat.PDF) saveToDownloads(context, name, fmt.mimeType, encoded)
                                else {
                                    val savedUri = saveToGallery(context, name, fmt.mimeType, encoded)
                                    runCatching { RecentsStore.cacheThumbnailFromFile(context, savedUri, File(stored.cachePath)) }
                                }
                                encoded.size.toLong()
                            } finally {
                                bmp.recycle()
                            }
                        }
                        saved++
                    } catch (e: Exception) {
                        failed++
                    }
                }
                batchProgress = BatchProgress("Saving", i + 1, total, ((i + 1) * 100) / total)
            }
            busy = false
            busyLabel = ""
            batchProgress = null
            batchSaveNote = when {
                failed == 0 -> "All $saved photos saved (${formatBytes(totalBytes)}). Find them in your gallery, in the PixelCraft album."
                saved == 0 -> "Saving failed. Please try again."
                else -> "$saved of $total photos saved; $failed failed. Saved photos are in your gallery."
            }
        }
    }

    fun requestBatchReplace() {
        if (photoQueue.isEmpty()) return
        if (Build.VERSION.SDK_INT >= 30) {
            pendingLaunch = "replaceBatch"
        } else {
            scope.launch { snackbar.showSnackbar("Replace original needs Android 11 or newer. Use 'Save as new files' instead.") }
        }
    }

    fun performBatchReplace() {
        val uris = photoQueue
        if (uris.isEmpty()) return
        busy = true
        busyLabel = "Replacing originals…"
        val total = uris.size
        batchProgress = BatchProgress("Saving", 0, total, 0)
        scope.launch {
            var saved = 0
            var failed = 0
            uris.forEachIndexed { i, uri ->
                batchProgress = BatchProgress("Saving", i + 1, total, (i * 100) / total)
                val stored = processedResults[uri.toString()]
                if (stored == null) {
                    failed++
                } else {
                    try {
                        val size = withContext(Dispatchers.IO) {
                            val bmp = BitmapFactory.decodeFile(stored.cachePath)
                                ?: throw IllegalStateException("Processed image is missing")
                            try {
                                val probed = ExportFormat.of(context.contentResolver, uri)
                                val effective = if (probed == ExportFormat.UNKNOWN || probed == ExportFormat.PDF) ExportFormat.JPEG else probed
                                val bytes = encodeImage(context, uri, bmp, effective, quality)
                                context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(bytes) }
                                    ?: throw IllegalStateException("Could not open the original photo for writing")
                                bytes.size.toLong()
                            } finally {
                                bmp.recycle()
                            }
                        }
                        if (sourceUri == uri) sourceBytes = size
                        saved++
                    } catch (e: Exception) {
                        failed++
                    }
                }
                batchProgress = BatchProgress("Saving", i + 1, total, ((i + 1) * 100) / total)
            }
            busy = false
            busyLabel = ""
            batchProgress = null
            batchSaveNote = when {
                failed == 0 -> "All $saved originals replaced with the resized versions."
                saved == 0 -> "Replacing failed. Nothing was changed. Try 'Save as new files' instead."
                else -> "$saved of $total originals replaced; $failed failed."
            }
        }
    }

    fun onProcess() {
        val src = workingBitmap
        val dpi = currentDpi()
        val w = parseToPx(inputWidth, sizeUnit, dpi)
        val h = parseToPx(inputHeight, sizeUnit, dpi)
        if (src == null) {
            scope.launch { snackbar.showSnackbar("Select a photo first.") }
            return
        }
        if (w == null || h == null) {
            scope.launch { snackbar.showSnackbar("Enter the width and height first.") }
            return
        }
        val valid = runCatching { ResizeMath.validate(w, h) }.getOrElse {
            scope.launch { snackbar.showSnackbar(it.message ?: "Invalid dimensions") }
            return
        }
        if (Transforms.sameAspect(src.width, src.height, valid.width, valid.height)) {
            runProcess(valid.width, valid.height, AdaptMode.STRETCH, CropAnchor.CENTER, FitPad.BLUR)
        } else {
            adaptRequest = AdaptRequest(valid.width, valid.height)
        }
    }

    fun runSingleChoice(choice: SheetChoice) {
        val bmp = workingBitmap ?: return
        when (choice) {
            is SheetChoice.FileTarget -> runFitToFileSize(choice.bytes)
            else -> {
                val target = choiceTargetFor(choice, bmp.width, bmp.height) ?: return
                applyTarget(target.first, target.second)
                onProcess()
            }
        }
    }

    fun runBatchResize(choice: SheetChoice) {
        val uris = photoQueue
        if (uris.size < 2) { runSingleChoice(choice); return }
        busy = true
        busyLabel = "Processing…"
        val total = uris.size
        batchProgress = BatchProgress("Processing", 0, total, 0)
        scope.launch {
            var done = 0
            var failed = 0
            uris.forEachIndexed { i, uri ->
                batchProgress = BatchProgress("Processing", i + 1, total, (i * 100) / total)
                try {
                    val outcome = withContext(Dispatchers.Default) {
                        val decoded = decodeOrientedBitmap(context, uri)
                        val result = when (choice) {
                            is SheetChoice.FileTarget -> fitToFileSize(decoded, outFormat(), quality, choice.bytes) { sub ->
                                batchProgress = BatchProgress("Processing", i + 1, total, ((i * 100) + sub) / total)
                            }
                            else -> {
                                val target = choiceTargetFor(choice, decoded.width, decoded.height)
                                    ?: throw IllegalStateException("Invalid resize target")
                                val outBmp = if (Transforms.sameAspect(decoded.width, decoded.height, target.first, target.second))
                                    Transforms.stretch(decoded, target.first, target.second)
                                else Transforms.fit(decoded, target.first, target.second, FitPad.BLUR)
                                outBmp to estimateBytes(outBmp, outFormat(), quality)
                            }
                        }
                        // Keep resized pixels on disk, not in memory: a queue of full-size
                        // bitmaps can exceed the app's heap and crash the save step.
                        val file = File(File(context.cacheDir, "batch").apply { mkdirs() }, "resized_${i}_${System.currentTimeMillis()}.png")
                        FileOutputStream(file).use { result.first.compress(Bitmap.CompressFormat.PNG, 100, it) }
                        val dims = result.first.width to result.first.height
                        if (result.first !== decoded) result.first.recycle()
                        decoded.recycle()
                        Triple(file.absolutePath, dims, result.second)
                    }
                    val old = queueInfo[uri.toString()]
                    processedResults[uri.toString()] = ProcessedResult(outcome.first, outcome.second.first, outcome.second.second, outcome.third, old?.bytes ?: -1L, old?.width ?: 0, old?.height ?: 0)
                    // Remember the photo and cache the EDITED image as its thumbnail, so
                    // Home > Recently edited and My Photos > Recent show real pictures even
                    // when the picker grant can no longer be read later.
                    withContext(Dispatchers.IO) {
                        runCatching {
                            RecentsStore.add(context, uri)
                            RecentsStore.cacheThumbnailFromFile(context, uri, File(outcome.first))
                        }
                    }
                    done++
                } catch (e: Exception) {
                    failed++
                }
                batchProgress = BatchProgress("Processing", i + 1, total, ((i + 1) * 100) / total)
            }
            if (done > 0) {
                processedResults[uris.first().toString()]?.let { firstStored ->
                    if (choice !is SheetChoice.FileTarget) {
                        lastUsedTarget = firstStored.width to firstStored.height
                        saveLastUsedTarget(context, firstStored.width, firstStored.height)
                    }
                }
                sourceUri?.toString()?.let { key ->
                    processedResults[key]?.let { current ->
                        val cached = withContext(Dispatchers.IO) { BitmapFactory.decodeFile(current.cachePath) }
                        if (cached != null) {
                            applyTarget(current.width, current.height)
                            processedBitmap = cached
                            estimatedBytes = current.bytes
                            beforeAfter = true
                        }
                    }
                }
                screen = Screen.QueueResult
            }
            busy = false
            busyLabel = ""
            batchProgress = null
            when {
                // Full success: the green result card already says it - no snackbar (user asked).
                done > 0 && failed == 0 -> Unit
                done > 0 -> snackbar.showSnackbar("$done of $total photos processed ($failed failed).")
                else -> snackbar.showSnackbar("Couldn't process these photos. Try smaller dimensions.")
            }
        }
    }

    fun finishSave(name: String, sizeBytes: Long) {
        sourceUri?.let { uri ->
            scope.launch(Dispatchers.IO) { DraftsStore.remove(context, uri.toString()) }
        }
        savedName = name
        savedSize = formatBytes(sizeBytes)
        screen = Screen.Success
    }

    fun doSaveAsNew() {
        busy = true
        busyLabel = "Saving…"
        scope.launch {
            try {
                val fmt = outFormat()
                val bytes = withContext(Dispatchers.IO) { encodeProcessed(fmt) }
                val filename = buildFileName(exportName, fmt)
                withContext(Dispatchers.IO) {
                    if (fmt == ExportFormat.PDF) saveToDownloads(context, filename, fmt.mimeType, bytes)
                    else {
                        val savedUri = saveToGallery(context, filename, fmt.mimeType, bytes)
                        processedBitmap?.let { bmp -> runCatching { RecentsStore.cacheThumbnailFromBitmap(context, savedUri, bmp) } }
                    }
                }
                finishSave(filename, bytes.size.toLong())
            } catch (e: Exception) {
                snackbar.showSnackbar("Save failed. Please try again.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun saveAsNew() {
        if (Build.VERSION.SDK_INT <= 28 &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            legacySaveAfterGrant = true
            pendingLaunch = "legacySave"

        } else {
            doSaveAsNew()
        }
    }

    fun performReplace() {
        val uri = sourceUri ?: return
        busy = true
        busyLabel = "Replacing original…"
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { encodeProcessed(sourceOutFormat()) }
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(bytes) }
                        ?: throw IllegalStateException("Could not open the original photo for writing")
                }
                sourceBytes = bytes.size.toLong()
                finishSave("Original photo updated", bytes.size.toLong())
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't replace the original photo. Try 'Save as new file' instead.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun requestReplaceOriginal() {
        if (sourceUri == null) return
        if (Build.VERSION.SDK_INT >= 30) {
            pendingLaunch = "replace"
        } else {
            scope.launch { snackbar.showSnackbar("Replace original needs Android 11 or newer. Use 'Save as new file' instead.") }
        }
    }

    fun saveToChosenFolder(target: Uri) {
        busy = true
        busyLabel = "Saving…"
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { encodeProcessed() }
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(target, "wt")?.use { it.write(bytes) }
                        ?: throw IllegalStateException("Could not write to the chosen location")
                }
                finishSave(buildFileName(exportName, outFormat()), bytes.size.toLong())
            } catch (e: Exception) {
                snackbar.showSnackbar("Save failed. Please try again.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun launchSaveToFolder() {
        pendingLaunch = "saf"
    }

    fun shareProcessed() {
        busy = true
        busyLabel = "Preparing share…"
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { encodeProcessed() }
                val shareUri = withContext(Dispatchers.IO) {
                    val dir = File(context.cacheDir, "images").apply { mkdirs() }
                    val file = File(dir, "share_${System.currentTimeMillis()}.${outFormat().extension}")
                    FileOutputStream(file).use { it.write(bytes) }
                    FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = outFormat().mimeType
                    putExtra(Intent.EXTRA_STREAM, shareUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Share photo"))
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't prepare the photo for sharing.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun openCamera() {
        pendingLaunch = "camera"
    }

    val multiPickerUnlimited = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.size == 1) {
            loadPhoto(uris[0], isCamera = false)
        } else if (uris.size > 1) {
            photoQueue = uris
            queueIndex = 0
            clearProcessedResults()
            screen = Screen.QueueOverview
            scope.launch(Dispatchers.IO) {
                uris.forEach { u ->
                    runCatching { RecentsStore.add(context, u) }
                    val t = decodeThumbnail(context, u)
                    if (t != null) {
                        queueThumbs[u.toString()] = t.asImageBitmap()
                        // Cache the SAME bitmap that just decoded successfully, so the
                        // recents tile never depends on a second decode of the picker URI.
                        runCatching { RecentsStore.cacheThumbnailFromBitmap(context, u, t) }
                    }
                }
            }
            scope.launch { snackbar.showSnackbar("${uris.size} photos selected. Tap Resize to process them all together.") }
        }
    }

    fun saveDraftIfNeeded() {
        val uri = sourceUri
        if (workingBitmap != null && uri != null && !sourceIsCamera) {
            val dpi = currentDpi()
            val draft = Draft(
                uri.toString(),
                parseToPx(inputWidth, sizeUnit, dpi)?.toString() ?: "",
                parseToPx(inputHeight, sizeUnit, dpi)?.toString() ?: "",
                lockRatio, quality, System.currentTimeMillis()
            )
            scope.launch(Dispatchers.IO) { DraftsStore.upsert(context, draft) }
        }
    }

    fun leaveEditorToHome() {
        saveDraftIfNeeded()
        screen = Screen.Home
    }

    fun pickPhotos() {
        pendingLaunch = "pick"
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) loadPhoto(uri, isCamera = false)
    }
    val multiPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(50)) { uris ->
        if (uris.size == 1) {
            loadPhoto(uris[0], isCamera = false)
        } else if (uris.size > 1) {
            photoQueue = uris
            queueIndex = 0
            clearProcessedResults()
            screen = Screen.QueueOverview
            scope.launch(Dispatchers.IO) {
                uris.forEach { u ->
                    runCatching { RecentsStore.add(context, u) }
                    val t = decodeThumbnail(context, u)
                    if (t != null) {
                        queueThumbs[u.toString()] = t.asImageBitmap()
                        // Cache the SAME bitmap that just decoded successfully, so the
                        // recents tile never depends on a second decode of the picker URI.
                        runCatching { RecentsStore.cacheThumbnailFromBitmap(context, u, t) }
                    }
                }
            }
            scope.launch { snackbar.showSnackbar("${uris.size} photos selected. Tap Resize to process them all together.") }
        }
    }
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val uri = pendingCameraUri
        if (ok && uri != null) loadPhoto(uri, isCamera = true)
        pendingCameraUri = null
    }
    val writeConsent = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) performReplace()
        else scope.launch { snackbar.showSnackbar("Replace cancelled. Nothing was changed.") }
    }
    val writeConsentBatch = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) performBatchReplace()
        else scope.launch { snackbar.showSnackbar("Replace cancelled. Nothing was changed.") }
    }
    val createJpeg = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/jpeg")) { uri ->
        if (uri != null) saveToChosenFolder(uri)
    }
    val createPng = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        if (uri != null) saveToChosenFolder(uri)
    }
    val createWebp = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/webp")) { uri ->
        if (uri != null) saveToChosenFolder(uri)
    }
    val createPdf = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) saveToChosenFolder(uri)
    }
    val legacyWritePermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && legacySaveAfterGrant) {
            legacySaveAfterGrant = false
            doSaveAsNew()
        } else if (!granted) {
            legacySaveAfterGrant = false
            scope.launch { snackbar.showSnackbar("Storage access was not granted, so the photo was not saved.") }
        }
    }

    LaunchedEffect(pendingLaunch) {
        when (pendingLaunch) {
            "pick" -> {
                try {
                    multiPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } catch (e: IllegalArgumentException) {
                    // Very old pickers enforce a lower system maximum; fall back to no app-side limit.
                    multiPickerUnlimited.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
            "camera" -> {
                val dir = File(context.cacheDir, "images").apply { mkdirs() }
                val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                pendingCameraUri = uri
                takePicture.launch(uri)
            }
            "replace" -> {
                val uri = sourceUri
                if (uri != null && Build.VERSION.SDK_INT >= 30) {
                    val pendingIntent = MediaStore.createWriteRequest(context.contentResolver, listOf(uri))
                    writeConsent.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
                }
            }
            "saf" -> {
                val suggested = buildFileName(exportName, outFormat())
                when (outFormat()) {
                    ExportFormat.JPEG -> createJpeg.launch(suggested)
                    ExportFormat.PNG -> createPng.launch(suggested)
                    ExportFormat.WEBP -> createWebp.launch(suggested)
                    ExportFormat.PDF -> createPdf.launch(suggested)
                    ExportFormat.UNKNOWN -> createJpeg.launch(suggested)
                }
            }
            "replaceBatch" -> {
                try {
                    if (Build.VERSION.SDK_INT >= 30 && photoQueue.isNotEmpty()) {
                        val pendingIntent = MediaStore.createWriteRequest(context.contentResolver, photoQueue)
                        writeConsentBatch.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
                    }
                } catch (e: Exception) {
                    scope.launch { snackbar.showSnackbar("Replace isn't available for these photos. Use 'Save as new files' instead.") }
                }
            }
            "legacySave" -> legacyWritePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        pendingLaunch = null
    }

    LaunchedEffect(chosenFormat) {
        val processed = processedBitmap
        if (processed != null) {
            estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(processed, outFormat(), quality) }
        }
    }

    LaunchedEffect(screen) {
        if (screen == Screen.Home) {
            recents = withContext(Dispatchers.IO) { RecentsStore.list(context) }
        }
    }
    LaunchedEffect(photoQueue) {
        photoQueue.forEach { uri ->
            val key = uri.toString()
            if (queueThumbs[key] == null) {
                val thumb = withContext(Dispatchers.IO) { decodeThumbnail(context, uri) }
                if (thumb != null) queueThumbs[key] = thumb.asImageBitmap()
            }
            if (!queueInfo.containsKey(key)) {
                val info = withContext(Dispatchers.IO) { probeImageInfo(context, uri) }
                if (info != null) queueInfo[key] = info
            }
        }
    }
    LaunchedEffect(Unit) {
        lastUsedTarget = loadLastUsedTarget(context)
    }
    LaunchedEffect(recents) {
        recents.toList().forEach { uri ->
            val key = uri.toString()
            if (!recentThumbs.containsKey(key)) {
                recentThumbs[key] = null
                // One child coroutine per photo: a slow or hanging source can only delay
                // its own tile, never the other tiles or the pruning of dead entries.
                launch {
                    val thumb = withContext(Dispatchers.IO) { RecentsStore.loadThumbnail(context, uri) }
                    if (thumb != null) {
                        recentThumbs[key] = thumb.asImageBitmap()
                    } else {
                        // No cached thumbnail and an unreadable source means a dead picker
                        // grant or a deleted photo: drop the entry instead of showing a
                        // permanent placeholder.
                        withContext(Dispatchers.IO) { RecentsStore.remove(context, uri) }
                        recents = recents.filterNot { it == uri }
                    }
                }
            }
        }
    }

    BackHandler(enabled = screen != Screen.Home) {
        when (screen) {
            Screen.Export -> screen = Screen.Editor
            Screen.QueueResult -> { screen = Screen.Home; tab = MainTab.Home }
            Screen.Editor -> leaveEditorToHome()
            else -> screen = Screen.Home
        }
    }

    if (showDimensionsSheet) {
        val bmp = workingBitmap
        DimensionsSheet(
            title = "Select new dimensions",
            baseWidth = bmp?.width ?: 0,
            baseHeight = bmp?.height ?: 0,
            baseBytes = sourceBytes,
            baseBitmap = bmp,
            format = outFormat(),
            quality = quality,
            lastUsed = lastUsedTarget,
            fromQueue = photoQueue.size > 1,
            initialUnit = sizeUnit,
            initialDpi = resizeDpi,
            onDismiss = { showDimensionsSheet = false },
            onApply = { choice ->
                showDimensionsSheet = false
                if (photoQueue.size > 1) runBatchResize(choice) else runSingleChoice(choice)
            }
        )
    }

    batchProgress?.let { p ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text("${p.action} photos", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("${p.action} ${p.current}/${p.total} - ${p.percent}%", color = DeepNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(PaleBlue)) {
                        Box(Modifier.fillMaxWidth((p.percent.coerceIn(0, 100)) / 100f).height(10.dp).clip(RoundedCornerShape(5.dp)).background(SkyBlue))
                    }
                }
            },
            confirmButton = { }
        )
    }
    batchSaveNote?.let { note ->
        AlertDialog(
            onDismissRequest = { batchSaveNote = null },
            title = { Text("Save", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = { Text(note, color = Slate, fontSize = 13.sp, lineHeight = 18.sp) },
            confirmButton = { TextButton(onClick = { batchSaveNote = null }) { Text("OK", color = DeepNavy, fontWeight = FontWeight.Bold) } }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            if (screen == Screen.Editor || screen == Screen.Export || screen == Screen.Print || screen == Screen.QueueOverview || screen == Screen.QueueResult || screen == Screen.Success) {
                TopAppBar(
                    title = {
                        Text(
                            when (screen) {
                                Screen.Editor -> "Resize Image"
                                Screen.Export -> "Export"
                                Screen.QueueOverview -> "Photos to resize"
                                Screen.QueueResult -> "Resized photos"
                                Screen.Success -> "Saved"
                                else -> "Print Size & DPI"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when (screen) {
                                Screen.Export -> screen = Screen.Editor
                                Screen.QueueResult -> { screen = Screen.Home; tab = MainTab.Home }
                                Screen.Editor -> leaveEditorToHome()
                                else -> screen = Screen.Home
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (screen == Screen.QueueResult) {
                            Text(
                                "Done",
                                modifier = Modifier
                                    .clickable { screen = Screen.Home; tab = MainTab.Home }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (screen == Screen.Editor) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { sourceUri?.let { loadPhoto(it, sourceIsCamera, fromQueue = photoQueue.isNotEmpty()) } }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (screen == Screen.Home) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(selected = tab == MainTab.Home, onClick = { tab = MainTab.Home }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                    NavigationBarItem(selected = tab == MainTab.MyPhotos, onClick = { tab = MainTab.MyPhotos }, icon = { Icon(Icons.Default.PhotoLibrary, null) }, label = { Text("My Photos") })
                    NavigationBarItem(selected = tab == MainTab.Presets, onClick = { tab = MainTab.Presets }, icon = { Icon(Icons.Default.Collections, null) }, label = { Text("Presets") })
                    NavigationBarItem(selected = tab == MainTab.Settings, onClick = { tab = MainTab.Settings }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
                }
            }
        }
    ) { padding ->
        when (screen) {
            Screen.Home -> when (tab) {
                MainTab.Home -> HomeScreen(
                    modifier = Modifier.padding(padding),
                    recents = recents,
                    recentThumbs = recentThumbs,
                    onSelectPhotos = { pickPhotos() },
                    onTakePhoto = { openCamera() },
                    onRecent = { uri -> loadPhoto(uri, isCamera = false) },
                    onPrint = { screen = Screen.Print },
                    onFeature = { feature ->
                        when (feature) {
                            "Resize" -> pickPhotos()
                            "Print Size & DPI" -> screen = Screen.Print
                            "Social & Shop Sizes" -> tab = MainTab.Presets
                            else -> scope.launch { snackbar.showSnackbar("$feature is planned for a later development stage.") }
                        }
                    }
                )
                MainTab.MyPhotos -> MyPhotosScreen(
                    modifier = Modifier.padding(padding),
                    recents = recents,
                    recentThumbs = recentThumbs,
                    onOpenPhoto = { uri -> loadPhoto(uri, isCamera = false) },
                    onOpenDraft = { draft ->
                        pendingDraft = draft
                        loadPhoto(Uri.parse(draft.sourceUri), isCamera = false)
                    }
                )
                MainTab.Presets -> PresetsScreen(
                    modifier = Modifier.padding(padding),
                    onPreset = { preset ->
                        if (workingBitmap != null) {
                            applyTarget(preset.width, preset.height)
                            screen = Screen.Editor
                            tool = Tool.Resize
                            scope.launch { snackbar.showSnackbar("Preset applied: ${preset.name} (${preset.width} × ${preset.height} px).") }
                        } else {
                            pendingPreset = preset
                            pickPhotos()
                        }
                    }
                )
                MainTab.Settings -> SettingsScreen(Modifier.padding(padding), darkMode = darkMode, onDarkMode = onDarkMode)
            }
            Screen.QueueOverview -> QueueOverviewScreen(
                modifier = Modifier.padding(padding),
                queueUris = photoQueue,
                queueThumbs = queueThumbs,
                queueInfo = queueInfo,
                currentFormatKey = chosenFormat,
                sharedTargetLabel = if (queueTargetW.isNotBlank() && queueTargetH.isNotBlank()) "$queueTargetW × $queueTargetH px" else "",
                namePattern = queueNamePattern,
                baseFormat = outFormat(),
                quality = quality,
                lastUsed = lastUsedTarget,
                onPhoto = { index ->
                    queueIndex = index
                    loadPhoto(photoQueue[index], isCamera = false, fromQueue = true)
                },
                onApplyTargetAll = { w, h -> queueTargetW = w.toString(); queueTargetH = h.toString() },
                onFormatAll = { chosenFormat = it },
                onPatternAll = { queueNamePattern = it },
                onProcessAll = { choice -> runBatchResize(choice) }
            )
            Screen.QueueResult -> QueueResultScreen(
                modifier = Modifier.padding(padding),
                queueUris = photoQueue,
                queueThumbs = queueThumbs,
                results = processedResults,
                currentFormatKey = chosenFormat,
                namePattern = queueNamePattern,
                baseWidth = queueInfo[photoQueue.firstOrNull()?.toString()]?.width ?: 0,
                baseHeight = queueInfo[photoQueue.firstOrNull()?.toString()]?.height ?: 0,
                baseBytes = queueInfo[photoQueue.firstOrNull()?.toString()]?.bytes ?: -1L,
                format = outFormat(),
                quality = quality,
                lastUsed = lastUsedTarget,
                onPhoto = { index ->
                    queueIndex = index
                    loadPhoto(photoQueue[index], isCamera = false, fromQueue = true)
                },
                onResizeAgain = { choice -> runBatchResize(choice) },
                onFormatAll = { chosenFormat = it },
                onPatternAll = { queueNamePattern = it },
                onSaveAsNew = { runBatchSave() },
                onReplaceAll = { requestBatchReplace() }
            )
            Screen.Editor -> EditorScreen(
                modifier = Modifier.padding(padding),
                queueLabel = if (photoQueue.size > 1) "Photo ${queueIndex + 1} of ${photoQueue.size}" else null,
                queueUris = photoQueue,
                queueIndex = queueIndex,
                queueThumbs = queueThumbs,
                onQueueSelect = { index ->
                    queueIndex = index
                    loadPhoto(photoQueue[index], isCamera = false, fromQueue = true)
                },
                working = workingBitmap,
                processed = processedBitmap,
                sizeUnit = sizeUnit,
                resizeDpi = resizeDpi,
                busy = busy,
                busyLabel = busyLabel,
                beforeAfter = beforeAfter,
                tool = tool,
                sourceBytes = sourceBytes,
                estimatedBytes = estimatedBytes,
                onBeforeAfter = { beforeAfter = it },
                onSizeUnit = { convertInputsToUnit(sizeUnit, it) },
                onResizeDpi = { resizeDpi = it },
                onTool = { selected ->
                    tool = selected
                    when (selected) {
                        Tool.Resize -> Unit
                        Tool.Presets -> { saveDraftIfNeeded(); screen = Screen.Home; tab = MainTab.Presets }
                        else -> scope.launch { snackbar.showSnackbar("${selected.label} is planned for a later stage. Resize and export are available now.") }
                    }
                },
                onRotate = {
                    val src = workingBitmap
                    if (src != null) {
                        val rotated = rotateRight(src)
                        workingBitmap = rotated
                        applyTarget(rotated.width, rotated.height)
                        scope.launch { snackbar.showSnackbar("Rotated 90°. Tap Process when the size is right.") }
                    }
                },
                onProcess = { showDimensionsSheet = true },
                onExport = { screen = Screen.Export }
            )
            Screen.Export -> ExportScreen(
                modifier = Modifier.padding(padding),
                processed = processedBitmap,
                source = workingBitmap,
                sourceBytes = sourceBytes,
                sourceWidth = workingBitmap?.width ?: 0,
                sourceHeight = workingBitmap?.height ?: 0,
                estimatedBytes = estimatedBytes,
                exportName = exportName,
                onName = { exportName = it },
                format = outFormat(),
                chosenFormat = chosenFormat,
                originalFormatLabel = if (sourceFormat == ExportFormat.UNKNOWN) "Original (JPG)" else "Original (${sourceFormat.name})",
                onChosenFormat = { chosenFormat = it },
                quality = quality,
                onQuality = { quality = it },
                onQualitySettled = {
                    val processed = processedBitmap
                    if (processed != null) {
                        scope.launch {
                            estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(processed, outFormat(), quality) }
                        }
                    }
                },
                canReplace = SaveUtils.canReplaceOriginal(sourceIsCamera, sourceFormat),
                replaceHint = when {
                    sourceIsCamera -> "Camera captures are new photos, so there is no gallery original to replace."
                    Build.VERSION.SDK_INT < 30 -> "Replace original needs Android 11 or newer."
                    else -> "Replace the photo you picked with this edited version. Your gallery app will ask you to confirm."
                } + " Replace original always keeps the original file format.",
                busy = busy,
                busyLabel = busyLabel,
                onSaveNew = { saveAsNew() },
                onReplace = { requestReplaceOriginal() },
                onSaveToFolder = { launchSaveToFolder() },
                onShare = { shareProcessed() }
            )
            Screen.Print -> PrintCalculator(
                modifier = Modifier.padding(padding),
                hasPhoto = workingBitmap != null,
                onApply = { pxW, pxH ->
                    if (workingBitmap != null) {
                        applyTarget(pxW, pxH)
                        screen = Screen.Editor
                        tool = Tool.Resize
                    } else {
                        scope.launch { snackbar.showSnackbar("Select a photo to apply a print-size calculation.") }
                    }
                }
            )
            Screen.Success -> SuccessScreen(
                modifier = Modifier.padding(padding),
                name = savedName,
                size = savedSize,
                nextLabel = if (queueIndex + 1 < photoQueue.size) "Next photo (${queueIndex + 2} of ${photoQueue.size})" else null,
                onNext = {
                    if (queueIndex + 1 < photoQueue.size) {
                        queueIndex += 1
                        loadPhoto(photoQueue[queueIndex], isCamera = false, fromQueue = true)
                    }
                },
                onDone = {
                    screen = Screen.Home
                    tab = MainTab.Home
                    workingBitmap = null
                    processedBitmap = null
                    sourceUri = null
                    photoQueue = emptyList()
                    queueIndex = 0
                    clearProcessedResults()
                },
                onAnother = {
                    screen = Screen.Home
                    tab = MainTab.Home
                    workingBitmap = null
                    processedBitmap = null
                    sourceUri = null
                    photoQueue = emptyList()
                    queueIndex = 0
                    clearProcessedResults()
                    pickPhotos()
                }
            )
        }
    }

    adaptRequest?.let { request ->
        workingBitmap?.let { src ->
            AspectRatioDialog(
                source = src,
                request = request,
                onCancel = { adaptRequest = null },
                onChoose = { mode, anchor, pad -> runProcess(request.width, request.height, mode, anchor, pad) }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier,
    recents: List<Uri>,
    recentThumbs: Map<String, ImageBitmap?>,
    onSelectPhotos: () -> Unit,
    onTakePhoto: () -> Unit,
    onRecent: (Uri) -> Unit,
    onPrint: () -> Unit,
    onFeature: (String) -> Unit
) {
    val features = listOf(
        Feature("Resize", "Change image dimensions", "↔", true),
        Feature("Remove Background", "Cut out a product or person", "✦"),
        Feature("Crop & Rotate", "Adjust framing and angle", "⌗"),
        Feature("Compress", "Reduce file size", "↓"),
        Feature("Convert Format", "JPEG, PNG and WebP", "⇄"),
        Feature("Social & Shop Sizes", "Creator-ready dimensions", "▦"),
        Feature("Batch Resize", "Process many photos", "▤"),
        Feature("Print Size & DPI", "Calculate print dimensions", "◉")
    )
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("A2Z Image Craft", color = DeepNavy, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("IMAGE RESIZER", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Icon(Icons.Default.Tune, contentDescription = "Settings", tint = DeepNavy, modifier = Modifier.size(26.dp))
        }
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Resize. Remove. Convert. Create.", color = DeepNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                Spacer(Modifier.height(14.dp))
                Button(onClick = onSelectPhotos, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Default.Image, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Select Photos", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(52.dp), shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = DeepNavy); Spacer(Modifier.width(8.dp)); Text("Take a Photo", color = DeepNavy, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (recents.isNotEmpty()) {
            Text("Recently edited", modifier = Modifier.padding(top = 18.dp, bottom = 8.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepNavy)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(recents, key = { it.toString() }) { uri ->
                    val thumb = recentThumbs[uri.toString()]
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftGrey)
                            .border(1.dp, BorderGrey, RoundedCornerShape(12.dp))
                            .clickable { onRecent(uri) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (thumb != null) {
                            Image(bitmap = thumb, contentDescription = "Recently edited photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Slate, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }
        }
        Text("Your creator toolkit", modifier = Modifier.padding(top = 20.dp, bottom = 10.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepNavy)
        features.chunked(2).forEach { rowItems ->
            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { feature ->
                    Card(
                        modifier = Modifier.weight(1f).clickable { onFeature(feature.title) },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGrey),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(12.dp).height(106.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Box(Modifier.size(34.dp).clip(CircleShape).background(PaleBlue), contentAlignment = Alignment.Center) { Text(feature.icon, color = SkyBlue, fontWeight = FontWeight.Bold, fontSize = 19.sp) }
                            Text(feature.title, color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 17.sp)
                            Text(feature.description, color = Slate, fontSize = 11.sp, lineHeight = 14.sp)
                        }
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Text("Stage 2 · Resize, adapt, rotate and export are live", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), color = Slate, fontSize = 11.sp)
    }
}

@Composable
private fun RowScope.QueueAction(label: String, enabled: Boolean, icon: ImageVector, onClick: () -> Unit) {
    Column(
        Modifier.weight(1f).padding(horizontal = 3.dp).clip(RoundedCornerShape(10.dp)).background(if (enabled) SkyBlue else PaleBlue).clickable(enabled = enabled) { onClick() }.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) Color.White else Slate, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(3.dp))
        Text(label, color = if (enabled) Color.White else Slate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
    }
}

@Composable
private fun SheetSectionHeader(label: String, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(PaleBlue).clickable { onToggle() }.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(if (expanded) "\u25B2" else "\u25BC", color = Slate, fontSize = 12.sp)
    }
}

@Composable
private fun SheetRadioRow(label: String, info: String, selected: Boolean, onSelect: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onSelect() }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = DeepNavy, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, softWrap = false)
        if (info.isNotBlank()) Text(info, color = Slate, fontSize = 12.sp, maxLines = 1, softWrap = false)
        Spacer(Modifier.width(10.dp))
        Box(Modifier.size(20.dp).border(2.dp, SkyBlue, CircleShape).padding(4.dp)) {
            if (selected) Box(Modifier.fillMaxSize().clip(CircleShape).background(SkyBlue))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DimensionsSheet(
    title: String,
    baseWidth: Int,
    baseHeight: Int,
    baseBytes: Long,
    baseBitmap: Bitmap?,
    format: ExportFormat,
    quality: Int,
    lastUsed: Pair<Int, Int>?,
    fromQueue: Boolean,
    initialUnit: String = "px",
    initialDpi: String = "300",
    onDismiss: () -> Unit,
    onApply: (SheetChoice) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    fun scaled(maxSide: Int): Pair<Int, Int>? {
        if (baseWidth <= 0 || baseHeight <= 0) return null
        val longest = maxOf(baseWidth, baseHeight)
        val target = minOf(maxSide, longest)
        val scale = target.toDouble() / longest
        return maxOf(1, (baseWidth * scale).toInt()) to maxOf(1, (baseHeight * scale).toInt())
    }
    val easy = listOf("Small" to 1024, "Medium" to 1920, "Large" to 2560)
    val percents = listOf(25, 30, 40, 50, 60, 75, 80, 90)
    val fileSizes = listOf(50 to "50 kB", 100 to "100 kB", 250 to "250 kB", 500 to "500 kB", 750 to "750 kB", 1024 to "1 MB", 1536 to "1.5 MB", 2048 to "2 MB", 2560 to "2.5 MB")
    val resPresets = if (baseWidth <= 0 || baseHeight <= 0) emptyList()
        else listOf(480, 600, 768, 1024, 1152, 1536, 2048, 3072).filter { it < baseWidth }.map { w -> w to maxOf(1, (w.toDouble() * baseHeight / baseWidth).toInt()) }
    var choice by rememberSaveable { mutableStateOf("") }
    var lastExpanded by rememberSaveable { mutableStateOf(false) }
    var easyExpanded by rememberSaveable { mutableStateOf(true) }
    var pctExpanded by rememberSaveable { mutableStateOf(false) }
    var resExpanded by rememberSaveable { mutableStateOf(false) }
    var fsExpanded by rememberSaveable { mutableStateOf(false) }
    var customPct by rememberSaveable { mutableStateOf("45") }
    var customW by rememberSaveable { mutableStateOf("") }
    var customH by rememberSaveable { mutableStateOf("") }
    // Plain remember keyed on the carried-in unit: every time this sheet opens it
    // starts on the editor's CURRENT unit (rememberSaveable kept the first-ever value).
    var customUnit by remember(initialUnit) { mutableStateOf(initialUnit) }
    var customDpi by remember(initialDpi) { mutableStateOf(initialDpi) }
    // The whole sheet DISPLAYS dimensions in the unit carried in from the editor
    // (user request: pick inch on the editor -> the sheet shows inches everywhere).
    // Processing stays in pixels; only the labels are converted.
    val dispDpi = initialDpi.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 } ?: 300.0
    fun dimLabel(w: Int, h: Int): String =
        if (initialUnit == "px") "$w \u00D7 $h px"
        else "${fmtUnit(w.toDouble(), initialUnit, dispDpi)} \u00D7 ${fmtUnit(h.toDouble(), initialUnit, dispDpi)} $initialUnit"
    val unitWord = when (initialUnit) { "px" -> "pixels"; "in" -> "inches"; "cm" -> "cm"; else -> "mm" }
    var easyEstimates by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    LaunchedEffect(baseBitmap, baseWidth, baseHeight, baseBytes, format, quality) {
        easyEstimates = easy.mapNotNull { (label, side) ->
            val dims = scaled(side) ?: return@mapNotNull null
            val est = if (baseBitmap != null) {
                withContext(Dispatchers.Default) {
                    val scaledBmp = Bitmap.createScaledBitmap(baseBitmap, dims.first, dims.second, true)
                    val value = estimateBytes(scaledBmp, format, quality)
                    if (scaledBmp != baseBitmap) scaledBmp.recycle()
                    value
                }
            } else if (baseBytes > 0 && baseWidth > 0 && baseHeight > 0) {
                val ratio = (dims.first.toDouble() * dims.second) / (baseWidth.toDouble() * baseHeight)
                (baseBytes * ratio).toLong()
            } else -1L
            if (est > 0) label to est else null
        }.toMap()
    }
    val customPctInt = customPct.trim().toIntOrNull()?.takeIf { it in 1..200 }
    val customDpiVal = customDpi.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 } ?: 300.0
    val customWpx = parseToPx(customW, customUnit, customDpiVal)
    val customHpx = parseToPx(customH, customUnit, customDpiVal)
    val sheetChoice: SheetChoice? = when {
        choice == "last" && lastUsed != null -> SheetChoice.Absolute(lastUsed.first, lastUsed.second)
        choice.startsWith("easy:") -> easy.firstOrNull { "easy:${it.first}" == choice }?.let { SheetChoice.Longest(it.second, it.first) }
        choice.startsWith("pct:") -> choice.removePrefix("pct:").toIntOrNull()?.let { SheetChoice.Percent(it) }
        choice == "pctcustom" -> customPctInt?.let { SheetChoice.Percent(it) }
        choice.startsWith("res:") -> resPresets.firstOrNull { "res:${it.first}x${it.second}" == choice }?.let { SheetChoice.Absolute(it.first, it.second) }
        choice == "rescustom" -> if (customWpx != null && customHpx != null) SheetChoice.Absolute(customWpx, customHpx) else null
        choice.startsWith("fs:") -> choice.removePrefix("fs:").toIntOrNull()?.let { kb -> fileSizes.firstOrNull { it.first == kb }?.let { SheetChoice.FileTarget(kb * 1024L, it.second) } }
        else -> null
    }
    val buttonLabel = when (val c = sheetChoice) {
        is SheetChoice.Absolute -> "Resize to ${dimLabel(c.w, c.h)}"
        is SheetChoice.Longest -> "Resize to ${c.label}"
        is SheetChoice.Percent -> "Resize to ${c.p}%"
        is SheetChoice.FileTarget -> "Shrink to ${c.label}"
        null -> "Resize"
    }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("\u2715", color = Slate, fontSize = 15.sp, modifier = Modifier.clip(CircleShape).clickable { onDismiss() }.padding(8.dp))
            }
            Spacer(Modifier.height(6.dp))
            if (lastUsed != null) {
                SheetSectionHeader("Last used", lastExpanded) { lastExpanded = !lastExpanded }
                if (lastExpanded) SheetRadioRow(dimLabel(lastUsed.first, lastUsed.second), "", choice == "last") { choice = "last" }
                Spacer(Modifier.height(10.dp))
            }
            SheetSectionHeader("Easy resize", easyExpanded) { easyExpanded = !easyExpanded }
            if (easyExpanded) {
                Text("Simple resize options", color = Slate, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp, top = 8.dp))
                easy.forEach { (label, side) ->
                    val est = easyEstimates[label]
                    val info = when {
                        est != null -> "~${formatBytes(est)}"
                        scaled(side) != null -> dimLabel(scaled(side)!!.first, scaled(side)!!.second)
                        else -> "\u2026"
                    }
                    SheetRadioRow(label, info, choice == "easy:$label") { choice = "easy:$label" }
                }
            }
            Spacer(Modifier.height(10.dp))
            SheetSectionHeader("Percentage", pctExpanded) { pctExpanded = !pctExpanded }
            if (pctExpanded) {
                Text("Resize to a % of the original resolution", color = Slate, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp, top = 8.dp))
                percents.forEach { p ->
                    val dims = if (baseWidth > 0 && baseHeight > 0) dimLabel((baseWidth * p / 100.0).toInt(), (baseHeight * p / 100.0).toInt()) else "\u2026"
                    SheetRadioRow("$p%", dims, choice == "pct:$p") { choice = "pct:$p" }
                }
                val customInfo = customPctInt?.let { p -> if (baseWidth > 0 && baseHeight > 0) "$p% | ${dimLabel((baseWidth * p / 100.0).toInt(), (baseHeight * p / 100.0).toInt())}" else "$p%" } ?: ""
                SheetRadioRow("Custom", customInfo, choice == "pctcustom") { choice = "pctcustom" }
                if (choice == "pctcustom") {
                    OutlinedTextField(
                        value = customPct,
                        onValueChange = { v -> customPct = v.filter { it.isDigit() }.take(3) },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        label = { Text("Custom percentage (1-200)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            SheetSectionHeader("Resolution", resExpanded) { resExpanded = !resExpanded }
            if (resExpanded) {
                Text("Set width & height in $unitWord", color = Slate, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp, top = 8.dp))
                resPresets.forEach { (w, h) ->
                    SheetRadioRow(dimLabel(w, h), "", choice == "res:${w}x${h}") { choice = "res:${w}x${h}" }
                }
                val resInfo = if (customWpx != null && customHpx != null) dimLabel(customWpx, customHpx) else ""
                SheetRadioRow("Custom", resInfo, choice == "rescustom") { choice = "rescustom" }
                if (choice == "rescustom") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        OutlinedTextField(value = customW, onValueChange = { customW = it }, modifier = Modifier.weight(1f), label = { Text("Width ($customUnit)") }, keyboardOptions = KeyboardOptions(keyboardType = if (customUnit == "px") KeyboardType.Number else KeyboardType.Decimal), singleLine = true)
                        OutlinedTextField(value = customH, onValueChange = { customH = it }, modifier = Modifier.weight(1f), label = { Text("Height ($customUnit)") }, keyboardOptions = KeyboardOptions(keyboardType = if (customUnit == "px") KeyboardType.Number else KeyboardType.Decimal), singleLine = true)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        listOf("px", "in", "cm", "mm").forEach { u ->
                            val sel = customUnit == u
                            Surface(modifier = Modifier.clickable { customUnit = u }, shape = RoundedCornerShape(9.dp), color = if (sel) SkyBlue else PaleBlue) {
                                Text(u, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 12.sp, color = if (sel) Color.White else DeepNavy, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                    // DPI shown under every unit (px included, same as the editor page) -
                    // he wants the field always visible even though px input ignores it.
                    OutlinedTextField(value = customDpi, onValueChange = { customDpi = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("DPI") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
            }
            Spacer(Modifier.height(10.dp))
            SheetSectionHeader("File Size", fsExpanded) { fsExpanded = !fsExpanded }
            if (fsExpanded) {
                Text("Resize to a target file size (quality and size adjust automatically)", color = Slate, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp, top = 8.dp))
                fileSizes.forEach { (kb, label) ->
                    SheetRadioRow(label, "", choice == "fs:$kb") { choice = "fs:$kb" }
                }
            }
            if (fromQueue) {
                Text("Applies to every photo in the queue, one after another. Percentage and easy options keep each photo's own shape; fixed sizes fit each photo with blurred padding when the shape differs.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 10.dp))
            }
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { sheetChoice?.let(onApply) },
                enabled = sheetChoice != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) { Text(buttonLabel, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QueueResultScreen(
    modifier: Modifier,
    queueUris: List<Uri>,
    queueThumbs: Map<String, ImageBitmap?>,
    results: Map<String, ProcessedResult>,
    currentFormatKey: String,
    namePattern: String,
    baseWidth: Int,
    baseHeight: Int,
    baseBytes: Long,
    format: ExportFormat,
    quality: Int,
    lastUsed: Pair<Int, Int>?,
    onPhoto: (Int) -> Unit,
    onResizeAgain: (SheetChoice) -> Unit,
    onFormatAll: (String) -> Unit,
    onPatternAll: (String) -> Unit,
    onSaveAsNew: () -> Unit,
    onReplaceAll: () -> Unit
) {
    var showResize by rememberSaveable { mutableStateOf(false) }
    var showConvert by rememberSaveable { mutableStateOf(false) }
    var showRename by rememberSaveable { mutableStateOf(false) }
    var showSaveOptions by rememberSaveable { mutableStateOf(false) }
    val entries = queueUris.map { u -> u to results[u.toString()] }
    val done = entries.filter { it.second != null }
    val oldTotal = done.mapNotNull { it.second?.oldBytes }.filter { it >= 0 }.sum()
    val newTotal = done.sumOf { it.second!!.bytes.coerceAtLeast(0) }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE9F9EF)), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("${done.size} photos have been resized", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    val savingText = if (oldTotal > 0 && newTotal > 0 && oldTotal >= newTotal) {
                        val saved = oldTotal - newTotal
                        val pct = (saved * 100 / oldTotal).toInt()
                        "${formatBytes(oldTotal)} \u2192 ${formatBytes(newTotal)}  |  Saved ${formatBytes(saved)} ($pct%)"
                    } else "New total: ${formatBytes(newTotal)}"
                    Text(savingText, color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f).padding(top = 10.dp)
        ) {
            items(entries.size) { index ->
                val (uri, r) = entries[index]
                Column(Modifier.clickable { onPhoto(index) }) {
                    Box(
                        Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(12.dp)).background(SoftGrey).border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val thumb = queueThumbs[uri.toString()]
                        if (thumb != null) {
                            Image(bitmap = thumb, contentDescription = "Photo ${index + 1}", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Text("${index + 1}", color = Slate, fontSize = 14.sp)
                        }
                    }
                    if (r != null) {
                        Text("${r.width} \u00D7 ${r.height} px", color = DeepNavy, fontSize = 11.sp, maxLines = 1, softWrap = false, modifier = Modifier.padding(top = 4.dp))
                        Row {
                            Text(if (r.bytes >= 0) formatBytes(r.bytes) else "\u2026", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, softWrap = false)
                            if (r.oldBytes >= 0) {
                                Spacer(Modifier.width(6.dp))
                                Text(formatBytes(r.oldBytes), color = Slate, fontSize = 11.sp, maxLines = 1, softWrap = false, style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough))
                            }
                        }
                    } else {
                        Text("Not processed", color = Slate, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 12.dp)) {
            QueueAction("Convert", true, Icons.Default.Sync) { showConvert = true }
            QueueAction("Rename", true, Icons.Default.Create) { showRename = true }
            QueueAction("Resize", true, Icons.Default.PhotoSizeSelectLarge) { showResize = true }
            QueueAction("Save", true, Icons.Default.CheckCircle) { showSaveOptions = true }
        }
    }
    if (showResize) {
        DimensionsSheet(
            title = "Resize all photos",
            baseWidth = baseWidth,
            baseHeight = baseHeight,
            baseBytes = baseBytes,
            baseBitmap = null,
            format = format,
            quality = quality,
            lastUsed = lastUsed,
            fromQueue = true,
            onDismiss = { showResize = false },
            onApply = { choice -> showResize = false; onResizeAgain(choice) }
        )
    }
    if (showSaveOptions) {
        AlertDialog(
            onDismissRequest = { showSaveOptions = false },
            title = { Text("Save resized photos", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = { Text("Save new copies in Pictures/PixelCraft. Your original photos stay untouched.", color = Slate, fontSize = 13.sp, lineHeight = 18.sp) },
            confirmButton = {
                Column {
                    Button(onClick = { showSaveOptions = false; onSaveAsNew() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(10.dp)) {
                        Text("Save as new files", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { showSaveOptions = false }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        Text("Cancel", color = Slate)
                    }
                }
            }
        )
    }
    if (showConvert) {
        AlertDialog(
            onDismissRequest = { showConvert = false },
            title = { Text("Convert format", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Used when you save. You can still change it per photo on its Export screen.", color = Slate, fontSize = 12.sp, lineHeight = 16.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp).horizontalScroll(rememberScrollState())) {
                        listOf("original" to "Original", "JPEG" to "JPG", "PNG" to "PNG", "WEBP" to "WEBP", "PDF" to "PDF").forEach { (value, label) ->
                            val selected = currentFormatKey == value
                            Surface(modifier = Modifier.clickable { onFormatAll(value); showConvert = false }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                                Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showConvert = false }) { Text("Close", color = Slate) } }
        )
    }
    if (showRename) {
        var pattern by rememberSaveable { mutableStateOf(namePattern) }
        AlertDialog(
            onDismissRequest = { showRename = false },
            title = { Text("Name pattern", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = pattern, onValueChange = { pattern = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Base name") }, singleLine = true)
                    Text(
                        if (pattern.isBlank()) "Leave empty to use automatic names."
                        else "Files: ${pattern.trim()}-1, ${pattern.trim()}-2, \u2026 (extension added automatically)",
                        color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 6.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onPatternAll(pattern.trim()); showRename = false }) { Text("Apply", color = DeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showRename = false }) { Text("Cancel", color = Slate) } }
        )
    }
}

@Composable
private fun QueueOverviewScreen(
    modifier: Modifier,
    queueUris: List<Uri>,
    queueThumbs: Map<String, ImageBitmap?>,
    queueInfo: Map<String, QueueItemInfo>,
    currentFormatKey: String,
    sharedTargetLabel: String,
    namePattern: String,
    baseFormat: ExportFormat,
    quality: Int,
    lastUsed: Pair<Int, Int>?,
    onPhoto: (Int) -> Unit,
    onApplyTargetAll: (Int, Int) -> Unit,
    onFormatAll: (String) -> Unit,
    onPatternAll: (String) -> Unit,
    onProcessAll: (SheetChoice) -> Unit
) {
    var showResize by rememberSaveable { mutableStateOf(false) }
    var showConvert by rememberSaveable { mutableStateOf(false) }
    var showRename by rememberSaveable { mutableStateOf(false) }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Image, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("${queueUris.size} photos", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    val infos = queueUris.mapNotNull { queueInfo[it.toString()] }
                    val ready = infos.size == queueUris.size && infos.all { it.bytes >= 0 }
                    Text(if (ready) formatBytes(infos.sumOf { it.bytes }) else "\u2026", color = Slate, fontSize = 13.sp)
                }
            }
        }
        val sharedBits = mutableListOf<String>()
        if (sharedTargetLabel.isNotBlank()) sharedBits += "Size: $sharedTargetLabel"
        if (currentFormatKey != "original") sharedBits += "Format: $currentFormatKey"
        if (namePattern.isNotBlank()) sharedBits += "Names: $namePattern-1, $namePattern-2, \u2026"
        if (sharedBits.isNotEmpty()) {
            Text(sharedBits.joinToString("  \u00B7  "), color = SkyBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f).padding(top = 10.dp)
        ) {
            items(queueUris.size) { index ->
                val key = queueUris[index].toString()
                val thumb = queueThumbs[key]
                val info = queueInfo[key]
                Column(Modifier.clickable { onPhoto(index) }) {
                    Box(
                        Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(12.dp)).background(SoftGrey).border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (thumb != null) {
                            Image(bitmap = thumb, contentDescription = "Photo ${index + 1}", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Text("${index + 1}", color = Slate, fontSize = 14.sp)
                        }
                    }
                    Text(info?.let { "${it.width} \u00D7 ${it.height} px" } ?: "\u2026", color = DeepNavy, fontSize = 11.sp, maxLines = 1, softWrap = false, modifier = Modifier.padding(top = 4.dp))
                    Text(info?.let { if (it.bytes >= 0) formatBytes(it.bytes) else "\u2026" } ?: "", color = Slate, fontSize = 11.sp, maxLines = 1, softWrap = false)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
            QueueAction("Convert", true, Icons.Default.Sync) { showConvert = true }
            QueueAction("Rename", true, Icons.Default.Create) { showRename = true }
            QueueAction("Resize", true, Icons.Default.PhotoSizeSelectLarge) { showResize = true }
            QueueAction("Save", false, Icons.Default.CheckCircle) { }
        }
        Text(
            "Resize processes every photo one after another and takes you to the results, where Save exports them all (new copies, or replacing the originals). You can also open any photo and export it on its own.",
            color = Slate,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
        )
    }
    if (showResize) {
        val first = queueUris.firstOrNull()?.toString()?.let { queueInfo[it] }
        DimensionsSheet(
            title = "Resize all photos",
            baseWidth = first?.width ?: 0,
            baseHeight = first?.height ?: 0,
            baseBytes = first?.bytes ?: -1L,
            baseBitmap = null,
            format = baseFormat,
            quality = quality,
            lastUsed = lastUsed,
            fromQueue = true,
            onDismiss = { showResize = false },
            onApply = { choice ->
                showResize = false
                if (choice is SheetChoice.Absolute) onApplyTargetAll(choice.w, choice.h)
                onProcessAll(choice)
            }
        )
    }
    if (showConvert) {
        AlertDialog(
            onDismissRequest = { showConvert = false },
            title = { Text("Convert format", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Used when you export each photo. You can still change it per photo on its Export screen.", color = Slate, fontSize = 12.sp, lineHeight = 16.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp).horizontalScroll(rememberScrollState())) {
                        listOf("original" to "Original", "JPEG" to "JPG", "PNG" to "PNG", "WEBP" to "WEBP", "PDF" to "PDF").forEach { (value, label) ->
                            val selected = currentFormatKey == value
                            Surface(modifier = Modifier.clickable { onFormatAll(value); showConvert = false }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                                Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showConvert = false }) { Text("Done", color = DeepNavy) } }
        )
    }
    if (showRename) {
        var pattern by rememberSaveable { mutableStateOf(namePattern) }
        AlertDialog(
            onDismissRequest = { showRename = false },
            title = { Text("Name pattern", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = pattern, onValueChange = { pattern = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Base name") }, singleLine = true)
                    Text(
                        if (pattern.isBlank()) "Leave empty to use automatic names."
                        else "Files: ${pattern.trim()}-1, ${pattern.trim()}-2, \u2026 (extension added automatically)",
                        color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 6.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onPatternAll(pattern.trim()); showRename = false }) { Text("Apply", color = DeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showRename = false }) { Text("Cancel", color = Slate) } }
        )
    }
}

@Composable
private fun EditorScreen(
    modifier: Modifier,
    queueLabel: String?,
    queueUris: List<Uri>,
    queueIndex: Int,
    queueThumbs: Map<String, ImageBitmap?>,
    onQueueSelect: (Int) -> Unit,
    working: Bitmap?,
    processed: Bitmap?,
    sizeUnit: String,
    resizeDpi: String,
    busy: Boolean,
    busyLabel: String,
    beforeAfter: Boolean,
    tool: Tool,
    sourceBytes: Long,
    estimatedBytes: Long,
    onBeforeAfter: (Boolean) -> Unit,
    onTool: (Tool) -> Unit,
    onSizeUnit: (String) -> Unit,
    onResizeDpi: (String) -> Unit,
    onRotate: () -> Unit,
    onProcess: () -> Unit,
    onExport: () -> Unit
) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        if (queueLabel != null) {
            Text(queueLabel, color = SkyBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        }
        if (queueUris.size > 1) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                items(queueUris.size) { index ->
                    val thumb = queueThumbs[queueUris[index].toString()]
                    val selected = index == queueIndex
                    Box(
                        Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(SoftGrey)
                            .border(if (selected) 2.dp else 1.dp, if (selected) SkyBlue else BorderGrey, RoundedCornerShape(10.dp))
                            .clickable { onQueueSelect(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (thumb != null) {
                            Image(bitmap = thumb, contentDescription = "Photo ${index + 1}", modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
                        } else {
                            Text("${index + 1}", color = Slate, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        // No Before/After tabs here (his request): the comparison lives on the result page.
        // The editor always shows the photo being edited.
        Box(
            Modifier.fillMaxWidth().padding(vertical = 8.dp).height(260.dp).clip(RoundedCornerShape(18.dp)).background(SoftGrey).border(1.dp, BorderGrey, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            val display = working
            val displayImage = remember(display) { display?.asImageBitmap() }
            if (displayImage != null) {
                Image(bitmap = displayImage, contentDescription = "Image preview", modifier = Modifier.fillMaxSize().padding(8.dp), contentScale = ContentScale.Fit)
            } else {
                Text("Select a photo to begin", color = DeepNavy)
            }
            if (busy) CircularProgressIndicator(color = Orange)
            IconButton(
                onClick = onRotate,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).border(1.dp, BorderGrey, CircleShape)
            ) {
                Icon(Icons.Default.RotateRight, contentDescription = "Rotate 90 degrees", tint = DeepNavy, modifier = Modifier.size(20.dp))
            }
        }
        val infoText = when {
            busy -> busyLabel.ifBlank { "Working…" }
            working != null -> {
                val size = if (sourceBytes >= 0) "  ·  ${formatBytes(sourceBytes)}" else ""
                "Before: ${working.width} × ${working.height} px$size"
            }
            else -> ""
        }
        Text(infoText, modifier = Modifier.padding(vertical = 8.dp), fontSize = 12.sp, color = Slate)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            Tool.values().forEach { item ->
                val selected = tool == item
                Surface(modifier = Modifier.clickable { onTool(item) }, shape = RoundedCornerShape(10.dp), color = if (selected) SkyBlue else SoftGrey) {
                    Text(
                        item.label,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        fontSize = 12.sp,
                        color = if (selected) Color.White else DeepNavy,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (tool == Tool.Resize) {
            // Unit chips stay on this page (user request); the chosen unit carries into
            // the options sheet. Size VALUES are chosen in the sheet only.
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf("px" to "Pixels", "in" to "Inches", "cm" to "cm", "mm" to "mm").forEach { (value, label) ->
                    val selected = sizeUnit == value
                    Surface(modifier = Modifier.clickable { onSizeUnit(value) }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
                    }
                }
            }
            OutlinedTextField(
                value = resizeDpi,
                onValueChange = onResizeDpi,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                label = { Text("DPI - 300 is standard for print") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            // Size is chosen in ONE place only: the options sheet that Process opens
            // (user decision on v0.4.5: no duplicated size fields on this page).
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Tap Process to choose the new size - quick sizes, percentage, exact pixels or a target file size, all in one place.", color = Slate, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(16.dp))
            }
        } else {
            Text("${tool.label} controls will be added in the next development stages. Resize and export are available now.", color = DeepNavy, fontSize = 13.sp)
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onProcess,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            enabled = !busy && working != null
        ) {
            if (busy) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp) else Text("Process", fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onExport,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            enabled = !busy && processed != null
        ) {
            Text("Export", fontWeight = FontWeight.Bold)
        }
        if (processed == null) {
            Text("Tap Process first, then Export saves the result.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
        } else {
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AspectRatioDialog(
    source: Bitmap,
    request: AdaptRequest,
    onCancel: () -> Unit,
    onChoose: (AdaptMode, CropAnchor, FitPad) -> Unit
) {
    var anchor by remember { mutableStateOf(CropAnchor.CENTER) }
    var pad by remember { mutableStateOf(FitPad.BLUR) }
    var thumb by remember { mutableStateOf<Bitmap?>(null) }
    val previews = remember { mutableStateMapOf<AdaptMode, ImageBitmap?>() }

    LaunchedEffect(request) {
        thumb = withContext(Dispatchers.Default) { Transforms.thumbnail(source) }
    }
    LaunchedEffect(thumb, request) {
        val t = thumb ?: return@LaunchedEffect
        val (tw, th) = Transforms.scaledTarget(request.width, request.height)
        previews[AdaptMode.STRETCH] = withContext(Dispatchers.Default) {
            Transforms.stretch(t, tw, th).asImageBitmap()
        }
    }
    LaunchedEffect(thumb, request, pad) {
        val t = thumb ?: return@LaunchedEffect
        val (tw, th) = Transforms.scaledTarget(request.width, request.height)
        previews[AdaptMode.FIT] = withContext(Dispatchers.Default) {
            Transforms.fit(t, tw, th, pad).asImageBitmap()
        }
    }
    LaunchedEffect(thumb, request, anchor) {
        val t = thumb ?: return@LaunchedEffect
        val (tw, th) = Transforms.scaledTarget(request.width, request.height)
        previews[AdaptMode.CROP] = withContext(Dispatchers.Default) {
            Transforms.crop(t, tw, th, anchor).asImageBitmap()
        }
    }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel", color = DeepNavy) }
        },
        title = { Text("Change in aspect ratio", fontWeight = FontWeight.Bold, color = DeepNavy) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Choose how to adapt your image to the new dimensions", color = Slate, fontSize = 13.sp)
                Text(
                    "${source.width}x${source.height} -> ${request.width}x${request.height} px",
                    color = DeepNavy,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )

                AdaptCard(
                    title = "Fit",
                    description = "Keep the whole photo. Empty space is filled with a background you choose.",
                    preview = previews[AdaptMode.FIT],
                    buttonLabel = "Use Fit",
                    onUse = { onChoose(AdaptMode.FIT, anchor, pad) }
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
                        FitPad.values().forEach { option ->
                            val selected = pad == option
                            Surface(
                                modifier = Modifier.clickable { pad = option },
                                shape = RoundedCornerShape(9.dp),
                                color = if (selected) SkyBlue else SoftGrey
                            ) {
                                Text(option.label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }

                AdaptCard(
                    title = "Crop",
                    description = "Fill the new size exactly. Choose which part of the photo to keep.",
                    preview = previews[AdaptMode.CROP],
                    buttonLabel = "Use Crop",
                    onUse = { onChoose(AdaptMode.CROP, anchor, pad) }
                ) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        for (row in 0..2) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 6.dp)) {
                                for (col in 0..2) {
                                    val cell = CropAnchor.values()[row * 3 + col]
                                    val selected = anchor == cell
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (selected) SkyBlue else SoftGrey)
                                            .border(1.dp, BorderGrey, RoundedCornerShape(6.dp))
                                            .clickable { anchor = cell }
                                    )
                                }
                            }
                        }
                        Text("Keeps: ${anchor.label}", color = Slate, fontSize = 11.sp)
                    }
                }

                AdaptCard(
                    title = "Stretch",
                    description = "Fill the new size exactly. The photo may look squeezed or stretched.",
                    preview = previews[AdaptMode.STRETCH],
                    buttonLabel = "Use Stretch",
                    onUse = { onChoose(AdaptMode.STRETCH, anchor, pad) }
                )
            }
        }
    )
}

@Composable
private fun AdaptCard(
    title: String,
    description: String,
    preview: ImageBitmap?,
    buttonLabel: String,
    onUse: () -> Unit,
    extra: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderGrey),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(description, color = Slate, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 2.dp))
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(150.dp).clip(RoundedCornerShape(10.dp)).background(SoftGrey),
                contentAlignment = Alignment.Center
            ) {
                if (preview != null) {
                    Image(bitmap = preview, contentDescription = "$title preview of your photo", modifier = Modifier.fillMaxSize().padding(6.dp), contentScale = ContentScale.Fit)
                } else {
                    CircularProgressIndicator(color = SkyBlue, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                }
            }
            extra?.invoke()
            Button(
                onClick = onUse,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) { Text(buttonLabel, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ExportScreen(
    modifier: Modifier,
    processed: Bitmap?,
    source: Bitmap?,
    sourceBytes: Long,
    sourceWidth: Int,
    sourceHeight: Int,
    estimatedBytes: Long,
    exportName: String,
    onName: (String) -> Unit,
    format: ExportFormat,
    chosenFormat: String,
    originalFormatLabel: String,
    onChosenFormat: (String) -> Unit,
    quality: Int,
    onQuality: (Int) -> Unit,
    onQualitySettled: () -> Unit,
    canReplace: Boolean,
    replaceHint: String,
    busy: Boolean,
    busyLabel: String,
    onSaveNew: () -> Unit,
    onReplace: () -> Unit,
    onSaveToFolder: () -> Unit,
    onShare: () -> Unit
) {
    // Before/After visual comparison on the result page (his request: same tabs as the editor).
    var showAfter by rememberSaveable { mutableStateOf(true) }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(false to "Before", true to "After").forEach { (after, label) ->
                val selected = showAfter == after
                val enabled = if (after) processed != null else source != null
                OutlinedButton(
                    onClick = { showAfter = after },
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) PaleBlue else Color.Transparent)
                ) { Text(label, color = if (enabled) DeepNavy else Slate) }
            }
        }
        Box(
            Modifier.fillMaxWidth().padding(bottom = 8.dp).height(220.dp).clip(RoundedCornerShape(18.dp)).background(SoftGrey).border(1.dp, BorderGrey, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            val shown = if (showAfter) processed else source
            val preview = remember(shown) { shown?.asImageBitmap() }
            if (preview != null) {
                Image(bitmap = preview, contentDescription = if (showAfter) "Resized photo preview" else "Original photo preview", modifier = Modifier.fillMaxSize().padding(8.dp), contentScale = ContentScale.Fit)
            } else {
                Text("Process the photo first, then come back to export.", color = DeepNavy)
            }
            if (busy) CircularProgressIndicator(color = Orange)
        }
        if (busyLabel.isNotBlank()) {
            Text(busyLabel, color = Slate, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
        }
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                if (processed != null) {
                    Text("Result: ${processed.width} × ${processed.height} px", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    if (sourceWidth > 0 && sourceHeight > 0) {
                        Text("Original: $sourceWidth × $sourceHeight px", color = Slate, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                    }
                    if (estimatedBytes >= 0) {
                        Text("Estimated size: ${formatBytes(estimatedBytes)}", color = DeepNavy, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
                    }
                    if (sourceBytes > 0 && estimatedBytes >= 0) {
                        val pct = ((sourceBytes - estimatedBytes) * 100.0 / sourceBytes).toInt()
                        val trend = if (pct >= 0) "$pct% smaller" else "${-pct}% larger"
                        Text("Original ${formatBytes(sourceBytes)}  ->  about $trend", color = Slate, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                    }
                } else {
                    Text("Nothing processed yet.", color = DeepNavy, fontSize = 13.sp)
                }
            }
        }
        OutlinedTextField(
            value = exportName,
            onValueChange = onName,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            label = { Text("File name") },
            singleLine = true
        )
        Text("Save as format", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp).horizontalScroll(rememberScrollState())) {
            listOf("original" to originalFormatLabel, "JPEG" to "JPG", "PNG" to "PNG", "WEBP" to "WEBP", "PDF" to "PDF").forEach { (value, label) ->
                val selected = chosenFormat == value
                Surface(modifier = Modifier.clickable { onChosenFormat(value) }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                    Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
                }
            }
        }
        if (format == ExportFormat.PNG) {
            Text("PNG keeps full quality and ignores the quality slider.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 6.dp))
        }
        if (format == ExportFormat.PDF) {
            Text("PDF files are saved to the Download/PixelCraft folder. The quality slider does not apply.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 6.dp))
        }
        if (format != ExportFormat.PNG && format != ExportFormat.PDF && processed != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Text("Quality: $quality%", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = quality.toFloat(),
                    onValueChange = { onQuality(it.toInt()) },
                    onValueChangeFinished = onQualitySettled,
                    valueRange = 10f..100f,
                    steps = 89,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Text("Lower quality makes a smaller file. The estimate updates when you stop moving the slider.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp)
        }
        Button(
            onClick = onSaveNew,
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            enabled = !busy && processed != null
        ) { Text("Save as new file", fontWeight = FontWeight.Bold) }
        OutlinedButton(
            onClick = onReplace,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = !busy && processed != null && canReplace
        ) { Text("Replace original", color = DeepNavy, fontWeight = FontWeight.Bold) }
        Text(replaceHint, color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp))
        OutlinedButton(
            onClick = onSaveToFolder,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = !busy && processed != null
        ) { Text("Save to folder you choose", color = DeepNavy, fontWeight = FontWeight.Bold) }
        TextButton(onClick = onShare, modifier = Modifier.fillMaxWidth().padding(top = 6.dp), enabled = !busy && processed != null) {
            Text("Share…", color = DeepNavy)
        }
        Text(
            if (format == ExportFormat.PDF) "The original photo is never changed unless you choose Replace original. PDF files go to Download/PixelCraft."
            else "The original photo is never changed unless you choose Replace original. New files go to Pictures/PixelCraft.",
            color = Slate,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun SuccessScreen(modifier: Modifier, name: String, size: String, nextLabel: String?, onNext: () -> Unit, onDone: () -> Unit, onAnother: () -> Unit) {
    Column(modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(70.dp))
        Spacer(Modifier.height(16.dp))
        Text("Saved successfully", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(name, color = DeepNavy, modifier = Modifier.padding(top = 8.dp), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(size, color = Slate, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
        if (nextLabel != null) {
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White)) { Text(nextLabel, color = Color.White) }
            Spacer(Modifier.height(8.dp))
        }
        Button(onClick = onAnother, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("Edit another photo", color = Color.White) }
        OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Done", color = DeepNavy) }
    }
}

private enum class PhotosTab(val label: String) { Recent("Recent"), Saved("Saved"), Drafts("Drafts") }

private data class SavedPhoto(val uri: Uri, val name: String, val sizeBytes: Long)

@Composable
private fun MyPhotosScreen(
    modifier: Modifier,
    recents: List<Uri>,
    recentThumbs: Map<String, ImageBitmap?>,
    onOpenPhoto: (Uri) -> Unit,
    onOpenDraft: (Draft) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var shareMsg by remember { mutableStateOf("") }
    var photosTab by rememberSaveable { mutableStateOf(PhotosTab.Recent) }
    var saved by remember { mutableStateOf<List<SavedPhoto>?>(null) }
    var drafts by remember { mutableStateOf<List<Draft>>(emptyList()) }
    val savedThumbs = remember { mutableStateMapOf<String, ImageBitmap?>() }
    val draftThumbs = remember { mutableStateMapOf<String, ImageBitmap?>() }

    LaunchedEffect(photosTab) {
        if (photosTab == PhotosTab.Saved) {
            saved = withContext(Dispatchers.IO) { querySavedExports(context) }
        }
        if (photosTab == PhotosTab.Drafts) {
            drafts = withContext(Dispatchers.IO) { DraftsStore.list(context) }
        }
    }
    LaunchedEffect(saved) {
        saved?.forEach { sp ->
            val key = sp.uri.toString()
            if (!savedThumbs.containsKey(key)) {
                savedThumbs[key] = null
                savedThumbs[key] = withContext(Dispatchers.IO) { loadSavedThumbnail(context, sp.uri) }?.asImageBitmap()
            }
        }
    }
    LaunchedEffect(drafts) {
        drafts.forEach { draft ->
            val key = draft.sourceUri
            if (!draftThumbs.containsKey(key)) {
                draftThumbs[key] = null
                draftThumbs[key] = withContext(Dispatchers.IO) { RecentsStore.loadThumbnail(context, Uri.parse(key)) }?.asImageBitmap()
            }
        }
    }

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text("My Photos", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
            PhotosTab.values().forEach { item ->
                val selected = photosTab == item
                Surface(
                    modifier = Modifier.clickable { photosTab = item },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) SkyBlue else SoftGrey
                ) {
                    Text(
                        item.label,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        fontSize = 13.sp,
                        color = if (selected) Color.White else DeepNavy,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
        when (photosTab) {
            PhotosTab.Recent -> {
                if (recents.isEmpty()) {
                    EmptyNote("Photos you edit will appear here. Nothing yet - pick a photo from Home and edit it.")
                } else {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        recents.chunked(3).forEach { rowItems ->
                            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowItems.forEach { uri ->
                                    PhotoCell(
                                        thumb = recentThumbs[uri.toString()],
                                        modifier = Modifier.weight(1f).height(100.dp),
                                        onClick = { onOpenPhoto(uri) }
                                    )
                                }
                                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            }
            PhotosTab.Saved -> {
                val list = saved
                when {
                    list == null -> Text("Loading…", color = Slate, fontSize = 13.sp)
                    Build.VERSION.SDK_INT < 29 -> EmptyNote("The saved-exports list needs Android 10 or newer. Your exports are still in the Pictures/PixelCraft folder.")
                    list.isEmpty() -> EmptyNote("Nothing saved yet. Exported photos land here and in the Pictures/PixelCraft folder in your gallery.")
                    else -> Column(Modifier.verticalScroll(rememberScrollState())) {
                        list.forEach { sp ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).background(SoftGrey).clickable { onOpenPhoto(sp.uri) }.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PhotoCell(thumb = savedThumbs[sp.uri.toString()], modifier = Modifier.size(52.dp), onClick = { onOpenPhoto(sp.uri) })
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(sp.name, color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(formatBytes(sp.sizeBytes), color = Slate, fontSize = 12.sp)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        try {
                                            shareExported(context, sp.uri, sp.name)
                                        } catch (e: Exception) {
                                            shareMsg = "Couldn't share this photo."
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = DeepNavy, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                        if (shareMsg.isNotBlank()) {
                            Text(shareMsg, color = Color(0xFFB3261E), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                        Text("Tapping a saved photo opens it as a new editing source.", color = Slate, fontSize = 11.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
            PhotosTab.Drafts -> {
                if (drafts.isEmpty()) {
                    EmptyNote("No drafts. If you leave the editor without exporting, your photo and settings are saved here automatically.")
                } else {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        drafts.forEach { draft ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).background(SoftGrey).clickable { onOpenDraft(draft) }.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PhotoCell(thumb = draftThumbs[draft.sourceUri], modifier = Modifier.size(52.dp), onClick = { onOpenDraft(draft) })
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text("Target: ${draft.width} × ${draft.height} px", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(
                                        "Quality ${draft.quality}% · " + formatDraftTime(draft.updatedAt),
                                        color = Slate,
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        withContext(Dispatchers.IO) { DraftsStore.remove(context, draft.sourceUri) }
                                        drafts = withContext(Dispatchers.IO) { DraftsStore.list(context) }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete draft", tint = Slate, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                        Text("Drafts keep your photo and settings, not the processed result - tap Process again after reopening.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoCell(thumb: ImageBitmap?, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SoftGrey)
            .border(1.dp, BorderGrey, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (thumb != null) {
            Image(bitmap = thumb, contentDescription = "Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Icon(Icons.Default.Image, contentDescription = null, tint = Slate, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun EmptyNote(text: String) {
    Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
        Text(text, color = Slate, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(16.dp))
    }
}

private fun formatDraftTime(updatedAt: Long): String {
    return try {
        SimpleDateFormat("d MMM, HH:mm", Locale.getDefault()).format(Date(updatedAt))
    } catch (e: Exception) {
        ""
    }
}

/** Thumbnail for a saved export: MediaStore's own thumbnailer (no permission needed for
 *  files this app wrote), falling back to the cached/decode path. */
private fun loadSavedThumbnail(context: Context, uri: Uri): Bitmap? {
    if (Build.VERSION.SDK_INT >= 29) {
        try {
            return context.contentResolver.loadThumbnail(uri, android.util.Size(384, 384), null)
        } catch (e: Exception) {
            // Fall through to the cache/decode path below.
        }
    }
    return RecentsStore.loadThumbnail(context, uri)
}

private fun querySavedExports(context: Context): List<SavedPhoto> {
    if (Build.VERSION.SDK_INT < 29) return emptyList()
    val out = mutableListOf<SavedPhoto>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )
    val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    val args = arrayOf("Pictures/PixelCraft%")
    val sort = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    try {
        context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, args, sort)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = android.content.ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                out.add(SavedPhoto(uri, cursor.getString(nameCol) ?: "photo", cursor.getLong(sizeCol)))
            }
        }
    } catch (e: Exception) {
        // Return what we have; an empty list shows the honest empty state.
    }
    return out
}

private suspend fun shareExported(context: Context, uri: Uri, displayName: String) {
    val pair = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "images").apply { mkdirs() }
        val ext = displayName.substringAfterLast('.', "jpg")
        val file = File(dir, "share_${System.currentTimeMillis()}.$ext")
        openImageStream(context, uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        } ?: throw IllegalStateException("Could not read the saved photo")
        val shareUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        shareUri to (context.contentResolver.getType(uri) ?: "image/jpeg")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = pair.second
        putExtra(Intent.EXTRA_STREAM, pair.first)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share photo"))
}

@Composable
private fun PresetsScreen(modifier: Modifier, onPreset: (Preset) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var presets by remember { mutableStateOf<List<Preset>?>(null) }
    var search by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        presets = withContext(Dispatchers.IO) { PresetStore.load(context) }
    }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Presets", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Shop & creator · Social media", color = Slate, modifier = Modifier.padding(top = 4.dp, bottom = 14.dp))
        OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Search presets") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        val list = presets
        if (list == null) {
            Text("Loading presets…", color = Slate, fontSize = 13.sp)
        } else {
            val filtered = list.filter {
                search.isBlank() || it.platform.contains(search, true) || it.name.contains(search, true)
            }
            if (filtered.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("No verified presets yet", color = DeepNavy, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Every preset size in PixelCraft is checked against official platform documentation before it appears here, with its source and review date. That research (Stage 2B) is not finished yet, so this list is honestly empty rather than filled with unverified numbers.",
                            color = Slate,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            } else {
                val grouped = filtered.groupBy { it.category }
                listOf("shop" to "Shop & creator", "social" to "Social media").forEach { (key, title) ->
                    val itemsForCategory = grouped[key].orEmpty()
                    if (itemsForCategory.isNotEmpty()) {
                        Text(title, color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp))
                        itemsForCategory.forEach { preset ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).background(PaleBlue).clickable { onPreset(preset) }.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.size(34.dp).clip(CircleShape).background(SkyBlue), contentAlignment = Alignment.Center) {
                                    Text(preset.platform.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text("${preset.platform} · ${preset.name}", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("${preset.width} × ${preset.height} px · ${preset.ratio}", color = Slate, fontSize = 12.sp)
                                }
                                if (preset.status == "verified") {
                                    Text("Verified", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier, darkMode: Boolean, onDarkMode: (Boolean) -> Unit) {
    Column(modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState())) {
        Text("Settings", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Text("Appearance", color = DeepNavy, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (darkMode) "Dark mode" else "Light mode", modifier = Modifier.weight(1f), color = DeepNavy)
            Checkbox(checked = darkMode, onCheckedChange = onDarkMode)
        }
        Spacer(Modifier.height(14.dp))
        Text("Privacy", color = DeepNavy, fontWeight = FontWeight.SemiBold)
        Text(
            "Your photos stay on this phone. PixelCraft asks for no account, uploads nothing, and requests no photo-library permission: you pick each photo yourself. 'Recently edited' remembers only photos you opened, stored on this device.",
            color = Slate,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text("PixelCraft Image Resizer", color = DeepNavy, fontWeight = FontWeight.SemiBold)
        Text("Version 0.3.0 · Learnify Zone", color = Slate, fontSize = 13.sp)
        Text("Stage 2C build: process-then-export, aspect-ratio choices, camera capture, multi-select, My Photos (recent, saved, drafts), quality control. Ads are not enabled in this development version.", color = Slate, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 14.dp))
    }
}

@Composable
private fun PrintCalculator(modifier: Modifier, hasPhoto: Boolean, onApply: (Int, Int) -> Unit) {
    var width by rememberSaveable { mutableStateOf("20") }
    var height by rememberSaveable { mutableStateOf("25") }
    var dpi by rememberSaveable { mutableStateOf("300") }
    val w = width.toDoubleOrNull()
    val h = height.toDoubleOrNull()
    val density = dpi.toDoubleOrNull()
    val pxW = if (w != null && density != null && w > 0 && density > 0) (w / 2.54 * density).toInt() else null
    val pxH = if (h != null && density != null && h > 0 && density > 0) (h / 2.54 * density).toInt() else null
    Column(modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState())) {
        Text("Print Size & DPI", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Print size → required pixels", color = Slate, modifier = Modifier.padding(top = 4.dp, bottom = 18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(width, { width = it }, modifier = Modifier.weight(1f), label = { Text("Width (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            OutlinedTextField(height, { height = it }, modifier = Modifier.weight(1f), label = { Text("Height (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
        }
        OutlinedTextField(dpi, { dpi = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("DPI") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
        Card(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), colors = CardDefaults.cardColors(containerColor = PaleBlue)) {
            Column(Modifier.padding(18.dp)) {
                Text(if (pxW != null && pxH != null) "$pxW × $pxH px" else "Enter valid values", color = DeepNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("${width} × ${height} cm at ${dpi} DPI", color = Slate, fontSize = 13.sp)
            }
        }
        Text("Changing DPI alone does not add image detail. This calculator estimates the pixels needed for a print.", color = Slate, fontSize = 12.sp, modifier = Modifier.padding(vertical = 14.dp))
        Button(
            onClick = { if (pxW != null && pxH != null) onApply(pxW, pxH) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            enabled = pxW != null && pxH != null
        ) { Text(if (hasPhoto) "Apply to photo" else "Select a photo first", color = Color.White) }
    }
}

private fun rotateRight(bitmap: Bitmap): Bitmap {
    val matrix = Matrix().apply { postRotate(90f) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private data class QueueItemInfo(val width: Int, val height: Int, val bytes: Long)

private fun probeImageInfo(context: Context, uri: Uri): QueueItemInfo? {
    return try {
        val bounds = openImageStream(context, uri)?.use { stream ->
            BitmapFactory.Options().apply { inJustDecodeBounds = true }.also { BitmapFactory.decodeStream(stream, null, it) }
        } ?: return null
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        QueueItemInfo(bounds.outWidth, bounds.outHeight, querySourceSize(context, uri))
    } catch (e: Exception) {
        null
    }
}

private fun decodeThumbnail(context: Context, uri: Uri, maxSide: Int = 192): Bitmap? {
    return try {
        val bounds = openImageStream(context, uri)?.use { stream ->
            BitmapFactory.Options().apply { inJustDecodeBounds = true }.also { BitmapFactory.decodeStream(stream, null, it) }
        } ?: return null
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) > maxSide) sample *= 2
        openImageStream(context, uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, BitmapFactory.Options().apply { inSampleSize = sample })
        }
    } catch (e: Exception) {
        null
    }
}

/** Opens an image stream for content:// AND file:// URIs (file:// lets JVM UI tests feed photos). */
private fun openImageStream(context: Context, uri: Uri): InputStream? =
    if (uri.scheme == "file") runCatching { File(uri.path!!).inputStream() }.getOrNull()
    else context.contentResolver.openInputStream(uri)

private fun decodeOrientedBitmap(context: Context, uri: Uri): Bitmap {
    val bounds = openImageStream(context, uri)?.use { stream ->
        BitmapFactory.Options().apply { inJustDecodeBounds = true }.also { options -> BitmapFactory.decodeStream(stream, null, options) }
    } ?: throw IllegalArgumentException("Cannot read image")
    val maxSide = maxOf(bounds.outWidth, bounds.outHeight)
    var sample = 1
    while (maxSide / sample > 4096) sample *= 2
    val bitmap = openImageStream(context, uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, BitmapFactory.Options().apply { inSampleSize = sample; inPreferredConfig = Bitmap.Config.ARGB_8888 })
    } ?: throw IllegalArgumentException("Unsupported image")
    val orientation = openImageStream(context, uri)?.use { stream ->
        ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    } ?: ExifInterface.ORIENTATION_NORMAL
    val matrix = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> { postRotate(90f); postScale(-1f, 1f) }
            ExifInterface.ORIENTATION_TRANSVERSE -> { postRotate(270f); postScale(-1f, 1f) }
        }
    }
    return if (orientation == ExifInterface.ORIENTATION_NORMAL || orientation == ExifInterface.ORIENTATION_UNDEFINED) bitmap
    else Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also { if (it !== bitmap) bitmap.recycle() }
}

/**
 * Encodes the processed bitmap to its final bytes. For JPEG output the photo's
 * own metadata (date, camera info, GPS) is copied across best-effort;
 * orientation is normalised because the pixels are already rotated correctly.
 */
private fun encodeImage(context: Context, exifSource: Uri?, bitmap: Bitmap, format: ExportFormat, quality: Int): ByteArray {
    if (format == ExportFormat.PDF) {
        val document = PdfDocument()
        try {
            val page = document.startPage(PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create())
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)
            val baos = ByteArrayOutputStream()
            document.writeTo(baos)
            return baos.toByteArray()
        } finally {
            document.close()
        }
    }
    if (format == ExportFormat.JPEG) {
        val tmp = File(context.cacheDir, "encode_${System.currentTimeMillis()}.jpg")
        try {
            FileOutputStream(tmp).use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                    throw IllegalStateException("Image encoding failed")
                }
            }
            if (exifSource != null) copyExifBestEffort(context, exifSource, tmp, bitmap.width, bitmap.height)
            return tmp.readBytes()
        } finally {
            tmp.delete()
        }
    }
    val baos = ByteArrayOutputStream()
    val q = if (format == ExportFormat.PNG) 100 else quality
    if (!bitmap.compress(format.compressFormat(), q, baos)) {
        throw IllegalStateException("Image encoding failed")
    }
    return baos.toByteArray()
}

private val EXIF_TAGS_TO_COPY = listOf(
    ExifInterface.TAG_DATETIME,
    ExifInterface.TAG_DATETIME_ORIGINAL,
    ExifInterface.TAG_MAKE,
    ExifInterface.TAG_MODEL,
    ExifInterface.TAG_F_NUMBER,
    ExifInterface.TAG_EXPOSURE_TIME,
    ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
    ExifInterface.TAG_FOCAL_LENGTH,
    ExifInterface.TAG_FLASH,
    ExifInterface.TAG_WHITE_BALANCE,
    ExifInterface.TAG_GPS_LATITUDE,
    ExifInterface.TAG_GPS_LATITUDE_REF,
    ExifInterface.TAG_GPS_LONGITUDE,
    ExifInterface.TAG_GPS_LONGITUDE_REF,
    ExifInterface.TAG_GPS_ALTITUDE,
    ExifInterface.TAG_GPS_ALTITUDE_REF,
    ExifInterface.TAG_GPS_TIMESTAMP,
    ExifInterface.TAG_GPS_DATESTAMP,
    ExifInterface.TAG_GPS_PROCESSING_METHOD,
    ExifInterface.TAG_USER_COMMENT,
    ExifInterface.TAG_IMAGE_DESCRIPTION
)

private fun copyExifBestEffort(context: Context, source: Uri, outFile: File, width: Int, height: Int) {
    try {
        val src = context.contentResolver.openFileDescriptor(source, "r")?.use { ExifInterface(it.fileDescriptor) } ?: return
        val dst = ExifInterface(outFile.absolutePath)
        for (tag in EXIF_TAGS_TO_COPY) {
            src.getAttribute(tag)?.let { dst.setAttribute(tag, it) }
        }
        dst.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, width.toString())
        dst.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, height.toString())
        dst.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
        dst.saveAttributes()
    } catch (e: Exception) {
        // Best effort: a JPEG without copied metadata is still a correct photo.
    }
}

private fun estimateBytes(bitmap: Bitmap, format: ExportFormat, quality: Int): Long {
    return try {
        val effective = if (format == ExportFormat.PDF) ExportFormat.JPEG else format
        val baos = ByteArrayOutputStream()
        val q = if (effective == ExportFormat.PNG) 100 else quality
        if (bitmap.compress(effective.compressFormat(), q, baos)) baos.size().toLong() else -1L
    } catch (e: Exception) {
        -1L
    }
}

private fun saveToGallery(context: Context, filename: String, mimeType: String, bytes: ByteArray): Uri {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= 29) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PixelCraft")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        ?: throw IllegalStateException("Could not create the output file")
    try {
        resolver.openOutputStream(uri, "w")?.use { it.write(bytes) }
            ?: throw IllegalStateException("Could not open the output file")
        if (Build.VERSION.SDK_INT >= 29) {
            resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
        }
    } catch (e: Exception) {
        resolver.delete(uri, null, null)
        throw e
    }
    return uri
}

private fun saveToDownloads(context: Context, filename: String, mimeType: String, bytes: ByteArray) {
    val resolver = context.contentResolver
    if (Build.VERSION.SDK_INT >= 29) {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/PixelCraft")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("Could not create the output file")
        try {
            resolver.openOutputStream(uri, "w")?.use { it.write(bytes) }
                ?: throw IllegalStateException("Could not open the output file")
            resolver.update(uri, ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }, null, null)
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    } else {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outDir = File(dir, "PixelCraft").apply { mkdirs() }
        FileOutputStream(File(outDir, filename)).use { it.write(bytes) }
    }
}

private fun querySourceSize(context: Context, uri: Uri): Long {
    try {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst() && !cursor.isNull(0)) return cursor.getLong(0)
        }
    } catch (e: Exception) { /* fall through */ }
    return try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: -1L
    } catch (e: Exception) {
        -1L
    }
}

private fun suggestBaseName(width: Int, height: Int): String = "pixelcraft-${width}x${height}"

private fun buildFileName(requestedName: String, format: ExportFormat): String {
    val cleaned = requestedName.trim().ifBlank { "pixelcraft-image" }
        .replace(Regex("[^A-Za-z0-9._ -]"), "_").take(80)
    val base = cleaned.replace(Regex("\\.(png|jpe?g|webp|pdf)$", RegexOption.IGNORE_CASE), "").ifBlank { "pixelcraft-image" }
    return "$base.${format.extension}"
}

private fun saveLastUsedTarget(context: Context, w: Int, h: Int) {
    context.getSharedPreferences("pixelcraft_state", Context.MODE_PRIVATE).edit()
        .putInt("last_resize_w", w).putInt("last_resize_h", h).apply()
}

private fun loadLastUsedTarget(context: Context): Pair<Int, Int>? {
    val prefs = context.getSharedPreferences("pixelcraft_state", Context.MODE_PRIVATE)
    val w = prefs.getInt("last_resize_w", -1)
    val h = prefs.getInt("last_resize_h", -1)
    return if (w > 0 && h > 0) w to h else null
}

private fun fmtUnit(px: Double, unit: String, dpi: Double): String {
    val df = DecimalFormat("#.##", java.text.DecimalFormatSymbols(java.util.Locale.US))
    return when (unit) {
        "in" -> df.format(px / dpi)
        "cm" -> df.format(px * 2.54 / dpi)
        "mm" -> df.format(px * 25.4 / dpi)
        else -> px.roundToInt().toString()
    }
}

private fun parseToPx(text: String, unit: String, dpi: Double): Int? {
    val v = text.trim().replace(',', '.').toDoubleOrNull() ?: return null
    if (v <= 0.0) return null
    val px = when (unit) {
        "in" -> v * dpi
        "cm" -> v * dpi / 2.54
        "mm" -> v * dpi / 25.4
        else -> v
    }
    return px.roundToInt().coerceAtLeast(1)
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "${DecimalFormat("#,##0.#").format(kb)} KB"
    return "${DecimalFormat("#,##0.##").format(kb / 1024.0)} MB"
}

private sealed class SheetChoice {
    data class Absolute(val w: Int, val h: Int) : SheetChoice()
    data class Longest(val side: Int, val label: String) : SheetChoice()
    data class Percent(val p: Int) : SheetChoice()
    data class FileTarget(val bytes: Long, val label: String) : SheetChoice()
}

private data class ProcessedResult(val cachePath: String, val width: Int, val height: Int, val bytes: Long, val oldBytes: Long, val oldWidth: Int, val oldHeight: Int)

private data class BatchProgress(val action: String, val current: Int, val total: Int, val percent: Int)

private fun choiceTargetFor(choice: SheetChoice, srcW: Int, srcH: Int): Pair<Int, Int>? {
    if (srcW <= 0 || srcH <= 0) return null
    return when (choice) {
        is SheetChoice.Absolute -> choice.w to choice.h
        is SheetChoice.Longest -> {
            val longest = maxOf(srcW, srcH)
            val target = minOf(choice.side, longest)
            val s = target.toDouble() / longest
            maxOf(1, (srcW * s).toInt()) to maxOf(1, (srcH * s).toInt())
        }
        is SheetChoice.Percent -> maxOf(1, (srcW * choice.p / 100.0).toInt()) to maxOf(1, (srcH * choice.p / 100.0).toInt())
        is SheetChoice.FileTarget -> null
    }
}

private fun fitToFileSize(src: Bitmap, format: ExportFormat, quality: Int, targetBytes: Long, onProgress: (Int) -> Unit): Pair<Bitmap, Long> {
    val effective = if (format == ExportFormat.PDF) ExportFormat.JPEG else format
    fun probe(bmp: Bitmap, q: Int): Long {
        val baos = ByteArrayOutputStream()
        return if (bmp.compress(effective.compressFormat(), q, baos)) baos.size().toLong() else -1L
    }
    val ladder = if (effective == ExportFormat.PNG) listOf(100) else listOf(95, 85, 75, 65, 55, 45, 35, 25, 15)
    var bmp = src
    var owned: Bitmap? = null
    var scale = 1.0
    var iteration = 0
    while (iteration < 8) {
        iteration++
        for (q in ladder) {
            val size = probe(bmp, q)
            if (size in 1..targetBytes) {
                if (owned != null && owned !== bmp) owned!!.recycle()
                return bmp to size
            }
        }
        onProgress((iteration * 100) / 9)
        scale *= 0.85
        val nw = maxOf(24, (src.width * scale).toInt())
        val nh = maxOf(24, (src.height * scale).toInt())
        if (nw >= bmp.width || nh >= bmp.height) break
        val next = Bitmap.createScaledBitmap(src, nw, nh, true)
        if (owned != null) owned!!.recycle()
        owned = next
        bmp = next
    }
    val finalSize = probe(bmp, ladder.last())
    return bmp to finalSize
}
