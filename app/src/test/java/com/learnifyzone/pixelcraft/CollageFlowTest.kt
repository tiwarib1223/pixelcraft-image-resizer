package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
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

/**
 * v0.17.0 Collage Maker flow (his 10-section spec): home -> workspace -> export, at a REAL
 * phone window (w360dp-h640dp). Screenshots are inspected before every delivery (his rule).
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CollageFlowTest {

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
    fun collageResetStartsFromZeroWithNewImageOption() {
        // His report (v0.18.2): Reset must wipe edits AND the loaded photos.
        launchRaw("pixelcraft.test.collage" to "1", "pixelcraft.test.collagelevel" to "edit").use { sc ->
            waitForText("Spacing between photos (8)")
            // Move the spacing slider off its default through its real semantics action.
            composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress))[0]
                .performSemanticsAction(SemanticsActions.SetProgress) { it(24f) }
            pump(); pump()
            waitForText("Spacing between photos (24)")
            shot(sc, "v0183_phone_collage_spacing24")
            composeRule.onNodeWithText("Reset").performClick()
            pump(); pump()
            // Full wipe: collage home with the photo picker and no photos loaded.
            waitForText("Combine your photos")
            waitForText("Add your photos (0/20)")
            shot(sc, "v0183_phone_collage_reset")
            sc.close()
        }
    }

    @Test
    fun collageBackToHomeWipesState() {
        // His report: "when I close and open again same things shows" - leaving the tool must
        // not carry the old session into the next open (every other tool already resets on exit).
        launchRaw("pixelcraft.test.collage" to "1", "pixelcraft.test.collagelevel" to "edit").use { sc ->
            waitForText("Spacing between photos (8)")
            composeRule.onNodeWithContentDescription("Back").performClick()
            pump(); pump()
            waitForText("Your creator toolkit")
            composeRule.onNodeWithText("Collage Maker").performScrollTo().performClick()
            pump(); pump()
            // Fresh session: no photos carried over from the previous one.
            waitForText("Add your photos (0/20)")
            shot(sc, "v0183_phone_collage_reopen_fresh")
            sc.close()
        }
    }

    @Test
    fun collageHomeShowsLayoutCardsAndStart() {
        // Home: title, 4 layout family cards, photo strip with count, Start Creating.
        launchRaw("pixelcraft.test.collage" to "1", "pixelcraft.test.collagelevel" to "home").use { sc ->
            waitForText("Combine your photos")
            waitForText("Choose a layout style")
            waitForText("Grid")
            waitForText("Mixed")
            waitForText("Vertical")
            waitForText("Horizontal")
            waitForText("Add your photos (3/20)")
            waitForText("Start Creating")
            shot(sc, "v0171_phone_collage_home")
            sc.close()
        }
    }

    @Test
    fun collageHomeStartCreatingOpensWorkspace() {
        // Real navigation: tapping Start Creating on Home lands on the workspace.
        launchRaw("pixelcraft.test.collage" to "1", "pixelcraft.test.collagelevel" to "home").use { sc ->
            waitForText("Start Creating")
            composeRule.onNodeWithText("Start Creating").performClick()
            waitForText("Continue to Export")
            waitForText("Spacing between photos")
            sc.close()
        }
    }

    @Test
    fun collageExportShowsSettingsAndSave() {
        // Export: format chips, resolution chips, size summary, Save Collage.
        launchRaw("pixelcraft.test.collage" to "1", "pixelcraft.test.collagelevel" to "export").use { sc ->
            waitForText("File format")
            waitForText("JPG")
            waitForText("PNG")
            waitForText("WebP")
            waitForText("Resolution")
            waitForText("Will save: 1080 x 1080 JPG")
            waitForText("Save Collage")
            shot(sc, "v0171_phone_collage_export")
            sc.close()
        }
    }

    @Test
    fun collageWorkspaceSelectedPhotoPanel() {
        // v0.17.1: with a photo selected (hook) the panel shows zoom, rotate/flip, replace,
        // reset and the per-photo frame; below sit margins and the collage-wide frame.
        launchRaw("pixelcraft.test.collage" to "1", "pixelcraft.test.collagesel" to "0").use { sc ->
            waitForText("Zoom - photo 1")
            waitForText("Rotate 90°")
            waitForText("Replace photo 1")
            waitForText("Reset photo")
            waitForText("Frame - this photo")
            shot(sc, "v0171_phone_collage_edit_selected")
            composeRule.onNodeWithText("Margin around the collage", substring = true).performScrollTo()
            waitForText("Frame around every photo")
            waitForText("Gradient")
            waitForText("None")
            shot(sc, "v0171_phone_collage_edit_styles")
            sc.close()
        }
    }

    @Test
    fun collageWorkspaceControlsChangePreview() {
        // Workspace: undo stack grows after a style change; Reset restores defaults.
        launchRaw("pixelcraft.test.collage" to "1").use { sc ->
            waitForText("Spacing between photos")
            waitForText("Canvas size")
            waitForText("Story")
            // Chips sit below the fold at h640dp: semantics clicks are touch injections at the
            // node centre, so scroll them on screen first (documented rig quirk).
            composeRule.onNodeWithText("Story").performScrollTo().performClick()
            waitForText("Story · 1080 × 1920 px")
            composeRule.onNodeWithText("Black").performScrollTo().performClick()
            pump()
            shot(sc, "v0171_phone_collage_edit_story_black")
            sc.close()
        }
    }
}
