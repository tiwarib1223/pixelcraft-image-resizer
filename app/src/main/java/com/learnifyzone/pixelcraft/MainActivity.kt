package com.learnifyzone.pixelcraft

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RotateRight
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
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
import java.text.DecimalFormat

private val SkyBlue = Color(0xFF42BFEF)
private val Orange = Color(0xFFFF8A32)
private val DeepNavy = Color(0xFF172B4D)
private val PaleBlue = Color(0xFFF0FAFF)
private val SoftGrey = Color(0xFFF3F6F9)
private val Slate = Color(0xFF66758A)
private val BorderGrey = Color(0xFFE0E8EE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PixelCraftApp() }
    }
}

private enum class MainTab { Home, Presets, Settings }
private enum class Screen { Home, Editor, Export, Print, Success }
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
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var legacySaveAfterGrant by rememberSaveable { mutableStateOf(false) }
    var photoQueue by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var queueIndex by rememberSaveable { mutableIntStateOf(0) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) loadPhoto(uri, isCamera = false)
    }
    val multiPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(50)) { uris ->
        if (uris.size == 1) {
            loadPhoto(uris[0], isCamera = false)
        } else if (uris.size > 1) {
            photoQueue = uris
            queueIndex = 0
            loadPhoto(uris[0], isCamera = false, fromQueue = true)
            scope.launch { snackbar.showSnackbar("${uris.size} photos selected. You process and export them one by one.") }
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
    val createJpeg = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/jpeg")) { uri ->
        if (uri != null) saveToChosenFolder(uri)
    }
    val createPng = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        if (uri != null) saveToChosenFolder(uri)
    }
    val createWebp = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/webp")) { uri ->
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

    fun outFormat(): ExportFormat = if (sourceFormat == ExportFormat.UNKNOWN) ExportFormat.JPEG else sourceFormat

    fun invalidateProcessed() {
        processedBitmap = null
        estimatedBytes = -1L
        beforeAfter = false
    }

    fun applyTarget(width: Int, height: Int) {
        inputWidth = width.toString()
        inputHeight = height.toString()
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
                    inputWidth = loaded.width.toString()
                    inputHeight = loaded.height.toString()
                    customPercent = ""
                    percentError = ""
                }
                exportName = suggestBaseName(loaded.width, loaded.height)
                beforeAfter = false
                if (!isCamera) withContext(Dispatchers.IO) { RecentsStore.add(context, uri) }
                pendingPreset?.let { preset ->
                    applyTarget(preset.width, preset.height)
                    pendingPreset = null
                }
                screen = Screen.Editor
                tool = Tool.Resize
                snackbar.showSnackbar("Photo loaded (${loaded.width} × ${loaded.height} px).")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't open this image. Try another photo.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun encodeProcessed(): ByteArray {
        val processed = processedBitmap ?: throw IllegalStateException("Nothing processed yet")
        return encodeImage(context, sourceUri, processed, outFormat(), quality)
    }

    fun runProcess(targetW: Int, targetH: Int, mode: AdaptMode, anchor: CropAnchor, pad: FitPad) {
        val src = workingBitmap ?: return
        busy = true
        busyLabel = "Processing…"
        adaptRequest = null
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
                exportName = suggestBaseName(targetW, targetH)
                beforeAfter = true
                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(result, outFormat(), quality) }
                snackbar.showSnackbar("Processed: $targetW × $targetH px. Tap Export to save.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't process this image. Try smaller dimensions.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun onProcess() {
        val src = workingBitmap
        val w = inputWidth.toIntOrNull()
        val h = inputHeight.toIntOrNull()
        if (src == null) {
            scope.launch { snackbar.showSnackbar("Select a photo first.") }
            return
        }
        if (w == null || h == null) {
            scope.launch { snackbar.showSnackbar("Enter width and height in pixels first.") }
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

    fun finishSave(name: String, sizeBytes: Long) {
        savedName = name
        savedSize = formatBytes(sizeBytes)
        screen = Screen.Success
    }

    fun doSaveAsNew() {
        busy = true
        busyLabel = "Saving…"
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { encodeProcessed() }
                val filename = buildFileName(exportName, outFormat())
                withContext(Dispatchers.IO) { saveToGallery(context, filename, outFormat().mimeType, bytes) }
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
            legacyWritePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                val bytes = withContext(Dispatchers.IO) { encodeProcessed() }
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
        val uri = sourceUri ?: return
        if (Build.VERSION.SDK_INT >= 30) {
            val pendingIntent = MediaStore.createWriteRequest(context.contentResolver, listOf(uri))
            writeConsent.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
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
        val suggested = buildFileName(exportName, outFormat())
        when (outFormat()) {
            ExportFormat.JPEG -> createJpeg.launch(suggested)
            ExportFormat.PNG -> createPng.launch(suggested)
            ExportFormat.WEBP -> createWebp.launch(suggested)
            ExportFormat.UNKNOWN -> createJpeg.launch(suggested)
        }
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
        val dir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        pendingCameraUri = uri
        takePicture.launch(uri)
    }

    val multiPickerUnlimited = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.size == 1) {
            loadPhoto(uris[0], isCamera = false)
        } else if (uris.size > 1) {
            photoQueue = uris
            queueIndex = 0
            loadPhoto(uris[0], isCamera = false, fromQueue = true)
            scope.launch { snackbar.showSnackbar("${uris.size} photos selected. You process and export them one by one.") }
        }
    }

    fun pickPhotos() {
        try {
            multiPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (e: IllegalArgumentException) {
            // Very old pickers enforce a lower system maximum; fall back to no app-side limit.
            multiPickerUnlimited.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    LaunchedEffect(screen) {
        if (screen == Screen.Home) {
            recents = withContext(Dispatchers.IO) { RecentsStore.list(context) }
        }
    }
    LaunchedEffect(recents) {
        recents.forEach { uri ->
            val key = uri.toString()
            if (!recentThumbs.containsKey(key)) {
                recentThumbs[key] = null
                val thumb = withContext(Dispatchers.IO) { RecentsStore.loadThumbnail(context, uri) }
                recentThumbs[key] = thumb?.asImageBitmap()
            }
        }
    }

    BackHandler(enabled = screen != Screen.Home) {
        screen = when (screen) {
            Screen.Export -> Screen.Editor
            Screen.Print -> Screen.Home
            else -> Screen.Home
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            if (screen == Screen.Editor || screen == Screen.Export || screen == Screen.Print) {
                TopAppBar(
                    title = {
                        Text(
                            when (screen) {
                                Screen.Editor -> "Resize Image"
                                Screen.Export -> "Export"
                                else -> "Print Size & DPI"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { screen = if (screen == Screen.Export) Screen.Editor else Screen.Home }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (screen == Screen.Editor) {
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
            Screen.Editor -> EditorScreen(
                modifier = Modifier.padding(padding),
                queueLabel = if (photoQueue.size > 1) "Photo ${queueIndex + 1} of ${photoQueue.size}" else null,
                working = workingBitmap,
                processed = processedBitmap,
                widthText = inputWidth,
                heightText = inputHeight,
                customPercent = customPercent,
                percentError = percentError,
                lockRatio = lockRatio,
                busy = busy,
                busyLabel = busyLabel,
                beforeAfter = beforeAfter,
                tool = tool,
                sourceBytes = sourceBytes,
                estimatedBytes = estimatedBytes,
                onBeforeAfter = { beforeAfter = it },
                onTool = { selected ->
                    tool = selected
                    when (selected) {
                        Tool.Resize -> Unit
                        Tool.Presets -> { screen = Screen.Home; tab = MainTab.Presets }
                        else -> scope.launch { snackbar.showSnackbar("${selected.label} is planned for a later stage. Resize and export are available now.") }
                    }
                },
                onWidth = { value ->
                    inputWidth = value
                    invalidateProcessed()
                    if (lockRatio) {
                        val old = workingBitmap
                        val newWidth = value.toIntOrNull()
                        if (old != null && newWidth != null && newWidth > 0) {
                            val size = runCatching { ResizeMath.fromWidth(old.width, old.height, newWidth) }.getOrNull()
                            if (size != null) inputHeight = size.height.toString()
                        }
                    }
                },
                onHeight = { value ->
                    inputHeight = value
                    invalidateProcessed()
                    val old = workingBitmap
                    val newHeight = value.toIntOrNull()
                    if (lockRatio && old != null && newHeight != null && newHeight > 0) {
                        val newWidth = (newHeight.toDouble() * old.width / old.height).toInt().coerceAtLeast(1)
                        inputWidth = newWidth.toString()
                    }
                },
                onLockRatio = { lockRatio = it },
                onPercent = { applyPercent(it) },
                onCustomPercent = { value ->
                    customPercent = value
                    val p = value.toDoubleOrNull()
                    if (value.isBlank()) {
                        percentError = ""
                    } else if (p == null || p < 10.0 || p > 200.0) {
                        percentError = "Choose a percentage between 10 and 200."
                    } else {
                        percentError = ""
                        applyPercent(p)
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
                onProcess = { onProcess() },
                onExport = { screen = Screen.Export }
            )
            Screen.Export -> ExportScreen(
                modifier = Modifier.padding(padding),
                processed = processedBitmap,
                sourceBytes = sourceBytes,
                estimatedBytes = estimatedBytes,
                exportName = exportName,
                onName = { exportName = it },
                format = outFormat(),
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
                },
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
                },
                onAnother = {
                    screen = Screen.Home
                    tab = MainTab.Home
                    workingBitmap = null
                    processedBitmap = null
                    sourceUri = null
                    photoQueue = emptyList()
                    queueIndex = 0
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
                Text("PixelCraft", color = DeepNavy, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("IMAGE RESIZER", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Icon(Icons.Default.Tune, contentDescription = "Settings", tint = DeepNavy, modifier = Modifier.size(26.dp))
        }
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Resize, crop and prepare photos in seconds", color = DeepNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
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
                        border = BorderStroke(1.dp, Color(0xFFE5EEF4)),
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
private fun EditorScreen(
    modifier: Modifier,
    queueLabel: String?,
    working: Bitmap?,
    processed: Bitmap?,
    widthText: String,
    heightText: String,
    customPercent: String,
    percentError: String,
    lockRatio: Boolean,
    busy: Boolean,
    busyLabel: String,
    beforeAfter: Boolean,
    tool: Tool,
    sourceBytes: Long,
    estimatedBytes: Long,
    onBeforeAfter: (Boolean) -> Unit,
    onTool: (Tool) -> Unit,
    onWidth: (String) -> Unit,
    onHeight: (String) -> Unit,
    onLockRatio: (Boolean) -> Unit,
    onPercent: (Double) -> Unit,
    onCustomPercent: (String) -> Unit,
    onRotate: () -> Unit,
    onProcess: () -> Unit,
    onExport: () -> Unit
) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        if (queueLabel != null) {
            Text(queueLabel, color = SkyBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(false to "Before", true to "After").forEach { (after, label) ->
                val selected = beforeAfter == after
                val enabled = !after || processed != null
                OutlinedButton(
                    onClick = { onBeforeAfter(after) },
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) PaleBlue else Color.Transparent)
                ) { Text(label, color = if (enabled) DeepNavy else Slate) }
            }
        }
        Box(
            Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(18.dp)).background(SoftGrey).border(1.dp, BorderGrey, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            val display = if (beforeAfter) processed else working
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
            beforeAfter && processed != null -> {
                val est = if (estimatedBytes >= 0) "  ·  estimated ${formatBytes(estimatedBytes)}" else ""
                "After: ${processed.width} × ${processed.height} px$est"
            }
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = widthText, onValueChange = onWidth, modifier = Modifier.weight(1f), label = { Text("Width (px)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = heightText, onValueChange = onHeight, modifier = Modifier.weight(1f), label = { Text("Height (px)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = lockRatio, onCheckedChange = onLockRatio)
                Text("Lock aspect ratio", color = DeepNavy, fontSize = 14.sp)
            }
            Text("Quick percentage", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp).horizontalScroll(rememberScrollState())) {
                listOf(25, 50, 75, 100, 125, 150, 200).forEach { percent ->
                    Surface(modifier = Modifier.clickable { onPercent(percent.toDouble()) }, shape = RoundedCornerShape(9.dp), color = PaleBlue) {
                        Text(
                            "$percent%",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            fontSize = 11.sp,
                            color = DeepNavy,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
            OutlinedTextField(
                value = customPercent,
                onValueChange = onCustomPercent,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                label = { Text("Custom percentage (10-200)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = percentError.isNotBlank()
            )
            if (percentError.isNotBlank()) {
                Text(percentError, color = Color(0xFFB3261E), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
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
    sourceBytes: Long,
    estimatedBytes: Long,
    exportName: String,
    onName: (String) -> Unit,
    format: ExportFormat,
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
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Box(
            Modifier.fillMaxWidth().padding(vertical = 8.dp).height(220.dp).clip(RoundedCornerShape(18.dp)).background(SoftGrey).border(1.dp, BorderGrey, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            val preview = remember(processed) { processed?.asImageBitmap() }
            if (preview != null) {
                Image(bitmap = preview, contentDescription = "Processed image preview", modifier = Modifier.fillMaxSize().padding(8.dp), contentScale = ContentScale.Fit)
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
        Text(
            "Format: ${format.name} (same as the original)" + if (format == ExportFormat.PNG) " · PNG ignores quality" else "",
            color = Slate,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (format != ExportFormat.PNG && processed != null) {
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
            "The original photo is never changed unless you choose Replace original. New files go to Pictures/PixelCraft.",
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
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E9D68), modifier = Modifier.size(70.dp))
        Spacer(Modifier.height(16.dp))
        Text("Saved successfully", color = DeepNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(name, color = DeepNavy, modifier = Modifier.padding(top = 8.dp), textAlign = TextAlign.Center)
        Text(size, color = Slate, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
        if (nextLabel != null) {
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White)) { Text(nextLabel, color = Color.White) }
            Spacer(Modifier.height(8.dp))
        }
        Button(onClick = onAnother, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("Edit another photo", color = Color.White) }
        OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Done", color = DeepNavy) }
    }
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
                                    Text("Verified", color = Color(0xFF2E9D68), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
        Text("Version 0.2.0 · Learnify Zone", color = Slate, fontSize = 13.sp)
        Text("Stage 2 build: process-then-export, aspect-ratio choices, camera capture, recently edited, quality control. Ads are not enabled in this development version.", color = Slate, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 14.dp))
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
        Text("Print Size & DPI", color = DeepNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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

private fun decodeOrientedBitmap(context: Context, uri: Uri): Bitmap {
    val bounds = context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.Options().apply { inJustDecodeBounds = true }.also { options -> BitmapFactory.decodeStream(stream, null, options) }
    } ?: throw IllegalArgumentException("Cannot read image")
    val maxSide = maxOf(bounds.outWidth, bounds.outHeight)
    var sample = 1
    while (maxSide / sample > 4096) sample *= 2
    val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, BitmapFactory.Options().apply { inSampleSize = sample; inPreferredConfig = Bitmap.Config.ARGB_8888 })
    } ?: throw IllegalArgumentException("Unsupported image")
    val orientation = context.contentResolver.openInputStream(uri)?.use { stream ->
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
        val baos = ByteArrayOutputStream()
        val q = if (format == ExportFormat.PNG) 100 else quality
        if (bitmap.compress(format.compressFormat(), q, baos)) baos.size().toLong() else -1L
    } catch (e: Exception) {
        -1L
    }
}

private fun saveToGallery(context: Context, filename: String, mimeType: String, bytes: ByteArray) {
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
    val base = cleaned.replace(Regex("\\.(png|jpe?g|webp)$", RegexOption.IGNORE_CASE), "").ifBlank { "pixelcraft-image" }
    return "$base.${format.extension}"
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "${DecimalFormat("#,##0.#").format(kb)} KB"
    return "${DecimalFormat("#,##0.##").format(kb / 1024.0)} MB"
}
