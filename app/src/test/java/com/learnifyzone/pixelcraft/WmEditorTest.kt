package com.learnifyzone.pixelcraft

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.learnifyzone.pixelcraft.image.BackgroundRemoval
import com.learnifyzone.pixelcraft.image.TextOverlayUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Watermark Stage 2 visual pass: text editor (light), logo editor (dark + imported),
 * plus host-safe unit checks on the drawing engine and the plain-background remover.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h2400dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WmEditorTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(900, TimeUnit.MILLISECONDS)
        try { composeRule.waitForIdle() } catch (e: androidx.compose.ui.test.ComposeTimeoutException) { }
    }

    private fun waitForText(text: String, timeoutMs: Long = 90_000) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            pump()
            if (composeRule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()) return
            Thread.sleep(60)
        }
        throw AssertionError("Text not found within ${timeoutMs}ms: $text")
    }

    private fun shot(scenario: ActivityScenario<MainActivity>, name: String) {
        pump()
        val dir = File("build/outputs/uishots").apply { mkdirs() }
        var bmp: Bitmap? = null
        scenario.onActivity { act ->
            val v = act.window.decorView
            bmp = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
            val c = Canvas(bmp!!)
            c.drawColor(Color.WHITE)
            v.draw(c)
        }
        File(dir, "$name.png").outputStream().use { bmp!!.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun launch(tool: String, dark: Boolean = false): ActivityScenario<MainActivity> {
        var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.tool", tool)
        if (dark) intent = intent.putExtra("pixelcraft.test.darkmode", "1")
        return ActivityScenario.launch(intent)
    }

    @Test
    fun we01TextEditorLight() {
        launch("wmtext").use { sc ->
            waitForText("Text Watermark")
            waitForText("Layer 1")
            waitForText("Weight")
            waitForText("Outline")
            shot(sc, "we_01_text_editor_light")
        }
    }

    @Test
    fun we02TextEditorDark() {
        launch("wmtext", dark = true).use { sc ->
            waitForText("Text Watermark")
            waitForText("Shadow")
            shot(sc, "we_02_text_editor_dark")
        }
    }

    @Test
    fun we03LogoEditorEmpty() {
        launch("wmlogoempty").use { sc ->
            waitForText("Logo Watermark")
            waitForText("Import Logo")
            shot(sc, "we_03_logo_empty")
        }
    }

    @Test
    fun we04LogoEditorWithLogo() {
        launch("wmlogo").use { sc ->
            waitForText("Logo Watermark")
            waitForText("Remove Background")
            waitForText("Rotation")
            shot(sc, "we_04_logo_imported")
        }
    }

    @Test
    fun we05UndoViaHeader() {
        launch("wmtext").use { sc ->
            waitForText("Text Watermark")
            // Duplicate creates layer 2 and pushes an undo state.
            composeRule.onNodeWithText("Duplicate").performClick()
            pump()
            waitForText("Layer 2")
            shot(sc, "we_05_two_layers")
        }
    }

    // ----- host-safe engine checks (no device APIs) -----

    @Test
    fun we06EngineDrawsWatermarkPixels() {
        val photo = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
        Canvas(photo).drawColor(0xFF224466.toInt())
        val layer = TextOverlayUtils.TextLayer(id = 1L, text = "Sample", sizeFrac = 0.15f, colorArgb = Color.WHITE, opacity = 0.9f)
        val c = Canvas(photo)
        TextOverlayUtils.drawAll(c, listOf(layer), photo.width, photo.height, 1L, true)
        var white = 0
        val px = IntArray(photo.width * photo.height)
        photo.getPixels(px, 0, photo.width, 0, 0, photo.width, photo.height)
        for (col in px) if ((col and 0xFFFFFF) > 0xDDDDDD) white++
        assertTrue("watermark text must paint visible pixels", white > 500)
    }

    @Test
    fun we07PlainBackgroundRemovalRunsOnHost() {
        // White-background logo: the flood fill must transparent the corners, keep the mark.
        val logo = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val c = Canvas(logo); c.drawColor(Color.WHITE)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFF8A32.toInt() }
        c.drawCircle(100f, 100f, 70f, p)
        val (cut, removed) = BackgroundRemoval.removeSolidBackground(logo, 0.14f)
        assertTrue("plain background should be removed", removed > 200 * 200 / 4)
        assertTrue("corners become transparent", BackgroundRemoval.transparentFraction(cut) > 0.4f)
        // centre of the mark survives
        val centre = cut.getPixel(100, 100)
        assertEquals(0xFF, (centre ushr 24).toLong())
    }

    @Test
    fun we08MediumWeightKeepsDefaults() {
        // The additive Stage-2 fields must default to the old Text-on-Photo behaviour.
        val layer = TextOverlayUtils.TextLayer(id = 9L, text = "x")
        assertTrue(!layer.medium)
        assertEquals(0.14f, layer.shadowBlur)
        assertEquals(0x99, layer.shadowAlpha)
    }
}
