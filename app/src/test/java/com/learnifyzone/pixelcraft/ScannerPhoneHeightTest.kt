package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
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


    private fun waitForDesc(desc: String, timeoutMs: Long = 60_000) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            pump()
            if (composeRule.onAllNodesWithContentDescription(desc).fetchSemanticsNodes().isNotEmpty()) return
            Thread.sleep(60)
        }
        throw AssertionError("Content description not found within ${timeoutMs}ms: $desc")
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
    fun phone03CropButtonsOneLine() {
        // His fix: the Crop bottom buttons (Cancel / Reset / Apply) must each stay on ONE line at phone width.
        seedDoc()
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).putExtra("pixelcraft.test.tool", "scanner")).use { sc ->
            waitForText("Scan with Camera")
            composeRule.onNodeWithText("My Scans").performClick()
            waitForText("Invoice March")
            composeRule.onNodeWithText("Invoice March").performClick()
            waitForText("Your scan", timeoutMs = 120_000)
            composeRule.onNodeWithText("Edit page").performClick()
            waitForText("Crop & Adjust", timeoutMs = 120_000)
            waitForText("Auto Crop")
            pump()
            listOf("Cancel", "Reset", "Apply").forEach { label ->
                // Unmerged tree: the raw Text node's tight bounds (the merged button node is 50dp tall by design).
                val nodes = composeRule.onAllNodesWithText(label, useUnmergedTree = true).fetchSemanticsNodes()
                if (nodes.isEmpty()) throw AssertionError("$label missing on the crop screen")
                val h = nodes.maxOf { it.boundsInRoot.height }
                // One line at 13sp is ~25px; a wrapped second line is ~45px+.
                if (h > 40f) throw AssertionError("$label wraps to more than one line at phone width (height=${h}px)")
            }
            waitForDesc("Page to crop")
            shot(sc, "scan_crop_phone")
            sc.close()
        }
    }

    @Test
    fun phone04EnhancePhoneHeight() {
        seedDoc()
        listOf(false, true).forEach { dark ->
            var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
                .putExtra("pixelcraft.test.tool", "scanner")
            if (dark) intent = intent.putExtra("pixelcraft.test.darkmode", "1")
            ActivityScenario.launch<MainActivity>(intent).use { sc ->
                waitForText("Scan with Camera")
                composeRule.onNodeWithText("My Scans").performClick()
                waitForText("Invoice March")
                composeRule.onNodeWithText("Invoice March").performClick()
                waitForText("Your scan", timeoutMs = 120_000)
                composeRule.onNodeWithText("Edit page").performClick()
                waitForText("Crop & Adjust", timeoutMs = 120_000)
                composeRule.onNodeWithText("Apply").performClick()
                waitForText("Enhance", timeoutMs = 120_000)
                waitForText("Whiten background")
                waitForText("Remove shadows")
                waitForText("Black & White")
                // The workspace (preview) must still have real room at phone height.
                waitForText("Hold: Compare Original")
                waitForDesc("Enhanced page")
                shot(sc, if (dark) "scan_enhance_phone_dark" else "scan_enhance_phone")
                sc.close()
            }
        }
    }

    @Test
    fun phone05CropDarkPhoneHeight() {
        seedDoc()
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.tool", "scanner").putExtra("pixelcraft.test.darkmode", "1")).use { sc ->
            waitForText("Scan with Camera")
            composeRule.onNodeWithText("My Scans").performClick()
            waitForText("Invoice March")
            composeRule.onNodeWithText("Invoice March").performClick()
            waitForText("Your scan", timeoutMs = 120_000)
            composeRule.onNodeWithText("Edit page").performClick()
            waitForText("Crop & Adjust", timeoutMs = 120_000)
            waitForDesc("Page to crop")
            listOf("Auto Crop", "Straighten", "Rotate Left", "Rotate Right", "Fit", "Cancel", "Reset", "Apply").forEach { waitForText(it) }
            shot(sc, "scan_crop_phone_dark")
            sc.close()
        }
    }

    @Test
    fun phone02ResultActionsReachable() {
        seedDoc()
        ActivityScenario.launch<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).putExtra("pixelcraft.test.tool", "scanner")).use { sc ->
            waitForText("Scan with Camera")
            composeRule.onNodeWithText("My Scans").performClick()
            waitForText("Invoice March")
            composeRule.onNodeWithText("Invoice March").performClick()
            waitForText("Your scan", timeoutMs = 120_000)
            waitForText("Save as PDF")
            // At real phone height every action must be composed AND inside the viewport.
            val root = composeRule.onRoot().fetchSemanticsNode().boundsInRoot
            listOf("Save as PDF", "Save as JPG", "Share", "Compress", "Print").forEach { label ->
                val nodes = composeRule.onAllNodesWithText(label).fetchSemanticsNodes()
                if (nodes.isEmpty()) throw AssertionError("$label not composed at phone height")
                val inView = nodes.any { it.boundsInRoot.top >= root.top - 1f && it.boundsInRoot.bottom <= root.bottom + 1f }
                if (!inView) throw AssertionError("$label outside the viewport at phone height")
            }
            // Every settings line must be reachable by scrolling (no fixed-button overlap).
            val content = hasScrollAction().and(hasAnyDescendant(hasText("File name")))
            composeRule.onNode(content).performScrollToNode(hasText("PDF saves to Documents", substring = true))
            pump()
            waitForText("PDF quality: 220 PPI")
            waitForText("More save options")
            // Scrolled state: estimate (both lines), quality and destination must be FULLY
            // inside the viewport, and the fixed actions + More save options still visible.
            fun assertFullyVisible(label: String) {
                val nodes = composeRule.onAllNodesWithText(label, substring = true).fetchSemanticsNodes()
                if (nodes.isEmpty()) throw AssertionError("$label not composed after scrolling")
                // Zero-size bounds = semantics row not really laid out; do not count it as visible.
                val inView = nodes.any { it.boundsInRoot.width > 1f && it.boundsInRoot.height > 1f && it.boundsInRoot.top >= root.top - 1f && it.boundsInRoot.bottom <= root.bottom + 1f }
                if (!inView) throw AssertionError("$label not fully visible after scrolling (bounds=${nodes.map { it.boundsInRoot }})")
            }
            assertFullyVisible("Estimated size:")
            assertFullyVisible("PDF quality: 220 PPI")
            assertFullyVisible("PDF saves to Documents")
            assertFullyVisible("More save options (PNG, margins, selected pages)")
            listOf("Save as PDF", "Save as JPG", "Share", "Compress", "Print").forEach { assertFullyVisible(it) }
            shot(sc, "scan_result_phone_scrolled")
            // The old export options are reachable again via More save options.
            composeRule.onNodeWithText("More save options (PNG, margins, selected pages)").performClick()
            waitForText("Page Size")
            waitForText("Custom")
            waitForText("PNG")
            shot(sc, "scan_export_more_phone")
            // Pages controls must scroll fully into view above the fixed Export Document action.
            // (performScrollToNode cannot target LazyRow items below the fold - they report
            // zero-size bounds - so scroll the container with a real swipe.)
            val exportContent = hasScrollAction().and(hasAnyDescendant(hasText("Format")))
            composeRule.onNode(exportContent).performTouchInput { swipeUp(startY = 400f, endY = 150f, durationMillis = 200) }
            pump()
            assertFullyVisible("Pages")
            assertFullyVisible("All pages")
            assertFullyVisible("Selected pages")
            assertFullyVisible("Export Document")
            shot(sc, "scan_export_pages_phone")
            sc.close()
        }
    }
}
