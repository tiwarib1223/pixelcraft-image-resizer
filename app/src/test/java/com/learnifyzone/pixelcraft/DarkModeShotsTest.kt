package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

/** v1.0.7 (his dark-mode sweep): visual self-review of dark mode across key screens. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DarkModeShotsTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(800, java.util.concurrent.TimeUnit.MILLISECONDS)
        try { composeRule.waitForIdle() } catch (e: androidx.compose.ui.test.ComposeTimeoutException) { }
    }

    private fun waitForText(text: String, timeoutMs: Long = 60_000) {
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

    @Test
    fun darkModeSweepShots() {
        val photo = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_dark", ".jpg")
        tmp.writeBytes(photo)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.darkmode", "1")
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
        ActivityScenario.launch<MainActivity>(intent).use { sc ->
            // Resize editor in dark mode (photo loaded)
            waitForText("Process")
            waitForText("Export")
            shot(sc, "v107_dark_editor_resize")
            sc.close()
        }
        // Export page in dark mode (test hook lands directly on Export)
        val intentExport = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.darkmode", "1")
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "export")
        ActivityScenario.launch<MainActivity>(intentExport).use { sc ->
            waitForText("File name")
            shot(sc, "v107_dark_export")
            sc.close()
        }
        // Home grid + Settings + drawer doc page in dark mode
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).putExtra("pixelcraft.test.darkmode", "1")).use { sc ->
            waitForText("PHOTO TOOLKIT")
            shot(sc, "v107_dark_home")
            composeRule.onAllNodesWithText("Settings")[0].performClick()
            pump()
            waitForText("Theme")
            shot(sc, "v107_dark_settings")
            sc.close()
        }
        // My Photos tab in dark mode
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).putExtra("pixelcraft.test.darkmode", "1")).use { sc ->
            waitForText("PHOTO TOOLKIT")
            composeRule.onAllNodesWithText("My Photos")[0].performClick()
            pump()
            waitForText("My Photos")
            pump()
            shot(sc, "v107_dark_myphotos")
            sc.close()
        }
        // Tool screens in dark mode via the photo pre-load hooks
        fun toolShot(tool: String, wait: String, name: String) {
            val i = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
                .putExtra("pixelcraft.test.darkmode", "1")
                .putExtra("pixelcraft.test.photo", tmp.absolutePath)
                .putExtra("pixelcraft.test.tool", tool)
            ActivityScenario.launch<MainActivity>(i).use { sc ->
                waitForText(wait, 120_000)
                shot(sc, name)
                sc.close()
            }
        }
        toolShot("print", "Set Up Print Size", "v107_dark_print")
        toolShot("compress", "Quality Compression", "v107_dark_compress")
        toolShot("convert", "Convert to", "v107_dark_convert")
        toolShot("social", "Social Media Sizes", "v107_dark_social")
        toolShot("shop", "Online Shop Sizes", "v107_dark_shop")
        // Text on Photo workspace in dark mode
        val itp = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.darkmode", "1")
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "textphoto")
        ActivityScenario.launch<MainActivity>(itp).use { sc ->
            waitForText("Add Image", 120_000)
            shot(sc, "v107_dark_textphoto")
            sc.close()
        }
        // Collage workspace in dark mode (synthetic photos)
        val icol = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.darkmode", "1")
            .putExtra("pixelcraft.test.collage", "1")
            .putExtra("pixelcraft.test.collagelevel", "edit")
        ActivityScenario.launch<MainActivity>(icol).use { sc ->
            waitForText("Spacing between photos", 120_000)
            shot(sc, "v107_dark_collage")
            sc.close()
        }
        // Inner tool screens in dark mode
        toolShot("printsetup", "Print Resolution (PPI)", "v107_dark_print_setup")
        toolShot("crop", "Apply Crop", "v107_dark_crop")
        // Photo Adjustments editor in dark mode
        val iadj = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.darkmode", "1")
            .putExtra("pixelcraft.test.adjust", "1")
        ActivityScenario.launch<MainActivity>(iadj).use { sc ->
            waitForText("Photo Adjustments", 120_000)
            shot(sc, "v107_dark_adjust")
            sc.close()
        }
        // Social platform preset page (Instagram) in dark mode
        val isoc = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.darkmode", "1")
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "social")
        ActivityScenario.launch<MainActivity>(isoc).use { sc ->
            waitForText("Instagram", 120_000)
            composeRule.onAllNodesWithText("Instagram")[0].performClick()
            waitForText("Official size", 120_000)
            pump()
            shot(sc, "v107_dark_social_presets")
            sc.close()
        }
        // Shop platform preset page (Etsy) in dark mode
        val ishp = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.darkmode", "1")
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "shop")
        ActivityScenario.launch<MainActivity>(ishp).use { sc ->
            waitForText("Etsy", 120_000)
            composeRule.onAllNodesWithText("Etsy")[0].performClick()
            waitForText("Official size", 120_000)
            pump()
            shot(sc, "v107_dark_shop_presets")
            sc.close()
        }
        // Presets tab + menu doc pages in dark mode
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).putExtra("pixelcraft.test.darkmode", "1")).use { sc ->
            waitForText("PHOTO TOOLKIT")
            composeRule.onAllNodesWithText("Presets")[0].performClick()
            pump()
            waitForText("Ready-made sizes")
            shot(sc, "v107_dark_presets_tab")
            composeRule.onAllNodesWithText("Home")[0].performClick()
            pump()
            waitForText("PHOTO TOOLKIT")
            composeRule.onNodeWithContentDescription("Menu").performClick()
            pump()
            waitForText("Help & tips")
            composeRule.onAllNodesWithText("Help & tips")[0].performClick()
            pump()
            waitForText("Help & tips")
            pump()
            shot(sc, "v107_dark_doc_help")
            composeRule.onNodeWithContentDescription("Back").performClick()
            pump()
            waitForText("Privacy Policy")
            composeRule.onAllNodesWithText("Privacy Policy")[0].performClick()
            pump()
            waitForText("Privacy Policy")
            pump()
            shot(sc, "v107_dark_doc_privacy")
            composeRule.onNodeWithContentDescription("Back").performClick()
            pump()
            waitForText("Help & tips")
            // The row sits below the drawer fold: scroll to it, then wait for the page
            // BODY (unique to the doc page) so the shot cannot catch the drawer instead.
            composeRule.onAllNodesWithText("What's new")[0].performScrollTo().performClick()
            pump()
            waitForText("Welcome to A2Z Image Craft")
            pump()
            shot(sc, "v107_dark_doc_whatsnew")
            composeRule.onNodeWithContentDescription("Back").performClick()
            pump()
            waitForText("Help & tips")
            composeRule.onAllNodesWithText("Terms & Conditions")[0].performClick()
            pump()
            waitForText("Terms & Conditions")
            pump()
            shot(sc, "v107_dark_doc_terms")
            composeRule.onNodeWithContentDescription("Back").performClick()
            pump()
            waitForText("Help & tips")
            composeRule.onAllNodesWithText("Open-source licences")[0].performClick()
            pump()
            waitForText("Open-source licences")
            pump()
            shot(sc, "v107_dark_doc_licences")
            sc.close()
        }
    }
}
