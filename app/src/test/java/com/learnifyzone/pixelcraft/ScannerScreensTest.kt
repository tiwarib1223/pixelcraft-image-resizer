package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
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
 * v1.0.8 Document Scanner visual pass: renders every scanner screen (light + dark)
 * for pixel-level self-inspection before delivery. Findings only.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h2400dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScannerScreensTest {

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

    /** Seed one saved scan doc with 2 real pages so Manager/Preview/Export render fully. */
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

    private fun launch(dark: Boolean = false): ActivityScenario<MainActivity> {
        var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        if (dark) intent = intent.putExtra("pixelcraft.test.darkmode", "1")
        return ActivityScenario.launch(intent)
    }

    private fun openScannerHome(sc: ActivityScenario<MainActivity>) {
        waitForText("Document Scanner")
        shot(sc, "tmp_home_card")
        composeRule.onNodeWithText("Document Scanner").performClick()
        waitForText("Scan with Camera")
    }

    @Test
    fun scan01HomeLight() {
        launch().use { sc ->
            openScannerHome(sc)
            shot(sc, "scan_home")
            sc.close()
        }
    }

    @Test
    fun scan02MyScansLight() {
        seedDoc()
        launch().use { sc ->
            openScannerHome(sc)
            composeRule.onNodeWithText("My Scans").performClick()
            waitForText("Invoice March")
            waitForText("Newest First")
            shot(sc, "scan_myscans")
            sc.close()
        }
    }

    @Test
    fun scan03ManagerPreviewExportLight() {
        seedDoc()
        launch().use { sc ->
            openScannerHome(sc)
            composeRule.onNodeWithText("My Scans").performClick()
            waitForText("Invoice March")
            composeRule.onNodeWithText("Invoice March").performClick()
            waitForText("Add Page", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "scan_manager")
            // header menu -> Preview
            composeRule.onNodeWithContentDescription("More options").performClick()
            pump()
            composeRule.onNodeWithText("Preview Document").performClick()
            waitForText("Export Document")
            pump()
            shot(sc, "scan_preview")
            composeRule.onNodeWithText("Export Document").performClick()
            waitForText("Page Size")
            pump()
            shot(sc, "scan_export_pdf")
            // Custom margin + selected pages + JPG estimate
            composeRule.onNodeWithText("Custom").performClick()
            pump()
            composeRule.onNodeWithText("Selected pages").performClick()
            pump()
            composeRule.onNodeWithText("Page 1").performClick()
            pump()
            shot(sc, "scan_export_custom_pages")
            composeRule.onNodeWithText("JPG").performClick()
            waitForText("Dimensions")
            pump(); pump()
            shot(sc, "scan_export_jpg")
            sc.close()
        }
    }

    @Test
    fun scan04SettingsLight() {
        launch().use { sc ->
            openScannerHome(sc)
            composeRule.onNodeWithContentDescription("Scanner settings").performClick()
            waitForText("Scanner Settings")
            pump()
            shot(sc, "scan_settings")
            sc.close()
        }
    }

    @Test
    fun scan05Dark() {
        seedDoc()
        launch(dark = true).use { sc ->
            openScannerHome(sc)
            shot(sc, "scan_home_dark")
            composeRule.onNodeWithText("My Scans").performClick()
            waitForText("Invoice March")
            shot(sc, "scan_myscans_dark")
            composeRule.onNodeWithText("Invoice March").performClick()
            waitForText("Add Page", timeoutMs = 120_000)
            pump(); pump()
            shot(sc, "scan_manager_dark")
            sc.close()
        }
    }
}
