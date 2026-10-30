# Social Media Sizes - official dimension table

Researched October 29, 2026. Primary sources are official platform help pages
where available; otherwise the platform's current specs as published by
established design-tool references. Encoded in
`app/src/main/java/com/learnifyzone/pixelcraft/image/SocialSizes.kt`.

## Instagram
| Use | Size | Source |
| --- | --- | --- |
| Profile picture | 320 x 320 px (shown as circle) | figma.com/resource-library/instagram-photo-size, canva.com/sizes/instagram |
| Post - square | 1080 x 1080 px (1:1) | same |
| Post - portrait | 1080 x 1350 px (4:5) | same |
| Post - landscape | 1080 x 566 px (1.91:1) | same |
| Story | 1080 x 1920 px (9:16) | same |
| Feed ad / Story ad | 1080 x 1080 / 1080 x 1920 px | same |

## Facebook
| Use | Size | Source |
| --- | --- | --- |
| Profile picture | 320 x 320 px (shown as circle) | Meta recommendations PDF, adobe.com/express/discover/sizes/facebook |
| Cover photo | 851 x 315 px (center safe) | same |
| Post - square / portrait | 1080 x 1080 / 1080 x 1350 px | same |
| Post - landscape / link | 1200 x 630 px (1.91:1) | same |
| Story | 1080 x 1920 px (9:16) | same |
| Feed ad / link ad | 1080 x 1080 / 1200 x 630 px | same |

## YouTube (official: support.google.com/youtube)
| Use | Size | Source |
| --- | --- | --- |
| Channel icon | 800 x 800 px (renders 98 x 98, circle) | answer/10456525 |
| Channel banner | 2560 x 1440 px, safe area 1546 x 423 px | answer/12950272 |
| Video thumbnail | 1280 x 720 px (16:9) | answer/72431 |
| Podcast thumbnail | 1280 x 1280 px (1:1) | answer/72431 |

## TikTok
| Use | Size | Source |
| --- | --- | --- |
| Profile picture | 200 x 200 px (shown as circle) | support.tiktok.com, mediacheatsheet.com |
| Photo / carousel (vertical) | 1080 x 1920 px (9:16) | ayrshare.com |
| Square photo | 1080 x 1080 px (1:1) | ayrshare.com |

## LinkedIn (official: linkedin.com/help/linkedin)
| Use | Size | Source |
| --- | --- | --- |
| Profile photo | 400 x 400 px (shown as circle) | answer/a549049 |
| Background photo | 1584 x 396 px | answer/a568217 |
| Post - landscape | 1200 x 627 px (1.91:1) | answer/a563309 |
| Post - square | 1080 x 1080 px (1:1) | answer/a563309 |
| Company cover | 1128 x 191 px | linkedin.com/help/company-pages |
| Ad creative | 1200 x 627 px (1.91:1) | linkedin.com/help/ads |

## Pinterest (official: help.pinterest.com, create.pinterest.com)
| Use | Size | Source |
| --- | --- | --- |
| Profile picture | 165 x 165 px (shown as circle) | help.pinterest.com |
| Profile cover | 800 x 450 px min (16:9; larger 16:9 ok; business accounts) | help.pinterest.com/en/business/article/personalize-your-profile |
| Pin | 1000 x 1500 px (2:3, taller gets cut) | create.pinterest.com |
| Square pin | 1000 x 1000 px (1:1) | same |
| Idea pin | 1080 x 1920 px (9:16) | same |
| Ad | 1000 x 1500 px (2:3) | same |

## X (Twitter)
| Use | Size | Source |
| --- | --- | --- |
| Profile picture | 400 x 400 px (shown as circle) | X Help Center via twitterapi.io, shapelessai.com/sizes/x |
| Header | 1500 x 500 px | same |
| Post - landscape | 1600 x 900 px (16:9) | same |
| Post - square / portrait | 1080 x 1080 / 1080 x 1350 px | same |
| Ad creative | 1200 x 675 px (16:9) | same |

## WhatsApp
| Use | Size | Source |
| --- | --- | --- |
| Profile picture | 1080 x 1080 px (min 192 x 192, circle) | mediasizes.com, canaltech.com.br |
| Status | 1080 x 1920 px (9:16) | same |

Any size not listed can be made with the in-app Custom Size (16-12000 px).

## In-app labels (v1.0.4)

Each size card in the app now shows either "Official size" (confirmed on the
platform's own help/developer pages) or "Common size" (widely used, but not
published as an exact pixel spec on the platform's official pages at the time
of checking). The bottom note on the social home screen reads: "Sizes follow
each platform's official guidance at the time of checking. Platforms can
change them anytime."

Entries labelled "Common size" in the app:
- Instagram: Profile picture (320 x 320), Feed ad (1080 x 1080), Story ad (1080 x 1920)
- Facebook: Profile picture (320 x 320), Post - square (1080 x 1080), Post - portrait (1080 x 1350), Story (1080 x 1920), Feed ad (1080 x 1080)
- TikTok: all sizes (official pages publish only a 20 x 20 px minimum)
- Pinterest: Profile picture (165 x 165)
- X: Post - landscape (1600 x 900), Post - square (1080 x 1080), Post - portrait (1080 x 1350), Website card ad (1200 x 675)
- WhatsApp: Profile picture, Status

Note on X website card ads: the app keeps 1200 x 675 px. Some current
third-party 2026 references cite 800 x 418 px as the official website-card
spec; X's own current ads documentation could not be re-confirmed at audit
time, so the entry is labelled "Common size".

All other entries show "Official size", backed by help.instagram.com,
facebook.com/help, developers.facebook.com, support.google.com,
linkedin.com/help, help.pinterest.com, create.pinterest.com, and help.x.com.
