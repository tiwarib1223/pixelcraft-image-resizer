package com.learnifyzone.pixelcraft

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.ContentCut
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
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
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
import com.learnifyzone.pixelcraft.image.BackgroundRemoval
import com.learnifyzone.pixelcraft.image.ExportFormat
import com.learnifyzone.pixelcraft.image.SaveUtils
import com.learnifyzone.pixelcraft.image.Transforms
import com.learnifyzone.pixelcraft.image.CropMath
import com.learnifyzone.pixelcraft.image.SocialPlatform
import com.learnifyzone.pixelcraft.image.SocialSize
import com.learnifyzone.pixelcraft.image.CompressUtils
import com.learnifyzone.pixelcraft.image.PrintUtils
import com.learnifyzone.pixelcraft.image.ConvertFormats
import com.learnifyzone.pixelcraft.image.ShopSizes
import com.learnifyzone.pixelcraft.image.SocialSizes
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
private enum class Screen { Home, Editor, Export, PrintHome, PrintSetup, PrintPreview, PrintExport, Success, QueueOverview, QueueResult, SocialHome, SocialPlatform, SocialEdit, ShopHome, ShopOther, ShopPlatform, ShopEdit, ShopCollage, ConvertHome, ConvertEdit, ConvertResult, CompressHome, CompressSettings, CompressResult }
private enum class Tool(val label: String) { Resize("Resize"), Presets("Presets"), Crop("Crop & Rotate"), Background("Background"), Circle("Circle Cutter"), Format("Format") }

