package com.learnifyzone.pixelcraft

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.view.View
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
 * v0.20.4 regression guards (his on-device reports on v0.20.3):
 *  - "If I click replace original app is exit": Replace original launched the MediaStore
 *    write-consent intent with no exception handling; a throwing source URI killed the app.
 *    The flow is now: in-app "Are you sure?" -> system consent -> success in-app, and every
 *    intent launch on that path is guarded. These tests prove the dialog, the cancel path,
 *    and that confirming Replace never crashes, even with a URI the system rejects.
 *  - Quality slider overflowed the right screen edge (Row without a width constraint).
 *    The slider bounds must stay inside the root bounds at a real phone window.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ExportScreenPolishTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launchOnExport(): ActivityScenario<MainActivity> {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_export_test", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "export")
        return ActivityScenario.launch(intent)
    }

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(800, TimeUnit.MILLISECONDS)
        try { composeRule.waitForIdle() } catch (e: androidx.compose.ui.test.ComposeTimeoutException) { }
    }

    private fun waitForText(text: String, substring: Boolean = false, timeoutMs: Long = 90_000) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            pump()
            if (composeRule.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isNotEmpty()) return
            Thread.sleep(60)
        }
        throw AssertionError("Text not found within ${timeoutMs}ms: $text")
    }

    /** Invoke a clickable node's OnClick directly - coordinate clicks land off-viewport on the
     *  paused Robolectric frame clock. Same handler, no coordinates. */
    private fun clickText(text: String) {
        val node = composeRule.onNode(hasText(text).and(hasClickAction())).fetchSemanticsNode()
        node.config[androidx.compose.ui.semantics.SemanticsActions.OnClick].action?.invoke()
        pump()
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
    fun qualitySliderStaysInsideScreen() {
        launchOnExport().use { sc ->
            waitForText("Smaller file")
            val root = composeRule.onRoot().fetchSemanticsNode().boundsInRoot
            val sliderNode = composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(
                androidx.compose.ui.semantics.SemanticsProperties.ProgressBarRangeInfo
            )).fetchSemanticsNodes().first()
            val b = sliderNode.boundsInRoot
            assertTrue("slider left ${b.left} outside root left ${root.left}", b.left >= root.left - 1f)
            assertTrue("slider right ${b.right} overflows root right ${root.right} (his v0.20.3 report)", b.right <= root.right + 1f)
            shot(sc, "v0205_phone_export")
            // Scroll the Quality row into view for the visual proof shot (his screenshot showed it cut off).
            val sliderInteraction = composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(
                androidx.compose.ui.semantics.SemanticsProperties.ProgressBarRangeInfo
            ))[0]
            sliderInteraction.performScrollTo()
            pump()
            shot(sc, "v0205_phone_export_slider")
        }
    }

    @Test
    fun replaceOriginalShowsAreYouSureDialogAndCancelClosesIt() {
        launchOnExport().use {
            waitForText("Replace original")
            clickText("Replace original")
            waitForText("Are you sure?")
            waitForText("The original photo will be overwritten by the new one.", substring = true)
            clickText("Cancel")
            pump()
            assertTrue(
                "dialog must close on Cancel",
                composeRule.onAllNodesWithText("Are you sure?").fetchSemanticsNodes().isEmpty()
            )
            // Still on the Export screen, nothing lost.
            waitForText("Replace original")
        }
    }

    @Test
    fun confirmReplaceNeverExitsTheApp() {
        launchOnExport().use { sc ->
            waitForText("Replace original")
            clickText("Replace original")
            waitForText("Are you sure?")
            clickText("Replace")
            // Give the guarded launch path several pump cycles to run and (pre-fix) crash.
            repeat(6) { pump(); Thread.sleep(100) }
            var destroyed = false
            sc.onActivity { a -> destroyed = a.isDestroyed || a.isFinishing }
            assertTrue("app exited after Replace original - his exact v0.20.3 crash", !destroyed)
            composeRule.onRoot().fetchSemanticsNode() // root still composed = UI alive
        }
    }
}
