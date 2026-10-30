package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
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

/** v0.18.0 Photo Adjustments screen at a REAL phone window (w360dp-h640dp). Screenshots inspected before delivery. */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PhotoAdjustFlowTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launchRaw(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        extras.forEach { (k, v) -> intent = intent.putExtra(k, v) }
        return ActivityScenario.launch(intent)
    }

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(800, TimeUnit.MILLISECONDS)
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

    @Test
    fun adjustScreenShowsSlidersAndControls() {
        // Mockup structure: preview, Undo/Redo/Compare/Reset, Adjust/Filters tiles, the 12 named
        // sliders in his 4 categories, save settings, Save Image.
        launchRaw("pixelcraft.test.adjust" to "1").use { sc ->
            waitForText("Undo")
            waitForText("Compare")
            waitForText("Reset")
            waitForText("Adjust")
            waitForText("Filters")
            waitForText("Original: 800 x 600")
            waitForText("Brightness")
            waitForText("Exposure")
            waitForText("Contrast")
            waitForText("Highlights")
            waitForText("Shadows")
            shot(sc, "v0180_phone_adjust_top")
            composeRule.onNodeWithText("Save Image").performScrollTo()
            waitForText("Saturation")
            waitForText("Vibrance")
            waitForText("Temperature")
            waitForText("Tint")
            waitForText("Sharpness")
            waitForText("Clarity")
            waitForText("Fade")
            waitForText("Save settings")
            waitForText("Original size")
            waitForText("Save Image")
            shot(sc, "v0180_phone_adjust_bottom")
            sc.close()
        }
    }

    @Test
    fun adjustCompareAndFiltersWork() {
        launchRaw("pixelcraft.test.adjust" to "1").use { sc ->
            waitForText("Compare")
            composeRule.onNodeWithText("Compare").performClick()
            waitForText("showing the original")
            composeRule.onNodeWithText("Filters").performClick()
            waitForText("B&W")
            composeRule.onNodeWithText("B&W").performClick()
            waitForText("Warm")
            shot(sc, "v0180_phone_adjust_filters")
            sc.close()
        }
    }

    @Test
    fun adjustResetStartsFromZeroWithNewImageOption() {
        // His report (v0.18.2): "Reset button not working ... it should start from zero with
        // new image option". Set a slider through its real semantics action, then Reset.
        launchRaw("pixelcraft.test.adjust" to "1").use { sc ->
            waitForText("Brightness")
            composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress))[0]
                .performSemanticsAction(SemanticsActions.SetProgress) { it(80f) }
            pump(); pump()
            // Value label shows the set value (exact match - "800 x 600" must not false-pass).
            run {
                val deadline = System.currentTimeMillis() + 30_000
                while (System.currentTimeMillis() < deadline) {
                    pump()
                    if (composeRule.onAllNodesWithText("80").fetchSemanticsNodes().isNotEmpty()) break
                    Thread.sleep(60)
                }
                assert(composeRule.onAllNodesWithText("80").fetchSemanticsNodes().isNotEmpty()) { "slider value 80 not shown" }
            }
            shot(sc, "v0183_phone_adjust_set80")
            composeRule.onNodeWithText("Reset").performClick()
            pump(); pump()
            // Full wipe: new-image option, no photo, no edit controls left on screen.
            waitForText("Adjust your photo")
            waitForText("Choose a photo")
            composeRule.onNodeWithText("Reset").assertDoesNotExist()
            composeRule.onNodeWithText("Undo").assertDoesNotExist()
            shot(sc, "v0183_phone_adjust_reset")
            sc.close()
        }
    }

    @Test
    fun adjustCompareShowsTrueOriginal() {
        // His report (v0.18.3): "compare button not working" - photo + Warm filter + Compare.
        // The old compare view kept the filter, so edited and "original" looked identical.
        // This test asserts PIXELS, not just the caption (the caption alone passed in v0.18.0).
        launchRaw("pixelcraft.test.adjust" to "1").use { sc ->
            waitForText("Compare")
            composeRule.onNodeWithText("Filters").performClick()
            waitForText("Warm")
            composeRule.onNodeWithText("Warm").performClick()
            pump(); pump(); pump()
            shot(sc, "v0184_phone_adjust_warm")
            composeRule.onNodeWithText("Compare").performClick()
            pump(); pump(); pump()
            waitForText("showing the original")
            shot(sc, "v0184_phone_adjust_compare_original")
            // The compare view must be the TRUE original: visibly different pixels from the
            // Warm-filtered view (old code failed this - both showed the Warm image).
            val dir = File("build/outputs/uishots")
            val a = android.graphics.BitmapFactory.decodeFile(File(dir, "v0184_phone_adjust_warm.png").absolutePath)
            val b = android.graphics.BitmapFactory.decodeFile(File(dir, "v0184_phone_adjust_compare_original.png").absolutePath)
            assert(a != null && b != null && a.width == b.width && a.height == b.height) { "screenshots unreadable" }
            var diff = 0L; var n = 0
            var y = 0
            while (y < a.height) {
                var x = 0
                while (x < a.width) {
                    val pa = a.getPixel(x, y); val pb = b.getPixel(x, y)
                    diff += kotlin.math.abs(((pa shr 16) and 0xFF) - ((pb shr 16) and 0xFF)) +
                            kotlin.math.abs(((pa shr 8) and 0xFF) - ((pb shr 8) and 0xFF)) +
                            kotlin.math.abs((pa and 0xFF) - (pb and 0xFF))
                    n++
                    x += 16
                }
                y += 16
            }
            val mean = diff / n
            assert(mean > 20) { "compare view did not change the preview (mean diff=$mean)" }
            // Toggle back returns to the filtered/edited view.
            composeRule.onNodeWithText("Edited").performClick()
            pump(); pump()
            run {
                val deadline = System.currentTimeMillis() + 30_000
                while (System.currentTimeMillis() < deadline) {
                    pump()
                    if (composeRule.onAllNodesWithText("showing the original").fetchSemanticsNodes().isEmpty()) break
                    Thread.sleep(60)
                }
                assert(composeRule.onAllNodesWithText("showing the original").fetchSemanticsNodes().isEmpty()) { "compare did not toggle back" }
            }
            sc.close()
        }
    }

    @Test
    fun adjustHomeTileExists() {
        // The new tile fills the last empty slot on Home.
        launchRaw().use { sc ->
            waitForText("Photo Adjustments")
            waitForText("Light, colour & detail sliders")
            sc.close()
        }
    }
}