private enum class CropTransform { LEFT, RIGHT, FLIP_H, FLIP_V }
private enum class BgChoice(val label: String) { TRANSPARENT("Transparent"), WHITE("White"), CUSTOM("Custom Color") }
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
    var pendingBgTool by remember { mutableStateOf(false) }
    var pendingCropTool by remember { mutableStateOf(false) }
    var cropRect by remember { mutableStateOf(CropMath.defaultRect()) }
    var cropAspect by rememberSaveable { mutableStateOf("Free") }
    var straightenAngle by rememberSaveable { mutableStateOf(0f) }
    var tiltHAngle by rememberSaveable { mutableStateOf(0f) }
    var tiltVAngle by rememberSaveable { mutableStateOf(0f) }
    var adjustMode by rememberSaveable { mutableStateOf("Straighten") }
    var bgCutout by remember { mutableStateOf<Bitmap?>(null) }
    var bgPreview by remember { mutableStateOf<Bitmap?>(null) }
    var bgChoice by remember { mutableStateOf(BgChoice.TRANSPARENT) }
    var bgCustomColor by remember { mutableLongStateOf(0xFF42A5F5) }
    var bgUnit by remember { mutableStateOf("px") }
    var bgWidth by remember { mutableStateOf("") }
    var bgHeight by remember { mutableStateOf("") }
    var bgDpi by remember { mutableStateOf("300") }
    var bgError by remember { mutableStateOf("") }
    var pendingCircleTool by remember { mutableStateOf(false) }
    var pendingSocialTool by remember { mutableStateOf(false) }
    var exportReturnToSocial by remember { mutableStateOf(false) }
    var socialPlatform by rememberSaveable { mutableStateOf("") }
    var socialTarget by remember { mutableStateOf<SocialSize?>(null) }
    var socialCustomW by rememberSaveable { mutableStateOf("") }
    var socialCustomH by rememberSaveable { mutableStateOf("") }
    var socialMode by rememberSaveable { mutableStateOf("Crop") }
    var socialRect by remember { mutableStateOf(CropMath.defaultRect()) }
    var socialZoom by remember { mutableStateOf(1f) }
    var socialPanX by remember { mutableStateOf(0f) }
    var socialPanY by remember { mutableStateOf(0f) }
    var socialFill by rememberSaveable { mutableStateOf("Blur") }
    var socialCustomColor by remember { mutableLongStateOf(0xFF42A5F5) }
    var pendingShopTool by remember { mutableStateOf(false) }
    var exportReturnToShop by remember { mutableStateOf(false) }
    var exportReturnToCollage by remember { mutableStateOf(false) }
    var shopPlatform by rememberSaveable { mutableStateOf("") }
    var shopFromOther by rememberSaveable { mutableStateOf(false) }
    var shopTarget by remember { mutableStateOf<SocialSize?>(null) }
    var shopCustomW by rememberSaveable { mutableStateOf("") }
    var shopCustomH by rememberSaveable { mutableStateOf("") }
    var shopMode by rememberSaveable { mutableStateOf("Crop") }
    var shopRect by remember { mutableStateOf(CropMath.defaultRect()) }
    var shopZoom by remember { mutableStateOf(1f) }
    var shopPanX by remember { mutableStateOf(0f) }
    var shopPanY by remember { mutableStateOf(0f) }
    var shopFill by rememberSaveable { mutableStateOf("White") }
    var shopCustomColor by remember { mutableLongStateOf(0xFF42A5F5) }
    var collageTarget by remember { mutableStateOf<SocialSize?>(null) }
    var collageLayout by rememberSaveable { mutableStateOf(2) }
    val collagePhotos = remember { mutableStateListOf<Bitmap>() }
    var pendingConvertTool by remember { mutableStateOf(false) }
    var convertGoEdit by remember { mutableStateOf(false) }
    val convertUris = remember { mutableStateListOf<Uri>() }
    val convertThumbs = remember { mutableStateListOf<Bitmap>() }
    val convertLabels = remember { mutableStateListOf<String>() }
    val convertOrigSizes = remember { mutableStateListOf<Long>() }
    var convertTarget by remember { mutableStateOf(ConvertFormats.targets.first()) }
    var convertQuality by rememberSaveable { mutableIntStateOf(90) }
    var convertBg by rememberSaveable { mutableStateOf("White") }
    var convertCustomColor by remember { mutableLongStateOf(0xFF42A5F5) }
    var convertResultBytes by remember { mutableStateOf<ByteArray?>(null) }
    var convertResultName by remember { mutableStateOf("") }
    var convertResultPreview by remember { mutableStateOf<Bitmap?>(null) }
    var convertResultOrigSize by remember { mutableLongStateOf(-1L) }
    var convertResultFrom by remember { mutableStateOf("") }
    var convertSaved by remember { mutableStateOf(false) }
    val convertReadyBitmaps = remember { mutableStateListOf<Bitmap?>() }
    var pendingPdfTestPath by remember { mutableStateOf<String?>(null) }
    var convertBatchResults by remember { mutableStateOf<List<Pair<String, ByteArray>>?>(null) }
    var convertBatchOutTotal by remember { mutableLongStateOf(-1L) }
    var convertBatchIsPdf by remember { mutableStateOf(false) }
    var pendingCompressTool by remember { mutableStateOf(false) }
    var compressGoSettings by remember { mutableStateOf(false) }
    val compressUris = remember { mutableStateListOf<Uri>() }
    val compressThumbs = remember { mutableStateListOf<Bitmap>() }
    val compressLabels = remember { mutableStateListOf<String>() }
    val compressNames = remember { mutableStateListOf<String>() }
    val compressOrigSizes = remember { mutableStateListOf<Long>() }
    var compressIsPdf by remember { mutableStateOf(false) }
    var compressPdfPages by remember { mutableStateOf<List<Bitmap>?>(null) }
    var compressMode by rememberSaveable { mutableStateOf("Quality") }
    var compressQuality by rememberSaveable { mutableIntStateOf(80) }
    var compressTargetKb by rememberSaveable { mutableStateOf("500") }
    var compressResizeOn by rememberSaveable { mutableStateOf(false) }
    var compressResizePct by rememberSaveable { mutableIntStateOf(75) }
    var compressResultBytes by remember { mutableStateOf<ByteArray?>(null) }
    var compressResultName by remember { mutableStateOf("") }
    var compressResultMime by remember { mutableStateOf("") }
    var compressResultOrig by remember { mutableLongStateOf(-1L) }
    var compressBefore by remember { mutableStateOf<Bitmap?>(null) }
    var compressAfter by remember { mutableStateOf<Bitmap?>(null) }
    var compressBatchFiles by remember { mutableStateOf<List<Pair<String, ByteArray>>?>(null) }
    var compressBatchOrig by remember { mutableStateOf<List<Long>?>(null) }
    var compressSaved by remember { mutableStateOf(false) }

    // ---- Print Size & DPI state ----
    var printUri by remember { mutableStateOf<Uri?>(null) }
    var printThumb by remember { mutableStateOf<Bitmap?>(null) }
    var printPreview by remember { mutableStateOf<Bitmap?>(null) }
    var printName by remember { mutableStateOf("image") }
    var printLabel by remember { mutableStateOf("") }
    var printOrigSize by remember { mutableStateOf(-1L) }
    var printImgW by remember { mutableStateOf(0) }
    var printImgH by remember { mutableStateOf(0) }
    var printPreset by remember { mutableStateOf("A4") }
    var printUnit by remember { mutableStateOf("in") }
    var printWText by remember { mutableStateOf("") }
    var printHText by remember { mutableStateOf("") }
    var printLandscape by remember { mutableStateOf(false) }
    var printLockAspect by remember { mutableStateOf(true) }
    var printBleedOn by remember { mutableStateOf(false) }
    var printPpi by remember { mutableStateOf(300) }
    var printFitMode by remember { mutableStateOf("Fit") }
    var printMarginsOn by remember { mutableStateOf(false) }
    var printCropMarks by remember { mutableStateOf(false) }
    var printBgWhite by remember { mutableStateOf(true) }
    var printFormat by remember { mutableStateOf("JPG") }
    var printFilename by remember { mutableStateOf("print-ready") }
    var printJpgQuality by remember { mutableStateOf(92) }
    var printResultBytes by remember { mutableStateOf<ByteArray?>(null) }
    var printResultName by remember { mutableStateOf("") }
    var printResultMime by remember { mutableStateOf("") }
    var printResultDims by remember { mutableStateOf("") }
    var printSaved by remember { mutableStateOf(false) }
    var printPdfUri by remember { mutableStateOf<Uri?>(null) }
    val printPdfThumbs = remember { mutableStateMapOf<Int, Bitmap>() }
    var printPdfPageCount by remember { mutableStateOf(0) }
    var printPdfTempFile by remember { mutableStateOf<File?>(null) }
    val printPdfThumbQueue = remember { java.util.concurrent.ConcurrentLinkedQueue<Int>() }
    val printPdfThumbQueued = remember { java.util.Collections.synchronizedSet(mutableSetOf<Int>()) }
    var printPdfThumbWorkerRunning by remember { mutableStateOf(false) }
    var printPdfPickerOpen by remember { mutableStateOf(false) }
    var printPdfAllPages by remember { mutableStateOf(false) }
    val printPdfSelected = remember { mutableStateSetOf<Int>() }
    var printPdfPageList by remember { mutableStateOf<List<Int>?>(null) }
    val printResultFiles = remember { mutableStateListOf<File>() }
    var printPageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var printSheetOn by remember { mutableStateOf(false) }
    var printSheet by remember { mutableStateOf("4 × 6") }
    var printCopyCount by remember { mutableStateOf(4) }
    var pendingPrintTool by remember { mutableStateOf(false) }
    var printGoSetup by remember { mutableStateOf(false) }
    var convertPdfPages by remember { mutableStateOf<List<Bitmap>?>(null) }
    val convertPdfChecked = remember { mutableStateListOf<Boolean>() }
    var circleShape by rememberSaveable { mutableStateOf("Circle") }
    var circleRect by remember { mutableStateOf(CropMath.defaultRect()) }
    var circleChoice by remember { mutableStateOf(BgChoice.TRANSPARENT) }
    var circleCustomColor by remember { mutableLongStateOf(0xFF42A5F5) }
    var circleUnit by remember { mutableStateOf("px") }
    var circleSize by remember { mutableStateOf("") }
    var circleDpi by remember { mutableStateOf("300") }
    var circlePreview by remember { mutableStateOf<Bitmap?>(null) }
    fun circleDpiValue(): Double = circleDpi.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 } ?: 300.0
    var exportDpi by remember { mutableIntStateOf(0) }
    fun bgDpiValue(): Double = bgDpi.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 } ?: 300.0
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
                bgCutout = null
                bgPreview = null
                bgError = ""
                bgWidth = ""
                bgHeight = ""
                exportDpi = 0
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
                cropRect = CropMath.defaultRect()
                cropAspect = "Free"
                straightenAngle = 0f
                tiltHAngle = 0f
                tiltVAngle = 0f
                if (circleSize.isBlank()) circleSize = minOf(loaded.width, loaded.height).toString()
                tool = when {
                    pendingBgTool -> { pendingBgTool = false; Tool.Background }
                    pendingCropTool -> { pendingCropTool = false; Tool.Crop }
                    pendingCircleTool -> { pendingCircleTool = false; circleRect = CropMath.maxRectForAspect("1:1", loaded.width, loaded.height); Tool.Circle }
                    else -> Tool.Resize
                }
                if (pendingSocialTool) {
                    pendingSocialTool = false
                    socialTarget?.let { t -> socialRect = CropMath.maxRectForRatio(t.w.toFloat() / t.h.toFloat(), loaded.width, loaded.height) }
                    screen = if (socialTarget != null) Screen.SocialEdit else Screen.SocialHome
                }
                if (pendingShopTool) {
                    pendingShopTool = false
                    shopTarget?.let { t -> shopRect = CropMath.maxRectForRatio(t.w.toFloat() / t.h.toFloat(), loaded.width, loaded.height) }
                    screen = if (shopTarget != null) Screen.ShopEdit else Screen.ShopHome
                }
                if (pendingConvertTool) {
                    pendingConvertTool = false
                    convertUris.add(uri)
                    convertThumbs.add(thumbOf(loaded))
                    convertLabels.add(ConvertFormats.detectFormat(context, uri))
                    convertOrigSizes.add(querySourceSize(context, uri))
                    convertReadyBitmaps.add(null)
                    screen = if (convertGoEdit) { convertGoEdit = false; Screen.ConvertEdit } else Screen.ConvertHome
                }
                if (pendingCompressTool) {
                    pendingCompressTool = false
                    compressUris.clear(); compressThumbs.clear(); compressLabels.clear(); compressNames.clear(); compressOrigSizes.clear()
                    compressIsPdf = false; compressPdfPages = null
                    compressUris.add(uri)
                    compressThumbs.add(thumbOf(loaded))
                    compressLabels.add(ConvertFormats.detectFormat(context, uri))
                    compressNames.add(queryDisplayName(context, uri) ?: "image")
                    compressOrigSizes.add(querySourceSize(context, uri))
                    screen = if (compressGoSettings) { compressGoSettings = false; Screen.CompressSettings } else Screen.CompressHome
                }
                if (pendingPrintTool) {
                    pendingPrintTool = false
                    printUri = uri
                    printImgW = loaded.width; printImgH = loaded.height
                    printThumb = thumbOf(loaded)
                    printPreview = loaded
                    printName = queryDisplayName(context, uri) ?: "image"
                    printLabel = ConvertFormats.detectFormat(context, uri)
                    printOrigSize = querySourceSize(context, uri)
                    printResultBytes = null; printSaved = false
                    screen = if (printGoSetup) { printGoSetup = false; Screen.PrintSetup } else Screen.PrintHome
                }
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
            val testTool = (context as? Activity)?.intent?.getStringExtra("pixelcraft.test.tool")
            if (testTool == "crop") pendingCropTool = true
            if (testTool == "circle") pendingCircleTool = true
            if (testTool == "social") pendingSocialTool = true
            if (testTool == "shop") pendingShopTool = true
            if (testTool == "convert") pendingConvertTool = true
            if (testTool == "convertedit") { pendingConvertTool = true; convertGoEdit = true }
            (context as? Activity)?.intent?.getStringExtra("pixelcraft.test.pdf")?.let { pdfPath ->
                pendingPdfTestPath = pdfPath
            }
            if (testTool == "compress") pendingCompressTool = true
            if (testTool == "compresssettings") { pendingCompressTool = true; compressGoSettings = true }
            if (testTool == "print") pendingPrintTool = true
            if (testTool == "printsetup") { pendingPrintTool = true; printGoSetup = true }
            loadPhoto(Uri.fromFile(File(testPath)), isCamera = false)
        }
    }

    // Live preview of the cut-out on the chosen background (small canvas for speed;
    // the full-size composite is only built at export time).
    LaunchedEffect(bgCutout, bgChoice, bgCustomColor, bgWidth, bgHeight, bgUnit, bgDpi) {
        val cut = bgCutout
        bgPreview = if (cut == null) null else withContext(Dispatchers.Default) {
            val dpi = bgDpiValue()
            val tw = parseToPx(bgWidth, bgUnit, dpi) ?: cut.width
            val th = parseToPx(bgHeight, bgUnit, dpi) ?: cut.height
            val scale = minOf(1f, 1200f / maxOf(tw, th).coerceAtLeast(1))
            composeOnBackground(cut, maxOf(1, (tw * scale).roundToInt()), maxOf(1, (th * scale).roundToInt()), bgChoice, bgCustomColor)
        }
    }

    fun runRemoveBackground() {
        val src = workingBitmap ?: return
        busy = true
        busyLabel = "Removing background\u2026"
        scope.launch {
            try {
                val fg = withContext(Dispatchers.IO) { BackgroundRemoval.cutOut(context, src) }
                bgCutout = fg
                bgError = ""
                if (bgWidth.isBlank()) bgWidth = fmtUnit(fg.width.toDouble(), bgUnit, bgDpiValue())
                if (bgHeight.isBlank()) bgHeight = fmtUnit(fg.height.toDouble(), bgUnit, bgDpiValue())
            } catch (e: Exception) {
                bgError = "Couldn't remove the background. If this is the first use, connect to the internet once so the on-device model can download, then try again."
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun convertBgToUnit(from: String, to: String) {
        if (from == to) return
        val dpi = bgDpiValue()
        val wPx = parseToPx(bgWidth, from, dpi)
        val hPx = parseToPx(bgHeight, from, dpi)
        bgUnit = to
        bgWidth = wPx?.let { fmtUnit(it.toDouble(), to, dpi) } ?: ""
        bgHeight = hPx?.let { fmtUnit(it.toDouble(), to, dpi) } ?: ""
    }

    fun continueBgExport() {
        val cut = bgCutout ?: return
        val dpi = bgDpiValue().coerceIn(72.0, 1200.0)
        val tw = parseToPx(bgWidth, bgUnit, dpi)?.coerceIn(16, 12000) ?: cut.width
        val th = parseToPx(bgHeight, bgUnit, dpi)?.coerceIn(16, 12000) ?: cut.height
        busy = true
        busyLabel = "Preparing export\u2026"
        scope.launch {
            try {
                val out = withContext(Dispatchers.Default) { composeOnBackground(cut, tw, th, bgChoice, bgCustomColor) }
                processedBitmap = out
                exportDpi = dpi.roundToInt()
                if (bgChoice == BgChoice.TRANSPARENT && outFormat() == ExportFormat.JPEG) chosenFormat = "PNG"
                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(out, outFormat(), quality) }
                exportReturnToSocial = false; exportReturnToShop = false; exportReturnToCollage = false
                screen = Screen.Export
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun convertCircleToUnit(from: String, to: String) {
        if (from == to) return
        val dpi = circleDpiValue()
        val sPx = parseToPx(circleSize, from, dpi)
        circleUnit = to
        circleSize = sPx?.let { fmtUnit(it.toDouble(), to, dpi) } ?: ""
    }

    // Live preview of the shape cut-out (small canvas for speed; full size at export).
    LaunchedEffect(tool, workingBitmap, circleRect, circleShape, circleChoice, circleCustomColor, circleSize, circleUnit, circleDpi) {
        if (tool != Tool.Circle) return@LaunchedEffect
        val src = workingBitmap
        circlePreview = if (src == null) null else withContext(Dispatchers.Default) {
            val s = (parseToPx(circleSize, circleUnit, circleDpiValue()) ?: minOf(src.width, src.height)).coerceIn(16, 384)
            composeOnBackground(shapeCutout(src, circleRect, circleShape, s), s, s, circleChoice, circleCustomColor)
        }
    }

    fun applyCircle() {
        val src = workingBitmap ?: return
        val dpi = circleDpiValue().coerceIn(72.0, 1200.0)
        val s = (parseToPx(circleSize, circleUnit, dpi) ?: minOf(src.width, src.height)).coerceIn(16, 12000)
        busy = true
        busyLabel = "Cutting shape…"
        scope.launch {
            try {
                val out = withContext(Dispatchers.Default) {
                    composeOnBackground(shapeCutout(src, circleRect, circleShape, s), s, s, circleChoice, circleCustomColor)
                }
                processedBitmap = out
                exportName = suggestBaseName(out.width, out.height)
                exportDpi = dpi.roundToInt()
                beforeAfter = true
                // Transparent needs real alpha - JPG has none, so force PNG like the Background tool.
                if (circleChoice == BgChoice.TRANSPARENT && outFormat() == ExportFormat.JPEG) chosenFormat = "PNG"
                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(out, outFormat(), quality) }
                exportReturnToSocial = false; exportReturnToShop = false; exportReturnToCollage = false
                screen = Screen.Export
                snackbar.showSnackbar("$circleShape cut: ${out.width} × ${out.height} px.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't cut this shape. Try a smaller size.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun selectSocialTarget(t: SocialSize) {
        socialTarget = t
        socialMode = "Crop"
        socialZoom = 1f
        socialPanX = 0f
        socialPanY = 0f
        val src = workingBitmap
        socialRect = if (src != null) CropMath.maxRectForRatio(t.w.toFloat() / t.h.toFloat(), src.width, src.height) else CropMath.defaultRect()
        screen = Screen.SocialEdit
    }

    fun applySocial() {
        val src = workingBitmap ?: return
        val t = socialTarget ?: return
        busy = true
        busyLabel = "Exporting…"
        scope.launch {
            try {
                val out = withContext(Dispatchers.Default) {
                    when (socialMode) {
                        "Position" -> Transforms.socialPosition(src, t.w, t.h, socialZoom, socialPanX, socialPanY)
                        "Background" -> Transforms.socialFit(src, t.w, t.h, when (socialFill) {
                            "White" -> Transforms.SocialFill.WHITE
                            "Custom Color" -> Transforms.SocialFill.CUSTOM
                            "Transparent" -> Transforms.SocialFill.TRANSPARENT
                            else -> Transforms.SocialFill.BLUR
                        }, socialCustomColor.toInt())
                        else -> Transforms.socialCrop(src, CropMath.toPixels(socialRect, src.width, src.height), t.w, t.h)
                    }
                }
                processedBitmap = out
                exportName = suggestBaseName(t.w, t.h)
                exportDpi = 0
                beforeAfter = true
                if (socialMode == "Background" && socialFill == "Transparent" && outFormat() == ExportFormat.JPEG) chosenFormat = "PNG"
                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(out, outFormat(), quality) }
                exportReturnToSocial = true
                screen = Screen.Export
                snackbar.showSnackbar("Ready: ${t.w} × ${t.h} px for $socialPlatform.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't export. Try a smaller target size.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun selectShopTarget(t: SocialSize) {
        shopTarget = t
        shopMode = "Crop"
        shopZoom = 1f
        shopPanX = 0f
        shopPanY = 0f
        val src = workingBitmap
        shopRect = if (src != null) CropMath.maxRectForRatio(t.w.toFloat() / t.h.toFloat(), src.width, src.height) else CropMath.defaultRect()
        screen = Screen.ShopEdit
    }

    fun bakeShop(src: Bitmap): Bitmap? {
        val t = shopTarget ?: return null
        return when (shopMode) {
            "Position" -> Transforms.socialPosition(src, t.w, t.h, shopZoom, shopPanX, shopPanY)
            "Background" -> Transforms.socialFit(src, t.w, t.h, when (shopFill) {
                "Blur" -> Transforms.SocialFill.BLUR
                "Custom Color" -> Transforms.SocialFill.CUSTOM
                "Transparent" -> Transforms.SocialFill.TRANSPARENT
                else -> Transforms.SocialFill.WHITE
            }, shopCustomColor.toInt())
            else -> Transforms.socialCrop(src, CropMath.toPixels(shopRect, src.width, src.height), t.w, t.h)
        }
    }

    fun applyShop() {
        val src = workingBitmap ?: return
        val t = shopTarget ?: return
        busy = true
        busyLabel = "Exporting…"
        scope.launch {
            try {
                val out = withContext(Dispatchers.Default) { bakeShop(src) } ?: return@launch
                processedBitmap = out
                exportName = suggestBaseName(t.w, t.h)
                exportDpi = 0
                beforeAfter = true
                if (shopMode == "Background" && shopFill == "Transparent" && outFormat() == ExportFormat.JPEG) chosenFormat = "PNG"
                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(out, outFormat(), quality) }
                exportReturnToShop = true
                screen = Screen.Export
                snackbar.showSnackbar("Ready: ${t.w} × ${t.h} px for $shopPlatform.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't export. Try a smaller target size.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun applyShopBatch(uris: List<Uri>) {
        val t = shopTarget ?: return
        busy = true
        scope.launch {
            var done = 0
            var failed = 0
            try {
                uris.forEachIndexed { i, uri ->
                    busyLabel = "Exporting ${i + 1}/${uris.size}…"
                    try {
                        val src = withContext(Dispatchers.IO) { decodeOrientedBitmap(context, uri) }
                        val out = withContext(Dispatchers.Default) {
                            // Batch crops centered: per-photo frames make no sense across many photos.
                            if (shopMode == "Crop") {
                                val rect = CropMath.toPixels(CropMath.maxRectForRatio(t.w.toFloat() / t.h.toFloat(), src.width, src.height), src.width, src.height)
                                Transforms.socialCrop(src, rect, t.w, t.h)
                            } else bakeShop(src)
                        } ?: return@forEachIndexed
                        val fmt = if (shopMode == "Background" && shopFill == "Transparent") ExportFormat.PNG else outFormat()
                        val bytes = withContext(Dispatchers.Default) {
                            val baos = ByteArrayOutputStream()
                            out.compress(fmt.compressFormat(), if (fmt == ExportFormat.PNG) 100 else quality, baos)
                            out.recycle()
                            baos.toByteArray()
                        }
                        withContext(Dispatchers.IO) {
                            saveToGallery(context, "pixelcraft_${t.w}x${t.h}_${i + 1}.${fmt.extension}", fmt.mimeType, bytes)
                        }
                        done++
                    } catch (e: Exception) { failed++ }
                }
                snackbar.showSnackbar(if (failed == 0) "Saved $done photos to Pictures/PixelCraft." else "Saved $done photos, $failed failed.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun loadCollagePhotos(uris: List<Uri>) {
        busy = true
        busyLabel = "Loading photos…"
        scope.launch {
            try {
                val loaded = mutableListOf<Bitmap>()
                uris.take(collageLayout).forEach { uri ->
                    withContext(Dispatchers.IO) { decodeOrientedBitmap(context, uri) }?.let { loaded.add(it) }
                }
                collagePhotos.clear()
                collagePhotos.addAll(loaded)
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun loadConvertPhotos(uris: List<Uri>) {
        busy = true
        busyLabel = "Loading photos…"
        scope.launch {
            try {
                val newUris = mutableListOf<Uri>()
                val newThumbs = mutableListOf<Bitmap>()
                val newLabels = mutableListOf<String>()
                val newSizes = mutableListOf<Long>()
                uris.take(20).forEach { u ->
                    withContext(Dispatchers.IO) { decodeOrientedBitmap(context, u) }?.let { bmp ->
                        newUris.add(u)
                        newThumbs.add(thumbOf(bmp))
                        newLabels.add(ConvertFormats.detectFormat(context, u))
                        newSizes.add(querySourceSize(context, u))
                    }
                }
                convertUris.clear(); convertUris.addAll(newUris)
                convertThumbs.clear(); convertThumbs.addAll(newThumbs)
                convertLabels.clear(); convertLabels.addAll(newLabels)
                convertOrigSizes.clear(); convertOrigSizes.addAll(newSizes)
                convertReadyBitmaps.clear(); repeat(newUris.size) { convertReadyBitmaps.add(null) }
                screen = if (convertGoEdit && convertUris.isNotEmpty()) { convertGoEdit = false; Screen.ConvertEdit } else Screen.ConvertHome
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun loadPdfInput(uri: Uri) {
        busy = true
        busyLabel = "Reading PDF…"
        scope.launch {
            try {
                val pages = withContext(Dispatchers.IO) {
                    ConvertFormats.withSeekablePdf(context, uri) { ConvertFormats.renderPdfPages(it) }
                }
                if (pages.isEmpty()) throw IllegalStateException("No pages")
                convertPdfPages = pages
                convertPdfChecked.clear()
                repeat(pages.size) { convertPdfChecked.add(true) }
                busy = false
                busyLabel = ""
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Couldn't read this PDF. Try another file.")
            }
        }
    }

    fun confirmPdfPages() {
        val pages = convertPdfPages ?: return
        val chosen = pages.filterIndexed { i, _ -> convertPdfChecked.getOrNull(i) == true }
        if (chosen.isEmpty()) {
            scope.launch { snackbar.showSnackbar("Pick at least one page.") }
            return
        }
        convertUris.clear()
        convertThumbs.clear()
        convertLabels.clear()
        convertOrigSizes.clear()
        convertReadyBitmaps.clear()
        chosen.forEachIndexed { i, page ->
            convertUris.add(Uri.EMPTY)
            convertThumbs.add(thumbOf(page))
            convertLabels.add("PDF p.${pages.indexOf(page) + 1}")
            convertOrigSizes.add(-1L)
            convertReadyBitmaps.add(page)
        }
        convertPdfPages = null
        convertPdfChecked.clear()
        screen = if (convertGoEdit) { convertGoEdit = false; Screen.ConvertEdit } else Screen.ConvertHome
    }

    fun discardPdfPages() {
        convertPdfPages = null
        convertPdfChecked.clear()
    }

    fun applyConvert() {
        if (convertUris.isEmpty()) {
            scope.launch { snackbar.showSnackbar("Select at least one image to convert.") }
            return
        }
        val fmt = convertTarget
        val bgColor = if (convertBg == "Custom Color") convertCustomColor.toInt() else android.graphics.Color.WHITE
        if (convertUris.size == 1) {
            busy = true
            busyLabel = "Converting…"
            scope.launch {
                try {
                    val src = convertReadyBitmaps.firstOrNull()
                        ?: withContext(Dispatchers.IO) { decodeOrientedBitmap(context, convertUris.first()) }
                        ?: throw IllegalStateException("decode failed")
                    val bytes = withContext(Dispatchers.Default) { ConvertFormats.encode(src, fmt, convertQuality, bgColor) }
                    convertResultBytes = bytes
                    convertResultName = "pixelcraft_${src.width}x${src.height}.${fmt.extension}"
                    convertResultPreview = src
                    convertResultOrigSize = convertOrigSizes.firstOrNull() ?: -1L
                    convertResultFrom = convertLabels.firstOrNull() ?: ""
                    convertSaved = false
                    busy = false
                    busyLabel = ""
                    screen = Screen.ConvertResult
                } catch (e: Exception) {
                    busy = false
                    busyLabel = ""
                    snackbar.showSnackbar("Couldn't convert this image. Try another photo.")
                }
            }
        } else {
            busy = true
            busyLabel = "Converting…"
            scope.launch {
                var origTotal = 0L
                var outTotal = 0L
                try {
                    val results = mutableListOf<Pair<String, ByteArray>>()
                    if (fmt.id == "pdf") {
                        val bitmaps = mutableListOf<Bitmap>()
                        convertUris.forEachIndexed { i, u ->
                            busyLabel = "Reading ${i + 1} of ${convertUris.size}…"
                            val page = convertReadyBitmaps.getOrNull(i)
                                ?: withContext(Dispatchers.IO) { decodeOrientedBitmap(context, u) }
                            page?.let { bitmaps.add(it) }
                        }
                        busyLabel = "Building PDF…"
                        val bytes = withContext(Dispatchers.Default) { ConvertFormats.encodePdf(bitmaps, bgColor) }
                        results.add("pixelcraft_document.pdf" to bytes)
                        outTotal = bytes.size.toLong()
                        origTotal = convertOrigSizes.filter { it > 0 }.sum()
                    } else {
                        convertUris.forEachIndexed { i, u ->
                            busyLabel = "Converting ${i + 1} of ${convertUris.size}…"
                            val ready = convertReadyBitmaps.getOrNull(i)
                            val src = ready
                                ?: withContext(Dispatchers.IO) { decodeOrientedBitmap(context, u) } ?: return@forEachIndexed
                            val bytes = withContext(Dispatchers.Default) { ConvertFormats.encode(src, fmt, convertQuality, bgColor) }
                            results.add("pixelcraft_${src.width}x${src.height}_${i + 1}.${fmt.extension}" to bytes)
                            if (ready == null) src.recycle()
                            outTotal += bytes.size
                            val os = convertOrigSizes.getOrNull(i) ?: -1L
                            if (os > 0) origTotal += os
                        }
                    }
                    convertBatchResults = results
                    convertBatchOutTotal = outTotal
                    convertBatchIsPdf = fmt.id == "pdf"
                    convertResultOrigSize = if (origTotal > 0) origTotal else -1L
                    convertResultFrom = convertLabels.map { it.substringBefore(" p.") }.distinct().let { if (it.size == 1) it.first() else "Mixed" }
                    convertResultPreview = null
                    convertSaved = false
                    busy = false
                    busyLabel = ""
                    screen = Screen.ConvertResult
                } catch (e: Exception) {
                    busy = false
                    busyLabel = ""
                    snackbar.showSnackbar("Couldn't finish the conversion. Try fewer or smaller photos.")
                }
            }
        }
    }

    fun loadCompressPhotos(uris: List<Uri>) {
        busy = true
        busyLabel = "Loading photos…"
        scope.launch {
            try {
                val newUris = mutableListOf<Uri>()
                val newThumbs = mutableListOf<Bitmap>()
                val newLabels = mutableListOf<String>()
                val newNames = mutableListOf<String>()
                val newSizes = mutableListOf<Long>()
                uris.take(20).forEach { u ->
                    withContext(Dispatchers.IO) { decodeOrientedBitmap(context, u) }?.let { bmp ->
                        newUris.add(u)
                        newThumbs.add(thumbOf(bmp))
                        newLabels.add(ConvertFormats.detectFormat(context, u))
                        newNames.add(queryDisplayName(context, u) ?: "image")
                        newSizes.add(querySourceSize(context, u))
                    }
                }
                compressUris.clear(); compressUris.addAll(newUris)
                compressThumbs.clear(); compressThumbs.addAll(newThumbs)
                compressLabels.clear(); compressLabels.addAll(newLabels)
                compressNames.clear(); compressNames.addAll(newNames)
                compressOrigSizes.clear(); compressOrigSizes.addAll(newSizes)
                compressIsPdf = false; compressPdfPages = null
                screen = if (compressGoSettings && compressUris.isNotEmpty()) { compressGoSettings = false; Screen.CompressSettings } else Screen.CompressHome
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun loadCompressPdf(uri: Uri) {
        busy = true
        busyLabel = "Reading PDF…"
        scope.launch {
            try {
                val pages = withContext(Dispatchers.IO) {
                    ConvertFormats.withSeekablePdf(context, uri) { ConvertFormats.renderPdfPages(it, maxPages = 40) }
                }
                if (pages.isEmpty()) throw IllegalStateException("No pages")
                compressUris.clear(); compressThumbs.clear(); compressLabels.clear(); compressNames.clear(); compressOrigSizes.clear()
                compressUris.add(uri)
                compressThumbs.add(thumbOf(pages.first()))
                compressLabels.add("PDF")
                compressNames.add(queryDisplayName(context, uri) ?: "document.pdf")
                compressOrigSizes.add(querySourceSize(context, uri))
                compressIsPdf = true
                compressPdfPages = pages
                screen = Screen.CompressSettings
                busy = false
                busyLabel = ""
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Couldn't read this PDF. Try another file.")
            }
        }
    }

    fun compressTargetBytes(): Long {
        val kb = compressTargetKb.filter { it.isDigit() }.toLongOrNull() ?: 500L
        return (kb.coerceIn(20L, 20_000L)) * 1024L
    }

    fun outputFormatFor(label: String): Triple<Bitmap.CompressFormat, String, String> = when (label) {
        "PNG" -> Triple(Bitmap.CompressFormat.PNG, "png", "image/png")
        "WEBP" -> Triple(Bitmap.CompressFormat.WEBP, "webp", "image/webp")
        "JPG" -> Triple(Bitmap.CompressFormat.JPEG, "jpg", "image/jpeg")
        else -> Triple(Bitmap.CompressFormat.JPEG, "jpg", "image/jpeg")
    }

    fun compressOne(src: Bitmap, label: String): Pair<ByteArray, Triple<Bitmap.CompressFormat, String, String>> {
        val fmt = outputFormatFor(label)
        val resized = if (compressResizeOn && !compressIsPdf) {
            Bitmap.createScaledBitmap(
                src,
                (src.width * compressResizePct / 100f).toInt().coerceAtLeast(1),
                (src.height * compressResizePct / 100f).toInt().coerceAtLeast(1),
                true
            )
        } else src
        val bytes = if (compressMode == "Target" && fmt.first != Bitmap.CompressFormat.PNG) {
            CompressUtils.compressImage(resized, fmt.first, compressTargetBytes())
        } else {
            val flat = if (fmt.first == Bitmap.CompressFormat.JPEG) ConvertFormats.flatten(resized, android.graphics.Color.WHITE) else resized
            val baos = java.io.ByteArrayOutputStream()
            flat.compress(fmt.first, if (fmt.first == Bitmap.CompressFormat.PNG) 100 else compressQuality, baos)
            baos.toByteArray()
        }
        return bytes to fmt
    }

    fun applyCompress() {
        if (compressUris.isEmpty()) {
            scope.launch { snackbar.showSnackbar("Select at least one image to compress.") }
            return
        }
        busy = true
        busyLabel = "Compressing…"
        scope.launch {
            try {
                if (compressIsPdf) {
                    val pages = compressPdfPages ?: throw IllegalStateException("No pages")
                    val bytes = withContext(Dispatchers.Default) {
                        if (compressMode == "Target") CompressUtils.compressPdfPages(pages, compressTargetBytes())
                        else {
                            val scale = (compressQuality / 100f).coerceIn(0.3f, 1f)
                            val scaled = if (scale >= 1f) pages else pages.map {
                                Bitmap.createScaledBitmap(it, (it.width * scale).toInt().coerceAtLeast(1), (it.height * scale).toInt().coerceAtLeast(1), true)
                            }
                            ConvertFormats.encodePdf(scaled, android.graphics.Color.WHITE)
                        }
                    }
                    compressResultBytes = bytes
                    compressResultName = "pixelcraft_compressed.pdf"
                    compressResultMime = "application/pdf"
                    compressResultOrig = compressOrigSizes.firstOrNull() ?: -1L
                    compressBefore = null
                    compressAfter = null
                    compressBatchFiles = null
                    compressSaved = false
                    busy = false
                    busyLabel = ""
                    screen = Screen.CompressResult
                } else if (compressUris.size == 1) {
                    val src = withContext(Dispatchers.IO) { decodeOrientedBitmap(context, compressUris.first()) }
                        ?: throw IllegalStateException("decode failed")
                    val (bytes, fmt) = withContext(Dispatchers.Default) { compressOne(src, compressLabels.firstOrNull() ?: "JPG") }
                    compressResultBytes = bytes
                    compressResultName = "pixelcraft_compressed.${fmt.second}"
                    compressResultMime = fmt.third
                    compressResultOrig = compressOrigSizes.firstOrNull() ?: -1L
                    compressBefore = src
                    compressAfter = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    compressBatchFiles = null
                    compressSaved = false
                    busy = false
                    busyLabel = ""
                    screen = Screen.CompressResult
                } else {
                    val files = mutableListOf<Pair<String, ByteArray>>()
                    val origs = mutableListOf<Long>()
                    compressUris.forEachIndexed { i, u ->
                        busyLabel = "Compressing ${i + 1} of ${compressUris.size}…"
                        val src = withContext(Dispatchers.IO) { decodeOrientedBitmap(context, u) } ?: return@forEachIndexed
                        val (bytes, fmt) = withContext(Dispatchers.Default) { compressOne(src, compressLabels.getOrNull(i) ?: "JPG") }
                        files.add("pixelcraft_compressed_${i + 1}.${fmt.second}" to bytes)
                        origs.add(compressOrigSizes.getOrNull(i) ?: -1L)
                        src.recycle()
                    }
                    compressBatchFiles = files
                    compressBatchOrig = origs
                    compressResultBytes = null
                    compressBefore = null
                    compressAfter = null
                    compressSaved = false
                    busy = false
                    busyLabel = ""
                    screen = Screen.CompressResult
                }
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Couldn't compress. Try fewer or smaller photos.")
            }
        }
    }

    fun saveCompressResult() {
        val batch = compressBatchFiles
        if (batch != null) {
            busy = true
            busyLabel = "Saving…"
            scope.launch {
                try {
                    batch.forEachIndexed { i, (name, bytes) ->
                        busyLabel = "Saving ${i + 1} of ${batch.size}…"
                        val mime = when {
                            name.endsWith(".png") -> "image/png"
                            name.endsWith(".webp") -> "image/webp"
                            else -> "image/jpeg"
                        }
                        saveToGallery(context, name, mime, bytes)
                    }
                    compressSaved = true
                    busy = false
                    busyLabel = ""
                    snackbar.showSnackbar("Saved to Pictures/PixelCraft.")
                } catch (e: Exception) {
                    busy = false
                    busyLabel = ""
                    snackbar.showSnackbar("Couldn't save. Try again.")
                }
            }
            return
        }
        val bytes = compressResultBytes ?: return
        scope.launch {
            try {
                if (compressResultMime == "application/pdf") saveToDownloads(context, compressResultName, compressResultMime, bytes)
                else saveToGallery(context, compressResultName, compressResultMime, bytes)
                compressSaved = true
                snackbar.showSnackbar(if (compressResultMime == "application/pdf") "Saved to Downloads/PixelCraft." else "Saved to Pictures/PixelCraft (originals untouched).")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't save. Try again.")
            }
        }
    }

    fun resetCompress() {
        compressUris.clear(); compressThumbs.clear(); compressLabels.clear(); compressNames.clear(); compressOrigSizes.clear()
        compressIsPdf = false; compressPdfPages = null
        compressResultBytes = null; compressBefore = null; compressAfter = null
        compressBatchFiles = null; compressBatchOrig = null; compressSaved = false
        compressGoSettings = false
    }

    fun loadPrintPhoto(uri: Uri) {
        printPdfAllPages = false
        printPdfPageList = null
        printPdfSelected.clear()
        scope.launch {
            busy = true
            busyLabel = "Loading photo…"
            try {
                val loaded = withContext(Dispatchers.IO) { decodeOrientedBitmap(context, uri) }
                printUri = uri
                printImgW = loaded.width; printImgH = loaded.height
                printThumb = thumbOf(loaded)
                printPreview = if (maxOf(loaded.width, loaded.height) > 1200) {
                    val sc = 1200f / maxOf(loaded.width, loaded.height)
                    Bitmap.createScaledBitmap(loaded, (loaded.width * sc).toInt().coerceAtLeast(1), (loaded.height * sc).toInt().coerceAtLeast(1), true)
                } else loaded
                printName = queryDisplayName(context, uri) ?: "image"
                printLabel = ConvertFormats.detectFormat(context, uri)
                printOrigSize = querySourceSize(context, uri)
                printResultBytes = null; printSaved = false
                screen = if (printGoSetup) { printGoSetup = false; Screen.PrintSetup } else Screen.PrintHome
            } catch (e: Exception) {
                snackbar.showSnackbar("Could not open that image.")
            }
            busy = false
            busyLabel = ""
        }
    }

    /** Picker thumbnails render one at a time, on demand, from the temp file - never all up front. */
    fun startPrintPdfThumbWorker() {
        if (printPdfThumbWorkerRunning) return
        printPdfThumbWorkerRunning = true
        scope.launch {
            try {
                while (true) {
                    val next = printPdfThumbQueue.poll() ?: break
                    printPdfThumbQueued.remove(next)
                    if (printPdfThumbs.containsKey(next)) continue
                    val file = printPdfTempFile ?: break
                    try {
                        val bmp = withContext(Dispatchers.IO) { ConvertFormats.renderPdfPageFromFile(file, next, maxDim = 200) }
                        printPdfThumbs[next] = bmp
                    } catch (_: Exception) { /* placeholder stays; scrolling away and back retries */ }
                }
            } finally {
                printPdfThumbWorkerRunning = false
                if (printPdfThumbQueue.peek() != null) startPrintPdfThumbWorker()
            }
        }
    }

    fun ensurePrintPdfThumb(index: Int) {
        if (printPdfTempFile == null) return
        if (index < 0 || index >= printPdfPageCount) return
        if (printPdfThumbs.containsKey(index)) return
        if (!printPdfThumbQueued.add(index)) return
        printPdfThumbQueue.add(index)
        startPrintPdfThumbWorker()
    }

    fun loadPrintPdf(uri: Uri) {
        busy = true
        busyLabel = "Reading PDF…"
        scope.launch {
            try {
                // Copy to a temp cache file first: some providers hand out non-seekable
                // pipe descriptors, and every render opens its own reader on the temp
                // file (a real file is always seekable) instead of reusing one
                // descriptor twice - the double open is what v0.15.2 did and his phone
                // rejected it.
                val (tmp, count) = withContext(Dispatchers.IO) {
                    val tmp = File.createTempFile("pixelcraft_pdf_", ".pdf", context.cacheDir)
                    try {
                        (context.contentResolver.openInputStream(uri)
                            ?: throw IllegalStateException("Could not open the PDF")).use { input ->
                            tmp.outputStream().use { input.copyTo(it) }
                        }
                        val count = ConvertFormats.pdfPageCountFromFile(tmp)
                        if (count <= 0) throw IllegalStateException("No pages")
                        if (count > 500) throw IllegalStateException("That PDF has $count pages - this tool handles up to 500.")
                        tmp to count
                    } catch (e: Exception) { tmp.delete(); throw e }
                }
                printPdfTempFile?.delete()
                printPdfTempFile = tmp
                printPdfPageCount = count
                printPdfThumbs.clear()
                printPdfThumbQueue.clear()
                printPdfThumbQueued.clear()
                printPdfUri = uri
                printPdfAllPages = false
                printPdfPageList = null
                printPdfSelected.clear()
                printPdfPickerOpen = true
                busy = false
                busyLabel = ""
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not read that PDF: ${e.message ?: "unknown error"}")
            }
        }
    }

    fun selectPrintPdfPage(index: Int) {
        val uri = printPdfUri ?: return
        printPdfAllPages = false
        printPdfPageList = null
        printPdfPickerOpen = false
        busy = true
        busyLabel = "Rendering page ${index + 1}…"
        scope.launch {
            try {
                val page = withContext(Dispatchers.IO) {
                    val tmp = printPdfTempFile ?: throw IllegalStateException("Pick the PDF again")
                    ConvertFormats.renderPdfPageFromFile(tmp, index, maxDim = 2600)
                }
                printPageBitmap = page
                printUri = uri
                printImgW = page.width
                printImgH = page.height
                printThumb = thumbOf(page)
                printPreview = if (maxOf(page.width, page.height) > 1200) {
                    val sc = 1200f / maxOf(page.width, page.height)
                    Bitmap.createScaledBitmap(page, (page.width * sc).toInt().coerceAtLeast(1), (page.height * sc).toInt().coerceAtLeast(1), true)
                } else page
                printName = queryDisplayName(context, uri) ?: "document.pdf"
                printLabel = "PDF · Page ${index + 1}"
                printOrigSize = querySourceSize(context, uri)
                printResultBytes = null
                printSaved = false
                printPdfThumbs.clear()
                busy = false
                busyLabel = ""
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not render that page: ${e.message ?: "unknown error"}")
            }
        }
    }

    fun selectPrintPdfAllPages() {
        val uri = printPdfUri ?: return
        val tmp = printPdfTempFile ?: return
        printPdfPageList = null
        printPdfSelected.clear()
        printPdfPickerOpen = false
        busy = true
        busyLabel = "Rendering page 1…"
        scope.launch {
            try {
                // Page 1 stands in for preview and aspect checks; every page is
                // composed with the same settings later, one at a time, at export.
                val page = withContext(Dispatchers.IO) { ConvertFormats.renderPdfPageFromFile(tmp, 0, maxDim = 2600) }
                printPageBitmap = page
                printUri = uri
                printImgW = page.width
                printImgH = page.height
                printThumb = thumbOf(page)
                printPreview = if (maxOf(page.width, page.height) > 1200) {
                    val sc = 1200f / maxOf(page.width, page.height)
                    Bitmap.createScaledBitmap(page, (page.width * sc).toInt().coerceAtLeast(1), (page.height * sc).toInt().coerceAtLeast(1), true)
                } else page
                printName = queryDisplayName(context, uri) ?: "document.pdf"
                printLabel = "PDF · All $printPdfPageCount pages"
                printOrigSize = querySourceSize(context, uri)
                printPdfAllPages = true
                printResultBytes = null
                printResultFiles.forEach { it.delete() }
                printResultFiles.clear()
                printSaved = false
                printPdfThumbs.clear()
                printPdfThumbQueue.clear()
                printPdfThumbQueued.clear()
                busy = false
                busyLabel = ""
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not render that PDF: ${e.message ?: "unknown error"}")
            }
        }
    }

    fun togglePrintPdfPage(index: Int) {
        if (index < 0 || index >= printPdfPageCount) return
        if (!printPdfSelected.remove(index)) printPdfSelected.add(index)
    }

    fun selectPrintPdfPages(indices: List<Int>) {
        if (indices.size <= 1) {
            val only = indices.firstOrNull() ?: return
            selectPrintPdfPage(only)
            return
        }
        val uri = printPdfUri ?: return
        val tmp = printPdfTempFile ?: return
        val ordered = indices.sorted()
        printPdfPickerOpen = false
        busy = true
        busyLabel = "Rendering page ${ordered.first() + 1}…"
        scope.launch {
            try {
                // The first selected page stands in for preview and aspect checks;
                // every selected page is composed with the same settings at export.
                val page = withContext(Dispatchers.IO) { ConvertFormats.renderPdfPageFromFile(tmp, ordered.first(), maxDim = 2600) }
                printPageBitmap = page
                printUri = uri
                printImgW = page.width
                printImgH = page.height
                printThumb = thumbOf(page)
                printPreview = if (maxOf(page.width, page.height) > 1200) {
                    val sc = 1200f / maxOf(page.width, page.height)
                    Bitmap.createScaledBitmap(page, (page.width * sc).toInt().coerceAtLeast(1), (page.height * sc).toInt().coerceAtLeast(1), true)
                } else page
                printName = queryDisplayName(context, uri) ?: "document.pdf"
                printLabel = "PDF · ${ordered.size} selected pages"
                printOrigSize = querySourceSize(context, uri)
                printPdfAllPages = false
                printPdfPageList = ordered
                printResultBytes = null
                printResultFiles.forEach { it.delete() }
                printResultFiles.clear()
                printSaved = false
                printPdfThumbs.clear()
                printPdfThumbQueue.clear()
                printPdfThumbQueued.clear()
                busy = false
                busyLabel = ""
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not render that PDF: ${e.message ?: "unknown error"}")
            }
        }
    }

    /** Custom-field value to inches; px mode converts through the selected PPI. */
    fun printToIn(v: Double): Double = if (printUnit == "px") v / printPpi else PrintUtils.toInches(v, printUnit)

    /** Inches back to a display string in the chosen unit (px rounds to whole pixels). */
    fun printFromIn(inches: Double): String =
        if (printUnit == "px") (inches * printPpi).toInt().toString() else PrintUtils.fmtDim(inches, printUnit)

    fun printDimsIn(): Pair<Double, Double> {
        val p = PrintUtils.presets.firstOrNull { it.name == printPreset }
        var wIn = p?.wIn ?: printToIn(printWText.replace(",", ".").toDoubleOrNull() ?: 0.0)
        var hIn = p?.hIn ?: printToIn(printHText.replace(",", ".").toDoubleOrNull() ?: 0.0)
        if (printLandscape && hIn > wIn) { val t = wIn; wIn = hIn; hIn = t }
        if (!printLandscape && wIn > hIn) { val t = wIn; wIn = hIn; hIn = t }
        return wIn to hIn
    }

    fun printImageAspect(): Double {
        if (printImgW <= 0 || printImgH <= 0) return 0.0
        val lo = minOf(printImgW, printImgH).toDouble()
        val hi = maxOf(printImgW, printImgH).toDouble()
        return if (printLandscape) hi / lo else lo / hi
    }

    fun printMultiCopyEligible(): Boolean {
        val g = PrintUtils.presets.firstOrNull { it.name == printPreset }?.group
        return g == "ID / Passport" || g == "Photo sizes"
    }

    fun printSheetDimsIn(): Pair<Double, Double> {
        val sp = PrintUtils.presets.firstOrNull { it.name == printSheet } ?: PrintUtils.presets.first { it.name == "A4" }
        var wIn = sp.wIn
        var hIn = sp.hIn
        if (printLandscape && hIn > wIn) { val t = wIn; wIn = hIn; hIn = t }
        if (!printLandscape && wIn > hIn) { val t = wIn; wIn = hIn; hIn = t }
        return wIn to hIn
    }

    /** Highest copy count achievable on the chosen sheet (tight layout included). */
    fun printSheetMaxCount(): Int {
        val (cW, cH) = printDimsIn()
        val (sW, sH) = printSheetDimsIn()
        val tight = PrintUtils.gridFit(sW, sH, cW, cH, 0.0, 0.0)
        return tight[0] * tight[1]
    }

    /**
     * Grid for the multi-copy sheet: [cols, rows, count, gapIn].
     * Tries the comfortable 3 mm gap / 5 mm margin first; when the requested
     * count only fits with the copies touching (real passport sheets are like
     * that - cut guides are drawn between copies), falls back to a tight layout.
     * count is 0 when nothing fits.
     */
    fun printSheetGrid(): IntArray {
        val (cW, cH) = printDimsIn()
        val (sW, sH) = printSheetDimsIn()
        val fit = PrintUtils.gridFit(sW, sH, cW, cH, PrintUtils.SHEET_GAP_IN, PrintUtils.SHEET_MARGIN_IN)
        val maxGapped = fit[0] * fit[1]
        val tight = PrintUtils.gridFit(sW, sH, cW, cH, 0.0, 0.0)
        val maxTight = tight[0] * tight[1]
        val wanted = if (printCopyCount <= 0) maxGapped else printCopyCount
        if (wanted <= 0) return intArrayOf(0, 0, 0, 0)
        if (wanted <= maxGapped) {
            val cols = minOf(fit[0], kotlin.math.ceil(kotlin.math.sqrt(wanted.toDouble())).toInt()).coerceAtLeast(1)
            val rows = kotlin.math.ceil(wanted.toDouble() / cols).toInt()
            return intArrayOf(cols, rows, wanted, 1)   // gap flag 1 = standard gap
        }
        if (wanted <= maxTight) {
            val cols = minOf(tight[0], kotlin.math.ceil(kotlin.math.sqrt(wanted.toDouble())).toInt()).coerceAtLeast(1)
            val rows = kotlin.math.ceil(wanted.toDouble() / cols).toInt()
            return intArrayOf(cols, rows, wanted, 0)   // 0 = touching
        }
        return intArrayOf(0, 0, 0, 0)
    }

    fun applyPrintExport() {
        val uri = printUri
        if (uri == null && printPageBitmap == null) return
        val (wIn, hIn) = printDimsIn()
        if (wIn <= 0 || hIn <= 0) { scope.launch { snackbar.showSnackbar("Enter a valid print size first.") }; return }
        val sheetMode = printSheetOn && printMultiCopyEligible()
        if (sheetMode && printSheetGrid()[2] <= 0) { scope.launch { snackbar.showSnackbar("That count does not fit on $printSheet - try fewer copies or A4.") }; return }
        val batchIndices: IntArray? = when {
            printPdfAllPages && printPdfTempFile != null -> IntArray(printPdfPageCount) { it }
            printPdfPageList != null && printPdfTempFile != null -> printPdfPageList!!.toIntArray()
            else -> null
        }
        val allPagesMode = batchIndices != null
        busy = true
        busyLabel = "Preparing print file…"
        scope.launch {
            try {
                printResultFiles.forEach { it.delete() }
                printResultFiles.clear()
                val bleedIn = if (printBleedOn) PrintUtils.BLEED_IN else 0.0
                val marginIn = if (printMarginsOn) PrintUtils.MARGIN_IN else 0.0
                val bg = if (printBgWhite) android.graphics.Color.WHITE else android.graphics.Color.rgb(0xF2, 0xF2, 0xF2)
                if (allPagesMode) {
                    // Every page in one run with the same settings, composed one page
                    // at a time into temp cache files - nothing is saved until Save.
                    val m = withContext(Dispatchers.IO) {
                        val tmpPdf = printPdfTempFile!!
                        val outDir = File(context.cacheDir, "images").apply { mkdirs() }
                        val onProg: (Int, Int) -> Unit = { p, n -> busyLabel = "Preparing print file… (page $p of $n)" }
                        if (sheetMode) {
                            val (sW, sH) = printSheetDimsIn()
                            val g = printSheetGrid()
                            val gapIn = if (g[3] == 1) PrintUtils.SHEET_GAP_IN else 0.0
                            if (printFormat == "PDF") buildPrintSheetPdfAllPages(tmpPdf, batchIndices!!, sW, sH, wIn, hIn, g[0], g[1], g[2], gapIn, printPpi, printFilename, File(outDir, "$printFilename.pdf"), onProg)
                            else buildPrintSheetRasterAllPages(tmpPdf, batchIndices!!, sW, sH, wIn, hIn, g[0], g[1], g[2], gapIn, printPpi, printJpgQuality, printFormat, printFilename, outDir, onProg)
                        } else if (printFormat == "PDF") {
                            buildPrintPdfAllPages(tmpPdf, batchIndices!!, wIn, hIn, bleedIn, marginIn, printFitMode, printPpi, printCropMarks, printFilename, File(outDir, "$printFilename.pdf"), onProg)
                        } else {
                            buildPrintRasterAllPages(tmpPdf, batchIndices!!, wIn, hIn, bleedIn, marginIn, bg, printFitMode, printPpi, printJpgQuality, printFormat, printFilename, outDir, onProg)
                        }
                    }
                    printResultFiles.addAll(m.files)
                    printResultBytes = null
                    printResultName = m.name
                    printResultMime = m.mime
                    printResultDims = m.dims
                } else {
                    val result = withContext(Dispatchers.IO) {
                        val src = printPageBitmap ?: decodeOrientedBitmap(context, uri!!)
                        if (sheetMode) {
                            val (sW, sH) = printSheetDimsIn()
                            val g = printSheetGrid()
                            val gapIn = if (g[3] == 1) PrintUtils.SHEET_GAP_IN else 0.0
                            if (printFormat == "PDF") {
                                buildPrintSheetPdf(src, sW, sH, wIn, hIn, g[0], g[1], g[2], gapIn, printPpi, printFilename)
                            } else {
                                buildPrintSheetRaster(src, sW, sH, wIn, hIn, g[0], g[1], g[2], gapIn, printPpi, printJpgQuality, printFormat, printFilename)
                            }
                        } else if (printFormat == "PDF") {
                            buildPrintPdf(src, wIn, hIn, bleedIn, marginIn, printFitMode, printPpi, printCropMarks, printFilename)
                        } else {
                            buildPrintRaster(src, wIn, hIn, bleedIn, marginIn, bg, printFitMode, printPpi, printJpgQuality, printFormat, printFilename)
                        }
                    }
                    printResultBytes = result.bytes
                    printResultName = result.name
                    printResultMime = result.mime
                    printResultDims = result.dims
                }
                printSaved = false
            } catch (e: Exception) {
                printResultFiles.forEach { it.delete() }
                printResultFiles.clear()
                snackbar.showSnackbar("Could not create the print file: ${e.message ?: "unknown error"}")
            }
            busy = false
            busyLabel = ""
        }
    }

    fun savePrintResult() {
        if (printResultFiles.isNotEmpty()) {
            busy = true
            busyLabel = "Saving…"
            scope.launch {
                try {
                    val count = printResultFiles.size
                    withContext(Dispatchers.IO) {
                        printResultFiles.forEachIndexed { idx, f ->
                            if (count > 1) busyLabel = "Saving… (${idx + 1} of $count)"
                            val bytes = f.readBytes()
                            if (printResultMime == "application/pdf") saveToDownloads(context, f.name, printResultMime, bytes)
                            else saveToGallery(context, f.name, printResultMime, bytes)
                        }
                    }
                    printSaved = true
                    busy = false
                    busyLabel = ""
                    snackbar.showSnackbar(if (count > 1) "Saved $count files. Your original is unchanged." else "Saved ${printResultFiles.first().name}. Your original is unchanged.")
                } catch (e: Exception) {
                    busy = false
                    busyLabel = ""
                    snackbar.showSnackbar("Save failed: ${e.message ?: "unknown error"}")
                }
            }
            return
        }
        val bytes = printResultBytes ?: return
        busy = true
        busyLabel = "Saving…"
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (printResultMime == "application/pdf") saveToDownloads(context, printResultName, printResultMime, bytes)
                    else saveToGallery(context, printResultName, printResultMime, bytes)
                }
                printSaved = true
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Saved $printResultName. Your original is unchanged.")
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Save failed: ${e.message ?: "unknown error"}")
            }
        }
    }

    fun sharePrintResult() {
        if (printResultFiles.isNotEmpty()) {
            scope.launch {
                try {
                    val uris = ArrayList(printResultFiles.map { FileProvider.getUriForFile(context, context.packageName + ".fileprovider", it) })
                    val intent = if (uris.size == 1) {
                        Intent(Intent.ACTION_SEND).apply {
                            type = printResultMime
                            putExtra(Intent.EXTRA_STREAM, uris[0])
                        }
                    } else {
                        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                            type = printResultMime
                            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                        }
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(Intent.createChooser(intent, "Share print file"))
                } catch (e: Exception) {
                    snackbar.showSnackbar("Share failed: ${e.message ?: "unknown error"}")
                }
            }
            return
        }
        val bytes = printResultBytes ?: return
        scope.launch {
            try {
                val shareUri = withContext(Dispatchers.IO) {
                    val dir = File(context.cacheDir, "images").apply { mkdirs() }
                    val file = File(dir, printResultName)
                    FileOutputStream(file).use { it.write(bytes) }
                    FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = printResultMime
                    putExtra(Intent.EXTRA_STREAM, shareUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Share print file"))
            } catch (e: Exception) {
                snackbar.showSnackbar("Share failed: ${e.message ?: "unknown error"}")
            }
        }
    }

    fun resetPrint() {
        printUri = null; printThumb = null; printPreview = null
        printName = "image"; printLabel = ""; printOrigSize = -1L
        printImgW = 0; printImgH = 0
        printPreset = "A4"; printUnit = "in"; printWText = ""; printHText = ""
        printLandscape = false; printLockAspect = true; printBleedOn = false
        printPpi = 300; printFitMode = "Fit"; printMarginsOn = false; printCropMarks = false
        printBgWhite = true; printFormat = "JPG"; printFilename = "print-ready"; printJpgQuality = 92
        printResultBytes = null; printResultName = ""; printResultMime = ""; printResultDims = ""
        printPdfUri = null; printPdfThumbs.clear(); printPdfPageCount = 0; printPdfTempFile?.delete(); printPdfTempFile = null; printPdfThumbQueue.clear(); printPdfThumbQueued.clear(); printPdfPickerOpen = false; printPageBitmap = null
        printSheetOn = false; printSheet = "4 × 6"; printCopyCount = 4
        printPdfAllPages = false; printPdfPageList = null; printPdfSelected.clear(); printResultFiles.forEach { it.delete() }; printResultFiles.clear()
        printSaved = false; printGoSetup = false
    }

    fun saveConvertResult() {
        val fmt = convertTarget
        val batch = convertBatchResults
        if (batch != null) {
            busy = true
            busyLabel = "Saving…"
            scope.launch {
                try {
                    batch.forEachIndexed { i, (name, bytes) ->
                        busyLabel = "Saving ${i + 1} of ${batch.size}…"
                        if (fmt.id == "pdf") saveToDownloads(context, name, fmt.mimeType, bytes)
                        else saveToGallery(context, name, fmt.mimeType, bytes)
                    }
                    convertSaved = true
                    busy = false
                    busyLabel = ""
                    val where = if (fmt.id == "pdf") "Downloads/PixelCraft" else "Pictures/PixelCraft"
                    snackbar.showSnackbar("Saved to $where.")
                } catch (e: Exception) {
                    busy = false
                    busyLabel = ""
                    snackbar.showSnackbar("Couldn't save. Try again.")
                }
            }
            return
        }
        val bytes = convertResultBytes ?: return
        scope.launch {
            try {
                if (fmt.id == "pdf") saveToDownloads(context, convertResultName, fmt.mimeType, bytes)
                else saveToGallery(context, convertResultName, fmt.mimeType, bytes)
                convertSaved = true
                snackbar.showSnackbar(if (fmt.id == "pdf") "Saved to Downloads/PixelCraft." else "Saved to your gallery (Pictures/PixelCraft).")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't save. Try again.")
            }
        }
    }

    fun resetConvert() {
        convertUris.clear(); convertThumbs.clear(); convertLabels.clear(); convertOrigSizes.clear()
        convertReadyBitmaps.clear(); convertPdfPages = null; convertPdfChecked.clear()
        convertResultBytes = null; convertResultPreview = null; convertSaved = false
        convertBatchResults = null; convertBatchOutTotal = -1L; convertBatchIsPdf = false
        convertGoEdit = false
    }

    fun applyShopCollage() {
        val t = collageTarget ?: return
        if (collagePhotos.size < 2) {
            scope.launch { snackbar.showSnackbar("Select at least 2 photos for the collage.") }
            return
        }
        busy = true
        busyLabel = "Exporting…"
        scope.launch {
            try {
                val photos = collagePhotos.toList()
                val out = withContext(Dispatchers.Default) {
                    Transforms.collage(photos, t.w, t.h, maxOf(4, t.w / 200), android.graphics.Color.WHITE)
                }
                processedBitmap = out
                exportName = suggestBaseName(t.w, t.h)
                exportDpi = 0
                beforeAfter = false
                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(out, outFormat(), quality) }
                exportReturnToCollage = true
                screen = Screen.Export
                snackbar.showSnackbar("Collage ready: ${t.w} × ${t.h} px.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Couldn't build the collage. Try a smaller target size.")
            } finally {
                busy = false
                busyLabel = ""
            }
        }
    }

    fun encodeProcessed(format: ExportFormat = outFormat()): ByteArray {
        val processed = processedBitmap ?: throw IllegalStateException("Nothing processed yet")
        return encodeImage(context, sourceUri, processed, format, quality, exportDpi)
    }

    fun runProcess(targetW: Int, targetH: Int, mode: AdaptMode, anchor: CropAnchor, pad: FitPad) {
        val src = workingBitmap ?: return
        busy = true
        busyLabel = "Processing…"
        exportDpi = 0
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
                exportReturnToSocial = false; exportReturnToShop = false; exportReturnToCollage = false
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
                exportReturnToSocial = false; exportReturnToShop = false; exportReturnToCollage = false
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

    // Full reset (his request): clear the loaded photo(s) and every editing trace -
    // including the auto-saved draft, so close/reopen cannot bring the photo back.
    // Stays INSIDE the current tool (e.g. Remove Background) where a Select Image
    // option appears; pendingBgTool makes the next picked photo reopen that tool.
    fun resetEditor() {
        val uri = sourceUri
        val stayInBackground = tool == Tool.Background
        val stayInCrop = tool == Tool.Crop
        val stayInCircle = tool == Tool.Circle
        clearProcessedResults()
        workingBitmap = null
        processedBitmap = null
        photoQueue = emptyList()
        queueIndex = 0
        queueTargetW = ""
        queueTargetH = ""
        queueNamePattern = ""
        sourceUri = null
        sourceIsCamera = false
        sourceFormat = ExportFormat.UNKNOWN
        sourceBytes = -1L
        estimatedBytes = -1L
        inputWidth = ""
        inputHeight = ""
        customPercent = ""
        percentError = ""
        beforeAfter = false
        bgCutout = null
        bgPreview = null
        bgError = ""
        bgWidth = ""
        bgHeight = ""
        exportDpi = 0
        pendingPreset = null
        pendingDraft = null
        pendingBgTool = stayInBackground
        pendingCropTool = stayInCrop
        pendingCircleTool = stayInCircle
        cropRect = CropMath.defaultRect()
        circleRect = CropMath.defaultRect()
        circlePreview = null
        straightenAngle = 0f
        tiltHAngle = 0f
        tiltVAngle = 0f
        if (uri != null) {
            scope.launch(Dispatchers.IO) { DraftsStore.remove(context, uri.toString()) }
        }
        tool = if (stayInBackground) Tool.Background else if (stayInCrop) Tool.Crop else if (stayInCircle) Tool.Circle else Tool.Resize
        screen = Screen.Editor
    }

    fun pickPhotos() {
        pendingLaunch = "pick"
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) loadPhoto(uri, isCamera = false)
    }
    val shopBatchPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(20)) { uris ->
        if (uris.isNotEmpty()) applyShopBatch(uris)
    }
    val collagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) { uris ->
        if (uris.isNotEmpty()) loadCollagePhotos(uris)
    }
    val convertPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(20)) { uris ->
        if (uris.isNotEmpty()) loadConvertPhotos(uris)
    }
    val convertPdfPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { loadPdfInput(it) }
    }
    val printPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) loadPrintPhoto(uri)
    }
    val printPdfPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { loadPrintPdf(it) }
    }
    val compressPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(20)) { uris ->
        if (uris.isNotEmpty()) loadCompressPhotos(uris)
    }
    val compressPdfPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { loadCompressPdf(it) }
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

    LaunchedEffect(pendingPdfTestPath) {
        pendingPdfTestPath?.let { path ->
            pendingPdfTestPath = null
            loadPdfInput(Uri.fromFile(File(path)))
        }
    }

    BackHandler(enabled = screen != Screen.Home) {
        when (screen) {
            Screen.Export -> {
                screen = when {
                    exportReturnToSocial -> { exportReturnToSocial = false; Screen.SocialEdit }
                    exportReturnToShop -> { exportReturnToShop = false; Screen.ShopEdit }
                    exportReturnToCollage -> { exportReturnToCollage = false; Screen.ShopCollage }
                    else -> Screen.Editor
                }
            }
            Screen.QueueResult -> { screen = Screen.Home; tab = MainTab.Home }
            Screen.Editor -> leaveEditorToHome()
            Screen.SocialEdit -> screen = Screen.SocialPlatform
            Screen.SocialPlatform -> screen = Screen.SocialHome
            Screen.ShopEdit -> screen = Screen.ShopPlatform
            Screen.ShopCollage -> screen = Screen.ShopPlatform
            Screen.ShopPlatform -> screen = if (shopFromOther) Screen.ShopOther else Screen.ShopHome
            Screen.ShopOther -> screen = Screen.ShopHome
            Screen.ConvertResult -> screen = Screen.ConvertEdit
            Screen.ConvertEdit -> screen = Screen.ConvertHome
            Screen.ConvertHome -> { resetConvert(); screen = Screen.Home }
            Screen.CompressResult -> screen = Screen.CompressSettings
            Screen.CompressSettings -> screen = Screen.CompressHome
            Screen.PrintExport -> screen = Screen.PrintPreview
            Screen.PrintPreview -> screen = Screen.PrintSetup
            Screen.PrintSetup -> screen = Screen.PrintHome
            Screen.PrintHome -> { resetPrint(); screen = Screen.Home }
            Screen.CompressHome -> { resetCompress(); screen = Screen.Home }
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

    convertPdfPages?.let { pages ->
        AlertDialog(
            onDismissRequest = { discardPdfPages() },
            title = { Text("Select pages", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                    pages.forEachIndexed { i, page ->
                        val checked = convertPdfChecked.getOrNull(i) == true
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { convertPdfChecked[i] = !checked }.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = thumbOf(page).asImageBitmap(),
                                contentDescription = "Page ${i + 1}",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Page ${i + 1}", Modifier.weight(1f), color = DeepNavy, fontWeight = FontWeight.SemiBold)
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = if (checked) "Selected" else "Not selected",
                                tint = if (checked) SuccessGreen else SoftGrey,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { confirmPdfPages() }) {
                    Text("Use ${convertPdfChecked.count { it }} Page${if (convertPdfChecked.count { it } == 1) "" else "s"}", color = DeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { discardPdfPages() }) { Text("Cancel", color = Slate) } }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            if (screen == Screen.Editor || screen == Screen.Export || screen == Screen.PrintHome || screen == Screen.PrintSetup || screen == Screen.PrintPreview || screen == Screen.PrintExport || screen == Screen.QueueOverview || screen == Screen.QueueResult || screen == Screen.Success || screen == Screen.SocialHome || screen == Screen.SocialPlatform || screen == Screen.SocialEdit || screen == Screen.ShopHome || screen == Screen.ShopOther || screen == Screen.ShopPlatform || screen == Screen.ShopEdit || screen == Screen.ShopCollage || screen == Screen.ConvertHome || screen == Screen.ConvertEdit || screen == Screen.ConvertResult || screen == Screen.CompressHome || screen == Screen.CompressSettings || screen == Screen.CompressResult) {
                TopAppBar(
                    title = {
                        Text(
                            when (screen) {
                                Screen.Editor -> "Resize Image"
                                Screen.Export -> "Export"
                                Screen.QueueOverview -> "Photos to resize"
                                Screen.QueueResult -> "Resized photos"
                                Screen.Success -> "Saved"
                                Screen.SocialHome -> "Social Media Sizes"
                                Screen.SocialPlatform -> socialPlatform
                                Screen.SocialEdit -> "$socialPlatform · ${socialTarget?.label ?: "Custom"}"
                                Screen.ShopHome -> "Online Shop Sizes"
                                Screen.ShopOther -> "Other Shop"
                                Screen.ShopPlatform -> shopPlatform
                                Screen.ShopEdit -> "$shopPlatform · ${shopTarget?.label ?: "Custom"}"
                                Screen.ShopCollage -> "$shopPlatform · Collage"
                                Screen.ConvertHome -> "Format Converter"
                                Screen.ConvertEdit -> "Format Converter · ${convertTarget.label}"
                                Screen.ConvertResult -> "Conversion Result"
                                Screen.CompressHome -> "Image Compressor"
                                Screen.CompressSettings -> "Compression Settings"
                                Screen.CompressResult -> "Compression Results"
                                Screen.PrintHome -> "Print Size & DPI"
                                Screen.PrintSetup -> "Print Setup"
                                Screen.PrintPreview -> "Print Preview"
                                Screen.PrintExport -> "Export Print File"
                                else -> "Print Size & DPI"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when (screen) {
                                Screen.Export -> screen = when {
                                    exportReturnToSocial -> { exportReturnToSocial = false; Screen.SocialEdit }
                                    exportReturnToShop -> { exportReturnToShop = false; Screen.ShopEdit }
                                    exportReturnToCollage -> { exportReturnToCollage = false; Screen.ShopCollage }
                                    else -> Screen.Editor
                                }
                                Screen.QueueResult -> { screen = Screen.Home; tab = MainTab.Home }
                                Screen.Editor -> leaveEditorToHome()
                                Screen.SocialEdit -> screen = Screen.SocialPlatform
                                Screen.SocialPlatform -> screen = Screen.SocialHome
                                Screen.ShopEdit -> screen = Screen.ShopPlatform
                                Screen.ShopCollage -> screen = Screen.ShopPlatform
                                Screen.ShopPlatform -> screen = if (shopFromOther) Screen.ShopOther else Screen.ShopHome
                                Screen.ShopOther -> screen = Screen.ShopHome
                                Screen.ConvertResult -> screen = Screen.ConvertEdit
                                Screen.ConvertEdit -> screen = Screen.ConvertHome
                                Screen.ConvertHome -> { resetConvert(); screen = Screen.Home }
                                Screen.CompressResult -> screen = Screen.CompressSettings
                                Screen.CompressSettings -> screen = Screen.CompressHome
                                Screen.PrintExport -> screen = Screen.PrintPreview
                                Screen.PrintPreview -> screen = Screen.PrintSetup
                                Screen.PrintSetup -> screen = Screen.PrintHome
                                Screen.PrintHome -> { resetPrint(); screen = Screen.Home }
                                Screen.CompressHome -> { resetCompress(); screen = Screen.Home }
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
                        } else if (screen == Screen.SocialEdit) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetEditor(); pendingSocialTool = true; screen = Screen.SocialEdit }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        } else if (screen == Screen.ShopEdit) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetEditor(); pendingShopTool = true; screen = Screen.ShopEdit }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        } else if (screen == Screen.ConvertEdit) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetConvert(); screen = Screen.ConvertHome }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        } else if (screen == Screen.CompressSettings) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetCompress(); screen = Screen.CompressHome }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        } else if (screen == Screen.PrintHome) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetPrint() }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        } else if (screen == Screen.PrintSetup) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetPrint(); screen = Screen.PrintHome }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        } else if (screen == Screen.ShopCollage) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { collagePhotos.clear() }
                                    .padding(horizontal = 14.dp),
                                color = DeepNavy
                            )
                        } else if (screen == Screen.Editor) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetEditor() }
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
                    onPrint = { resetPrint(); screen = Screen.PrintHome },
                    onFeature = { feature ->
                        when (feature) {
                            "Resize" -> pickPhotos()
                            "Print Size & DPI" -> { resetPrint(); screen = Screen.PrintHome }
                            "Social Media Sizes" -> screen = Screen.SocialHome
                            "Online Shop Sizes" -> screen = Screen.ShopHome
                            "Format Converter" -> screen = Screen.ConvertHome
                            "Compress" -> screen = Screen.CompressHome
                            "Remove Background" -> {
                                if (workingBitmap != null) { screen = Screen.Editor; tool = Tool.Background }
                                else { pendingBgTool = true; pickPhotos() }
                            }
                            "Crop & Rotate" -> {
                                if (workingBitmap != null) { screen = Screen.Editor; tool = Tool.Crop; cropAspect = "Free"; cropRect = CropMath.defaultRect() }
                                else { pendingCropTool = true; pickPhotos() }
                            }
                            "Circle Cutter" -> {
                                val src = workingBitmap
                                if (src != null) { screen = Screen.Editor; tool = Tool.Circle; circleShape = "Circle"; circleRect = CropMath.maxRectForAspect("1:1", src.width, src.height) }
                                else { pendingCircleTool = true; pickPhotos() }
                            }
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
                        Tool.Background -> Unit
                        Tool.Crop -> { cropAspect = "Free"; cropRect = CropMath.defaultRect(); straightenAngle = 0f; tiltHAngle = 0f; tiltVAngle = 0f }
                        Tool.Circle -> { circleShape = "Circle"; val src = workingBitmap; circleRect = if (src != null) CropMath.maxRectForAspect("1:1", src.width, src.height) else CropMath.defaultRect() }
                        else -> scope.launch { snackbar.showSnackbar("${selected.label} is planned for a later stage. Resize and export are available now.") }
                    }
                },
                onRotate = {
                    val src = workingBitmap
                    if (src != null) {
                        val rotated = rotateRight(src)
                        workingBitmap = rotated
                        applyTarget(rotated.width, rotated.height)
                        if (tool == Tool.Crop) {
                            cropRect = CropMath.maxRectForAspect(cropAspect, rotated.width, rotated.height)
                            scope.launch { snackbar.showSnackbar("Rotated 90°.") }
                        } else {
                            scope.launch { snackbar.showSnackbar("Rotated 90°. Tap Process when the size is right.") }
                        }
                    }
                },
                cropRect = cropRect,
                cropAspect = cropAspect,
                straightenAngle = straightenAngle,
                tiltHAngle = tiltHAngle,
                tiltVAngle = tiltVAngle,
                adjustMode = adjustMode,
                onAdjustMode = { adjustMode = it },
                onAdjustValue = { value ->
                    when (adjustMode) {
                        "Horizontal" -> tiltHAngle = value
                        "Vertical" -> tiltVAngle = value
                        else -> straightenAngle = value
                    }
                },
                onAdjustReset = { straightenAngle = 0f; tiltHAngle = 0f; tiltVAngle = 0f },
                onCropRect = { cropRect = it },
                onCropAspect = { asp ->
                    cropAspect = asp
                    val src = workingBitmap
                    if (src != null) cropRect = CropMath.maxRectForAspect(asp, src.width, src.height)
                },
                onCropTransform = { kind ->
                    val src = workingBitmap
                    if (src != null) {
                        val out = when (kind) {
                            CropTransform.LEFT -> Transforms.rotateLeft(src)
                            CropTransform.RIGHT -> Transforms.rotateRight(src)
                            CropTransform.FLIP_H -> Transforms.flipH(src)
                            CropTransform.FLIP_V -> Transforms.flipV(src)
                        }
                        workingBitmap = out
                        applyTarget(out.width, out.height)
                        invalidateProcessed()
                        cropRect = CropMath.maxRectForAspect(cropAspect, out.width, out.height)
                    }
                },
                onApplyCrop = {
                    val src = workingBitmap
                    if (src != null) {
                        busy = true
                        busyLabel = "Cropping…"
                        scope.launch {
                            try {
                                val result = withContext(Dispatchers.Default) {
                                    val adjusted = if (straightenAngle != 0f || tiltHAngle != 0f || tiltVAngle != 0f)
                                        Transforms.adjustPerspective(src, straightenAngle, tiltHAngle, tiltVAngle) else src
                                    Transforms.cropRegion(adjusted, CropMath.toPixels(cropRect, adjusted.width, adjusted.height))
                                }
                                processedBitmap = result
                                exportName = suggestBaseName(result.width, result.height)
                                beforeAfter = true
                                estimatedBytes = withContext(Dispatchers.IO) { estimateBytes(result, outFormat(), quality) }
                                cropRect = CropMath.defaultRect()
                                straightenAngle = 0f
                                tiltHAngle = 0f
                                tiltVAngle = 0f
                                exportReturnToSocial = false; exportReturnToShop = false; exportReturnToCollage = false
                                screen = Screen.Export
                                snackbar.showSnackbar("Cropped: ${result.width} × ${result.height} px.")
                            } catch (e: Exception) {
                                snackbar.showSnackbar("Couldn't crop this photo.")
                            } finally {
                                busy = false
                                busyLabel = ""
                            }
                        }
                    }
                },
                onProcess = { showDimensionsSheet = true },
                onExport = { screen = Screen.Export },
                bgPreview = bgPreview,
                bgCutoutReady = bgCutout != null,
                bgChoice = bgChoice,
                bgCustomColor = bgCustomColor,
                bgUnit = bgUnit,
                bgWidth = bgWidth,
                bgHeight = bgHeight,
                bgDpi = bgDpi,
                bgError = bgError,
                onBgChoice = { bgChoice = it },
                onBgCustomColor = { bgCustomColor = it },
                onBgUnit = { convertBgToUnit(bgUnit, it) },
                onBgWidth = { bgWidth = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                onBgHeight = { bgHeight = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                onBgDpi = { bgDpi = it.filter { c -> c.isDigit() } },
                onRemoveBackground = { runRemoveBackground() },
                onBgExport = { continueBgExport() },
                circleShape = circleShape,
                circleRect = circleRect,
                circleChoice = circleChoice,
                circleCustomColor = circleCustomColor,
                circleUnit = circleUnit,
                circleSize = circleSize,
                circleDpi = circleDpi,
                circlePreview = circlePreview,
                onCircleShape = { circleShape = it; circleRect = CropMath.defaultRect() },
                onCircleRect = { circleRect = it },
                onCircleChoice = { circleChoice = it },
                onCircleCustomColor = { circleCustomColor = it },
                onCircleUnit = { convertCircleToUnit(circleUnit, it) },
                onCircleSize = { circleSize = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                onCircleDpi = { circleDpi = it.filter { c -> c.isDigit() } },
                onApplyCircle = { applyCircle() },
                onSelectImage = { pickPhotos() }
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
            Screen.SocialHome -> SocialHomeScreen(
                modifier = Modifier.padding(padding),
                photo = workingBitmap,
                photoLabel = workingBitmap?.let { "${it.width} × ${it.height} px" },
                onSelectImage = { pendingSocialTool = true; pickPhotos() },
                onPlatform = { name -> socialPlatform = name; screen = Screen.SocialPlatform }
            )
            Screen.SocialPlatform -> SocialPlatformScreen(
                modifier = Modifier.padding(padding),
                platform = SocialSizes.platforms.firstOrNull { it.name == socialPlatform },
                customW = socialCustomW,
                customH = socialCustomH,
                onCustomW = { socialCustomW = it.filter { c -> c.isDigit() } },
                onCustomH = { socialCustomH = it.filter { c -> c.isDigit() } },
                onTarget = { selectSocialTarget(it) },
                onCustom = {
                    val w = socialCustomW.toIntOrNull()
                    val h = socialCustomH.toIntOrNull()
                    if (w == null || h == null || w < 16 || h < 16 || w > 12000 || h > 12000) {
                        scope.launch { snackbar.showSnackbar("Enter width and height between 16 and 12000 px.") }
                    } else {
                        selectSocialTarget(SocialSize("Custom", "$w x $h px", w, h))
                    }
                }
            )
            Screen.SocialEdit -> SocialEditScreen(
                modifier = Modifier.padding(padding),
                working = workingBitmap,
                target = socialTarget,
                mode = socialMode,
                rect = socialRect,
                zoom = socialZoom,
                panX = socialPanX,
                panY = socialPanY,
                fill = socialFill,
                customColor = socialCustomColor,
                busy = busy,
                onMode = { socialMode = it },
                onRect = { socialRect = it },
                onZoom = { socialZoom = it },
                onPanDelta = { dx, dy ->
                    socialPanX = (socialPanX + dx).coerceIn(-1f, 1f)
                    socialPanY = (socialPanY + dy).coerceIn(-1f, 1f)
                },
                onFill = { socialFill = it },
                onCustomColor = { socialCustomColor = it },
                onSelectImage = { pendingSocialTool = true; pickPhotos() },
                onExport = { applySocial() }
            )
            Screen.ShopHome -> ShopHomeScreen(
                modifier = Modifier.padding(padding),
                photo = workingBitmap,
                photoLabel = workingBitmap?.let { "${it.width} × ${it.height} px" },
                onSelectImage = { pendingShopTool = true; pickPhotos() },
                onShop = { name ->
                    if (name == "Other Shop") { screen = Screen.ShopOther }
                    else { shopPlatform = name; shopFromOther = false; screen = Screen.ShopPlatform }
                }
            )
            Screen.ShopOther -> ShopOtherScreen(
                modifier = Modifier.padding(padding),
                onShop = { name -> shopPlatform = name; shopFromOther = true; screen = Screen.ShopPlatform }
            )
            Screen.ShopPlatform -> ShopPlatformScreen(
                modifier = Modifier.padding(padding),
                platform = ShopSizes.platforms.firstOrNull { it.name == shopPlatform },
                customW = shopCustomW,
                customH = shopCustomH,
                onCustomW = { shopCustomW = it.filter { c -> c.isDigit() } },
                onCustomH = { shopCustomH = it.filter { c -> c.isDigit() } },
                onTarget = { selectShopTarget(it) },
                onCollage = { size ->
                    collageTarget = size
                    collagePhotos.clear()
                    screen = Screen.ShopCollage
                },
                onCustom = {
                    val w = shopCustomW.toIntOrNull()
                    val h = shopCustomH.toIntOrNull()
                    if (w == null || h == null || w < 16 || h < 16 || w > 12000 || h > 12000) {
                        scope.launch { snackbar.showSnackbar("Enter width and height between 16 and 12000 px.") }
                    } else {
                        selectShopTarget(SocialSize("Custom", "$w x $h px", w, h))
                    }
                }
            )
            Screen.ShopEdit -> ShopEditScreen(
                modifier = Modifier.padding(padding),
                working = workingBitmap,
                target = shopTarget,
                mode = shopMode,
                rect = shopRect,
                zoom = shopZoom,
                panX = shopPanX,
                panY = shopPanY,
                fill = shopFill,
                customColor = shopCustomColor,
                busy = busy,
                onMode = { shopMode = it },
                onRect = { shopRect = it },
                onZoom = { shopZoom = it },
                onPanDelta = { dx, dy ->
                    shopPanX = (shopPanX + dx).coerceIn(-1f, 1f)
                    shopPanY = (shopPanY + dy).coerceIn(-1f, 1f)
                },
                onFill = { shopFill = it },
                onCustomColor = { shopCustomColor = it },
                onSelectImage = { pendingShopTool = true; pickPhotos() },
                onExport = { applyShop() },
                onBatch = { shopBatchPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            )
            Screen.ShopCollage -> ShopCollageScreen(
                modifier = Modifier.padding(padding),
                target = collageTarget,
                layout = collageLayout,
                photos = collagePhotos,
                busy = busy,
                onLayout = { n ->
                    collageLayout = n
                    while (collagePhotos.size > n) collagePhotos.removeAt(collagePhotos.size - 1)
                },
                onSelectPhotos = { collagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onExport = { applyShopCollage() }
            )
            Screen.ConvertHome -> ConvertHomeScreen(
                modifier = Modifier.padding(padding),
                thumbs = convertThumbs,
                labels = convertLabels,
                onSelectImages = { convertPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onSelectPdf = { convertPdfPicker.launch(arrayOf("application/pdf")) },
                onTarget = { fmt ->
                    convertTarget = fmt
                    if (convertUris.isEmpty()) {
                        convertGoEdit = true
                        convertPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        screen = Screen.ConvertEdit
                    }
                }
            )
            Screen.ConvertEdit -> ConvertEditScreen(
                modifier = Modifier.padding(padding),
                thumbs = convertThumbs,
                labels = convertLabels,
                target = convertTarget,
                quality = convertQuality,
                onQuality = { convertQuality = it },
                bg = convertBg,
                onBg = { convertBg = it },
                customColor = convertCustomColor,
                onCustomColor = { convertCustomColor = it },
                busy = busy,
                onConvert = { applyConvert() },
                onTarget = { convertTarget = it }
            )
            Screen.ConvertResult -> ConvertResultScreen(
                modifier = Modifier.padding(padding),
                preview = convertResultPreview,
                fromLabel = convertResultFrom,
                target = convertTarget,
                origSize = convertResultOrigSize,
                newSize = convertBatchResults?.let { convertBatchOutTotal } ?: (convertResultBytes?.size?.toLong() ?: -1L),
                saved = convertSaved,
                batchCount = convertBatchResults?.size ?: 0,
                batchIsPdf = convertBatchIsPdf,
                onSave = { saveConvertResult() },
                onConvertAnother = { resetConvert(); screen = Screen.ConvertHome }
            )
            Screen.CompressHome -> CompressHomeScreen(
                modifier = Modifier.padding(padding),
                thumbs = compressThumbs,
                labels = compressLabels,
                onSelectImages = { compressPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onSelectPdf = { compressPdfPicker.launch(arrayOf("application/pdf")) },
                onMode = { m ->
                    compressMode = m
                    if (compressUris.isEmpty()) {
                        compressGoSettings = true
                        compressPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        screen = Screen.CompressSettings
                    }
                }
            )
            Screen.CompressSettings -> CompressSettingsScreen(
                modifier = Modifier.padding(padding),
                thumbs = compressThumbs,
                labels = compressLabels,
                names = compressNames,
                origSizes = compressOrigSizes,
                isPdf = compressIsPdf,
                mode = compressMode,
                onMode = { compressMode = it },
                quality = compressQuality,
                onQuality = { compressQuality = it },
                targetKb = compressTargetKb,
                onTargetKb = { compressTargetKb = it },
                resizeOn = compressResizeOn,
                onResizeOn = { compressResizeOn = it },
                resizePct = compressResizePct,
                onResizePct = { compressResizePct = it },
                busy = busy,
                onCompress = { applyCompress() }
            )
            Screen.CompressResult -> CompressResultScreen(
                modifier = Modifier.padding(padding),
                isPdf = compressIsPdf,
                batchFiles = compressBatchFiles,
                batchOrig = compressBatchOrig,
                origSize = compressResultOrig,
                newBytes = compressResultBytes,
                resultName = compressNames.firstOrNull() ?: "image",
                pdfPages = compressPdfPages?.size ?: 0,
                before = compressBefore,
                after = compressAfter,
                saved = compressSaved,
                onSave = { saveCompressResult() },
                onAgain = { resetCompress(); screen = Screen.CompressHome }
            )
            Screen.PrintHome -> PrintHomeScreen(
                modifier = Modifier.padding(padding),
                thumb = printThumb,
                name = printName,
                label = printLabel,
                sizeBytes = printOrigSize,
                imgW = printImgW,
                imgH = printImgH,
                onPick = { printPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onPickPdf = { printPdfPicker.launch(arrayOf("application/pdf")) },
                pdfPageCount = if (printPdfPickerOpen) printPdfPageCount else 0,
                pdfThumbs = printPdfThumbs,
                pdfSelected = printPdfSelected,
                onPdfPagesAdd = { pages -> pages.forEach { printPdfSelected.add(it) } },
                onPdfThumbNeeded = { ensurePrintPdfThumb(it) },
                onPdfAllPages = { selectPrintPdfAllPages() },
                onPdfPage = { togglePrintPdfPage(it) },
                onPdfPages = { selectPrintPdfPages(it) },
                onContinue = { screen = Screen.PrintSetup }
            )
            Screen.PrintSetup -> {
                val (pwIn, phIn) = printDimsIn()
                PrintSetupScreen(
                    modifier = Modifier.padding(padding),
                    preset = printPreset,
                    onPreset = {
                        printPreset = it
                        if (PrintUtils.presets.firstOrNull { p -> p.name == it }?.group == "ID / Passport") printSheetOn = true
                    },
                    unit = printUnit,
                    onUnit = { printUnit = it },
                    wText = printWText,
                    onWText = { v ->
                        printWText = v
                        printPreset = "Custom"
                        val w = v.replace(",", ".").toDoubleOrNull()
                        val a = printImageAspect()
                        if (printLockAspect && w != null && w > 0 && a > 0) {
                            printHText = printFromIn(printToIn(w) / a)
                        }
                    },
                    hText = printHText,
                    onHText = { v ->
                        printHText = v
                        printPreset = "Custom"
                        val h = v.replace(",", ".").toDoubleOrNull()
                        val a = printImageAspect()
                        if (printLockAspect && h != null && h > 0 && a > 0) {
                            printWText = printFromIn(printToIn(h) * a)
                        }
                    },
                    landscape = printLandscape,
                    onLandscape = { printLandscape = it },
                    lockAspect = printLockAspect,
                    onLockAspect = { printLockAspect = it },
                    bleedOn = printBleedOn,
                    onBleedOn = { printBleedOn = it },
                    ppi = printPpi,
                    onPpi = { printPpi = it },
                    fitMode = printFitMode,
                    onFitMode = { printFitMode = it },
                    imgW = printImgW,
                    imgH = printImgH,
                    wIn = pwIn,
                    hIn = phIn,
                    aspect = printImageAspect(),
                    onResetProportions = {
                        val a = printImageAspect()
                        val w = printWText.replace(",", ".").toDoubleOrNull()
                        val h = printHText.replace(",", ".").toDoubleOrNull()
                        if (a > 0 && w != null && w > 0) {
                            printHText = printFromIn(printToIn(w) / a)
                        } else if (a > 0 && h != null && h > 0) {
                            printWText = printFromIn(printToIn(h) * a)
                        }
                        printPreset = "Custom"
                    },
                    multiCopy = printMultiCopyEligible(),
                    sheetOn = printSheetOn,
                    onSheetOn = { printSheetOn = it },
                    sheet = printSheet,
                    onSheet = { printSheet = it },
                    copyCount = printCopyCount,
                    onCopyCount = { printCopyCount = it },
                    maxCount = printSheetMaxCount(),
                    gridCount = printSheetGrid()[2],
                    onContinue = { screen = Screen.PrintPreview }
                )
            }
            Screen.PrintPreview -> {
                val (vwIn, vhIn) = printDimsIn()
                PrintPreviewScreen(
                    modifier = Modifier.padding(padding),
                    preview = printPreview,
                    wIn = vwIn,
                    hIn = vhIn,
                    preset = printPreset,
                    landscape = printLandscape,
                    ppi = printPpi,
                    fitMode = printFitMode,
                    onFitMode = { printFitMode = it },
                    marginsOn = printMarginsOn,
                    onMarginsOn = { printMarginsOn = it },
                    bleedOn = printBleedOn,
                    onBleedOn = { printBleedOn = it },
                    cropMarks = printCropMarks,
                    onCropMarks = { printCropMarks = it },
                    bgWhite = printBgWhite,
                    onBgWhite = { printBgWhite = it },
                    sheetMode = printSheetOn && printMultiCopyEligible(),
                    sheet = printSheet,
                    grid = printSheetGrid(),
                    sheetWIn = printSheetDimsIn().first,
                    sheetHIn = printSheetDimsIn().second,
                    allPagesNote = when {
                        printPdfAllPages && printPdfPageCount > 1 -> "All $printPdfPageCount pages are exported in one run with the same settings - this preview shows page 1."
                        printPdfPageList != null && printPdfPageList!!.size > 1 -> "${printPdfPageList!!.size} selected pages are exported in one run with the same settings - this preview shows the first selected page."
                        else -> null
                    },
                    onContinue = { screen = Screen.PrintExport }
                )
            }
            Screen.PrintExport -> {
                val (ewIn, ehIn) = printDimsIn()
                PrintExportScreen(
                    modifier = Modifier.padding(padding),
                    format = printFormat,
                    onFormat = { printFormat = it },
                    filename = printFilename,
                    onFilename = { printFilename = it },
                    jpgQuality = printJpgQuality,
                    onJpgQuality = { printJpgQuality = it },
                    wIn = ewIn,
                    hIn = ehIn,
                    ppi = printPpi,
                    bleedOn = printBleedOn,
                    marginsOn = printMarginsOn,
                    cropMarks = printCropMarks,
                    imgW = printImgW,
                    imgH = printImgH,
                    sheetMode = printSheetOn && printMultiCopyEligible(),
                    grid = printSheetGrid(),
                    sheet = printSheet,
                    resultReady = printResultBytes != null || printResultFiles.isNotEmpty(),
                    resultName = if (printResultFiles.size > 1) "${printResultFiles.size} files · ${printResultFiles.first().name} …" else printResultName,
                    resultSize = printResultBytes?.size?.toLong() ?: if (printResultFiles.isNotEmpty()) printResultFiles.sumOf { it.length() } else -1L,
                    resultDims = printResultDims,
                    saved = printSaved,
                    busy = busy,
                    onCreate = { applyPrintExport() },
                    onSave = { savePrintResult() },
                    onShare = { sharePrintResult() },
                    onAgain = { resetPrint(); screen = Screen.PrintHome }
                )
            }
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
        Feature("Circle Cutter", "Cut photos into shapes", "◯"),
        Feature("Social Media Sizes", "Creator-ready dimensions", "▦"),
        Feature("Online Shop Sizes", "Marketplace-ready images", "▣"),
        Feature("Format Converter", "Any format to any format", "⇄"),
        Feature("Compress", "Reduce file size", "↓"),
        Feature("Batch Resize", "Process many photos", "▤"),
        Feature("Print Size & DPI", "Ready images for printing", "◉")
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
    cropRect: FloatArray,
    cropAspect: String,
    straightenAngle: Float,
    tiltHAngle: Float,
    tiltVAngle: Float,
    adjustMode: String,
    onAdjustMode: (String) -> Unit,
    onAdjustValue: (Float) -> Unit,
    onAdjustReset: () -> Unit,
    onCropRect: (FloatArray) -> Unit,
    onCropAspect: (String) -> Unit,
    onCropTransform: (CropTransform) -> Unit,
    onApplyCrop: () -> Unit,
    onProcess: () -> Unit,
    onExport: () -> Unit,
    bgPreview: Bitmap?,
    bgCutoutReady: Boolean,
    bgChoice: BgChoice,
    bgCustomColor: Long,
    bgUnit: String,
    bgWidth: String,
    bgHeight: String,
    bgDpi: String,
    bgError: String,
    onBgChoice: (BgChoice) -> Unit,
    onBgCustomColor: (Long) -> Unit,
    onBgUnit: (String) -> Unit,
    onBgWidth: (String) -> Unit,
    onBgHeight: (String) -> Unit,
    onBgDpi: (String) -> Unit,
    onRemoveBackground: () -> Unit,
    onBgExport: () -> Unit,
    circleShape: String,
    circleRect: FloatArray,
    circleChoice: BgChoice,
    circleCustomColor: Long,
    circleUnit: String,
    circleSize: String,
    circleDpi: String,
    circlePreview: Bitmap?,
    onCircleShape: (String) -> Unit,
    onCircleRect: (FloatArray) -> Unit,
    onCircleChoice: (BgChoice) -> Unit,
    onCircleCustomColor: (Long) -> Unit,
    onCircleUnit: (String) -> Unit,
    onCircleSize: (String) -> Unit,
    onCircleDpi: (String) -> Unit,
    onApplyCircle: () -> Unit,
    onSelectImage: () -> Unit
) {
    // While the crop frame is being dragged the page scroll is paused, otherwise
    // the scroll steals vertical drags and the frame feels stuck (his report).
    var cropDragging by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState(), enabled = !cropDragging)) {
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
            val display = if (tool == Tool.Background && bgPreview != null) bgPreview else working
            val displayImage = remember(display) { display?.asImageBitmap() }
            if (tool == Tool.Circle && working != null) {
                ShapePreview(
                    bitmap = working,
                    rect = circleRect,
                    shape = circleShape,
                    onRect = onCircleRect,
                    onDraggingChange = { cropDragging = it },
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )
            } else if (tool == Tool.Crop && working != null) {
                CropPreview(
                    bitmap = working,
                    rect = cropRect,
                    aspect = CropMath.aspectValue(cropAspect, working.width, working.height),
                    straighten = straightenAngle,
                    tiltH = tiltHAngle,
                    tiltV = tiltVAngle,
                    onRect = onCropRect,
                    onDraggingChange = { cropDragging = it },
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )
            } else if (displayImage != null) {
                Image(bitmap = displayImage, contentDescription = "Image preview", modifier = Modifier.fillMaxSize().padding(8.dp), contentScale = ContentScale.Fit)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)).clickable { onSelectImage() }.padding(24.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(40.dp))
                    Text("Select Image", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.padding(top = 10.dp))
                    Text("Choose a photo from your phone", color = Slate, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
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
        } else if (tool == Tool.Background) {
            if (!bgCutoutReady) {
                Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Cut the subject out of your photo, right on your phone - nothing is uploaded. The first use downloads a small model once (internet needed); after that it works fully offline.", color = Slate, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(16.dp))
                }
            }
            Button(
                onClick = onRemoveBackground,
                modifier = Modifier.fillMaxWidth().padding(top = if (bgCutoutReady) 0.dp else 10.dp).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                enabled = !busy && working != null
            ) {
                if (busy) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp) else {
                    Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (bgCutoutReady) "Remove Again" else "Remove Background", fontWeight = FontWeight.Bold)
                }
            }
            if (bgError.isNotBlank()) {
                Text(bgError, color = Color(0xFFB3261E), fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 8.dp))
            }
            if (bgCutoutReady) {
                Text("Output Background", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    BgChoice.values().forEach { item ->
                        val selected = bgChoice == item
                        Surface(modifier = Modifier.clickable { onBgChoice(item) }, shape = RoundedCornerShape(10.dp), color = if (selected) SkyBlue else SoftGrey) {
                            Text(item.label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), fontSize = 12.sp, color = if (selected) Color.White else DeepNavy, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                        }
                    }
                }
                if (bgChoice == BgChoice.CUSTOM) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                        listOf(0xFF42A5F5, 0xFFFF7043, 0xFF66BB6A, 0xFFEC407A, 0xFFAB47BC, 0xFFFFCA28, 0xFF8D6E63, 0xFF212121).forEach { colorValue ->
                            val selected = bgCustomColor == colorValue
                            Box(
                                Modifier.size(34.dp).clip(CircleShape).background(Color(colorValue))
                                    .border(if (selected) 3.dp else 1.dp, if (selected) SkyBlue else BorderGrey, CircleShape)
                                    .clickable { onBgCustomColor(colorValue) }
                            )
                        }
                    }
                }
                Text("Output size and DPI", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    listOf("px" to "Pixels", "in" to "Inches", "cm" to "cm", "mm" to "mm").forEach { (value, label) ->
                        val selected = bgUnit == value
                        Surface(modifier = Modifier.clickable { onBgUnit(value) }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                            Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = bgWidth, onValueChange = onBgWidth, modifier = Modifier.weight(1f), label = { Text(if (bgUnit == "px") "Width (px)" else "Width ($bgUnit)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                    OutlinedTextField(value = bgHeight, onValueChange = onBgHeight, modifier = Modifier.weight(1f), label = { Text(if (bgUnit == "px") "Height (px)" else "Height ($bgUnit)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
                OutlinedTextField(value = bgDpi, onValueChange = onBgDpi, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("DPI - 300 is standard for print") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                Text("Transparent stays see-through in PNG and WebP. JPG and PDF put the photo on white.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 8.dp))
                Button(
                    onClick = onBgExport,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !busy
                ) {
                    Text("Continue to Export", fontWeight = FontWeight.Bold)
                }
            }
        } else if (tool == Tool.Crop) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf(
                    Triple(CropTransform.LEFT, "Left 90°", Icons.Default.RotateLeft),
                    Triple(CropTransform.RIGHT, "Right 90°", Icons.Default.RotateRight),
                    Triple(CropTransform.FLIP_H, "Mirror", Icons.Default.SwapHoriz),
                    Triple(CropTransform.FLIP_V, "Flip V", Icons.Default.SwapVert)
                ).forEach { (kind, label, icon) ->
                    OutlinedButton(onClick = { onCropTransform(kind) }, shape = RoundedCornerShape(10.dp), enabled = !busy && working != null) {
                        Icon(icon, contentDescription = label, modifier = Modifier.size(16.dp), tint = DeepNavy)
                        Spacer(Modifier.width(4.dp))
                        Text(label, color = DeepNavy, fontSize = 12.sp, maxLines = 1, softWrap = false)
                    }
                }
            }
            Text("Adjust", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                    listOf("Straighten", "Horizontal", "Vertical").forEach { label ->
                        val selected = adjustMode == label
                        Surface(modifier = Modifier.clickable { onAdjustMode(label) }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                            Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                        }
                    }
                }
                if (straightenAngle != 0f || tiltHAngle != 0f || tiltVAngle != 0f) {
                    Text(
                        "Reset",
                        color = SkyBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp).clip(RoundedCornerShape(8.dp)).clickable { onAdjustReset() }.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
            AdjustDialAndSlider(
                value = when (adjustMode) {
                    "Horizontal" -> tiltHAngle
                    "Vertical" -> tiltVAngle
                    else -> straightenAngle
                },
                onValue = onAdjustValue,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Text("Aspect ratio", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf("Free", "Original", "1:1", "4:3", "16:9", "9:16").forEach { label ->
                    val selected = cropAspect == label
                    Surface(modifier = Modifier.clickable { onCropAspect(label) }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                        Text(label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), fontSize = 12.sp, color = if (selected) Color.White else DeepNavy, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                    }
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 10.dp)) {
                Text("Drag the corners on the photo to frame it. Drag inside the frame to move it. Rotate or flip first if you like - everything happens on your phone, nothing is uploaded.", color = Slate, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(16.dp))
            }
            Button(
                onClick = onApplyCrop,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                enabled = !busy && working != null
            ) {
                if (busy) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp) else {
                    Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Apply Crop", fontWeight = FontWeight.Bold)
                }
            }
        } else if (tool == Tool.Circle) {
            Text("Shape", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf("Circle", "Square", "Rounded", "Heart").forEach { label ->
                    val selected = circleShape == label
                    Surface(modifier = Modifier.clickable { onCircleShape(label) }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                        Text(label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), fontSize = 12.sp, color = if (selected) Color.White else DeepNavy, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                    }
                }
            }
            Text("Background", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 14.dp, bottom = 4.dp))
            Text("Choose what to show outside the shape", color = Slate, fontSize = 11.sp, modifier = Modifier.padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                BgChoice.values().forEach { item ->
                    val selected = circleChoice == item
                    Surface(modifier = Modifier.clickable { onCircleChoice(item) }, shape = RoundedCornerShape(10.dp), color = if (selected) SkyBlue else SoftGrey) {
                        Text(item.label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), fontSize = 12.sp, color = if (selected) Color.White else DeepNavy, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                    }
                }
            }
            if (circleChoice == BgChoice.CUSTOM) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    listOf(0xFF42A5F5, 0xFFFF7043, 0xFF66BB6A, 0xFFEC407A, 0xFFAB47BC, 0xFFFFCA28, 0xFF8D6E63, 0xFF212121).forEach { colorValue ->
                        val selected = circleCustomColor == colorValue
                        Box(
                            Modifier.size(34.dp).clip(CircleShape).background(Color(colorValue))
                                .border(if (selected) 3.dp else 1.dp, if (selected) SkyBlue else BorderGrey, CircleShape)
                                .clickable { onCircleCustomColor(colorValue) }
                        )
                    }
                }
            }
            Text("Output size and DPI", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf("px" to "Pixels", "in" to "Inches", "cm" to "cm", "mm" to "mm").forEach { (value, label) ->
                    val selected = circleUnit == value
                    Surface(modifier = Modifier.clickable { onCircleUnit(value) }, shape = RoundedCornerShape(9.dp), color = if (selected) SkyBlue else PaleBlue) {
                        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), fontSize = 11.sp, color = if (selected) Color.White else DeepNavy, textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
                    }
                }
            }
            OutlinedTextField(value = circleSize, onValueChange = onCircleSize, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text(if (circleUnit == "px") "Size (px)" else "Size ($circleUnit)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            OutlinedTextField(value = circleDpi, onValueChange = onCircleDpi, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("DPI - 300 is standard for print") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            Text("Transparent stays see-through in PNG and WebP. JPG and PDF put the photo on white.", color = Slate, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 8.dp))
            if (circlePreview != null) {
                Text("Preview", color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
                    Image(bitmap = circlePreview.asImageBitmap(), contentDescription = "Live preview of the shape cutout", modifier = Modifier.padding(14.dp).size(96.dp), contentScale = ContentScale.Fit)
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 10.dp)) {
                Text("Drag inside the frame to move it. Drag a corner to resize. Everything happens on your phone, nothing is uploaded.", color = Slate, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(16.dp))
            }
            Button(
                onClick = onApplyCircle,
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                enabled = !busy && working != null
            ) {
                if (busy) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp) else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Crop", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Text("${tool.label} controls will be added in the next development stages. Resize and export are available now.", color = DeepNavy, fontSize = 13.sp)
        }
        Spacer(Modifier.height(12.dp))
        if (tool != Tool.Background && tool != Tool.Crop && tool != Tool.Circle) {
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

/** The shape outline inside frame (l,t,r,b) for preview drawing. Heart is a
 *  symmetric two-lobe curve; Rounded uses an 18% corner radius. */
private fun Path.addShapeOutline(shape: String, l: Float, t: Float, r: Float, b: Float) {
    val w = r - l
    val h = b - t
    when (shape) {
        "Circle" -> addOval(Rect(l, t, r, b))
        "Rounded" -> addRoundRect(RoundRect(l, t, r, b, CornerRadius(w * 0.18f)))
        "Heart" -> {
            moveTo(l + 0.5f * w, t + 0.28f * h)
            cubicTo(l + 0.5f * w, t + 0.18f * h, l + 0.36f * w, t + 0.02f * h, l + 0.2f * w, t + 0.02f * h)
            cubicTo(l + 0.02f * w, t + 0.02f * h, l, t + 0.22f * h, l, t + 0.34f * h)
            cubicTo(l, t + 0.52f * h, l + 0.22f * w, t + 0.68f * h, l + 0.5f * w, t + 0.9f * h)
            cubicTo(l + 0.78f * w, t + 0.68f * h, r, t + 0.52f * h, r, t + 0.34f * h)
            cubicTo(r, t + 0.22f * h, l + 0.98f * w, t + 0.02f * h, l + 0.8f * w, t + 0.02f * h)
            cubicTo(l + 0.64f * w, t + 0.02f * h, l + 0.5f * w, t + 0.18f * h, l + 0.5f * w, t + 0.28f * h)
            close()
        }
        else -> addRect(Rect(l, t, r, b))
    }
}

/** Android-graphics twin of addShapeOutline for the export mask (square canvas). */
private fun androidShapePath(shape: String, size: Float): android.graphics.Path {
    val p = android.graphics.Path()
    val rect = android.graphics.RectF(0f, 0f, size, size)
    when (shape) {
        "Circle" -> p.addOval(rect, android.graphics.Path.Direction.CW)
        "Rounded" -> p.addRoundRect(rect, size * 0.18f, size * 0.18f, android.graphics.Path.Direction.CW)
        "Heart" -> {
            p.moveTo(0.5f * size, 0.28f * size)
            p.cubicTo(0.5f * size, 0.18f * size, 0.36f * size, 0.02f * size, 0.2f * size, 0.02f * size)
            p.cubicTo(0.02f * size, 0.02f * size, 0f, 0.22f * size, 0f, 0.34f * size)
            p.cubicTo(0f, 0.52f * size, 0.22f * size, 0.68f * size, 0.5f * size, 0.9f * size)
            p.cubicTo(0.78f * size, 0.68f * size, size, 0.52f * size, size, 0.34f * size)
            p.cubicTo(size, 0.22f * size, 0.98f * size, 0.02f * size, 0.8f * size, 0.02f * size)
            p.cubicTo(0.64f * size, 0.02f * size, 0.5f * size, 0.18f * size, 0.5f * size, 0.28f * size)
            p.close()
        }
        else -> p.addRect(rect, android.graphics.Path.Direction.CW)
    }
    return p
}

/** Crop the framed square region, scale to outSize and mask into the chosen shape
 *  with real alpha (Square keeps the full square). */
private fun shapeCutout(src: Bitmap, rect: FloatArray, shape: String, outSize: Int): Bitmap {
    val cropped = Transforms.cropRegion(src, CropMath.toPixels(rect, src.width, src.height))
    val scaled = Bitmap.createScaledBitmap(cropped, outSize, outSize, true)
    if (shape == "Square") return scaled
    val out = Bitmap.createBitmap(outSize, outSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(out)
    canvas.drawBitmap(scaled, 0f, 0f, null)
    // Erase everything OUTSIDE the shape: draw the inverse (frame minus shape,
    // even-odd) with DST_OUT. A DST_IN draw of the shape itself can never work -
    // a path draw only touches the pixels it covers, so outside pixels stay
    // opaque (that is the v0.10.0 bug he reported as "not crop"). ShapeCutoutTest
    // verifies real output pixels on native graphics, the same Skia his phone runs.
    val inverse = android.graphics.Path().apply {
        addRect(android.graphics.RectF(0f, 0f, outSize.toFloat(), outSize.toFloat()), android.graphics.Path.Direction.CW)
        addPath(androidShapePath(shape, outSize.toFloat()))
        fillType = android.graphics.Path.FillType.EVEN_ODD
    }
    val erase = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT)
    }
    canvas.drawPath(inverse, erase)
    return out
}

/** Shape frame preview for Circle Cutter: the photo with a draggable 1:1 frame
 *  (circle / square / rounded / heart). Same drag mechanics as CropPreview - drag
 *  inside to move, corner handles to resize (aspect locked 1:1), and the page
 *  scroll pauses while a drag is active so the frame never feels stuck. */
@Composable
private fun ShapePreview(
    bitmap: Bitmap,
    rect: FloatArray,
    shape: String,
    onRect: (FloatArray) -> Unit,
    onDraggingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val boxW = constraints.maxWidth.toFloat()
        val boxH = constraints.maxHeight.toFloat()
        val scale = minOf(boxW / bitmap.width, boxH / bitmap.height)
        val dispW = bitmap.width * scale
        val dispH = bitmap.height * scale
        val offX = (boxW - dispW) / 2f
        val offY = (boxH - dispH) / 2f
        val density = LocalDensity.current
        val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
        Image(
            bitmap = imageBitmap,
            contentDescription = "Shape preview - drag to position the frame",
            modifier = Modifier.size(with(density) { dispW.toDp() }, with(density) { dispH.toDp() }),
            contentScale = ContentScale.FillBounds
        )
        val hitRadius = with(density) { 34.dp.toPx() }
        val currentRect by rememberUpdatedState(rect)
        val currentOnRect by rememberUpdatedState(onRect)
        val currentOnDragging by rememberUpdatedState(onDraggingChange)
        val imgAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        Canvas(
            Modifier.fillMaxSize().pointerInput(bitmap) {
                var mode = -1 // -1 none, 0..3 corner handle, 4 move frame
                detectDragGestures(
                    onDragStart = { p ->
                        val r0 = currentRect
                        val l = offX + r0[0] * dispW
                        val t = offY + r0[1] * dispH
                        val r = offX + r0[2] * dispW
                        val b = offY + r0[3] * dispH
                        val corners = listOf(l to t, r to t, l to b, r to b)
                        mode = -1
                        var best = Float.MAX_VALUE
                        var bestIdx = -1
                        corners.forEachIndexed { i, c ->
                            val d = (p.x - c.first) * (p.x - c.first) + (p.y - c.second) * (p.y - c.second)
                            if (d < best) { best = d; bestIdx = i }
                        }
                        if (bestIdx >= 0 && best <= hitRadius * hitRadius) mode = bestIdx
                        else if (p.x in l..r && p.y in t..b) mode = 4
                        currentOnDragging(mode != -1)
                    },
                    onDrag = { change, drag ->
                        change.consume()
                        val dx = drag.x / dispW
                        val dy = drag.y / dispH
                        val next = when {
                            mode == 4 -> CropMath.move(currentRect, dx, dy)
                            mode in 0..3 -> CropMath.dragCorner(currentRect, mode, dx, dy, 1f, imgAspect)
                            else -> currentRect
                        }
                        if (next !== currentRect) currentOnRect(next)
                    },
                    onDragEnd = { mode = -1; currentOnDragging(false) },
                    onDragCancel = { mode = -1; currentOnDragging(false) }
                )
            }
        ) {
            val l = offX + rect[0] * dispW
            val t = offY + rect[1] * dispH
            val r = offX + rect[2] * dispW
            val b = offY + rect[3] * dispH
            val dim = Color.Black.copy(alpha = 0.45f)
            val dimPath = Path().apply {
                addRect(Rect(0f, 0f, boxW, boxH))
                addShapeOutline(shape, l, t, r, b)
                fillType = PathFillType.EvenOdd
            }
            drawPath(dimPath, dim)
            drawPath(Path().apply { addShapeOutline(shape, l, t, r, b) }, SkyBlue, style = Stroke(2.dp.toPx()))
            val handleR = 9.dp.toPx()
            listOf(l to t, r to t, l to b, r to b).forEach { c ->
                drawCircle(Color.White, handleR, Offset(c.first, c.second))
                drawCircle(SkyBlue, handleR, Offset(c.first, c.second), style = Stroke(3.dp.toPx()))
            }
        }
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

/** Composite the cut-out onto the chosen background, fit and centered on the target canvas. */
private fun composeOnBackground(cutout: Bitmap, targetW: Int, targetH: Int, choice: BgChoice, customColor: Long): Bitmap {
    val out = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(out)
    when (choice) {
        BgChoice.TRANSPARENT -> {}
        BgChoice.WHITE -> canvas.drawColor(android.graphics.Color.WHITE)
        BgChoice.CUSTOM -> canvas.drawColor(customColor.toInt())
    }
    val scale = minOf(targetW.toFloat() / cutout.width, targetH.toFloat() / cutout.height)
    val w = (cutout.width * scale).coerceAtLeast(1f)
    val h = (cutout.height * scale).coerceAtLeast(1f)
    val left = (targetW - w) / 2f
    val top = (targetH - h) / 2f
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
    canvas.drawBitmap(cutout, null, android.graphics.RectF(left, top, left + w, top + h), paint)
    return out
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

/**
 * Interactive crop frame over the photo: dimmed outside area, rule-of-thirds
 * grid, big corner handles. Drag a corner to resize (aspect lock honored),
 * drag inside the frame to move it. rect is normalized over the photo.
 */
@Composable
private fun CropPreview(
    bitmap: Bitmap,
    rect: FloatArray,
    aspect: Float?,
    straighten: Float,
    tiltH: Float,
    tiltV: Float,
    onRect: (FloatArray) -> Unit,
    onDraggingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val boxW = constraints.maxWidth.toFloat()
        val boxH = constraints.maxHeight.toFloat()
        val scale = minOf(boxW / bitmap.width, boxH / bitmap.height)
        val dispW = bitmap.width * scale
        val dispH = bitmap.height * scale
        val offX = (boxW - dispW) / 2f
        val offY = (boxH - dispH) / 2f
        val density = LocalDensity.current
        val hasAdjust = straighten != 0f || tiltH != 0f || tiltV != 0f
        val adjustMatrix = remember(bitmap, straighten, tiltH, tiltV, dispW, dispH, offX, offY) {
            if (!hasAdjust) null else {
                val focal = 1.5f * maxOf(bitmap.width, bitmap.height)
                val quad = CropMath.transformedCorners(bitmap.width, bitmap.height, straighten, tiltH, tiltV, focal)
                val cover = CropMath.coverScale(quad, bitmap.width, bitmap.height)
                val cx = bitmap.width / 2f
                val cy = bitmap.height / 2f
                val dst = FloatArray(8)
                for (i in 0..3) {
                    dst[i * 2] = cx + (quad[i * 2] - cx) * cover
                    dst[i * 2 + 1] = cy + (quad[i * 2 + 1] - cy) * cover
                }
                val srcPts = floatArrayOf(0f, 0f, bitmap.width.toFloat(), 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), 0f, bitmap.height.toFloat())
                android.graphics.Matrix().apply {
                    setPolyToPoly(srcPts, 0, dst, 0, 4)
                    postScale(dispW / bitmap.width, dispH / bitmap.height)
                    postTranslate(offX, offY)
                }
            }
        }
        val adjustPaint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG) }
        val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
        if (adjustMatrix == null) {
            // The Box centers the child; adding offX/offY here too double-shifted
            // the photo to one side (his v0.9.1 bug report). No manual offset.
            Image(
                bitmap = imageBitmap,
                contentDescription = "Crop preview - drag the corners to frame the photo",
                modifier = Modifier.size(with(density) { dispW.toDp() }, with(density) { dispH.toDp() }),
                contentScale = ContentScale.FillBounds
            )
        }
        val hitRadius = with(density) { 34.dp.toPx() }
        val currentRect by rememberUpdatedState(rect)
        val currentOnRect by rememberUpdatedState(onRect)
        val currentOnDragging by rememberUpdatedState(onDraggingChange)
        val imgAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        Canvas(
            Modifier.fillMaxSize().pointerInput(bitmap, aspect) {
                var mode = -1 // -1 none, 0..3 corner handle, 4 move frame
                detectDragGestures(
                    onDragStart = { p ->
                        val r0 = currentRect
                        val l = offX + r0[0] * dispW
                        val t = offY + r0[1] * dispH
                        val r = offX + r0[2] * dispW
                        val b = offY + r0[3] * dispH
                        val corners = listOf(l to t, r to t, l to b, r to b)
                        mode = -1
                        var best = Float.MAX_VALUE
                        var bestIdx = -1
                        corners.forEachIndexed { i, c ->
                            val d = (p.x - c.first) * (p.x - c.first) + (p.y - c.second) * (p.y - c.second)
                            if (d < best) { best = d; bestIdx = i }
                        }
                        if (bestIdx >= 0 && best <= hitRadius * hitRadius) mode = bestIdx
                        else if (p.x in l..r && p.y in t..b) mode = 4
                        currentOnDragging(mode != -1)
                    },
                    onDrag = { change, drag ->
                        change.consume()
                        val dx = drag.x / dispW
                        val dy = drag.y / dispH
                        val next = when {
                            mode == 4 -> CropMath.move(currentRect, dx, dy)
                            mode in 0..3 -> CropMath.dragCorner(currentRect, mode, dx, dy, aspect, imgAspect)
                            else -> currentRect
                        }
                        if (next !== currentRect) currentOnRect(next)
                    },
                    onDragEnd = { mode = -1; currentOnDragging(false) },
                    onDragCancel = { mode = -1; currentOnDragging(false) }
                )
            }
        ) {
            adjustMatrix?.let { drawContext.canvas.nativeCanvas.drawBitmap(bitmap, it, adjustPaint) }
            val l = offX + rect[0] * dispW
            val t = offY + rect[1] * dispH
            val r = offX + rect[2] * dispW
            val b = offY + rect[3] * dispH
            val dim = Color.Black.copy(alpha = 0.45f)
            drawRect(dim, Offset.Zero, Size(boxW, t))
            drawRect(dim, Offset(0f, b), Size(boxW, boxH - b))
            drawRect(dim, Offset(0f, t), Size(l, b - t))
            drawRect(dim, Offset(r, t), Size(boxW - r, b - t))
            val grid = Color.White.copy(alpha = 0.5f)
            val gridStroke = 1.dp.toPx()
            for (i in 1..2) {
                val gx = l + (r - l) * i / 3f
                val gy = t + (b - t) * i / 3f
                drawLine(grid, Offset(gx, t), Offset(gx, b), gridStroke)
                drawLine(grid, Offset(l, gy), Offset(r, gy), gridStroke)
            }
            drawRect(SkyBlue, Offset(l, t), Size(r - l, b - t), style = Stroke(2.dp.toPx()))
            val handleR = 9.dp.toPx()
            listOf(l to t, r to t, l to b, r to b).forEach { c ->
                drawCircle(Color.White, handleR, Offset(c.first, c.second))
                drawCircle(SkyBlue, handleR, Offset(c.first, c.second), style = Stroke(3.dp.toPx()))
            }
        }
    }
}

/**
 * The straighten/tilt control from his design reference: a round value dial
 * over a draggable tick ruler, in the app's light style. Range -45..+45
 * degrees in half-degree steps.
 */
@Composable
private fun AdjustDialAndSlider(
    value: Float,
    onValue: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(76.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = 6.dp.toPx()
                val inset = stroke / 2f + 2.dp.toPx()
                drawCircle(SoftGrey, radius = size.minDimension / 2f - inset, style = Stroke(stroke))
                val sweep = (kotlin.math.abs(value) / 45f).coerceIn(0f, 1f) * 360f
                if (sweep > 0.5f) {
                    drawArc(
                        SkyBlue,
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - inset * 2, size.height - inset * 2),
                        style = Stroke(stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                }
            }
            Text("%.1f".format(value), color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        val currentValue by rememberUpdatedState(value)
        val currentOnValue by rememberUpdatedState(onValue)
        Canvas(
            Modifier.fillMaxWidth().padding(top = 8.dp).height(44.dp)
                .pointerInput(Unit) {
                    detectTapGestures { pnt ->
                        val half = size.width / 2f
                        val v = ((pnt.x - half) / half * 45f).coerceIn(-45f, 45f)
                        // Snap to an exact zero nearby: getting back to "no tilt" should be easy.
                        currentOnValue(if (kotlin.math.abs(v) < 1f) 0f else (v * 2).roundToInt() / 2f)
                    }
                }
                .pointerInput(Unit) {
                    var acc = 0f
                    var base = 0f
                    detectDragGestures(
                        onDragStart = { acc = 0f; base = currentValue },
                        onDrag = { change, drag ->
                            change.consume()
                            acc += drag.x
                            val half = size.width / 2f
                            val v = (base + acc / half * 45f).coerceIn(-45f, 45f)
                            currentOnValue(if (kotlin.math.abs(v) < 1f) 0f else (v * 2).roundToInt() / 2f)
                        }
                    )
                }
        ) {
            val half = size.width / 2f
            val midY = size.height / 2f
            for (i in -45..45) {
                val x = half + (i - value) / 45f * half
                if (x < 0f || x > size.width) continue
                val big = i % 5 == 0
                val len = (if (big) 14.dp else 8.dp).toPx()
                drawLine(Slate.copy(alpha = if (big) 0.7f else 0.4f), Offset(x, midY - len / 2), Offset(x, midY + len / 2), 1.5.dp.toPx())
            }
            drawLine(SkyBlue, Offset(half, midY - 12.dp.toPx()), Offset(half, midY + 12.dp.toPx()), 3.dp.toPx())
        }
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
private fun encodeImage(context: Context, exifSource: Uri?, bitmap: Bitmap, format: ExportFormat, quality: Int, dpi: Int = 0): ByteArray {
    // JPEG and PDF have no alpha channel: flatten onto white instead of dropping
    // transparency onto black (matters for the Remove Background export).
    val flat = if ((format == ExportFormat.JPEG || format == ExportFormat.PDF) && bitmap.hasAlpha()) {
        val out = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(out)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        out
    } else bitmap
    if (format == ExportFormat.PDF) {
        val document = PdfDocument()
        try {
            // PDF pages are sized in 1/72 inch points: with a DPI we honor the
            // intended physical print size, otherwise one point per pixel.
            val pageW = if (dpi > 0) (flat.width * 72f / dpi).roundToInt().coerceAtLeast(1) else flat.width
            val pageH = if (dpi > 0) (flat.height * 72f / dpi).roundToInt().coerceAtLeast(1) else flat.height
            val page = document.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, 1).create())
            page.canvas.drawBitmap(flat, null, android.graphics.RectF(0f, 0f, pageW.toFloat(), pageH.toFloat()), null)
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
                if (!flat.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                    throw IllegalStateException("Image encoding failed")
                }
            }
            if (exifSource != null) copyExifBestEffort(context, exifSource, tmp, flat.width, flat.height, dpi)
            else if (dpi > 0) writeDpiTags(ExifInterface(tmp.absolutePath), dpi)
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

private fun writeDpiTags(dst: ExifInterface, dpi: Int) {
    try {
        dst.setAttribute(ExifInterface.TAG_X_RESOLUTION, dpi.toString())
        dst.setAttribute(ExifInterface.TAG_Y_RESOLUTION, dpi.toString())
        dst.setAttribute(ExifInterface.TAG_RESOLUTION_UNIT, "2")
        dst.saveAttributes()
    } catch (e: Exception) {
        // Best effort: resolution metadata is informational.
    }
}

private fun copyExifBestEffort(context: Context, source: Uri, outFile: File, width: Int, height: Int, dpi: Int = 0) {
    try {
        val src = context.contentResolver.openFileDescriptor(source, "r")?.use { ExifInterface(it.fileDescriptor) } ?: return
        val dst = ExifInterface(outFile.absolutePath)
        for (tag in EXIF_TAGS_TO_COPY) {
            src.getAttribute(tag)?.let { dst.setAttribute(tag, it) }
        }
        dst.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, width.toString())
        dst.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, height.toString())
        dst.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
        if (dpi > 0) {
            dst.setAttribute(ExifInterface.TAG_X_RESOLUTION, dpi.toString())
            dst.setAttribute(ExifInterface.TAG_Y_RESOLUTION, dpi.toString())
            dst.setAttribute(ExifInterface.TAG_RESOLUTION_UNIT, "2")
        }
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

private fun thumbOf(bmp: Bitmap): Bitmap {
    val maxDim = maxOf(bmp.width, bmp.height)
    if (maxDim <= 192) return bmp
    val scale = 192f / maxDim
    return Bitmap.createScaledBitmap(
        bmp,
        (bmp.width * scale).toInt().coerceAtLeast(1),
        (bmp.height * scale).toInt().coerceAtLeast(1),
        true
    )
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

private data class PrintOutput(val bytes: ByteArray, val name: String, val mime: String, val dims: String)

private fun encodePrintRaster(bmp: Bitmap, format: String, quality: Int, bg: Int, filename: String, wPx: Int, hPx: Int, extraDims: String): PrintOutput {
    val baos = java.io.ByteArrayOutputStream()
    if (format == "PNG") {
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos)
    } else {
        ConvertFormats.flatten(bmp, bg).compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), baos)
    }
    val ext = if (format == "PNG") "png" else "jpg"
    return PrintOutput(baos.toByteArray(), "$filename.$ext", if (format == "PNG") "image/png" else "image/jpeg", "$wPx × $hPx px$extraDims")
}

private fun buildPrintRaster(src: Bitmap, wIn: Double, hIn: Double, bleedIn: Double, marginIn: Double, bg: Int, mode: String, ppi: Int, quality: Int, format: String, filename: String): PrintOutput {
    val totalWIn = wIn + bleedIn * 2
    val totalHIn = hIn + bleedIn * 2
    val req = PrintUtils.requiredPixels(totalWIn, totalHIn, ppi)
    val capped = PrintUtils.cappedPixels(req[0], req[1])
    val wasCapped = capped[0] != req[0] || capped[1] != req[1]
    val marginPxX = (marginIn * capped[0] / totalWIn).toInt()
    val marginPxY = (marginIn * capped[1] / totalHIn).toInt()
    val areaW = (capped[0] - marginPxX * 2).coerceAtLeast(1)
    val areaH = (capped[1] - marginPxY * 2).coerceAtLeast(1)
    val placed = PrintUtils.renderPrint(src, areaW, areaH, mode, if (format == "PNG") android.graphics.Color.TRANSPARENT else bg)
    val out = Bitmap.createBitmap(capped[0], capped[1], Bitmap.Config.ARGB_8888)
    val c = android.graphics.Canvas(out)
    c.drawColor(if (format == "PNG") android.graphics.Color.TRANSPARENT else bg)
    c.drawBitmap(placed, marginPxX.toFloat(), marginPxY.toFloat(), null)
    return encodePrintRaster(out, format, quality, bg, filename, capped[0], capped[1], if (wasCapped) " (scaled to fit device memory)" else "")
}

private fun buildPrintSheetRaster(src: Bitmap, sheetWIn: Double, sheetHIn: Double, cellWIn: Double, cellHIn: Double, cols: Int, rows: Int, count: Int, gapIn: Double, ppi: Int, quality: Int, format: String, filename: String): PrintOutput {
    val req = PrintUtils.requiredPixels(sheetWIn, sheetHIn, ppi)
    val capped = PrintUtils.cappedPixels(req[0], req[1])
    val wasCapped = capped[0] != req[0] || capped[1] != req[1]
    val cellWPx = (cellWIn * capped[0] / sheetWIn).toInt().coerceAtLeast(1)
    val cellHPx = (cellHIn * capped[1] / sheetHIn).toInt().coerceAtLeast(1)
    val gapPx = (gapIn * capped[0] / sheetWIn).toInt()
    val sheet = PrintUtils.renderSheet(src, capped[0], capped[1], cellWPx, cellHPx, cols, rows, gapPx, true, count)
    return encodePrintRaster(sheet, format, quality, android.graphics.Color.WHITE, filename, capped[0], capped[1],
        " · $count ${if (count == 1) "copy" else "copies"} ($cols × $rows)" + (if (wasCapped) " (scaled to fit device memory)" else ""))
}

private fun buildPrintPdf(src: Bitmap, wIn: Double, hIn: Double, bleedIn: Double, marginIn: Double, mode: String, ppi: Int, cropMarks: Boolean, filename: String): PrintOutput {
    val pageWIn = wIn + bleedIn * 2
    val pageHIn = hIn + bleedIn * 2
    val pageWPt = (pageWIn * 72).toInt().coerceAtLeast(1)
    val pageHPt = (pageHIn * 72).toInt().coerceAtLeast(1)
    val areaWIn = (pageWIn - marginIn * 2).coerceAtLeast(0.1)
    val areaHIn = (pageHIn - marginIn * 2).coerceAtLeast(0.1)
    val req0 = PrintUtils.requiredPixels(areaWIn, areaHIn, ppi)
    val req = PrintUtils.cappedPixels(req0[0], req0[1])
    val placed = PrintUtils.renderPrint(src, req[0], req[1], mode, android.graphics.Color.WHITE)
    val doc = PdfDocument()
    val page = doc.startPage(PdfDocument.PageInfo.Builder(pageWPt, pageHPt, 1).create())
    val canvas = page.canvas
    canvas.drawColor(android.graphics.Color.WHITE)
    val left = (marginIn * 72).toFloat()
    val top = (marginIn * 72).toFloat()
    canvas.drawBitmap(placed, null, android.graphics.RectF(left, top, pageWPt - left, pageHPt - top), android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG))
    if (cropMarks && bleedIn > 0) {
        val paint = android.graphics.Paint().apply { color = android.graphics.Color.BLACK; strokeWidth = 0.7f }
        val t = (bleedIn * 72).toFloat()
        val len = 12f
        val off = 4f
        canvas.drawLine(t - off - len, t, t - off, t, paint)
        canvas.drawLine(t, t - off - len, t, t - off, paint)
        canvas.drawLine(pageWPt - t + off, t, pageWPt - t + off + len, t, paint)
        canvas.drawLine(pageWPt - t, t - off - len, pageWPt - t, t - off, paint)
        canvas.drawLine(t - off - len, pageHPt - t, t - off, pageHPt - t, paint)
        canvas.drawLine(t, pageHPt - t + off, t, pageHPt - t + off + len, paint)
        canvas.drawLine(pageWPt - t + off, pageHPt - t, pageWPt - t + off + len, pageHPt - t, paint)
        canvas.drawLine(pageWPt - t, pageHPt - t + off, pageWPt - t, pageHPt - t + off + len, paint)
    }
    doc.finishPage(page)
    val baos = java.io.ByteArrayOutputStream()
    doc.writeTo(baos)
    doc.close()
    val dims = "${PrintUtils.fmtDim(pageWIn, "in")} × ${PrintUtils.fmtDim(pageHIn, "in")} in PDF page"
    return PrintOutput(baos.toByteArray(), "$filename.pdf", "application/pdf", dims)
}

private fun buildPrintSheetPdf(src: Bitmap, sheetWIn: Double, sheetHIn: Double, cellWIn: Double, cellHIn: Double, cols: Int, rows: Int, count: Int, gapIn: Double, ppi: Int, filename: String): PrintOutput {
    val cellPx0 = PrintUtils.requiredPixels(cellWIn, cellHIn, ppi)
    val cellPx = PrintUtils.cappedPixels(cellPx0[0], cellPx0[1])
    val cell = PrintUtils.renderPrint(src, cellPx[0], cellPx[1], "Fill", android.graphics.Color.WHITE)
    val pageWPt = (sheetWIn * 72).toInt().coerceAtLeast(1)
    val pageHPt = (sheetHIn * 72).toInt().coerceAtLeast(1)
    val doc = PdfDocument()
    val page = doc.startPage(PdfDocument.PageInfo.Builder(pageWPt, pageHPt, 1).create())
    val canvas = page.canvas
    canvas.drawColor(android.graphics.Color.WHITE)
    val cellWPt = (cellWIn * 72).toFloat()
    val cellHPt = (cellHIn * 72).toFloat()
    val gapPt = (gapIn * 72).toFloat()
    val gridW = cols * cellWPt + (cols - 1) * gapPt
    val gridH = rows * cellHPt + (rows - 1) * gapPt
    val startX = (pageWPt - gridW) / 2f
    val startY = (pageHPt - gridH) / 2f
    val paint = android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG)
    val guide = android.graphics.Paint().apply { color = android.graphics.Color.LTGRAY; style = android.graphics.Paint.Style.STROKE; strokeWidth = 0.5f }
    for (i in 0 until count) {
        val r = i / cols
        val c = i % cols
        val x = startX + c * (cellWPt + gapPt)
        val y = startY + r * (cellHPt + gapPt)
        canvas.drawBitmap(cell, null, android.graphics.RectF(x, y, x + cellWPt, y + cellHPt), paint)
        canvas.drawRect(x, y, x + cellWPt, y + cellHPt, guide)
    }
    doc.finishPage(page)
    val baos = java.io.ByteArrayOutputStream()
    doc.writeTo(baos)
    doc.close()
    val dims = "$count ${if (count == 1) "copy" else "copies"} ($cols × $rows) · ${PrintUtils.fmtDim(sheetWIn, "in")} × ${PrintUtils.fmtDim(sheetHIn, "in")} in PDF page"
    return PrintOutput(baos.toByteArray(), "$filename.pdf", "application/pdf", dims)
}

data class PrintMultiOutput(val files: List<File>, val name: String, val mime: String, val dims: String)

private fun renderSrcPdfPage(renderer: PdfRenderer, index: Int, maxDim: Int): Bitmap {
    val page = renderer.openPage(index)
    try {
        val scale = minOf(1f, maxDim.toFloat() / maxOf(page.width, page.height).coerceAtLeast(1))
        val w = (page.width * scale).toInt().coerceAtLeast(1)
        val h = (page.height * scale).toInt().coerceAtLeast(1)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(android.graphics.Color.WHITE)
        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bmp
    } finally {
        page.close()
    }
}

/** All-pages raster: every PDF page composed with the same settings, one encoded temp file per page. */
fun buildPrintRasterAllPages(pdfFile: File, indices: IntArray, wIn: Double, hIn: Double, bleedIn: Double, marginIn: Double, bg: Int, mode: String, ppi: Int, quality: Int, format: String, filename: String, outDir: File, onProgress: (Int, Int) -> Unit): PrintMultiOutput {
    val totalWIn = wIn + bleedIn * 2
    val totalHIn = hIn + bleedIn * 2
    val req = PrintUtils.requiredPixels(totalWIn, totalHIn, ppi)
    val capped = PrintUtils.cappedPixels(req[0], req[1])
    val wasCapped = capped[0] != req[0] || capped[1] != req[1]
    val marginPxX = (marginIn * capped[0] / totalWIn).toInt()
    val marginPxY = (marginIn * capped[1] / totalHIn).toInt()
    val areaW = (capped[0] - marginPxX * 2).coerceAtLeast(1)
    val areaH = (capped[1] - marginPxY * 2).coerceAtLeast(1)
    val ext = if (format == "PNG") "png" else "jpg"
    val files = mutableListOf<File>()
    ConvertFormats.withPdfRendererOnFile(pdfFile) { renderer ->
        if (renderer.pageCount == 0) throw IllegalStateException("No pages")
        require(indices.isNotEmpty() && indices.all { it >= 0 && it < renderer.pageCount }) { "Page out of range" }
        for ((k, i) in indices.withIndex()) {
            onProgress(k + 1, indices.size)
            val src = renderSrcPdfPage(renderer, i, 2600)
            try {
                val placed = PrintUtils.renderPrint(src, areaW, areaH, mode, if (format == "PNG") android.graphics.Color.TRANSPARENT else bg)
                val out = Bitmap.createBitmap(capped[0], capped[1], Bitmap.Config.ARGB_8888)
                val c = android.graphics.Canvas(out)
                c.drawColor(if (format == "PNG") android.graphics.Color.TRANSPARENT else bg)
                c.drawBitmap(placed, marginPxX.toFloat(), marginPxY.toFloat(), null)
                val f = File(outDir, "$filename-page-${(i + 1).toString().padStart(3, '0')}.$ext")
                FileOutputStream(f).use { fos ->
                    if (format == "PNG") {
                        out.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    } else {
                        val fl = ConvertFormats.flatten(out, bg)
                        fl.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), fos)
                        if (fl != out) fl.recycle()
                    }
                }
                files.add(f)
                placed.recycle()
                out.recycle()
            } finally {
                src.recycle()
            }
        }
    }
    if (files.isEmpty()) throw IllegalStateException("No pages")
    val dims = "${files.size} files · ${capped[0]} × ${capped[1]} px" + (if (wasCapped) " (scaled to fit device memory)" else "")
    return PrintMultiOutput(files, files.first().name, if (format == "PNG") "image/png" else "image/jpeg", dims)
}

/** All-pages PDF: one multi-page print-ready PDF, every page composed with the same settings. */
fun buildPrintPdfAllPages(pdfFile: File, indices: IntArray, wIn: Double, hIn: Double, bleedIn: Double, marginIn: Double, mode: String, ppi: Int, cropMarks: Boolean, filename: String, outFile: File, onProgress: (Int, Int) -> Unit): PrintMultiOutput {
    val pageWIn = wIn + bleedIn * 2
    val pageHIn = hIn + bleedIn * 2
    val pageWPt = (pageWIn * 72).toInt().coerceAtLeast(1)
    val pageHPt = (pageHIn * 72).toInt().coerceAtLeast(1)
    val areaWIn = (pageWIn - marginIn * 2).coerceAtLeast(0.1)
    val areaHIn = (pageHIn - marginIn * 2).coerceAtLeast(0.1)
    val req0 = PrintUtils.requiredPixels(areaWIn, areaHIn, ppi)
    val req = PrintUtils.cappedPixels(req0[0], req0[1])
    var count = 0
    val doc = PdfDocument()
    ConvertFormats.withPdfRendererOnFile(pdfFile) { renderer ->
        if (renderer.pageCount == 0) throw IllegalStateException("No pages")
        require(indices.isNotEmpty() && indices.all { it >= 0 && it < renderer.pageCount }) { "Page out of range" }
        for ((k, i) in indices.withIndex()) {
            onProgress(k + 1, indices.size)
            val src = renderSrcPdfPage(renderer, i, 2600)
            try {
                val placed = PrintUtils.renderPrint(src, req[0], req[1], mode, android.graphics.Color.WHITE)
                val page = doc.startPage(PdfDocument.PageInfo.Builder(pageWPt, pageHPt, i + 1).create())
                val canvas = page.canvas
                canvas.drawColor(android.graphics.Color.WHITE)
                val left = (marginIn * 72).toFloat()
                val top = (marginIn * 72).toFloat()
                canvas.drawBitmap(placed, null, android.graphics.RectF(left, top, pageWPt - left, pageHPt - top), android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG))
                if (cropMarks && bleedIn > 0) {
                    val paint = android.graphics.Paint().apply { color = android.graphics.Color.BLACK; strokeWidth = 0.7f }
                    val t = (bleedIn * 72).toFloat()
                    val len = 12f
                    val off = 4f
                    canvas.drawLine(t - off - len, t, t - off, t, paint)
                    canvas.drawLine(t, t - off - len, t, t - off, paint)
                    canvas.drawLine(pageWPt - t + off, t, pageWPt - t + off + len, t, paint)
                    canvas.drawLine(pageWPt - t, t - off - len, pageWPt - t, t - off, paint)
                    canvas.drawLine(t - off - len, pageHPt - t, t - off, pageHPt - t, paint)
                    canvas.drawLine(t, pageHPt - t + off, t, pageHPt - t + off + len, paint)
                    canvas.drawLine(pageWPt - t + off, pageHPt - t, pageWPt - t + off + len, pageHPt - t, paint)
                    canvas.drawLine(pageWPt - t, pageHPt - t + off, pageWPt - t, pageHPt - t + off + len, paint)
                }
                doc.finishPage(page)
                placed.recycle()
                count = indices.size
            } finally {
                src.recycle()
            }
        }
    }
    if (count == 0) { doc.close(); throw IllegalStateException("No pages") }
    FileOutputStream(outFile).use { doc.writeTo(it) }
    doc.close()
    val dims = "$count pages · ${PrintUtils.fmtDim(pageWIn, "in")} × ${PrintUtils.fmtDim(pageHIn, "in")} in PDF page"
    return PrintMultiOutput(listOf(outFile), outFile.name, "application/pdf", dims)
}

/** All-pages multi-copy sheets as raster files: one sheet image per PDF page. */
fun buildPrintSheetRasterAllPages(pdfFile: File, indices: IntArray, sheetWIn: Double, sheetHIn: Double, cellWIn: Double, cellHIn: Double, cols: Int, rows: Int, count: Int, gapIn: Double, ppi: Int, quality: Int, format: String, filename: String, outDir: File, onProgress: (Int, Int) -> Unit): PrintMultiOutput {
    val req = PrintUtils.requiredPixels(sheetWIn, sheetHIn, ppi)
    val capped = PrintUtils.cappedPixels(req[0], req[1])
    val wasCapped = capped[0] != req[0] || capped[1] != req[1]
    val cellWPx = (cellWIn * capped[0] / sheetWIn).toInt().coerceAtLeast(1)
    val cellHPx = (cellHIn * capped[1] / sheetHIn).toInt().coerceAtLeast(1)
    val gapPx = (gapIn * capped[0] / sheetWIn).toInt()
    val ext = if (format == "PNG") "png" else "jpg"
    val files = mutableListOf<File>()
    ConvertFormats.withPdfRendererOnFile(pdfFile) { renderer ->
        if (renderer.pageCount == 0) throw IllegalStateException("No pages")
        require(indices.isNotEmpty() && indices.all { it >= 0 && it < renderer.pageCount }) { "Page out of range" }
        for ((k, i) in indices.withIndex()) {
            onProgress(k + 1, indices.size)
            val src = renderSrcPdfPage(renderer, i, 2600)
            try {
                val sheet = PrintUtils.renderSheet(src, capped[0], capped[1], cellWPx, cellHPx, cols, rows, gapPx, true, count)
                val f = File(outDir, "$filename-page-${(i + 1).toString().padStart(3, '0')}.$ext")
                FileOutputStream(f).use { fos ->
                    if (format == "PNG") {
                        sheet.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    } else {
                        val fl = ConvertFormats.flatten(sheet, android.graphics.Color.WHITE)
                        fl.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), fos)
                        if (fl != sheet) fl.recycle()
                    }
                }
                files.add(f)
                sheet.recycle()
            } finally {
                src.recycle()
            }
        }
    }
    if (files.isEmpty()) throw IllegalStateException("No pages")
    val dims = "${files.size} files · $count ${if (count == 1) "copy" else "copies"} ($cols × $rows) each" + (if (wasCapped) " (scaled to fit device memory)" else "")
    return PrintMultiOutput(files, files.first().name, if (format == "PNG") "image/png" else "image/jpeg", dims)
}

/** All-pages multi-copy sheets in one PDF: one sheet page per PDF page. */
fun buildPrintSheetPdfAllPages(pdfFile: File, indices: IntArray, sheetWIn: Double, sheetHIn: Double, cellWIn: Double, cellHIn: Double, cols: Int, rows: Int, count: Int, gapIn: Double, ppi: Int, filename: String, outFile: File, onProgress: (Int, Int) -> Unit): PrintMultiOutput {
    val cellPx0 = PrintUtils.requiredPixels(cellWIn, cellHIn, ppi)
    val cellPx = PrintUtils.cappedPixels(cellPx0[0], cellPx0[1])
    val pageWPt = (sheetWIn * 72).toInt().coerceAtLeast(1)
    val pageHPt = (sheetHIn * 72).toInt().coerceAtLeast(1)
    var srcCount = 0
    val doc = PdfDocument()
    ConvertFormats.withPdfRendererOnFile(pdfFile) { renderer ->
        if (renderer.pageCount == 0) throw IllegalStateException("No pages")
        require(indices.isNotEmpty() && indices.all { it >= 0 && it < renderer.pageCount }) { "Page out of range" }
        for ((k, i) in indices.withIndex()) {
            onProgress(k + 1, indices.size)
            val src = renderSrcPdfPage(renderer, i, 2600)
            try {
                val cell = PrintUtils.renderPrint(src, cellPx[0], cellPx[1], "Fill", android.graphics.Color.WHITE)
                val page = doc.startPage(PdfDocument.PageInfo.Builder(pageWPt, pageHPt, i + 1).create())
                val canvas = page.canvas
                canvas.drawColor(android.graphics.Color.WHITE)
                val cellWPt = (cellWIn * 72).toFloat()
                val cellHPt = (cellHIn * 72).toFloat()
                val gapPt = (gapIn * 72).toFloat()
                val gridW = cols * cellWPt + (cols - 1) * gapPt
                val gridH = rows * cellHPt + (rows - 1) * gapPt
                val startX = (pageWPt - gridW) / 2f
                val startY = (pageHPt - gridH) / 2f
                val paint = android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG)
                val guide = android.graphics.Paint().apply { color = android.graphics.Color.LTGRAY; style = android.graphics.Paint.Style.STROKE; strokeWidth = 0.5f }
                for (k in 0 until count) {
                    val r = k / cols
                    val c = k % cols
                    val x = startX + c * (cellWPt + gapPt)
                    val y = startY + r * (cellHPt + gapPt)
                    canvas.drawBitmap(cell, null, android.graphics.RectF(x, y, x + cellWPt, y + cellHPt), paint)
                    canvas.drawRect(x, y, x + cellWPt, y + cellHPt, guide)
                }
                doc.finishPage(page)
                cell.recycle()
                srcCount = indices.size
            } finally {
                src.recycle()
            }
        }
    }
    if (srcCount == 0) { doc.close(); throw IllegalStateException("No pages") }
    FileOutputStream(outFile).use { doc.writeTo(it) }
    doc.close()
    val dims = "$srcCount pages · $count ${if (count == 1) "copy" else "copies"} ($cols × $rows) each · ${PrintUtils.fmtDim(sheetWIn, "in")} × ${PrintUtils.fmtDim(sheetHIn, "in")} in PDF page"
    return PrintMultiOutput(listOf(outFile), outFile.name, "application/pdf", dims)
}

fun queryDisplayName(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            val i = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (i >= 0 && c.moveToFirst()) c.getString(i) else null
        }
    } catch (_: Exception) { null }
}

/** Parses "3, 23, 67-70, 30" into sorted 0-based page indices; unusable tokens go to skipped. */
fun parsePageList(text: String, pageCount: Int): Pair<List<Int>, List<String>> {
    val ok = sortedSetOf<Int>()
    val skipped = mutableListOf<String>()
    text.split(',', ';', ' ').map { it.trim() }.filter { it.isNotEmpty() }.forEach { tok ->
        val dash = tok.split('-', '–')
        if (dash.size == 2 && dash[0].toIntOrNull() != null && dash[1].toIntOrNull() != null) {
            val a = dash[0].toInt()
            val b = dash[1].toInt()
            val lo = minOf(a, b)
            val hi = maxOf(a, b)
            if (lo < 1 || hi > pageCount) skipped.add(tok) else for (p in lo..hi) ok.add(p - 1)
        } else if (dash.size == 1 && tok.toIntOrNull() != null) {
            val p = tok.toInt()
            if (p < 1 || p > pageCount) skipped.add(tok) else ok.add(p - 1)
        } else {
            skipped.add(tok)
        }
    }
    return ok.toList() to skipped
}

/** "Pages 5, 8, 12-20, 24" from sorted 0-based page indices (runs compress to ranges). */
private fun formatPageRanges(zeroBasedSorted: List<Int>): String {
    if (zeroBasedSorted.isEmpty()) return ""
    val parts = mutableListOf<String>()
    var start = zeroBasedSorted[0]
    var prev = zeroBasedSorted[0]
    for (k in 1..zeroBasedSorted.size) {
        val cur = if (k < zeroBasedSorted.size) zeroBasedSorted[k] else -1
        if (cur != prev + 1) {
            parts.add(if (start == prev) "${start + 1}" else "${start + 1}-${prev + 1}")
            start = cur
        }
        prev = cur
    }
    return parts.joinToString(", ")
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


@Composable
private fun SocialHomeScreen(
    modifier: Modifier = Modifier,
    photo: Bitmap?,
    photoLabel: String?,
    onSelectImage: () -> Unit,
    onPlatform: (String) -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("Every platform. Every image size.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(4.dp))
        Text("Pick a platform, choose the official size, and export your photo ready to upload.", fontSize = 14.sp, color = Slate)
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                if (photo != null) {
                    Image(
                        bitmap = photo.asImageBitmap(),
                        contentDescription = "Selected photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("Select Your Image", fontWeight = FontWeight.SemiBold, color = DeepNavy)
                    Spacer(Modifier.height(2.dp))
                    Text(if (photo != null) "Current photo: ${photoLabel ?: ""}" else "No photo selected yet", fontSize = 13.sp, color = Slate)
                }
                Button(onClick = onSelectImage, colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Text(if (photo != null) "Change" else "Select Image")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        val names = SocialSizes.platforms.map { it.name }
        names.chunked(2).forEach { rowNames ->
            Row(Modifier.fillMaxWidth()) {
                rowNames.forEach { name ->
                    Card(
                        modifier = Modifier.weight(1f).padding(4.dp).clickable { onPlatform(name) },
                        colors = CardDefaults.cardColors(containerColor = SoftGrey),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).background(SkyBlue, CircleShape), contentAlignment = Alignment.Center) {
                                Text(name.take(1), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(name, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                        }
                    }
                }
                if (rowNames.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
            Text(
                "All sizes follow the official recommendations from each platform. The full sourced table is in the app documentation.",
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SocialPlatformScreen(
    modifier: Modifier = Modifier,
    platform: SocialPlatform?,
    customW: String,
    customH: String,
    onCustomW: (String) -> Unit,
    onCustomH: (String) -> Unit,
    onTarget: (SocialSize) -> Unit,
    onCustom: () -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        if (platform == null) {
            Text("Platform not found.", color = Slate)
        } else {
            platform.categories.forEach { cat ->
                Text(cat.label, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(cat.sub, fontSize = 13.sp, color = Slate)
                Spacer(Modifier.height(6.dp))
                cat.sizes.forEach { size ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onTarget(size) },
                        colors = CardDefaults.cardColors(containerColor = SoftGrey),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Text(size.label, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                            Text(size.sub, fontSize = 13.sp, color = Slate)
                            if (size.note.isNotEmpty()) Text(size.note, fontSize = 12.sp, color = Slate)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
        Text("Custom Size", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Any width and height, from 16 to 12000 px", fontSize = 13.sp, color = Slate)
        Spacer(Modifier.height(6.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = customW, onValueChange = onCustomW, label = { Text("Width (px)") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(10.dp))
                    OutlinedTextField(value = customH, onValueChange = onCustomH, label = { Text("Height (px)") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick = onCustom, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = DeepNavy), shape = RoundedCornerShape(12.dp)) {
                    Text("Use Custom Size", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SocialEditScreen(
    modifier: Modifier = Modifier,
    working: Bitmap?,
    target: SocialSize?,
    mode: String,
    rect: FloatArray,
    zoom: Float,
    panX: Float,
    panY: Float,
    fill: String,
    customColor: Long,
    busy: Boolean,
    onMode: (String) -> Unit,
    onRect: (FloatArray) -> Unit,
    onZoom: (Float) -> Unit,
    onPanDelta: (Float, Float) -> Unit,
    onFill: (String) -> Unit,
    onCustomColor: (Long) -> Unit,
    onSelectImage: () -> Unit,
    onExport: () -> Unit
) {
    var dragging by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState(), enabled = !dragging)) {
        Spacer(Modifier.height(10.dp))
        if (working == null || target == null) {
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select Image", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DeepNavy)
                    Spacer(Modifier.height(4.dp))
                    Text("Choose a photo from your gallery to start.", fontSize = 13.sp, color = Slate)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onSelectImage, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                        Text("Select Image")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            return@Column
        }
        val tw = target.w
        val th = target.h
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(16.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth().height(320.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                when (mode) {
                    "Position" -> {
                        val pScale = 420f / maxOf(tw, th)
                        val pw = (tw * pScale).toInt().coerceAtLeast(1)
                        val ph = (th * pScale).toInt().coerceAtLeast(1)
                        val preview = remember(working, tw, th, zoom, panX, panY) {
                            Transforms.socialPosition(working, pw, ph, zoom, panX, panY)
                        }
                        // screen px per target px: the finger maps 1:1 onto the photo at any zoom
                        val dispScale = minOf(constraints.maxWidth.toFloat() / tw, constraints.maxHeight.toFloat() / th)
                        val density = LocalDensity.current
                        val frameW = with(density) { (tw * dispScale).toDp() }
                        val frameH = with(density) { (th * dispScale).toDp() }
                        val base = maxOf(tw.toFloat() / working.width, th.toFloat() / working.height) * zoom
                        val maxOffX = (working.width * base - tw) / 2f
                        val maxOffY = (working.height * base - th) / 2f
                        Image(
                            bitmap = preview.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(frameW, frameH).pointerInput(working, tw, th, zoom) {
                                detectDragGestures(
                                    onDragStart = { dragging = true },
                                    onDragEnd = { dragging = false },
                                    onDragCancel = { dragging = false }
                                ) { change, dragAmount ->
                                    change.consume()
                                    // finger right = photo right = viewport left: pan decreases. Delta-based so every drag event counts.
                                    val dx = if (maxOffX > 0.5f) -dragAmount.x / (maxOffX * dispScale) else 0f
                                    val dy = if (maxOffY > 0.5f) -dragAmount.y / (maxOffY * dispScale) else 0f
                                    onPanDelta(dx, dy)
                                }
                            }
                        )
                    }
                    "Background" -> {
                        val pScale = 420f / maxOf(tw, th)
                        val pw = (tw * pScale).toInt().coerceAtLeast(1)
                        val ph = (th * pScale).toInt().coerceAtLeast(1)
                        val preview = remember(working, tw, th, fill, customColor) {
                            Transforms.socialFit(working, pw, ph, when (fill) {
                                "White" -> Transforms.SocialFill.WHITE
                                "Custom Color" -> Transforms.SocialFill.CUSTOM
                                "Transparent" -> Transforms.SocialFill.TRANSPARENT
                                else -> Transforms.SocialFill.BLUR
                            }, customColor.toInt())
                        }
                        Image(bitmap = preview.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                    }
                    else -> CropPreview(
                        bitmap = working,
                        rect = rect,
                        aspect = tw.toFloat() / th.toFloat(),
                        straighten = 0f,
                        tiltH = 0f,
                        tiltV = 0f,
                        onRect = onRect,
                        onDraggingChange = { dragging = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("$tw × $th px · ${target.label}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DeepNavy)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf("Crop", "Position", "Background").forEach { m ->
                val selected = mode == m
                OutlinedButton(
                    onClick = { onMode(m) },
                    modifier = Modifier.weight(1f).padding(horizontal = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) DeepNavy else Slate)
                ) { Text(m, fontSize = 12.sp, maxLines = 1, softWrap = false) }
            }
        }
        if (mode == "Position") {
            Spacer(Modifier.height(8.dp))
            Text("Zoom", fontSize = 13.sp, color = Slate)
            Slider(value = zoom, onValueChange = onZoom, valueRange = 1f..5f)
        }
        if (mode == "Background") {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                listOf("Blur", "White", "Custom Color", "Transparent").forEach { f ->
                    val selected = fill == f
                    OutlinedButton(
                        onClick = { onFill(f) },
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) DeepNavy else Slate)
                    ) { Text(f, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                }
            }
            if (fill == "Custom Color") {
                Spacer(Modifier.height(8.dp))
                Row {
                    listOf(0xFF42A5F5, 0xFF172B4D, 0xFFE91E63, 0xFF1E9E50, 0xFFFFC107, 0xFF9C27B0).forEach { c ->
                        Box(
                            Modifier.padding(end = 10.dp).size(34.dp).background(Color(c), CircleShape)
                                .then(if (customColor == c) Modifier.background(Color.Transparent, CircleShape) else Modifier)
                                .clickable { onCustomColor(c) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (customColor == c) Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp)) {
            Text(
                when (mode) {
                    "Position" -> "Drag your photo to position it. Use the slider to zoom in or out."
                    "Background" -> "Your whole photo fits inside the frame, with the space around it filled."
                    else -> "Drag the frame to choose the part of your photo to keep."
                },
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = onExport, enabled = !busy, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Text("Export Image", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(24.dp))
    }
}


@Composable
private fun ShopHomeScreen(
    modifier: Modifier = Modifier,
    photo: Bitmap?,
    photoLabel: String?,
    onSelectImage: () -> Unit,
    onShop: (String) -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("Every shop. Every product image.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(4.dp))
        Text("Pick your shop, choose the official size, and export your product photo ready to upload.", fontSize = 14.sp, color = Slate)
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                if (photo != null) {
                    Image(
                        bitmap = photo.asImageBitmap(),
                        contentDescription = "Selected photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("Select Your Image", fontWeight = FontWeight.SemiBold, color = DeepNavy)
                    Spacer(Modifier.height(2.dp))
                    Text(if (photo != null) "Current photo: ${photoLabel ?: ""}" else "Choose a product photo from your gallery", fontSize = 13.sp, color = Slate)
                }
                Button(onClick = onSelectImage, colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Text(if (photo != null) "Change" else "Select Image")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        val names = ShopSizes.mainShops + "Other Shop"
        names.chunked(2).forEach { rowNames ->
            Row(Modifier.fillMaxWidth()) {
                rowNames.forEach { name ->
                    Card(
                        modifier = Modifier.weight(1f).padding(4.dp).clickable { onShop(name) },
                        colors = CardDefaults.cardColors(containerColor = SoftGrey),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).background(SkyBlue, CircleShape), contentAlignment = Alignment.Center) {
                                Text(name.take(1), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(name, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                        }
                    }
                }
                if (rowNames.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
            Text(
                "All sizes follow the official requirements from each marketplace. The full sourced table is in the app documentation.",
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ShopOtherScreen(
    modifier: Modifier = Modifier,
    onShop: (String) -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("More marketplaces", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(4.dp))
        Text("Official sizes for these shops too.", fontSize = 14.sp, color = Slate)
        Spacer(Modifier.height(14.dp))
        ShopSizes.platforms.map { it.name }.filter { it !in ShopSizes.mainShops }.forEach { name ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onShop(name) },
                colors = CardDefaults.cardColors(containerColor = SoftGrey),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).background(SkyBlue, CircleShape), contentAlignment = Alignment.Center) {
                        Text(name.take(1), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(name, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 16.sp)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ShopPlatformScreen(
    modifier: Modifier = Modifier,
    platform: SocialPlatform?,
    customW: String,
    customH: String,
    onCustomW: (String) -> Unit,
    onCustomH: (String) -> Unit,
    onTarget: (SocialSize) -> Unit,
    onCollage: (SocialSize) -> Unit,
    onCustom: () -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        if (platform == null) {
            Text("Shop not found.", color = Slate)
        } else {
            platform.categories.forEach { cat ->
                Text(cat.label, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(cat.sub, fontSize = 13.sp, color = Slate)
                Spacer(Modifier.height(6.dp))
                cat.sizes.forEach { size ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                            if (cat.label == "Product Collage") onCollage(size) else onTarget(size)
                        },
                        colors = CardDefaults.cardColors(containerColor = SoftGrey),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Text(size.label, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                            Text(size.sub, fontSize = 13.sp, color = Slate)
                            if (size.note.isNotEmpty()) Text(size.note, fontSize = 12.sp, color = Slate)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
        Text("Custom Size", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Any width and height, from 16 to 12000 px", fontSize = 13.sp, color = Slate)
        Spacer(Modifier.height(6.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = customW, onValueChange = onCustomW, label = { Text("Width (px)") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(10.dp))
                    OutlinedTextField(value = customH, onValueChange = onCustomH, label = { Text("Height (px)") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick = onCustom, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = DeepNavy), shape = RoundedCornerShape(12.dp)) {
                    Text("Use Custom Size", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ShopEditScreen(
    modifier: Modifier = Modifier,
    working: Bitmap?,
    target: SocialSize?,
    mode: String,
    rect: FloatArray,
    zoom: Float,
    panX: Float,
    panY: Float,
    fill: String,
    customColor: Long,
    busy: Boolean,
    onMode: (String) -> Unit,
    onRect: (FloatArray) -> Unit,
    onZoom: (Float) -> Unit,
    onPanDelta: (Float, Float) -> Unit,
    onFill: (String) -> Unit,
    onCustomColor: (Long) -> Unit,
    onSelectImage: () -> Unit,
    onExport: () -> Unit,
    onBatch: () -> Unit
) {
    var dragging by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState(), enabled = !dragging)) {
        Spacer(Modifier.height(10.dp))
        if (working == null || target == null) {
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select Image", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DeepNavy)
                    Spacer(Modifier.height(4.dp))
                    Text("Choose a product photo from your gallery to start.", fontSize = 13.sp, color = Slate)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onSelectImage, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                        Text("Select Image")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            return@Column
        }
        val tw = target.w
        val th = target.h
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(16.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth().height(320.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                when (mode) {
                    "Position" -> {
                        val pScale = 420f / maxOf(tw, th)
                        val pw = (tw * pScale).toInt().coerceAtLeast(1)
                        val ph = (th * pScale).toInt().coerceAtLeast(1)
                        val preview = remember(working, tw, th, zoom, panX, panY) {
                            Transforms.socialPosition(working, pw, ph, zoom, panX, panY)
                        }
                        val dispScale = minOf(constraints.maxWidth.toFloat() / tw, constraints.maxHeight.toFloat() / th)
                        val density = LocalDensity.current
                        val frameW = with(density) { (tw * dispScale).toDp() }
                        val frameH = with(density) { (th * dispScale).toDp() }
                        val base = maxOf(tw.toFloat() / working.width, th.toFloat() / working.height) * zoom
                        val maxOffX = (working.width * base - tw) / 2f
                        val maxOffY = (working.height * base - th) / 2f
                        Image(
                            bitmap = preview.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(frameW, frameH).pointerInput(working, tw, th, zoom) {
                                detectDragGestures(
                                    onDragStart = { dragging = true },
                                    onDragEnd = { dragging = false },
                                    onDragCancel = { dragging = false }
                                ) { change, dragAmount ->
                                    change.consume()
                                    val dx = if (maxOffX > 0.5f) -dragAmount.x / (maxOffX * dispScale) else 0f
                                    val dy = if (maxOffY > 0.5f) -dragAmount.y / (maxOffY * dispScale) else 0f
                                    onPanDelta(dx, dy)
                                }
                            }
                        )
                    }
                    "Background" -> {
                        val pScale = 420f / maxOf(tw, th)
                        val pw = (tw * pScale).toInt().coerceAtLeast(1)
                        val ph = (th * pScale).toInt().coerceAtLeast(1)
                        val preview = remember(working, tw, th, fill, customColor) {
                            Transforms.socialFit(working, pw, ph, when (fill) {
                                "Blur" -> Transforms.SocialFill.BLUR
                                "Custom Color" -> Transforms.SocialFill.CUSTOM
                                "Transparent" -> Transforms.SocialFill.TRANSPARENT
                                else -> Transforms.SocialFill.WHITE
                            }, customColor.toInt())
                        }
                        Image(bitmap = preview.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                    }
                    else -> CropPreview(
                        bitmap = working,
                        rect = rect,
                        aspect = tw.toFloat() / th.toFloat(),
                        straighten = 0f,
                        tiltH = 0f,
                        tiltV = 0f,
                        onRect = onRect,
                        onDraggingChange = { dragging = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("$tw × $th px · ${target.label}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DeepNavy)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf("Crop", "Position", "Background").forEach { m ->
                val selected = mode == m
                OutlinedButton(
                    onClick = { onMode(m) },
                    modifier = Modifier.weight(1f).padding(horizontal = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) DeepNavy else Slate)
                ) { Text(m, fontSize = 12.sp, maxLines = 1, softWrap = false) }
            }
        }
        if (mode == "Position") {
            Spacer(Modifier.height(8.dp))
            Text("Zoom", fontSize = 13.sp, color = Slate)
            Slider(value = zoom, onValueChange = onZoom, valueRange = 1f..5f)
        }
        if (mode == "Background") {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                listOf("Blur", "White", "Custom Color", "Transparent").forEach { f ->
                    val selected = fill == f
                    OutlinedButton(
                        onClick = { onFill(f) },
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) DeepNavy else Slate)
                    ) { Text(f, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                }
            }
            if (fill == "Custom Color") {
                Spacer(Modifier.height(8.dp))
                Row {
                    listOf(0xFF42A5F5, 0xFF172B4D, 0xFFE91E63, 0xFF1E9E50, 0xFFFFC107, 0xFF9C27B0).forEach { c ->
                        Box(
                            Modifier.padding(end = 10.dp).size(34.dp).background(Color(c), CircleShape).clickable { onCustomColor(c) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (customColor == c) Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp)) {
            Text(
                when (mode) {
                    "Position" -> "Drag your photo to position it. Use the slider to zoom in or out."
                    "Background" -> "Your whole photo fits inside the frame, with the space around it filled."
                    else -> "Drag the frame to choose the part of your photo to keep."
                },
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = onExport, enabled = !busy, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Text("Export Image", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBatch, enabled = !busy, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Batch export more photos at this size", fontSize = 14.sp, maxLines = 1, softWrap = false)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ShopCollageScreen(
    modifier: Modifier = Modifier,
    target: SocialSize?,
    layout: Int,
    photos: List<Bitmap>,
    busy: Boolean,
    onLayout: (Int) -> Unit,
    onSelectPhotos: () -> Unit,
    onExport: () -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        if (target == null) {
            Text("No collage size selected.", color = Slate)
            Spacer(Modifier.height(24.dp))
            return@Column
        }
        val tw = target.w
        val th = target.h
        Text("$tw × $th px · Product Collage", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DeepNavy)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf(2, 3, 4).forEach { n ->
                val selected = layout == n
                OutlinedButton(
                    onClick = { onLayout(n) },
                    modifier = Modifier.weight(1f).padding(horizontal = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) DeepNavy else Slate)
                ) { Text("$n photos", fontSize = 13.sp, maxLines = 1, softWrap = false) }
            }
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(16.dp)) {
            Box(Modifier.fillMaxWidth().height(320.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                if (photos.size >= 2) {
                    val preview = remember(photos.toList(), layout, tw, th) {
                        val pScale = 480f / maxOf(tw, th)
                        val pw = (tw * pScale).toInt().coerceAtLeast(1)
                        val ph = (th * pScale).toInt().coerceAtLeast(1)
                        Transforms.collage(photos, pw, ph, maxOf(2, pw / 200), android.graphics.Color.WHITE)
                    }
                    Image(bitmap = preview.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                } else {
                    Text("Your collage preview shows here", color = Slate, fontSize = 14.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp)) {
            Text(
                "Pick $layout photos. Each one fills its own part of the layout - the grid arranges itself.",
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = onSelectPhotos, enabled = !busy, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = DeepNavy), shape = RoundedCornerShape(14.dp)) {
            Text(if (photos.isEmpty()) "Select Photos" else "Change Photos (${photos.size}/$layout)", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onExport, enabled = !busy && photos.size >= 2, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Text("Export Image", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ConvertHomeScreen(
    modifier: Modifier = Modifier,
    thumbs: List<Bitmap>,
    labels: List<String>,
    onSelectImages: () -> Unit,
    onSelectPdf: () -> Unit,
    onTarget: (ConvertFormats.Fmt) -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("Convert images into the format you need.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(4.dp))
        Text("Pick one photo or many, choose a format, and save the result to your phone.", fontSize = 14.sp, color = Slate)
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                if (thumbs.isNotEmpty()) {
                    Row(Modifier.fillMaxWidth().padding(bottom = 10.dp).horizontalScroll(rememberScrollState())) {
                        thumbs.forEachIndexed { i, t ->
                            Box(Modifier.padding(end = 6.dp)) {
                                Image(
                                    bitmap = t.asImageBitmap(),
                                    contentDescription = "Selected image ${i + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    labels.getOrNull(i) ?: "",
                                    fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                    modifier = Modifier.align(Alignment.BottomStart).background(DeepNavy).padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Select Your Image", fontWeight = FontWeight.SemiBold, color = DeepNavy)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            if (thumbs.isNotEmpty()) "${thumbs.size} image${if (thumbs.size == 1) "" else "s"} selected" else "Choose one or multiple images",
                            fontSize = 13.sp, color = Slate
                        )
                    }
                    Button(onClick = onSelectImages, colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Text(if (thumbs.isNotEmpty()) "Change" else "Select Image")
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onSelectPdf, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("Select PDF (pages become images)", color = DeepNavy, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Convert to", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 16.sp)
        Spacer(Modifier.height(6.dp))
        ConvertFormats.targets.chunked(2).forEach { rowFmts ->
            Row(Modifier.fillMaxWidth()) {
                rowFmts.forEach { fmt ->
                    Card(
                        modifier = Modifier.weight(1f).padding(4.dp).clickable { onTarget(fmt) },
                        colors = CardDefaults.cardColors(containerColor = SoftGrey),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Text(fmt.label, fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 16.sp)
                            Spacer(Modifier.height(2.dp))
                            Text(fmt.desc, fontSize = 12.sp, color = Slate)
                        }
                    }
                }
                if (rowFmts.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
            Text(
                "Everything happens on your phone - no upload, no internet. JPG, BMP and PDF have no transparency, so transparent areas are filled with the background you pick.",
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ConvertEditScreen(
    modifier: Modifier = Modifier,
    thumbs: List<Bitmap>,
    labels: List<String>,
    target: ConvertFormats.Fmt,
    quality: Int,
    onQuality: (Int) -> Unit,
    bg: String,
    onBg: (String) -> Unit,
    customColor: Long,
    onCustomColor: (Long) -> Unit,
    busy: Boolean,
    onConvert: () -> Unit,
    onTarget: (ConvertFormats.Fmt) -> Unit
) {
    val fromLabel = labels.map { it.substringBefore(" p.") }.distinct().let { if (it.size == 1) it.first() else "Mixed" }
    var showTargetPicker by remember { mutableStateOf(false) }
    if (showTargetPicker) {
        AlertDialog(
            onDismissRequest = { showTargetPicker = false },
            title = { Text("Convert to", color = DeepNavy, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    ConvertFormats.targets.forEach { fmt ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { onTarget(fmt); showTargetPicker = false }.padding(vertical = 10.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(fmt.label, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                                Text(fmt.desc, fontSize = 12.sp, color = Slate)
                            }
                            if (fmt.id == target.id) Icon(Icons.Default.CheckCircle, contentDescription = "Current", tint = SuccessGreen, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showTargetPicker = false }) { Text("Cancel", color = DeepNavy) } }
        )
    }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    thumbs.forEachIndexed { i, t ->
                        Box(Modifier.padding(end = 8.dp)) {
                            Image(
                                bitmap = t.asImageBitmap(),
                                contentDescription = "Selected image ${i + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(72.dp)
                            )
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = SuccessGreen,
                                modifier = Modifier.align(Alignment.TopEnd).size(20.dp)
                            )
                            Text(
                                labels.getOrNull(i) ?: "",
                                fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                modifier = Modifier.align(Alignment.BottomStart).background(DeepNavy).padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "${thumbs.size} image${if (thumbs.size == 1) "" else "s"} selected · Original format: $fromLabel",
                    fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("From", fontSize = 12.sp, color = Slate)
                    Text(fromLabel, fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 16.sp)
                }
            }
            Text("→", fontSize = 22.sp, color = DeepNavy, modifier = Modifier.padding(horizontal = 10.dp))
            Card(Modifier.weight(1f).clickable { showTargetPicker = true }, colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("To", fontSize = 12.sp, color = Slate)
                    Text(target.label, fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 16.sp)
                    Text("Tap to change", fontSize = 10.sp, color = SkyBlue)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        if (ConvertFormats.qualityApplies(target) || target.id == "jpg") {
            Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("Conversion options", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                    if (ConvertFormats.qualityApplies(target)) {
                        Spacer(Modifier.height(8.dp))
                        Text("Image quality: $quality%", fontSize = 13.sp, color = DeepNavy)
                        Slider(
                            value = quality.toFloat(),
                            onValueChange = { onQuality(it.toInt().coerceIn(10, 100)) },
                            valueRange = 10f..100f
                        )
                    }
                    if (target.id == "jpg") {
                        Spacer(Modifier.height(6.dp))
                        Text("Background for transparent areas", fontSize = 13.sp, color = DeepNavy)
                        Spacer(Modifier.height(6.dp))
                        Row {
                            listOf("White", "Custom Color").forEach { b ->
                                val selected = bg == b
                                OutlinedButton(
                                    onClick = { onBg(b) },
                                    modifier = Modifier.padding(end = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) DeepNavy else Slate)
                                ) { Text(b, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                            }
                        }
                        if (bg == "Custom Color") {
                            Spacer(Modifier.height(8.dp))
                            Row {
                                listOf(0xFF42A5F5, 0xFF172B4D, 0xFFE91E63, 0xFF1E9E50, 0xFFFFC107, 0xFF9C27B0).forEach { c ->
                                    Box(
                                        Modifier.padding(end = 10.dp).size(34.dp).background(Color(c), CircleShape)
                                            .clickable { onCustomColor(c) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (customColor == c) Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }
        Button(
            onClick = onConvert,
            enabled = !busy && thumbs.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(if (busy) "Converting…" else if (thumbs.size > 1) "Convert ${thumbs.size} Images" else "Convert Image", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp)) {
            Text(
                if (target.id == "pdf" && thumbs.size > 1) "All selected images become pages of one PDF document, saved to Downloads/PixelCraft."
                else if (thumbs.size > 1) "Every selected image is converted and saved to Pictures/PixelCraft."
                else "Your converted image keeps its original dimensions.",
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ConvertResultScreen(
    modifier: Modifier = Modifier,
    preview: Bitmap?,
    fromLabel: String,
    target: ConvertFormats.Fmt,
    origSize: Long,
    newSize: Long,
    saved: Boolean,
    batchCount: Int = 0,
    batchIsPdf: Boolean = false,
    onSave: () -> Unit,
    onConvertAnother: () -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (preview != null) {
                    Image(
                        bitmap = preview.asImageBitmap(),
                        contentDescription = "Converted image preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().height(240.dp)
                    )
                    if (target.id == "pdf") {
                        Spacer(Modifier.height(6.dp))
                        Text("Saved as a PDF document", fontSize = 12.sp, color = Slate)
                    }
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(72.dp))
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (batchCount > 1) "Converted $batchCount images to ${target.label}" else "Converted to ${target.label}", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(if (batchCount > 1) "Original: $batchCount images ($fromLabel)${if (origSize > 0) " · ${formatBytes(origSize)}" else ""}" else "Original: $fromLabel${if (origSize > 0) " · ${formatBytes(origSize)}" else ""}", fontSize = 14.sp, color = Slate)
                Spacer(Modifier.height(4.dp))
                Text(if (batchCount > 1 && !batchIsPdf) "Converted: $batchCount ${target.label} files${if (newSize > 0) " · ${formatBytes(newSize)}" else ""}" else "Converted: ${target.label}${if (newSize > 0) " · ${formatBytes(newSize)}" else ""}", fontSize = 14.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSave,
            enabled = !saved,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(if (saved) "Saved ✓" else if (target.id == "pdf") "Save PDF to Downloads" else if (batchCount > 1) "Save $batchCount Images to Phone" else "Save to Phone", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onConvertAnother, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Convert Another Image", color = DeepNavy)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CompressHomeScreen(
    modifier: Modifier = Modifier,
    thumbs: List<Bitmap>,
    labels: List<String>,
    onSelectImages: () -> Unit,
    onSelectPdf: () -> Unit,
    onMode: (String) -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("Reduce file size. Keep image quality.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (thumbs.isNotEmpty()) {
                    Row(Modifier.padding(bottom = 10.dp).horizontalScroll(rememberScrollState())) {
                        thumbs.forEachIndexed { i, t ->
                            Box(Modifier.padding(end = 6.dp)) {
                                Image(bitmap = t.asImageBitmap(), contentDescription = "Selected image ${i + 1}", contentScale = ContentScale.Crop, modifier = Modifier.size(64.dp))
                                Text(
                                    labels.getOrNull(i) ?: "",
                                    fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                    modifier = Modifier.align(Alignment.BottomStart).background(DeepNavy).padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(44.dp))
                    Spacer(Modifier.height(8.dp))
                }
                Text("Select Images", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = DeepNavy)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (thumbs.isNotEmpty()) "${thumbs.size} image${if (thumbs.size == 1) "" else "s"} selected" else "Choose one image or multiple images",
                    fontSize = 13.sp, color = Slate
                )
                Spacer(Modifier.height(2.dp))
                Text("JPG · PNG · WebP", fontSize = 12.sp, color = Slate)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onSelectImages, colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Text(if (thumbs.isNotEmpty()) "Change" else "Select Images")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onSelectPdf, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("Select PDF to compress", color = DeepNavy, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.weight(1f).padding(4.dp).clickable { onMode("Quality") },
                colors = CardDefaults.cardColors(containerColor = PaleBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Quality Compression", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("Adjust image quality", fontSize = 12.sp, color = Slate)
                }
            }
            Card(
                modifier = Modifier.weight(1f).padding(4.dp).clickable { onMode("Target") },
                colors = CardDefaults.cardColors(containerColor = PaleBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Target File Size", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("Aim for a chosen size", fontSize = 12.sp, color = Slate)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
            Text(
                "Everything happens on your phone. Compressed copies are saved as new files - your originals are never touched.",
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CompressSettingsScreen(
    modifier: Modifier = Modifier,
    thumbs: List<Bitmap>,
    labels: List<String>,
    names: List<String>,
    origSizes: List<Long>,
    isPdf: Boolean,
    mode: String,
    onMode: (String) -> Unit,
    quality: Int,
    onQuality: (Int) -> Unit,
    targetKb: String,
    onTargetKb: (String) -> Unit,
    resizeOn: Boolean,
    onResizeOn: (Boolean) -> Unit,
    resizePct: Int,
    onResizePct: (Int) -> Unit,
    busy: Boolean,
    onCompress: () -> Unit
) {
    val totalOrig = origSizes.filter { it > 0 }.sum()
    val title = if (thumbs.size > 1) "${thumbs.size} images selected" else names.firstOrNull() ?: "image"
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                if (totalOrig > 0) Text("Original size: ${formatBytes(totalOrig)}", fontSize = 13.sp, color = Slate)
                if (thumbs.isNotEmpty() && thumbs.size <= 1) {
                    Spacer(Modifier.height(10.dp))
                    Box {
                        Image(bitmap = thumbs[0].asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(52.dp))
                        Text(
                            labels.getOrNull(0) ?: "",
                            fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White,
                            modifier = Modifier.align(Alignment.BottomStart).background(DeepNavy).padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                if (thumbs.size > 1) {
                    Spacer(Modifier.height(10.dp))
                    names.forEachIndexed { i, name ->
                        val sz = origSizes.getOrNull(i) ?: -1L
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                Image(bitmap = thumbs[i].asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(40.dp))
                                Text(
                                    labels.getOrNull(i) ?: "",
                                    fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                    modifier = Modifier.align(Alignment.BottomStart).background(DeepNavy).padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(name, fontSize = 12.sp, color = DeepNavy, modifier = Modifier.weight(1f), maxLines = 1)
                            Text(if (sz > 0) formatBytes(sz) else "-", fontSize = 12.sp, color = Slate)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Compression mode", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf("Quality" to "Choose compression level", "Target" to "Set desired file size").forEach { (m, desc) ->
                val selected = mode == m
                Card(
                    modifier = Modifier.weight(1f).padding(4.dp).clickable { onMode(m) },
                    colors = CardDefaults.cardColors(containerColor = if (selected) SkyBlue else SoftGrey),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(if (m == "Target") "Target Size" else m, fontWeight = FontWeight.Bold, color = if (selected) Color.White else DeepNavy, fontSize = 15.sp)
                        Text(desc, fontSize = 11.sp, color = if (selected) Color.White else Slate)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (mode == "Quality") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Image quality", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text("$quality%", fontWeight = FontWeight.Bold, color = SkyBlue, fontSize = 16.sp)
            }
            Slider(value = quality.toFloat(), onValueChange = { onQuality(it.toInt().coerceIn(1, 100)) }, valueRange = 1f..100f)
            Row(Modifier.fillMaxWidth()) {
                Text("Smaller file", fontSize = 12.sp, color = Slate, modifier = Modifier.weight(1f))
                Text("Higher quality", fontSize = 12.sp, color = Slate)
            }
        } else {
            Text("Target file size", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = targetKb,
                onValueChange = { onTargetKb(it.filter { c -> c.isDigit() }.take(5)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Size in KB (e.g. 500)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(Modifier.height(4.dp))
            Text("The compressor gets as close as it can - very small targets may lower dimensions too.", fontSize = 12.sp, color = Slate)
        }
        if (!isPdf) {
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Optional resize", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                    Text("Resize image dimensions", fontSize = 12.sp, color = Slate)
                }
                Switch(checked = resizeOn, onCheckedChange = { onResizeOn(it) }, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
            }
            if (resizeOn) {
                Spacer(Modifier.height(6.dp))
                Row {
                    listOf(90, 75, 50, 25).forEach { p ->
                        val selected = resizePct == p
                        OutlinedButton(
                            onClick = { onResizePct(p) },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) DeepNavy else Slate)
                        ) { Text("$p%", fontSize = 12.sp) }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCompress,
            enabled = !busy && thumbs.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(if (busy) "Compressing…" else if (isPdf) "Compress PDF" else if (thumbs.size > 1) "Compress ${thumbs.size} Images" else "Compress Image", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
            Text(
                when {
                    isPdf -> "PDF pages are re-rendered at a lower resolution to shrink the file. Already well-compressed PDFs may shrink only a little."
                    labels.distinct() == listOf("PNG") -> "PNG is lossless - use Optional resize or Target mode to shrink PNG files. Transparency is always kept."
                    else -> "JPG and WebP use your quality setting. Transparency is preserved for PNG and WebP. Originals stay untouched."
                },
                modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CompressResultScreen(
    modifier: Modifier = Modifier,
    isPdf: Boolean,
    batchFiles: List<Pair<String, ByteArray>>?,
    batchOrig: List<Long>?,
    origSize: Long,
    newBytes: ByteArray?,
    resultName: String,
    pdfPages: Int,
    before: Bitmap?,
    after: Bitmap?,
    saved: Boolean,
    onSave: () -> Unit,
    onAgain: () -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        if (batchFiles != null) {
            val totalNew = batchFiles.sumOf { it.second.size.toLong() }
            val totalOrig = (batchOrig ?: emptyList()).filter { it > 0 }.sum()
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Compressed ${batchFiles.size} images", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
                    }
                    if (totalOrig > 0) {
                        Spacer(Modifier.height(6.dp))
                        Text("${formatBytes(totalOrig)} → ${formatBytes(totalNew)} · ${CompressUtils.percentSaved(totalOrig, totalNew)}% smaller", fontSize = 14.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(10.dp))
                    batchFiles.forEachIndexed { i, (name, bytes) ->
                        val o = batchOrig?.getOrNull(i) ?: -1L
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(name, fontSize = 12.sp, color = Slate, modifier = Modifier.weight(1f), maxLines = 1)
                            Text(
                                if (o > 0) "${formatBytes(o)} → ${formatBytes(bytes.size.toLong())} (-${CompressUtils.percentSaved(o, bytes.size.toLong())}%)"
                                else formatBytes(bytes.size.toLong()),
                                fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        } else {
            val newSize = newBytes?.size?.toLong() ?: -1L
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Compression Results", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        resultName + if (isPdf && pdfPages > 0) " · $pdfPages page${if (pdfPages == 1) "" else "s"}" else "",
                        fontSize = 13.sp, color = Slate, maxLines = 1
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Card(Modifier.weight(1f).padding(4.dp), colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Original", fontSize = 12.sp, color = Slate)
                                Text(if (origSize > 0) formatBytes(origSize) else "-", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
                            }
                        }
                        Card(Modifier.weight(1f).padding(4.dp), colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Compressed", fontSize = 12.sp, color = Slate)
                                Text(if (newSize > 0) formatBytes(newSize) else "-", fontWeight = FontWeight.Bold, color = SkyBlue, fontSize = 17.sp)
                            }
                        }
                    }
                    if (origSize > 0 && newSize > 0) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("${CompressUtils.percentSaved(origSize, newSize)}% smaller", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                        }
                        if (isPdf && CompressUtils.percentSaved(origSize, newSize) < 5) {
                            Spacer(Modifier.height(4.dp))
                            Text("This PDF was already well-compressed, so savings are small.", fontSize = 12.sp, color = Slate)
                        }
                    }
                }
            }
            if (before != null && after != null) {
                Spacer(Modifier.height(14.dp))
                Text("Compare image quality", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                Spacer(Modifier.height(6.dp))
                var showAfter by remember { mutableStateOf(true) }
                Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            bitmap = (if (showAfter) after else before).asImageBitmap(),
                            contentDescription = if (showAfter) "Compressed preview" else "Original preview",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth().height(220.dp).clickable { showAfter = !showAfter }
                        )
                        Spacer(Modifier.height(6.dp))
                        Row {
                            listOf(false to "Before", true to "After").forEach { (a, label) ->
                                val selected = showAfter == a
                                OutlinedButton(
                                    onClick = { showAfter = a },
                                    modifier = Modifier.padding(end = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) SkyBlue else Color.Transparent, contentColor = if (selected) Color.White else DeepNavy)
                                ) { Text(label, fontSize = 12.sp) }
                            }
                        }
                        Text("Tap the image to toggle", fontSize = 11.sp, color = Slate)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSave,
            enabled = !saved,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                if (saved) "Saved ✓"
                else if (isPdf) "Save Compressed PDF"
                else if (batchFiles != null) "Save ${batchFiles.size} Compressed Images"
                else "Save Compressed Image",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onAgain, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Compress Another", color = DeepNavy)
        }
        Spacer(Modifier.height(24.dp))
    }
}


// ---------------- Print Size & DPI screens (v0.15.0) ----------------

@Composable
private fun PrintHomeScreen(
    modifier: Modifier = Modifier,
    thumb: Bitmap?,
    name: String,
    label: String,
    sizeBytes: Long,
    imgW: Int,
    imgH: Int,
    onPick: () -> Unit,
    onPickPdf: () -> Unit,
    pdfPageCount: Int,
    pdfThumbs: Map<Int, Bitmap>,
    pdfSelected: Set<Int>,
    onPdfPagesAdd: (List<Int>) -> Unit,
    onPdfThumbNeeded: (Int) -> Unit,
    onPdfAllPages: () -> Unit,
    onPdfPage: (Int) -> Unit,
    onPdfPages: (List<Int>) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("Prepare images for printing at the right size and resolution.", fontSize = 15.sp, color = Slate)
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (thumb == null) {
                    Text("Select Your Image", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Choose a photo, artwork, document image or design from your phone.", fontSize = 13.sp, color = Slate, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = onPick, colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)) { Text("Choose Image", color = Color.White) }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onPickPdf) { Text("Select PDF", color = DeepNavy) }
                } else {
                    Image(bitmap = thumb.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(120.dp))
                    Spacer(Modifier.height(10.dp))
                    Text(name, fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp, maxLines = 1)
                    Text(
                        "$imgW × $imgH px" + (if (label.isNotBlank()) " · $label" else "") + (if (sizeBytes > 0) " · ${formatBytes(sizeBytes)}" else ""),
                        fontSize = 13.sp, color = Slate
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onPick) { Text("Change", color = DeepNavy) }
                        OutlinedButton(onClick = onPickPdf) { Text("Select PDF", color = DeepNavy) }
                    }
                }
            }
        }
        if (pdfPageCount > 0) {
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("Choose a page ($pdfPageCount pages)", fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    var pickAll by remember(pdfPageCount) { mutableStateOf(false) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (pickAll) {
                            OutlinedButton(onClick = { pickAll = false }, modifier = Modifier.weight(1f)) { Text("Selected page", color = DeepNavy) }
                            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = SkyBlue), modifier = Modifier.weight(1f)) { Text("All pages", color = Color.White) }
                        } else {
                            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = SkyBlue), modifier = Modifier.weight(1f)) { Text("Selected page", color = Color.White) }
                            OutlinedButton(onClick = { pickAll = true }, modifier = Modifier.weight(1f)) { Text("All pages", color = DeepNavy) }
                        }
                    }
                    if (pickAll) {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onPdfAllPages, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue), shape = RoundedCornerShape(12.dp)) { Text("Use all $pdfPageCount pages", color = Color.White, fontWeight = FontWeight.Bold) }
                    } else {
                        Spacer(Modifier.height(8.dp))
                        var showAllPages by remember { mutableStateOf(false) }
                        if (pdfSelected.isEmpty()) {
                            Text("Tap pages below to select them - selected pages get a ✅.", fontSize = 12.sp, color = Slate)
                        } else {
                            Text(
                                "${pdfSelected.size} page${if (pdfSelected.size == 1) "" else "s"} selected: ${formatPageRanges(pdfSelected.sorted())}",
                                fontSize = 15.sp, color = DeepNavy, fontWeight = FontWeight.Bold,
                                maxLines = if (showAllPages) 8 else 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth().background(PaleBlue, RoundedCornerShape(8.dp)).clickable { showAllPages = !showAllPages }.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Select pages", color = DeepNavy, fontSize = 15.sp)
                        Spacer(Modifier.height(2.dp))
                        var pageText by remember(pdfPageCount) { mutableStateOf("") }
                        var pageTextNote by remember { mutableStateOf("") }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextField(
                                value = pageText,
                                onValueChange = { pageText = it; pageTextNote = "" },
                                placeholder = { Text("(e.g., 4, 9, 47, 83, 126, 229, …)", fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, softWrap = false) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = SkyBlue,
                                    unfocusedIndicatorColor = Slate
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(onClick = {
                                val (pages, skipped) = parsePageList(pageText, pdfPageCount)
                                when {
                                    pages.isEmpty() && skipped.isEmpty() -> pageTextNote = "Type page numbers first, like 3, 23, 67-70."
                                    skipped.isEmpty() -> { onPdfPagesAdd(pages); pageText = ""; pageTextNote = "" }
                                    pages.isEmpty() -> pageTextNote = "Could not use: ${skipped.joinToString(", ")} - this PDF has $pdfPageCount pages."
                                    else -> { onPdfPagesAdd(pages); pageText = ""; pageTextNote = "Added. Skipped: ${skipped.joinToString(", ")} - this PDF has $pdfPageCount pages." }
                                }
                            }) { Text("Add", color = DeepNavy) }
                        }
                        if (pageTextNote.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(pageTextNote, fontSize = 12.sp, color = if (pageTextNote.startsWith("Added")) Slate else MaterialTheme.colorScheme.error)
                        }
                        Spacer(Modifier.height(6.dp))
                        if (pdfSelected.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { onPdfPages(pdfSelected.sorted()) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue), shape = RoundedCornerShape(12.dp)) {
                                Text(if (pdfSelected.size == 1) "Use page ${pdfSelected.first() + 1}" else "Use ${pdfSelected.size} selected pages", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(pdfPageCount) { i ->
                            val t = pdfThumbs[i]
                            val isSel = !pickAll && pdfSelected.contains(i)
                            LaunchedEffect(i) { if (t == null) onPdfThumbNeeded(i) }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { if (!pickAll) onPdfPage(i) }) {
                                Box {
                                    if (t != null) {
                                        Image(bitmap = t.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.height(120.dp).border(if (isSel) 3.dp else 1.dp, if (isSel) SkyBlue else Slate))
                                    } else {
                                        Box(Modifier.width(88.dp).height(120.dp).border(if (isSel) 3.dp else 1.dp, if (isSel) SkyBlue else Slate), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = SkyBlue)
                                        }
                                    }
                                    if (isSel) {
                                        Text("✅", fontSize = 22.sp, modifier = Modifier.align(Alignment.TopEnd).padding(2.dp))
                                    }
                                }
                                Text("Page ${i + 1}", fontSize = 12.sp, color = if (isSel) SkyBlue else DeepNavy, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onContinue,
            enabled = thumb != null,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Set Up Print Size", color = Color.White, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
            Text(
                "Your original image remains unchanged. Print files are always saved as new files.",
                fontSize = 13.sp, color = Slate, modifier = Modifier.padding(14.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PrintPresetChip(name: String, desc: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (selected) PaleBlue else SoftGrey),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .border(if (selected) 2.dp else 0.dp, if (selected) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(name, fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 14.sp)
            Text(desc, fontSize = 11.sp, color = Slate)
        }
    }
}

@Composable
private fun PrintSetupScreen(
    modifier: Modifier = Modifier,
    preset: String,
    onPreset: (String) -> Unit,
    unit: String,
    onUnit: (String) -> Unit,
    wText: String,
    onWText: (String) -> Unit,
    hText: String,
    onHText: (String) -> Unit,
    landscape: Boolean,
    onLandscape: (Boolean) -> Unit,
    lockAspect: Boolean,
    onLockAspect: (Boolean) -> Unit,
    bleedOn: Boolean,
    onBleedOn: (Boolean) -> Unit,
    ppi: Int,
    onPpi: (Int) -> Unit,
    fitMode: String,
    onFitMode: (String) -> Unit,
    imgW: Int,
    imgH: Int,
    wIn: Double,
    hIn: Double,
    aspect: Double,
    onResetProportions: () -> Unit,
    multiCopy: Boolean,
    sheetOn: Boolean,
    onSheetOn: (Boolean) -> Unit,
    sheet: String,
    onSheet: (String) -> Unit,
    copyCount: Int,
    onCopyCount: (Int) -> Unit,
    maxCount: Int,
    gridCount: Int,
    onContinue: () -> Unit
) {
    val effPpi = PrintUtils.effectivePpi(imgW, imgH, wIn, hIn)
    val req = if (wIn > 0 && hIn > 0) PrintUtils.requiredPixels(wIn, hIn, ppi) else intArrayOf(0, 0)
    val quality = PrintUtils.qualityLevel(effPpi, ppi)
    val ratioMismatch = wIn > 0 && hIn > 0 && aspect > 0 && kotlin.math.abs((wIn / hIn) - aspect) / aspect > 0.02

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("Choose the print size", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
        Spacer(Modifier.height(10.dp))
        listOf("Paper sizes", "Paper sizes (US)", "Photo sizes", "ID / Passport", "Square", "Posters").forEach { group ->
            Text(group, fontWeight = FontWeight.SemiBold, color = Slate, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                PrintUtils.presets.filter { it.group == group }.forEach { p ->
                    PrintPresetChip(p.name, p.desc, preset == p.name) { onPreset(p.name) }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(6.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("Custom size", fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        wText, onWText,
                        modifier = Modifier.weight(1f),
                        label = { Text("Width ($unit)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        hText, onHText,
                        modifier = Modifier.weight(1f),
                        label = { Text("Height ($unit)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("in", "cm", "mm", "px").forEach { u ->
                        Box(Modifier.weight(1f)) {
                            PrintPresetChip(u, if (u == "in") "inches" else if (u == "cm") "centimeters" else if (u == "mm") "millimeters" else "pixels", unit == u) { onUnit(u) }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) { PrintPresetChip("Portrait", "taller than wide", !landscape) { onLandscape(false) } }
                    Box(Modifier.weight(1f)) { PrintPresetChip("Landscape", "wider than tall", landscape) { onLandscape(true) } }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Keep aspect ratio", fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 14.sp)
                        Text("Changing one side calculates the other.", fontSize = 12.sp, color = Slate)
                    }
                    Switch(checked = lockAspect, onCheckedChange = onLockAspect, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Bleed allowance", fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 14.sp)
                        Text("Adds 3 mm on each side for professional printing.", fontSize = 12.sp, color = Slate)
                    }
                    Switch(checked = bleedOn, onCheckedChange = onBleedOn, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
                }
                if (unit == "px") {
                    Text("Pixel size converts through the PPI chosen below: inches = pixels / PPI.", fontSize = 12.sp, color = Slate, modifier = Modifier.padding(top = 4.dp))
                }
                TextButton(onClick = onResetProportions, enabled = aspect > 0) { Text("Reset to original proportions", color = SkyBlue) }
            }
        }
        if (multiCopy && maxCount > 1) {
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Multiple copies on one sheet", fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 15.sp)
                            Text("Neat grid with cutting guides.", fontSize = 12.sp, color = Slate)
                        }
                        Switch(checked = sheetOn, onCheckedChange = onSheetOn, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
                    }
                    if (sheetOn) {
                        Spacer(Modifier.height(6.dp))
                        Text("Sheet", fontSize = 12.sp, color = Slate)
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            listOf("A4", "4 × 6", "5 × 7").forEach { sh ->
                                PrintPresetChip(sh, "sheet", sheet == sh) { onSheet(sh) }
                            }
                        }
                        Text("Copies", fontSize = 12.sp, color = Slate)
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            listOf(4, 6, 8).filter { it <= maxCount }.forEach { n ->
                                PrintPresetChip("$n", "copies", copyCount == n) { onCopyCount(n) }
                            }
                            PrintPresetChip("Max", "$maxCount fit", copyCount <= 0 || copyCount > maxCount) { onCopyCount(0) }
                        }
                        if (gridCount > 0) {
                            Text("$gridCount ${if (gridCount == 1) "copy" else "copies"} on $sheet", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("That count does not fit on $sheet - try fewer copies or A4.", fontSize = 13.sp, color = Color(0xFFC62828))
                        }
                    }
                }
            }
        }
        if (ratioMismatch) {
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("The print shape differs from your image.", fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        PrintPresetChip("Fit", "whole image, white edges", fitMode == "Fit") { onFitMode("Fit") }
                        PrintPresetChip("Fill & Crop", "fills page, crops edges", fitMode == "Fill") { onFitMode("Fill") }
                        PrintPresetChip("Stretch", "fills exactly", fitMode == "Stretch") { onFitMode("Stretch") }
                    }
                    if (fitMode == "Stretch") {
                        Text("Stretch can distort the image.", fontSize = 13.sp, color = Color(0xFFC62828))
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Print Resolution (PPI)", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
        Text("Often called DPI. Higher means more detail in print.", fontSize = 13.sp, color = Slate)
        Spacer(Modifier.height(8.dp))
        listOf(
            Triple(150, "Large prints", "May be appropriate for larger prints viewed from a distance."),
            Triple(200, "General print", "A useful option for many everyday print jobs."),
            Triple(300, "Recommended", "A common target for high-quality photographs and detailed prints."),
            Triple(600, "Fine detail", "For workflows that specifically benefit from higher resolution.")
        ).forEach { (opt, badge, desc) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = if (ppi == opt) PaleBlue else SoftGrey),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(if (ppi == opt) 2.dp else 0.dp, if (ppi == opt) SkyBlue else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { onPpi(opt) }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$opt PPI", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp, modifier = Modifier.weight(1f))
                        Text(
                            badge, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            color = if (badge == "Recommended") SuccessGreen else Slate,
                            modifier = Modifier.background(Color.White, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(desc, fontSize = 13.sp, color = Slate)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("Print calculator", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("Print size", Modifier.weight(1f), fontSize = 13.sp, color = Slate)
                    Text(
                        if (unit == "px") "${PrintUtils.fmtDim(wIn, "in")} × ${PrintUtils.fmtDim(hIn, "in")} in (${PrintUtils.fmtDim(wIn, "cm")} × ${PrintUtils.fmtDim(hIn, "cm")} cm)"
                        else "${PrintUtils.fmtDim(wIn, unit)} × ${PrintUtils.fmtDim(hIn, unit)} $unit",
                        fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold
                    )
                }
                Row { Text("Image pixels", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("$imgW × $imgH px", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                Row { Text("Effective PPI", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("${effPpi.toInt()} PPI", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                Row { Text("Required pixels", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("${req[0]} × ${req[1]} px (${PrintUtils.megapixels(req[0], req[1])} MP)", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                Spacer(Modifier.height(6.dp))
                Text("This is the target calculation, not a claim that the selected image already has sufficient resolution.", fontSize = 11.sp, color = Slate)
            }
        }
        Spacer(Modifier.height(10.dp))
        val (qColor, qTitle, qText) = when (quality) {
            PrintUtils.Quality.GOOD -> Triple(SuccessGreen, "✓ Good resolution", "The image meets or exceeds the chosen target PPI at the requested size.")
            PrintUtils.Quality.ACCEPTABLE -> Triple(Orange, "⚠ Acceptable with limitations", "The image may print adequately, depending on the subject, print method and viewing distance.")
            PrintUtils.Quality.LOW -> Triple(Color(0xFFC62828), "⚠ Low resolution", "The image may appear soft or pixelated at the selected print size. Consider a smaller print or a higher-resolution original.")
        }
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text(qTitle, fontWeight = FontWeight.Bold, color = qColor, fontSize = 15.sp)
                Text(qText, fontSize = 13.sp, color = Slate)
            }
        }
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onContinue,
            enabled = wIn > 0 && hIn > 0,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Continue to Print Preview", color = Color.White, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PrintPreviewScreen(
    modifier: Modifier = Modifier,
    preview: Bitmap?,
    wIn: Double,
    hIn: Double,
    preset: String,
    landscape: Boolean,
    ppi: Int,
    fitMode: String,
    onFitMode: (String) -> Unit,
    marginsOn: Boolean,
    onMarginsOn: (Boolean) -> Unit,
    bleedOn: Boolean,
    onBleedOn: (Boolean) -> Unit,
    cropMarks: Boolean,
    onCropMarks: (Boolean) -> Unit,
    bgWhite: Boolean,
    onBgWhite: (Boolean) -> Unit,
    sheetMode: Boolean,
    sheet: String,
    grid: IntArray,
    sheetWIn: Double,
    sheetHIn: Double,
    allPagesNote: String?,
    onContinue: () -> Unit
) {
    val bleedIn = if (bleedOn && !sheetMode) PrintUtils.BLEED_IN else 0.0
    val marginIn = if (marginsOn && !sheetMode) PrintUtils.MARGIN_IN else 0.0
    val pageWIn = if (sheetMode) sheetWIn else wIn + bleedIn * 2
    val pageHIn = if (sheetMode) sheetHIn else hIn + bleedIn * 2
    val bg = if (bgWhite) Color.White else Color(0xFFF2F2F2)

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text(
            if (sheetMode) "$sheet sheet · ${grid[2]} ${if (grid[2] == 1) "copy" else "copies"} · $ppi PPI"
            else "$preset · ${if (landscape) "Landscape" else "Portrait"} · $ppi PPI",
            fontSize = 14.sp, color = Slate
        )
        if (allPagesNote != null) {
            Spacer(Modifier.height(6.dp))
            Text(allPagesNote, fontSize = 13.sp, color = SkyBlue, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth().height(340.dp).padding(18.dp)) {
                val pageRatio = (pageWIn / pageHIn).toFloat()
                var pw = maxWidth
                var ph = maxWidth / pageRatio
                if (ph > maxHeight) { ph = maxHeight; pw = maxHeight * pageRatio }
                Box(Modifier.size(pw, ph).align(Alignment.Center)) {
                    if (bleedIn > 0) {
                        Box(Modifier.fillMaxSize().background(Color(0xFFFFE9E9)))
                        Spacer(Modifier.fillMaxSize())
                    }
                    val bleedPadX = if (bleedIn > 0) pw * (bleedIn / pageWIn).toFloat() else 0.dp
                    val bleedPadY = if (bleedIn > 0) ph * (bleedIn / pageHIn).toFloat() else 0.dp
                    Box(Modifier.fillMaxSize().padding(horizontal = bleedPadX, vertical = bleedPadY).background(Color.White).border(1.dp, DeepNavy)) {
                        if (sheetMode && preview != null && grid[2] > 0) {
                            val sheetPreview = remember(preview, grid[0], grid[1], grid[2], grid[3]) {
                                PrintUtils.renderSheet(
                                    preview, 900, (900 * pageHIn / pageWIn).toInt(),
                                    (900 * (wIn / pageWIn)).toInt().coerceAtLeast(1),
                                    (900 * (hIn / pageWIn)).toInt().coerceAtLeast(1),
                                    grid[0], grid[1],
                                    (900 * (if (grid[3] == 1) PrintUtils.SHEET_GAP_IN else 0.0) / pageWIn).toInt(),
                                    true, grid[2]
                                )
                            }
                            Image(bitmap = sheetPreview.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                        } else if (preview != null) {
                            val marginPadX = pw * (marginIn / pageWIn).toFloat()
                            val marginPadY = ph * (marginIn / pageHIn).toFloat()
                            Box(Modifier.fillMaxSize().padding(horizontal = marginPadX, vertical = marginPadY).background(bg)) {
                                Image(
                                    bitmap = preview.asImageBitmap(), contentDescription = null,
                                    contentScale = when (fitMode) { "Fill" -> ContentScale.Crop; "Stretch" -> ContentScale.FillBounds; else -> ContentScale.Fit },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
        if (bleedIn > 0) Text("Pink band = bleed area (trimmed after printing).", fontSize = 11.sp, color = Slate, modifier = Modifier.padding(top = 4.dp))
        if (sheetMode) Text("Thin grey lines are cutting guides.", fontSize = 11.sp, color = Slate, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        if (!sheetMode) {
            Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("Placement", fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        PrintPresetChip("Fit", "whole image", fitMode == "Fit") { onFitMode("Fit") }
                        PrintPresetChip("Fill & Crop", "fill page", fitMode == "Fill") { onFitMode("Fill") }
                        PrintPresetChip("Stretch", "exact fill", fitMode == "Stretch") { onFitMode("Stretch") }
                    }
                    if (fitMode == "Stretch") Text("Stretch can distort the image.", fontSize = 13.sp, color = Color(0xFFC62828))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("White margins (0.25 in)", Modifier.weight(1f), fontSize = 14.sp, color = DeepNavy)
                        Switch(checked = marginsOn, onCheckedChange = onMarginsOn, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Bleed (3 mm)", Modifier.weight(1f), fontSize = 14.sp, color = DeepNavy)
                        Switch(checked = bleedOn, onCheckedChange = onBleedOn, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Crop marks (PDF export)", Modifier.weight(1f), fontSize = 14.sp, color = DeepNavy)
                        Switch(checked = cropMarks, onCheckedChange = onCropMarks, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
                    }
                    Text("Background for transparent images", fontSize = 13.sp, color = Slate)
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        PrintPresetChip("White", "default", bgWhite) { onBgWhite(true) }
                        PrintPresetChip("Light grey", "soft", !bgWhite) { onBgWhite(false) }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Text("This preview is approximate - screens can't guarantee exact printed dimensions on every device or printer.", fontSize = 12.sp, color = Slate)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onContinue,
            enabled = !sheetMode || grid[2] > 0,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Continue to Export", color = Color.White, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PrintExportScreen(
    modifier: Modifier = Modifier,
    format: String,
    onFormat: (String) -> Unit,
    filename: String,
    onFilename: (String) -> Unit,
    jpgQuality: Int,
    onJpgQuality: (Int) -> Unit,
    wIn: Double,
    hIn: Double,
    ppi: Int,
    bleedOn: Boolean,
    marginsOn: Boolean,
    cropMarks: Boolean,
    imgW: Int,
    imgH: Int,
    sheetMode: Boolean,
    grid: IntArray,
    sheet: String,
    resultReady: Boolean,
    resultName: String,
    resultSize: Long,
    resultDims: String,
    saved: Boolean,
    busy: Boolean,
    onCreate: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onAgain: () -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        Text("Choose output format", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 17.sp)
        Spacer(Modifier.height(8.dp))
        listOf(
            Triple("JPG", "JPG / JPEG", "For photographs and common print services."),
            Triple("PNG", "PNG", "For artwork, graphics and supported transparency."),
            Triple("PDF", "PDF", "For a print-ready page with the image placed at the selected physical dimensions.")
        ).forEach { (id, title, desc) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = if (format == id) PaleBlue else SoftGrey),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(if (format == id) 2.dp else 0.dp, if (format == id) SkyBlue else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { onFormat(id) }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                    Text(desc, fontSize = 13.sp, color = Slate)
                }
            }
        }
        OutlinedTextField(
            filename, onFilename,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            label = { Text("Filename") },
            singleLine = true
        )
        if (format == "JPG") {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("JPEG quality", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = DeepNavy, fontSize = 14.sp)
                Text("$jpgQuality%", color = SkyBlue, fontWeight = FontWeight.Bold)
            }
            Slider(value = jpgQuality.toFloat(), onValueChange = { onJpgQuality(it.toInt()) }, valueRange = 50f..100f)
        }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("Summary", fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp)
                Spacer(Modifier.height(6.dp))
                if (sheetMode) {
                    Row { Text("Sheet", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text(sheet, fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                    Row { Text("Copies", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("${grid[2]} (${grid[0]} × ${grid[1]})", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                    Row { Text("Each copy", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("${PrintUtils.fmtDim(wIn, "mm")} × ${PrintUtils.fmtDim(hIn, "mm")} mm", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                } else {
                    Row { Text("Print size", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("${PrintUtils.fmtDim(wIn, "in")} × ${PrintUtils.fmtDim(hIn, "in")} in", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                    val req = PrintUtils.requiredPixels(wIn, hIn, ppi)
                    Row { Text("Pixels", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("${req[0]} × ${req[1]} px", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                }
                Row { Text("Resolution", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text("$ppi PPI", fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                if (!sheetMode && (bleedOn || marginsOn || cropMarks)) {
                    val extras = buildList {
                        if (bleedOn) add("bleed")
                        if (marginsOn) add("margins")
                        if (cropMarks) add("crop marks")
                    }.joinToString(" · ")
                    Row { Text("Options", Modifier.weight(1f), fontSize = 13.sp, color = Slate); Text(extras, fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onCreate,
            enabled = !busy && (wIn > 0 && hIn > 0) && (!sheetMode || grid[2] > 0) && filename.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(12.dp)
        ) { Text(if (busy) "Working…" else "Create File", color = Color.White, fontWeight = FontWeight.Bold) }
        if (resultReady) {
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(resultName, fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 15.sp, maxLines = 1)
                    Text((if (resultSize > 0) formatBytes(resultSize) + " · " else "") + resultDims, fontSize = 13.sp, color = Slate)
                    if (saved) {
                        Spacer(Modifier.height(6.dp))
                        Text("✓ Saved. Your original is unchanged.", color = SuccessGreen, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSave,
                    enabled = !busy && !saved,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(if (saved) "Saved ✓" else "Save to Phone", color = Color.White, fontWeight = FontWeight.Bold) }
                OutlinedButton(onClick = onShare, enabled = !busy, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp)) {
                    Text("Share", color = DeepNavy)
                }
            }
            Text("Nothing is saved until you tap Save to Phone.", fontSize = 12.sp, color = Slate, modifier = Modifier.padding(top = 6.dp))
        }
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = onAgain, modifier = Modifier.fillMaxWidth()) { Text("Start Over", color = SkyBlue) }
        Spacer(Modifier.height(20.dp))
    }
}
