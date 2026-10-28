package com.learnifyzone.pixelcraft

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

private val SkyBlue = Color(0xFF42BFEF)
private val Orange = Color(0xFFFF8A32)
private val DeepNavy = Color(0xFF172B4D)
private val PaleBlue = Color(0xFFF0FAFF)
private val SoftGrey = Color(0xFFF3F6F9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PixelCraftApp() }
    }
}

private enum class MainTab { Home, Presets, Settings }
private enum class Screen { Home, Editor, Print, Success }
private enum class Tool(val label: String) { Resize("Resize"), Presets("Presets"), Crop("Crop"), Background("Background"), Format("Format") }
private data class Feature(val title: String, val description: String, val icon: String, val active: Boolean = false)

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
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var editedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageName by rememberSaveable { mutableStateOf("pixelcraft-image") }
    var inputWidth by rememberSaveable { mutableStateOf("") }
    var inputHeight by rememberSaveable { mutableStateOf("") }
    var lockRatio by rememberSaveable { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var savedName by rememberSaveable { mutableStateOf("") }
    var savedSize by rememberSaveable { mutableStateOf("") }
    var beforeAfter by rememberSaveable { mutableStateOf(false) }

    val singlePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            busy = true
            sourceUri = uri
            scope.launch {
                try {
                    val loaded = withContext(Dispatchers.IO) { decodeOrientedBitmap(context, uri) }
                    originalBitmap = loaded
                    editedBitmap = loaded
                    inputWidth = loaded.width.toString()
                    inputHeight = loaded.height.toString()
                    imageName = "pixelcraft-image"
                    screen = Screen.Editor
                    tool = Tool.Resize
                    snackbar.showSnackbar("Photo loaded. Stage 1 supports resize and export.")
                } catch (e: Exception) {
                    snackbar.showSnackbar("Couldn't open this image. Try another photo.")
                } finally { busy = false }
            }
        }
    }
    val multiplePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(20)) { uris ->
        if (uris.isNotEmpty()) snackbar.showSnackbar("Batch selection is recognised. Batch processing is planned for a later stage.")
    }

    BackHandler(enabled = screen != Screen.Home) {
        screen = if (screen == Screen.Success) Screen.Home else Screen.Home
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            if (screen != Screen.Home && screen != Screen.Success) {
                TopAppBar(
                    title = { Text(if (screen == Screen.Print) "Print Size & DPI" else "${tool.label} Image", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = { IconButton(onClick = { screen = Screen.Home }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                    actions = { Text("Reset", modifier = Modifier.clickable { originalBitmap?.let { editedBitmap = it; inputWidth = it.width.toString(); inputHeight = it.height.toString() } }.padding(horizontal = 14.dp), color = DeepNavy) }
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
                    onSelectPhoto = { singlePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    onBatch = { multiplePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    onPrint = { screen = Screen.Print },
                    onFeature = { feature ->
                        if (feature == "Resize") singlePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        else if (feature == "Batch Resize") multiplePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        else if (feature == "Print Size & DPI") screen = Screen.Print
                        else scope.launch { snackbar.showSnackbar("$feature is planned for a later development stage.") }
                    }
                )
                MainTab.Presets -> PresetsScreen(Modifier.padding(padding), onPick = { singlePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) })
                MainTab.Settings -> SettingsScreen(Modifier.padding(padding), darkMode = darkMode, onDarkMode = onDarkMode, onPrivacy = { scope.launch { snackbar.showSnackbar("Privacy policy URL will be added before release.") } })
            }
            Screen.Editor -> EditorScreen(
                modifier = Modifier.padding(padding),
                original = originalBitmap,
                edited = editedBitmap,
                widthText = inputWidth,
                heightText = inputHeight,
                lockRatio = lockRatio,
                busy = busy,
                beforeAfter = beforeAfter,
                tool = tool,
                onBeforeAfter = { beforeAfter = it },
                onTool = { selected ->
                    tool = selected
                    if (selected != Tool.Resize) scope.launch { snackbar.showSnackbar("${selected.label} is planned for a later stage. Resize and export are available now.") }
                },
                onWidth = { value ->
                    inputWidth = value
                    if (lockRatio) {
                        val old = originalBitmap
                        val newWidth = value.toIntOrNull()
                        if (old != null && newWidth != null && newWidth > 0) {
                            val size = runCatching { ResizeMath.fromWidth(old.width, old.height, newWidth) }.getOrNull()
                            if (size != null) inputHeight = size.height.toString()
                        }
                    }
                },
                onHeight = { value ->
                    inputHeight = value
                    val old = originalBitmap
                    val newHeight = value.toIntOrNull()
                    if (lockRatio && old != null && newHeight != null && newHeight > 0) {
                        val newWidth = (newHeight.toDouble() * old.width / old.height).toInt().coerceAtLeast(1)
                        inputWidth = newWidth.toString()
                    }
                },
                onLockRatio = { lockRatio = it },
                onResize = {
                    val src = originalBitmap
                    val w = inputWidth.toIntOrNull()
                    val h = inputHeight.toIntOrNull()
                    if (src == null || w == null || h == null) scope.launch { snackbar.showSnackbar("Enter valid positive pixel dimensions.") }
                    else {
                        val valid = runCatching { ResizeMath.validate(w, h) }.getOrElse {
                            scope.launch { snackbar.showSnackbar(it.message ?: "Invalid dimensions") }; null
                        }
                        if (valid != null) {
                            busy = true
                            scope.launch {
                                try {
                                    editedBitmap = withContext(Dispatchers.Default) { Bitmap.createScaledBitmap(src, valid.width, valid.height, true) }
                                    snackbar.showSnackbar("Preview updated: ${valid.width} × ${valid.height} px")
                                } catch (e: Exception) { snackbar.showSnackbar("Couldn't resize this image. Try smaller dimensions.") }
                                finally { busy = false }
                            }
                        }
                    }
                },
                onPercent = { percent ->
                    val src = originalBitmap
                    if (src != null) {
                        val size = runCatching { ResizeMath.fromPercent(src.width, src.height, percent) }.getOrElse {
                            scope.launch { snackbar.showSnackbar(it.message ?: "Invalid percentage") }; null
                        }
                        if (size != null) { inputWidth = size.width.toString(); inputHeight = size.height.toString() }
                    }
                },
                imageName = imageName,
                onExport = {
                    val result = editedBitmap
                    if (result == null) scope.launch { snackbar.showSnackbar("Select a photo first.") }
                    else {
                        busy = true
                        scope.launch {
                            try {
                                val out = withContext(Dispatchers.IO) { saveBitmap(context, result, imageName) }
                                savedName = out.first
                                savedSize = out.second
                                screen = Screen.Success
                            } catch (e: Exception) { snackbar.showSnackbar("Save failed. Please try again.") }
                            finally { busy = false }
                        }
                    }
                },
                onName = { imageName = it }
            )
            Screen.Print -> PrintCalculator(Modifier.padding(padding), onApply = { scope.launch { snackbar.showSnackbar("Select a photo to apply a print-size calculation.") } })
            Screen.Success -> SuccessScreen(
                modifier = Modifier.padding(padding), name = savedName, size = savedSize,
                onDone = { screen = Screen.Home; tab = MainTab.Home; originalBitmap = null; editedBitmap = null },
                onAnother = { screen = Screen.Home; tab = MainTab.Home; originalBitmap = null; editedBitmap = null; singlePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            )
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier, onSelectPhoto: () -> Unit, onBatch: () -> Unit, onPrint: () -> Unit, onFeature: (String) -> Unit) {
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
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("PixelCraft", color = DeepNavy, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("IMAGE RESIZER", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Icon(Icons.Default.Tune, contentDescription = "Settings", tint = DeepNavy, modifier = Modifier.size(26.dp))
        }
        Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Resize, crop and remove backgrounds in seconds", color = DeepNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                Spacer(Modifier.height(14.dp))
                Button(onClick = onSelectPhoto, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Default.Image, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Select Photo", fontWeight = FontWeight.Bold)
                }
            }
        }
        Text("Your creator toolkit", modifier = Modifier.padding(top = 20.dp, bottom = 10.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepNavy)
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(features) { feature ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onFeature(feature.title) },
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5EEF4)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(12.dp).height(106.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Box(Modifier.size(34.dp).clip(CircleShape).background(PaleBlue), contentAlignment = Alignment.Center) { Text(feature.icon, color = SkyBlue, fontWeight = FontWeight.Bold, fontSize = 19.sp) }
                        Text(feature.title, color = DeepNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 17.sp)
                        Text(feature.description, color = Color(0xFF66758A), fontSize = 11.sp, lineHeight = 14.sp)
                    }
                }
            }
        }
        Text("Stage 1 preview · More tools are being built", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), color = Color(0xFF66758A), fontSize = 11.sp)
    }
}

