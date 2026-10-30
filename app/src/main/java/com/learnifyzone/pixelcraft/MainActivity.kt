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
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Title
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.learnifyzone.pixelcraft.data.Draft
import com.learnifyzone.pixelcraft.data.DraftsStore
import com.learnifyzone.pixelcraft.data.Preset
import com.learnifyzone.pixelcraft.data.PresetStore
import com.learnifyzone.pixelcraft.data.RecentsStore
import com.learnifyzone.pixelcraft.image.BackgroundRemoval
import com.learnifyzone.pixelcraft.image.CollageMaker
import com.learnifyzone.pixelcraft.image.PhotoAdjust
import com.learnifyzone.pixelcraft.image.ExportFormat
import com.learnifyzone.pixelcraft.image.FilterUtils
import com.learnifyzone.pixelcraft.image.SaveUtils
import com.learnifyzone.pixelcraft.data.TextStylePreset
import com.learnifyzone.pixelcraft.data.TextStylePresetsStore
import com.learnifyzone.pixelcraft.image.TextOverlayUtils
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
        if (!android.os.Build.FINGERPRINT.contains("robolectric", ignoreCase = true)) installSplashScreen()   // v0.20.0: branded splash (his confirmed feature)
        super.onCreate(savedInstanceState)
        setContent { PixelCraftApp() }
    }
}

private enum class MainTab { Home, MyPhotos, Presets, Settings }
private enum class Screen { Home, Editor, Export, PrintHome, PrintSetup, PrintPreview, PrintExport, Success, QueueOverview, QueueResult, SocialHome, SocialPlatform, SocialEdit, ShopHome, ShopOther, ShopPlatform, ShopEdit, ShopCollage, ConvertHome, ConvertEdit, ConvertResult, CompressHome, CompressSettings, CompressResult, TextPhoto, Collage, PhotoAdjust }
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
    // v0.20.1 (his call, overrides the earlier static intro): his real 5-second opening video plays on cold start.
    // Any tap or system back skips straight to the app - promised him skippable. rememberSaveable so a rotation
    // mid-video does not restart it. Skipped on the Robolectric host (no media playback there).
    val testHost = android.os.Build.FINGERPRINT.contains("robolectric", ignoreCase = true)
    var showIntro by rememberSaveable { mutableStateOf(!testHost) }
    BackHandler(enabled = showIntro) { showIntro = false }
    MaterialTheme(colorScheme = colors) {
        if (showIntro) {
            OpeningVideoScreen(onFinished = { showIntro = false })
        } else {
            PixelCraftRoot(darkMode = darkMode, onDarkMode = { darkMode = it })
        }
    }
}

/** v0.20.1: his real 5-second opening video (res/raw/opening.mp4, his own asset - "keep 5 seconds video in opening").
 *  Fullscreen on the video's starry navy, letterboxed; plays once per cold start; tap anywhere or press back to skip;
 *  any playback error goes straight to the app. Replaces the static intro of v0.20.0. */
