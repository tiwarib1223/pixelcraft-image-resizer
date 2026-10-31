package com.learnifyzone.pixelcraft.image

/** Official shop image requirements for the Online Shop Sizes tool.
 *  Researched Oct 29, 2026 against official platform sources - the full sourced
 *  table with links lives in docs/shop-sizes.md. */
object ShopSizes {
    // Top-level picker order (his design): these 5 + "Other Shop".
    val mainShops = listOf("Etsy", "Shopify", "Amazon", "eBay", "Gumroad")

    val platforms: List<SocialPlatform> = listOf(
        SocialPlatform("Etsy", listOf(
            SocialCategory("Product Image", "Main product photo and listing images", listOf(
                SocialSize("Listing Photo", "2000 × 2000 px", 2000, 2000, "Etsy recommends at least 2000 px per side; first photo minimum 635 px"))),
            SocialCategory("Listing Gallery", "Additional product photos (up to 20)", listOf(
                SocialSize("Gallery Photo", "2000 × 2000 px", 2000, 2000))),
            SocialCategory("Shop Banner", "Store banner, cover and header", listOf(
                SocialSize("Big Banner", "1600 × 400 px", 1600, 400, "Minimum 1200 × 300 px"),
                SocialSize("Mini Banner", "1600 × 213 px", 1600, 213, "Minimum 1200 × 160 px"),
                SocialSize("Carousel Banner", "1200 × 300 px", 1200, 300, "Etsy Plus subscribers"))),
            SocialCategory("Shop Logo & Icon", "Brand icon and store profile image", listOf(
                SocialSize("Shop Icon", "500 × 500 px", 500, 500))),
            SocialCategory("Promotional Image", "Sale announcements and promotions", listOf(
                SocialSize("Order Receipt Banner", "760 × 100 px", 760, 100, "Minimum size for receipt banners"))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "2000 × 2000 px", 2000, 2000, "Matches the listing photo size", official = false)))
        )),
        SocialPlatform("Shopify", listOf(
            SocialCategory("Product Image", "Main product photo and listing images", listOf(
                SocialSize("Product Image", "2048 × 2048 px", 2048, 2048, "Up to 5000 × 5000 px / 20 MB; zoom needs more than 800 px", official = false))),
            SocialCategory("Listing Gallery", "Additional product photos", listOf(
                SocialSize("Gallery Image", "2048 × 2048 px", 2048, 2048, "Keep one aspect ratio across the gallery", official = false))),
            SocialCategory("Shop Banner", "Store banner and slideshow", listOf(
                SocialSize("Banner / Slideshow", "1200 × 400 px", 1200, 400, "Theme-dependent; 3:1 works on most themes", official = false))),
            SocialCategory("Shop Logo & Icon", "Brand logo and store profile image", listOf(
                SocialSize("Square Logo", "512 × 512 px", 512, 512, "Minimum size for brand assets; may be cropped to a circle", official = false))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "2048 × 2048 px", 2048, 2048, "Matches the product image size", official = false)))
        )),
        SocialPlatform("Amazon", listOf(
            SocialCategory("Product Image", "Main product photo and listing images", listOf(
                SocialSize("Product Image", "2000 × 2000 px", 2000, 2000, "500-10000 px allowed; over 1000 px enables zoom; Main image needs a pure white background", official = false))),
            SocialCategory("Listing Gallery", "Additional product photos", listOf(
                SocialSize("Gallery Image", "2000 × 2000 px", 2000, 2000, "Amazon recommends at least 6 images", official = false))),
            SocialCategory("Shop Banner", "Amazon Store hero image", listOf(
                SocialSize("Store Hero Image", "3000 × 600 px", 3000, 600, "Keep key content in the center 50%; sides crop on small screens"))),
            SocialCategory("Shop Logo & Icon", "Amazon Store brand logo", listOf(
                SocialSize("Brand Logo", "400 × 400 px", 400, 400))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "2000 × 2000 px", 2000, 2000, "Matches the product image size", official = false)))
        )),
        SocialPlatform("eBay", listOf(
            SocialCategory("Product Image", "Main product photo and listing images", listOf(
                SocialSize("Listing Photo", "1600 × 1600 px", 1600, 1600, "Minimum 500 px on the longest side; 1600 px enables zoom"))),
            SocialCategory("Listing Gallery", "Additional product photos (up to 24)", listOf(
                SocialSize("Gallery Photo", "1600 × 1600 px", 1600, 1600))),
            SocialCategory("Shop Banner", "eBay Store billboard", listOf(
                SocialSize("Store Billboard", "1200 × 270 px", 1200, 270, "Under 12 MB; some locales show 1280 × 290 px"))),
            SocialCategory("Shop Logo & Icon", "eBay Store logo", listOf(
                SocialSize("Store Logo", "300 × 300 px", 300, 300, "Minimum 150 × 150 px, under 12 MB"))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "1600 × 1600 px", 1600, 1600, "Matches the listing photo size", official = false)))
        )),
        SocialPlatform("Gumroad", listOf(
            SocialCategory("Product Image", "Product cover image", listOf(
                SocialSize("Product Cover", "1280 × 720 px (16:9)", 1280, 720, "At least 72 DPI, under 50 MB"))),
            SocialCategory("Listing Gallery", "Additional covers", listOf(
                SocialSize("Additional Cover", "1280 × 720 px (16:9)", 1280, 720, "Keep all covers the same shape for a clean page"))),
            SocialCategory("Promotional Image", "Discover and library thumbnail", listOf(
                SocialSize("Thumbnail", "600 × 600 px", 600, 600, "Minimum size for Discover, libraries and your profile"))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "1280 × 720 px (16:9)", 1280, 720, "Matches the product cover size", official = false)))
        )),
        SocialPlatform("Payhip", listOf(
            SocialCategory("Product Image", "Product cover image", listOf(
                SocialSize("Product Image", "1000 × 1000 px (1:1)", 1000, 1000, "Payhip adapts to your chosen ratio; square is the safe shape; minimum width 1000 px", official = false))),
            SocialCategory("Listing Gallery", "Additional product photos", listOf(
                SocialSize("Gallery Image", "1000 × 1000 px (1:1)", 1000, 1000, official = false))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "1000 × 1000 px (1:1)", 1000, 1000, "Matches the product image size", official = false)))
        )),
        SocialPlatform("WooCommerce", listOf(
            SocialCategory("Product Image", "Main product photo and listing images", listOf(
                SocialSize("Product Image", "1200 × 1200 px", 1200, 1200, "Minimum 800 × 800 px; larger renders better with zoom", official = false),
                SocialSize("Minimum", "800 × 800 px", 800, 800))),
            SocialCategory("Listing Gallery", "Additional product photos", listOf(
                SocialSize("Gallery Image", "1200 × 1200 px", 1200, 1200, official = false))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "1200 × 1200 px", 1200, 1200, "Matches the product image size", official = false)))
        )),
        SocialPlatform("Wix Stores", listOf(
            SocialCategory("Product Image", "Main product photo and listing images", listOf(
                SocialSize("Product Image", "3000 × 3000 px (1:1)", 3000, 3000, "Wix's minimum resolution; 1:1 recommended for mobile"))),
            SocialCategory("Listing Gallery", "Additional product photos", listOf(
                SocialSize("Gallery Image", "3000 × 3000 px (1:1)", 3000, 3000))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "3000 × 3000 px (1:1)", 3000, 3000, "Matches the product image size", official = false)))
        )),
        SocialPlatform("Squarespace", listOf(
            SocialCategory("Product Image", "Main product photo and listing images", listOf(
                SocialSize("Product Image", "2500 × 2500 px", 2500, 2500, "2500 px width is ideal; under 1500 px looks blurry; product images under 5 MB", official = false))),
            SocialCategory("Listing Gallery", "Additional product photos", listOf(
                SocialSize("Gallery Image", "2500 × 2500 px", 2500, 2500, official = false))),
            SocialCategory("Shop Banner", "Store banner and header", listOf(
                SocialSize("Banner", "2500 × 1500 px", 2500, 1500, "2500 px width gives the full resolution range", official = false))),
            SocialCategory("Shop Logo & Icon", "Site logo", listOf(
                SocialSize("Site Logo", "1000 × 1000 px", 1000, 1000, "Squarespace resizes logos automatically", official = false))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "2500 × 2500 px", 2500, 2500, "Matches the product image size", official = false)))
        )),
        SocialPlatform("Ko-fi Shop", listOf(
            SocialCategory("Product Image", "Product preview images (up to 20)", listOf(
                SocialSize("Square Preview", "600 × 600 px (1:1)", 600, 600),
                SocialSize("Wide Preview", "600 × 300 px (2:1)", 600, 300))),
            SocialCategory("Shop Banner", "Page cover image", listOf(
                SocialSize("Cover Image", "1200 × 400 px (3:1)", 1200, 400, "Under 8 MB; sides crop slightly on mobile; keep text centered"))),
            SocialCategory("Shop Logo & Icon", "Profile picture", listOf(
                SocialSize("Profile Picture", "500 × 500 px (1:1)", 500, 500, "Shown as a square", official = false))),
            SocialCategory("Promotional Image", "Sharing preview image", listOf(
                SocialSize("Share Preview", "1000 × 500 px (2:1)", 1000, 500, "Shown when your shop link is shared", official = false))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "600 × 600 px (1:1)", 600, 600, "Matches the square preview size", official = false)))
        )),
        SocialPlatform("Creative Market", listOf(
            SocialCategory("Product Image", "Product screenshots", listOf(
                SocialSize("Screenshot", "1820 × 1214 px", 1820, 1214, "Minimum 910 × 607; maximum 3640 × 10920; under 5 MB"),
                SocialSize("Minimum", "910 × 607 px", 910, 607))),
            SocialCategory("Listing Gallery", "Additional screenshots (up to 100)", listOf(
                SocialSize("Gallery Screenshot", "1820 × 1214 px", 1820, 1214, "Fill at least 3-5 screenshot slots"))),
            SocialCategory("Promotional Image", "Graphics product previews", listOf(
                SocialSize("Graphics Preview", "1920 × 1080 px", 1920, 1080, "HD minimum for graphics products", official = false))),
            SocialCategory("Product Collage", "Combine multiple photos in one layout", listOf(
                SocialSize("Collage", "1820 × 1214 px", 1820, 1214, "Matches the screenshot size", official = false)))
        ))
    )
}
