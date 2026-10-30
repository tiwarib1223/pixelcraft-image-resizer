package com.learnifyzone.pixelcraft

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.view.View
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
    fun phoneWindowTextSelected() {
        launch("pixelcraft.test.textlayer" to "1").use { sc ->
            waitForText("Quick edits")
            shot(sc, "v0165_phone_text")
            sc.close()
        }
    }
}
