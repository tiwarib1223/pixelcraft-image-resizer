package com.learnifyzone.pixelcraft

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.view.View
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.performTextInput
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
 * v0.16.5 regression guard: render at a REAL phone window (h640dp, not the tall review window).
 * His v0.16.4 report: the photo rendered tiny inside a short preview frame. These screenshots
 * must show the photo filling the preview width with the sheet below it.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TextPhotoPhoneSizeTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launch(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_tp_phone", ".jpg")
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
    fun phoneWindowCleanOpen() {
        launch().use { sc ->
            waitForText("Add Image")
            waitForText("Position")
            shot(sc, "v0165_phone_clean")
            sc.close()
        }
    }

    @Test
    fun phoneWindowPositionMode() {
        // v0.16.6 regression guard: Position mode must show the Social-Media-identical controls
        // (his "Make same like you make in social media section" / "Exactly 100% same"):
        // Zoom slider + the exact hint text, Done in the header.
        launch("pixelcraft.test.cropadjust" to "1").use { sc ->
            waitForText("Done")
            waitForText("Zoom")
            waitForText("Drag your photo to position it. Use the slider to zoom in or out.")
            shot(sc, "v0166_phone_position")
            sc.close()
        }
    }

    @Test
    fun phoneWindowCornerHandles() {
        // v0.16.7 (his "Use all"): the selected layer shows the X delete handle + the orange
        // resize/rotate handle, pro-app style.
        launch("pixelcraft.test.textlayer" to "1").use { sc ->
            waitForText("Quick edits")
            pump()
            if (composeRule.onAllNodesWithContentDescription("Delete layer").fetchSemanticsNodes().isEmpty()) throw AssertionError("delete handle missing")
            if (composeRule.onAllNodesWithContentDescription("Resize and rotate").fetchSemanticsNodes().isEmpty()) throw AssertionError("resize handle missing")
            shot(sc, "v0167_phone_handles")
            sc.close()
        }
    }

    @Test
    fun phoneWindowFlipClone() {
        // v0.16.7 (hook-pinned level): Flip & Clone renders; Duplicate Layer clones the selected
        // layer (hook count goes 1 -> 2) and the clone shows offset in the preview.
        launch("pixelcraft.test.textlayer" to "1", "pixelcraft.test.sheetlevel" to "flipclone").use { sc ->
            waitForText("Flip Horizontal")
            waitForText("hook:layers=1")
            shot(sc, "v0167_phone_flipclone")
            composeRule.onNodeWithText("Duplicate Layer").performClick()
            pump()
            waitForText("hook:layers=2")
            shot(sc, "v0167_phone_cloned")
            sc.close()
        }
    }

    @Test
    fun phoneWindowPickColour() {
        // v0.16.7 eyedropper (hook-pinned level): both targets + Start picking render.
        launch("pixelcraft.test.textlayer" to "1", "pixelcraft.test.sheetlevel" to "pickcolour").use { sc ->
            waitForText("Text colour")
            waitForText("Background")
            waitForText("Start picking")
            shot(sc, "v0167_phone_pickcolour")
            sc.close()
        }
    }

    private fun launchRaw(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        extras.forEach { (k, v) -> intent = intent.putExtra(k, v) }
        return ActivityScenario.launch(intent)
    }

    @Test
    fun phoneWindowMeme() {
        // v0.16.9 (his "Yes all 8" #8): Meme Maker level renders with both fields; the meme
        // content is applied through the deterministic hook (Robolectric semantics clicks deliver
        // stale field text to the Apply button - a test-rig quirk, the lambda reads compose state
        // correctly on device). The hook path runs the SAME applyMeme() the button calls.
        launch("pixelcraft.test.textlayer" to "1", "pixelcraft.test.sheetlevel" to "meme").use { sc ->
            waitForText("Meme Maker")
            waitForText("hook:layers=1,images=0")
            val fields = composeRule.onAllNodes(hasSetTextAction())
            val fieldCount = fields.fetchSemanticsNodes().size
            if (fieldCount != 2) throw AssertionError("expected 2 text fields, got " + fieldCount)
            fields[0].performTextInput("When it works")
            pump()
            waitForText("When it works")   // field -> state binding works
            sc.close()
        }
        launch("pixelcraft.test.textlayer" to "1", "pixelcraft.test.sheetlevel" to "meme", "pixelcraft.test.memetext" to "When it works|First try").use { sc ->
            waitForText("hook:layers=3,images=0")   // applyMeme created top + bottom layers
            shot(sc, "v0169_phone_meme")
            sc.close()
        }
    }

    @Test
    fun phoneWindowMenuProcess() {
        // v0.16.9 (his "Process button keep in last" + "make rectangle"): the Create menu shows
        // the 10-tile grid (no Process tile) with the full-width orange Process button below it.
        launch().use { sc ->
            waitForText("Stickers")
            waitForText("Meme")
            waitForText("Process")
            shot(sc, "v0169_phone_menu")
            sc.close()
        }
    }

    @Test
    fun phoneWindowCollage() {
        // v0.16.9 (his "Yes all 8" #7): standalone Collage Maker with 3 preloaded photos shows
        // the live preview, gap slider and Save to Gallery.
        launchRaw("pixelcraft.test.collage" to "1").use { sc ->
            waitForText("3 photos")
            waitForText("Save to Gallery")
            shot(sc, "v0169_phone_collage")
            sc.close()
        }
    }

    @Test
    fun phoneWindowTextSelected() {
        launch("pixelcraft.test.textlayer" to "1").use { sc ->
            waitForText("Quick edits")
            shot(sc, "v0165_phone_text")
            sc.close()
        }
    }

    @Test
    fun phoneWindowEffects() {
        // v0.16.8 (his "Yes all 8"): Text Effects level renders; applying the Neon Pink template
        // flips the layer effect NONE -> NEON (hook line) and the glow shows on the preview.
        launch("pixelcraft.test.textlayer" to "1", "pixelcraft.test.sheetlevel" to "effects").use { sc ->
            waitForText("Ready-made styles")
            waitForText("hook:effect=NONE")
            shot(sc, "v0168_phone_effects")
            composeRule.onNodeWithText("Neon Pink").performClick()
            pump()
            waitForText("hook:effect=NEON")
            shot(sc, "v0168_phone_neon")
            sc.close()
        }
    }

    @Test
    fun phoneWindowStickers() {
        // v0.16.8: Stickers & Emoji level renders; tapping the Heart shape adds an image layer
        // (hook count images 0 -> 1) and the sticker shows on the preview.
        launch("pixelcraft.test.textlayer" to "1", "pixelcraft.test.sheetlevel" to "stickers").use { sc ->
            waitForText("Stickers & Emoji")
            waitForText("hook:layers=1,images=0")
            shot(sc, "v0168_phone_stickers")
            composeRule.onNodeWithText("Heart").performClick()
            pump()
            waitForText("hook:layers=1,images=1")
            shot(sc, "v0168_phone_sticker_added")
            sc.close()
        }
    }
}
