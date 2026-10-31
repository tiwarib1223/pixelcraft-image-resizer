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
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
 * Watermark Stage 1 visual pass: home (light + dark), settings, choose-type.
 * Tall-screen qualifier so every element is composed for the shots.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h2400dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WmScreensTest {

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
        val view = scenario.onActivity { it.window.decorView }.let { null }
        // Draw the activity view hierarchy to a bitmap (same approach as the scanner shots).
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
    fun wm01HomeLight() {
        launch("watermark").use { sc ->
            waitForText("Select Images")
            waitForText("Text Watermark")
            waitForText("Logo Watermark")
            waitForText("Recent Projects")
            shot(sc, "wm_home")
            sc.close()
        }
    }

    @Test
    fun wm02HomeDark() {
        launch("watermark", dark = true).use { sc ->
            waitForText("Select Images")
            waitForText("Recent Projects")
            shot(sc, "wm_home_dark")
            sc.close()
        }
    }

    @Test
    fun wm03Type() {
        launch("wmtype").use { sc ->
            waitForText("Choose Watermark Type")
            waitForText("2 images ready")
            waitForText("Create a watermark using custom text.")
            waitForText("Use an existing brand logo or transparent PNG.")
            shot(sc, "wm_type")
            sc.close()
        }
    }

    @Test
    fun wm04Settings() {
        launch("watermark").use { sc ->
            waitForText("Select Images")
            composeRule.onNodeWithContentDescription("Watermark settings").performClick()
            waitForText("Watermark Settings")
            waitForText("Default export format")
            waitForText("WebP")
            waitForText("Theme")
            waitForText("Reset Watermark Settings")
            shot(sc, "wm_settings")
            sc.close()
        }
    }
}