@Composable
private fun EditorScreen(
    modifier: Modifier, original: Bitmap?, edited: Bitmap?, widthText: String, heightText: String,
    lockRatio: Boolean, busy: Boolean, beforeAfter: Boolean, tool: Tool,
    onBeforeAfter: (Boolean) -> Unit, onTool: (Tool) -> Unit, onWidth: (String) -> Unit,
    onHeight: (String) -> Unit, onLockRatio: (Boolean) -> Unit, onResize: () -> Unit,
    onPercent: (Double) -> Unit, imageName: String, onExport: () -> Unit, onName: (String) -> Unit
) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(false to "Before", true to "After").forEach { (after, label) ->
                val selected = beforeAfter == after
                OutlinedButton(onClick = { onBeforeAfter(after) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) PaleBlue else Color.Transparent)) { Text(label, color = DeepNavy) }
            }
        }
        Box(Modifier.fillMaxWidth().weight(0.38f).clip(RoundedCornerShape(18.dp)).background(SoftGrey).border(1.dp, Color(0xFFE0E8EE), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
            val display = if (beforeAfter) edited else original
            if (display != null) Image(bitmap = display.asImageBitmap(), contentDescription = "Image preview", modifier = Modifier.fillMaxSize().padding(8.dp), contentScale = androidx.compose.ui.layout.ContentScale.Fit)
            else Text("Select a photo to begin", color = DeepNavy)
            if (busy) CircularProgressIndicator(color = Orange)
        }
        if (edited != null) {
            val bytes = remember(edited) { edited.byteCount }
            Text("${edited.width} × ${edited.height} px  ·  ${formatBytes(bytes.toLong())}  ·  ${if (edited.hasAlpha()) "PNG-ready" else "JPEG-ready"}", modifier = Modifier.padding(vertical = 8.dp), fontSize = 12.sp, color = Color(0xFF66758A))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            Tool.values().forEach { item ->
                val selected = tool == item
                Surface(modifier = Modifier.weight(1f).clickable { onTool(item) }, shape = RoundedCornerShape(10.dp), color = if (selected) SkyBlue else SoftGrey) {
                    Text(item.label, modifier = Modifier.padding(vertical = 9.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 10.sp, color = if (selected) Color.White else DeepNavy, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
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
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(25, 50, 75, 100, 125, 200).forEach { percent ->
                    Surface(modifier = Modifier.weight(1f).clickable { onPercent(percent.toDouble()) }, shape = RoundedCornerShape(9.dp), color = PaleBlue) {
                        Text("$percent%", modifier = Modifier.padding(vertical = 9.dp), fontSize = 10.sp, color = DeepNavy, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
            OutlinedTextField(value = imageName, onValueChange = onName, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Output file name (PNG)") }, singleLine = true)
        Text("This first build exports PNG. Crop, compression, format controls and background removal are planned for later stages.", color = Color(0xFF66758A), fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 8.dp))
        } else {
            Text("${tool.label} controls will be added in the next development stages. Basic resize and export are available now.", color = DeepNavy, fontSize = 13.sp)
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = onExport, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White), shape = RoundedCornerShape(14.dp), enabled = !busy && edited != null) {
            if (busy) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp) else Text("Export", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun PresetsScreen(modifier: Modifier, onPick: () -> Unit) {
    val platforms = listOf("Etsy", "Gumroad", "Payhip", "Spotify", "YouTube", "Instagram", "Facebook", "Pinterest", "TikTok", "X", "LinkedIn", "Snapchat", "Tumblr")
    var search by rememberSaveable { mutableStateOf("") }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Presets", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Shop & creator · Social media", color = Color(0xFF66758A), modifier = Modifier.padding(top = 4.dp, bottom = 14.dp))
        OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Search platforms") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        Text("Preset library is being prepared", color = DeepNavy, fontWeight = FontWeight.SemiBold)
        Text("Sizes will include a source and verification status. Unverified sizes will not be labelled Verified.", color = Color(0xFF66758A), fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
        platforms.filter { it.contains(search, ignoreCase = true) }.forEach { platform ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(12.dp)).background(PaleBlue).clickable { onPick() }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(34.dp).clip(CircleShape).background(SkyBlue), contentAlignment = Alignment.Center) { Text(platform.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
                Text(platform, modifier = Modifier.weight(1f).padding(start = 12.dp), color = DeepNavy, fontWeight = FontWeight.SemiBold)
                Text("Select photo", color = DeepNavy, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier, darkMode: Boolean, onDarkMode: (Boolean) -> Unit, onPrivacy: () -> Unit) {
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Text("Settings", color = DeepNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Text("Appearance", color = DeepNavy, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (darkMode) "Dark mode" else "Light mode", modifier = Modifier.weight(1f), color = DeepNavy)
            Checkbox(checked = darkMode, onCheckedChange = onDarkMode)
        }
        Spacer(Modifier.height(14.dp))
        OutlinedButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth()) { Text("Privacy policy (added before release)") }
        Spacer(Modifier.height(12.dp))
        Text("PixelCraft Image Resizer", color = DeepNavy, fontWeight = FontWeight.SemiBold)
        Text("Version 0.1.0 · Learnify Zone", color = Color(0xFF66758A), fontSize = 13.sp)
        Text("Stage 1 build. Ads are not enabled in this development version.", color = Color(0xFF66758A), fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
    }
}

@Composable
private fun PrintCalculator(modifier: Modifier, onApply: () -> Unit) {
    var width by rememberSaveable { mutableStateOf("20") }
    var height by rememberSaveable { mutableStateOf("25") }
    var dpi by rememberSaveable { mutableStateOf("300") }
    val w = width.toDoubleOrNull()
    val h = height.toDoubleOrNull()
    val density = dpi.toDoubleOrNull()
    val pxW = if (w != null && density != null && w > 0 && density > 0) (w / 2.54 * density).toInt() else null
    val pxH = if (h != null && density != null && h > 0 && density > 0) (h / 2.54 * density).toInt() else null
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Text("Print Size & DPI", color = DeepNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Print size → required pixels", color = Color(0xFF66758A), modifier = Modifier.padding(top = 4.dp, bottom = 18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(width, { width = it }, modifier = Modifier.weight(1f), label = { Text("Width (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            OutlinedTextField(height, { height = it }, modifier = Modifier.weight(1f), label = { Text("Height (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
        }
        OutlinedTextField(dpi, { dpi = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("DPI") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
        Card(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), colors = CardDefaults.cardColors(containerColor = PaleBlue)) {
            Column(Modifier.padding(18.dp)) {
                Text(if (pxW != null && pxH != null) "$pxW × $pxH px" else "Enter valid values", color = DeepNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("${width} × ${height} cm at ${dpi} DPI", color = Color(0xFF66758A), fontSize = 13.sp)
            }
        }
        Text("Changing DPI alone does not add image detail. This calculator estimates the pixels needed for a print.", color = Color(0xFF66758A), fontSize = 12.sp, modifier = Modifier.padding(vertical = 14.dp))
        Button(onClick = onApply, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("Apply to photo", color = Color.White) }
    }
}

@Composable
private fun SuccessScreen(modifier: Modifier, name: String, size: String, onDone: () -> Unit, onAnother: () -> Unit) {
    Column(modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E9D68), modifier = Modifier.size(70.dp))
        Spacer(Modifier.height(16.dp))
        Text("Saved successfully", color = DeepNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(name, color = DeepNavy, modifier = Modifier.padding(top = 8.dp))
        Text(size, color = Color(0xFF66758A), modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
        Button(onClick = onAnother, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Orange)) { Text("Edit another photo", color = Color.White) }
        OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Done", color = DeepNavy) }
    }
}

private fun decodeOrientedBitmap(context: android.content.Context, uri: Uri): Bitmap {
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

private fun saveBitmap(context: android.content.Context, bitmap: Bitmap, requestedName: String): Pair<String, String> {
    val entered = requestedName.trim().ifBlank { "pixelcraft-image" }
        .replace(Regex("[^A-Za-z0-9._ -]"), "_").take(80)
    val baseName = entered.replace(Regex("\\.(png|jpe?g|webp)$", RegexOption.IGNORE_CASE), "").ifBlank { "pixelcraft-image" }
    val filename = "$baseName.png"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= 29) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PixelCraft")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val resolver = context.contentResolver
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val uri = resolver.insert(collection, values) ?: throw IllegalStateException("Could not create output file")
    try {
        resolver.openOutputStream(uri)?.use { stream ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) throw IllegalStateException("Image encoding failed")
        } ?: throw IllegalStateException("Could not open output file")
        if (Build.VERSION.SDK_INT >= 29) resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
        val bytes = resolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
        return filename to formatBytes(bytes)
    } catch (e: Exception) {
        resolver.delete(uri, null, null)
        throw e
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "${DecimalFormat("#,##0.#").format(kb)} KB"
    return "${DecimalFormat("#,##0.##").format(kb / 1024.0)} MB"
}
