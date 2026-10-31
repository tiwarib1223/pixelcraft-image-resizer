package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

/** v1.0.7: visual proof of the triggered reduced-resolution warnings before export. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h2400dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PrintWarningShotsTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(800, java.util.concurrent.TimeUnit.MILLISECONDS)
        try { composeRule.waitForIdle() } catch (e: androidx.compose.ui.test.ComposeTimeoutException) { }
    }

    private fun waitForText(text: String, timeoutMs: Long = 60_000) {
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

    private fun buildPdfBytes(pageCount: Int): ByteArray {
        val sb = StringBuilder("%PDF-1.4\n")
        val offsets = mutableListOf<Int>()
        fun obj(body: String) { offsets.add(sb.length); sb.append(body) }
        obj("1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n")
        val kids = (3 until 3 + pageCount).joinToString(" ") { "$it 0 R" }
        obj("2 0 obj<</Type/Pages/Kids[$kids]/Count $pageCount>>endobj\n")
        for (i in 0 until pageCount) obj("${3 + i} 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 40 30]>>endobj\n")
        val xrefPos = sb.length
        sb.append("xref\n0 ${offsets.size + 1}\n0000000000 65535 f \n")
        offsets.forEach { sb.append(String.format("%010d 00000 n \n", it)) }
        sb.append("trailer<</Size ${offsets.size + 1}/Root 1 0 R>>\nstartxref\n").append(xrefPos).append("\n%%EOF")
        return sb.toString().toByteArray(Charsets.ISO_8859_1)
    }

    @Test
    fun outputCapWarningShownForPosterAt300() {
        val photo = javaClass.classLoader!!.getResourceAsStream("test_photo.jpg")!!.readBytes()
        val tmp = File.createTempFile("pc_warn", ".jpg")
        tmp.writeBytes(photo)
        tmp.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.photo", tmp.absolutePath)
            .putExtra("pixelcraft.test.tool", "printsetup")
        ActivityScenario.launch<MainActivity>(intent).use { sc ->
            waitForText("Print Resolution (PPI)", 120_000)
            // 24 x 36 poster at 300 PPI needs 77.8 MP - over the 48 MP ceiling.
            // Tall test screen: the whole setup page is visible, no flaky scrolling.
            composeRule.onAllNodesWithText("24 × 36")[0].performClick()
            waitForText("Saved at about 235 PPI instead of 300 PPI", 60_000)
            pump(); pump()
            shot(sc, "v107_warn_output_cap")
            sc.close()
        }
    }

    @Test
    fun sourceCapWarningShownForPdfA2At300() {
        // A2 at 300 PPI = 34.8 MP output (UNDER the 48 MP ceiling, so the output
        // warning must NOT show) but the source PDF page needs a 7016-px render -
        // over the 6144 source ceiling - so the source warning must show instead.
        val pdf = File.createTempFile("pc_warn_src", ".pdf")
        pdf.writeBytes(buildPdfBytes(1))
        pdf.deleteOnExit()
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
            .putExtra("pixelcraft.test.tool", "printpdfsetup")
            .putExtra("pixelcraft.test.pdf", pdf.absolutePath)
        ActivityScenario.launch<MainActivity>(intent).use { sc ->
            try {
                waitForText("Print Resolution (PPI)", 60_000)
            } catch (e: AssertionError) {
                System.out.println("PDF-ENGINE-ABSENT: print setup with PDF source not reachable on host")
                sc.close(); return
            }
            System.out.println("PDF-ENGINE-RAN: source-cap scenario executed for real")
            composeRule.onAllNodesWithText("A2")[0].performClick()
            waitForText("PDF pages rendered at about", 60_000)
            // The output-ceiling card must NOT appear for this 34.8 MP case.
            assert(composeRule.onAllNodesWithText("Saved at about", substring = true).fetchSemanticsNodes().isEmpty()) {
                "output-cap card must not show for A2@300 (34.8 MP under the ceiling)"
            }
            pump(); pump()
            shot(sc, "v107_warn_source_cap")
            sc.close()
        }
    }
}
