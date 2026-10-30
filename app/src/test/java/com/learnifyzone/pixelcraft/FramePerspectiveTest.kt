package com.learnifyzone.pixelcraft

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.learnifyzone.pixelcraft.image.TextOverlayUtils
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
 * v0.19.0 Frame + Perspective tiles at a REAL phone window (w360dp-h640dp).
 * Real-flow checks: tiles visible in the grid, a frame changes the rendered pixels,
 * perspective skews the photo and its value label snaps back to centre. Screenshots inspected before delivery.
 */
@org.junit.FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class FramePerspectiveTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun launch(vararg extras: Pair<String, String>): ActivityScenario<MainActivity> {
        val bytes = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_fp", ".jpg")
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
        scenario.onActivity { act ->
            val v = act.window.decorView
            val bmp = android.graphics.Bitmap.createBitmap(v.width, v.height, android.graphics.Bitmap.Config.ARGB_8888)
            v.draw(android.graphics.Canvas(bmp))
            bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, File(dir, "$name.png").outputStream())
        }
        pump()
    }

    /** Mean abs per-channel pixel difference of the TOP [toFrac] of two screenshots. */
    private fun meanDiff(a: String, b: String, toFrac: Float = 1f): Double {
        val dir = File("build/outputs/uishots")
        val ba = BitmapFactory.decodeFile(File(dir, "$a.png").absolutePath)
        val bb = BitmapFactory.decodeFile(File(dir, "$b.png").absolutePath)
        val h = (minOf(ba.height, bb.height) * toFrac).toInt()
        val w = minOf(ba.width, bb.width)
        var sum = 0.0; var n = 0
        var y = 0
        while (y < h) {
            var x = 0
            while (x < w) {
                val pa = ba.getPixel(x, y); val pb = bb.getPixel(x, y)
                sum += Math.abs((pa shr 16 and 0xFF) - (pb shr 16 and 0xFF)) +
                        Math.abs((pa shr 8 and 0xFF) - (pb shr 8 and 0xFF)) +
                        Math.abs((pa and 0xFF) - (pb and 0xFF))
                n += 3
                x += 2
            }
            y += 2
        }
        return sum / n
    }

    /** Rows holding a >=200px run of near-white pixels (a polaroid/stamp band is the only thing that draws one). */
    private fun whiteBandRows(name: String): Int {
        val bmp = BitmapFactory.decodeFile(File("build/outputs/uishots", "$name.png").absolutePath)
        var rows = 0
        for (y in 0 until bmp.height) {
            var run = 0
            for (x in 0 until bmp.width) {
                val p = bmp.getPixel(x, y)
                val white = (p shr 16 and 0xFF) > 235 && (p shr 8 and 0xFF) > 235 && (p and 0xFF) > 235
                run = if (white) run + 1 else 0
                if (run >= 200) { rows++; break }
            }
        }
        return rows
    }

    @Test
    fun a_tilesFillTheEmptySlots() {
        launch().use { sc ->
            waitForText("Process")
            waitForText("Meme")
            waitForText("Frame")
            waitForText("Perspective")
            composeRule.onNodeWithText("Perspective").performScrollTo()
            pump()
            shot(sc, "v0190_phone_menu_tiles")
        }
    }

    @Test
    fun b_frameRendersPolaroidOnThePhoto() {
        launch().use { sc ->
            waitForText("Frame")
            composeRule.onNodeWithText("Frame").performScrollTo().performClick()
            waitForText("Tap a style")
            waitForText("Polaroid")
            shot(sc, "v0190_phone_frame_none")
            val before = whiteBandRows("v0190_phone_frame_none")
            composeRule.onNodeWithText("Polaroid").performScrollTo().performClick()
            pump(); pump()
            shot(sc, "v0190_phone_frame_polaroid")
            val after = whiteBandRows("v0190_phone_frame_polaroid")
            assertTrue("polaroid bands must add white runs to the preview (before=$before after=$after)", after > before + 5)
        }
    }

    @Test
    fun c_perspectiveSkewsAndSnapsBack() {
        launch().use { sc ->
            waitForText("Perspective")
            composeRule.onNodeWithText("Perspective").performScrollTo().performClick()
            waitForText("Vertical")
            waitForText("Reset perspective")
            shot(sc, "v0190_phone_persp_v0")
            composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress))[0]
                .performSemanticsAction(SemanticsActions.SetProgress) { it(80f) }
            pump(); pump(); pump()
            // value label shows the new value
            val dl = System.currentTimeMillis() + 30_000
            var found = false
            while (System.currentTimeMillis() < dl) {
                pump()
                if (composeRule.onAllNodesWithText("80").fetchSemanticsNodes().isNotEmpty()) { found = true; break }
                Thread.sleep(60)
            }
            assertTrue("value label 80 not shown", found)
            shot(sc, "v0190_phone_persp_v80")
            val skewed = meanDiff("v0190_phone_persp_v0", "v0190_phone_persp_v80", toFrac = 0.55f)
            assertTrue("perspective must visibly skew the preview (diff=$skewed)", skewed > 3.0)
            // his "snap back": tap the value label -> back to centre, photo straight again
            composeRule.onNodeWithText("80").performClick()
            pump(); pump()
            shot(sc, "v0190_phone_persp_reset")
            val restored = meanDiff("v0190_phone_persp_v0", "v0190_phone_persp_reset", toFrac = 0.55f)
            assertTrue("snap-back must restore the straight photo (diff=$restored)", restored < 2.5)
        }
    }

    @Test
    fun d_perspectiveMatrixGeometry() {
        val ident = TextOverlayUtils.perspectiveMatrix(800, 600, 0f, 0f)
        val pt = floatArrayOf(0f, 0f, 400f, 300f)
        ident.mapPoints(pt)
        assertEquals(0f, pt[0], 0.01f); assertEquals(0f, pt[1], 0.01f)
        assertEquals(400f, pt[2], 0.01f); assertEquals(300f, pt[3], 0.01f)
        val m = TextOverlayUtils.perspectiveMatrix(800, 600, 0f, 100f)
        val c = floatArrayOf(400f, 300f)
        m.mapPoints(c)
        assertEquals(400f, c[0], 1f); assertEquals(300f, c[1], 1f)
        val corner = floatArrayOf(0f, 0f)
        m.mapPoints(corner)
        assertTrue("corner must move under tilt", corner[0] != 0f || corner[1] != 0f)
    }

    @Test
    fun e_drawFrameLineColoursBorderOnly() {
        val b = android.graphics.Bitmap.createBitmap(200, 200, android.graphics.Bitmap.Config.ARGB_8888)
        b.eraseColor(0xFF336699.toInt())
        TextOverlayUtils.drawFrame(android.graphics.Canvas(b), 200, 200, TextOverlayUtils.FrameStyle.LINE, 10f, 0xFFFF0000.toInt())
        assertEquals(0xFFFF0000.toInt(), b.getPixel(5, 100))
        assertEquals(0xFF336699.toInt(), b.getPixel(100, 100))
    }

    @Test
    fun f_renderToBitmapFrameAndPerspective() {
        val photo = android.graphics.Bitmap.createBitmap(120, 90, android.graphics.Bitmap.Config.ARGB_8888)
        photo.eraseColor(0xFF204060.toInt())
        val framed = TextOverlayUtils.renderToBitmap(
            photo, emptyList(), emptyList(),
            frameStyle = TextOverlayUtils.FrameStyle.LINE, frameWidthPx = 8f, frameColor = 0xFFFF0000.toInt()
        )
        assertEquals(0xFFFF0000.toInt(), framed.getPixel(4, 45))
        assertEquals(0xFF204060.toInt(), framed.getPixel(60, 45))
        val skewed = TextOverlayUtils.renderToBitmap(photo, emptyList(), emptyList(), perspH = 100f)
        // strong tilt: the right edge recedes, so both right-side corners sit outside the warped quad
        assertTrue("top-right corner must pull in under horizontal tilt", skewed.getPixel(119, 0) != 0xFF204060.toInt())
        assertTrue("bottom-right corner must pull in under horizontal tilt", skewed.getPixel(119, 89) != 0xFF204060.toInt())
        assertEquals(0xFF204060.toInt(), skewed.getPixel(60, 45))
    }
}
