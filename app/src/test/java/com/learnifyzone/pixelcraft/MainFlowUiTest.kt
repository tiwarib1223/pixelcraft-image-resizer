package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.assertIsDisplayed
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
}
