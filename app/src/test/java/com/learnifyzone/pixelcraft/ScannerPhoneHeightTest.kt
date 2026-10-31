package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
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
 * Real phone height (w360dp-h640dp): scanner home cards must not clip, and the Export
 * action must be composed + visible without scrolling on the export screen.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScannerPhoneHeightTest {

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

    // Invokes the OnClick action of the nearest clickable ancestor: deterministic in
    // Robolectric regardless of whether the chip is inside the current viewport.
    private fun clickChip(label: String) {
        val nodes = composeRule.onAllNodesWithText(label, useUnmergedTree = true).fetchSemanticsNodes()
        if (nodes.isEmpty()) throw AssertionError("no node with text: $label")
        var n = nodes[0]
        while (!n.config.contains(androidx.compose.ui.semantics.SemanticsActions.OnClick)) {
            n = n.parent ?: throw AssertionError("no clickable ancestor for: $label")
        }
        @Suppress("UNCHECKED_CAST")
        val act = n.config[androidx.compose.ui.semantics.SemanticsActions.OnClick].action as? () -> Boolean
        if (act == null) throw AssertionError("null click action for: $label")
        act.invoke()
        pump()
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

    private fun seedDoc(): String {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val docId = "testdoc1"
        listOf("p1", "p2").forEach { pid ->
            File(com.learnifyzone.pixelcraft.data.ScansStore.pageFile(context, docId, pid).absolutePath).writeBytes(bytes)
        }
        val meta = com.learnifyzone.pixelcraft.data.ScansStore.DocMeta(
            docId, "Invoice March", "Bills & Invoices", System.currentTimeMillis() - 86400000L, System.currentTimeMillis()
        )
        meta.pages.add(com.learnifyzone.pixelcraft.data.ScansStore.PageMeta("p1"))
        meta.pages.add(com.learnifyzone.pixelcraft.data.ScansStore.PageMeta("p2"))
        com.learnifyzone.pixelcraft.data.ScansStore.saveDoc(context, meta)
        return docId
    }

    @Test
    fun phone01HomeCardsNotClipped() {
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).putExtra("pixelcraft.test.tool", "scanner")).use { sc ->
            waitForText("Scan with Camera")
            // The full subtitle must be composed (it was clipped by the fixed card height before the fix).
            waitForText("Capture paper documents with your camera")
            shot(sc, "scan_home_phone")
            sc.close()
        }
    }

    @Test
    fun phone02ExportButtonReachable() {
        seedDoc()
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).putExtra("pixelcraft.test.tool", "scanner")).use { sc ->
            waitForText("Scan with Camera")
            composeRule.onNodeWithText("My Scans").performClick()
            waitForText("Invoice March")
            composeRule.onNodeWithText("Invoice March").performClick()
            waitForText("Add Page", timeoutMs = 120_000)
            composeRule.onNodeWithContentDescription("More options").performClick()
            pump()
            composeRule.onNodeWithText("Preview Document").performClick()
            waitForText("Export Document")
            composeRule.onAllNodesWithText("Export Document")[0].performClick()
            waitForText("Page Size")
            // Both the header title and the bottom Export action must exist at phone height.
            val nodes = composeRule.onAllNodesWithText("Export Document").fetchSemanticsNodes()
            if (nodes.size < 2) throw AssertionError("Export action not composed at phone height (found ${nodes.size})")
            // Custom margin + selected pages still leave the action composed.
            // The Margins row is still inside the 640dp viewport; scroll the content column
            // so the Pages row is too before tapping (off-viewport taps do not dispatch).
            composeRule.onNodeWithText("Custom").performClick()
            pump()
            val content = hasScrollAction().and(hasAnyDescendant(hasText("Page Size")))
            composeRule.onNode(content).performScrollToNode(hasText("Selected pages"))
            pump()
            clickChip("Selected pages")
            composeRule.onNode(content).performScrollToNode(hasText("Page 1"))
            pump()
            clickChip("Page 1")
            val nodes2 = composeRule.onAllNodesWithText("Export Document").fetchSemanticsNodes()
            if (nodes2.size < 2) throw AssertionError("Export action lost after options at phone height")
            shot(sc, "scan_export_phone")
            sc.close()
        }
    }
}
