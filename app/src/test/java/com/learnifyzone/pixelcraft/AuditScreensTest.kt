package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
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
 * v0.18.0 audit rig (his "Review 1 by 1 deeply every small detail of every tab section"):
 * renders EVERY screen at real phone size (w360dp-h640dp) for visual inspection.
 * Findings only - this class changes no app behaviour.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AuditScreensTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launch(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_audit", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
        extras.forEach { (k, v) -> intent = intent.putExtra(k, v) }
        return ActivityScenario.launch(intent)
    }

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
        scenario.onActivity { act ->
            val v = act.window.decorView
            val bmp = android.graphics.Bitmap.createBitmap(v.width, v.height, android.graphics.Bitmap.Config.ARGB_8888)
            v.draw(android.graphics.Canvas(bmp))
            bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, File(dir, "$name.png").outputStream())
        }
        pump()
    }

    private fun launchHome(): ActivityScenario<MainActivity> {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        return ActivityScenario.launch(intent)
    }

    @Test
    fun audit01HomeAndTabs() {
        launchHome().use { sc ->
            waitForText("A2Z Image Craft")
            waitForText("Photo Adjustments")
            shot(sc, "audit_home")
            composeRule.onNodeWithText("My Photos").performClick()
            pump(); pump()
            shot(sc, "audit_tab_myphotos")
            composeRule.onNodeWithText("Presets").performClick()
            pump(); pump()
            shot(sc, "audit_tab_presets")
            composeRule.onNodeWithText("Settings").performClick()
            pump(); pump()
            shot(sc, "audit_tab_settings")
            sc.close()
        }
    }

    @Test
    fun audit02EditorResize() {
        launch().use { sc ->
            waitForText("Resize", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_editor_resize")
            sc.close()
        }
    }

    @Test
    fun audit03CropTool() {
        launch("pixelcraft.test.tool" to "crop").use { sc ->
            waitForText("Crop", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_editor_crop")
            sc.close()
        }
    }

    @Test
    fun audit04CircleTool() {
        launch("pixelcraft.test.tool" to "circle").use { sc ->
            waitForText("Circle", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_editor_circle")
            sc.close()
        }
    }

    @Test
    fun audit05Social() {
        launch("pixelcraft.test.tool" to "social").use { sc ->
            waitForText("Social", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_social_home")
            composeRule.onNodeWithText("Instagram", substring = true).performClick()
            waitForText("Official size", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_social_labels")
            sc.close()
        }
    }

    @Test
    fun audit10BackgroundTool() {
        launch("pixelcraft.test.tool" to "background").use { sc ->
            waitForText("Remove Background", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_editor_background")
            sc.close()
        }
    }

    @Test
    fun audit06Shop() {
        launch("pixelcraft.test.tool" to "shop").use { sc ->
            waitForText("Shop", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_shop_home")
            sc.close()
        }
    }

    @Test
    fun audit07Convert() {
        launch("pixelcraft.test.tool" to "convert").use { sc ->
            waitForText("Convert", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_convert_home")
            sc.close()
        }
        launch("pixelcraft.test.tool" to "convertedit").use { sc ->
            waitForText("Convert", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_convert_edit")
            sc.close()
        }
    }

    @Test
    fun audit08Compress() {
        launch("pixelcraft.test.tool" to "compresssettings").use { sc ->
            waitForText("Compress", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_compress_settings")
            sc.close()
        }
    }

    @Test
    fun audit09Print() {
        launch("pixelcraft.test.tool" to "printsetup").use { sc ->
            waitForText("Print", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "audit_print_setup")
            sc.close()
        }
    }
}
