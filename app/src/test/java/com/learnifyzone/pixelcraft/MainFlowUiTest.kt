package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File

/**
 * Single-photo editor render check on the JVM (Robolectric), via the
 * pixelcraft.test.photo intent hook: photo loads and the editor shows its
 * tools, unit chips and DPI field.
 *
 * NOTE: the options bottom sheet and the resize-apply navigation cannot run
 * under Robolectric (ModalBottomSheet popup windows never attach in this
 * environment, verified across multiple attempts). Those paths are covered by
 * static trace review instead.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class MainFlowUiTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launchWithPhoto(): ActivityScenario<MainActivity> {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_ui_test", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
        return ActivityScenario.launch(intent)
    }

    private fun waitForText(text: String, substring: Boolean = false, timeoutMs: Long = 60_000) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            // Robolectric's main looper is paused: advance it so coroutine work lands.
            shadowOf(Looper.getMainLooper()).idleFor(500, java.util.concurrent.TimeUnit.MILLISECONDS)
            composeRule.waitForIdle()
            if (composeRule.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isNotEmpty()) return
            Thread.sleep(50)
        }
        throw AssertionError("Text not found within ${timeoutMs}ms: $text")
    }

    @Test
    fun photoLoads_editorShowsToolsUnitsAndDpi() {
        launchWithPhoto().use {
            waitForText("Process")
            waitForText("Pixels")
            waitForText("Inches")
            waitForText("mm")
            waitForText("DPI - 300 is standard for print")
            waitForText("Export")
        }
    }

    @Test
    fun cropRotateToolShowsItsControls() {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_ui_test_crop", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "crop")
        ActivityScenario.launch<MainActivity>(intent).use {
            waitForText("Aspect ratio")
            waitForText("Apply Crop")
            waitForText("Mirror")
            waitForText("Left 90°")
            waitForText("16:9")
            waitForText("Straighten")
            waitForText("Horizontal")
            waitForText("Vertical")
        }
    }

    @Test
    fun circleCutterToolShowsItsControls() {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_ui_test_circle", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "circle")
        ActivityScenario.launch<MainActivity>(intent).use {
            waitForText("Shape")
            waitForText("Heart")
            waitForText("Rounded")
            waitForText("Custom Color")
            waitForText("Crop")
            waitForText("DPI - 300 is standard for print")
        }
    }

    @Test
    fun socialSizesHomeShowsPlatforms() {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_ui_test_social", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "social")
        ActivityScenario.launch<MainActivity>(intent).use {
            waitForText("Social Media Sizes")
            waitForText("Every platform. Every image size.")
            waitForText("Select Your Image")
            waitForText("Instagram")
            waitForText("YouTube")
            waitForText("WhatsApp")
        }
    }

    @Test
    fun shopSizesHomeShowsShops() {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_ui_test_shop", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "shop")
        ActivityScenario.launch<MainActivity>(intent).use {
            waitForText("Online Shop Sizes")
            waitForText("Every shop. Every product image.")
            waitForText("Select Your Image")
            waitForText("Etsy")
            waitForText("Gumroad")
            waitForText("Other Shop")
        }
    }

    @Test
    fun convertHomeShowsAllSixFormats() {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_ui_test_convert", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "convert")
        ActivityScenario.launch<MainActivity>(intent).use {
            waitForText("Convert images into the format you need.")
            waitForText("Select Your Image")
            waitForText("Convert to")
            waitForText("JPG/JPEG")
            waitForText("PDF")
            waitForText("TIFF")
        }
    }
}