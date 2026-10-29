package com.learnifyzone.pixelcraft.image

/** Official platform image dimensions for the Social Media Sizes tool.
 *  Researched Oct 29, 2026 against official platform sources - the full sourced
 *  table with links lives in docs/social-media-sizes.md. */
data class SocialSize(val label: String, val sub: String, val w: Int, val h: Int, val note: String = "")

data class SocialCategory(val label: String, val sub: String, val sizes: List<SocialSize>)

data class SocialPlatform(val name: String, val categories: List<SocialCategory>)

object SocialSizes {
    val platforms: List<SocialPlatform> = listOf(
        SocialPlatform("Instagram", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "320 x 320 px", 320, 320, "Shown as a circle"))),
            SocialCategory("Post", "Square, portrait and landscape", listOf(
                SocialSize("Square", "1080 x 1080 px (1:1)", 1080, 1080),
                SocialSize("Portrait", "1080 x 1350 px (4:5)", 1080, 1350),
                SocialSize("Landscape", "1080 x 566 px (1.91:1)", 1080, 566))),
            SocialCategory("Story", "Full-screen vertical image", listOf(
                SocialSize("Story", "1080 x 1920 px (9:16)", 1080, 1920))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Feed Ad", "1080 x 1080 px (1:1)", 1080, 1080),
                SocialSize("Story Ad", "1080 x 1920 px (9:16)", 1080, 1920)))
        )),
        SocialPlatform("Facebook", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "320 x 320 px", 320, 320, "Shown as a circle"))),
            SocialCategory("Post", "Square, portrait and landscape", listOf(
                SocialSize("Square", "1080 x 1080 px (1:1)", 1080, 1080),
                SocialSize("Portrait", "1080 x 1350 px (4:5)", 1080, 1350),
                SocialSize("Landscape", "1200 x 630 px (1.91:1)", 1200, 630))),
            SocialCategory("Story", "Full-screen vertical image", listOf(
                SocialSize("Story", "1080 x 1920 px (9:16)", 1080, 1920))),
            SocialCategory("Cover Photo", "Profile and page cover", listOf(
                SocialSize("Cover Photo", "851 x 315 px", 851, 315, "Keep key content centered - sides crop on mobile"))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Feed Ad", "1080 x 1080 px (1:1)", 1080, 1080),
                SocialSize("Link Ad", "1200 x 630 px (1.91:1)", 1200, 630)))
        )),
        SocialPlatform("YouTube", listOf(
            SocialCategory("Profile Picture", "Channel icon", listOf(
                SocialSize("Channel Icon", "800 x 800 px", 800, 800, "Renders at 98 x 98 px, shown as a circle"))),
            SocialCategory("Cover Photo / Banner", "Channel banner", listOf(
                SocialSize("Channel Banner", "2560 x 1440 px", 2560, 1440, "Keep text and logo inside the center 1546 x 423 px safe area"))),
            SocialCategory("Video Thumbnail", "Video cover and preview", listOf(
                SocialSize("Video Thumbnail", "1280 x 720 px (16:9)", 1280, 720),
                SocialSize("Podcast Thumbnail", "1280 x 1280 px (1:1)", 1280, 1280)))
        )),
        SocialPlatform("TikTok", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "200 x 200 px", 200, 200, "Shown as a circle"))),
            SocialCategory("Post", "Photo and carousel posts", listOf(
                SocialSize("Vertical Photo", "1080 x 1920 px (9:16)", 1080, 1920),
                SocialSize("Square Photo", "1080 x 1080 px (1:1)", 1080, 1080)))
        )),
        SocialPlatform("LinkedIn", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Photo", "400 x 400 px", 400, 400, "Shown as a circle"))),
            SocialCategory("Post", "Feed image sizes", listOf(
                SocialSize("Landscape", "1200 x 627 px (1.91:1)", 1200, 627),
                SocialSize("Square", "1080 x 1080 px (1:1)", 1080, 1080))),
            SocialCategory("Cover Photo / Banner", "Profile background photo", listOf(
                SocialSize("Background Photo", "1584 x 396 px (4:1)", 1584, 396),
                SocialSize("Company Page Cover", "1128 x 191 px", 1128, 191))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Sponsored Content", "1200 x 627 px (1.91:1)", 1200, 627)))
        )),
        SocialPlatform("Pinterest", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "165 x 165 px", 165, 165, "Shown as a circle"))),
            SocialCategory("Post", "Pins", listOf(
                SocialSize("Standard Pin", "1000 x 1500 px (2:3)", 1000, 1500, "Taller than 2:3 gets cut off in feeds"),
                SocialSize("Square Pin", "1000 x 1000 px (1:1)", 1000, 1000),
                SocialSize("Idea Pin", "1080 x 1920 px (9:16)", 1080, 1920))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Promoted Pin", "1000 x 1500 px (2:3)", 1000, 1500)))
        )),
        SocialPlatform("X", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Photo", "400 x 400 px", 400, 400, "Shown as a circle"))),
            SocialCategory("Post", "Timeline image sizes", listOf(
                SocialSize("Landscape", "1600 x 900 px (16:9)", 1600, 900),
                SocialSize("Square", "1080 x 1080 px (1:1)", 1080, 1080),
                SocialSize("Portrait", "1080 x 1350 px (4:5)", 1080, 1350))),
            SocialCategory("Cover Photo / Banner", "Profile header", listOf(
                SocialSize("Header Photo", "1500 x 500 px (3:1)", 1500, 500))),
            SocialCategory("Ad Creative", "Promotional image sizes", listOf(
                SocialSize("Website Card Ad", "1200 x 675 px (16:9)", 1200, 675)))
        )),
        SocialPlatform("WhatsApp", listOf(
            SocialCategory("Profile Picture", "Avatar and account photo", listOf(
                SocialSize("Profile Picture", "1080 x 1080 px", 1080, 1080, "Minimum 192 x 192 px, shown as a circle"))),
            SocialCategory("Story", "Status updates", listOf(
                SocialSize("Status", "1080 x 1920 px (9:16)", 1080, 1920)))
        ))
    )
}
