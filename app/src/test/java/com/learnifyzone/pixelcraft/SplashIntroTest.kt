package com.learnifyzone.pixelcraft

import android.content.Intent
import android.os.Looper
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.activity.compose.setContent
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
 * v0.20.0: the branded opening moment (his v3 splash design) renders correctly.
 * The intro itself is skipped on this host (paused frame clock), so we render it directly.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30], qualifiers = "w360dp-h640dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SplashIntroTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private fun pump() {
        shadowOf(Looper.getMainLooper()).idleFor(900, TimeUnit.MILLISECONDS)
        try { composeRule.waitForIdle() } catch (e: androidx.compose.ui.test.ComposeTimeoutException) { }
    }

    @Test
    fun introRendersLogoAndCredit() {
        ActivityScenario.launch(MainActivity::class.java).use { sc ->
            pump()
            sc.onActivity { act -> act.setContent { IntroScreen() } }
            pump(); pump()
            composeRule.onNodeWithText("POWERED BY BITTY").assertIsDisplayed()
            val dir = File("build/outputs/uishots").apply { mkdirs() }
            sc.onActivity { act ->
                val v = act.window.decorView
                val bmp = android.graphics.Bitmap.createBitmap(v.width, v.height, android.graphics.Bitmap.Config.ARGB_8888)
                v.draw(android.graphics.Canvas(bmp))
                bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, File(dir, "v0200_phone_intro.png").outputStream())
            }
            pump()
            sc.close()
        }
    }
}
