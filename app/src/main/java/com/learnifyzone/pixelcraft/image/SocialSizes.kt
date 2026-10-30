package com.learnifyzone.pixelcraft.image

/** Platform image dimensions for the Social Media Sizes tool.
 *  Researched Oct 29-30, 2026. Sizes with official = true come from the platform's own
 *  help/docs pages; official = false shows in the app as "Common size" (no official
 *  source publishes exact pixels). The sourced table lives in docs/social-media-sizes.md. */
data class SocialSize(val label: String, val sub: String, val w: Int, val h: Int, val note: String = "", val official: Boolean = true)

data class SocialCategory(val label: String, val sub: String, val sizes: List<SocialSize>)

data class SocialPlatform(val name: String, val categories: List<SocialCategory>)

object SocialSizes {
    val platforms: List<SocialPlatform> = listOf(
        SocialPlatform("Instagram", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "320 × 320 px", 320, 320, "Shown as a circle", official = false))),
            SocialCategory("Post", "Square, portrait and landscape", listOf(
                SocialSize("Square", "1080 × 1080 px (1:1)", 1080, 1080),
                SocialSize("Portrait", "1080 × 1350 px (4:5)", 1080, 1350),
                SocialSize("Landscape", "1080 × 566 px (1.91:1)", 1080, 566))),
            SocialCategory("Story", "Full-screen vertical image", listOf(
                SocialSize("Story", "1080 × 1920 px (9:16)", 1080, 1920))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Feed Ad", "1080 × 1080 px (1:1)", 1080, 1080, official = false),
                SocialSize("Story Ad", "1080 × 1920 px (9:16)", 1080, 1920, official = false)))
        )),
        SocialPlatform("Facebook", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "320 × 320 px", 320, 320, "Shown as a circle", official = false))),
            SocialCategory("Post", "Square, portrait and landscape", listOf(
                SocialSize("Square", "1080 × 1080 px (1:1)", 1080, 1080, official = false),
                SocialSize("Portrait", "1080 × 1350 px (4:5)", 1080, 1350, official = false),
                SocialSize("Landscape", "1200 × 630 px (1.91:1)", 1200, 630))),
            SocialCategory("Story", "Full-screen vertical image", listOf(
                SocialSize("Story", "1080 × 1920 px (9:16)", 1080, 1920, official = false))),
            SocialCategory("Cover Photo", "Profile and page cover", listOf(
                SocialSize("Cover Photo", "851 × 315 px", 851, 315, "Keep key content centered - sides crop on mobile"))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Feed Ad", "1080 × 1080 px (1:1)", 1080, 1080, official = false),
                SocialSize("Link Ad", "1200 × 630 px (1.91:1)", 1200, 630)))
        )),
        SocialPlatform("YouTube", listOf(
            SocialCategory("Profile Picture", "Channel icon", listOf(
                SocialSize("Channel Icon", "800 × 800 px", 800, 800, "Renders at 98 × 98 px, shown as a circle"))),
            SocialCategory("Cover Photo / Banner", "Channel banner", listOf(
                SocialSize("Channel Banner", "2560 × 1440 px", 2560, 1440, "Keep text and logo inside the center 1546 × 423 px safe area"))),
            SocialCategory("Video Thumbnail", "Video cover and preview", listOf(
                SocialSize("Video Thumbnail", "1280 × 720 px (16:9)", 1280, 720),
                SocialSize("Podcast Thumbnail", "1280 × 1280 px (1:1)", 1280, 1280)))
        )),
        SocialPlatform("TikTok", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "200 × 200 px", 200, 200, "Shown as a circle", official = false))),
            SocialCategory("Post", "Photo and carousel posts", listOf(
                SocialSize("Vertical Photo", "1080 × 1920 px (9:16)", 1080, 1920, official = false),
                SocialSize("Square Photo", "1080 × 1080 px (1:1)", 1080, 1080, official = false)))
        )),
        SocialPlatform("LinkedIn", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Photo", "400 × 400 px", 400, 400, "Shown as a circle"))),
            SocialCategory("Post", "Feed image sizes", listOf(
                SocialSize("Landscape", "1200 × 627 px (1.91:1)", 1200, 627),
                SocialSize("Square", "1080 × 1080 px (1:1)", 1080, 1080))),
            SocialCategory("Cover Photo / Banner", "Profile background photo", listOf(
                SocialSize("Background Photo", "1584 × 396 px (4:1)", 1584, 396),
                SocialSize("Company Page Cover", "1128 × 191 px", 1128, 191))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Sponsored Content", "1200 × 627 px (1.91:1)", 1200, 627)))
        )),
        SocialPlatform("Pinterest", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "165 × 165 px", 165, 165, "Shown as a circle", official = false))),
            SocialCategory("Cover Photo", "Profile cover banner", listOf(
                SocialSize("Profile Cover", "800 × 450 px (16:9)", 800, 450, "Business accounts; Pinterest crops to 16:9 - larger 16:9 sizes also work"))),
            SocialCategory("Post", "Pins", listOf(
                SocialSize("Standard Pin", "1000 × 1500 px (2:3)", 1000, 1500, "Taller than 2:3 gets cut off in feeds"),
                SocialSize("Square Pin", "1000 × 1000 px (1:1)", 1000, 1000),
                SocialSize("Idea Pin", "1080 × 1920 px (9:16)", 1080, 1920))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Promoted Pin", "1000 × 1500 px (2:3)", 1000, 1500)))
        )),
        SocialPlatform("X", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Photo", "400 × 400 px", 400, 400, "Shown as a circle"))),
            SocialCategory("Post", "Timeline image sizes", listOf(
                SocialSize("Landscape", "1600 × 900 px (16:9)", 1600, 900, official = false),
                SocialSize("Square", "1080 × 1080 px (1:1)", 1080, 1080, official = false),
                SocialSize("Portrait", "1080 × 1350 px (4:5)", 1080, 1350, official = false))),
            SocialCategory("Cover Photo / Banner", "Profile header", listOf(
                SocialSize("Header Photo", "1500 × 500 px (3:1)", 1500, 500))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Website Card Ad", "1200 × 675 px (16:9)", 1200, 675, official = false)))
        )),
        SocialPlatform("WhatsApp", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "1080 × 1080 px", 1080, 1080, "Minimum 192 × 192 px, shown as a circle", official = false))),
            SocialCategory("Story", "Status updates", listOf(
                SocialSize("Status", "1080 × 1920 px (9:16)", 1080, 1920, official = false)))
        ))
    )
}