@Composable
internal fun OpeningVideoScreen(onFinished: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF030B1E))
            .clickable { onFinished() }
    ) {
        AndroidView(
            factory = { ctx ->
                android.widget.VideoView(ctx).apply {
                    setVideoURI(Uri.parse("android.resource://" + ctx.packageName + "/" + R.raw.opening))
                    setOnPreparedListener { start() }
                    setOnCompletionListener { onFinished() }
                    setOnErrorListener { _, _, _ -> onFinished(); true }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Text(
            "Tap to skip",
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 18.dp, end = 20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PixelCraftRoot(darkMode: Boolean, onDarkMode: (Boolean) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var drawerOpen by remember { mutableStateOf(false) }   // v0.20.0: home side drawer (his reference app)
    var settingsDoc by remember { mutableStateOf<String?>(null) }   // v0.20.0: terms/privacy in-app pages
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

    // v0.16.9 Collage Maker (standalone home tile, his "Yes all 8" #7) + Meme Maker state
    val collage = remember { CollageUiState() }
    var collageAppend by remember { mutableStateOf(false) }
    val adjust = remember { AdjustUiState() }
    var memeTopId by remember { mutableStateOf<Long?>(null) }
    var memeBottomId by remember { mutableStateOf<Long?>(null) }
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

    // Text on Photo (v0.16.0) - live-preview text layers over a photo; never touches the original.
    var textPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var textPhotoName by remember { mutableStateOf("") }
    var textPhotoBytes by remember { mutableStateOf(-1L) }
    val textLayers = remember { mutableStateListOf<TextOverlayUtils.TextLayer>() }
    val textImageLayers = remember { mutableStateListOf<TextOverlayUtils.ImageLayer>() }
    var textImageSelectedId by remember { mutableStateOf<Long?>(null) }
    var textSelectedId by remember { mutableStateOf<Long?>(null) }
    val textUndo = remember { TextOverlayUtils.UndoRedo() }
    var textTick by remember { mutableStateOf(0) }   // bumped on every layer mutation so the live preview repaints
    var textSelTick by remember { mutableStateOf(0) }   // bumped on every layer SELECT (canvas tap or Layers row) so re-tapping an already-selected layer re-opens its options panel (v0.16.10: his 'no Remove background button' report)
    var textFormat by remember { mutableStateOf("PNG") }
    var textResultBytes by remember { mutableStateOf<ByteArray?>(null) }
    var textResultDims by remember { mutableStateOf("") }
    var textSaved by remember { mutableStateOf(false) }
    var textEditOpen by remember { mutableStateOf(false) }
    var textCropRect by remember { mutableStateOf<FloatArray?>(null) }   // canvas cutter: [l,t,r,b] fractions of the photo
    var textCropLabel by remember { mutableStateOf("") }
    var textCropAdjust by remember { mutableStateOf(false) }
    var textCropZoomAbs by remember { mutableStateOf(1f) }   // v0.16.6: absolute Position-mode zoom (slider 1..5, like Social Media)
    var textFilterId by remember { mutableStateOf("normal") }
    // v0.19.0: Frame + Perspective tools (his confirmed spec - fill the 2 empty grid slots next to Meme)
    var textFrameStyle by remember { mutableStateOf("none") }
    var textFrameWidth by remember { mutableStateOf(8f) }
    var textFrameColor by remember { mutableStateOf(0xFFFFFFFF.toInt()) }
    var textPerspV by remember { mutableStateOf(0f) }
    var textPerspH by remember { mutableStateOf(0f) }
    var textStylePresets by remember { mutableStateOf<List<TextStylePreset>>(emptyList()) }
    var pendingTextStyle by remember { mutableStateOf<TextStylePreset?>(null) }
    var textLastGestureRecord by remember { mutableStateOf(0L) }
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
            if (testTool != "textphoto") loadPhoto(Uri.fromFile(File(testPath)), isCamera = false)
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

    /** v0.17.0: load 2-20 photos for the Collage Maker. Edit bitmaps are capped at 1024px for
     *  memory (his spec 9.3); the original URIs are kept and re-decoded per section at export. */
    fun loadCollageMakerPhotos(uris: List<Uri>) {
        scope.launch {
            busy = true
            busyLabel = "Loading photos…"
            try {
                val merged = if (collageAppend) (collage.uris + uris).take(20) else uris.take(20)
                val loaded = mutableListOf<CollageMaker.Photo>()
                merged.forEach { uri ->
                    val b = withContext(Dispatchers.IO) { runCatching { decodeAnyImage(context, uri) }.getOrNull() }
                    if (b != null) {
                        val capped = if (maxOf(b.width, b.height) > 1024) {
                            val f = 1024f / maxOf(b.width, b.height)
                            Bitmap.createScaledBitmap(b, (b.width * f).roundToInt(), (b.height * f).roundToInt(), true)
                        } else b
                        loaded.add(CollageMaker.Photo(capped))
                    }
                }
                busy = false
                busyLabel = ""
                if (loaded.size >= 2) {
                    collage.uris.clear()
                    collage.uris.addAll(merged)
                    collage.photos.clear()
                    collage.photos.addAll(loaded)
                    collage.saved = false
                    collage.selected = -1
                    collage.undoStack.clear()
                    collage.redoStack.clear()
                    collage.level = "home"
                    collageAppend = false
                    screen = Screen.Collage
                } else if (loaded.isNotEmpty()) {
                    snackbar.showSnackbar("Pick at least 2 photos for a collage.")
                } else {
                    snackbar.showSnackbar("Could not load those photos - try JPG or PNG.")
                }
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not load those photos - try JPG or PNG.")
            }
        }
    }

    /** v0.16.9: save the collage shown in the preview (the preview IS the export, 1600px PNG). */
    /** Cap a bitmap so its longest side is at most [maxSide] (keeps collages light in memory). */
    fun capBitmap(b: Bitmap, maxSide: Int): Bitmap = if (maxOf(b.width, b.height) > maxSide) {
        val f = maxSide.toFloat() / maxOf(b.width, b.height)
        Bitmap.createScaledBitmap(b, (b.width * f).roundToInt(), (b.height * f).roundToInt(), true)
    } else b

    /** v0.18.0: load one photo for Photo Adjustments (edit copy capped 1600px; original re-decoded at save). */
    fun loadAdjustPhoto(uri: Uri) {
        scope.launch {
            busy = true
            busyLabel = "Loading photo…"
            val full = withContext(Dispatchers.IO) { runCatching { decodeOrientedBitmap(context, uri) }.getOrNull() }
            busy = false
            busyLabel = ""
            if (full != null) {
                adjust.uri = uri
                adjust.origW = full.width
                adjust.origH = full.height
                adjust.base = capBitmap(full, 1600)
                adjust.params.copyFrom(PhotoAdjust.Params())
                adjust.filterId = "normal"
                adjust.compare = false
                adjust.saved = false
                adjust.undoStack.clear()
                adjust.redoStack.clear()
                adjust.panel = "adjust"
                adjust.tick++
                screen = Screen.PhotoAdjust
            } else {
                snackbar.showSnackbar("Could not load that photo - try JPG or PNG.")
            }
        }
    }

    /** v0.18.0: save the adjusted photo. The ORIGINAL is re-decoded, adjusted once, saved once -
     *  no repeated compression (his spec). The file on the phone is never touched. */
    fun saveAdjustPhoto() {
        val base = adjust.base ?: return
        scope.launch {
            busy = true
            busyLabel = "Saving…"
            try {
                val src = withContext(Dispatchers.IO) {
                    val u = adjust.uri
                    if (u != null) runCatching { decodeOrientedBitmap(context, u) }.getOrNull() ?: base else base
                }
                val filtered = if (adjust.filterId == "normal") src else FilterUtils.applyFilter(src, adjust.filterId)
                var out = withContext(Dispatchers.Default) { PhotoAdjust.apply(filtered, adjust.params) }
                if (adjust.resLabel == "Custom") {
                    val w = adjust.customW.toIntOrNull()?.coerceIn(256, 4096) ?: out.width
                    if (w != out.width) {
                        val f = w.toFloat() / out.width
                        out = Bitmap.createScaledBitmap(out, w, (out.height * f).roundToInt(), true)
                    }
                }
                val (fmt, mime, ext) = when (adjust.format) {
                    "PNG" -> Triple(Bitmap.CompressFormat.PNG, "image/png", "png")
                    "WebP" -> Triple(Bitmap.CompressFormat.WEBP, "image/webp", "webp")
                    else -> Triple(Bitmap.CompressFormat.JPEG, "image/jpeg", "jpg")
                }
                val q = if (fmt == Bitmap.CompressFormat.PNG) 100 else adjust.quality.roundToInt().coerceIn(50, 100)
                val baos = java.io.ByteArrayOutputStream()
                out.compress(fmt, q, baos)
                val name = "adjusted-${System.currentTimeMillis()}.$ext"
                withContext(Dispatchers.IO) { saveToGallery(context, name, mime, baos.toByteArray()) }
                adjust.saved = true
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Saved to your gallery as $name.")
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not save. Try again.")
            }
        }
    }

    /** v0.17.1: swap one collage photo for a new pick, keeping its slot. */
    fun replaceCollagePhoto(uri: Uri) {
        val idx = collage.selected
        if (idx !in collage.photos.indices) return
        scope.launch {
            busy = true
            busyLabel = "Loading photo…"
            val b = withContext(Dispatchers.IO) { runCatching { decodeAnyImage(context, uri) }.getOrNull() }
            busy = false
            busyLabel = ""
            if (b != null) {
                val ph = CollageMaker.Photo(capBitmap(b, 1024))
                collage.photos[idx] = ph
                if (idx < collage.uris.size) collage.uris[idx] = uri
                collage.tick++
                collage.saved = false
            } else {
                snackbar.showSnackbar("Could not load that photo.")
            }
        }
    }

    val collageProjectFile = remember { java.io.File(context.filesDir, "collage_project.json") }
    var collageHasProject by remember { mutableStateOf(collageProjectFile.exists()) }

    /** v0.17.1: save the whole collage (photos by reference + every adjustment) on the phone. */
    fun saveCollageProject() {
        scope.launch {
            busy = true
            busyLabel = "Saving project…"
            try {
                val o = org.json.JSONObject()
                o.put("family", collage.family.name)
                o.put("canvasW", collage.canvasW); o.put("canvasH", collage.canvasH); o.put("canvasLabel", collage.canvasLabel)
                o.put("gapPx", collage.gapPx.toDouble()); o.put("marginPx", collage.marginPx.toDouble()); o.put("radiusPx", collage.radiusPx.toDouble())
                o.put("borderPx", collage.borderPx.toDouble()); o.put("borderColor", collage.borderColor)
                o.put("bgLabel", collage.bgLabel); o.put("bgCustom", collage.bgCustom); o.put("gradientLabel", collage.gradientLabel)
                o.put("format", collage.format); o.put("quality", collage.quality.toDouble())
                o.put("resLabel", collage.resLabel); o.put("customResW", collage.customResW)
                val arr = org.json.JSONArray()
                collage.photos.forEachIndexed { i, ph ->
                    val pj = org.json.JSONObject()
                    pj.put("uri", collage.uris.getOrNull(i)?.toString() ?: "")
                    pj.put("zoom", ph.zoom.toDouble()); pj.put("panX", ph.panX.toDouble()); pj.put("panY", ph.panY.toDouble())
                    pj.put("rotation", ph.rotation.toDouble()); pj.put("flipH", ph.flipH); pj.put("flipV", ph.flipV)
                    pj.put("borderPx", ph.borderPx); pj.put("borderColor", ph.borderColor)
                    arr.put(pj)
                }
                o.put("photos", arr)
                withContext(Dispatchers.IO) { collageProjectFile.writeText(o.toString()) }
                collageHasProject = true
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Project saved on this phone.")
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not save the project.")
            }
        }
    }

    /** v0.17.1: reopen a saved project. Photo-picker links can expire; unreadable photos are skipped honestly. */
    fun loadCollageProject() {
        scope.launch {
            busy = true
            busyLabel = "Opening project…"
            try {
                val o = withContext(Dispatchers.IO) { org.json.JSONObject(collageProjectFile.readText()) }
                val arr = o.getJSONArray("photos")
                val loaded = mutableListOf<CollageMaker.Photo>()
                val uris = mutableListOf<Uri>()
                var unreadable = 0
                for (i in 0 until arr.length()) {
                    val pj = arr.getJSONObject(i)
                    val uriStr = pj.optString("uri", "")
                    val b = if (uriStr.isNotEmpty()) withContext(Dispatchers.IO) { runCatching { decodeAnyImage(context, Uri.parse(uriStr)) }.getOrNull() } else null
                    if (b == null) { unreadable++; continue }
                    val ph = CollageMaker.Photo(capBitmap(b, 1024))
                    ph.zoom = pj.optDouble("zoom", 1.0).toFloat()
                    ph.panX = pj.optDouble("panX", 0.0).toFloat()
                    ph.panY = pj.optDouble("panY", 0.0).toFloat()
                    ph.rotation = pj.optDouble("rotation", 0.0).toFloat()
                    ph.flipH = pj.optBoolean("flipH", false)
                    ph.flipV = pj.optBoolean("flipV", false)
                    ph.borderPx = pj.optInt("borderPx", -1)
                    ph.borderColor = pj.optInt("borderColor", 0xFFFFFFFF.toInt())
                    loaded.add(ph)
                    uris.add(Uri.parse(uriStr))
                }
                busy = false
                busyLabel = ""
                if (loaded.size >= 2) {
                    collage.photos.clear(); collage.photos.addAll(loaded)
                    collage.uris.clear(); collage.uris.addAll(uris)
                    collage.family = runCatching { CollageMaker.LayoutFamily.valueOf(o.optString("family", "GRID")) }.getOrDefault(CollageMaker.LayoutFamily.GRID)
                    collage.canvasW = o.optInt("canvasW", 1080); collage.canvasH = o.optInt("canvasH", 1080)
                    collage.canvasLabel = o.optString("canvasLabel", "Square 1080")
                    collage.gapPx = o.optDouble("gapPx", 8.0).toFloat()
                    collage.marginPx = o.optDouble("marginPx", 0.0).toFloat()
                    collage.radiusPx = o.optDouble("radiusPx", 0.0).toFloat()
                    collage.borderPx = o.optDouble("borderPx", 0.0).toFloat()
                    collage.borderColor = o.optInt("borderColor", 0xFFFFFFFF.toInt())
                    collage.bgLabel = o.optString("bgLabel", "White")
                    collage.bgCustom = o.optInt("bgCustom", 0xFF3D5AFE.toInt())
                    collage.gradientLabel = o.optString("gradientLabel", "Sunset")
                    collage.format = o.optString("format", "JPG")
                    collage.quality = o.optDouble("quality", 90.0).toFloat()
                    collage.resLabel = o.optString("resLabel", "Standard")
                    collage.customResW = o.optString("customResW", "1600")
                    collage.selected = -1
                    collage.undoStack.clear(); collage.redoStack.clear()
                    collage.level = "edit"
                    collage.tick++
                    collage.saved = false
                    screen = Screen.Collage
                    if (unreadable > 0) snackbar.showSnackbar("Opened without $unreadable photo(s) that are no longer available.")
                } else {
                    snackbar.showSnackbar("Those photos are no longer available - pick them again.")
                }
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not open the saved project.")
            }
        }
    }

    /** v0.17.0: export through the SAME engine as the preview. Originals are re-decoded one
     *  section at a time (sampled, capped) so export keeps quality without holding 20 originals. */
    fun saveCollageMaker() {
        if (collage.photos.size < 2) return
        scope.launch {
            busy = true
            busyLabel = "Saving…"
            try {
                val (ew, eh) = collage.exportSize()
                val sections = collage.sections()
                val scaleF = ew.toFloat() / collage.canvasW
                val hiBitmaps = mutableListOf<Bitmap>()
                val result = withContext(Dispatchers.IO) {
                    val r = CollageMaker.renderSections({ i ->
                        val edit = collage.photos[i]
                        val uri = collage.uris.getOrNull(i)
                        var bmp = edit.bitmap
                        if (uri != null) {
                            runCatching { decodeOrientedBitmap(context, uri) }.getOrNull()?.let { full ->
                                // cap near what the section needs (zoom can crop in 4x, so 2x section is plenty)
                                val secW = (sections[i].width() * ew).roundToInt().coerceAtLeast(64)
                                val secH = (sections[i].height() * eh).roundToInt().coerceAtLeast(64)
                                val cap = maxOf(secW, secH) * 2
                                bmp = if (maxOf(full.width, full.height) > cap) {
                                    val f = cap.toFloat() / maxOf(full.width, full.height)
                                    val scaled = Bitmap.createScaledBitmap(full, (full.width * f).roundToInt(), (full.height * f).roundToInt(), true)
                                    if (scaled != full) full.recycle()
                                    scaled
                                } else full
                                if (bmp != edit.bitmap) hiBitmaps.add(bmp)
                            }
                        }
                        CollageMaker.Photo(bmp, edit.zoom, edit.panX, edit.panY, edit.rotation, edit.flipH, edit.flipV)
                    }, collage.photos.size, sections, ew, eh, collage.style(scaleF))
                    hiBitmaps.forEach { it.recycle() }
                    r
                }
                val (fmt, mime, ext) = when (collage.format) {
                    "PNG" -> Triple(Bitmap.CompressFormat.PNG, "image/png", "png")
                    "WebP" -> Triple(Bitmap.CompressFormat.WEBP, "image/webp", "webp")
                    else -> Triple(Bitmap.CompressFormat.JPEG, "image/jpeg", "jpg")
                }
                // JPG has no transparency: a transparent background becomes white.
                val toEncode = if (fmt == Bitmap.CompressFormat.JPEG && collage.bgLabel == "Transparent") {
                    val flat = Bitmap.createBitmap(result.width, result.height, Bitmap.Config.ARGB_8888)
                    val c = android.graphics.Canvas(flat)
                    c.drawColor(android.graphics.Color.WHITE)
                    c.drawBitmap(result, 0f, 0f, null)
                    flat
                } else result
                val q = if (fmt == Bitmap.CompressFormat.PNG) 100 else collage.quality.roundToInt().coerceIn(50, 100)
                val baos = java.io.ByteArrayOutputStream()
                toEncode.compress(fmt, q, baos)
                val name = "collage-${System.currentTimeMillis()}.$ext"
                withContext(Dispatchers.IO) { saveToGallery(context, name, mime, baos.toByteArray()) }
                collage.saved = true
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Saved to your gallery as $name.")
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not save. Try again.")
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

    /** "Add" applies the currently marked pages - same action as the big "Use N selected pages" button. */
    fun addMarkedPrintPages() {
        if (printPdfSelected.isEmpty()) {
            scope.launch { snackbar.showSnackbar("Tap pages below to select them first.") }
            return
        }
        selectPrintPdfPages(printPdfSelected.sorted())
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
                val bg = if (printBgWhite) android.graphics.Color.WHITE else PrintUtils.LIGHT_GREY_BG
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
    val collageMakerPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(20)) { uris ->
        if (uris.isNotEmpty()) loadCollageMakerPhotos(uris)
    }
    val collageReplacePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) replaceCollagePhoto(uri)
    }
    val photoAdjustPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) loadAdjustPhoto(uri)
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

    fun resetTextPhoto() {
        textPhotoBitmap = null
        textPhotoName = ""
        textPhotoBytes = -1L
        textLayers.clear()
        textSelectedId = null
        textImageLayers.clear()
        textImageSelectedId = null
        textUndo.clear()
        textFormat = "PNG"
        textResultBytes = null
        textSaved = false
        textEditOpen = false
        textCropRect = null
        textCropLabel = ""
        textCropAdjust = false
        textCropZoomAbs = 1f
        textTick++
    }

    /** His v0.16.3 "Add reset button": start over on the SAME photo - clears layers, crop, undo and results. */
    fun resetTextEdits() {
        textFilterId = "normal"
        textFrameStyle = "none"
        textFrameWidth = 8f
        textFrameColor = 0xFFFFFFFF.toInt()
        textPerspV = 0f
        textPerspH = 0f
        textLayers.clear()
        textSelectedId = null
        textImageLayers.clear()
        textImageSelectedId = null
        textUndo.clear()
        textCropRect = null
        textCropLabel = ""
        textCropAdjust = false
        textCropZoomAbs = 1f
        textResultBytes = null
        textSaved = false
        textEditOpen = false
        textTick++
    }

    /** One undo entry per change; gesture drags and typing coalesce into 1-1.5s windows so the stack stays meaningful. */
    fun recordTextUndo(coalesceMs: Long = 0L) {
        val now = System.currentTimeMillis()
        if (coalesceMs > 0L && now - textLastGestureRecord < coalesceMs) return
        textLastGestureRecord = now
        textUndo.record(textLayers.toList())
    }

    /** v0.16.9 (his "Yes all 8" #8): classic meme top/bottom captions on the shared text engine. */
    fun applyMeme(topText: String, bottomText: String) {
        recordTextUndo()
        fun styleMeme(l: TextOverlayUtils.TextLayer, text: String, y: Float) {
            l.text = text.uppercase()
            l.font = TextOverlayUtils.FontChoice.Black
            l.bold = false
            l.sizeFrac = 0.09f
            l.colorArgb = 0xFFFFFFFF.toInt()
            l.outlineFrac = 0.08f
            l.outlineColorArgb = 0xFF000000.toInt()
            l.bgColorArgb = 0
            l.shadow = false
            l.effect = TextOverlayUtils.TextEffect.NONE
            l.cx = 0.5f
            l.cy = y
        }
        if (topText.isNotBlank()) {
            val existing = textLayers.firstOrNull { it.id == memeTopId }
            if (existing != null) {
                styleMeme(existing, topText, 0.10f)
            } else {
                var id = System.currentTimeMillis()
                while (textLayers.any { it.id == id } || textImageLayers.any { it.id == id }) id++
                val l = TextOverlayUtils.TextLayer(id = id)
                styleMeme(l, topText, 0.10f)
                textLayers.add(l)
                memeTopId = id
            }
        }
        if (bottomText.isNotBlank()) {
            val existing = textLayers.firstOrNull { it.id == memeBottomId }
            if (existing != null) {
                styleMeme(existing, bottomText, 0.90f)
            } else {
                var id = System.currentTimeMillis() + 1
                while (textLayers.any { it.id == id } || textImageLayers.any { it.id == id }) id++
                val l = TextOverlayUtils.TextLayer(id = id)
                styleMeme(l, bottomText, 0.90f)
                textLayers.add(l)
                memeBottomId = id
            }
        }
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** v0.16.9 (his "Yes all 8" #6): remove the background of the selected image layer, fully on-device. */
    fun removeBgOnSelectedImageLayer() {
        val layer = textImageLayers.firstOrNull { it.id == textImageSelectedId } ?: return
        scope.launch {
            busy = true
            busyLabel = "Removing background…"
            try {
                // v0.16.10 (his "not working" report): the subject engine kept his whole logo card
                // as the "subject", so the bitmap came back unchanged and the success snackbar was
                // the only sign anything ran. Now the result is MEASURED: if the subject engine cut
                // (<2% transparent), a solid-colour flood remover runs (logos/icons on a plain
                // background); if still nothing, he gets an honest message instead of fake success.
                val totalPx = layer.bitmap.width * layer.bitmap.height
                val outcome = withContext(Dispatchers.IO) {
                    val cut = BackgroundRemoval.cutOut(context, layer.bitmap)
                    var frac = BackgroundRemoval.transparentFraction(cut)
                    var best = cut
                    if (frac < 0.02f) {
                        val (solid, removed) = BackgroundRemoval.removeSolidBackground(layer.bitmap)
                        val frac2 = removed.toFloat() / totalPx
                        if (frac2 > frac) { best = solid; frac = frac2 }
                    }
                    Pair(best, frac)
                }
                busy = false
                busyLabel = ""
                if (outcome.second >= 0.02f) {
                    recordTextUndo()
                    layer.bitmap = outcome.first
                    textResultBytes = null
                    textSaved = false
                    textTick++
                    snackbar.showSnackbar("Background removed - drag the layer to place it.")
                } else {
                    snackbar.showSnackbar("No clear background found on this image - it works best on one clear subject or a plain background.")
                }
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not remove the background on this one - try another image.")
            }
        }
    }

    fun selectedTextLayer(): TextOverlayUtils.TextLayer? = textLayers.firstOrNull { it.id == textSelectedId }

    fun addTextLayer() {
        recordTextUndo()
        val layer = TextOverlayUtils.TextLayer(id = System.currentTimeMillis())
        textLayers.add(layer)
        textSelectedId = layer.id
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun deleteTextLayer(id: Long) {
        recordTextUndo()
        textLayers.removeAll { it.id == id }
        if (textSelectedId == id) textSelectedId = textLayers.lastOrNull()?.id
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun moveTextLayer(id: Long, dir: Int) {
        val i = textLayers.indexOfFirst { it.id == id }
        val j = i + dir
        if (i < 0 || j !in textLayers.indices) return
        recordTextUndo()
        val l = textLayers.removeAt(i)
        textLayers.add(j, l)
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun mutateSelectedLayer(coalesceMs: Long = 0L, block: (TextOverlayUtils.TextLayer) -> Unit) {
        val layer = selectedTextLayer() ?: return
        recordTextUndo(coalesceMs)
        block(layer)
        textResultBytes = null
        textSaved = false
        textTick++
    }

    // ---- Image overlay layers (his v0.16.1 "Add image" spec: a logo/sticker photo ON TOP of the
    //      main photo, stays fully editable - move/resize/rotate/delete - until export) ----
    fun addTextImageLayer(uri: Uri) {
        scope.launch {
            busy = true
            busyLabel = "Adding image…"
            try {
                // decodeAnyImage: standard decode first, ImageDecoder fallback for formats
                // BitmapFactory can't read on his phone (his "logo not added" report).
                val loaded = withContext(Dispatchers.IO) { decodeAnyImage(context, uri) }
                val capped = if (maxOf(loaded.width, loaded.height) > 2048) {
                    val fct = 2048f / maxOf(loaded.width, loaded.height)
                    Bitmap.createScaledBitmap(loaded, (loaded.width * fct).roundToInt(), (loaded.height * fct).roundToInt(), true)
                } else loaded
                val layer = TextOverlayUtils.ImageLayer(System.currentTimeMillis() + 1L, capped, cx = 0.5f, cy = 0.4f)
                textImageLayers.add(layer)
                textImageSelectedId = layer.id
                textSelectedId = null
                textResultBytes = null
                textSaved = false
                textTick++
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not add that image - try a JPG or PNG.")
                return@launch
            }
            busy = false
            busyLabel = ""
            scope.launch { snackbar.showSnackbar("Image layer added - drag it on the photo.") }
        }
    }

    fun selectedImageLayer(): TextOverlayUtils.ImageLayer? = textImageLayers.firstOrNull { it.id == textImageSelectedId }

    fun mutateSelectedImageLayer(block: (TextOverlayUtils.ImageLayer) -> Unit) {
        val layer = selectedImageLayer() ?: return
        block(layer)
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun deleteImageLayer(id: Long) {
        textImageLayers.removeAll { it.id == id }
        if (textImageSelectedId == id) textImageSelectedId = null
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun moveImageLayer(id: Long, dir: Int) {
        val i = textImageLayers.indexOfFirst { it.id == id }
        val j = i + dir
        if (i < 0 || j !in textImageLayers.indices) return
        val l = textImageLayers.removeAt(i)
        textImageLayers.add(j, l)
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun undoTextLayers() {
        textUndo.undo(textLayers.toList())?.let { snap ->
            textLayers.clear()
            textLayers.addAll(snap)
            if (textLayers.none { it.id == textSelectedId }) textSelectedId = textLayers.lastOrNull()?.id
        }
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun redoTextLayers() {
        textUndo.redo(textLayers.toList())?.let { snap ->
            textLayers.clear()
            textLayers.addAll(snap)
            if (textLayers.none { it.id == textSelectedId }) textSelectedId = textLayers.lastOrNull()?.id
        }
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** Canvas size in effect: the crop when one is set, else the whole photo. */
    fun textEffectiveSize(): IntArray {
        val bmp = textPhotoBitmap ?: return intArrayOf(0, 0)
        val r = textCropRect ?: return intArrayOf(bmp.width, bmp.height)
        return TextOverlayUtils.cropPixelSize(bmp.width, bmp.height, r)
    }

    fun setTextCrop(label: String, rw: Int, rh: Int) {
        val bmp = textPhotoBitmap ?: return
        textCropRect = TextOverlayUtils.maxCropForRatio(bmp.width, bmp.height, rw, rh)
        textCropLabel = "$label $rw:$rh"
        textCropAdjust = true
        textCropZoomAbs = 1f
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun clearTextCrop() {
        textCropRect = null
        textCropLabel = ""
        textCropAdjust = false
        textCropZoomAbs = 1f
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** Drag the crop frame; deltas are photo-space fractions. Moves freely, clamped at the photo edges - no snapping. */
    fun nudgeTextCrop(dxFrac: Float, dyFrac: Float) {
        val r = textCropRect ?: return
        textCropRect = com.learnifyzone.pixelcraft.image.CropMath.move(r, dxFrac, dyFrac)
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** Pinch-zoom the crop frame around its centre (his Position interaction, like Social Media Sizes). */
    fun zoomTextCrop(zoom: Float) {
        val r = textCropRect ?: return
        textCropRect = TextOverlayUtils.zoomCrop(r, zoom)
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /**
     * v0.16.6 (his "make same like you make in social media section" / "Exactly 100% same"): ABSOLUTE
     * zoom for the Position-mode slider, 1..5 like the Social Media cropper. Applied as a relative
     * zoom around the frame's CURRENT centre, so a pan is never lost when the slider moves.
     */
    fun setTextCropZoomAbs(newZoom: Float) {
        val old = textCropZoomAbs
        val z = newZoom.coerceIn(1f, 5f)
        val r = textCropRect
        if (r == null || kotlin.math.abs(z - old) < 0.0001f) { textCropZoomAbs = z; return }
        textCropRect = TextOverlayUtils.zoomCrop(r, z / old)
        textCropZoomAbs = z
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** Pinch in Position mode: same absolute model, so the slider follows the fingers. */
    fun pinchTextCropZoom(factor: Float) {
        val old = textCropZoomAbs
        val newAbs = (old * factor).coerceIn(1f, 5f)
        if (kotlin.math.abs(newAbs - old) > 0.0001f) {
            zoomTextCrop(newAbs / old)
            textCropZoomAbs = newAbs
        }
    }

    /** v0.16.7 eyedropper (his approved idea): sample the photo colour at an effective-canvas point and apply it to the selected text layer. */
    fun pickColourAt(xEff: Float, yEff: Float, target: String): Boolean {
        val bmp = textPhotoBitmap ?: return false
        val layer = selectedTextLayer() ?: return false
        val r = textCropRect
        val px = ((r?.get(0) ?: 0f) * bmp.width + xEff).toInt()
        val py = ((r?.get(1) ?: 0f) * bmp.height + yEff).toInt()
        if (px !in 0 until bmp.width || py !in 0 until bmp.height) return false
        recordTextUndo()
        val c = bmp.getPixel(px, py)
        if (target == "background") layer.bgColorArgb = c else layer.colorArgb = c
        textResultBytes = null
        textSaved = false
        textTick++
        return true
    }

    /** v0.16.7: mirror the selected layer (text or image) horizontally - one tap. */
    fun flipSelectedLayer() {
        val t = selectedTextLayer()
        val im = selectedImageLayer()
        if (t == null && im == null) return
        recordTextUndo()
        t?.let { it.flipH = !it.flipH }
        im?.let { it.flipH = !it.flipH }
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** v0.16.7: duplicate the selected layer with a small offset - one tap. */
    fun cloneSelectedLayer() {
        val t = selectedTextLayer()
        if (t != null) {
            recordTextUndo()
            var newId = System.currentTimeMillis()
            while (textLayers.any { it.id == newId } || textImageLayers.any { it.id == newId }) newId++
            val c = t.copy(id = newId, cx = (t.cx + 0.05f).coerceAtMost(0.95f), cy = (t.cy + 0.05f).coerceAtMost(0.95f))
            textLayers.add(c)
            textSelectedId = c.id
            textImageSelectedId = null
        } else {
            val im = selectedImageLayer() ?: return
            var newId = System.currentTimeMillis()
            while (textLayers.any { it.id == newId } || textImageLayers.any { it.id == newId }) newId++
            val c = TextOverlayUtils.ImageLayer(newId, im.bitmap, (im.cx + 0.05f).coerceAtMost(0.95f), (im.cy + 0.05f).coerceAtMost(0.95f), im.widthFrac, im.rotation, im.opacity)
            c.flipH = im.flipH
            textImageLayers.add(c)
            textImageSelectedId = c.id
            textSelectedId = null
        }
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** v0.16.8: emoji from the Stickers tab - added as an editable text layer (resize/rotate/effects all work). */
    fun addEmojiTextLayer(emoji: String) {
        recordTextUndo()
        var newId = System.currentTimeMillis()
        while (textLayers.any { it.id == newId } || textImageLayers.any { it.id == newId }) newId++
        val layer = TextOverlayUtils.TextLayer(id = newId)
        layer.text = emoji
        layer.sizeFrac = 0.16f
        textLayers.add(layer)
        textSelectedId = layer.id
        textImageSelectedId = null
        textResultBytes = null
        textSaved = false
        textTick++
    }

    /** v0.16.8: drawn shape sticker (heart/star/arrow/burst) - added as an image layer under the text. */
    fun addStickerLayer(kind: TextOverlayUtils.StickerKind, colorArgb: Int) {
        recordTextUndo()
        val bmp = TextOverlayUtils.makeStickerBitmap(kind, 512, colorArgb)
        var newId = System.currentTimeMillis()
        while (textLayers.any { it.id == newId } || textImageLayers.any { it.id == newId }) newId++
        val layer = TextOverlayUtils.ImageLayer(newId, bmp, cx = 0.5f, cy = 0.4f, widthFrac = 0.3f)
        textImageLayers.add(layer)
        textImageSelectedId = layer.id
        textSelectedId = null
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun refreshTextStylePresets() {
        scope.launch(Dispatchers.IO) {
            val list = TextStylePresetsStore.load(context)
            withContext(Dispatchers.Main) { textStylePresets = list }
        }
    }

    /** Apply a saved style to the selected layer (creates one when none exists). */
    fun applyTextStylePreset(p: TextStylePreset) {
        if (selectedTextLayer() == null) addTextLayer()
        val layer = selectedTextLayer() ?: return
        recordTextUndo()
        layer.font = TextOverlayUtils.FontChoice.values().firstOrNull { it.name == p.font } ?: TextOverlayUtils.FontChoice.Sans
        layer.sizeFrac = p.sizeFrac
        layer.bold = p.bold
        layer.italic = p.italic
        layer.align = TextOverlayUtils.Align.values().firstOrNull { it.name == p.align } ?: TextOverlayUtils.Align.CENTER
        layer.colorArgb = p.colorArgb
        layer.outlineFrac = p.outlineFrac
        layer.outlineColorArgb = p.outlineColorArgb
        layer.bgColorArgb = p.bgColorArgb
        layer.shadow = p.shadow
        layer.opacity = p.opacity
        layer.curved = p.curved
        layer.arcSweep = p.arcSweep
        layer.letterSpacing = p.letterSpacing
        layer.lineSpacing = p.lineSpacing
        textResultBytes = null
        textSaved = false
        textTick++
    }

    fun saveTextStylePreset(name: String) {
        val layer = selectedTextLayer() ?: return
        val p = TextStylePreset(
            id = System.currentTimeMillis(), name = name, font = layer.font.name,
            sizeFrac = layer.sizeFrac, bold = layer.bold, italic = layer.italic, align = layer.align.name,
            colorArgb = layer.colorArgb, outlineFrac = layer.outlineFrac, outlineColorArgb = layer.outlineColorArgb,
            bgColorArgb = layer.bgColorArgb, shadow = layer.shadow, opacity = layer.opacity,
            curved = layer.curved, arcSweep = layer.arcSweep,
            letterSpacing = layer.letterSpacing, lineSpacing = layer.lineSpacing,
            createdAt = System.currentTimeMillis()
        )
        scope.launch(Dispatchers.IO) {
            TextStylePresetsStore.add(context, p)
            refreshTextStylePresets()
        }
        scope.launch { snackbar.showSnackbar("Style \"" + name + "\" saved - find it in the Presets tab.") }
    }

    fun loadTextPhoto(uri: Uri) {
        scope.launch {
            busy = true
            busyLabel = "Loading photo…"
            try {
                val loaded = withContext(Dispatchers.IO) { decodeOrientedBitmap(context, uri) }
                textPhotoBitmap = loaded
                textPhotoName = queryDisplayName(context, uri) ?: "image"
                textPhotoBytes = querySourceSize(context, uri)
                textResultBytes = null
                textSaved = false
                // His v0.16.2 rule: the photo opens CLEAN - no default "Your text" layer.
                // A text layer appears only when he taps + Text (or a saved style needs one).
                refreshTextStylePresets()
                pendingTextStyle?.let { p -> pendingTextStyle = null; applyTextStylePreset(p) }
                textTick++
            } catch (e: Exception) {
                busy = false
                busyLabel = ""
                snackbar.showSnackbar("Could not open that image.")
                return@launch
            }
            busy = false
            busyLabel = ""
        }
    }


    // Test hook (JVM UI tests): open Text on Photo directly with a pre-loaded photo, an optional
    // sample text layer and an optional overlay image - the pickers cannot run under Robolectric.
    LaunchedEffect(Unit) {
        val act = context as? Activity
        if (act?.intent?.getStringExtra("pixelcraft.test.tool") == "textphoto") {
            act.intent.getStringExtra("pixelcraft.test.photo")?.let { p ->
                screen = Screen.TextPhoto
                loadTextPhoto(Uri.fromFile(File(p)))
            }
            if (act.intent.getStringExtra("pixelcraft.test.textlayer") == "1") {
                addTextLayer()
                selectedTextLayer()?.text = "Hello"
                textTick++
            }
            act.intent.getStringExtra("pixelcraft.test.overlay")?.let { addTextImageLayer(Uri.fromFile(File(it))) }
            // v0.16.10: simulate one tap on the already-selected layer (Robolectric cannot drive the
            // canvas detectTapGestures reliably) - runs the same state change as onSelectImageLayer.
            if (act.intent.getStringExtra("pixelcraft.test.seltap") == "1") { textSelTick++ }
            // v0.16.9: deterministic meme content for UI tests ("top|bottom") - Robolectric
            // semantics clicks deliver stale field text to the Apply button (documented in tests).
            act.intent.getStringExtra("pixelcraft.test.memetext")?.let { spec ->
                val parts = spec.split("|")
                applyMeme(parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
            }
        }
        if (act?.intent?.getStringExtra("pixelcraft.test.collage") == "1") {
            // Deterministic synthetic photos - the pickers cannot run under Robolectric.
            collage.uris.clear()
            collage.photos.clear()
            for (i in 0..2) {
                val b = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
                b.eraseColor(when (i) { 0 -> 0xFFF95F6A.toInt(); 1 -> 0xFF42BFEF.toInt(); else -> 0xFF3ECF8E.toInt() })
                collage.photos.add(CollageMaker.Photo(b))
            }
            collage.selected = act.intent.getStringExtra("pixelcraft.test.collagesel")?.toIntOrNull() ?: -1
            collage.level = act.intent.getStringExtra("pixelcraft.test.collagelevel") ?: "edit"
            screen = Screen.Collage
        }
        if (act?.intent?.getStringExtra("pixelcraft.test.adjust") == "1") {
            // Deterministic synthetic photo - the pickers cannot run under Robolectric.
            val b = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
            val px = IntArray(800 * 600) { i ->
                val x = i % 800; val y = i / 800
                (0xFF shl 24) or ((x * 255 / 800) shl 16) or ((y * 255 / 600) shl 8) or 0x80
            }
            b.setPixels(px, 0, 800, 0, 0, 800, 600)
            adjust.uri = null
            adjust.origW = 800; adjust.origH = 600
            adjust.base = b
            adjust.params.copyFrom(PhotoAdjust.Params())
            adjust.filterId = "normal"
            screen = Screen.PhotoAdjust
        }
    }

    /** Tap-to-select: topmost layer whose measured block contains the point (in EFFECTIVE canvas pixels; rotation-agnostic, generous pad). */
    fun textLayerAt(xEff: Float, yEff: Float): TextOverlayUtils.TextLayer? {
        val eff = textEffectiveSize()
        if (eff[0] == 0) return null
        for (i in textLayers.indices.reversed()) {
            val l = textLayers[i]
            val b = TextOverlayUtils.measure(l, eff[0])
            val pad = b.textSize * 0.5f
            val halfW: Float
            val halfH: Float
            if (l.curved) {
                val tw = b.lineWidths.maxOrNull() ?: 0f
                val r = TextOverlayUtils.arcRadiusFor(tw, l.arcSweep, b.textSize)
                val sag = r * (1f - kotlin.math.cos(Math.toRadians((l.arcSweep / 2f).toDouble())).toFloat())
                halfW = tw / 2f + pad
                halfH = (sag + b.textSize) / 2f + pad
            } else {
                halfW = b.w / 2f + pad
                halfH = b.h / 2f + pad
            }
            val cxp = l.cx * eff[0]
            val cyp = l.cy * eff[1] - if (l.curved) halfH - pad else 0f
            if (xEff >= cxp - halfW && xEff <= cxp + halfW && yEff >= cyp - halfH && yEff <= cyp + halfH) return l
        }
        return null
    }

    fun processTextPhoto() {
        val bmp = textPhotoBitmap ?: return
        scope.launch {
            busy = true
            busyLabel = "Processing…"
            try {
                val eff = textEffectiveSize()
                val bytes = withContext(Dispatchers.IO) {
                    val crop = textCropRect
                    val source = if (crop == null) bmp else Bitmap.createBitmap(
                        bmp,
                        (crop[0] * bmp.width).toInt().coerceIn(0, bmp.width - 16),
                        (crop[1] * bmp.height).toInt().coerceIn(0, bmp.height - 16),
                        eff[0].coerceAtMost(bmp.width - (crop[0] * bmp.width).toInt().coerceIn(0, bmp.width - 16)),
                        eff[1].coerceAtMost(bmp.height - (crop[1] * bmp.height).toInt().coerceIn(0, bmp.height - 16))
                    )
                    val filtered = if (textFilterId == "normal") source else FilterUtils.applyFilter(source, textFilterId)
                    val out = TextOverlayUtils.renderToBitmap(
                        filtered, textLayers.toList(), textImageLayers.toList(),
                        frameStyle = TextOverlayUtils.FrameStyle.fromId(textFrameStyle),
                        frameWidthPx = textFrameWidth * minOf(filtered.width, filtered.height) / 400f,
                        frameColor = textFrameColor,
                        perspV = textPerspV, perspH = textPerspH
                    )
                    val baos = java.io.ByteArrayOutputStream()
                    if (textFormat == "PNG") out.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    else out.compress(Bitmap.CompressFormat.JPEG, 95, baos)
                    out.recycle()
                    if (filtered !== source) filtered.recycle()
                    if (source !== bmp) source.recycle()
                    baos.toByteArray()
                }
                textResultBytes = bytes
                textResultDims = "${eff[0]} × ${eff[1]} px"
                textSaved = false
            } catch (e: Exception) {
                snackbar.showSnackbar("Could not process that image.")
            }
            busy = false
            busyLabel = ""
        }
    }

    fun saveTextPhoto() {
        val bytes = textResultBytes ?: return
        scope.launch {
            busy = true
            busyLabel = "Saving…"
            try {
                val ext = if (textFormat == "PNG") "png" else "jpg"
                val mime = if (textFormat == "PNG") "image/png" else "image/jpeg"
                val name = "text-on-photo-${System.currentTimeMillis()}.$ext"
                withContext(Dispatchers.IO) { saveToGallery(context, name, mime, bytes) }
                textSaved = true
                snackbar.showSnackbar("Saved to your gallery as $name. The original photo is untouched.")
            } catch (e: Exception) {
                snackbar.showSnackbar("Could not save. Try again.")
            }
            busy = false
            busyLabel = ""
        }
    }

    val textPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { loadTextPhoto(it) }
    }
    val textImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { addTextImageLayer(it) }
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
                                Screen.TextPhoto -> { resetTextPhoto(); screen = Screen.Home }
            Screen.CompressHome -> { resetCompress(); screen = Screen.Home }
            else -> screen = Screen.Home
        }
    }

    // v0.20.1 hotfix (found on his phone): back on Home-tab pages EXITED the app, because the handler above only
    // runs for tool screens. Now: an open drawer closes, a doc page returns to Settings, any other tab returns to
    // Home. Back must always return, never exit. Last enabled handler wins, so this order is load-bearing.
    BackHandler(enabled = screen == Screen.Home && tab != MainTab.Home) { tab = MainTab.Home }
    BackHandler(enabled = settingsDoc != null) { settingsDoc = null }
    BackHandler(enabled = drawerOpen) { drawerOpen = false }


    @Composable
    fun RenderTextPhoto(modifier: Modifier) {
        TextOnPhotoScreen(
            modifier = modifier,
                bitmap = textPhotoBitmap,
                name = textPhotoName,
                sizeBytes = textPhotoBytes,
                layers = textLayers,
                tick = textTick,
                selTick = textSelTick,
                selectedId = textSelectedId,
                canUndo = textUndo.canUndo,
                canRedo = textUndo.canRedo,
                format = textFormat,
                resultBytes = textResultBytes,
                resultDims = textResultDims,
                saved = textSaved,
                editOpen = textEditOpen,
                busy = busy,
                onSelectPhoto = { textPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onResetEdits = { resetTextEdits() },
                filterId = textFilterId,
                onFilter = { id -> textFilterId = id; textResultBytes = null; textSaved = false; textTick++ },
                frameStyle = textFrameStyle,
                frameWidth = textFrameWidth,
                frameColor = textFrameColor,
                perspV = textPerspV,
                perspH = textPerspH,
                onFrameStyle = { id -> textFrameStyle = id; textResultBytes = null; textSaved = false; textTick++ },
                onFrameWidth = { v -> textFrameWidth = v; textResultBytes = null; textSaved = false; textTick++ },
                onFrameColor = { c -> textFrameColor = c; textResultBytes = null; textSaved = false; textTick++ },
                onPerspV = { v -> textPerspV = v; textResultBytes = null; textSaved = false; textTick++ },
                onPerspH = { v -> textPerspH = v; textResultBytes = null; textSaved = false; textTick++ },
                onResetPerspective = { textPerspV = 0f; textPerspH = 0f; textResultBytes = null; textSaved = false; textTick++ },
                onSelectLayer = { textSelectedId = it; textImageSelectedId = null; textTick++; textSelTick++ },
                onAddLayer = { textImageSelectedId = null; addTextLayer() },
                onDeleteLayer = { deleteTextLayer(it) },
                onMoveLayer = { id, dir -> moveTextLayer(id, dir) },
                onMutate = { coalesce, block -> mutateSelectedLayer(coalesce, block) },
                imageLayers = textImageLayers,
                selectedImageId = textImageSelectedId,
                onAddImageLayer = { textImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onSelectImageLayer = { textImageSelectedId = it; textSelectedId = null; textTick++; textSelTick++ },
                onMutateImageLayer = { block -> mutateSelectedImageLayer(block) },
                onTransform = { dx, dy, z, r ->
                    if (textImageSelectedId != null && selectedImageLayer() != null) {
                        mutateSelectedImageLayer { im ->
                            im.cx = (im.cx + dx).coerceIn(0f, 1f)
                            im.cy = (im.cy + dy).coerceIn(0f, 1f)
                            im.widthFrac = (im.widthFrac * z).coerceIn(0.02f, 2f)
                            im.rotation = (im.rotation + r) % 360f
                        }
                    } else {
                        mutateSelectedLayer(1000L) { l ->
                            l.cx = (l.cx + dx).coerceIn(0f, 1f)
                            l.cy = (l.cy + dy).coerceIn(0f, 1f)
                            l.scale = (l.scale * z).coerceIn(0.2f, 10f)
                            l.rotation = (l.rotation + r) % 360f
                        }
                    }
                },
                onDoubleTapAt = { x, y ->
                    val hit = textLayerAt(x, y)
                    if (hit != null) { textSelectedId = hit.id; textImageSelectedId = null; textTick++; textEditOpen = true }
                    else {
                        val eff2 = textEffectiveSize()
                        val im2 = textImageLayers.lastOrNull { TextOverlayUtils.imageLayerHit(it, x, y, eff2[0], eff2[1]) }
                        if (im2 != null) { textImageSelectedId = im2.id; textSelectedId = null; textTick++ }
                        else textEditOpen = true
                    }
                },
                onDeleteImageLayer = { deleteImageLayer(it) },
                onMoveImageLayer = { id, dir -> moveImageLayer(id, dir) },
                onTapAt = { x, y ->
                    // drawAll draws image layers UNDER text, so the TOP layer is text: hit-test text
                    // first (textLayerAt walks topmost-first), then image layers (topmost first).
                    // (v0.16.6 briefly had images first - corrected here to match the real draw order.)
                    val hit = textLayerAt(x, y)
                    if (hit != null) { textSelectedId = hit.id; textImageSelectedId = null; textTick++; textSelTick++ }
                    else {
                        val eff = textEffectiveSize()
                        val im = textImageLayers.lastOrNull { TextOverlayUtils.imageLayerHit(it, x, y, eff[0], eff[1]) }
                        if (im != null) { textImageSelectedId = im.id; textSelectedId = null; textTick++; textSelTick++ }
                    }
                },
                onEditOpen = { textEditOpen = it },
                onUndo = { undoTextLayers() },
                onRedo = { redoTextLayers() },
                onFormat = { textFormat = it; textResultBytes = null; textSaved = false },
                onProcess = { processTextPhoto() },
                onSave = { saveTextPhoto() },
                cropRect = textCropRect,
                cropLabel = textCropLabel,
                cropAdjust = textCropAdjust,
                canvasDims = textEffectiveSize().let { "${it[0]} × ${it[1]} px" },
                onSetCrop = { label, rw, rh -> setTextCrop(label, rw, rh) },
                onClearCrop = { clearTextCrop() },
                onCropAdjust = { on ->
                    textCropZoomAbs = 1f
                    if (on && textCropRect == null) {
                        // His "I want to zoom and crop" - Position must work WITHOUT picking a size first:
                        // start from the full-photo frame; pinch zooms in, drag pans, Done keeps the region.
                        textCropRect = floatArrayOf(0f, 0f, 1f, 1f)
                        textCropLabel = "Custom"
                    }
                    if (!on && textCropLabel == "Custom" && textCropRect != null) {
                        val r = textCropRect!!
                        val untouched = kotlin.math.abs(r[0]) < 0.001f && kotlin.math.abs(r[1]) < 0.001f && kotlin.math.abs(r[2] - 1f) < 0.001f && kotlin.math.abs(r[3] - 1f) < 0.001f
                        if (untouched) { textCropRect = null; textCropLabel = "" }
                    }
                    textCropAdjust = on
                    textTick++
                },
                onCropNudge = { dx, dy -> nudgeTextCrop(dx, dy) },
                onCropZoom = { z -> pinchTextCropZoom(z) },
                cropZoomAbs = textCropZoomAbs,
                onCropZoomAbs = { z -> setTextCropZoomAbs(z) },
                onPickColourAt = { x, y, t -> pickColourAt(x, y, t) },
                onFlipSelected = { flipSelectedLayer() },
                onCloneSelected = { cloneSelectedLayer() },
                onAddEmoji = { e -> addEmojiTextLayer(e) },
                onAddSticker = { k, c -> addStickerLayer(k, c) },
                onApplyMeme = { t, b -> applyMeme(t, b) },
                onRemoveBgImageLayer = { removeBgOnSelectedImageLayer() },
                stylePresets = textStylePresets,
                onLoadStyles = { refreshTextStylePresets() },
                onApplyStyle = { p -> applyTextStylePreset(p) },
                onSaveStyle = { name -> saveTextStylePreset(name) }
            
        )
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
            initialCustomW = inputWidth,
            initialCustomH = inputHeight,
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

    // v0.20.0: Rate us + Share the app (his drawer/settings steering). The store page resolves once the app is published.
    fun openPlayStorePage() {
        try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.packageName))) }
        catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName))) }
    }
    // v0.20.0: "Need support? Email us" - his confirmed support address (11:29).
    fun emailSupport() {
        val mail = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:bittyjee1223@gmail.com")).apply {
            putExtra(Intent.EXTRA_SUBJECT, "A2Z Image Craft support")
        }
        try { context.startActivity(mail) } catch (e: Exception) { scope.launch { snackbar.showSnackbar("No email app found on this phone.") } }
    }
    fun shareApp() {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out A2Z Image Craft - a free photo toolkit: resize, crop, convert, collage, text on photo and more, all on your phone.\nhttps://play.google.com/store/apps/details?id=" + context.packageName)
        }
        context.startActivity(Intent.createChooser(send, "Share A2Z Image Craft"))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            if (screen == Screen.Editor || screen == Screen.Export || screen == Screen.PrintHome || screen == Screen.PrintSetup || screen == Screen.PrintPreview || screen == Screen.PrintExport || screen == Screen.QueueOverview || screen == Screen.QueueResult || screen == Screen.Success || screen == Screen.SocialHome || screen == Screen.SocialPlatform || screen == Screen.SocialEdit || screen == Screen.ShopHome || screen == Screen.ShopOther || screen == Screen.ShopPlatform || screen == Screen.ShopEdit || screen == Screen.ShopCollage || screen == Screen.ConvertHome || screen == Screen.ConvertEdit || screen == Screen.ConvertResult || screen == Screen.CompressHome || screen == Screen.CompressSettings || screen == Screen.CompressResult || screen == Screen.Collage || screen == Screen.PhotoAdjust) {
                TopAppBar(
                    title = {
                        Text(
                            when (screen) {
                                Screen.Editor -> if (tool == Tool.Resize) "Resize Image" else tool.label
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
                                Screen.TextPhoto -> "Text on Photo"
                                Screen.Collage -> "Collage Maker"
                                Screen.PhotoAdjust -> "Photo Adjustments"
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
                                Screen.TextPhoto -> { resetTextPhoto(); screen = Screen.Home }
                                Screen.CompressHome -> { resetCompress(); screen = Screen.Home }
                                Screen.Collage -> { collage.resetToNewPhotos(); screen = Screen.Home }
                                Screen.PhotoAdjust -> { adjust.resetToNewPhoto(); screen = Screen.Home }
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
                        } else if (screen == Screen.TextPhoto) {
                            Text(
                                "Reset",
                                modifier = Modifier
                                    .clickable { resetTextPhoto() }
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
                    onOpenDrawer = { drawerOpen = true },
                    onOpenSettings = { tab = MainTab.Settings },
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
                            "Text on Photo" -> { resetTextPhoto(); screen = Screen.TextPhoto }
                            "Collage Maker" -> {
                                collage.saved = false
                                screen = Screen.Collage
                            }
                            "Photo Adjustments" -> {
                                adjust.saved = false
                                if (adjust.base == null) photoAdjustPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                else screen = Screen.PhotoAdjust
                            }
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
                    textStyles = textStylePresets,
                    onRefreshTextStyles = { refreshTextStylePresets() },
                    onTextStyle = { p ->
                        if (textPhotoBitmap != null) {
                            applyTextStylePreset(p)
                        } else {
                            resetTextPhoto()
                            pendingTextStyle = p
                        }
                        tab = MainTab.Home
                        screen = Screen.TextPhoto
                    },
                    onDeleteTextStyle = { id ->
                        scope.launch(Dispatchers.IO) {
                            TextStylePresetsStore.remove(context, id)
                            refreshTextStylePresets()
                        }
                    },
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
                MainTab.Settings -> SettingsScreen(
                    Modifier.padding(padding),
                    darkMode = darkMode,
                    onDarkMode = onDarkMode,
                    doc = settingsDoc,
                    onOpenDoc = { settingsDoc = it },
                    onEmail = { emailSupport() },
                    onRate = { openPlayStorePage() },
                    onShare = { shareApp() }
                )
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
            Screen.Collage -> CollageMakerFlow(
                modifier = Modifier.padding(padding),
                state = collage,
                busy = busy,
                hasProject = collageHasProject,
                onPick = { collageAppend = false; collageMakerPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onAddMore = { collageAppend = true; collageMakerPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onReplace = { collageReplacePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onSaveProject = { saveCollageProject() },
                onOpenProject = { loadCollageProject() },
                onSave = { saveCollageMaker() }
            )
            Screen.PhotoAdjust -> PhotoAdjustScreen(
                modifier = Modifier.padding(padding),
                state = adjust,
                busy = busy,
                onPick = { photoAdjustPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onSave = { saveAdjustPhoto() }
            )
            Screen.TextPhoto -> RenderTextPhoto(Modifier.padding(padding))
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
                onPdfAddMarked = { addMarkedPrintPages() },
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

    // v0.20.0 (his steering): hamburger on Home opens this side drawer over whatever tab is showing.
    if (drawerOpen) {
        HomeDrawer(
            onClose = { drawerOpen = false },
            onEmail = { emailSupport() },
            onRate = { openPlayStorePage() },
            onShare = { shareApp() }
        )
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
private fun EffectsLevel(selected: TextOverlayUtils.TextLayer?, testSheetLevel: String?, tick: Int, onMutate: (Long, (TextOverlayUtils.TextLayer) -> Unit) -> Unit) {
                    // v0.16.8 Text Effects Pack (his "Yes all 8"): gradient, neon/glow, hollow + ready-made styles.
                    Text("Text Effects", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(if (selected == null) "Tap a text layer on the photo first, then come back here." else "One tap changes the whole look. The preview on your photo is exactly what gets saved.", color = Slate, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Ready-made styles", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    TextOverlayUtils.EFFECT_TEMPLATES.chunked(2).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            rowItems.forEach { tpl ->
                                OutlinedButton(
                                    onClick = { onMutate(0L) { l -> TextOverlayUtils.applyTemplate(l, tpl.name) } },
                                    enabled = selected != null,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                ) {
                                    Text("Ag", color = Color(tpl.previewArgb), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(tpl.name, color = DeepNavy, fontSize = 12.sp, maxLines = 1)
                                }
                            }
                            repeat(2 - rowItems.size) { Spacer(Modifier.weight(1f).padding(end = 8.dp)) }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Effect", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        TextOverlayUtils.TextEffect.values().forEach { eff ->
                            val selEff = selected?.effect == eff
                            OutlinedButton(
                                onClick = { onMutate(0L) { l -> l.effect = eff } },
                                enabled = selected != null,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selEff) SkyBlue else Color.Transparent, contentColor = if (selEff) Color.White else DeepNavy),
                                modifier = Modifier.padding(end = 8.dp)
                            ) { Text(eff.label, fontSize = 12.sp) }
                        }
                    }
                    if (selected?.effect == TextOverlayUtils.TextEffect.GRADIENT) {
                        Spacer(Modifier.height(8.dp))
                        Text("Gradient colours", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            TextOverlayUtils.GRADIENT_PRESETS.forEach { gp ->
                                val selG = selected?.gradientStartArgb == gp.startArgb && selected?.gradientEndArgb == gp.endArgb
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 10.dp).clickable { onMutate(0L) { l -> l.gradientStartArgb = gp.startArgb; l.gradientEndArgb = gp.endArgb } }) {
                                    Box(Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(Color(gp.startArgb), Color(gp.endArgb)))).border(if (selG) 3.dp else 1.dp, if (selG) SkyBlue else BorderGrey, RoundedCornerShape(10.dp)))
                                    Spacer(Modifier.height(2.dp))
                                    Text(gp.label, color = if (selG) SkyBlue else DeepNavy, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    if (selected?.effect == TextOverlayUtils.TextEffect.NEON) {
                        Spacer(Modifier.height(8.dp))
                        Text("Glow colour", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            (TextOverlayUtils.QUICK_COLORS + TextOverlayUtils.EXTRA_COLORS.take(6)).forEach { c ->
                                val selC = selected?.glowColorArgb == c
                                Box(Modifier.padding(end = 10.dp).size(34.dp).clip(RoundedCornerShape(17.dp)).background(Color(c)).border(if (selC) 3.dp else 1.dp, if (selC) SkyBlue else BorderGrey, RoundedCornerShape(17.dp)).clickable { onMutate(0L) { l -> l.glowColorArgb = c } })
                            }
                        }
                    }
                    if (testSheetLevel != null) Text("hook:effect=" + (selected?.effect?.name ?: "none") + " t" + tick, fontSize = 8.sp, color = BorderGrey)}

@Composable
private fun StickersLevel(testSheetLevel: String?, layerCount: Int, imageCount: Int, tick: Int, onAddEmoji: (String) -> Unit, onAddSticker: (TextOverlayUtils.StickerKind, Int) -> Unit) {
                    // v0.16.8 (his "Yes all 8"): stickers & emoji - tap to add, then drag/resize/rotate on the photo.
                    Text("Stickers & Emoji", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Tap to add one - then drag, resize or rotate it on the photo. Shapes sit under your text; emoji act like text.", color = Slate, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Shapes", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        TextOverlayUtils.STICKERS.forEach { st ->
                            val stickerBmp = remember(st.kind) { TextOverlayUtils.makeStickerBitmap(st.kind, 128, st.colorArgb) }
                            OutlinedButton(
                                onClick = { onAddSticker(st.kind, st.colorArgb) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Image(bitmap = stickerBmp.asImageBitmap(), contentDescription = st.label, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(st.label, color = DeepNavy, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Emoji", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    var emojiCat by remember { mutableStateOf(0) }
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        TextOverlayUtils.EMOJI_CATEGORIES.forEachIndexed { i, cat ->
                            OutlinedButton(
                                onClick = { emojiCat = i },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (emojiCat == i) SkyBlue else Color.Transparent, contentColor = if (emojiCat == i) Color.White else DeepNavy),
                                modifier = Modifier.padding(end = 8.dp)
                            ) { Text(cat.first, fontSize = 12.sp) }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TextOverlayUtils.EMOJI_CATEGORIES[emojiCat.coerceIn(0, TextOverlayUtils.EMOJI_CATEGORIES.size - 1)].second.chunked(5).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                            rowItems.forEach { emoji ->
                                Text(emoji, fontSize = 26.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(1f).clickable { onAddEmoji(emoji) })
                            }
                            repeat(5 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                    if (testSheetLevel != null) Text("hook:layers=" + layerCount + ",images=" + imageCount + " t" + tick, fontSize = 8.sp, color = BorderGrey)}

/** v0.19.0: frame style thumbnail - drawn by the SAME drawFrame the photo preview and export use. */
@Composable
private fun FrameThumb(style: TextOverlayUtils.FrameStyle, color: Int, selected: Boolean, onClick: () -> Unit) {
    val tb = remember(style, color) {
        val b = Bitmap.createBitmap(96, 72, Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(b)
        val gp = android.graphics.Paint().apply {
            shader = android.graphics.LinearGradient(0f, 0f, 96f, 72f, 0xFF90CAF9.toInt(), 0xFF1565C0.toInt(), android.graphics.Shader.TileMode.CLAMP)
        }
        c.drawRect(0f, 0f, 96f, 72f, gp)
        TextOverlayUtils.drawFrame(c, 96, 72, style, 6f, color)
        b
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 10.dp).clickable { onClick() }) {
        Image(
            bitmap = tb.asImageBitmap(),
            contentDescription = style.label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).border(if (selected) 2.dp else 1.dp, if (selected) SkyBlue else BorderGrey, RoundedCornerShape(10.dp))
        )
        Spacer(Modifier.height(2.dp))
        Text(style.label, color = if (selected) SkyBlue else DeepNavy, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun MemeLevel(testSheetLevel: String?, layerCount: Int, imageCount: Int, tick: Int, onApplyMeme: (String, String) -> Unit) {
                    // v0.16.9 Meme Maker (his "Yes all 8" #8): classic top/bottom captions in one tap.
                    var memeTop by remember { mutableStateOf("") }
                    var memeBottom by remember { mutableStateOf("") }
                    Text("Meme Maker", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Type your lines - they land on the photo in the classic meme style. Drag them anywhere after.", color = Slate, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = memeTop, onValueChange = { memeTop = it }, singleLine = true, label = { Text("Top text") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = memeBottom, onValueChange = { memeBottom = it }, singleLine = true, label = { Text("Bottom text") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { onApplyMeme(memeTop, memeBottom) }, enabled = memeTop.isNotBlank() || memeBottom.isNotBlank(), modifier = Modifier.fillMaxWidth().height(46.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) { Text("Apply Meme Text", fontWeight = FontWeight.Bold) }
                    if (testSheetLevel != null) Text("hook:layers=" + layerCount + ",images=" + imageCount + " t" + tick, fontSize = 8.sp, color = BorderGrey)}

// ---------------------------------------------------------------------------
// v0.17.0 Collage Maker (his 10-section spec): Home -> Workspace -> Export.
// ONE engine (image/CollageMaker.kt) draws both the preview and the saved file,
// so what he sees is exactly what he gets. Deferred to v0.17.1: drag & drop between
// sections, rotation/flip controls, per-photo borders, gradients, transparent bg,
// margins, replace-in-section, save project, custom move/resize of sections.
// ---------------------------------------------------------------------------

private data class CollagePhotoSnap(
    val zoom: Float, val panX: Float, val panY: Float, val rotation: Float,
    val flipH: Boolean, val flipV: Boolean, val borderPx: Int, val borderColor: Int
)

private data class CollageSnap(
    val photos: List<CollagePhotoSnap>,
    val family: CollageMaker.LayoutFamily,
    val canvasW: Int, val canvasH: Int, val canvasLabel: String,
    val gapPx: Float, val marginPx: Float, val radiusPx: Float,
    val borderPx: Float, val borderColor: Int,
    val bgLabel: String, val bgCustom: Int, val gradientLabel: String
)

private class CollageUiState {
    val uris = mutableStateListOf<Uri>()
    val photos = mutableStateListOf<CollageMaker.Photo>()
    var family by mutableStateOf(CollageMaker.LayoutFamily.GRID)
    var level by mutableStateOf("home")          // home | edit | export
    var canvasW by mutableStateOf(1080)
    var canvasH by mutableStateOf(1080)
    var canvasLabel by mutableStateOf("Square 1080")
    var gapPx by mutableStateOf(8f)
    var marginPx by mutableStateOf(0f)
    var radiusPx by mutableStateOf(0f)
    var borderPx by mutableStateOf(0f)           // frame around EVERY photo
    var borderColor by mutableStateOf(0xFFFFFFFF.toInt())
    var bgLabel by mutableStateOf("White")       // White | Black | Colour | Gradient | Transparent
    var bgCustom by mutableStateOf(0xFF3D5AFE.toInt())
    var gradientLabel by mutableStateOf("Sunset")
    var selected by mutableStateOf(-1)
    var tick by mutableStateOf(0)                // drives recomposition when Photo fields change
    var saved by mutableStateOf(false)
    var format by mutableStateOf("JPG")          // JPG | PNG | WebP
    var quality by mutableStateOf(90f)
    var resLabel by mutableStateOf("Standard")   // Standard | High | Custom
    var customResW by mutableStateOf("1600")
    val undoStack = mutableListOf<CollageSnap>()
    val redoStack = mutableListOf<CollageSnap>()

    fun bgColor(): Int = when (bgLabel) {
        "White" -> android.graphics.Color.WHITE
        "Black" -> android.graphics.Color.BLACK
        "Colour" -> bgCustom
        else -> android.graphics.Color.WHITE   // Gradient/Transparent handled via gradient/alpha
    }

    fun gradientColors(): IntArray = when (gradientLabel) {
        "Ocean" -> intArrayOf(0xFF42BFEF.toInt(), 0xFF3D5AFE.toInt())
        "Mint" -> intArrayOf(0xFF3ECF8E.toInt(), 0xFF42BFEF.toInt())
        "Berry" -> intArrayOf(0xFF9B5DE5.toInt(), 0xFFF95F6A.toInt())
        "Dusk" -> intArrayOf(0xFF172B4D.toInt(), 0xFF3D5AFE.toInt())
        else -> intArrayOf(0xFFFF8A32.toInt(), 0xFFF95F6A.toInt())   // Sunset
    }

    /** Style for a render at [scale] times the canvas size (preview < 1, export >= 1). */
    fun style(scale: Float = 1f) = CollageMaker.Style(
        gapPx = (gapPx * scale).roundToInt(),
        marginPx = (marginPx * scale).roundToInt(),
        radiusPx = (radiusPx * scale).roundToInt(),
        borderPx = (borderPx * scale).roundToInt(),
        borderColor = borderColor,
        bgColor = if (bgLabel == "Transparent") 0 else bgColor(),
        bgGradient = if (bgLabel == "Gradient") gradientColors() else null
    )

    fun sections(): List<CollageMaker.Section> =
        CollageMaker.sections(family, photos.size.coerceAtLeast(2), canvasW, canvasH)

    fun exportSize(): Pair<Int, Int> = when (resLabel) {
        "High" -> {
            val w = canvasW * 2; val h = canvasH * 2
            if (maxOf(w, h) > 4096) { val f = 4096f / maxOf(w, h); (w * f).roundToInt() to (h * f).roundToInt() } else w to h
        }
        "Custom" -> {
            val w = customResW.toIntOrNull()?.coerceIn(256, 4096) ?: canvasW
            w to (w.toFloat() * canvasH / canvasW).roundToInt()
        }
        else -> canvasW to canvasH
    }

    fun snapshot() = CollageSnap(
        photos.map { CollagePhotoSnap(it.zoom, it.panX, it.panY, it.rotation, it.flipH, it.flipV, it.borderPx, it.borderColor) },
        family, canvasW, canvasH, canvasLabel, gapPx, marginPx, radiusPx, borderPx, borderColor, bgLabel, bgCustom, gradientLabel
    )

    /** Save an undo point before a one-tap change (layout, canvas, background, reset). */
    fun push() {
        undoStack.add(snapshot())
        if (undoStack.size > 40) undoStack.removeAt(0)
        redoStack.clear()
    }

    private fun applySnap(s: CollageSnap) {
        s.photos.forEachIndexed { i, p ->
            photos.getOrNull(i)?.let { ph ->
                ph.zoom = p.zoom; ph.panX = p.panX; ph.panY = p.panY; ph.rotation = p.rotation
                ph.flipH = p.flipH; ph.flipV = p.flipV; ph.borderPx = p.borderPx; ph.borderColor = p.borderColor
            }
        }
        family = s.family; canvasW = s.canvasW; canvasH = s.canvasH; canvasLabel = s.canvasLabel
        gapPx = s.gapPx; marginPx = s.marginPx; radiusPx = s.radiusPx
        borderPx = s.borderPx; borderColor = s.borderColor
        bgLabel = s.bgLabel; bgCustom = s.bgCustom; gradientLabel = s.gradientLabel
        tick++; saved = false
    }

    fun undo() { if (undoStack.isNotEmpty()) { redoStack.add(snapshot()); applySnap(undoStack.removeAt(undoStack.lastIndex)) } }
    fun redo() { if (redoStack.isNotEmpty()) { undoStack.add(snapshot()); applySnap(redoStack.removeAt(redoStack.lastIndex)) } }

    /** v0.18.3 (his words): Reset starts from zero with a new-image option. Full wipe. */
    fun resetToNewPhotos() {
        uris.clear(); photos.clear()
        family = CollageMaker.LayoutFamily.GRID
        level = "home"
        canvasW = 1080; canvasH = 1080; canvasLabel = "Square 1080"
        gapPx = 8f; marginPx = 0f; radiusPx = 0f; borderPx = 0f; borderColor = 0xFFFFFFFF.toInt()
        bgLabel = "White"; bgCustom = 0xFF3D5AFE.toInt(); gradientLabel = "Sunset"
        selected = -1
        format = "JPG"; quality = 90f; resLabel = "Standard"; customResW = "1600"
        undoStack.clear(); redoStack.clear()
        tick++; saved = false
    }
}

@Composable
private fun CollageMakerFlow(
    modifier: Modifier,
    state: CollageUiState,
    busy: Boolean,
    hasProject: Boolean,
    onPick: () -> Unit,
    onAddMore: () -> Unit,
    onReplace: () -> Unit,
    onSaveProject: () -> Unit,
    onOpenProject: () -> Unit,
    onSave: () -> Unit
) {
    when (state.level) {
        "edit" -> CollageEditScreen(modifier, state, onReplace)
        "export" -> CollageExportScreen(modifier, state, busy, onSave)
        else -> CollageHomeScreen(modifier, state, hasProject, onPick, onAddMore, onSaveProject, onOpenProject)
    }
}

@Composable
private fun CollageMiniPreview(family: CollageMaker.LayoutFamily, active: Boolean) {
    val tint = if (active) Orange else Slate
    Canvas(Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 4.dp)) {
        val secs = CollageMaker.sections(family, 4, 100, 100)
        secs.forEach { s ->
            drawRoundRect(
                color = tint,
                topLeft = Offset(s.left * size.width + 2f, s.top * size.height + 2f),
                size = Size((s.width() * size.width - 4f).coerceAtLeast(4f), (s.height() * size.height - 4f).coerceAtLeast(4f)),
                cornerRadius = CornerRadius(6f, 6f)
            )
        }
    }
}

@Composable
private fun CollageChip(label: String, active: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = if (active) Orange else Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) Orange else BorderGrey)
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = if (active) Color.White else DeepNavy, fontSize = 12.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun CollageHomeScreen(modifier: Modifier, state: CollageUiState, hasProject: Boolean, onPick: () -> Unit, onAddMore: () -> Unit, onSaveProject: () -> Unit, onOpenProject: () -> Unit) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        Text("Photo Collage Maker", color = DeepNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Combine 2 to 20 photos into one collage - no watermark, fully on your phone.", color = Slate, fontSize = 13.sp)
        Spacer(Modifier.height(14.dp))

        Text("1. Choose a layout style", color = DeepNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val fams = listOf(
                CollageMaker.LayoutFamily.GRID to "Grid",
                CollageMaker.LayoutFamily.MIXED to "Mixed",
                CollageMaker.LayoutFamily.VERTICAL to "Vertical",
                CollageMaker.LayoutFamily.HORIZONTAL to "Horizontal"
            )
            fams.forEach { (fam, label) ->
                val active = state.family == fam
                Card(
                    modifier = Modifier.weight(1f).clickable { if (!active) { state.push(); state.family = fam; state.selected = -1; state.tick++; state.saved = false } },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(if (active) 2.dp else 1.dp, if (active) Orange else BorderGrey)
                ) {
                    Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CollageMiniPreview(fam, active)
                        Spacer(Modifier.height(4.dp))
                        Text(label, color = if (active) Orange else DeepNavy, fontSize = 10.sp, maxLines = 1, softWrap = false, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))

        Text("2. Add your photos (${state.photos.size}/20)", color = DeepNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (state.photos.isEmpty()) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, BorderGrey)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Add your photos", color = DeepNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("JPG, PNG or WebP. Pick 2 to 20 photos.", color = Slate, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onPick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Select 2-20 photos", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.photos.size) { i ->
                    val sel = state.selected == i
                    Image(
                        bitmap = state.photos[i].bitmap.asImageBitmap(),
                        contentDescription = "Photo ${i + 1}",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { state.selected = if (sel) -1 else i },
                        contentScale = ContentScale.Crop
                    )
                }
            }
            if (state.selected in state.photos.indices) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        val i = state.selected
                        if (i > 0) {
                            state.photos.add(i - 1, state.photos.removeAt(i))
                            if (i < state.uris.size) state.uris.add(i - 1, state.uris.removeAt(i))
                            state.selected = i - 1; state.tick++; state.saved = false
                        }
                    }, shape = RoundedCornerShape(10.dp)) { Text("< Move left", color = DeepNavy, fontSize = 12.sp) }
                    OutlinedButton(onClick = {
                        val i = state.selected
                        if (i in 0 until state.photos.size - 1) {
                            state.photos.add(i + 1, state.photos.removeAt(i))
                            if (i < state.uris.size) state.uris.add(i + 1, state.uris.removeAt(i))
                            state.selected = i + 1; state.tick++; state.saved = false
                        }
                    }, shape = RoundedCornerShape(10.dp)) { Text("Move right >", color = DeepNavy, fontSize = 12.sp) }
                    OutlinedButton(onClick = {
                        val i = state.selected
                        state.photos.removeAt(i)
                        if (i < state.uris.size) state.uris.removeAt(i)
                        state.selected = -1; state.tick++; state.saved = false
                    }, shape = RoundedCornerShape(10.dp)) { Text("Remove", color = Color(0xFFD64545), fontSize = 12.sp) }
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onAddMore, enabled = state.photos.size < 20, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Text(if (state.photos.size < 20) "Add more photos" else "20 photos maximum", color = DeepNavy)
            }
        }
        if (state.photos.size >= 2 || hasProject) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.photos.size >= 2) {
                    OutlinedButton(onClick = onSaveProject, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Save project", color = DeepNavy, fontSize = 12.sp) }
                }
                if (hasProject) {
                    OutlinedButton(onClick = onOpenProject, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Open saved project", color = DeepNavy, fontSize = 12.sp) }
                }
            }
        }
        Spacer(Modifier.height(14.dp))

        Text("3. Start creating", color = DeepNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { state.level = "edit" }, enabled = state.photos.size >= 2, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Text(if (state.photos.size >= 2) "Start Creating" else "Pick at least 2 photos first", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CollageEditScreen(modifier: Modifier, state: CollageUiState, onReplace: () -> Unit) {
    val sections = remember(state.family, state.photos.size, state.canvasW, state.canvasH) { state.sections() }
    val preview = remember(state.tick, state.photos.size, state.family, state.canvasW, state.canvasH, state.gapPx, state.radiusPx, state.bgLabel, state.bgCustom) {
        val sc = 720f / maxOf(state.canvasW, state.canvasH).toFloat()
        CollageMaker.render(state.photos, sections, (state.canvasW * sc).roundToInt(), (state.canvasH * sc).roundToInt(), state.style(sc))
    }
    // Slider drags push ONE undo point per drag (captured at the first change).
    var dragSnap by remember { mutableStateOf<CollageSnap?>(null) }
    fun beginDrag() { if (dragSnap == null) dragSnap = state.snapshot() }
    fun endDrag() { dragSnap?.let { state.undoStack.add(it); if (state.undoStack.size > 40) state.undoStack.removeAt(0); state.redoStack.clear() }; dragSnap = null }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { state.undo() }, enabled = state.undoStack.isNotEmpty(), modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Undo", color = DeepNavy, fontSize = 13.sp) }
            OutlinedButton(onClick = { state.redo() }, enabled = state.redoStack.isNotEmpty(), modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Redo", color = DeepNavy, fontSize = 13.sp) }
            OutlinedButton(onClick = { state.resetToNewPhotos() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Reset", color = DeepNavy, fontSize = 13.sp) }
        }
        Spacer(Modifier.height(10.dp))

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                bitmap = preview.asImageBitmap(),
                contentDescription = "Collage preview",
                modifier = Modifier
                    .fillMaxWidth(if (state.canvasW >= state.canvasH) 1f else 0.72f)
                    .aspectRatio(state.canvasW.toFloat() / state.canvasH.toFloat())
                    .clip(RoundedCornerShape(8.dp))
                    .pointerInput(sections) {
                        // Long-press then drag onto another frame swaps the two photos.
                        var dragPos = Offset.Zero
                        detectDragGesturesAfterLongPress(
                            onDragStart = { off -> dragPos = off; beginDrag() },
                            onDragEnd = {
                                val fx = dragPos.x / size.width; val fy = dragPos.y / size.height
                                val from = state.selected
                                val to = sections.indexOfFirst { fx >= it.left && fx <= it.right && fy >= it.top && fy <= it.bottom }
                                if (from in state.photos.indices && to in state.photos.indices && from != to) {
                                    val tmp = state.photos[from]; state.photos[from] = state.photos[to]; state.photos[to] = tmp
                                    if (from < state.uris.size && to < state.uris.size) {
                                        val tu = state.uris[from]; state.uris[from] = state.uris[to]; state.uris[to] = tu
                                    }
                                    state.selected = to; state.saved = false
                                    dragSnap?.let { state.undoStack.add(it); state.redoStack.clear() }
                                }
                                dragSnap = null
                                state.tick++
                            },
                            onDragCancel = { dragSnap = null }
                        ) { change, drag ->
                            change.consume()
                            dragPos += drag
                        }
                    }
                    .pointerInput(sections) {
                        detectTapGestures { off ->
                            val fx = off.x / size.width; val fy = off.y / size.height
                            val idx = sections.indexOfFirst { fx >= it.left && fx <= it.right && fy >= it.top && fy <= it.bottom }
                            if (idx >= 0) state.selected = idx
                        }
                    }
                    .pointerInput(state.selected) {
                        detectDragGestures(
                            onDragStart = { beginDrag() },
                            onDragEnd = { endDrag() },
                            onDragCancel = { endDrag() }
                        ) { change, drag ->
                            change.consume()
                            val ph = state.photos.getOrNull(state.selected) ?: return@detectDragGestures
                            ph.panX = (ph.panX + drag.x / size.width * 2f).coerceIn(-1f, 1f)
                            ph.panY = (ph.panY + drag.y / size.height * 2f).coerceIn(-1f, 1f)
                            state.tick++; state.saved = false
                        }
                    },
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(4.dp))
        Text("Tap a photo to select it. Drag to move it in its frame. Hold and drop on another frame to swap.", color = Slate, fontSize = 12.sp)

        val sel = state.photos.getOrNull(state.selected)
        if (sel != null) {
            Spacer(Modifier.height(8.dp))
            Text("Zoom - photo ${state.selected + 1} (${"%.1f".format(sel.zoom)}x)", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Slider(
                value = sel.zoom.coerceIn(1f, 4f),
                onValueChange = { beginDrag(); sel.zoom = it; state.tick++; state.saved = false },
                onValueChangeFinished = { endDrag() },
                valueRange = 1f..4f
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = { state.push(); sel.rotation = (sel.rotation + 90f) % 360f; state.tick++; state.saved = false }, shape = RoundedCornerShape(10.dp)) { Text("Rotate 90°", color = DeepNavy, fontSize = 12.sp) }
                OutlinedButton(onClick = { state.push(); sel.flipH = !sel.flipH; state.tick++; state.saved = false }, shape = RoundedCornerShape(10.dp)) { Text("Flip H", color = DeepNavy, fontSize = 12.sp) }
                OutlinedButton(onClick = { state.push(); sel.flipV = !sel.flipV; state.tick++; state.saved = false }, shape = RoundedCornerShape(10.dp)) { Text("Flip V", color = DeepNavy, fontSize = 12.sp) }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = onReplace, shape = RoundedCornerShape(10.dp)) { Text("Replace photo ${state.selected + 1}", color = DeepNavy, fontSize = 12.sp) }
                OutlinedButton(onClick = { state.push(); sel.zoom = 1f; sel.panX = 0f; sel.panY = 0f; sel.rotation = 0f; sel.flipH = false; sel.flipV = false; sel.borderPx = -1; state.tick++; state.saved = false }, shape = RoundedCornerShape(10.dp)) { Text("Reset photo", color = DeepNavy, fontSize = 12.sp) }
            }
            Spacer(Modifier.height(6.dp))
            Text("Frame - photo ${state.selected + 1} (${if (sel.borderPx < 0) "same as all" else sel.borderPx.toString()})", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Slider(
                value = if (sel.borderPx < 0) 0f else sel.borderPx.toFloat(),
                onValueChange = { beginDrag(); sel.borderPx = it.roundToInt(); state.tick++; state.saved = false },
                onValueChangeFinished = { endDrag() },
                valueRange = 0f..20f
            )
            if (sel.borderPx > 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0xFFFFFFFF, 0xFF000000, 0xFFF95F6A, 0xFFFF8A32, 0xFFFFD166, 0xFF3ECF8E, 0xFF42BFEF, 0xFF3D5AFE).forEach { cl ->
                        val ci = cl.toInt()
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(cl))
                                .clickable { state.push(); sel.borderColor = ci; state.tick++; state.saved = false }
                        ) {
                            if (sel.borderColor == ci) Text(if (ci == 0xFFFFFFFF.toInt()) "✓" else "✓", color = if (ci == 0xFFFFFFFF.toInt()) DeepNavy else Color.White, modifier = Modifier.align(Alignment.Center), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Spacing between photos (${state.gapPx.roundToInt()})", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Slider(
            value = state.gapPx,
            onValueChange = { beginDrag(); state.gapPx = it; state.tick++; state.saved = false },
            onValueChangeFinished = { endDrag() },
            valueRange = 0f..40f
        )
        Text("Rounded corners (${state.radiusPx.roundToInt()})", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Slider(
            value = state.radiusPx,
            onValueChange = { beginDrag(); state.radiusPx = it; state.tick++; state.saved = false },
            onValueChangeFinished = { endDrag() },
            valueRange = 0f..60f
        )
        Text("Margin around the collage (${state.marginPx.roundToInt()})", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Slider(
            value = state.marginPx,
            onValueChange = { beginDrag(); state.marginPx = it; state.tick++; state.saved = false },
            onValueChangeFinished = { endDrag() },
            valueRange = 0f..60f
        )
        Text("Frame around every photo (${state.borderPx.roundToInt()})", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Slider(
            value = state.borderPx,
            onValueChange = { beginDrag(); state.borderPx = it; state.tick++; state.saved = false },
            onValueChangeFinished = { endDrag() },
            valueRange = 0f..20f
        )
        if (state.borderPx > 0f) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0xFFFFFFFF, 0xFF000000, 0xFFF95F6A, 0xFFFF8A32, 0xFFFFD166, 0xFF3ECF8E, 0xFF42BFEF, 0xFF3D5AFE).forEach { cl ->
                    val ci = cl.toInt()
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(cl))
                            .clickable { state.push(); state.borderColor = ci; state.tick++; state.saved = false }
                    ) {
                        if (state.borderColor == ci) Text("✓", color = if (ci == 0xFFFFFFFF.toInt()) DeepNavy else Color.White, modifier = Modifier.align(Alignment.Center), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Canvas size", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CollageChip("Square", state.canvasLabel == "Square 1080") { state.push(); state.canvasLabel = "Square 1080"; state.canvasW = 1080; state.canvasH = 1080 }
            CollageChip("Portrait", state.canvasLabel == "Portrait 1080x1350") { state.push(); state.canvasLabel = "Portrait 1080x1350"; state.canvasW = 1080; state.canvasH = 1350 }
            CollageChip("Story", state.canvasLabel == "Story 1080x1920") { state.push(); state.canvasLabel = "Story 1080x1920"; state.canvasW = 1080; state.canvasH = 1920 }
            CollageChip("Wide", state.canvasLabel == "Landscape 1920x1080") { state.push(); state.canvasLabel = "Landscape 1920x1080"; state.canvasW = 1920; state.canvasH = 1080 }
        }
        Text(state.canvasLabel, color = Slate, fontSize = 11.sp)

        Spacer(Modifier.height(8.dp))
        Text("Background", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.height(38.dp)) {
            item { CollageChip("White", state.bgLabel == "White") { state.push(); state.bgLabel = "White" } }
            item { CollageChip("Black", state.bgLabel == "Black") { state.push(); state.bgLabel = "Black" } }
            item { CollageChip("Colour", state.bgLabel == "Colour") { state.push(); state.bgLabel = "Colour" } }
            item { CollageChip("Gradient", state.bgLabel == "Gradient") { state.push(); state.bgLabel = "Gradient" } }
            item { CollageChip("None", state.bgLabel == "Transparent") { state.push(); state.bgLabel = "Transparent" } }
        }
        if (state.bgLabel == "Transparent") {
            Spacer(Modifier.height(4.dp))
            Text("Transparent background stays transparent in PNG and WebP exports (JPG saves on white).", color = Slate, fontSize = 11.sp)
        }
        if (state.bgLabel == "Gradient") {
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Sunset" to listOf(0xFFFF8A32, 0xFFF95F6A), "Ocean" to listOf(0xFF42BFEF, 0xFF3D5AFE), "Mint" to listOf(0xFF3ECF8E, 0xFF42BFEF), "Berry" to listOf(0xFF9B5DE5, 0xFFF95F6A), "Dusk" to listOf(0xFF172B4D, 0xFF3D5AFE)).forEach { (name, cols) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(androidx.compose.ui.graphics.Brush.verticalGradient(cols.map { Color(it) }))
                                .clickable { state.push(); state.gradientLabel = name }
                        ) {
                            if (state.gradientLabel == name) Text("✓", color = Color.White, modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold)
                        }
                        Text(name, color = Slate, fontSize = 10.sp)
                    }
                }
            }
        }
        if (state.bgLabel == "Colour") {
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0xFFF95F6A, 0xFFFF8A32, 0xFFFFD166, 0xFF3ECF8E, 0xFF42BFEF, 0xFF3D5AFE, 0xFF9B5DE5).forEach { c ->
                    val col = Color(c.toLong() or 0xFF000000L)
                    Box(
                        Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(col)
                            .clickable { state.push(); state.bgCustom = c.toInt() }
                    ) {
                        if (state.bgCustom == c.toInt()) Text("✓", color = Color.White, modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        var showFullPreview by remember { mutableStateOf(false) }
        OutlinedButton(onClick = { showFullPreview = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("View full screen", color = DeepNavy) }
        if (showFullPreview) {
            Dialog(onDismissRequest = { showFullPreview = false }) {
                val full = remember {
                    val sc = 1400f / maxOf(state.canvasW, state.canvasH).toFloat()
                    CollageMaker.render(state.photos, sections, (state.canvasW * sc).roundToInt(), (state.canvasH * sc).roundToInt(), state.style(sc))
                }
                Box(Modifier.fillMaxSize().background(Color(0xCC000000)).clickable { showFullPreview = false }, contentAlignment = Alignment.Center) {
                    Image(
                        bitmap = full.asImageBitmap(),
                        contentDescription = "Full screen collage preview",
                        modifier = Modifier.fillMaxWidth().aspectRatio(state.canvasW.toFloat() / state.canvasH.toFloat()),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = { state.level = "export" }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Text("Continue to Export", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { state.level = "home" }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Back to photos & layout", color = DeepNavy) }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CollageExportScreen(modifier: Modifier, state: CollageUiState, busy: Boolean, onSave: () -> Unit) {
    val sections = remember(state.family, state.photos.size, state.canvasW, state.canvasH) { state.sections() }
    val preview = remember(state.tick, state.photos.size, state.family, state.canvasW, state.canvasH, state.gapPx, state.radiusPx, state.bgLabel, state.bgCustom) {
        val sc = 420f / maxOf(state.canvasW, state.canvasH).toFloat()
        CollageMaker.render(state.photos, sections, (state.canvasW * sc).roundToInt(), (state.canvasH * sc).roundToInt(), state.style(sc))
    }
    val (ew, eh) = state.exportSize()
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                bitmap = preview.asImageBitmap(),
                contentDescription = "Collage export preview",
                modifier = Modifier
                    .fillMaxWidth(if (state.canvasW >= state.canvasH) 0.8f else 0.55f)
                    .aspectRatio(state.canvasW.toFloat() / state.canvasH.toFloat())
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(12.dp))

        Text("File format", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CollageChip("JPG", state.format == "JPG") { state.format = "JPG"; state.saved = false }
            CollageChip("PNG", state.format == "PNG") { state.format = "PNG"; state.saved = false }
            CollageChip("WebP", state.format == "WebP") { state.format = "WebP"; state.saved = false }
        }
        if (state.format != "PNG") {
            Spacer(Modifier.height(8.dp))
            Text("Quality (${state.quality.roundToInt()}%)", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Slider(value = state.quality, onValueChange = { state.quality = it; state.saved = false }, valueRange = 50f..100f)
        }

        Spacer(Modifier.height(8.dp))
        Text("Resolution", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CollageChip("Standard", state.resLabel == "Standard") { state.resLabel = "Standard"; state.saved = false }
            CollageChip("High (2x)", state.resLabel == "High") { state.resLabel = "High"; state.saved = false }
            CollageChip("Custom", state.resLabel == "Custom") { state.resLabel = "Custom"; state.saved = false }
        }
        if (state.resLabel == "Custom") {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.customResW,
                    onValueChange = { state.customResW = it.filter { ch -> ch.isDigit() }.take(4); state.saved = false },
                    label = { Text("Width in px") },
                    modifier = Modifier.width(140.dp)
                )
                Text("256 to 4096, height follows", color = Slate, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("Will save: $ew x $eh ${state.format}", color = Slate, fontSize = 12.sp)

        Spacer(Modifier.height(14.dp))
        Button(onClick = onSave, enabled = !busy && !state.saved, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Text(if (state.saved) "Saved ✓ - see your gallery" else "Save Collage", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { state.level = "edit" }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Back to editing", color = DeepNavy) }
        Spacer(Modifier.height(16.dp))
    }
}

// ---------------------------------------------------------------------------
// v0.18.0 Photo Adjustments (his spec): 12 sliders, non-destructive, live preview,
// undo/redo/compare/reset, JPG/PNG/WebP export. One engine (image/PhotoAdjust.kt)
// feeds preview and save. His mockup's extra tiles: Filters reuses the app's filter
// engine inside this screen; Crop & Text already have their own full tools on Home;
// Draw/Shapes/Frames/Blur are a separate split (reported, not silently dropped).
// ---------------------------------------------------------------------------

private class AdjustUiState {
    var uri by mutableStateOf<Uri?>(null)
    var base by mutableStateOf<Bitmap?>(null)      // edit copy (capped 1600px)
    var origW by mutableStateOf(0)
    var origH by mutableStateOf(0)
    val params = PhotoAdjust.Params()
    var filterId by mutableStateOf("normal")
    var panel by mutableStateOf("adjust")          // adjust | filters
    var compare by mutableStateOf(false)
    var tick by mutableStateOf(0)
    var saved by mutableStateOf(false)
    var format by mutableStateOf("JPG")
    var quality by mutableStateOf(90f)
    var resLabel by mutableStateOf("Original")     // Original | Custom
    var customW by mutableStateOf("1600")
    val undoStack = mutableListOf<Pair<PhotoAdjust.Params, String>>()
    val redoStack = mutableListOf<Pair<PhotoAdjust.Params, String>>()

    fun snapshot() = params.copy() to filterId

    fun push() {
        undoStack.add(snapshot())
        if (undoStack.size > 40) undoStack.removeAt(0)
        redoStack.clear()
    }

    private fun applySnap(snap: Pair<PhotoAdjust.Params, String>) {
        params.copyFrom(snap.first); filterId = snap.second; tick++; saved = false
    }

    fun undo() { if (undoStack.isNotEmpty()) { redoStack.add(snapshot()); applySnap(undoStack.removeAt(undoStack.lastIndex)) } }
    fun redo() { if (redoStack.isNotEmpty()) { undoStack.add(snapshot()); applySnap(redoStack.removeAt(redoStack.lastIndex)) } }

    /** v0.18.3 (his words): Reset starts from zero with a new-image option. Full wipe. */
    fun resetToNewPhoto() {
        uri = null; base = null; origW = 0; origH = 0
        params.copyFrom(PhotoAdjust.Params())
        filterId = "normal"; panel = "adjust"; compare = false
        format = "JPG"; quality = 90f; resLabel = "Original"; customW = "1600"
        undoStack.clear(); redoStack.clear()
        tick++; saved = false
    }
}

@Composable
private fun AdjustSlider(
    label: String,
    value: Float,
    onBegin: () -> Unit,
    onChange: (Float) -> Unit,
    onEnd: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, modifier = Modifier.weight(1f), color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("-100", color = Slate, fontSize = 10.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                value.roundToInt().toString(),
                color = if (value == 0f) Slate else Orange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { if (value != 0f) { onBegin(); onChange(0f); onEnd() } }
            )
            Spacer(Modifier.width(8.dp))
            Text("+100", color = Slate, fontSize = 10.sp)
        }
        Slider(
            value = value,
            onValueChange = { onBegin(); onChange(it) },
            onValueChangeFinished = { onEnd() },
            valueRange = -100f..100f
        )
    }
}

@Composable
private fun PhotoAdjustScreen(
    modifier: Modifier,
    state: AdjustUiState,
    busy: Boolean,
    onPick: () -> Unit,
    onSave: () -> Unit
) {
    val base = state.base
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        if (base == null) {
            Text("Adjust your photo", color = DeepNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Light, colour and detail sliders with a live preview. Your original photo is never changed.", color = Slate, fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            Button(onClick = onPick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Choose a photo", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            return@Column
        }

        // Slider drags push ONE undo point per drag (captured at the first change).
        var dragSnap by remember { mutableStateOf<Pair<PhotoAdjust.Params, String>?>(null) }
        fun beginDrag() { if (dragSnap == null) dragSnap = state.snapshot() }
        fun endDrag() { dragSnap?.let { state.undoStack.add(it); if (state.undoStack.size > 40) state.undoStack.removeAt(0); state.redoStack.clear() }; dragSnap = null }

        val preview = remember(state.tick, state.compare) {
            val filtered = if (state.filterId == "normal") base else FilterUtils.applyFilter(base, state.filterId)
            // v0.18.4: Compare shows the TRUE original - before filter AND sliders (his report:
            // with only a filter applied, the old compare view still showed the filter, so the
            // toggle changed nothing on screen and read as "compare button not working").
            val adjusted = if (state.compare) base else PhotoAdjust.apply(filtered, state.params)
            val sc = 720f / maxOf(adjusted.width, adjusted.height).toFloat()
            if (sc < 1f) Bitmap.createScaledBitmap(adjusted, (adjusted.width * sc).roundToInt(), (adjusted.height * sc).roundToInt(), true) else adjusted
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                bitmap = preview.asImageBitmap(),
                contentDescription = "Photo preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(base.width.toFloat() / base.height.toFloat())
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(4.dp))
        Text("Original: ${state.origW} x ${state.origH}" + if (state.compare) " - showing the original" else "", color = Slate, fontSize = 12.sp)

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedButton(onClick = { state.undo() }, enabled = state.undoStack.isNotEmpty(), modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 2.dp)) { Text("Undo", color = DeepNavy, fontSize = 12.sp, maxLines = 1, softWrap = false) }
            OutlinedButton(onClick = { state.redo() }, enabled = state.redoStack.isNotEmpty(), modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 2.dp)) { Text("Redo", color = DeepNavy, fontSize = 12.sp, maxLines = 1, softWrap = false) }
            OutlinedButton(onClick = { state.compare = !state.compare }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 2.dp)) { Text(if (state.compare) "Edited" else "Compare", color = if (state.compare) Orange else DeepNavy, fontSize = 11.sp, maxLines = 1, softWrap = false) }
            OutlinedButton(onClick = { state.resetToNewPhoto() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 2.dp)) { Text("Reset", color = DeepNavy, fontSize = 12.sp, maxLines = 1, softWrap = false) }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("adjust" to "Adjust", "filters" to "Filters").forEach { (id, label) ->
                val active = state.panel == id
                Card(
                    modifier = Modifier.weight(1f).clickable { state.panel = id },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(if (active) 2.dp else 1.dp, if (active) Orange else BorderGrey)
                ) {
                    Text(label, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), color = if (active) Orange else DeepNavy, fontSize = 13.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        if (state.panel == "filters") {
            Text("One-tap filters - sliders still apply on top.", color = Slate, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.heightIn(max = 40.dp)) {
                items(FilterUtils.FilterChoice.values().toList()) { choice ->
                    CollageChip(choice.label, state.filterId == choice.id) { state.push(); state.filterId = choice.id; state.tick++ }
                }
            }
        } else {
            Text("Tap a value to reset that slider.", color = Slate, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text("Light", color = DeepNavy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            AdjustSlider("Brightness", state.params.brightness, ::beginDrag, { state.params.brightness = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Exposure", state.params.exposure, ::beginDrag, { state.params.exposure = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Contrast", state.params.contrast, ::beginDrag, { state.params.contrast = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Highlights", state.params.highlights, ::beginDrag, { state.params.highlights = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Shadows", state.params.shadows, ::beginDrag, { state.params.shadows = it; state.tick++; state.saved = false }, ::endDrag)
            Spacer(Modifier.height(6.dp))
            Text("Colour", color = DeepNavy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            AdjustSlider("Saturation", state.params.saturation, ::beginDrag, { state.params.saturation = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Vibrance", state.params.vibrance, ::beginDrag, { state.params.vibrance = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Temperature", state.params.temperature, ::beginDrag, { state.params.temperature = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Tint", state.params.tint, ::beginDrag, { state.params.tint = it; state.tick++; state.saved = false }, ::endDrag)
            Spacer(Modifier.height(6.dp))
            Text("Details", color = DeepNavy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            AdjustSlider("Sharpness", state.params.sharpness, ::beginDrag, { state.params.sharpness = it; state.tick++; state.saved = false }, ::endDrag)
            AdjustSlider("Clarity", state.params.clarity, ::beginDrag, { state.params.clarity = it; state.tick++; state.saved = false }, ::endDrag)
            Spacer(Modifier.height(6.dp))
            Text("Creative", color = DeepNavy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            AdjustSlider("Fade", state.params.fade, ::beginDrag, { state.params.fade = it; state.tick++; state.saved = false }, ::endDrag)
        }

        Spacer(Modifier.height(12.dp))
        Text("Save settings", color = DeepNavy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CollageChip("JPG", state.format == "JPG") { state.format = "JPG"; state.saved = false }
            CollageChip("PNG", state.format == "PNG") { state.format = "PNG"; state.saved = false }
            CollageChip("WebP", state.format == "WebP") { state.format = "WebP"; state.saved = false }
        }
        if (state.format != "PNG") {
            Text("Quality (${state.quality.roundToInt()}%)", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Slider(value = state.quality, onValueChange = { state.quality = it; state.saved = false }, valueRange = 50f..100f)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CollageChip("Original size", state.resLabel == "Original") { state.resLabel = "Original"; state.saved = false }
            CollageChip("Custom size", state.resLabel == "Custom") { state.resLabel = "Custom"; state.saved = false }
        }
        if (state.resLabel == "Custom") {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.customW,
                    onValueChange = { state.customW = it.filter { ch -> ch.isDigit() }.take(4); state.saved = false },
                    label = { Text("Width in px") },
                    modifier = Modifier.width(140.dp)
                )
                Text("256 to 4096, height follows", color = Slate, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(14.dp))
        Button(onClick = onSave, enabled = !busy && !state.saved, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Text(if (state.saved) "Saved ✓ - see your gallery" else "Save Image", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onPick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Choose a different photo", color = DeepNavy) }
        Spacer(Modifier.height(16.dp))
    }
}


@Composable
private fun HomeScreen(
    modifier: Modifier,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
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
        Feature("Text on Photo", "Add text to images", "T"),
        Feature("Collage Maker", "Combine 2-20 photos", "⊞"),
        Feature("Photo Adjustments", "Light, colour & detail sliders", "◐"),
        Feature("Print Size & DPI", "Ready images for printing", "◉")
    )
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            // v0.20.0 (his reference app): hamburger top-left opens the side drawer, gear top-right opens Settings.
            IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, contentDescription = "Menu", tint = DeepNavy, modifier = Modifier.size(26.dp)) }
            Column(Modifier.weight(1f)) {
                Text("A2Z Image Craft", color = DeepNavy, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("PHOTO TOOLKIT", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DeepNavy, modifier = Modifier.size(26.dp)) }
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
internal fun DimensionsSheet(
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
    initialCustomW: String = "",
    initialCustomH: String = "",
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
    // v0.20.0: a tapped platform preset (Presets tab) arms the editor's target size; the sheet opens
    // with that exact size prefilled and preselected under Resolution > Custom, one tap from done.
    val hasInitialSize = initialCustomW.isNotBlank() && initialCustomH.isNotBlank()
    var choice by rememberSaveable { mutableStateOf(if (hasInitialSize) "rescustom" else "") }
    var lastExpanded by rememberSaveable { mutableStateOf(false) }
    var easyExpanded by rememberSaveable { mutableStateOf(!hasInitialSize) }
    var pctExpanded by rememberSaveable { mutableStateOf(false) }
    var resExpanded by rememberSaveable { mutableStateOf(hasInitialSize) }
    var fsExpanded by rememberSaveable { mutableStateOf(false) }
    var customPct by rememberSaveable { mutableStateOf("45") }
    var customW by rememberSaveable { mutableStateOf(initialCustomW) }
    var customH by rememberSaveable { mutableStateOf(initialCustomH) }
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
            // v0.18.2 audit fix: hide chips that open nothing real yet (Presets chip navigated away
            // mid-edit; Format chip was a dead-end snackbar). They return when they do something honest.
            Tool.values().filter { it != Tool.Presets && it != Tool.Format }.forEach { item ->
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
                    OutlinedButton(onClick = { onCropTransform(kind) }, shape = RoundedCornerShape(10.dp), enabled = !busy && working != null, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                        Icon(icon, contentDescription = label, modifier = Modifier.size(14.dp), tint = DeepNavy)
                        Spacer(Modifier.width(3.dp))
                        Text(label, color = DeepNavy, fontSize = 11.sp, maxLines = 1, softWrap = false)
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
private fun PresetsScreen(
    modifier: Modifier,
    textStyles: List<TextStylePreset>,
    onRefreshTextStyles: () -> Unit,
    onTextStyle: (TextStylePreset) -> Unit,
    onDeleteTextStyle: (Long) -> Unit,
    onPreset: (Preset) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var presets by remember { mutableStateOf<List<Preset>?>(null) }
    var search by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        onRefreshTextStyles()
        presets = withContext(Dispatchers.IO) { PresetStore.load(context) }
    }
    Column(modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Presets", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Your text styles · Shop & creator · Social media", color = Slate, modifier = Modifier.padding(top = 4.dp, bottom = 14.dp))
        if (textStyles.isNotEmpty()) {
            Text("My text styles", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp))
            textStyles.forEach { style ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).background(PaleBlue).clickable { onTextStyle(style) }.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(34.dp).clip(CircleShape).background(SkyBlue), contentAlignment = Alignment.Center) {
                        Text("T", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(style.name, color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(
                            style.font + (if (style.bold) " · bold" else "") + (if (style.italic) " · italic" else "") + (if (style.curved) " · curved" else ""),
                            color = Slate, fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = { onDeleteTextStyle(style.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete style", tint = Slate, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Text("Tap a style to open Text on Photo with it applied.", color = Slate, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp))
        }
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
                            "Every preset size in A2Z Image Craft is checked against official platform documentation before it appears here, with its source and review date. That research is not finished yet, so this list is honestly empty rather than filled with unverified numbers.",
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
private fun SettingsScreen(
    modifier: Modifier,
    darkMode: Boolean,
    onDarkMode: (Boolean) -> Unit,
    doc: String?,
    onOpenDoc: (String?) -> Unit,
    onEmail: () -> Unit,
    onRate: () -> Unit,
    onShare: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var recentsCount by remember { mutableStateOf(-1) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var clearedTick by remember { mutableStateOf(0) }
    LaunchedEffect(clearedTick) { recentsCount = withContext(Dispatchers.IO) { RecentsStore.list(context).size } }
    // v0.20.0: Terms & Conditions / Privacy Policy open as simple in-app pages (honest on-device wording).
    if (doc != null) {
        Column(modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onOpenDoc(null) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeepNavy) }
                Text(if (doc == "terms") "Terms & Conditions" else "Privacy Policy", color = DeepNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                if (doc == "terms") TERMS_TEXT else PRIVACY_TEXT,
                color = Slate, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 10.dp)
            )
        }
        return
    }
    Column(modifier.fillMaxSize().padding(18.dp).verticalScroll(rememberScrollState())) {
        Text("Settings", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        SettingsSection("Appearance") {
            SettingsRow(
                icon = { Icon(Icons.Default.DarkMode, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Dark mode",
                subtitle = if (darkMode) "On - easier on the eyes" else "Off - light theme"
            ) {
                Switch(checked = darkMode, onCheckedChange = onDarkMode, colors = SwitchDefaults.colors(checkedTrackColor = SkyBlue))
            }
        }

        SettingsSection("Privacy") {
            SettingsRow(
                icon = { Icon(Icons.Default.Lock, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Your photos stay on this phone",
                subtitle = "No account, no uploads, no photo-library permission - you pick each photo yourself."
            )
            SettingsRow(
                icon = { Icon(Icons.Default.Delete, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Clear recently edited",
                subtitle = if (recentsCount > 0) recentsCount.toString() + " photo(s) remembered on this phone" else "Nothing remembered right now",
                onClick = if (recentsCount > 0) ({ showClearConfirm = true }) else null
            )
        }

        SettingsSection("Support") {
            SettingsRow(
                icon = { Icon(Icons.Default.MailOutline, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Need support? Email us",
                subtitle = "Tell us what you'd like to see, or bugs you found",
                onClick = onEmail
            )
            SettingsRow(
                icon = { Icon(Icons.Default.Star, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Rate us",
                subtitle = "Like the app? Support it with 5 stars on Google Play",
                onClick = onRate
            )
            SettingsRow(
                icon = { Icon(Icons.Default.Share, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Share",
                subtitle = "Tell a friend about A2Z Image Craft",
                onClick = onShare
            )
            SettingsRow(
                icon = { Icon(Icons.Default.Description, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Terms & Conditions",
                subtitle = "The simple rules for using the app",
                onClick = { onOpenDoc("terms") }
            )
            SettingsRow(
                icon = { Icon(Icons.Default.Lock, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "Privacy Policy",
                subtitle = "How your photos and data are treated",
                onClick = { onOpenDoc("privacy") }
            )
        }

        SettingsSection("About") {
            SettingsRow(
                icon = { Icon(Icons.Default.Info, null, tint = SkyBlue, modifier = Modifier.size(20.dp)) },
                title = "A2Z Image Craft",
                subtitle = "Version " + BuildConfig.VERSION_NAME.removeSuffix("-debug") + " · Bitty"
            )
            Text(
                "Resize, crop, convert, compress, print, collage, text, stickers, frames and photo adjustments. Every tool runs fully on this phone - no account, no uploads, no photo-library permission.",
                color = Slate, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
    }
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear recently edited?") },
            text = { Text("This removes the photos shown under Recently edited on the Home tab. Your original photos are not touched.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    scope.launch(Dispatchers.IO) {
                        RecentsStore.clear(context)
                        withContext(Dispatchers.Main) { clearedTick++ }
                    }
                }) { Text("Clear", color = Orange) }
            },
            dismissButton = { TextButton(onClick = { showClearConfirm = false }) { Text("Cancel") } }
        )
    }
}

/** v0.20.1 (his dedup call after testing v0.20.0): side drawer keeps only the quick actions - Email us / Rate us /
 *  Share. Terms & Conditions and Privacy Policy live in Settings only, so nothing shows on both sides. */
@Composable
private fun HomeDrawer(onClose: () -> Unit, onEmail: () -> Unit, onRate: () -> Unit, onShare: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(Color(0x99061722)).clickable { onClose() })
        Column(Modifier.fillMaxHeight().fillMaxWidth(0.82f).background(Color.White).verticalScroll(rememberScrollState())) {
            Column(Modifier.fillMaxWidth().background(DeepNavy).padding(horizontal = 20.dp, vertical = 24.dp)) {
                Text("A2Z Image Craft", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("PHOTO TOOLKIT", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 4.dp))
            }
            DrawerRow({ Icon(Icons.Default.MailOutline, null, tint = SkyBlue, modifier = Modifier.size(22.dp)) }, "Need support? Email us", "Tell us what you'd like to see, or bugs you found") { onEmail() }
            DrawerRow({ Icon(Icons.Default.Star, null, tint = SkyBlue, modifier = Modifier.size(22.dp)) }, "Rate us", "Support the app with 5 stars") { onRate() }
            DrawerRow({ Icon(Icons.Default.Share, null, tint = SkyBlue, modifier = Modifier.size(22.dp)) }, "Share", "Tell a friend about A2Z Image Craft") { onShare() }
        }
    }
}

@Composable
private fun DrawerRow(icon: @Composable () -> Unit, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        icon()
        Column(Modifier.padding(start = 14.dp)) {
            Text(title, color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = Slate, fontSize = 12.sp)
        }
    }
}

private const val TERMS_TEXT = "These Terms & Conditions govern your use of A2Z Image Craft, developed and published by Bitty.\n\n" +
    "1. Free of charge. The app is free: no advertising, no subscriptions, no in-app purchases.\n\n" +
    "2. Your content. You keep every right to your photos. All editing runs on your device; nothing is uploaded, and images are saved or shared only when you choose to do so.\n\n" +
    "3. Acceptable use. You are responsible for the images you edit and share. Do not use the app to infringe copyrights, privacy or other rights of third parties, or for any unlawful purpose.\n\n" +
    "4. No warranty. The app is provided as is. Every tool is tested carefully, but uninterrupted or error-free operation cannot be guaranteed, and no liability is accepted for how exported images are used.\n\n" +
    "5. Changes. These terms may be updated with new versions of the app; the current version is always shown on this page."

private const val PRIVACY_TEXT = "A2Z Image Craft is private by design. This page explains, in plain words, how the app treats your data.\n\n" +
    "What the app does not do:\n" +
    "- No account and no sign-in.\n" +
    "- No uploads. Your photos never leave your phone: every tool - resize, crop, convert, compress, collage, text, frames, adjustments - runs entirely on your device.\n" +
    "- No photo-library access. The app holds no gallery permission; you hand it each photo yourself through the Android system picker.\n" +
    "- No analytics, no tracking, no advertising.\n\n" +
    "What stays on your phone:\n" +
    "- Recently edited: a list of photos you opened, with small thumbnails, stored locally. Clear it any time under Settings > Privacy.\n" +
    "- Your own text styles, stored locally.\n" +
    "- Exports, written only when you tap Save, to the folders the app shows you.\n\n" +
    "If a future version ever changes these principles, this page will say so before the change ships."

/** v0.20.0: one titled group on the Settings page. */
@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Text(title, color = Slate, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 6.dp))
    Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
        Column { content() }
    }
}

/** v0.20.0: one icon + title + subtitle row inside a Settings section.
 *  With onClick the WHOLE row is tappable and shows a chevron; otherwise an optional trailing action composable. */
@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    val rowMod = if (onClick != null) Modifier.fillMaxWidth().clickable { onClick() } else Modifier.fillMaxWidth()
    Row(rowMod.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) { icon() }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = Slate, fontSize = 12.sp, lineHeight = 16.sp)
        }
        if (onClick != null) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Slate, modifier = Modifier.size(20.dp))
        } else if (action != null) action()
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

/** Decode any gallery image: EXIF-corrected BitmapFactory first, ImageDecoder fallback (HEIC/AVIF/WebP sources). */
private fun decodeAnyImage(context: Context, uri: Uri): Bitmap {
    try {
        return decodeOrientedBitmap(context, uri)
    } catch (e: Exception) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } catch (e2: Exception) {
                // fall through to the original error
            }
        }
        throw e
    }
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

internal sealed class SheetChoice {
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
    onPdfAddMarked: () -> Unit,
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
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Select pages", color = DeepNavy, fontSize = 15.sp)
                            OutlinedButton(onClick = onPdfAddMarked) { Text("Add", color = DeepNavy) }
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
    val bg = if (bgWhite) Color.White else Color(PrintUtils.LIGHT_GREY_BG)

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
                        PrintPresetChip("Light grey", "soft grey", !bgWhite) { onBgWhite(false) }
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

/* Text on Photo (v0.16.0)
 * Screen order follows his spec exactly: Header -> Select Photo -> Image Preview -> Add Text ->
 * Text Styling -> Position and Layers -> Save Photo. The preview and the export share ONE
 * rendering path (TextOverlayUtils.drawAll in image space), so the live preview is the export.
 */
@Composable
private fun TextOnPhotoScreen(
    modifier: Modifier,
    bitmap: Bitmap?,
    name: String,
    sizeBytes: Long,
    layers: List<TextOverlayUtils.TextLayer>,
    tick: Int,
    selTick: Int,
    selectedId: Long?,
    canUndo: Boolean,
    canRedo: Boolean,
    format: String,
    resultBytes: ByteArray?,
    resultDims: String,
    saved: Boolean,
    editOpen: Boolean,
    busy: Boolean,
    onSelectPhoto: () -> Unit,
    onResetEdits: () -> Unit,
    filterId: String,
    onFilter: (String) -> Unit,
    frameStyle: String,
    frameWidth: Float,
    frameColor: Int,
    perspV: Float,
    perspH: Float,
    onFrameStyle: (String) -> Unit,
    onFrameWidth: (Float) -> Unit,
    onFrameColor: (Int) -> Unit,
    onPerspV: (Float) -> Unit,
    onPerspH: (Float) -> Unit,
    onResetPerspective: () -> Unit,
    onSelectLayer: (Long) -> Unit,
    onAddLayer: () -> Unit,
    onDeleteLayer: (Long) -> Unit,
    onMoveLayer: (Long, Int) -> Unit,
    onMutate: (Long, (TextOverlayUtils.TextLayer) -> Unit) -> Unit,
    imageLayers: List<TextOverlayUtils.ImageLayer>,
    selectedImageId: Long?,
    onAddImageLayer: () -> Unit,
    onSelectImageLayer: (Long) -> Unit,
    onMutateImageLayer: ((TextOverlayUtils.ImageLayer) -> Unit) -> Unit,
    onTransform: (Float, Float, Float, Float) -> Unit,
    onDoubleTapAt: (Float, Float) -> Unit,
    onDeleteImageLayer: (Long) -> Unit,
    onMoveImageLayer: (Long, Int) -> Unit,
    onTapAt: (Float, Float) -> Unit,
    onEditOpen: (Boolean) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onFormat: (String) -> Unit,
    onProcess: () -> Unit,
    onSave: () -> Unit,
    cropRect: FloatArray?,
    cropLabel: String,
    cropAdjust: Boolean,
    canvasDims: String,
    onSetCrop: (String, Int, Int) -> Unit,
    onClearCrop: () -> Unit,
    onCropAdjust: (Boolean) -> Unit,
    onCropNudge: (Float, Float) -> Unit,
    onCropZoom: (Float) -> Unit,
    cropZoomAbs: Float,
    onCropZoomAbs: (Float) -> Unit,
    onPickColourAt: (Float, Float, String) -> Boolean,
    onFlipSelected: () -> Unit,
    onCloneSelected: () -> Unit,
    onAddEmoji: (String) -> Unit,
    onAddSticker: (TextOverlayUtils.StickerKind, Int) -> Unit,
    onApplyMeme: (String, String) -> Unit,
    onRemoveBgImageLayer: () -> Unit,
    stylePresets: List<TextStylePreset>,
    onLoadStyles: () -> Unit,
    onApplyStyle: (TextStylePreset) -> Unit,
    onSaveStyle: (String) -> Unit
) {
    LaunchedEffect(Unit) { onLoadStyles() }
    val selected = layers.firstOrNull { it.id == selectedId }
    val selectedImage = imageLayers.firstOrNull { it.id == selectedImageId }
    var previewTouch by remember { mutableStateOf(false) }
    var pickColourMode by remember { mutableStateOf(false) }   // v0.16.7 eyedropper
    var pickTarget by remember { mutableStateOf("text") }

    BoxWithConstraints(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    // v0.16.5: the sheet takes a SHARE of the screen (42%, clamped) instead of a fixed height,
    // so the preview keeps the majority of the screen on small AND tall phones alike.
    val sheetH = (maxHeight * 0.38f).coerceIn(230.dp, 320.dp)   // v0.16.6: his "little more space for image edit" (was 42%)
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        if (bitmap == null) {
            Spacer(Modifier.height(4.dp))
            Text("Add words. Create something beautiful.", color = Slate, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
        }

        // ---- Select Photo ----
        if (bitmap == null) {
            Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Choose Your Photo", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Select a photo from your gallery to get started.", color = Slate, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onSelectPhoto, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                        Text("Select Photo", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            return@Column
        }

        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(name, color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Text("${bitmap.width} × ${bitmap.height} px" + (if (sizeBytes >= 0) " · ${formatBytes(sizeBytes)}" else ""), color = Slate, fontSize = 11.sp)
                }
                OutlinedButton(onClick = onSelectPhoto, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), modifier = Modifier.height(34.dp)) { Text("Change", color = DeepNavy, fontSize = 13.sp) }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onResetEdits, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), modifier = Modifier.height(34.dp)) { Text("Reset", color = DeepNavy, fontSize = 13.sp) }
            }
        }
        Spacer(Modifier.height(8.dp))

        // ---- Image Preview (live - every change repaints instantly; identical to export) ----
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f).heightIn(min = 240.dp).clip(RoundedCornerShape(12.dp)).background(SoftGrey).border(1.dp, BorderGrey, RoundedCornerShape(12.dp))) {
            val bmp = bitmap
            // Adjust mode shows the WHOLE photo with the crop frame; normal mode shows the effective canvas.
            val effW = if (cropRect != null && !cropAdjust) ((cropRect[2] - cropRect[0]) * bmp.width).toInt().coerceAtLeast(1) else bmp.width
            val effH = if (cropRect != null && !cropAdjust) ((cropRect[3] - cropRect[1]) * bmp.height).toInt().coerceAtLeast(1) else bmp.height
            val imgRatio = effW.toFloat() / effH
            var pw = maxWidth
            var ph = maxWidth / imgRatio
            if (ph > maxHeight) { ph = maxHeight; pw = maxHeight * imgRatio }
            val density = LocalDensity.current
            val sPx = with(density) { pw.toPx() } / effW
            val fPaint = remember(filterId) { FilterUtils.filterPaint(filterId) }
            Box(
                Modifier
                    .size(pw, ph)
                    .align(Alignment.Center)
                    .semantics { contentDescription = "Photo preview - tap a layer to edit it" }
                    .pointerInput(bmp) {
                        // While a finger is on the preview the page scroll is disabled,
                        // so single-finger drags move the text (or the crop frame) instead of the page.
                        awaitEachGesture {
                            awaitFirstDown()
                            previewTouch = true
                            do {
                                val event = awaitPointerEvent()
                            } while (event.changes.any { it.pressed })
                            previewTouch = false
                        }
                    }
                    .pointerInput(bmp, cropAdjust, pickColourMode, pickTarget) {
                        if (!cropAdjust) {
                            detectTapGestures(
                                onTap = { off ->
                                    if (pickColourMode) { if (onPickColourAt(off.x / sPx, off.y / sPx, pickTarget)) pickColourMode = false }
                                    else onTapAt(off.x / sPx, off.y / sPx)
                                },
                                onDoubleTap = { off -> onDoubleTapAt(off.x / sPx, off.y / sPx) }
                            )
                        }
                    }
                    .pointerInput(bmp, cropAdjust) {
                        detectTransformGestures { _, pan, zoom, rot ->
                            if (cropAdjust) {
                                onCropNudge(pan.x / sPx / bmp.width, pan.y / sPx / bmp.height)
                                if (zoom != 1f) onCropZoom(zoom)
                            } else {
                                // Routed through the App so the CURRENT selection (text vs image layer) is read at
                                // gesture time: pointerInput only restarts when bmp/cropAdjust change, so a captured
                                // selectedImageId here goes stale and the image layer would never move (v0.16.2 fix).
                                onTransform(pan.x / sPx / effW, pan.y / sPx / effH, zoom, rot)
                            }
                        }
                    }
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    @Suppress("UNUSED_VARIABLE") val t = tick   // state read: repaints on every layer mutation
                    val nc = drawContext.canvas.nativeCanvas
                    nc.save()
                    nc.scale(sPx, sPx)
                    if (cropAdjust && cropRect != null) {
                        // Whole photo + darkened outside + crop frame.
                        nc.drawBitmap(bmp, 0f, 0f, fPaint)
                        val cl = cropRect[0] * bmp.width
                        val ct = cropRect[1] * bmp.height
                        val cr = cropRect[2] * bmp.width
                        val cb = cropRect[3] * bmp.height
                        val scrim = android.graphics.Paint().apply { color = 0x99000000.toInt() }
                        nc.drawRect(0f, 0f, bmp.width.toFloat(), ct, scrim)
                        nc.drawRect(0f, cb, bmp.width.toFloat(), bmp.height.toFloat(), scrim)
                        nc.drawRect(0f, ct, cl, cb, scrim)
                        nc.drawRect(cr, ct, bmp.width.toFloat(), cb, scrim)
                        val frame = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            style = android.graphics.Paint.Style.STROKE
                            strokeWidth = (bmp.width * 0.004f).coerceAtLeast(2f)
                            color = 0xFF42BFEF.toInt()
                        }
                        nc.drawRect(cl, ct, cr, cb, frame)
                    } else {
                        if (cropRect != null) nc.translate(-cropRect[0] * bmp.width, -cropRect[1] * bmp.height)
                        // v0.19.0 Perspective: tilt the photo under the layers (text/stickers stay put).
                        if (perspV != 0f || perspH != 0f) {
                            nc.save()
                            nc.concat(TextOverlayUtils.perspectiveMatrix(effW, effH, perspV, perspH))
                            nc.drawBitmap(bmp, 0f, 0f, fPaint)
                            nc.restore()
                        } else {
                            nc.drawBitmap(bmp, 0f, 0f, fPaint)
                        }
                        // v0.19.0 Frame: over the photo edges, under the layers.
                        val pvStyle = TextOverlayUtils.FrameStyle.fromId(frameStyle)
                        if (pvStyle != TextOverlayUtils.FrameStyle.NONE) {
                            TextOverlayUtils.drawFrame(nc, effW, effH, pvStyle, frameWidth * minOf(effW, effH) / 400f, frameColor)
                        }
                        TextOverlayUtils.drawAll(nc, layers, effW, effH, selectedId, selectionFrame = true, imageLayers = imageLayers, selectedImageId = selectedImageId)
                    }
                    nc.restore()
                }
                // v0.16.7 (his "Use all"): pro corner handles on the selected layer - top-left X deletes,
                // bottom-right orange handle resizes + rotates with ONE finger (routes through onTransform,
                // the same proven path as pinch). Drag math reads the corner vector fresh via state.
                if (!cropAdjust && (selected != null || selectedImage != null)) {
                    val hCxPx: Float; val hCyPx: Float; val hHalfW: Float; val hHalfH: Float; val hRot: Float
                    if (selected != null) {
                        val mb = TextOverlayUtils.measure(selected, effW)
                        hCxPx = selected.cx * effW; hCyPx = selected.cy * effH
                        hHalfW = mb.w / 2f; hHalfH = mb.h / 2f; hRot = selected.rotation
                    } else {
                        val sz = TextOverlayUtils.imageLayerSize(selectedImage!!, effW)
                        hCxPx = selectedImage!!.cx * effW; hCyPx = selectedImage!!.cy * effH
                        hHalfW = sz[0] / 2f; hHalfH = sz[1] / 2f; hRot = selectedImage!!.rotation
                    }
                    val hRad = Math.toRadians(hRot.toDouble())
                    val hCos = kotlin.math.cos(hRad).toFloat(); val hSin = kotlin.math.sin(hRad).toFloat()
                    val hCentre = Offset(hCxPx * sPx, hCyPx * sPx)
                    val cornerVecNow = { Offset((hHalfW * hCos - hHalfH * hSin) * sPx, (hHalfW * hSin + hHalfH * hCos) * sPx) }
                    val cornerVecState = rememberUpdatedState(cornerVecNow)
                    val onTransformNow = rememberUpdatedState(onTransform)
                    val delCorner = hCentre + Offset((-hHalfW * hCos + hHalfH * hSin) * sPx, (-hHalfW * hSin - hHalfH * hCos) * sPx)
                    val rsCorner = hCentre + cornerVecNow()
                    Box(
                        Modifier
                            .offset { IntOffset((delCorner.x - 13.dp.toPx()).roundToInt(), (delCorner.y - 13.dp.toPx()).roundToInt()) }
                            .size(26.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color(0xFFE53935), CircleShape)
                            .clickable { if (selected != null) onDeleteLayer(selected.id) else selectedImage?.let { onDeleteImageLayer(it.id) } },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Close, contentDescription = "Delete layer", tint = Color(0xFFE53935), modifier = Modifier.size(15.dp)) }
                    Box(
                        Modifier
                            .offset { IntOffset((rsCorner.x - 14.dp.toPx()).roundToInt(), (rsCorner.y - 14.dp.toPx()).roundToInt()) }
                            .size(28.dp)
                            .background(Orange, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .pointerInput(selectedId, selectedImageId) {
                                var vec = Offset.Zero
                                detectDragGestures(
                                    onDragStart = { vec = cornerVecState.value() }
                                ) { change, drag ->
                                    change.consume()
                                    val len = vec.getDistance()
                                    val newVec = vec + drag
                                    if (len > 6f) {
                                        val z = newVec.getDistance() / len
                                        val r = (Math.toDegrees(kotlin.math.atan2(newVec.y.toDouble(), newVec.x.toDouble())) - Math.toDegrees(kotlin.math.atan2(vec.y.toDouble(), vec.x.toDouble()))).toFloat()
                                        vec = newVec
                                        if (z.isFinite() && z > 0.05f) onTransformNow.value(0f, 0f, z, r)
                                    } else vec = newVec
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Refresh, contentDescription = "Resize and rotate", tint = Color.White, modifier = Modifier.size(16.dp)) }
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            if (pickColourMode) "Tap anywhere on the photo - that colour goes to your text."
            else if (cropAdjust) "Drag your photo to position it · use the slider or pinch to zoom · tap Done when it looks right."
            else "Drag to move · pinch to resize · twist to rotate · double-tap to edit text",
            color = Slate, fontSize = 10.sp
        )

        Spacer(Modifier.height(10.dp))

        // ---- Bottom sheet editor (v0.16.4 redesign: his "we dont need to scroll up and down many times") ----
        // Two-level menu: root entries -> tap one for its options -> Back arrow returns. The photo
        // stays visible above the sheet, so every edit is seen live (his "easy to look live edit").
        val testSheetLevel = (androidx.compose.ui.platform.LocalContext.current as? Activity)?.intent?.getStringExtra("pixelcraft.test.sheetlevel")
        val testStatusLine = (androidx.compose.ui.platform.LocalContext.current as? Activity)?.intent?.getStringExtra("pixelcraft.test.statusline") == "1"
        val testCropAdjust = (androidx.compose.ui.platform.LocalContext.current as? Activity)?.intent?.getStringExtra("pixelcraft.test.cropadjust")
        LaunchedEffect(Unit) { if (testCropAdjust == "1" && !cropAdjust) onCropAdjust(true) }
        var sheetLevel by remember { mutableStateOf(testSheetLevel ?: "menu") }
        LaunchedEffect(sheetLevel) { if (sheetLevel != "pickcolour") pickColourMode = false }
        // Selecting a layer jumps straight to its options (his flow); Back returns to the menu manually.
        // v0.16.10: keyed on selTick as well as the IDs - re-tapping the SAME selected layer (his
        // logo report: selected, but the sheet stayed on the Create grid) still re-opens its panel.
        LaunchedEffect(selectedId, selectedImageId, selTick) {
            if (testSheetLevel == null || selTick > 0) {   // a test hook pins the INITIAL level; a real select-tap still wins
                if (selectedImageId != null) sheetLevel = "image"
                else if (selectedId != null) sheetLevel = "text"
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier.fillMaxWidth().height(sheetH).border(1.dp, BorderGrey, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        ) {
            Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp).verticalScroll(rememberScrollState())) {
                Row(Modifier.fillMaxWidth().padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (sheetLevel != "menu") {
                        // His v0.16.6 ask: "Menu button make orange colour with white writing"
                        Button(
                            onClick = { sheetLevel = "menu" },
                            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to menu", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Menu", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("Create", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    // His "I want to zoom and crop": Position always visible in the sheet header (v0.16.5 -
                    // moved here so the preview gets the space; starts from the full photo, zoom + pan, Done keeps it).
                    Button(
                        onClick = { onCropAdjust(!cropAdjust) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (cropAdjust) Orange else SkyBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(30.dp)
                    ) { Text(if (cropAdjust) "Done" else "Position", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    if (cropRect != null && !cropAdjust) {
                        TextButton(onClick = onClearCrop, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp), modifier = Modifier.height(30.dp)) { Text("Original", fontSize = 12.sp, color = DeepNavy) }
                    }
                    IconButton(onClick = onUndo, enabled = canUndo, modifier = Modifier.size(30.dp)) { Icon(Icons.Default.Undo, contentDescription = "Undo", tint = if (canUndo) DeepNavy else BorderGrey, modifier = Modifier.size(18.dp)) }
                    IconButton(onClick = onRedo, enabled = canRedo, modifier = Modifier.size(30.dp)) { Icon(Icons.Default.Redo, contentDescription = "Redo", tint = if (canRedo) DeepNavy else BorderGrey, modifier = Modifier.size(18.dp)) }
                }
                if (testStatusLine) Text("hook:sel=" + selectedId + ",imgsel=" + selectedImageId + ",selt=" + selTick + ",level=" + sheetLevel, fontSize = 8.sp, color = BorderGrey)
                if (cropAdjust) {
                    // v0.16.6 - his "Make same like you make in social media section" / "Exactly 100% same":
                    // Position mode gets the Social Media cropper's exact controls: Zoom slider + drag + hint.
                    Spacer(Modifier.height(4.dp))
                    Text("Zoom", fontSize = 13.sp, color = Slate)
                    Slider(value = cropZoomAbs, onValueChange = onCropZoomAbs, valueRange = 1f..5f)
                    Spacer(Modifier.height(10.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(14.dp)) {
                        Text("Drag your photo to position it. Use the slider to zoom in or out.", modifier = Modifier.padding(14.dp), fontSize = 13.sp, color = Slate)
                    }
                } else when (sheetLevel) {
                    "image" -> {

                    // ---- Image layer quick edits (his "Add image" spec: stays editable until export) ----
                    if (selectedImage != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Image layer", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Drag on the photo to move · pinch to resize · twist to rotate.", color = Slate, fontSize = 11.sp)
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Size", color = Slate, fontSize = 12.sp)
                                    Slider(
                                        value = selectedImage.widthFrac,
                                        onValueChange = { v -> onMutateImageLayer { im -> im.widthFrac = v } },
                                        valueRange = 0.05f..1f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("${(selectedImage.widthFrac * 100).toInt()}%", color = DeepNavy, fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Rotation", color = Slate, fontSize = 12.sp)
                                    Slider(
                                        value = ((selectedImage.rotation % 360f) + 540f) % 360f - 180f,
                                        onValueChange = { v -> onMutateImageLayer { im -> im.rotation = v } },
                                        valueRange = -180f..180f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("${(((selectedImage.rotation % 360f) + 540f) % 360f - 180f).toInt()}°", color = DeepNavy, fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Opacity", color = Slate, fontSize = 12.sp)
                                    Slider(
                                        value = selectedImage.opacity,
                                        onValueChange = { v -> onMutateImageLayer { im -> im.opacity = v } },
                                        valueRange = 0.2f..1f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("${(selectedImage.opacity * 100).toInt()}%", color = DeepNavy, fontSize = 12.sp)
                                }
                                Button(onClick = { onRemoveBgImageLayer() }, enabled = !busy, modifier = Modifier.fillMaxWidth().height(44.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(12.dp)) { Text("Remove background", fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.height(6.dp))
                                OutlinedButton(onClick = { onDeleteImageLayer(selectedImage.id) }, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Delete image layer", color = DeepNavy)
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    }
                    "text" -> {
                    // ---- Quick edits (his layout request: font size, background, shadow, curve and spacing live
                    //      right under the image, so every change is seen live without scrolling) ----
                    if (selected != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Quick edits", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Every change shows live on your photo above.", color = Slate, fontSize = 11.sp)
                                Spacer(Modifier.height(8.dp))

                                Text("Font size", color = Slate, fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("A-", color = Slate, fontSize = 12.sp)
                                    Slider(
                                        value = selected.sizeFrac,
                                        onValueChange = { v -> onMutate(1200L) { l -> l.sizeFrac = v } },
                                        valueRange = 0.02f..0.20f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("A+", color = DeepNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Curve Text", color = Slate, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    Switch(checked = selected.curved, onCheckedChange = { onMutate(0L) { l -> l.curved = it } })
                                }
                                if (selected.curved) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Arc", color = Slate, fontSize = 12.sp)
                                        Slider(
                                            value = selected.arcSweep,
                                            onValueChange = { v -> onMutate(1200L) { l -> l.arcSweep = v } },
                                            valueRange = 60f..320f,
                                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                        )
                                        Text("${selected.arcSweep.toInt()}°", color = DeepNavy, fontSize = 12.sp)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))

                                Text("Text background", color = Slate, fontSize = 12.sp)
                                run {
                                    var bgColoursOpen by remember { mutableStateOf(false) }
                                    Row(Modifier.padding(top = 4.dp, bottom = 8.dp).horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
                                        listOf(0 to "None", 0xFFFFFFFF.toInt() to "White", 0xFF000000.toInt() to "Black", 0xFF42BFEF.toInt() to "Blue", 0xFFF5B942.toInt() to "Gold").forEach { (c, lbl) ->
                                            val sel = selected.bgColorArgb == c
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = if (sel) PaleBlue else Color.White),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).border(if (sel) 2.dp else 0.dp, if (sel) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onMutate(0L) { l -> l.bgColorArgb = c } }
                                            ) { Text(lbl, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 12.sp, color = DeepNavy) }
                                        }
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = if (bgColoursOpen) PaleBlue else SoftGrey),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.border(if (bgColoursOpen) 2.dp else 0.dp, if (bgColoursOpen) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { bgColoursOpen = !bgColoursOpen }
                                        ) { Text("More colours", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                                    }
                                    if (bgColoursOpen) {
                                        TextOverlayUtils.EXTRA_COLORS.chunked(8).forEach { rowItems ->
                                            Row(Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                rowItems.forEach { c ->
                                                    val sel = selected.bgColorArgb == c
                                                    Box(
                                                        Modifier
                                                            .size(30.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(c))
                                                            .border(if (sel) 3.dp else 1.dp, if (sel) SkyBlue else BorderGrey, CircleShape)
                                                            .clickable { onMutate(0L) { l -> l.bgColorArgb = c } }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Shadow", color = Slate, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    Switch(checked = selected.shadow, onCheckedChange = { onMutate(0L) { l -> l.shadow = it } })
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Opacity", color = Slate, fontSize = 12.sp)
                                    Slider(
                                        value = selected.opacity,
                                        onValueChange = { v -> onMutate(1200L) { l -> l.opacity = v } },
                                        valueRange = 0.2f..1f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("${(selected.opacity * 100).toInt()}%", color = DeepNavy, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Letter spacing", color = Slate, fontSize = 12.sp)
                                    Slider(
                                        value = selected.letterSpacing,
                                        onValueChange = { v -> onMutate(1200L) { l -> l.letterSpacing = v } },
                                        valueRange = 0f..0.3f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("${(selected.letterSpacing * 100).toInt()}%", color = DeepNavy, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Line spacing", color = Slate, fontSize = 12.sp)
                                    Slider(
                                        value = selected.lineSpacing,
                                        onValueChange = { v -> onMutate(1200L) { l -> l.lineSpacing = v } },
                                        valueRange = 0f..1f,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Text("${(selected.lineSpacing * 100).toInt()}%", color = DeepNavy, fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                        if (selected != null) {
                        Text("Text colour", color = Slate, fontSize = 12.sp)
                        // v0.16.4: all 18 swatches visible as a grid (his palette reference) - no "More colours" hunting.
                        (TextOverlayUtils.QUICK_COLORS + TextOverlayUtils.EXTRA_COLORS).chunked(6).forEach { rowItems ->
                            Row(Modifier.padding(top = 6.dp, bottom = 2.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowItems.forEach { c ->
                                    val sel = selected.colorArgb == c
                                    Box(
                                        Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Color(c))
                                            .border(if (sel) 3.dp else 1.dp, if (sel) SkyBlue else BorderGrey, CircleShape)
                                            .clickable { onMutate(0L) { l -> l.colorArgb = c } }
                                    )
                                }
                            }
                        }
                        }

                    OutlinedTextField(
                        value = selected?.text ?: "",
                        onValueChange = { v -> onMutate(1500L) { l -> l.text = v } },
                        enabled = selected != null,
                        minLines = 2,
                        label = { Text(if (selected != null) "Text for the selected layer (multiple lines OK)" else "Add a text layer first") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ---- Text Styling ----
                    if (selected != null) {
                        Text("Text Styling", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))

                        run {
                            var saveStyleOpen by remember { mutableStateOf(false) }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("My styles", color = Slate, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                OutlinedButton(onClick = { saveStyleOpen = true }) { Text("Save current style", color = DeepNavy) }
                            }
                            if (stylePresets.isNotEmpty()) {
                                stylePresets.chunked(3).forEach { rowItems ->
                                    Row {
                                        rowItems.forEach { p ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = SoftGrey),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).clickable { onApplyStyle(p) }
                                            ) { Text(p.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 12.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold, maxLines = 1) }
                                        }
                                    }
                                }
                            } else {
                                Text("Style a text layer, then save it here to reuse on any photo - it also appears in the Presets tab.", color = Slate, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
                            }
                            if (saveStyleOpen) {
                                var styleName by remember { mutableStateOf("") }
                                AlertDialog(
                                    onDismissRequest = { saveStyleOpen = false },
                                    title = { Text("Save current style") },
                                    text = {
                                        OutlinedTextField(value = styleName, onValueChange = { styleName = it }, singleLine = true, label = { Text("Style name") }, modifier = Modifier.fillMaxWidth())
                                    },
                                    confirmButton = {
                                        Button(onClick = { if (styleName.isNotBlank()) { onSaveStyle(styleName.trim()); saveStyleOpen = false } }) { Text("Save") }
                                    },
                                    dismissButton = {
                                        OutlinedButton(onClick = { saveStyleOpen = false }) { Text("Cancel", color = DeepNavy) }
                                    }
                                )
                            }
                        }

                        Text("Quick Text Styles", color = Slate, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        TextOverlayUtils.QUICK_STYLE_NAMES.chunked(2).forEach { rowItems ->
                            Row(Modifier.fillMaxWidth()) {
                                rowItems.forEach { style ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = SoftGrey),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp, bottom = 8.dp).clickable { onMutate(0L) { l -> TextOverlayUtils.applyPreset(l, style) } }
                                    ) {
                                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            Text(style, fontWeight = FontWeight.Bold, color = DeepNavy, fontSize = 14.sp)
                                            Text(
                                                when (style) {
                                                    "Classic" -> "Clean, simple text"
                                                    "Bold" -> "Strong headlines"
                                                    "Elegant" -> "Stylish typography"
                                                    else -> "Text with depth"
                                                },
                                                fontSize = 11.sp, color = Slate
                                            )
                                        }
                                    }
                                }
                                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }

                        Text("Font", color = Slate, fontSize = 12.sp)
                        run {
                            var fontsOpen by remember { mutableStateOf(false) }
                            Row(Modifier.padding(top = 4.dp).horizontalScroll(rememberScrollState())) {
                                TextOverlayUtils.FontChoice.values().filter { it.common }.forEach { f ->
                                    val sel = selected.font == f
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = if (sel) PaleBlue else SoftGrey),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).border(if (sel) 2.dp else 0.dp, if (sel) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onMutate(0L) { l -> l.font = f } }
                                    ) { Text(f.label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 13.sp, color = DeepNavy, fontFamily = FontFamily(android.graphics.Typeface.create(f.family, android.graphics.Typeface.NORMAL)), fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) }
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (fontsOpen) PaleBlue else SoftGrey),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).border(if (fontsOpen) 2.dp else 0.dp, if (fontsOpen) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { fontsOpen = !fontsOpen }
                                ) { Text("More fonts", modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.SemiBold) }
                            }
                            if (fontsOpen) {
                                TextOverlayUtils.FontChoice.values().filter { !it.common }.chunked(3).forEach { rowItems ->
                                    Row {
                                        rowItems.forEach { f ->
                                            val sel = selected.font == f
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = if (sel) PaleBlue else SoftGrey),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).border(if (sel) 2.dp else 0.dp, if (sel) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onMutate(0L) { l -> l.font = f } }
                                            ) { Text(f.label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 13.sp, color = DeepNavy, fontFamily = FontFamily(android.graphics.Typeface.create(f.family, android.graphics.Typeface.NORMAL)), fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) }
                                        }
                                    }
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (selected.bold) PaleBlue else SoftGrey),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).border(if (selected.bold) 2.dp else 0.dp, if (selected.bold) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onMutate(0L) { l -> l.bold = !l.bold } }
                            ) { Text("Bold", modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 13.sp, color = DeepNavy, fontWeight = FontWeight.Bold) }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (selected.italic) PaleBlue else SoftGrey),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).border(if (selected.italic) 2.dp else 0.dp, if (selected.italic) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onMutate(0L) { l -> l.italic = !l.italic } }
                            ) { Text("Italic", modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 13.sp, color = DeepNavy, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic) }
                        }

                        Text("Text alignment", color = Slate, fontSize = 12.sp)
                        Row(Modifier.padding(top = 4.dp)) {
                            TextOverlayUtils.Align.values().forEach { a ->
                                val sel = selected.align == a
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (sel) PaleBlue else SoftGrey),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).border(if (sel) 2.dp else 0.dp, if (sel) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onMutate(0L) { l -> l.align = a } }
                                ) { Text(a.label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 13.sp, color = DeepNavy) }
                            }
                        }


                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Outline", color = Slate, fontSize = 12.sp)
                            Slider(
                                value = selected.outlineFrac,
                                onValueChange = { v -> onMutate(1200L) { l -> l.outlineFrac = v } },
                                valueRange = 0f..0.25f,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                            )
                            Text(if (selected.outlineFrac <= 0.005f) "Off" else "${(selected.outlineFrac * 100).toInt()}%", color = DeepNavy, fontSize = 12.sp)
                        }
                        if (selected.outlineFrac > 0.005f) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf(0xFF000000.toInt() to "Black", 0xFFFFFFFF.toInt() to "White", 0xFF172B4D.toInt() to "Navy").forEach { (c, lbl) ->
                                    val sel = selected.outlineColorArgb == c
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onMutate(0L) { l -> l.outlineColorArgb = c } }) {
                                        Box(Modifier.size(18.dp).clip(CircleShape).background(Color(c)).border(if (sel) 3.dp else 1.dp, if (sel) SkyBlue else BorderGrey, CircleShape))
                                        Spacer(Modifier.width(4.dp))
                                        Text(lbl, color = Slate, fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                    }

                    }
                    "filters" -> {
                        Text("Filters", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Tap a filter - the photo updates live. Works together with your text and logo.", color = Slate, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        val thumbs = remember(bitmap) {
                            val b = bitmap
                            if (b == null) emptyList()
                            else {
                                val tw = 180
                                val th = (180f * b.height / b.width).toInt().coerceAtLeast(1)
                                val small = Bitmap.createScaledBitmap(b, tw, th, true)
                                FilterUtils.FilterChoice.values().map { it to FilterUtils.applyFilter(small, it.id) }
                            }
                        }
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            thumbs.forEach { (fc, tb) ->
                                val sel = filterId == fc.id
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 10.dp).clickable { onFilter(fc.id) }) {
                                    Image(
                                        bitmap = tb.asImageBitmap(),
                                        contentDescription = fc.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).border(if (sel) 2.dp else 1.dp, if (sel) SkyBlue else BorderGrey, RoundedCornerShape(10.dp))
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(fc.label, color = if (sel) SkyBlue else DeepNavy, fontSize = 11.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                    "size" -> {
                    // ---- Image Size (his v0.16.1 spec: 4 common sizes up front + "More sizes" -> all sizes + custom) ----
                    run {
                        var moreOpen by remember { mutableStateOf(false) }
                        var customW by remember { mutableStateOf("") }
                        var customH by remember { mutableStateOf("") }
                        var customNote by remember { mutableStateOf("") }
                        Text("Image Size", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Pick the canvas shape first, then add your words.", color = Slate, fontSize = 11.sp)
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            TextOverlayUtils.ASPECT_OPTIONS.filter { it.common }.forEach { opt ->
                                PrintPresetChip(opt.label, "${opt.rw}:${opt.rh}", cropLabel == "${opt.label} ${opt.rw}:${opt.rh}") { onSetCrop(opt.label, opt.rw, opt.rh) }
                            }
                            PrintPresetChip("More sizes", "all + custom", moreOpen) { moreOpen = !moreOpen }
                        }
                        if (moreOpen) {
                            Row(Modifier.horizontalScroll(rememberScrollState())) {
                                PrintPresetChip("Original", "no crop", cropRect == null && cropLabel.isEmpty()) { onClearCrop() }
                                TextOverlayUtils.ASPECT_OPTIONS.filter { !it.common }.take(3).forEach { opt ->
                                    PrintPresetChip(opt.label, "${opt.rw}:${opt.rh}", cropLabel == "${opt.label} ${opt.rw}:${opt.rh}") { onSetCrop(opt.label, opt.rw, opt.rh) }
                                }
                            }
                            Row {
                                TextOverlayUtils.ASPECT_OPTIONS.filter { !it.common }.drop(3).forEach { opt ->
                                    PrintPresetChip(opt.label, "${opt.rw}:${opt.rh}", cropLabel == "${opt.label} ${opt.rw}:${opt.rh}") { onSetCrop(opt.label, opt.rw, opt.rh) }
                                }
                            }
                            Text("Custom size", color = Slate, fontSize = 12.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = customW,
                                    onValueChange = { customW = it; customNote = "" },
                                    singleLine = true,
                                    label = { Text("Width") },
                                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                                )
                                Text(":", color = Slate, fontSize = 16.sp)
                                OutlinedTextField(
                                    value = customH,
                                    onValueChange = { customH = it; customNote = "" },
                                    singleLine = true,
                                    label = { Text("Height") },
                                    modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
                                )
                                OutlinedButton(onClick = {
                                    val ratio = TextOverlayUtils.parseCustomRatio(customW, customH)
                                    if (ratio == null) customNote = "Enter whole numbers from 1 to 99, like 5 and 7."
                                    else onSetCrop("Custom", ratio[0], ratio[1])
                                }) { Text("Apply", color = DeepNavy) }
                            }
                            if (customNote.isNotBlank()) Text(customNote, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                        if (cropRect != null) {
                            Spacer(Modifier.height(4.dp))
                            Text("Canvas: $cropLabel · $canvasDims", color = SkyBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    }
                    "layers" -> {
                    // ---- Position and Layers ----
                    Text("Position and Layers", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    Row {
                        OutlinedButton(onClick = onUndo, enabled = canUndo, modifier = Modifier.padding(end = 8.dp)) {
                            Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Undo")
                        }
                        OutlinedButton(onClick = onRedo, enabled = canRedo) {
                            Icon(Icons.Default.Redo, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Redo")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (layers.isEmpty()) {
                        Text("No text layers yet - tap \"Add text layer\" above.", color = Slate, fontSize = 12.sp)
                    }
                    layers.forEachIndexed { i, layer ->
                        val sel = layer.id == selectedId
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (sel) PaleBlue else SoftGrey),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).border(if (sel) 2.dp else 0.dp, if (sel) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onSelectLayer(layer.id) }
                        ) {
                            Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(layer.text.lines().firstOrNull()?.take(24)?.ifBlank { "(empty)" } ?: "(empty)", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    Text(
                                        "Layer ${i + 1}" + (if (layer.curved) " · curved" else "") + (if (i == layers.lastIndex && layers.size > 1) " · on top" else ""),
                                        color = Slate, fontSize = 11.sp
                                    )
                                }
                                IconButton(onClick = { onMoveLayer(layer.id, -1) }, enabled = i > 0) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Send backward", tint = if (i > 0) DeepNavy else BorderGrey)
                                }
                                IconButton(onClick = { onMoveLayer(layer.id, 1) }, enabled = i < layers.lastIndex) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Bring forward", tint = if (i < layers.lastIndex) DeepNavy else BorderGrey)
                                }
                                IconButton(onClick = { onDeleteLayer(layer.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete layer", tint = Slate)
                                }
                            }
                        }
                    }
                    imageLayers.forEachIndexed { i, layer ->
                        val sel = layer.id == selectedImageId
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (sel) PaleBlue else SoftGrey),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).border(if (sel) 2.dp else 0.dp, if (sel) SkyBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onSelectImageLayer(layer.id) }
                        ) {
                            Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Image ${i + 1}", color = DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    Text("Overlay · sits under the text", color = Slate, fontSize = 11.sp)
                                }
                                IconButton(onClick = { onMoveImageLayer(layer.id, -1) }, enabled = i > 0) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Send backward", tint = if (i > 0) DeepNavy else BorderGrey)
                                }
                                IconButton(onClick = { onMoveImageLayer(layer.id, 1) }, enabled = i < imageLayers.lastIndex) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Bring forward", tint = if (i < imageLayers.lastIndex) DeepNavy else BorderGrey)
                                }
                                IconButton(onClick = { onDeleteImageLayer(layer.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete image", tint = Slate)
                                }
                            }
                        }
                    }
                    Text("Later layers sit on top of earlier ones. Use the arrows to reorder.", color = Slate, fontSize = 11.sp)
                    Spacer(Modifier.height(14.dp))

                    }
                    "pickcolour" -> {
                    // v0.16.7 eyedropper (his approved idea): pick any colour straight from the photo.
                    Text("Pick Colour", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(if (selected == null) "Tap a text layer on the photo first, then come back here." else "Choose where the colour goes, tap Start picking, then tap the photo.", color = Slate, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    Row {
                        OutlinedButton(onClick = { pickTarget = "text" }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (pickTarget == "text") SkyBlue else Color.Transparent, contentColor = if (pickTarget == "text") Color.White else DeepNavy)) { Text("Text colour", fontSize = 12.sp) }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = { pickTarget = "background" }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (pickTarget == "background") SkyBlue else Color.Transparent, contentColor = if (pickTarget == "background") Color.White else DeepNavy)) { Text("Background", fontSize = 12.sp) }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { pickColourMode = !pickColourMode },
                        enabled = selected != null,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (pickColourMode) Orange else SkyBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text(if (pickColourMode) "Now tap the photo…" else "Start picking", fontWeight = FontWeight.Bold) }
                    }
                    "flipclone" -> {
                    // v0.16.7 (his approved idea): mirror + duplicate the selected layer in one tap.
                    Text("Flip & Clone", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(if (selected == null && selectedImage == null) "Tap a layer on the photo first - then flip or duplicate it here." else "Works on the selected " + (if (selected != null) "text" else "image") + " layer.", color = Slate, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onFlipSelected, enabled = selected != null || selectedImage != null, modifier = Modifier.fillMaxWidth().height(46.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) { Text("Flip Horizontal", fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onCloneSelected, enabled = selected != null || selectedImage != null, modifier = Modifier.fillMaxWidth().height(46.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) { Text("Duplicate Layer", fontWeight = FontWeight.Bold) }
                    if (testSheetLevel != null) Text("hook:layers=" + layers.size + ",images=" + imageLayers.size, fontSize = 8.sp, color = BorderGrey)
                    }
                    "effects" -> { EffectsLevel(selected, testSheetLevel, tick, onMutate) }
                    "stickers" -> { StickersLevel(testSheetLevel, layers.size, imageLayers.size, tick, onAddEmoji, onAddSticker) }
                    "meme" -> { MemeLevel(testSheetLevel, layers.size, imageLayers.size, tick, onApplyMeme) }
                    "save" -> {
                    // ---- Save Photo ----
                    Text("Process & Save", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    Row {
                        PrintPresetChip("PNG", "best quality", format == "PNG") { val had = resultBytes != null; onFormat("PNG"); if (had) onProcess() }
                        PrintPresetChip("JPG", "smaller file", format == "JPG") { val had = resultBytes != null; onFormat("JPG"); if (had) onProcess() }
                    }
                    Text("Nothing is saved until you tap Save. Your original photo is never replaced.", color = Slate, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    // v0.16.7 (his "After first process again second process coming"): once a fresh
                    // result exists, ONLY "Save to Gallery" shows - Process returns when an edit
                    // (layer, format, filter, position) invalidates the result (resultBytes -> null).
                    if (resultBytes == null) {
                    Button(
                        onClick = onProcess,
                        // v0.16.5: enabled with zero layers too - zoom/crop + save without text is a real flow.
                        enabled = !busy,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Process", fontWeight = FontWeight.Bold) }   // v0.16.6: no confusing "Process again" - edits invalidate the result, so this is always the one first step
                    }
                    if (resultBytes != null) {
                        Spacer(Modifier.height(8.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = SoftGrey), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Ready to save", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("text-on-photo.${if (format == "PNG") "png" else "jpg"} · $resultDims" + (if (cropRect != null) " · $cropLabel" else "") + " · ${formatBytes(resultBytes.size.toLong())}", color = Slate, fontSize = 12.sp)
                                Text("${layers.size} text ${if (layers.size == 1) "layer" else "layers"}", color = Slate, fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onSave,
                            enabled = !busy && !saved,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text(if (saved) "Saved ✓ - see your gallery" else "Save to Gallery", fontWeight = FontWeight.Bold) }
                    }
                    }
                    "frame" -> {
                        Text("Frame", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Tap a style - the photo updates live. Text and stickers stay on top.", color = Slate, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            TextOverlayUtils.FrameStyle.values().forEach { st ->
                                FrameThumb(st, if (st.usesColor) frameColor else 0xFFFFFFFF.toInt(), frameStyle == st.id) { onFrameStyle(st.id) }
                            }
                        }
                        val curStyle = TextOverlayUtils.FrameStyle.fromId(frameStyle)
                        if (curStyle.usesColor) {
                            Spacer(Modifier.height(6.dp))
                            Text("Frame colour", color = Slate, fontSize = 11.sp)
                            Spacer(Modifier.height(4.dp))
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(0xFFFFFFFF, 0xFF111111, 0xFF42BFEF, 0xFFF95F6A, 0xFFFF8A32, 0xFFFFCA28, 0xFF3ECF8E, 0xFF3D5AFE, 0xFF9B5DE5, 0xFF8D6E63, 0xFFD4AF37, 0xFFC0C0C0).forEach { cv ->
                                    val c = cv.toInt()
                                    Box(
                                        Modifier.size(30.dp).clip(CircleShape).background(Color(c))
                                            .border(if (frameColor == c) 3.dp else 1.dp, if (frameColor == c) SkyBlue else BorderGrey, CircleShape)
                                            .clickable { onFrameColor(c) }
                                    )
                                }
                            }
                        }
                        if (curStyle != TextOverlayUtils.FrameStyle.NONE) {
                            Spacer(Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Thickness", color = Slate, fontSize = 12.sp)
                                Slider(
                                    value = frameWidth,
                                    onValueChange = { onFrameWidth(it) },
                                    valueRange = 2f..20f,
                                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                )
                                Text(frameWidth.toInt().toString(), color = DeepNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    "perspective" -> {
                        Text("Perspective", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Tilt the photo - flatten a tilted shot or add a 3D look. Text and stickers stay put. Tap a value to snap it back to the centre.", color = Slate, fontSize = 11.sp)
                        Spacer(Modifier.height(4.dp))
                        AdjustSlider("Vertical", perspV, {}, { onPerspV(it) }, {})
                        AdjustSlider("Horizontal", perspH, {}, { onPerspH(it) }, {})
                        Spacer(Modifier.height(4.dp))
                        OutlinedButton(onClick = onResetPerspective, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("Reset perspective", color = DeepNavy) }
                    }
                    else -> {
                        Text("Tap a layer on the photo to style it - its options open here.", color = Slate, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        listOf(
                            Triple("Add Text", Icons.Default.Create, { onAddLayer() }),
                            Triple("Add Image", Icons.Default.Image, { onAddImageLayer() }),
                            Triple("Image Size", Icons.Default.PhotoSizeSelectLarge, { sheetLevel = "size" }),
                            Triple("Layers", Icons.Default.Collections, { sheetLevel = "layers" }),
                            Triple("Filters", Icons.Default.Tune, { sheetLevel = "filters" }),
                            Triple("Pick Colour", Icons.Default.Colorize, { sheetLevel = "pickcolour" }),
                            Triple("Flip & Clone", Icons.Default.Flip, { sheetLevel = "flipclone" }),
                            Triple("Effects", Icons.Default.AutoAwesome, { sheetLevel = "effects" }),
                            Triple("Stickers", Icons.Default.EmojiEmotions, { sheetLevel = "stickers" }),
                            Triple("Meme", Icons.Default.Title, { sheetLevel = "meme" }),
                            Triple("Frame", Icons.Default.CropSquare, { sheetLevel = "frame" }),
                            Triple("Perspective", Icons.Default.Transform, { sheetLevel = "perspective" })
                        ).chunked(3).forEach { rowItems ->
                            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                rowItems.forEach { (label, icon, action) ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = SoftGrey),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp).clickable { action() }
                                    ) {
                                        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(icon, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(26.dp))
                                            Spacer(Modifier.height(4.dp))
                                            Text(label, color = DeepNavy, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f).padding(end = 8.dp)) }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        // v0.16.9 (his "Process button keep in last" + "make rectangle"): the primary
                        // action sits BELOW the grid as a full-width rectangle, orange like Export.
                        Button(
                            onClick = { sheetLevel = "save"; if (resultBytes == null && !busy) onProcess() },
                            enabled = !busy,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Process", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
    }

    if (editOpen && selected != null) {
        var draft by remember(selected.id) { mutableStateOf(selected.text) }
        AlertDialog(
            onDismissRequest = { onEditOpen(false) },
            title = { Text("Edit text") },
            text = {
                OutlinedTextField(value = draft, onValueChange = { draft = it }, minLines = 3, modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                Button(onClick = { onMutate(0L) { l -> l.text = draft }; onEditOpen(false) }) { Text("Done") }
            },
            dismissButton = {
                OutlinedButton(onClick = { onEditOpen(false) }) { Text("Cancel", color = DeepNavy) }
            }
        )
    }
}
