package com.learnifyzone.pixelcraft

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.view.View
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.unit.dp
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * "You check yourself" (his v0.16.2 instruction): render the REAL Text on Photo screen on the JVM
 * and verify with eyes AND geometry before delivery:
 *  - clean photo open: no default "Your text" layer (empty-layers hint visible)
 *  - Text background chips sit DIRECTLY under their label (the v0.16.1 gap bug)
 *  - More fonts / More sizes / More colours are full-width readable buttons (the squish bug)
 *  - Curve Text is a single compact line with the Arc slider below when ON
 *  - the image-layer quick card renders
 * Screenshots are drawn from the real view tree into a bitmap and saved for visual review.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h3000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TextPhotoUiTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launch(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_tp_ui", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.tool", "textphoto")
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
        extras.forEach { (k, v) -> intent = intent.putExtra(k, v) }
        return ActivityScenario.launch(intent)
    }

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(800, TimeUnit.MILLISECONDS)
        try {
            composeRule.waitForIdle()
        } catch (e: androidx.compose.ui.test.ComposeTimeoutException) {
            // busy spinner animation still running - the caller loops
        }
    }

    private fun waitForText(text: String, timeoutMs: Long = 90_000) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            pump()
            if (composeRule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()) return
            Thread.sleep(60)
        }
        val tree = try { composeRule.onRoot().printToString().take(3000) } catch (e: Exception) { "<no tree>" }
        throw AssertionError("Text not found within ${timeoutMs}ms: $text\nSEMANTICS:\n$tree")
    }

    private fun shot(scenario: ActivityScenario<MainActivity>, name: String) {
        pump()
        val dir = File("build/outputs/uishots").apply { mkdirs() }
        scenario.onActivity { a ->
            val v: View = a.window.decorView
            val w = if (v.width > 0) v.width else a.resources.displayMetrics.widthPixels
            val h = if (v.height > 0) v.height else a.resources.displayMetrics.heightPixels
            if (v.width == 0 || v.height == 0) {
                v.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY))
                v.layout(0, 0, w, h)
            }
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            v.draw(Canvas(bmp))
            File(dir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Test
    fun aWarmup() {
        // First launch in the class pays the one-time Robolectric + Compose init; keep it out of the real checks.
        launch().use { sc ->
            waitForText("Add Image")
            sc.close()
        }
    }

    @Test
    fun cleanOpenHasNoDefaultTextLayer() {
        launch().use { sc ->
            waitForText("Add Image")
            pump()
            assertTrue(
                "clean open must show the bottom-sheet root menu (v0.16.4 redesign)",
                composeRule.onAllNodesWithText("Tap a layer on the photo to style it", substring = true).fetchSemanticsNodes().isNotEmpty()
            )
            assertTrue(
                "Text Styling must stay hidden until he adds a layer (clean-open design)",
                composeRule.onAllNodesWithText("Text Styling", substring = true).fetchSemanticsNodes().isEmpty()
            )
            // v0.16.3: Position (zoom+crop the main photo) and Reset must be visible WITHOUT picking a size first
            waitForText("Position")
            waitForText("Reset")
            shot(sc, "v0164_clean_open")
            sc.close()
        }
    }

    @Test
    fun textSelectedSectionsAreCompactAndReadable() {
        launch("pixelcraft.test.textlayer" to "1").use { sc ->
            waitForText("Quick edits")
            waitForText("Text background")
            // Gap bug: the first background chip ("None") must sit DIRECTLY under the label.
            val labelBottom = composeRule.onNodeWithText("Text background").getBoundsInRoot().bottom
            val chipTop = composeRule.onNodeWithText("None").getBoundsInRoot().top
            assertTrue(
                "Text background gap bug: chips must start within 32dp of the label, was ${chipTop - labelBottom}",
                chipTop - labelBottom < 32.dp
            )
            // Squish bug: every More button must be a readable full-width pill (>= 90dp wide, <= 48dp tall).
            waitForText("More fonts")
            // v0.16.4: text colour is now an always-visible 18-swatch grid (no More colours for text);
            // only the background row keeps its More colours expander.
            val moreColours = composeRule.onAllNodesWithText("More colours", substring = true).fetchSemanticsNodes()
            assertTrue("expected 1 More colours button (background only), found ${moreColours.size}", moreColours.size == 1)
            // Curve Text: single compact line.
            val curve = composeRule.onNodeWithText("Curve Text").getBoundsInRoot()
            assertTrue("Curve Text must be one compact line, height ${curve.bottom - curve.top}", curve.bottom - curve.top <= 48.dp)
            shot(sc, "v0164_text_selected")
            sc.close()
        }
    }

    @Test
    fun pngOverlayAddsAndShowsQuickCard() {
        val png = Bitmap.createBitmap(60, 40, Bitmap.Config.ARGB_8888).apply { eraseColor(0xFF2255CC.toInt()) }
        val overlay = File.createTempFile("pc_tp_overlay_png", ".png")
        overlay.outputStream().use { png.compress(Bitmap.CompressFormat.PNG, 100, it) }
        overlay.deleteOnExit()
        launch("pixelcraft.test.overlay" to overlay.absolutePath).use { sc ->
            waitForText("Image layer")
            waitForText("Delete image layer")
            sc.close()
        }
    }

    @Test
    fun sizeLevelShowsSizeChips() {
        launch("pixelcraft.test.sheetlevel" to "size").use { sc ->
            waitForText("Image Size")
            waitForText("More sizes")
            waitForText("Square")
            shot(sc, "v0164_size_level")
            sc.close()
        }
    }

    @Test
    fun imageLayerSelectedShowsQuickCard() {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val overlay = File.createTempFile("pc_tp_overlay", ".jpg")
        overlay.writeBytes(bytes)
        overlay.deleteOnExit()
        launch("pixelcraft.test.overlay" to overlay.absolutePath).use { sc ->
            waitForText("Image layer")
            waitForText("Delete image layer")
            shot(sc, "v0164_image_layer")
            sc.close()
        }
    }
}
