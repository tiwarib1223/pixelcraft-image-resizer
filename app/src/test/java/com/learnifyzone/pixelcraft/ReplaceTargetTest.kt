package com.learnifyzone.pixelcraft

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.learnifyzone.pixelcraft.image.SaveUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * v0.20.5 - his v0.20.4 on-device report: Replace original showed the fallback snackbar instead of
 * the system "Allow to modify photos?" consent. Cause: the photo picker hands out
 * content://media/picker/... URIs, which MediaStore.createWriteRequest rejects ("no requested volume").
 * replaceTargetFor resolves them to the real MediaStore item URI carrying the same row id.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ReplaceTargetTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun photoPickerUri_resolvesToMediaStoreItem() {
        val picker = Uri.parse("content://media/picker/0/com.android.providers.media.photopicker/media/1000000034")
        assertEquals(
            "content://media/external/images/media/1000000034",
            SaveUtils.replaceTargetFor(context, picker).toString()
        )
    }

    @Test
    fun legacyPickerUri_resolvesToMediaStoreItem() {
        val picker = Uri.parse("content://media/picker_get_content/0/com.android.providers.media.photopicker/media/1000000034")
        assertEquals(
            "content://media/external/images/media/1000000034",
            SaveUtils.replaceTargetFor(context, picker).toString()
        )
    }

    @Test
    fun mediaDocumentUri_resolvesViaDocumentId() {
        val doc = Uri.parse("content://com.android.providers.media.documents/document/image%3A12345")
        assertEquals(
            "content://media/external/images/media/12345",
            SaveUtils.replaceTargetFor(context, doc).toString()
        )
    }

    @Test
    fun plainMediaStoreUri_isUsedAsIs() {
        val media = Uri.parse("content://media/external/images/media/777")
        assertEquals(media, SaveUtils.replaceTargetFor(context, media))
    }

    @Test
    fun fileUriAndNonNumericUri_returnNull() {
        assertNull(SaveUtils.replaceTargetFor(context, Uri.parse("file:///tmp/photo.jpg")))
        assertNull(SaveUtils.replaceTargetFor(context, Uri.parse("content://com.example.docs/document/report")))
    }
}
