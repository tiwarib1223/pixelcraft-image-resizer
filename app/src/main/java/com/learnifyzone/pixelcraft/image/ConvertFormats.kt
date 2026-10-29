package com.learnifyzone.pixelcraft.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import java.io.ByteArrayOutputStream

/**
 * Real encoders for the Format Converter tool.
 *
 * JPEG/PNG/WebP go through Bitmap.compress. BMP is written as a real 24-bit
 * BI_RGB bitmap file (alpha flattened onto the chosen background). TIFF is a
 * valid little-endian baseline TIFF, uncompressed RGB or RGBA (alpha kept).
 * PDF uses PdfDocument, one page per image, page size = image size.
 */
object ConvertFormats {

    data class Fmt(val id: String, val label: String, val desc: String, val extension: String, val mimeType: String)

    val targets = listOf(
        Fmt("jpg", "JPG/JPEG", "Photos and smaller files", "jpg", "image/jpeg"),
        Fmt("png", "PNG", "Transparency support", "png", "image/png"),
        Fmt("webp", "WebP", "Web-friendly images", "webp", "image/webp"),
        Fmt("pdf", "PDF", "Images in a document", "pdf", "application/pdf"),
        Fmt("bmp", "BMP", "Bitmap images", "bmp", "image/bmp"),
        Fmt("tiff", "TIFF", "High-quality image files", "tiff", "image/tiff")
    )

    fun byId(id: String): Fmt = targets.first { it.id == id }

    /** Quality applies to JPEG and WebP only; the others are lossless. */
    fun qualityApplies(fmt: Fmt): Boolean = fmt.id == "jpg" || fmt.id == "webp"

