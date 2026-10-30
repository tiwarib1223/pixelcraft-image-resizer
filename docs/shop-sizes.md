# Online Shop Sizes - official dimension table

Researched October 29, 2026 against official platform documentation.
Encoded in `app/src/main/java/com/learnifyzone/pixelcraft/image/ShopSizes.kt`.

## Etsy (official: help.etsy.com article 115015663347 + 115015663247)
| Use | Size | Source |
| --- | --- | --- |
| Listing / gallery photo | 2000 x 2000 px recommended (first photo min 635 px) | help.etsy.com "Requirements and Best Practices for Images" |
| Big shop banner | 1600 x 400 px recommended (min 1200 x 300) | same + "Customize Your Shop's Appearance" |
| Mini shop banner | 1600 x 213 px recommended (min 1200 x 160) | same |
| Carousel banner | 1200 x 300 px (Etsy Plus) | same |
| Shop icon | 500 x 500 px | Etsy Seller Handbook |
| Order receipt banner | 760 x 100 px minimum | help.etsy.com |

## Shopify (official: help.shopify.com)
| Use | Size | Source |
| --- | --- | --- |
| Product / gallery image | 2048 x 2048 px (max 5000 x 5000, 20 MB; zoom > 800 px) | help.shopify.com "Product media types" |
| Banner / slideshow | 1200 x 400 px (theme-dependent, 3:1) | shopify.com/blog/image-sizes |
| Square logo | 512 x 512 px minimum (may crop to circle) | help.shopify.com "Managing brand assets" |

## Amazon (official: sellercentral.amazon.com + advertising.amazon.com)
| Use | Size | Source |
| --- | --- | --- |
| Product / gallery image | 2000 x 2000 px recommended (500-10000 allowed; > 1000 px enables zoom; MAIN: pure white background) | Seller Central "Product image requirements" |
| Store hero image | 3000 x 600 px, center 50% safe zone | advertising.amazon.com/resources/ad-specs/stores |
| Brand logo | 400 x 400 px | same |

## eBay (official: ebay help pages)
| Use | Size | Source |
| --- | --- | --- |
| Listing / gallery photo | 1600 x 1600 px (min 500 px longest side; 1600 enables zoom) | ebay.com/help "Adding pictures to listings" |
| Store billboard | 1200 x 270 px, under 12 MB (some locales show 1280 x 290) | pages.ebay.co.uk/ebay-shops + Stores checklist |
| Store logo | 300 x 300 px (min 150 x 150) | ebay.com.au/help "Manage eBay Store" |

## Gumroad (official: gumroad.com/help/article/60)
| Use | Size | Source |
| --- | --- | --- |
| Product cover / additional covers | 1280 x 720 px (16:9), at least 72 DPI, under 50 MB | "Adding a cover image" |
| Discover/library thumbnail | 600 x 600 px minimum | same |

## Payhip (official: help.payhip.com article 231)
| Use | Size | Source |
| --- | --- | --- |
| Product / gallery image | 1000 x 1000 px (1:1) - Payhip adapts to any ratio, square is the safe shape; min width 1000 px | "Optimize product images" |

## WooCommerce (official: woocommerce.com/document)
| Use | Size | Source |
| --- | --- | --- |
| Product / gallery image | 1200 x 1200 px (minimum 800 x 800; larger renders better with zoom) | "Adding Product Images and Galleries" |

## Wix Stores (official: support.wix.com)
| Use | Size | Source |
| --- | --- | --- |
| Product / gallery image | 3000 x 3000 px minimum, 1:1 recommended for mobile | "Adding product images, GIFs and videos" |

## Squarespace Commerce (official: support.squarespace.com)
| Use | Size | Source |
| --- | --- | --- |
| Product / gallery image | 2500 x 2500 px (2500 px width ideal, < 1500 px blurry, < 5 MB) | "Formatting images for display on the web" |
| Banner | 2500 x 1500 px (2500 px width) | same |
| Site logo | 1000 x 1000 px (Squarespace resizes automatically) | same |

## Ko-fi Shop (official: help.ko-fi.com)
| Use | Size | Source |
| --- | --- | --- |
| Product preview | 600 x 600 px (1:1) or 600 x 300 px (2:1) | "Ko-fi Shop - Sell digital & physical products" |
| Page cover | 1200 x 400 px (3:1), under 8 MB | "Make your page stand out with a cover image" |
| Profile picture | 500 x 500 px (1:1) | help.ko-fi.com |
| Share preview | 1000 x 500 px (2:1) | "Ko-fi Shop" article |

## Creative Market (official: support.creativemarket.com)
| Use | Size | Source |
| --- | --- | --- |
| Product screenshot / gallery | 1820 x 1214 px recommended (min 910 x 607, max 3640 x 10920, < 5 MB) | "Listing a New Product" |
| Graphics preview | 1920 x 1080 px minimum, 300 DPI | "Quality Guidelines for Graphics" |

Every platform also offers Product Collage (2-4 photos combined into one
canvas at the platform's product image size) and Custom Size (16-12000 px).

## In-app labels (v1.0.5)

Each shop size card shows "Official size" only when the shop's own help/spec
page gives that exact number; otherwise "Recommended size". Audited October 31, 2026.

Official (exact number on the shop's own page):
- Etsy: listing/gallery 2000 (min 635 first photo), big banner 1600x400, mini banner 1600x213, carousel 1200x300, shop icon 500x500, receipt banner 760x100 - help.etsy.com/hc/en-us/articles/115015663347
- Amazon: Store hero 3000x600, brand logo 400x400 - advertising.amazon.com/resources/ad-specs/stores
- eBay: listing/gallery 1600 longest side (min 500) - ebay help; store billboard 1200x270, store logo 300x300 - ebay.com.au Seller Centre
- Gumroad: cover 1280x720, thumbnail 600x600 - gumroad.com/help/article/60
- WooCommerce: minimum 800x800 - woocommerce.com/document/adding-product-images-and-galleries
- Wix Stores: 3000x3000 minimum resolution - support.wix.com (Wix Stores: Adding Product Images, GIFs, and Videos)
- Ko-fi: previews 600x600 / 600x300, cover 1200x400 - help.ko-fi.com
- Creative Market: screenshot 1820x1214 (min 910x607) - support.creativemarket.com

Recommended (the shop publishes no exact number for this use, or our pick):
- Shopify: all entries (help page gives only max 25 MP / 20 MB; 2048 is the widely used recommendation from Shopify's blog, not a help page)
- Amazon: product/gallery 2000 (official pages give ranges 500-10000 and 1000+ for zoom)
- Payhip: all entries (help page explicitly recommends no specific dimensions - aspect ratio only)
- Squarespace: all entries (official guidance is a 1500-2500 px range, not an exact size)
- Ko-fi: profile picture 500, share preview 1000x500 (Ko-fi's help gives 600x300 2:1 for social previews - flagged for his decision)
- Creative Market: graphics preview 1920x1080
- Every shop's "Product Collage" entry (our own collage-tool output size, matched to the listing size)
- eBay: (none)
- Etsy: Product Collage only
- Gumroad: Product Collage only
- WooCommerce: product/gallery 1200 (official pages recommend 800+ only)
- Wix: Product Collage only
