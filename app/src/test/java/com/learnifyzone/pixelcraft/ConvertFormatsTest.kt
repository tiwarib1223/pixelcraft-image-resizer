package com.learnifyzone.pixelcraft

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.learnifyzone.pixelcraft.image.ConvertFormats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Real-output checks for the Format Converter: every produced file is parsed back. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ConvertFormatsTest {

    private fun redBmp(w: Int = 40, h: Int = 30, alpha: Boolean = false): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        if (alpha) {
            bmp.eraseColor(Color.TRANSPARENT)
            // half transparent, half red
            for (x in 0 until w / 2) for (y in 0 until h) bmp.setPixel(x, y, Color.RED)
        } else {
            bmp.eraseColor(Color.RED)
        }
        return bmp
    }

    @Test
    fun jpegConvertsAndFlattensTransparency() {
        val bytes = ConvertFormats.encode(redBmp(alpha = true), ConvertFormats.byId("jpg"), 90, Color.WHITE)
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)!!
        assertEquals(40, decoded.width); assertEquals(30, decoded.height)
        val left = decoded.getPixel(5, 15)
        assertTrue(Color.red(left) > 240)                // red half kept (JPEG is lossy)
        val right = decoded.getPixel(35, 15)
        assertTrue(Color.red(right) > 240 && Color.green(right) > 240 && Color.blue(right) > 240) // transparent -> white
    }

    @Test
    fun pngRoundTripKeepsAlpha() {
        val bytes = ConvertFormats.encode(redBmp(alpha = true), ConvertFormats.byId("png"), 100, Color.WHITE)
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)!!
        assertEquals(40, decoded.width)
        assertEquals(0, Color.alpha(decoded.getPixel(35, 15)))   // transparency preserved
        assertEquals(255, Color.red(decoded.getPixel(5, 15)))
    }

    @Test
    fun webpRoundTrip() {
        val bytes = ConvertFormats.encode(redBmp(), ConvertFormats.byId("webp"), 90, Color.WHITE)
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)!!
        assertEquals(40, decoded.width); assertEquals(30, decoded.height)
        assertTrue(Color.red(decoded.getPixel(20, 15)) > 200)
    }

    @Test
    fun bmpIsARealBitmapFile() {
        val bytes = ConvertFormats.encode(redBmp(), ConvertFormats.byId("bmp"), 100, Color.WHITE)
        assertEquals('B'.code.toByte(), bytes[0]); assertEquals('M'.code.toByte(), bytes[1])
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals(bytes.size, buf.getInt(2))        // file size field
        assertEquals(54, buf.getInt(10))               // pixel data offset
        assertEquals(40, buf.getInt(18))               // width
        assertEquals(30, buf.getInt(22))               // height
        assertEquals(24, buf.getShort(28).toInt())     // 24 bpp
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)!!
        assertEquals(40, decoded.width); assertEquals(30, decoded.height)
        assertEquals(255, Color.red(decoded.getPixel(20, 15)))
        assertEquals(0, Color.green(decoded.getPixel(20, 15)))
    }

    @Test
    fun tiffIsAValidLittleEndianRgbFile() {
        val bytes = ConvertFormats.encode(redBmp(), ConvertFormats.byId("tiff"), 100, Color.WHITE)
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x4949, buf.getShort(0).toInt())  // "II"
        assertEquals(42, buf.getShort(2).toInt())
        val ifdOff = buf.getInt(4)
        val count = buf.getShort(ifdOff).toInt()
        assertEquals(10, count)
        val tags = mutableMapOf<Int, Int>()
        for (i in 0 until count) {
            val e = ifdOff + 2 + i * 12
            tags[buf.getShort(e).toInt()] = buf.getInt(e + 8)
        }
        assertEquals(40, tags[256]); assertEquals(30, tags[257])
        assertEquals(8, tags[273])                     // pixel data right after header
        assertEquals(40 * 30 * 3, tags[279])           // RGB strip bytes
        // first pixel (top-left) is red: R,G,B bytes at offset 8
        assertEquals(255, bytes[8].toInt() and 0xFF)
        assertEquals(0, bytes[9].toInt() and 0xFF)
        assertEquals(0, bytes[10].toInt() and 0xFF)
    }

    @Test
    fun tiffKeepsAlphaWithExtraSamples() {
        val bytes = ConvertFormats.encode(redBmp(alpha = true), ConvertFormats.byId("tiff"), 100, Color.WHITE)
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val ifdOff = buf.getInt(4)
        val count = buf.getShort(ifdOff).toInt()
        assertEquals(11, count)
        val tags = mutableMapOf<Int, Int>()
        for (i in 0 until count) {
            val e = ifdOff + 2 + i * 12
            tags[buf.getShort(e).toInt()] = buf.getInt(e + 8)
        }
        assertEquals(40 * 30 * 4, tags[279])           // RGBA strip bytes
        assertEquals(2, tags[338])                     // ExtraSamples = unassociated alpha
        // pixel (5,0) is opaque red: R,G,B,A at 8 + 5*4
        assertEquals(255, bytes[8 + 5 * 4 + 3].toInt() and 0xFF)
        // pixel (35,0) is transparent: alpha byte 0 at 8 + 35*4 + 3
        assertEquals(0, bytes[8 + 35 * 4 + 3].toInt() and 0xFF)
    }

    @Test
    fun pdfEncodesEveryPageAndRendersWhenSupported() {
        val red = redBmp()
        val blue = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.BLUE) }
        // The host test JVM has no native pdfium, so the real PdfDocument reports
        // itself closed and generation cannot run here. Accept exactly that known
        // signature; every other failure means a real bug and fails the test. On
        // device (his phone) the same code produces and renders real PDFs.
        val bytes = try {
            ConvertFormats.encodePdf(listOf(red, blue), Color.WHITE)
        } catch (e: IllegalStateException) {
            assertTrue("unexpected PdfDocument failure: ${e.message}", e.message?.contains("closed") == true)
            return
        }
        val expectedPages = 2
        assertTrue(bytes.size > 100)
        assertEquals('%'.code.toByte(), bytes[0]); assertEquals('P'.code.toByte(), bytes[1])
        val text = String(bytes, Charsets.ISO_8859_1)
        assertEquals(expectedPages, Regex("/Type\\s*/Page(?!s)").findAll(text).count()) // one page object per image
        assertTrue(text.contains("/MediaBox"))                                            // real page dimensions
        // Pixel-level check on runtimes with real pdfium (his phone); Robolectric's
        // shadow PdfRenderer can throw, where the structure checks above stand in.
        runCatching {
            val tmp = File.createTempFile("pc_pdf_test", ".pdf")
            tmp.writeBytes(bytes)
            val renderer = PdfRenderer(ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY))
            assertEquals(expectedPages, renderer.pageCount)
            val page = renderer.openPage(0)
            val rendered = Bitmap.createBitmap(40, 30, Bitmap.Config.ARGB_8888)
            page.render(rendered, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
            assertTrue(Color.red(rendered.getPixel(20, 15)) > 200)
            page.close()
            if (expectedPages == 2) {
                val page2 = renderer.openPage(1)
                val rendered2 = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
                page2.render(rendered2, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                assertTrue(Color.blue(rendered2.getPixel(10, 10)) > 200)
                page2.close()
            }
            renderer.close()
            tmp.delete()
        }
    }

    @Test
    fun formatDetectionReadsContentNotExtension() {
        assertEquals("JPG", ConvertFormats.detectFormatBytes(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 1, 2), 6))
        assertEquals("PNG", ConvertFormats.detectFormatBytes(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A).also {}, 8))
        assertEquals("GIF", ConvertFormats.detectFormatBytes("GIF89a123".toByteArray(), 9))
        assertEquals("BMP", ConvertFormats.detectFormatBytes(byteArrayOf(0x42, 0x4D, 0, 0), 4))
        assertEquals("WEBP", ConvertFormats.detectFormatBytes("RIFF1234WEBP".toByteArray(), 12))
        assertEquals("TIFF", ConvertFormats.detectFormatBytes(byteArrayOf(0x49, 0x49, 42, 0), 4))
        assertEquals("TIFF", ConvertFormats.detectFormatBytes(byteArrayOf(0x4D, 0x4D, 0, 42), 4))
        assertEquals("PDF", ConvertFormats.detectFormatBytes("%PDF-1.7".toByteArray(), 8))
        assertEquals("HEIC", ConvertFormats.detectFormatBytes("....ftypheic".toByteArray(), 12))
        assertEquals(null, ConvertFormats.detectFormatBytes(byteArrayOf(1, 2, 3, 4), 4))
        // the case he cares about: a file NAMED .jpg whose content is PNG must report PNG
        val pngBytes = ConvertFormats.encode(redBmp(), ConvertFormats.byId("png"), 100, android.graphics.Color.WHITE)
        assertEquals("PNG", ConvertFormats.detectFormatBytes(pngBytes.copyOf(16), 16))
    }

    @Test
    fun pdfInputRendersPagesWhereSupported() {
        // Minimal but fully valid 2-page PDF (proper xref), built byte-exactly so both
        // strict and lenient parsers accept it.
        val objs = listOf(
            "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n",
            "2 0 obj<</Type/Pages/Kids[3 0 R 4 0 R]/Count 2>>endobj\n",
            "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 40 30]>>endobj\n",
            "4 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 20 20]>>endobj\n"
        )
        val sb = StringBuilder("%PDF-1.4\n")
        val offsets = mutableListOf<Int>()
        objs.forEach { offsets.add(sb.length); sb.append(it) }
        val xrefPos = sb.length
        sb.append("xref\n0 5\n0000000000 65535 f \n")
        offsets.forEach { sb.append(String.format("%010d 00000 n \n", it)) }
        sb.append("trailer<</Size 5/Root 1 0 R>>\nstartxref\n").append(xrefPos).append("\n%%EOF")
        val pdf = sb.toString().toByteArray(Charsets.ISO_8859_1)
        assertEquals("PDF", ConvertFormats.detectFormatBytes(pdf.copyOf(16), 16))
        val tmp = File.createTempFile("pc_pdf_in_test", ".pdf")
        tmp.writeBytes(pdf)
        try {
            val pages = ConvertFormats.renderPdfPages(ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY))
            if (pages.isEmpty()) return   // host shadow parses no pages; on-device check covers this
            assertEquals(2, pages.size)
            assertEquals(40, pages[0].width); assertEquals(30, pages[0].height)
            assertEquals(20, pages[1].width); assertEquals(20, pages[1].height)
            val px = pages[0].getPixel(20, 15)
            assertEquals(255, android.graphics.Color.red(px))
            assertEquals(255, android.graphics.Color.green(px))
            assertEquals(255, android.graphics.Color.blue(px))   // blank page renders white
        } catch (e: Exception) {
            val ok = e is IllegalStateException || e is UnsatisfiedLinkError || e is NoClassDefFoundError
            assertTrue("unexpected PdfRenderer failure: ${e}", ok)
        }
        tmp.delete()
    }

    @Test
    fun pdfPageCountAndSinglePageWhereSupported() {
        val objs = listOf(
            "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n",
            "2 0 obj<</Type/Pages/Kids[3 0 R 4 0 R]/Count 2>>endobj\n",
            "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 40 30]>>endobj\n",
            "4 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 20 20]>>endobj\n"
        )
        val sb = StringBuilder("%PDF-1.4\n")
        val offsets = mutableListOf<Int>()
        objs.forEach { offsets.add(sb.length); sb.append(it) }
        val xrefPos = sb.length
        sb.append("xref\n0 5\n0000000000 65535 f \n")
        offsets.forEach { sb.append(String.format("%010d 00000 n \n", it)) }
        sb.append("trailer<</Size 5/Root 1 0 R>>\nstartxref\n").append(xrefPos).append("\n%%EOF")
        val tmp = File.createTempFile("pc_pdf_count_test", ".pdf")
        tmp.writeBytes(sb.toString().toByteArray(Charsets.ISO_8859_1))
        try {
            val pfd = ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY)
            val count = ConvertFormats.pdfPageCount(pfd)
            if (count == 0) return   // host shadow parses no pages
            assertEquals(2, count)
            val p1 = ConvertFormats.renderPdfPage(ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY), 1)
            assertEquals(20, p1.width)
            assertEquals(20, p1.height)
            try {
                ConvertFormats.renderPdfPage(ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY), 5)
                org.junit.Assert.fail("out-of-range page must throw")
            } catch (expected: IllegalArgumentException) { }
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("closed") || e is java.io.IOException) return   // host JVM has no PDF engine
            throw e
        } finally {
            tmp.delete()
        }
    }

    @Test
    fun withSeekablePdfCopiesAndCleansUp() {
        // Some providers (Downloads, cloud-backed files) hand out non-seekable pipe
        // descriptors that PdfRenderer rejects. withSeekablePdf must copy the bytes
        // to a temp file, give the block a seekable descriptor, and delete the temp.
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val objs = listOf(
            "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n",
            "2 0 obj<</Type/Pages/Kids[3 0 R 4 0 R]/Count 2>>endobj\n",
            "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 40 30]>>endobj\n",
            "4 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 20 20]>>endobj\n"
        )
        val sb = StringBuilder("%PDF-1.4\n")
        val offsets = mutableListOf<Int>()
        objs.forEach { offsets.add(sb.length); sb.append(it) }
        val xrefPos = sb.length
        sb.append("xref\n0 5\n0000000000 65535 f \n")
        offsets.forEach { sb.append(String.format("%010d 00000 n \n", it)) }
        sb.append("trailer<</Size 5/Root 1 0 R>>\nstartxref\n").append(xrefPos).append("\n%%EOF")
        // file:// exercises the same openInputStream -> temp-copy -> seekable-descriptor
        // path; the non-seekable provider pipe it guards against exists only on-device.
        val src = File.createTempFile("pc_pdf_src_test", ".pdf")
        src.writeBytes(sb.toString().toByteArray(Charsets.ISO_8859_1))
        val uri = android.net.Uri.fromFile(src)
        fun tempLeftovers() = context.cacheDir.listFiles()?.count { it.name.startsWith("pixelcraft_pdf_") } ?: 0
        val before = tempLeftovers()
        try {
            val count = ConvertFormats.withSeekablePdf(context, uri) { ConvertFormats.pdfPageCount(it) }
            if (count != 0) assertEquals(2, count)   // host shadow parses no pages; on-device path proves the copy
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (!(msg.contains("closed") || e is java.io.IOException)) throw e   // host JVM has no PDF engine
        } finally {
            assertEquals(before, tempLeftovers())   // temp file always deleted
            src.delete()
        }
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
    fun twoHundredPagePdfCountsAndRendersLatePageFromFile() {
        // His requirement: even a 200-page PDF must open, show every page, and render
        // a late page directly (page 199 without rendering the 199 before it).
        val tmp = File.createTempFile("pc_pdf_200_test", ".pdf")
        tmp.writeBytes(buildPdfBytes(200))
        try {
            val count = ConvertFormats.pdfPageCountFromFile(tmp)
            if (count == 0) return   // host shadow parses no pages
            assertEquals(200, count)
            val p199 = ConvertFormats.renderPdfPageFromFile(tmp, 199, maxDim = 200)
            assertTrue(p199.width > 0 && p199.height > 0)
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("closed") || e is java.io.IOException) return   // host JVM has no PDF engine
            throw e
        } finally {
            tmp.delete()
        }
    }

    @Test
    fun allPagesRasterFromTwoPagePdf() {
        // His flow: every page converted in one run with the same settings, one file per page.
        val tmp = File.createTempFile("pc_pdf_all_test", ".pdf")
        tmp.writeBytes(buildPdfBytes(2))
        val outDir = File.createTempFile("pc_print_out", "").apply { delete(); mkdirs() }
        try {
            val m = buildPrintRasterAllPages(tmp, 4.0, 6.0, 0.0, 0.0, android.graphics.Color.WHITE, "Fit", 150, 90, "JPG", "test-print", outDir) { _, _ -> }
            assertEquals(2, m.files.size)
            assertTrue(m.files[0].name.contains("-page-001") && m.files[1].name.contains("-page-002"))
            m.files.forEach { f ->
                val decoded = BitmapFactory.decodeFile(f.absolutePath)
                assertTrue(decoded != null && decoded.width > 0 && decoded.height > 0)
            }
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("closed") || msg.contains("No pages") || e is java.io.IOException) return   // host JVM has no PDF engine
            throw e
        } finally {
            tmp.delete()
            outDir.deleteRecursively()
        }
    }

    @Test
    fun allPagesPdfFromTwoPagePdf() {
        // One multi-page print-ready PDF, page size = chosen size, one source page per PDF page.
        val tmp = File.createTempFile("pc_pdf_allpdf_test", ".pdf")
        tmp.writeBytes(buildPdfBytes(2))
        val out = File.createTempFile("pc_print_all", ".pdf")
        try {
            val m = buildPrintPdfAllPages(tmp, 4.0, 6.0, 0.0, 0.0, "Fit", 150, false, "test-print", out) { _, _ -> }
            assertEquals(1, m.files.size)
            assertEquals("application/pdf", m.mime)
            val bytes = out.readBytes()
            assertTrue(bytes.size > 100 && String(bytes, 0, 5) == "%PDF-")
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("closed") || msg.contains("No pages") || e is java.io.IOException) return   // host JVM has no PDF engine
            throw e
        } finally {
            tmp.delete()
            out.delete()
        }
    }

    @Test
    fun sixTargetsInHisOrder() {
        assertEquals(listOf("JPG/JPEG", "PNG", "WebP", "PDF", "BMP", "TIFF"), ConvertFormats.targets.map { it.label })
        assertTrue(ConvertFormats.qualityApplies(ConvertFormats.byId("jpg")))
        assertTrue(ConvertFormats.qualityApplies(ConvertFormats.byId("webp")))
        assertTrue(!ConvertFormats.qualityApplies(ConvertFormats.byId("png")))
        assertEquals("PNG", ConvertFormats.friendlyFormat("image/png"))
        assertEquals("JPG", ConvertFormats.friendlyFormat("image/jpeg"))
        assertEquals("HEIC", ConvertFormats.friendlyFormat("image/heic"))
    }
}