    fun encode(src: Bitmap, fmt: Fmt, quality: Int, bgColor: Int): ByteArray = when (fmt.id) {
        "jpg" -> {
            val flat = flatten(src, bgColor)
            val baos = ByteArrayOutputStream()
            flat.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), baos)
            baos.toByteArray()
        }
        "png" -> {
            val baos = ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.PNG, 100, baos)
            baos.toByteArray()
        }
        "webp" -> {
            val baos = ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.WEBP, quality.coerceIn(1, 100), baos)
            baos.toByteArray()
        }
        "bmp" -> encodeBmp(src, bgColor)
        "tiff" -> encodeTiff(src)
        "pdf" -> encodePdf(listOf(src), bgColor)
        else -> throw IllegalArgumentException("Unknown format: ${fmt.id}")
    }

    /** Draws the bitmap onto a solid color. Returns the source unchanged when it has no alpha. */
    fun flatten(src: Bitmap, color: Int): Bitmap {
        if (!src.hasAlpha()) return src
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(color)
        canvas.drawBitmap(src, 0f, 0f, null)
        return out
    }

    /** 24-bit uncompressed BMP (BI_RGB), bottom-up rows, BGR order, 4-byte row padding. */
    fun encodeBmp(src: Bitmap, bgColor: Int): ByteArray {
        val flat = flatten(src, bgColor)
        val w = flat.width
        val h = flat.height
        val rowPadded = (w * 3 + 3) / 4 * 4
        val imageSize = rowPadded * h
        val fileSize = 54 + imageSize
        val out = ByteArrayOutputStream(fileSize)
        fun le16(v: Int) { out.write(v and 0xFF); out.write((v shr 8) and 0xFF) }
        fun le32(v: Int) { le16(v and 0xFFFF); le16((v shr 16) and 0xFFFF) }
        out.write(0x42); out.write(0x4D)          // "BM"
        le32(fileSize); le32(0); le32(54)          // size, reserved, pixel data offset
        le32(40); le32(w); le32(h)                 // BITMAPINFOHEADER
        le16(1); le16(24)                          // planes, bits per pixel
        le32(0); le32(imageSize)                   // BI_RGB, image size
        le32(2835); le32(2835)                     // ~72 DPI in px/m
        le32(0); le32(0)                           // palette colors
        val pixels = IntArray(w * h)
        flat.getPixels(pixels, 0, w, 0, 0, w, h)
        val row = ByteArray(rowPadded)
        for (y in h - 1 downTo 0) {
            java.util.Arrays.fill(row, 0.toByte())
            for (x in 0 until w) {
                val p = pixels[y * w + x]
                row[x * 3] = (p and 0xFF).toByte()
                row[x * 3 + 1] = ((p shr 8) and 0xFF).toByte()
                row[x * 3 + 2] = ((p shr 16) and 0xFF).toByte()
            }
            out.write(row)
        }
        return out.toByteArray()
    }

    /**
     * Little-endian baseline TIFF, one uncompressed strip, top-down rows.
     * RGB when the source is opaque, RGBA (ExtraSamples = unassociated alpha) otherwise.
     */
    fun encodeTiff(src: Bitmap): ByteArray {
        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        // The ARGB_8888 config flag is set even for fully opaque images (e.g. decoded
        // JPEGs), so scan the pixels: RGBA only when a pixel is actually transparent.
        val reallyHasAlpha = src.hasAlpha() && pixels.any { (it ushr 24) != 0xFF }
        val spp = if (reallyHasAlpha) 4 else 3
        val pixelBytes = w * h * spp
        val dataOffset = 8
        var ifdOffset = dataOffset + pixelBytes
        if (ifdOffset % 2 != 0) ifdOffset += 1     // IFD must start on an even offset
        val entryCount = if (spp == 4) 11 else 10
        val bitsOffset = ifdOffset + 2 + 12 * entryCount + 4
        val total = bitsOffset + 2 * spp
        val out = ByteArrayOutputStream(total)
        fun le16(v: Int) { out.write(v and 0xFF); out.write((v shr 8) and 0xFF) }
        fun le32(v: Int) { le16(v and 0xFFFF); le16((v shr 16) and 0xFFFF) }
        out.write(0x49); out.write(0x49)           // "II" little-endian
        le16(42); le32(ifdOffset)
        for (p in pixels) {
            out.write((p shr 16) and 0xFF)         // R
            out.write((p shr 8) and 0xFF)          // G
            out.write(p and 0xFF)                  // B
            if (spp == 4) out.write((p shr 24) and 0xFF) // A
        }
        while (out.size() < ifdOffset) out.write(0)
        fun entry(tag: Int, type: Int, count: Int, value: Int) {
            le16(tag); le16(type); le32(count); le32(value)
        }
        le16(entryCount)
        entry(256, 4, 1, w)                        // ImageWidth (LONG)
        entry(257, 4, 1, h)                        // ImageLength (LONG)
        entry(258, 3, spp, bitsOffset)             // BitsPerSample -> array of SHORTs
        entry(259, 3, 1, 1)                        // Compression = none
        entry(262, 3, 1, 2)                        // Photometric = RGB
        entry(273, 4, 1, dataOffset)               // StripOffsets
        entry(277, 3, 1, spp)                      // SamplesPerPixel
        entry(278, 4, 1, h)                        // RowsPerStrip
        entry(279, 4, 1, pixelBytes)               // StripByteCounts
        entry(284, 3, 1, 1)                        // PlanarConfiguration = chunky
        if (spp == 4) entry(338, 3, 1, 2)          // ExtraSamples = unassociated alpha
        le32(0)                                    // no next IFD
        for (i in 0 until spp) le16(8)             // 8 bits per sample
        return out.toByteArray()
    }

    /** One PDF page per image; page size = image size, flattened onto the background color. */
    fun encodePdf(bitmaps: List<Bitmap>, bgColor: Int): ByteArray {
        val doc = PdfDocument()
        try {
            bitmaps.forEachIndexed { i, bmp ->
                val flat = flatten(bmp, bgColor)
                val info = PdfDocument.PageInfo.Builder(flat.width, flat.height, i + 1).create()
                val page = doc.startPage(info)
                page.canvas.drawColor(bgColor)
                page.canvas.drawBitmap(flat, 0f, 0f, null)
                doc.finishPage(page)
            }
            val baos = ByteArrayOutputStream()
            doc.writeTo(baos)
            return baos.toByteArray()
        } finally {
            doc.close()
        }
    }

    /** Short display label for a source MIME type, e.g. "image/png" -> "PNG". */
    fun friendlyFormat(mime: String?): String = when (mime?.lowercase()) {
        "image/jpeg", "image/jpg" -> "JPG"
        "image/png" -> "PNG"
        "image/webp" -> "WEBP"
        "image/gif" -> "GIF"
        "image/bmp", "image/x-ms-bmp" -> "BMP"
        "image/tiff" -> "TIFF"
        "image/heic", "image/heif" -> "HEIC"
        "application/pdf" -> "PDF"
        else -> mime?.substringAfter('/')?.uppercase()?.take(6) ?: "IMAGE"
    }
}
