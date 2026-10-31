package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsActions
import androidx.activity.compose.setContent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.learnifyzone.pixelcraft.data.PresetStore
import com.learnifyzone.pixelcraft.image.ExportFormat
import org.junit.Assert.assertEquals
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
 * v0.20.0 finalization: Presets tab filled with verified platform sizes + the rebuilt Settings page.
 * Real flows at a real phone window; screenshots inspected before delivery.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PresetsSettingsTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launch(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        var intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        extras.forEach { (k, v) -> intent = intent.putExtra(k, v) }
        return ActivityScenario.launch(intent)
    }

    private fun launchWithPhoto(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_ps", ".jpg")
        tmp.writeBytes(bytes)
        tmp.deleteOnExit()
        return launch(*extras, "pixelcraft.test.photo" to tmp.absolutePath)
    }

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(800, TimeUnit.MILLISECONDS)
        try { composeRule.waitForIdle() } catch (e: androidx.compose.ui.test.ComposeTimeoutException) { }
    }

    /** Invoke a clickable node's OnClick directly - semantics clicks land off-viewport here because
     *  scroll animations ride the frame clock, which is paused on this host. Same handler, no coordinates. */
    private fun clickText(text: String) {
        val node = composeRule.onNode(
            androidx.compose.ui.test.hasText(text).and(androidx.compose.ui.test.hasClickAction())
        ).fetchSemanticsNode()
        node.config[androidx.compose.ui.semantics.SemanticsActions.OnClick].action?.invoke()
        pump()
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
    fun a_presetsShowVerifiedPlatformSizes() {
        launch().use { sc ->
            waitForText("Presets")
            composeRule.onNodeWithText("Presets").performClick()
            waitForText("Shop & creator")
            waitForText("Etsy · Listing photo")
            waitForText("2000 × 2000 px")
            waitForText("Social media")
            waitForText("Instagram · Post (square)")
            waitForText("YouTube · Thumbnail")
            waitForText("Official size")
            shot(sc, "v0200_phone_presets_list")
        }
    }

    @Test
    fun b_searchFiltersTheList() {
        launch().use { sc ->
            waitForText("Presets")
            composeRule.onNodeWithText("Presets").performClick()
            waitForText("Etsy · Listing photo")
            composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.SetText))[0].performTextInput("etsy")
            pump(); pump()
            waitForText("Etsy · Listing photo")
            val leaked = composeRule.onAllNodesWithText("Instagram · Post (square)").fetchSemanticsNodes()
            assertTrue("search must hide non-matching presets", leaked.isEmpty())
            shot(sc, "v0200_phone_presets_search")
        }
    }

    @Test
    fun c_everyPresetIsVerifiedWithSource() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val presets = PresetStore.load(ctx)
        assertEquals("15 verified presets ship in v1.0.1 (13 + Gumroad cover + thumbnail)", 15, presets.size)
        presets.forEach { p ->
            assertTrue(p.platform + " width", p.width > 0)
            assertTrue(p.platform + " height", p.height > 0)
            assertEquals(p.platform + " status", "verified", p.status)
            assertTrue(p.platform + " source", p.sourceUrl.startsWith("https://"))
            assertEquals(p.platform + " date", "2026-10-30", p.dateVerified)
        }
        val platforms = presets.map { it.platform }.toSet()
        assertTrue(platforms.containsAll(setOf("Etsy", "Gumroad", "Instagram", "Facebook", "Pinterest", "YouTube", "X")))
    }

    @Test
    fun d_settingsPageKeepsOnlyModeAndClearRecent() {
        launch().use { sc ->
            waitForText("Settings")
            composeRule.onNodeWithText("Settings").performClick()
            waitForText("Appearance")
            waitForText("Dark mode")
            waitForText("Privacy")
            waitForText("Clear recently edited")
            // v1.0.2 (his call, overriding the v1.0.0 launch spec): Settings = mode + clear
            // recently edited ONLY - Support and About links live in the drawer.
            listOf("Support", "Rate us", "Share", "About", "Open-source licences", "What's new", "Need support? Email us", "Help & tips", "Terms & Conditions", "Privacy Policy").forEach { gone ->
                val leaked = composeRule.onAllNodesWithText(gone).fetchSemanticsNodes()
                assertTrue("Settings must not show: $gone", leaked.isEmpty())
            }
            val stale = composeRule.onAllNodesWithText("Learnify Zone", substring = true).fetchSemanticsNodes()
            assertTrue("no Learnify Zone may remain in the UI", stale.isEmpty())
            shot(sc, "v102_phone_settings")
        }
    }

    @Test
    fun e_homeTopBarOpensDrawerAndSettings() {
        launch().use { sc ->
            waitForText("A2Z Image Craft")
            waitForText("PHOTO TOOLKIT")
            // gear top-right -> Settings tab
            composeRule.onNodeWithText("Settings").performClick()  // v1.0.1: the top-bar gear was removed (his call); Settings opens from the bottom bar
            waitForText("Appearance")
            // back to Home, hamburger top-left -> drawer
            composeRule.onNodeWithText("Home").performClick()
            waitForText("PHOTO TOOLKIT")
            composeRule.onNodeWithContentDescription("Menu").performClick()
            waitForText("Need support? Email us")
            waitForText("Rate us")
            waitForText("Share")
            waitForText("Terms & Conditions")
            waitForText("Privacy Policy")
            waitForText(com.learnifyzone.pixelcraft.BuildConfig.VERSION_NAME.removeSuffix("-debug"))   // version footer in the drawer - follows the shipped version
            shot(sc, "v0202_phone_drawer")
            // drawer Terms row -> Terms doc page (v1.0.3: renders over the current tab; back returns to Home)
            clickText("Terms & Conditions")
            waitForText("developed and published by Bitty")
            shot(sc, "v0202_phone_terms")
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitForText("PHOTO TOOLKIT")
        }
    }

    @Test
    fun g_v100DrawerDocsAndSettingsRows() {
        launch().use { sc ->
            waitForText("PHOTO TOOLKIT")
            composeRule.onNodeWithContentDescription("Menu").performClick()
            // v1.0.0 launch spec: Help & tips is the first drawer row; Open-source licences joins the drawer.
            waitForText("Help & tips")
            waitForText("Open-source licences")
            clickText("Help & tips")
            waitForText("Remove Background")
            waitForText("Shape Cutter")
            shot(sc, "v100_phone_help")
            // v1.0.6 (his ask): back from a doc page returns to the drawer MENU LIST, not Home.
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitForText("Terms & Conditions")
            shot(sc, "v106_doc_back_drawer_menu")
            clickText("Open-source licences")
            waitForText("U-2-Net")
            shot(sc, "v100_phone_licences")
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitForText("Terms & Conditions")
            // v1.0.2: What's new moved to the drawer (below Open-source licences).
            clickText("What's new")
            waitForText("What's new in version 1.0.6")
            shot(sc, "v100_phone_whatsnew")
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitForText("Terms & Conditions")
        }
    }

    @Test
    fun f_backFromDocPagesReturnsNeverExits() {
        launch().use { sc ->
            waitForText("Settings")
            composeRule.onNodeWithText("Settings").performClick()
            waitForText("Appearance")
            // doc pages open from the drawer since v0.20.2
            composeRule.onNodeWithText("Home").performClick()
            waitForText("PHOTO TOOLKIT")
            composeRule.onNodeWithContentDescription("Menu").performClick()
            clickText("Terms & Conditions")
            waitForText("developed and published by Bitty")
            shot(sc, "v0202_phone_terms_back")
            // 1) v1.0.6: the page's own back arrow returns to the drawer menu list
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitForText("Help & tips")
            // closing the drawer returns to Home content
            var finishingAfterDrawerBack = false
            sc.onActivity { act ->
                act.onBackPressedDispatcher.onBackPressed()
                finishingAfterDrawerBack = act.isFinishing
            }
            pump()
            assertTrue("system back on the open drawer must NOT finish the activity", !finishingAfterDrawerBack)
            waitForText("PHOTO TOOLKIT")
            // 2) system back on the doc page also returns to the drawer menu - the v0.20.0 bug was an app EXIT here
            composeRule.onNodeWithContentDescription("Menu").performClick()
            clickText("Privacy Policy")
            waitForText("your photos stay on your phone")
            var finishingAfterDocBack = false
            sc.onActivity { act ->
                act.onBackPressedDispatcher.onBackPressed()
                finishingAfterDocBack = act.isFinishing
            }
            pump()
            assertTrue("system back on a doc page must NOT finish the activity", !finishingAfterDocBack)
            waitForText("Help & tips")
            // close the drawer again, back to Home content
            sc.onActivity { act -> act.onBackPressedDispatcher.onBackPressed() }
            pump()
            waitForText("PHOTO TOOLKIT")
            // 3) system back on the Settings tab returns to the Home tab
            composeRule.onNodeWithText("Settings").performClick()
            waitForText("Appearance")
            var finishingAfterTabBack = false
            sc.onActivity { act ->
                act.onBackPressedDispatcher.onBackPressed()
                finishingAfterTabBack = act.isFinishing
            }
            pump()
            assertTrue("system back on the Settings tab must NOT finish the activity", !finishingAfterTabBack)
            waitForText("PHOTO TOOLKIT")
        }
    }

    @Test
    fun f_settingsDocPagesFromDrawerRows() {
        launch().use { sc ->
            waitForText("PHOTO TOOLKIT")
            composeRule.onNodeWithContentDescription("Menu").performClick()
            clickText("Privacy Policy")
            waitForText("your photos stay on your phone")
            shot(sc, "v0202_phone_privacy")
        }
    }

    @Test
    fun h_dimensionsSheetShowsPresetPrefilled() {
        // Visual self-review of the preset hand-off: the size sheet rendered exactly as it appears
        // after tapping the Etsy preset - Resolution > Custom prefilled 2000 x 2000 and preselected.
        ActivityScenario.launch(MainActivity::class.java).use { sc ->
            pump()
            sc.onActivity { act ->
                act.setContent {
                    DimensionsSheet(
                        title = "Resize",
                        baseWidth = 800,
                        baseHeight = 600,
                        baseBytes = -1L,
                        baseBitmap = null,
                        format = ExportFormat.JPEG,
                        quality = 90,
                        lastUsed = null,
                        fromQueue = false,
                        initialCustomW = "2000",
                        initialCustomH = "2000",
                        onDismiss = {},
                        onApply = {}
                    )
                }
            }
            pump(); pump()
            waitForText("Resolution")
            waitForText("Resize to 2000 × 2000 px")
            shot(sc, "v0200_phone_sheet_prefill")
            sc.close()
        }
    }

    @Test
    fun g_tappingPresetAppliesItInResizeFlow() {
        launchWithPhoto().use { sc ->
            waitForText("Process", 120_000)   // resize editor loaded with the photo (same anchor as MainFlowUiTest)
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitForText("Presets")
            composeRule.onNodeWithText("Presets").performClick()
            waitForText("Etsy · Listing photo")
            composeRule.onNodeWithText("Etsy · Listing photo").performScrollTo().performClick()
            // The tap arms the preset as the editor's target size; Process opens the size sheet with
            // 2000 x 2000 prefilled and preselected under Resolution > Custom. (The "Preset applied"
            // snackbar can sit queued behind the earlier "Photo loaded" snackbar - dismissal rides the
            // frame clock, paused on this host - so verify the END RESULT: the exported file size.)
            waitForText("Process", 120_000)
            shot(sc, "v0200_phone_preset_applied")
            // The editor's own scroll animation rides the paused frame clock, so a semantics click can
            // land off-viewport; invoke the button's OnClick action directly (same handler, no coordinates).
            clickText("Process")
            pump(); pump()
            waitForText("Resolution")   // the size sheet opened, Resolution section expanded by the preset
            clickText("Resize to 2000 × 2000 px")
            waitForText("Use Fit")   // 800x600 -> 2000x2000 changes the aspect, so the adapt dialog appears
            clickText("Use Fit")
            // the Export page shows the true output size - it must be exactly the Etsy preset
            waitForText("Result: 2000 × 2000 px", 120_000)
            shot(sc, "v0200_phone_preset_result")
        }
    }
}
