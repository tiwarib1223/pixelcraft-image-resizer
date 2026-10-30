# PixelCraft Image Resizer — Progress

## v0.20.3 (versionCode 70): seamless opening - the video's own first frame behind everything
His test of v0.20.2 (screen recording): the dim icon was gone, but the pre-video screen was a plain BLACK frame
and the video popping in was still a visible jump - "Still not properly fixed".
- Fix: extracted frame 0 of his untouched opening.mp4 (res/drawable-nodpi/opening_first_frame.png, 478x850) and
  made it (a) the activity window background (new drawable/opening_bg.xml on Theme.PixelCraft) so the pre-Compose
  window already shows the video's first scene, and (b) a fullscreen overlay on the video surface that hides the
  exact moment the player renders its first real frame (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START). The video
  now flows out of its own still first frame with no black gap and no pop. The video asset itself is untouched.
- Splash gate screenshot refreshed (v0203_phone_opening_gate).
- SECOND PASS, same evening (his "No brother still logo seen in starting" / "Remove frozen picture"): the ROOT CAUSE
  was inside his own asset - its actual frame 0 is the finished bright end-card logo (the player holds it while
  buffering, so a frozen logo showed first). Re-encoded the video WITHOUT that one frame (ffmpeg drop frame 0,
  ~33ms, audio stream copied untouched and in sync, h264+aac, 478x850, total 5.04s, CRF 18). Verified by extracting
  frames from the re-encoded file: frame 0 is now the dark starry wave, no logo. The window/overlay first-frame
  image was regenerated from the re-encoded video, so the app opens on exactly the video's new first frame -
  no frozen logo, no black frame, no pop.

## v0.20.2 (versionCode 69): Settings = mode + clear recent only, drawer becomes the info page, clean pre-video splash
His two requests after testing v0.20.1 on his phone (evening 2026-10-30, his messages + a 9s screen recording):
- Request 1 ("Setting page only mode and clear recently edited", "Rest of all move next side info page"):
  Settings now shows ONLY Dark mode and Clear recently edited. Everything else - Email us, Rate us, Share,
  Terms & Conditions, Privacy Policy - moved to the side drawer (now the app's info page), with the app version
  as small footer text ("A2Z Image Craft · Version 0.20.2 · Bitty"). Nothing duplicated, nothing removed from
  the app. The doc pages themselves are unchanged and keep the v0.20.1 back-safe navigation. (VETOABLE: version
  footer placement; the privacy info card "Your photos stay on this phone" was removed from Settings - its facts
  still live on the Privacy Policy page.)
- Request 2 ("Starting scene not good", "Exactly start from 3.2 seconds"): the pre-video system splash flashed a
  dim, muddy app icon on black before the video - looked cheap. The splash icon is now fully transparent and the
  splash background is the same starry navy (#030B1E) as the video, so the app opens straight into deep blue and
  his 5-second video flows in seamlessly. His video asset itself is untouched - full 5.05s, his audio, exactly as
  he confirmed again.
- Tests updated to the new layout (Settings minimal assertions, drawer lists all five rows + version footer, doc
  pages open from the drawer, back-still-never-exits regression kept). Screenshots refreshed (v0202_*).

## v0.20.1 (versionCode 68): HOTFIX - back navigation on doc pages + drawer dedup + his 5s opening video
His phone test of v0.20.0 (evening 2026-10-30, screenshots + his messages):
- REAL BUG: on the Terms & Conditions / Privacy Policy pages, system back EXITED the app instead of returning.
  Root cause: the global BackHandler only ran for tool screens; these pages sit on the Home-tab screen, so back
  fell through and finished the activity. Fix: three ordered BackHandlers - an open drawer closes first, a doc page
  returns to Settings, any other tab returns to Home. Back now always returns, never exits. The doc pages keep
  their top-bar back arrow (returns to Settings).
- Dedup (his call): Email us / Rate us / Share / Terms / Privacy showed in BOTH the drawer and Settings. The drawer
  now keeps only the quick actions (Email us, Rate us, Share); Terms & Conditions and Privacy Policy live in
  Settings only.
- Opening (his explicit instruction, overrides the v0.20.0 static intro): his own 5-second video plays fullscreen
  as the app opening (res/raw/opening.mp4, 478x850 h264+aac, letterboxed on its starry navy). Any tap or system
  back skips straight to the app - promised and wired. Plays on every cold start (VETOABLE: can become once-a-day).
  A playback error goes straight to the app rather than trapping him. "Tap to skip" hint top-right (VETOABLE).
- Regression tests: back from Terms/Privacy (arrow + system back) returns and never finishes the activity; drawer
  no longer lists the two documents; the bundled video is his ~5s clip with its audio track; the opening's tap
  surface invokes skip. Drawer/privacy screenshots refreshed (v0201_*).
- Test-host note (unchanged policy): no media playback under Robolectric, so the video gate is skipped there via
  the same FINGERPRINT guard as before; the asset, its duration/audio, and the skip wiring are tested instead.

## v0.20.0 (versionCode 67): FINALIZATION - Presets tab filled (verified sizes), Settings rebuilt, developer credit = Bitty
- His call (delegated to parent, confirmed "Then fill brother"): the Presets tab is now FILLED with 13 platform sizes, every one verified against official platform documentation on 2026-10-30. No guessed numbers - platforms that publish nothing (WhatsApp, IG/Pinterest profile photos) are deliberately absent.
- Shop & creator: Etsy Listing photo 2000x2000 (help.etsy.com). Social media: Instagram Post square 1080x1080 / portrait 1080x1350 / landscape 1080x566, Story + Reel cover 1080x1920 (help.instagram.com); Facebook Cover 851x315 + Post/link 1200x630 (facebook.com/help + developers.facebook.com); Pinterest Pin 1000x1500 (help.pinterest.com); YouTube Thumbnail 1280x720 + Channel banner 2560x1440 (support.google.com); X Profile 400x400 + Header 1500x500 (help.x.com). Every row shows platform, purpose, exact px, ratio, a Verified badge, and carries sourceUrl + dateVerified in presets.json.
- Tapping a preset arms the editor's target size and jumps into the resize flow; tapping Process then opens the size sheet with that exact size PREFILLED AND PRESELECTED under Resolution > Custom - one tap on "Resize to <size>" finishes. Search filters the list live.
- REAL BUG found by the end-to-end test (his "test deeply" rule): the first implementation only showed a "Preset applied" snackbar - the armed size never reached the size sheet, so the user would have had to type the numbers anyway. Caught because the test drives the full flow (preset tap -> Process -> sheet -> Resize to 2000 x 2000 px -> adapt dialog -> result must measure exactly 2000 x 2000), not just the toast.
- SETTINGS REBUILT (his "Make this page properly"): clean grouped cards with icon rows - Appearance (Dark mode switch), Privacy (on-device promise + Clear recently edited with confirm dialog - new RecentsStore.clear wipes the list AND cached thumbnails), Support (Rate on Google Play + Share the app), About (app name, version, developer credit). Same design language as the rest of the app.
- His steering: developer credit changed from "Learnify Zone" to "Bitty" everywhere in the UI (it appeared in exactly one place - the About line). Package name, signing and repo metadata untouched.
- Note: the Rate/Share rows point at the Play Store listing for the package id; they open the store page once the app is published.
- HOME TOP BAR (his reference app "Text Art", 3 screenshots): hamburger menu top-left + settings gear top-right. The hamburger opens a proper side drawer: navy header with the app name, then Need support? Email us / Rate us / Share / Terms & Conditions / Privacy Policy rows with icons.
- EMAIL US (his confirmed address, verbatim): bittyjee1223@gmail.com, opened as a mailto with subject "A2Z Image Craft support". Available from BOTH the drawer and the Settings Support section.
- TERMS & PRIVACY: simple in-app pages with honest professional copy - the privacy page states the truth of this app: everything happens on the phone, photos never leave the device, no account, no analytics, no ads, no tracking. Terms cover free use, your-photos-stay-yours, and no warranty in plain language.
- SPLASH SCREEN (his "What's about when app opens?" -> "Yes"): proper Android 12+ SplashScreen API plus a short branded opening moment that mirrors the splash video he designed himself (a2z_splash_perfect.mp4): dark navy background (#03132B, sampled from his video), his A2Z logo card extracted from the video itself, and "Powered by Bitty" underneath (his verbatim steering: remove "Every Pixel, Your Way.", bottom line reads Powered by Bitty). The system splash uses the same logo + background so the handover is seamless.
- PROFESSIONAL COPY PASS ("Research deeply and make every thing professional"): drawer, settings, dialogs, share text and doc pages all written in a clean professional register - no stage jargon, no developer notes, no empty promises.
- OFFICIAL LOGO (his own asset, confirmed as the app icon): launcher icon regenerated at every density from his official A2Z Image Craft logo (white margins cropped, rounded-square mask, adaptive icon with matching navy background), and the same logo is the splash/opening card. The Play Console listing graphic can use his 1254px master directly.
- VETOABLE PICKS this release: the 13-preset set, the drawer row set (no Language, no Remove Ads - they would be dead buttons), the exact Terms/Privacy wording, the 1.2s intro duration.
- Tests +5 (PresetsSettingsTest): list shows grouped verified entries; search hides non-matches; data integrity (13 entries, all status=verified, https sources, 2026-10-30); Settings sections render + zero "Learnify Zone" left in the UI; real tap-to-apply flow. Screenshots v0200_phone_presets_list / _presets_search / _settings / _preset_applied.

## v0.19.0 (versionCode 66): Frame + Perspective tools in Text on Photo (his confirmed spec)
- His flow: he pointed at the 2 empty slots in the Text on Photo tool grid (next to Meme), asked for "One frame" and "presp" (the perspective tool he knows from his reference app), confirmed "Yes", then said "Add many frame". Parent spec: fill exactly those 2 slots.
- FRAME tile (CropSquare icon): a scrollable strip of 13 frame styles, drawn as live thumbnails by the SAME drawing code that renders the photo frame (what he sees is what exports). Styles: None, Line, Double, Rounded, Inset, Dashed, Corners, Neon, Gradient, Polaroid, Film, Stamp, Vignette. Colour styles get a 12-colour swatch row; every style gets a Thickness slider. The frame draws over the photo edges and UNDER the text/sticker layers, live in the preview, and bakes into the Processed image. VETOABLE PICKS: the exact style set (12 real styles), the colour palette, and Polaroid/Film/Stamp/Vignette being fixed-colour (white/black) styles without the swatch row - all easy to change or extend on his word.
- PERSPECTIVE tile (Transform icon): exactly his reference app - two sliders, Vertical and Horizontal (-100..100, mapping to about +-38 degrees of tilt), live preview, tap the value label to snap it back to centre, plus a Reset perspective button. The tilt applies to the photo only; text and stickers stay put. Non-destructive until Process.
- Reset (his v0.18.3 definition) wipes frame + perspective too, and Process exports exactly what the preview shows (same drawFrame / perspectiveMatrix code path in preview and export).
- Tests (real flows, pixels not vibes): grid shows the 2 new tiles; Frame sheet -> tap Polaroid -> white band rows appear in the rendered preview; Perspective sheet -> slider to 80 -> preview pixels visibly skew -> tap the value label -> photo straight again (screenshot diff < 2.5); unit checks for the matrix geometry (centre fixed, corners move), drawFrame border-only painting, and the export path. Screenshots v0190_phone_menu_tiles / _frame_none / _frame_polaroid / _persp_v0 / _persp_v80 / _persp_reset.

## v0.18.4 (versionCode 65): FIX - Compare shows the TRUE original (his report)
- His report (v0.18.3): "Brother compare button not working" - photo + one-tap Warm filter + tap Compare, nothing changed on screen.
- Root cause (present since v0.18.0, NOT caused by the v0.18.3 Reset fix): the compare view showed the photo WITH the filter still applied (only the sliders were removed). With a filter and untouched sliders, "edited" and "original" rendered the same image, so the toggle looked dead - and the "showing the original" caption was inaccurate whenever a filter was on.
- Fix: Compare now shows the true original photo - before filter AND sliders. Tapping Compare/Edited switches original <-> edited both ways.
- Regression test asserts PIXELS, not just the caption (the caption alone passed in v0.18.0 while the image was wrong): photo + Warm filter + Compare -> the two views must differ visibly; toggle back hides the original again. Screenshots v0184_phone_adjust_warm / _compare_original.

## v0.18.3 (versionCode 64): FIX - Reset now starts from zero with a new-image option (his report + his definition)
- His report (v0.18.2, both tools): "Reset button not working. And when I close and open again same things shows." Then: "When we click reset it should start from zero with new image option."
- Root causes: (1) Reset only zeroed slider/style values - it kept the loaded photos, so with values already at defaults (or in Compare view) a tap looked like nothing happened; (2) unlike every other tool, Collage Maker and Photo Adjustments did NOT wipe their state when leaving to Home, so the old session was still there on reopen.
- Photo Adjustments: Reset now wipes everything - photo, all 12 sliders, filter, compare, save settings, undo/redo - and shows "Choose a photo" again.
- Collage Maker: Reset now wipes everything - photos, layout edits, canvas, background, frames, spacing, undo/redo - and returns to the collage home with "Add your photos (0/20)". A project saved with "Save project" is untouched (explicit save stays explicit).
- Leaving either tool with the back arrow now also wipes the session (same rule as Convert, Print, Compress and Text on Photo already had).
- Tests +3 (real flows, per his rule): adjust - set Brightness to 80 via the slider's own semantics action, Reset, verify zeroed + "Choose a photo"; collage - set spacing to 24, Reset, verify "Add your photos (0/20)"; collage - back to Home, reopen, verify no photos carried over. Screenshots v0183_phone_adjust_set80 / _reset, v0183_phone_collage_spacing24 / _reset, v0183_phone_collage_reopen_fresh.

## v0.18.2 (versionCode 63): Audit fixes round 2 - dead editor chips hidden (parent decision)
- Settings version line now reads from the build itself (BuildConfig), so it can never go stale again.
- The editor tool row no longer shows "Presets" (it navigated away mid-edit to the still-empty Presets tab) or "Format" (it only showed a "planned for a later stage" snackbar). Both return when they open something real. The tools row is now: Resize, Crop & Rotate, Background, Circle Cutter.
- Findings still deferred (cosmetic, all content reachable by scrolling): scroll hints on the tool chip row and the print size rows.

## v0.18.1 (versionCode 62): Audit fixes round 1 (his deep-review request)
- Settings tab now shows the real identity (name, version, and the Privacy paragraph all say A2Z Image Craft): "A2Z Image Craft", "Version 0.18.1 · Learnify Zone" (was the stale "PixelCraft Image Resizer / Version 0.3.0" + an old Stage 2C build note). New summary line lists the toolset and the privacy promise in plain words.
- Presets tab empty state: wording updated to the current app name, internal stage jargon removed. The list is still honestly empty (verification research not done) - unchanged behaviour.
- Home: removed the "Stage 2 ..." developer note at the bottom; the subtitle under the app title now reads "PHOTO TOOLKIT" (was "IMAGE RESIZER").
- Editor top bar now names the active tool (Crop & Rotate, Circle Cutter, Background, Presets, Format) instead of always saying "Resize Image".
- Crop & Rotate: the rotate/flip buttons are now compact enough that all four (Left 90, Right 90, Mirror, Flip V) fit a 360dp phone screen without clipping.
- Findings deliberately NOT changed yet (cosmetic, everything reachable): scroll hints on the editor tool chip row and print size rows; save folders stay Pictures/PixelCraft and Downloads/PixelCraft so already-saved files are not scattered.

## v0.18.0 (versionCode 61): Photo Adjustments (his spec) - new Home tile
- New Home tile **Photo Adjustments** (fills the last empty slot): one photo in, sliders out.
- **12 sliders in his 4 categories**, each -100..+100 with the value shown, min/max labels, live preview, fully independent: Light (Brightness, Exposure, Contrast, Highlights, Shadows), Colour (Saturation, Vibrance, Temperature, Tint), Details (Sharpness, Clarity), Creative (Fade). Tapping a slider's value resets just that slider (his "double-tap or reset option").
- **Undo / Redo / Compare / Reset** row (his mockup): undo is per slider-drag, Compare toggles between the original and the edited photo, Reset restores every slider and the filter.
- **Filters tile** reuses the app's existing filter engine inside this screen (Normal, B&W, Warm, Cool, Vintage, ...) - sliders still apply on top. Crop and Text on Photo already have their own full tools on Home; Draw/Shapes/Frames/Blur from his mockup are a proposed separate version (reported, not silently dropped).
- **Original quality kept**: the original photo is never touched; at save time the ORIGINAL is re-decoded (up to 4096px), adjusted ONCE and saved ONCE - no repeated compression (his spec 6.2/6.3). "Original: WxH" is shown under the preview (his spec 4.3).
- **Export**: JPG / PNG / WebP, quality 50-100 (default 90), Original size or Custom width, save to device (his spec 5). Fully offline, no watermark, no subscription, no new permissions.
- New engine `image/PhotoAdjust.kt`: tonal sliders compile into per-channel lookup tables (one fast pass), colour sliders run as a ColorMatrix, sharpness/clarity run an unsharp mask. Honest approximations: vibrance uses a gentler saturation curve (not true skin-protection); clarity uses a fixed small blur radius for local contrast.


## v0.17.1 (versionCode 60): Collage Maker part 2 (rest of his spec)
- **Swap photos between frames**: hold a photo and drop it on another frame to swap them (undoable).
- **Rotate / flip per photo**: with a photo selected - Rotate 90°, Flip H, Flip V, Reset photo.
- **Replace photo in its slot**: "Replace photo N" picks a new photo into that frame, keeping the layout.
- **Frames (borders)**: a frame slider + colour swatches around EVERY photo, plus a per-photo frame override ("same as all" by default).
- **Margins**: slider adds a background ring around the whole collage.
- **Backgrounds**: White / Black / Colour / Gradient (Sunset, Ocean, Mint, Berry, Dusk) / None (transparent - stays transparent in PNG and WebP; JPG saves on white).
- **View full screen**: opens the collage large, tap anywhere to close.
- **Save project / Open saved project**: the whole collage (photos by reference + every adjustment + export settings) saves on the phone and reopens later. If a photo is no longer available on the phone it is skipped with an honest message.
- Custom move/resize of individual sections is the one spec item left; it is a mini layout editor of its own (candidate for a later version - flagged, not silently dropped).
- Same engine, same single draw path, no new permissions.


## v0.17.0 (versionCode 59): Collage Maker upgrade (his 10-section spec)
- Collage Maker rebuilt as a 3-step flow: **1) Home** (layout style cards with live mini previews: Grid / Mixed / Vertical / Horizontal; photo strip with thumbnails, count "N/20", select / move left / move right / remove / add more; Start Creating), **2) Workspace** (Undo / Redo / Reset; live preview; tap a photo to select it, drag to move it inside its frame, zoom slider 1-4x per photo; spacing slider 0-40; rounded corners slider 0-60; canvas size Square 1080 / Portrait 1080x1350 / Story 1080x1920 / Wide 1920x1080; background White / Black / Colour with 7 colour swatches; Continue to Export), **3) Export** (JPG / PNG / WebP, quality slider 50-100 (JPG/WebP), resolution Standard / High 2x (capped 4096) / Custom width, live "Will save: WxH format" summary, Save Collage).
- **2 to 20 photos** (was 2-9). Layout library: Grid with named shapes (2,3,4,6,8,9,12,16) + near-square fallback; Mixed with hand-set asymmetric templates for 2-6 and a featured 2x2 grid for 7-20; Vertical (rows); Horizontal (columns).
- New engine `image/CollageMaker.kt`: ONE draw path feeds the preview and the saved file (what he sees is what he saves). Photos keep their proportions - sections are filled with a centre-crop, never stretched; zoom only crops in more; pan is clamped so background can never bleed into a frame, even with rotation.
- Memory: editing bitmaps are capped at 1024px; at export the ORIGINALS are re-decoded one section at a time (sampled, capped near the section's pixel size) so a 20-photo 4096px export does not hold all originals in memory (his spec 9.3/9.4).
- Home tile subtitle updated: "Combine 2-20 photos".
- No new permissions, fully on-device, no watermark, free. (His spec 9.6-9.10.)
- Deferred to v0.17.1 (told through Instinct): drag & drop between sections, rotation/flip buttons, per-photo borders, gradient/transparent backgrounds, margins, replace-in-section, full-screen preview, save project, custom move/resize of sections. The engine already supports rotation/flip/gradients/transparent/per-photo borders.
- (Vetoable picks: canvas presets named Square/Portrait/Story/Wide; colour palette of 7 swatches; grid-3 stacks vertically on a square canvas; quality slider 50-100 default 90.)


## Current stage
Stage 3 v0.10.1 (built on Stage 2E v0.8.1): queue overview screen, the full "Select new dimensions" Process sheet, batch processing of the whole queue with live percent progress, a result screen with savings summary, and batch save (new copies or replace originals). Stage 2C delivered: "My Photos" library screen, export format picker (JPG/PNG/WEBP/PDF), size unit picker (pixels/inch/cm/mm), queue thumbnail strip, and a persistent debug signing key. Stage 2 delivered: process-then-export workflow, aspect-ratio adaptation, camera capture, multi-select (up to 50) with one-by-one queue, recently edited photos, quality control.

## Stage 2C: implemented in source
- New bottom-nav tab "My Photos" with three internal tabs, all zero-permission and on-device:
  - Recent: grid of photos opened in PixelCraft (persisted picker grants; same data as the Home row).
  - Saved: the app's own exports from Pictures/PixelCraft (an app can always see files it created; needs Android 10+), with tap-to-open as a new editing source and a share button.
  - Drafts: edit sessions AUTO-SAVED when leaving the editor without exporting (photo reference + target size + aspect lock + quality). Tapping a draft reopens the editor exactly where the user left off; processing is one tap on Process again. Drafts are removed automatically when that photo is exported. Camera captures are not drafted (their cache files are temporary).
- Choosing NEW photos still goes through the system photo picker (the zero-permission "Original/Albums" equivalent). An in-app full-gallery tab would require the photo-library permission the user declined.

## v0.3.0 additions (user-requested after testing Stage 2 on his phone)
- Export format picker on the Export screen: "Original" (same as the source photo, the default), JPG, PNG, WEBP and PDF. The picker applies to new-file exports and sharing; "Replace original" always keeps the original file format. PDF export creates a single-page PDF (Android PdfDocument) saved to Download/PixelCraft because Android's gallery only accepts image types. GIF was deliberately NOT included: GIF supports only 256 colours, so photos visibly degrade, and a single-frame GIF is not animated - it offers nothing JPG does not do better. (Vetoable.)
- Size unit picker in the Resize tool: Pixels / Inches / cm / mm chips above the width and height fields. For non-pixel units a DPI field appears (default 300, the print standard, editable) and the same DPI math as the print-size calculator converts to pixels; all processing stays in pixels and aspect-ratio lock keeps working in every unit. (Vetoable.)
- Queue thumbnail strip: in multi-photo mode the editor now shows a horizontal strip of all selected photos under the "Photo X of N" label, with the current photo highlighted; tapping a thumbnail jumps straight to that photo. (Vetoable.)
- Persistent debug signing key: the repo now carries app/debug.keystore (standard public debug credentials) wired into app/build.gradle.kts, so every CI build signs the debug APK with the SAME key and each new APK updates cleanly over the previous install. Note: APKs built before this change used a throwaway key, so installing v0.3.0 over the Stage 2 APK needs one final uninstall+reinstall; every version after that updates in place.

## v0.4.0 additions (user-requested, from his reference-app screenshots)
- Queue OVERVIEW screen: after picking 2+ photos the app lands on "Photos to resize" instead of the editor (a single pick still goes straight to the editor). Summary card with photo count and total size, 3-column grid of all photos with dimensions and file size under each thumbnail, and a bottom action bar: Resize / Convert / Rename active, Save greyed until photos are processed. Tapping a photo opens its editor at that queue position. (Vetoable.)
- PROCESS now opens a "Select new dimensions" bottom sheet on the same page (no separate screen), with collapsible sections and ONE radio selection across all of them: Last used (one tap to reuse the previous size, remembered on the phone), Easy resize (Small/Medium/Large with real estimated file sizes computed on-device), Percentage (25/30/40/50/60/75/80/90% with computed dimensions + an editable Custom % row), Resolution (preset pixel sizes scaled from the photo's own ratio + a Custom row with width/height fields that keep the px/in/cm/mm unit chips and DPI field), and File Size (50 kB to 2.5 MB targets). The big bottom button names the choice ("Resize to Medium", "Shrink to 500 kB"). The editor's width/height fields stay available for fine-tuning. The quick-percent chips and the old custom-percent field were removed from the editor because the sheet covers them. (Vetoable.)
- Target FILE SIZE resize: iterates a quality ladder at the current dimensions, then steps dimensions down, until the encoded file fits the target; shows the resulting dimensions and size. PNG is lossless, so for PNG only dimension steps apply. If even the smallest attempt misses the target, the smallest achieved file is kept. (Vetoable.)
- BATCH processing (user confirmed "yes together"): picking an option and tapping the button resizes ALL queued photos one after another with a blocking progress dialog ("Processing 3/6 - 45%", real progress bar). Percentage and easy options keep each photo's own shape; fixed pixel sizes fit each photo with blurred padding when the shape differs. This replaces the earlier "one by one" queue note.
- RESULT screen after batch processing: green success card ("6 photos have been resized" + "4.4 MB -> 782 kB | Saved 3.6 MB (83%)"), a grid of the resized photos (real resized previews; new dimensions; new size in bold with the old size struck through), and the same bottom bar with Save now ACTIVE.
- Batch SAVE with two options: "Save as new files" (writes resized copies to Pictures/PixelCraft, one progress dialog, name pattern or automatic names, chosen format applies) and "Replace originals" (one system confirmation dialog for the whole queue via MediaStore createWriteRequest, then each original is overwritten in its OWN original format; Android 11+ only). No ads anywhere (the reference app's banner is not replicated).
- Single-photo flow is unchanged apart from the new sheet: Process > sheet > resize applied > Export screen.

## v0.4.1 fix (user-reported crash on his phone)
- Bottom bar buttons on the overview and result screens are now sky blue (user request), white icon + label, full labels readable; the inactive Save keeps the pale style.
- Fixed truncated bottom-bar labels on the queue overview and result screens ("Conv..." / "Renam..."): the bar now uses the reference layout - icon on top, full label below - so Convert / Rename / Resize / Save always render completely.
- Fixed "Recently edited" placeholders on Home: a small thumbnail is now cached on the phone the moment a photo is opened, so the row always shows real photos, even in a later session. (Photos opened before this fix may still show a placeholder if their picker grant expired; new opens always cache.)
- Fixed a crash when saving a large batch (8 photos): resized bitmaps were all held in memory until Save, which could exceed the app's heap and close the app. Resized photos are now written to a private on-device cache file one by one during processing and decoded only for the single photo being viewed or saved; each save re-encodes from that lossless cached copy and frees it immediately. Batch cache files are cleaned up when the queue is cleared. (Also: "Save as new files" with format "Original" now keeps EACH photo's own original format instead of one format for the whole queue, and the Replace-originals permission path is guarded so a system error shows a message instead of closing the app.)

## v0.4.2 fixes (user-reported on v0.4.1)
- Save dialog layout: "Cancel" was squeezed onto the same row as "Replace originals"; all three actions (Save as new files / Replace originals / Cancel) are now stacked full-width.
- Result screen now has a way home: the back arrow returns to Home and a "Done" action sits in the top bar.
- (No code change, packaging note: after uploading a zip, TWO Android builds run - one on the zip-upload commit with the OLD code, one after Bootstrap with the NEW code. Always install the APK from the build that runs after "Bootstrap project" finishes.)

## v0.4.3 change
- Replace-originals option removed from the batch Save dialog: queue photos come from the system photo picker (read-only URIs), where in-place replace is unreliable; the dialog now goes straight to "Save as new files" + Cancel. The replace code path stays in the app, dormant, for the parked full-gallery feature (custom picker with photo-library permission) where replace becomes real.

## v0.4.4 fixes (user-reported on v0.4.2: "Recent image, saved image not showing")
- Thumbnails are now cached for EVERY photo that goes through PixelCraft, not only photos opened one-by-one in the editor: picking photos for a batch remembers them (with their picture), finishing batch processing stores the EDITED image as each photo's thumbnail, and every saved file gets its thumbnail cached at save time. Home > Recently edited and My Photos > Recent therefore always show real photos after any edit or batch run. (Vetoable.)
- My Photos > Saved rows now load real thumbnails through Android's MediaStore thumbnailer (needs no permission for files the app itself wrote), with the app's own cache as fallback, instead of only trying a raw decode of the file.
- Self-cleaning recents: an entry whose source can no longer be read (expired picker grant or deleted photo) AND that has no cached thumbnail is dropped from the list instead of showing a permanent grey placeholder. This also clears the placeholders left over from versions before thumbnails were cached. (Vetoable.)


## v0.4.5 fixes (user-reported on v0.4.4: Recent/Recently edited still placeholders; no way home from result screen)
- Recent tab + Recently edited now load thumbnails in three layers: the app's own cached copy first, then Android's MediaStore thumbnailer (the same system path that made the Saved tab work), then a raw decode as last resort. Thumbnails are also cached from the bitmap that just decoded successfully at pick/open time, so the tile never depends on reading the picker URI a second time. Each photo loads in its own coroutine, so one slow source can no longer block the other tiles or the cleanup of dead entries.
- Result screen ("Resized photos") now has the top bar that v0.4.2 intended: back arrow and Done both return Home. (The bar's contents were written in v0.4.2 but the condition that shows the bar left this screen out.)


## v0.4.6 changes (user-requested on v0.4.5)
- Size is chosen in ONE place only (user decision): the editor page no longer shows width/height fields, unit chips, DPI or the aspect-lock checkbox - its Resize tab now shows a short hint ("Tap Process to choose the new size") and the Process button opens the options sheet, which keeps every sizing option (quick sizes, percentage + custom %, exact pixels + custom, target file size). Nothing about processing itself changed. (Vetoable.)
- Back navigation audit: every pushed page now has the top bar with a working back arrow - the final "Saved" page was the last one missing it (back arrow + title "Saved"; its on-page Done button still returns Home). Home / My Photos / Presets / Settings are root tabs with the bottom bar and need no back arrow.


## v0.4.7 refinement (user-requested on v0.4.6)
- Unit chips (Pixels / Inches / cm / mm) are back on the editor page, without the width/height fields: the chosen unit (and DPI, shown for inch/cm/mm) now carries into the options sheet - its Resolution Custom row and DPI field start on the editor's choice. Size values themselves are still chosen only in the sheet. Aspect-lock stays off the editor page: it only mattered for linked width/height typing, which no longer exists there.


## v0.4.8 fixes (user-reported on v0.4.7)
- Unit carry-over now works EVERY time: the options sheet starts on the editor's current unit at each open (the carried-in value was previously remembered from the first open, so changing pixels > inches > cm on the editor never reached the sheet).
- The DPI field on the editor page now shows under ALL four units, including Pixels (user request), not only inch/cm/mm.


## v0.4.9 fix (user-reported on v0.4.8: sheet still shows pixels after choosing inch/cm)
- The options sheet now DISPLAYS every dimension in the unit chosen on the editor page: the Resolution subtitle and presets, Percentage computed sizes, Last used, the Custom summary and the big action button all convert (e.g. choosing Inches shows "1.6 × 2.4 in" instead of "480 × 719 px", using the editor's DPI). Processing still works in pixels internally; only the labels convert. Root cause of the v0.4.7/0.4.8 miss: only the collapsed Custom row's fields followed the unit - everything visible stayed hardcoded in pixels.


## v0.5.0 fix (user-reported on v0.4.9: applying a size just returns to the editor)
- Single-photo flow now goes straight to the result page after processing: picking a size in the options sheet (or a file-size target, or an aspect-ratio choice) lands on the Export page with the processed preview, the result dimensions, original-vs-new size comparison and all save/share options - instead of silently closing the sheet back onto the editor. The editor is still one back-arrow away for adjustments.




## v0.16.7 - Corner handles, eyedropper, flip & clone (his "Use all" - 3 approved ideas)

- CORNER HANDLES on the selected layer, pro-app style: a small white X at the top-left corner deletes the layer (one tap, undoable), and an orange handle at the bottom-right corner resizes + rotates with ONE finger (drag away/toward = bigger/smaller, drag around = rotate). Works for text and image layers, on top of the existing pinch/drag gestures.
- PICK COLOUR (eyedropper): new Create-menu tab. Choose the target (Text colour / Background), tap "Start picking", tap any spot on the photo - that exact colour goes to the selected text layer. Picking mode exits after one successful pick or when leaving the tab.
- FLIP & CLONE: new Create-menu tab. "Flip Horizontal" mirrors the selected text or image layer (mirroring is applied in the draw pipeline, so preview AND export match). "Duplicate Layer" clones the selected layer with a small offset and selects the clone.
- Create grid is now 8 tiles: Add Text, Add Image, Image Size, Layers, Filters, Process, Pick Colour, Flip & Clone.
- Correction inside the tap hit-test: draw order is image layers UNDER text (verified in drawAll), so text is hit-tested first again (v0.16.6 briefly had images first). Topmost layer always wins, logo still tappable wherever no text bounds cover it.
- Tests: 121 total / 0 failures (flipHorizontalMirrorsInkAroundLayerCentre, phoneWindowCornerHandles, phoneWindowFlipClone with a real Duplicate click, phoneWindowPickColour + the v0.16.6 set).

## v0.16.6 - Menu button colour + Position-mode zoom slider (his two "small things")

- Menu back button is now ORANGE with WHITE writing (his exact ask: "Menu button make orange colour with white writing").
- Position mode on Text on Photo now works EXACTLY like the Social Media Position mode (his "Zooming fix properly", "I can't choose properly in main image", "Make same like you make in social media section", "Exactly 100% same"): a visible Zoom slider (1x-5x) under the photo, drag on the photo to position it, and the same hint card - "Drag your photo to position it. Use the slider to zoom in or out." Pinch-to-zoom still works and the slider follows the fingers; panning is never lost when the slider moves. Zoom resets to 1x when Position is opened, when a size preset is picked, and on Reset/Original.
- Regression guard: TextPhotoPhoneSizeTest.phoneWindowPositionMode opens Position mode on a real phone window and asserts the Zoom slider + exact hint text render (screenshot v0166_phone_position).
- Tap-to-select fixed (his "I can't choose properly in main image"): the TOP layer now always wins - image layers (his logo) draw above text but were hit-tested AFTER text, so a tap grabbed the text underneath; image layers are now checked first, and within each kind the topmost (last drawn) layer is picked.
- More room for the photo (his "If possible increase little more space for image edit"): the bottom sheet now takes 38% of the screen instead of 42%, the photo name/size card is slimmer, and the hint line under the preview is smaller. Everything stays reachable; the photo gets noticeably more vertical space.
- Save flow wording (his "First save button there, when i click then coming process again" -> his decision "Better change save to process"): the last Create-menu tile is now "Process" (was "Save"), the level is titled "Process & Save", and the action button always reads "Process" - the confusing "Process again" is gone (edits invalidate the result, so Process is always the one first step). Flow reads: tap Process -> result ready -> "Save to Gallery". The hint "Nothing is saved until you tap Save. Your original photo is never replaced." stays.

## v0.16.5 - His three v0.16.4 errors + Menu button (live phone feedback)

- Preview too small (his "When I select image it is very small"): the sheet layout squeezed the preview to ~200dp on a real phone, so a square photo rendered as a tiny centered square. Fixed chrome: decorative subtitle hidden once a photo is loaded, slimmer photo card, Position moved into the sheet header (with Original beside it), sheet height 340 -> 300dp, undo/redo compact in the header. The preview now gets the real remaining space and the photo fills its width. Regression guard: new TextPhotoPhoneSizeTest renders at a REAL phone window (w360dp-h640dp) - the tall review window had masked this.
- Text background not working on curved text (his screenshot: Black selected, nothing drawn): drawCurved checked bgColorArgb but never painted it (pre-existing engine bug, straight text was fine). Curved text now draws a proper ribbon background that follows the arc behind the glyphs. Regression test: curvedTextBackgroundDrawsRibbon (pixel-count assertion).
- Process greyed out with no layers (his "When I click to save option it show like this"): was enabled only with text layers. Now enabled whenever a photo is loaded - zoom/crop + save without adding text is a real flow.
- Menu back control is now a bold, proper button (his '"Menu" make more bold with button').
- FILTERS (his "Its possible to add Filter" - placed in the Create menu next to Save, Save moved to last): new Filters level with 8 live thumbnail previews of HIS photo (Normal, B&W, Warm, Cool, Vintage, Bright, Fade, Vivid). ColorMatrix-based - instant live preview, composes with crop/position/layers, baked into the export. Filter change invalidates the processed result; Reset clears back to Normal.
- Pinch-zoom in the Position/crop flow confirmed intact (his "Dont forget to zoom option when crop image") - gesture code untouched by the preview resize.

## v0.16.4 - Text on Photo bottom-sheet redesign (his design direction: "we dont need to scroll up and down many times")

- Bottom sheet editor (his core ask): the long scrolling editing screen is gone when a photo is loaded. The photo stays on screen ALWAYS (his "easy to look live edit") and all controls live in a fixed bottom sheet with two-level navigation, exactly like his reference app but in our light style: root menu (Add Text, Add Image, Image Size, Layers, Save) -> tap one to open its options -> Back arrow returns to the menu. Undo/Redo sit in the sheet header.
- + Text opens styling instantly (his flow): adding or tapping a text layer jumps straight to its options with the 3 essentials together at the top - font size, font colour (now an always-visible 18-swatch grid, no "More colours" hunting for text colour), and background colour. Fonts still show in their real typeface (v0.16.3).
- Tapping an image layer opens its options (size/rotation/opacity/delete) the same way.
- Image Size, Layers (reorder/delete) and Save each live behind their menu entry; nothing was removed, everything is one tap from the menu instead of a long scroll.
- NOT in this version (next candidates, his screenshots): corner bubbles on the selected layer (X delete, resize/rotate handle), eyedropper colour pick from the photo, flip/clone layer actions, Decorators/Sticker/Emoji entries.

## v0.16.3 - Text on Photo: Position always available, Reset button, add-image hardening (his feedback)

- Position for everyone (his "I cant zoom main image. I want to zoom and crop."): the Position button only appeared AFTER picking a size, so on an untouched photo there was no way to zoom/crop. Now a sky-blue Position button sits directly under the preview and works immediately - it starts from the full photo frame, drag pans freely, pinch zooms, Done keeps the region (label "Custom"; tapping Done without moving anything clears back to Original). Picking a ratio first still works as before. Free-form (non-uniform) crop shapes are NOT in this version - the frame keeps the current canvas aspect (vetoable).
- Reset button (his "Add reset button."): next to Change in the photo card - clears all text + image layers, crop, undo stack and results while KEEPING the loaded photo. The original photo on his phone is never touched.
- Font picker shows the real thing (his design screenshots): every font tile now renders its name in that font's actual typeface, so he can see what each font looks like before tapping.
- Add image layer hardened (his "tried to add image through layer for logo not added", on v0.16.2): the overlay decoder now falls back to Android's ImageDecoder when the standard decoder can't read the picked file (HEIC/WebP/AVIF sources), a failure says "Could not add that image - try a JPG or PNG." instead of failing quietly, a success snackbar confirms "Image layer added - drag it on the photo.", and the busy overlay can no longer stick behind a snackbar on either the photo-open or image-add path.

## v0.16.2 - Text on Photo fixes from his phone testing (Oct 29, 2026)

He tested v0.16.1 on his phone and sent screenshots. Three fixes:

- Image layer gestures FIXED (his "I can't edit and move layer image"): the preview's gesture handler only re-reads its captured inputs when the photo or crop mode changes, so the image-layer selection was invisible to it - drags fell through to the text path, and with no text layer selected nothing moved. Preview transforms now route through the app, which reads the CURRENT selection at gesture time: image layers drag, pinch-resize and twist-rotate exactly like text layers. Double-tap on an image layer selects it without opening the text editor.
- Clean photo open FIXED (his "When I open image already 'your text' coming over image. No need to come."): opening a photo no longer drops a default "Your text" layer on it. The photo starts with zero layers; a text layer is created only when he taps + Text (or when a saved style is applied and needs one).
- Curve Text control FIXED (his "Curve text button fix all text in one line... Arc in second line... It takes more space"): the long two-line pill button is gone - Curve Text is now a compact one-line toggle row (same Switch style as Shadow), and the Arc slider sits on its own line below when ON.
- Squished "More" buttons FIXED (his screenshots): the font, image-size, text-colour and background-colour rows squeezed the More button into a one-letter-per-line vertical strip. All four rows now scroll horizontally, so quick choices keep their natural size and the More button stays a normal readable button.
- Text background gap FIXED (his "I can't find background colour"): that squeezed More colours card was silently inflating the row height, leaving the chips floating in the middle with big empty space above and below. With the row scrolling, the chips (None/White/Black/Blue/Gold + More colours) sit directly under the label again.

## v0.16.1 - Image-size cutter inside Text on Photo (his spec)
- New "Image Size" section right after Image Preview (before Add Text, so the canvas shape is set before styling): 4 common sizes up front - Square 1:1, Portrait 4:5, Story 9:16, Wide 16:9 - each labelled with name AND ratio like the app's other pickers, plus a 5th "More sizes" button revealing the full list (adds Classic 3:2, Tall 2:3, Screen 5:4, Standard 4:3, Upright 3:4, A4 210:297, plus an "Original - no crop" chip) and a Custom size entry (two whole-number fields W:H, 1..99, with a gentle inline error - "Enter whole numbers from 1 to 99, like 5 and 7.").
- Choosing a size shows the crop frame over the WHOLE photo (darkened outside, sky-blue frame - the app's Crop & Rotate look) and opens the POSITION step (his follow-up: same as the Social Media Sizes Position button - the button is even named "Position"): drag pans FREELY in any direction (no snapping, clamped only at the photo edges), pinch zooms the frame in/out around its centre (5% min-size guard, never leaves the photo), "Done" confirms, "Position" reopens, "Original size" clears. While adjusting, text gestures are paused so the two never fight; the scroll-lock covers frame drags too.
- Layout per his follow-up ("easy to seen live working"): a "Quick edits" card sits DIRECTLY under the image preview holding the live controls - font size, Curve Text + arc, text background, shadow, opacity (controls text AND text-background alpha - one slider), plus the new letter-spacing and line-spacing sliders. Text Styling below keeps the deeper options: quick styles, fonts, bold/italic, alignment, colours, outline. Grouped in clean labelled cards, app light style ("make easy to use and professional").
- Text spacing (my delegated pick, he approved "all 3"): letter-spacing (0-30% em) and line-spacing (0-100%) sliders in Quick edits; the engine applies them in Paint.letterSpacing and line-height math for BOTH straight and curved multiline text.
- Save as Preset (delegated pick): "Save current style" in Text Styling saves the full style (font, size, weight, align, colour, outline, background, shadow, opacity, curve + arc, both spacings) with a name; saved styles appear as tappable chips in the tool AND in a new "My text styles" section in the Presets tab (tap = open Text on Photo with the style applied to the current/new layer; if no photo is loaded it applies as soon as one is picked; delete from the Presets tab). Fully on-device (SharedPreferences JSON), capped at 40.
- Add Image layer (his follow-up: "add image as well as a layer... like logo... but it should editable"): an "Image" button in Add Layers places a SECOND photo on top of the main one as its own layer - selectable by tap, draggable, pinch-resizable, twist-rotatable on the preview, with Size/Rotation/Opacity sliders and Delete in a quick card under the preview, reorder arrows in the layer list, and a dashed selection frame. Nothing is flattened until export: overlay layers render into the saved photo at full cropped resolution, UNDER the text layers so captions stay readable. Overlay images are EXIF-corrected and capped at 2048px (memory safety). Image-layer changes are not part of the text undo stack (text undo keeps covering text only - vetoable).
- Colour sections per his follow-ups: the text-colour 6-dot quick palette stays, plus a "More colours" button revealing the 16-colour extended palette; the text-background section gets the same treatment (5 quick choices incl. None + its own "More colours" button with the full palette). Same expand pattern as More fonts.
- Font picker per his follow-up: only the 4 core fonts show up front (Sans, Serif, Mono, Script) plus a "More fonts" button revealing the full list (Condensed, Light, Black, Casual, Small Caps - all real Android generic families, bold/italic still apply on top).
- Text layers live in canvas coordinates (fractions of the cropped canvas), so after cropping the preview shows exactly the cropped canvas and the export is at the chosen aspect at full cropped resolution. The result card shows the crop label. Crop changes are not part of the layer undo stack (like the app's other crop tools). New unit tests pin the ratio math (centred max-fit rects incl. exact-fit edge case), pixel sizes, custom-ratio validation, and the option list matching his spec (4 commons, names+ratios, A4 present).

## v0.16.0 - NEW TOOL: Text on Photo (his spec + mockup; Batch Resize tile removed)
- Home grid: the "Batch Resize" placeholder tile is gone; "Text on Photo" ("Add text to images") sits in its exact spot. Header subtitle from his mockup: "Add words. Create something beautiful." Screen order exactly per his spec: Header -> Select Photo -> Image Preview -> Add Text -> Text Styling -> Position and Layers -> Save Photo.
- HIS #1 PRIORITY - live editing preview: the preview is a real-time canvas that repaints on every change. It and the export share ONE rendering path (TextOverlayUtils.drawAll in image pixel space; the preview only applies the fit-transform), so what he sees on the photo is pixel-identical to the saved file - they can never drift (same lesson as the v0.15.10 grey fix).
- All 8 of his points: (1) any text incl. multiple lines; (2) font family (Sans/Serif/Mono/Script), size slider (A-/A+), bold, italic, left/centre/right alignment; (3) colour (his 6-dot quick palette: white, black, blue, gold, red/pink, green), outline (strength slider + black/white/navy colour), text background box (none/white/black/blue/gold), shadow switch, opacity slider; (4) touch gestures on the preview: drag to move, pinch to resize, twist to rotate, double-tap opens the text editor; page scroll is disabled while a finger is on the preview so single-finger drags always move the text; (5) multiple layers with add, tap-to-select (tap the text on the photo or its row), reorder (later = on top) and delete; (6) undo/redo (snapshot stack, capped at 60; gesture drags and typing coalesce into ~1-1.5s windows so the stack stays meaningful); (7) export PNG (lossless) or JPG (quality 95) at the photo's full resolution; (8) the original is never replaced - output saves as a new file (text-on-photo-<timestamp>) and the flow is convert-then-confirm: Process renders and shows the result summary (name, dimensions, real byte size, layer count), nothing is written until he taps "Save to Gallery".
- Quick Text Styles presets from his mockup: Classic (clean white + soft shadow), Bold (bold + navy outline), Elegant (serif italic gold), Shadow (blue + strong shadow).
- Curve Text (his essential requirement): a dedicated Curve Text button inside the tool. Rendering is a genuine arc - android.graphics.Path.addArc + drawTextOnPath, NOT a rotated straight box. Radius derives from the measured text width so the text always spans the chosen arc (slider 60-320°); multiline curves stack on expanding radii; drag/resize/rotate/font/colour/outline/shadow/opacity all apply (outline uses a stroke pass under the fill); undo/redo/delete cover it; it exports in the final PNG/JPG. A unit test proves the curve is real: same text and size, the curved ink bounding box is clearly taller than straight, and the arc's centre ink rises above its ends.
- Memory discipline: photo decodes through the existing capped path (<=4096px, EXIF-corrected); the export bitmap is recycled right after encoding; only bytes are held. No new permissions; fully on-device.
- Engine lives in image/TextOverlayUtils.kt (pure android.graphics, no Compose) so it is fully unit-testable on the host: 16 new tests (94 total, 0 failures) covering text layout math, multiline, alignment, opacity blending, outline/background/shadow pixels, genuine-curve geometry, arc radius math, layer z-order, rotation bounds, undo/redo semantics + deep-copy snapshots, presets, the 6-colour palette, and export-block placement. Batch Resize note: only the placeholder tile was removed (it never launched anything); the real multi-photo resize flow via Select Photos is untouched.

## v0.15.10 - Light grey is now a real soft grey (his bug: "default and light grey same"; FINAL: "Keep but light grey should be soft grey")
- The old "Light grey" was #F2F2F2 - nearly identical to White, so switching looked like it did nothing. Now #E0E0E0 (Material grey 300): visibly soft grey, clearly distinguishable from White on a phone screen, still light. Both options, their order and the default are unchanged: "White / default" stays the default, "Light grey / soft grey" stays the alternative. The value is ONE shared constant (PrintUtils.LIGHT_GREY_BG) used by BOTH the print preview and the export rasterizer, so the two paths can never drift apart. New unit test proves: constant value, >= 0x20 channel distance from white, and a real rendered pixel of the exported bitmap is exactly that grey (white path still renders white).

## v0.15.9 - typed page entry removed entirely (his veto: "But no typing")
- The typed page-number entry is gone completely: no box, no underline, no placeholder, no hint, no keyboard, nothing tappable except Add. The row is plain "Select pages" text on the left and the Add button on the right, same line. Add applies the currently ✅-marked pages - the same action as the big "Use N selected pages" button (both stay, matching his mental model of Add = apply my marks); with nothing marked, Add shows a gentle "Tap pages below to select them first." Selection by ✅ marks on thumbnails is the one way to pick pages, and thumbnails now show clean - no "Page N" labels under them (his removal, confirmed by screenshot); the ✅ mark plus blue border on selected pages is the only overlay. Kept as he confirmed: "N pages selected: ..." banner, "Choose a page (N pages)", thumbnail page labels, "Use N selected pages", All pages mode. (The page-list parser code remains but is user-unreachable, covered by its unit test.)

## v0.15.8 text - final label casing (his pick)
- The entry label is "Select pages" (lowercase p), matching "Choose a page" above it.

## v0.15.7 layout - borderless one-line page entry (his spec)
- "Brother remove box": the page-entry field lost its rectangle outline - it is now a plain underline-style field. The label reads "Select Pages" (his latest wording), and the typing line with the "(e.g., 4, 9, 47, 83, 126, 229, …)" hint sits in ONE row with the Add button on the right; the hint never wraps (truncates with ellipsis instead). Add behavior, the selected-pages banner, the ✅ marks and everything else unchanged.

## v0.15.6 text - his exact label + hint for the page-entry box (his wording, his weights)
- The quick-entry area now reads exactly (his final wording, all regular weight - he reversed the bold): a "Select Page" label directly above the entry field, and inside the box the placeholder "(e.g., 4, 9, 47, 83, 126, 229, …)". The Add button and behavior are unchanged; the page count stays above the mode chips ("Choose a page (N pages)").

## v0.15.5 addition - multi-page selection in the PDF picker (his requests, his labels)
- "Selected page" mode is now a real multi-select: tapping thumbnails toggles selection (tap again to un-select), each selected page gets a ✅ mark over the thumbnail plus a blue highlight border, and a line under the picker shows BOTH the count and the actual pages ("3 pages selected: 5, 8, 12-20" - long lists truncate to 2 lines and expand on tap). Selection survives scrolling.
- For scattered pages in big documents (his example: 3, 23, 67, 207) there is a quick text entry accepting numbers and ranges ("3, 23, 67-70"); Add merges them into the same selection (thumbnails get marked too). Out-of-range or malformed entries get a gentle inline message naming the skipped tokens - no crash, valid entries still apply.
- Confirm button reads "Use page N" for one page (single-page flow identical to before) and "Use N selected pages" for many: all selected pages convert together in one run with the same settings (progress "page N of M", summary before Save, nothing written until Save). PDF export = one multi-page PDF with the selected pages in page order; JPG/PNG = one file per selected page named by the ACTUAL page number (name-page-005.jpg) so files sort correctly. Preview shows the first selected page with a note. "All pages" mode is unchanged and still covers select-everything.

## v0.15.4 additions - Reset on Print landing + "All pages" PDF printing (both his requests)
- The landing screen ("Select Your Image") had no Reset; it now has the same top-right Reset as the other tool screens. It clears the selected image/PDF, the chosen page and thumbnails, the temp PDF copy, and every print setting back to defaults, staying on the landing screen.
- The PDF page picker now offers his two choices: "Selected page" (tap a thumbnail, as before) or "All pages". With All pages, every page is converted in ONE run with the SAME settings/ratio: the export step composes pages one at a time (progress "page N of M", never holding all pages in memory) and produces all results together. PDF export makes ONE multi-page print-ready PDF at the chosen size/PPI; JPG/PNG export makes one file per page, named with zero-padded page numbers (name-page-001.jpg ...). The result screen shows the full summary (file count, total size, page size) BEFORE saving, and nothing is written to the phone until he taps Save to Phone - his confirm-save rule is unchanged. The preview shows page 1 with a note that all pages are exported. Share sends all files together. Multi-copy sheets combine too (one sheet per PDF page). The 500-page picker guard still applies (vetoable).

## v0.15.3 fix - PDF open path rebuilt (his "File descriptor not seekable" report + 200-page requirement)
- Root cause from the v0.15.1 -> v0.15.2 diff: v0.15.1 opened Android's PDF engine ONCE per file and worked; v0.15.2 opened it twice on the same descriptor (once to count pages, once to render) and the second open is what his phone rejected with "File descriptor not seekable". Back to one open per operation, and every operation works on a temp copy of the PDF in the app's cache - a real file is always seekable, so Downloads/cloud providers that hand out pipe descriptors work too. The temp file is deleted when the tool resets or another PDF is picked.
- The page picker is built for big documents now: opening a PDF only reads the page count (instant even for 200 pages), the picker lists every page right away with a loading placeholder, and each thumbnail renders on demand as it scrolls into view - pages are never all rendered up front. The chosen page renders singly at full resolution (up to 2600 px), so page 200 is as fast as page 1.
- The same temp-copy path now covers the Format Converter and Compress PDF inputs, which shared the old direct-descriptor open.
- Tested against a synthetic 200-page PDF (page count + rendering page 199 directly) and the temp-copy/cleanup path. The 500-page guard from v0.15.2 still applies (vetoable).

## v0.15.2 fix - px chip layout + all PDF pages
- The px unit chip was crushed (zero-width vertical wrap, same pattern as v0.14.1): unit and orientation chips now share the row width equally.
- PDF page picker showed only 12 pages; it now shows every page (thumbnails for the whole document, up to a 500-page guard) and renders just the chosen page at full resolution instead of re-rendering every page before it (fast even for page 200 of a planner).

## v0.15.1 additions - PDF input + pixels unit (both his approvals)
- "Select PDF" sits next to Choose Image on the landing page. Picking a PDF renders page thumbnails and asks him to choose a page; the chosen page renders at good resolution (up to 2600 px) via PdfRenderer and then flows through the whole tool exactly like an image (calculator, quality check, preview, export). Label shows "PDF · Page N". Device-only path (host JVM has no PDF engine) - same known caveat as the converter's PDF input.
- Pixels unit: Custom size offers px alongside in/cm/mm. In px mode the fields take pixels and the calculator works in reverse: physical size = pixels / PPI at the selected PPI, shown in inches and cm; required pixels equals what he entered; the quality check automatically compares the source image against the entered pixels; aspect lock and reset-to-proportions work in px too.

## v0.15.0 feature - Print Size & DPI (his GO "research deeply and make perfect" + full 3-batch spec)
- The old placeholder calculator became the real "Print Size & DPI" tool, four screens in the app's design language (his mockup's dark theme was layout-only, per his instruction):
  1. "Print Size & DPI" - subtitle "Prepare images for printing at the right size and resolution.", Select Your Image card; after picking, the image shows with preview, pixel dimensions, format and file size (his standing rule), "Your original image remains unchanged."
  2. "Print Setup" - grouped presets: Paper (ISO A2-A6, exact ISO 216 millimetres), Paper US (Letter, Legal, Tabloid), Photo (wallet 2.5x3.5, 4x6, 5x7, 8x10, 11x14, 16x20), ID / Passport (2x2 in US/India, 35x45 mm EU/UK/Schengen - both verified against travel.state.gov and gov.uk), Square (5x5, 8x8, 12x12), Posters (12x18, 18x24, 24x36), plus Custom (in/cm/mm fields, portrait/landscape, keep-aspect auto-calc both ways, 3 mm bleed toggle, reset-to-original-proportions). Ratio mismatch offers Fit / Fill & Crop / Stretch with a distortion warning on Stretch. PPI cards 150/200/300/600 with his exact descriptions, 300 badged Recommended, "Often called DPI" note. Print calculator shows all four of his values (print size, image pixels, effective PPI, required pixels + MP) with the honest "target calculation, not a claim" note. Quality banner: Good (green) / Acceptable (amber) / Low (red) with his exact texts. Reset in-tool.
  3. "Print Preview" - real page preview: white page with dark border, pink bleed band when bleed is on, margins inset, the actual image placed per Fit/Fill/Stretch, multi-copy sheets rendered as the real grid with grey cutting guides; placement/margins/bleed/crop-marks/background controls; the no-exact-physical-calibration disclaimer.
  4. "Export Print File" - format cards JPG/PNG/PDF with his exact texts, filename, JPEG quality slider, summary card (size, pixels/copies, PPI, options). "Create File" builds in memory and shows name + size + dimensions; nothing is written until "Save to Phone" (his ask-before-save rule); Share via the system sheet; Start Over.
- Multi-copy sheets (ID/passport + photo sizes): sheet choice A4 / 4x6 / 5x7, copy count 4/6/8 chips (only counts that fit are shown) or Max; live "N copies on SHEET" count; neat centered grid with even gaps and cutting guides; hidden when only 1 copy fits. Counts that only fit when copies touch (e.g. 4+ copies of 2x2 in on 4x6 - physically impossible with gaps) automatically use a tight touching layout with guides, like real passport sheets; the default for ID presets is 4 copies on 4x6 (his choice).
- Engine (image/PrintUtils.kt, host-testable): required pixels = inches x PPI; effective PPI = limiting-axis pixels/inch; quality thresholds (Good >= target, Acceptable >= 2/3 target and >= 120 PPI absolute floor, else Low - our chosen thresholds, vetoable); unit conversion; ISO/inch preset table; grid fit math; Fit/Fill/Stretch renderer; multi-copy sheet renderer with guides; 48 MP export cap with an honest "scaled to fit device memory" note (prevents OOM on poster sizes).
- Exports: JPG/PNG = full required pixel dimensions (upscale allowed - the quality check warns, never blocks; his decision), bleed adds 3 mm per side to the canvas, margins inset the image, PNG keeps transparency / JPG flattens on the chosen background. PDF = page at exact physical size in points with the image placed (plus margins, bleed, and real crop-mark lines at the trim corners). Originals never modified; images save to gallery, PDFs to Downloads/PixelCraft.
- Verified: requiredPixels matches his 10x8@300 = 3000x2400 (7.2 MP) example and the standard A4@300 = 2480x3508; unit round-trips; effective-PPI limiting axis; quality threshold boundaries; export cap keeps ratio; Fit letterboxes on background / Fill covers / Stretch distorts (real pixel assertions); placement rect centers with margins; grid fit counts incl. 4x6-with-2x2in reality; sheet render tiles with red-cell/white-gap/guide-pixel assertions; exact-count sheets skip extra cells; preset table exactness incl. passport sizes. UI tests cover the landing and setup screens. PDF byte path is device-only (host JVM has no PDF engine) - same known caveat as the converter.

## v0.14.1 fix - Compressor settings page with many images
- Layout bug he caught with 9 images selected: the header text ("9 images selected / Original size") had zero width beside the scrolling thumbnail strip, so it wrapped one character per line and inflated the card. Fixed: the selected-files card is now a simple top-to-bottom layout - summary line ("9 images selected" + total original size) on top, then one row per file with its thumbnail, name and size. Single-file behavior unchanged (name, original size, one thumbnail).

## v0.14.0 feature - Image & PDF Compressor (his GO "Make perfect" + his 4-page design spec)
- The "Compress" homepage tile is now a real tool. HIS design, three pages:
  1. Landing "Image Compressor" - subtitle "Reduce file size. Keep image quality.", big Select Images card ("Choose one image or multiple images", "JPG · PNG · WebP", thumbnails with content-sniffed format badges), "Select PDF to compress" button, and his two mode cards: Quality Compression / Target File Size.
  2. "Compression Settings" - file card showing every selected file with thumbnail, NAME and SIZE (his "it must show file and size"), Compression mode toggle (Quality / Target Size), quality slider 1-100% ("Smaller file" / "Higher quality"), KB input in Target mode, "Optional resize" toggle (90/75/50/25% dimension chips), blue Compress button. Reset in-tool.
  3. "Compression Results" - Original vs Compressed cards (compressed in blue), green-check "N% smaller" with the real calculated percentage, "Compare image quality" Before/After preview (tap-to-toggle + buttons) decoded from the ACTUAL compressed bytes, blue "Save Compressed Image". Batch shows a per-file summary (name, original → compressed, -N%) plus totals; nothing saves until he taps Save. PDF results show the same with a note when the PDF was already well-compressed (<5% saved).
- Engine (image/CompressUtils.kt): Quality mode encodes at the chosen quality (JPG/WebP); Target mode walks a quality ladder at full size first (best quality that fits wins), then steps dimensions down (floor 320px) until the file fits - PNG is lossless so it shrinks by dimensions only. Transparency preserved: PNG and WebP keep alpha (real pixel test); JPG flattens onto white. Output keeps the source format (JPG/PNG/WebP); other formats become JPG. Saves are always NEW files (pixelcraft_compressed...) - originals never touched.
- PDF compression: pages render via PdfRenderer, then Quality mode re-renders at the chosen resolution or Target mode picks the highest resolution that fits the target size; rebuilt with PdfDocument, saved to Downloads/PixelCraft.
- Verified with REAL OUTPUT tests (CompressUtilsTest): noisy 1200x900 JPEG hits a 60KB target and decodes back with dims inside the floor; files that fit keep full dimensions; PNG shrinks only via dimensions and keeps transparent pixels (alpha == 0 asserted); WebP keeps alpha; percent math (75%, never negative, unknown-original safe). PDF shrink ordering asserted where a PDF engine exists (host JVM has none - known signature accepted, same caveat as the converter). UI render tests cover landing + settings pages.

## v0.13.2 fix - Format Converter home card format labels (his fix #1: "it must show file format with image in this page")
- The Select Your Image card on the converter home page now puts the same content-sniffed format badge on every selected thumbnail, matching the conversion page badges.
- His fix #2: BATCH conversions no longer save automatically. Convert now builds all outputs in memory and lands on the Conversion Result page (like single-file): "Converted 6 images to PNG", totals "Original: 6 images (Mixed) · 7.2 MB" / "Converted: 6 PNG files · 3.1 MB", and NOTHING is written until he taps "Save N Images to Phone". Batch-to-PDF behaves the same ("Save PDF to Downloads", one multi-page PDF). Single-file flow unchanged.

## v0.13.1 fix+feature - Format Converter polish + PDF input (his screenshot feedback + "How to choose PDF?" + "Pdf to png/jpg" + "it must show accurate current format")
- Every selected image in the file card now carries its own format badge on the thumbnail, detected from the FILE CONTENT (magic bytes), not the extension - a .jpg that is really a PNG shows PNG. Detection covers JPG/PNG/GIF/BMP/WebP/TIFF/PDF/HEIC with MIME fallback (ConvertFormats.detectFormatBytes, fully unit-tested including the mislabeled-file case).
- The "To" box is tappable ("Tap to change") and opens a chooser with all 6 formats - target can be switched without going back to the picker. Top bar title follows the new target.
- PDF as INPUT: "Select PDF (pages become images)" button on the converter home card. Pages render on-device with PdfRenderer (max 20 pages, 3000px cap, flattened onto white), he picks the pages he wants in a dialog with thumbnails, and the chosen pages convert like normal photos - one output per page, or pages into a new PDF. Badge shows "PDF p.N". From box shows "PDF".
- Honest verification note: PDF rendering (in and out) uses Android's real PdfRenderer/PdfDocument, which need the native engine that only exists on a device - the host test JVM cannot run them. Unit tests verify everything around them: content sniffing (real PNG bytes), the hand-written 2-page PDF parses, page geometry, and the exact known-signature fallback. Pixel assertions for rendered PDF pages run wherever the native engine exists. Worth one device tap: PDF > PNG, and PNG > PDF > open it.

## v0.13.0 feature - Format Converter (his GO: "Format Converter", "Any format to any format", "Research deeply and make professional dear brother")
- The placeholder "Convert Format" homepage tile is now a real tool, renamed "Format Converter" (his name). Subtitle "Convert images into the format you need." + Select Your Image card ("Choose one or multiple images", selected images show as thumbnails) + a "Convert to" grid of 6 tiles: JPG/JPEG, PNG, WebP, PDF, BMP, TIFF.
- ANY input the phone can decode (JPG, PNG, WebP, HEIC, GIF, BMP, TIFF...) converts to any of the 6 outputs. Conversion page: file card with thumbnails + green checks + "Original format: PNG", From > To boxes, Conversion options (quality slider 10-100%, default 90, for JPG/WebP; solid background White/Custom Color for JPG), big Convert button. Reset in-tool.
- Result shows original and converted file sizes (his requirement) with a preview, Save to Phone, Convert Another. Batch: up to 20 images converted in one go with a progress dialog; PDF batch = one multi-page PDF saved to Downloads/PixelCraft, other formats save one file per image to Pictures/PixelCraft with a size summary.
- REAL encoders, no renamed files (image/ConvertFormats.kt): JPEG/PNG/WebP via Bitmap.compress; BMP written as a real 24-bit BI_RGB bitmap file (transparency flattened onto the chosen background); TIFF written as a valid little-endian baseline TIFF, uncompressed RGB, or RGBA with an ExtraSamples tag when the source has transparency (alpha preserved); PDF via PdfDocument, one page per image at the image's own size. Fully on-device, no new permissions.
- Verified with REAL OUTPUT tests (ConvertFormatsTest): every produced file is parsed back - JPEG/PNG/WebP/BMP decoded with BitmapFactory (dims + pixel colors, JPG transparency flattening onto white, PNG alpha preserved), TIFF parsed byte-by-byte (magic, IFD tags, strip bytes, RGB and RGBA+ExtraSamples layouts, first-pixel colors), PDF re-rendered through PdfRenderer page by page (page count + rendered colors). UI render test covers the picker page (all 6 format tiles).
- Bugfix folded in: collage photo list now survives recomposition (it was created without remember, so changing the collage layout chip could drop the picked photos).

## v0.12.0 feature - Online Shop Sizes (his GO: "This is my plan", "Deeply research. Accurate size of all platforms")
- New tool on a new homepage tile "Online Shop Sizes", right next to Social Media Sizes (his placement). Same 3-page flow and engine as Social Media Sizes.
- Platform picker: "Online Shop Sizes / Every shop. Every product image." + Select Your Image card (with the photo thumbnail, v0.11.1 pattern) + 6 tiles: Etsy, Shopify, Amazon, eBay, Gumroad, Other Shop (his design: Payhip, WooCommerce, Wix Stores, Squarespace, Ko-fi Shop and Creative Market live inside Other Shop). No "More" tile.
- Per-platform categories: Product Image, Listing Gallery, Shop Banner, Shop Logo & Icon, Promotional Image, Product Collage, Custom Size - only the categories the platform really has, with every official variant as its own option (e.g. Gumroad cover 1280 x 720 AND thumbnail 600 x 600).
- OFFICIAL sizes researched Oct 29, 2026 from each platform's own documentation; the full sourced table is in docs/shop-sizes.md and encoded in ShopSizes.kt. No invented numbers: platforms without an official banner/logo spec simply do not show those categories.
- Editing page reuses the social engine: Crop (ratio-locked free-drag frame), Position (v0.11.2 free-pan + zoom), Background (Blur/White/Custom Color/Transparent fill). Reset in-tool. Export lands on the standard result page (JPG/PNG/WebP + quality slider = his "image quality and compression" requirement) and back returns to the shop editor.
- Product Collage (new mini-feature): pick 2, 3 or 4 photos, they combine into one canvas at the platform's product size (side-by-side, stacked, big+two, or 2x2 depending on the canvas shape), white gaps, live preview, export through the standard page.
- Batch export (new): from the shop editing page, "Batch export more photos at this size" processes up to 20 picked photos to the SAME target and settings in one go and saves them all to Pictures/PixelCraft with a progress dialog (Crop mode uses a centered crop per photo). Fully on-device, no new permissions.
- Verified with REAL PIXEL tests (ShopSizesTest): collage layout geometry (side-by-side, stacked, quadrants, gap color), exact canvas dims, and the official-table sanity checks (11 platforms, 5 main shops, key sizes, collage on every platform). UI render test covers the picker.

## v0.11.3 fix (his report: "name not matching")
- Homepage tile renamed "Social & Shop Sizes" -> "Social Media Sizes" so the tile and the tool inside carry the same name everywhere.

## v0.11.2 fix (his report: "when I tried to zoom i can't select fix part. It should freely move and select any part")
- Social Position mode pan fixed: drag deltas were measured in screen pixels but converted with preview-bitmap math (pan moved far too little), the pan direction was inverted against the finger, and stale captured pan values swallowed most drag events. Now the preview frame is rendered at its exact on-screen size, finger movement maps 1:1 onto the photo at any zoom, dragging right moves the photo right, and every drag event accumulates on fresh state. Verified with a REAL PIXEL test: zoom 2x pan reaches all four quadrants (red/blue/green/yellow corners).
- "Background" mode button label wrapped to two lines - mode buttons and fill chips now stay on one line.

## v0.11.1 fix (his feedback: "when we select image it must show here")
- Social Media Sizes home: the "Select Your Image" card now shows the selected photo as a visible thumbnail next to the text (the size text stays).
- Removed the "More" tile from the platform grid (his request).
- Added the missing Pinterest Profile Cover: 800 x 450 px minimum, 16:9 (official: help.pinterest.com "Personalize your profile"; business accounts, Pinterest crops to 16:9). Documented in docs/social-media-sizes.md.
- Homepage: "Social & Shop Sizes" moved into the Compress tile slot; Compress moved down to its old slot (his request).

## v0.11.0 feature - Social Media Sizes (his GO: "Research deeply and add official size")
- New tool, opened from the homepage "Social & Shop Sizes" tile (the tile now opens this tool instead of the Presets shortcut; the Presets bottom tab itself is unchanged). Homepage layout otherwise untouched.
- Three pages: a platform picker ("Social Media Sizes / Every platform. Every image size." + Select Your Image card + a grid of Instagram, Facebook, YouTube, TikTok, LinkedIn, Pinterest, X, WhatsApp, plus More), a category list per platform (Profile Picture / Post / Story / Cover Photo-Banner / Video Thumbnail / Ad Creative / Custom Size), and an editing page ("Instagram - Post" style) with live preview, the target size label, Crop / Position / Background buttons and an orange Export Image button.
- OFFICIAL sizes, researched Oct 29, 2026 against official platform help pages where they exist (YouTube, LinkedIn, Pinterest) and established design references elsewhere. The full sourced table is documented in docs/social-media-sizes.md and encoded in SocialSizes.kt. Safe-area notes (YouTube banner 1546x423, Facebook cover center-safe) are shown on the cards.
- Custom Size accepts any width/height from 16 to 12000 px.
- Editing reuses the existing engine: Crop = free-drag frame locked to the target ratio (same CropPreview as Crop & Rotate), Position = cover-fill with drag-to-pan and a zoom slider (WYSIWYG preview), Background = whole photo fitted inside the target with Blur / White / Custom Color / Transparent fill (Transparent forces PNG export for real alpha).
- Export lands on the standard result page (before/after + sizes + save/share) and back from it returns to the Social editing page. Reset stays in-tool with a Select Image card. Fully on-device, no new permissions.
- Verified with REAL PIXEL tests on Robolectric native graphics (SocialSizesTest): exact target dimensions out of every bake, crop content correctness, position full-frame coverage and real panning, white/transparent/blur fill corner pixels, plus the official-table sanity checks. UI render test covers the platform picker.

## v0.10.1 fix (his report: "it should crop in selected area. Now it not crop")
- Circle Cutter output: the shape mask silently did nothing - the mask never erased anything: a DST_IN path draw only touches pixels the path covers, so everything outside the shape stayed opaque, and the "cutout" was the plain square region. Now the inverse frame-minus-shape path is drawn with DST_OUT (anti-aliased edges). Verified with REAL PIXEL tests on Robolectric native graphics (corner alpha = 0, centre = 255 for Circle/Heart/Rounded; Square untouched) - same pipeline as his phone.
- Default frame is now the largest centered 1:1 square (was a non-square default on non-square photos, which squashed the output).
- Button renamed "Apply & Save" -> "Crop" (his words).

## v0.10.0 feature - Circle Cutter (his tab-4 request, reference screenshot provided)
- New tool, 4th tile on the homepage (Compress moved down one row; homepage layout/style otherwise untouched).
- Shapes: Circle, Square, Rounded (18% corner radius), Heart. Draggable 1:1 frame on the photo: drag inside to move, corner handles to resize, page scroll pauses while dragging (same fix as Crop & Rotate v0.9.2).
- Background choice: Transparent (real PNG/WebP alpha; JPG forced to PNG) / White / Custom Color (8 swatches).
- Output size in px/inch/cm/mm with DPI field under the units (his rule), one square size.
- Live preview card of the cutout. Reset stays in-tool with Select Image card. Apply & Save goes to the standard result page (before/after + sizes + export). Fully on-device.

## v0.9.2 fix (his report: "when I select image it is must stay in centre. Now it is stay in one side.")
- Crop & Rotate preview: the photo was shifted to one side because centering was applied twice (the layout centers the image AND a manual offset moved it again). The manual offset is removed - the photo and its crop frame now stay centered on select, and through rotate, mirror and adjust.
- Crop & Rotate layout (his request): the instruction text ("Drag the corners...") moved down below the edit controls - the rotate/mirror buttons and Adjust section now sit directly under the image.
- Crop & Rotate drag (his report: "It must move freely any part of image"): the page scroll was stealing vertical drags so the frame felt stuck. Page scroll now pauses while a drag is active, corner handles have a larger touch target, and drag end/cancel always restores scrolling.

## v0.9.1 feature (his ask: screenshots of a reference editor + "add these things... and please add mirror as well")
- STRAIGHTEN, HORIZONTAL and VERTICAL adjust dials in Crop & Rotate, matching his reference app in our light style: a round value dial over a draggable tick ruler (-45 to +45 degrees, half-degree steps), mode chips to switch between the three, and a small Reset that zeroes all adjustments. Straighten rotates the photo freely; Horizontal and Vertical add real 3D perspective tilt.
- MIRROR: the Flip H button is now called Mirror (his word for it), next to Flip V.
- TRUE WYSIWYG: the preview draws the exact projective transform the export bakes (pure, unit-tested math: corner projection + cover-zoom so no empty wedges can appear at any angle). Preview and output always match. Everything still on-device, zero permissions, no new dependencies.

## v0.9.0 feature (his ask: "Now we build next tab" > "Crop and rotate" + his design reference "Make like this")
- CROP & ROTATE TOOL, built to his design reference in the app's light style: an interactive crop frame right on the photo - drag the corners to resize, drag inside the frame to move it, dimmed outside area, rule-of-thirds grid, big corner handles sized for fingers. Left 90° / Right 90° / Flip H / Flip V buttons apply instantly. Aspect ratio chips: Free, Original, 1:1, 4:3, 16:9, 9:16 - locked ratios keep their shape while dragging.
- "Apply Crop" goes straight to the result page (before/after + sizes + export), no bounce back - same flow as Resize. The Crop & Rotate editor chip and the home toolkit tile both open the tool (photo picker first if no photo is loaded). Reset stays inside the tool with the Select Image card, same as Remove Background.
- Crop-frame math is a pure, unit-tested module (centering, aspect locks, corner drags, bounds clamping). All on-device, zero permissions, no new dependencies.

## v0.8.1 fix (his test: "Not good brother. Previous was good")
- REVERTED the background remover to the U-2-Netp engine (the v0.7.0 quality he said was good). He tested v0.8.0's heavier ISNet engine on a real photo (baby + bunny) and it left background patches, so the ISNet model and its code are REMOVED. Engine chain is again: U-2-Netp > ML Kit fallback, all on-device and offline.
- APK shrinks back to about 60 MB (the 46 MB ISNet model is gone).
- LESSON KEPT HONEST: "strongest model" is not always "best result" - the heavier model lost on his real photo, and his eyes are the test that counts.

## v0.8.0 feature (his ask: "make more strong background remover")
- STRONGEST ON-DEVICE ENGINE: the cut-out now runs ISNet (isnet-general-use, dynamically quantized to 46 MB, bundled in the app) - the current best general-purpose open segmentation model (rembg's top default), dichotomous segmentation at 1024px. Fallback chain: ISNet > U-2-Netp > ML Kit, all on-device and offline. Validated HERE before shipping: quantized model output differs from the full model by 0.7% on average, and a real-photo test (car + person, busy background) produced a clean full-subject cut-out with sharp edges.
- Mask normalization is now min-max (matches the reference pipeline).
- APK grows to ~106 MB (ISNet 46 MB + U-2-Netp 4.6 MB + ONNX runtime 34 MB, arm64-only). He accepted a bigger app for quality; downloads are one-time per install.
- HONEST LIMIT: this is the top of what runs on a phone. remove.bg-class cloud services still win on the hardest cases (loose hair strands against busy backgrounds) because they use much bigger models; on product photos and clear subjects ISNet is very close.
## v0.7.0 feature+fix (his asks: "Reset should open remove background tab and select image option" + "make perfect remove background, like professional tool")
- QUALITY: the cut-out engine is now U-2-Netp (u2netp.onnx, 4.6 MB, bundled in the app) running on ONNX Runtime - the open model family behind popular offline cut-out tools. It outputs a full alpha MATTE (soft per-pixel transparency) instead of ML Kit's coarse binary mask, so edges (hair, fur, product outlines) come out visibly cleaner; the matte is upscaled with filtering which feathers the edge. ML Kit subject segmentation stays as automatic fallback if the ONNX engine cannot initialize on a device. Still 100% on-device, works offline, no upload, no API key. APK is restricted to arm64-v8a (all modern phones) to carry the native runtime without x86 bloat. HONEST LIMIT: remove.bg-style services use much larger server-side models; u2netp is close on clear subjects (product photos on plain-ish backgrounds) but fine wisps of hair and very busy backgrounds are still a notch below paid cloud tools. He should compare with his real product photos.
- RESET UX: Reset now stays INSIDE the current tool. In Remove Background (and the other editor tools) it clears the photo and every editing trace and shows a "Select Image - Choose a photo from your phone" card right on the page; picking a photo in the Background tool reopens the Background tool. No more jump to the homepage.
## v0.6.1 fix (his report: "same image coming after close and start - Reset should give new image upload option")
- Editor Reset now does a FULL reset: clears the loaded photo(s), queue, processed results, sizes, Background cut-out state AND the auto-saved draft for that photo, then returns to the Home page where he picks a new image. Before, Reset silently reloaded the same photo, and the draft could bring it back after closing the app.
## v0.6.0 feature (his ask: "Now build remove background" - the real feature, with his structure screenshot)
- REMOVE BACKGROUND is live: home toolkit tile and the editor's Background tab both open the new tool (tile picks a photo first when none is loaded). Flow follows his structure screenshot in the app's design language: photo preview > "Remove Background" main action > Output Background (Transparent / White / Custom Color with swatches) > Width x Height with the SAME unit chips as Resize (px / Inches / cm / mm, same conversion behavior, DPI field visible under all units) > Continue to Export, where the existing Save as format row (Original / JPG / PNG / WEBP / PDF) and Download/save options take over.
- The cut-out runs ON-DEVICE with ML Kit subject segmentation (no upload, works offline). Honest caveat: Play Services downloads the small model once on first use, so the very first tap needs internet; the UI says this, and first-use failure shows a plain-English retry hint. Model quality varies by photo - he should test with his real product photos.
- Live preview: after removal the preview box shows the cut-out on the chosen background/color and updates as options change (small preview canvas; full size is built at export).
- DPI is real: stored in JPEG EXIF resolution tags and used for the physical page size of PDF exports.
- Transparency is honest: PNG/WebP keep see-through; JPG and PDF flatten onto white (before, a transparent bitmap saved as JPG could come out on black). If Transparent is chosen while the export format would be JPG, the export defaults to PNG.
- Held for later, only on his explicit ask: the My Photos gallery upgrade and the Presets catalog (Etsy/YouTube/Pinterest verified entries) - both built during earlier scope reads and unbuilt after his corrections.
- NOT VERIFIABLE HERE: ML Kit cannot run on this build machine (no emulator) - the segmentation path is compile-verified and unit-test-safe only; real-device behavior is his to confirm.
## v0.5.2 fix (user-reported on v0.5.1: remove Before/After from the editor page)
- Editor (Resize Image) page: removed the Before/After tabs and the "After:" info line - the editor simply shows the photo being edited. The before/after comparison now lives only on the result page (where it was added in v0.5.1).
## v0.5.1 fix (user-reported on v0.5.0: no DPI field under Pixels in the multi-photo sheet)
- The options sheet's Custom row now shows the DPI field under ALL units, Pixels included (it was hidden unless inch/cm/mm was picked). Matches the editor page, where DPI has been visible under every unit since v0.4.8. Pixel input still ignores DPI for the math - the field is there for consistency as he asked.
- Queue result screen: removed the "All N photos processed." snackbar that lingered over the bottom buttons - the green success card already carries the summary (user-reported on v0.5.0).
- Result page now also shows the ORIGINAL dimensions under the result dimensions ("Original: W x H px") so the before/after comparison is complete (his "show before and after, size" request).
- Added a small internal test hook: an intent extra (pixelcraft.test.photo) pre-loads a photo from a file path so automated UI tests can run the full flow without the system photo picker. Invisible in normal use.
- Visual consistency pass (his "even font, colour, size" ask): unified all page titles to 26sp (Saved successfully and Print Size & DPI were 24sp), replaced a one-off hardcoded border color with the shared palette color.
- Unified the two different success greens into the one palette green (Saved page icon + Verified badge), and capped the saved-file name on the Saved page at 2 lines so a long name can never spill out of the frame.
- Result page now has Before/After tabs above the preview - the same comparison tabs as the editor page (his request: "before and after comparison in final page, after resized, before save"). After shows the resized photo, Before shows the original.
- REBRAND (his request): app is now "A2Z Image Craft" with his new launcher icon (adaptive icon: his artwork centered on the icon's own dark navy; legacy PNGs for all densities). Home page shows "A2Z Image Craft" with his tagline "Resize. Remove. Convert. Create." Package name and save folders unchanged so the update installs over the old app.
- Fixed a hidden stall the UI tests caught: after picking a photo, the Process button kept spinning for ~4 seconds (busy was gated behind the snackbar's auto-dismiss). Button is ready immediately now.


## Implemented in source
- Sky-blue, orange, white and deep-navy theme (unchanged from Stage 1).
- Home screen as a source chooser: "Select Photos", "Take a Photo" (system camera app via FileProvider, no camera permission) and a zero-permission "Recently edited" row that remembers photos you opened (persisted picker grants, max 12, stored only on the phone).
- Bottom navigation (Home, Presets, Settings) unchanged.
- Editor flow is now Process-then-Export: the Process button applies the resize, then Export is a separate screen.
- Percentage resize: quick chips (25/50/75/100/125/150/200%) plus a custom percentage field validated to 10-200%.
- Width/height fields with aspect-ratio lock and dimension/pixel-count validation (ResizeMath, unchanged limits).
- Toolbar chip labels can no longer wrap (single-line, horizontally scrollable row). Fixes the Stage 1 "Backgroun/d" wrap.
- "Change in aspect ratio" dialog when the target size changes the aspect ratio: Fit, Crop and Stretch cards, each with title, description and a LIVE preview of the actual photo.
  - Fit: keeps the whole photo, fills empty space with a blurred, white or black background (selectable).
  - Crop: 3x3 position chooser for which part of the photo to keep. Freehand crop is planned for Stage 3.
  - Stretch: exact fill, with a distortion warning.
- Rotate 90 degrees button on the editor preview (in-memory; the original file is untouched until export).
- Multi-select photo picker (user requirement, Stage 2): "Select Photos" now allows picking up to 50 photos in the system photo picker (no extra permission). Photos are processed and exported ONE BY ONE in a queue ("Photo 1 of 6", "Next photo (2 of 6)"); the target dimensions carry over to the next photo so a set of photos can be resized to the same size quickly. Applying settings to all selected photos fully automatically (true batch processing) remains Stage 6 by design (spec v2). The system picker is used instead of a custom in-app gallery so the app keeps its zero-photo-permission principle.
- Export screen: before/after size summary with estimated output size, editable file name, same-format-as-original export (JPEG, PNG or WebP), quality slider (10-100%, default 90%) for JPEG/WebP, and three save options:
  - "Save as new file" to Pictures/PixelCraft (MediaStore with pending-state writes).
  - "Replace original" with Android's system confirmation dialog (MediaStore createWriteRequest). Available on Android 11+ for gallery photos, not for camera captures and not when the original format is unknown.
  - "Save to folder you choose" (system folder picker, Storage Access Framework).
- Share button on the export screen (Android share sheet, file served from app cache via FileProvider).
- JPEG metadata (EXIF: date, camera, GPS, etc.) is copied to the exported JPEG best-effort; orientation is normalised because pixels are already rotated.
- Presets screen now reads the bundled presets.json data file. It is intentionally EMPTY: no preset is shown until Stage 2B research verifies each size against official platform documentation (each entry will carry sourceUrl, dateVerified and verification status).
- Print-size calculator can now apply its result directly to a loaded photo.
- GitHub Actions workflow for unit tests, debug APK and release AAB build artifacts (unchanged).
- Unit tests for percentage resizing, aspect-ratio calculation and invalid dimensions (unchanged, still passing scope).

## Research additions in this stage (beyond the settled requirements)
The user asked to "add everything what we need" after researching top resizer apps. Added, and listed here so they can be vetoed:
1. Quality slider for JPEG/WebP export (standard in Image Size, Puma, Resize Me).
2. Estimated output file size + "x% smaller/larger" summary (standard in Puma, Compressify; computed by a real encode pass, shown as an estimate).
3. "Save to folder you choose" export option (Storage Access Framework; standard in top apps).
4. Share button on the export screen (standard in Image Size, Resize Me).
5. EXIF metadata kept on JPEG export (standard in Image Size, Puma; automatic, a keep/strip choice is planned for Stage 3).
6. Rotate 90 degrees button (standard in Image Size, Resize Me).
Not added on purpose (later stages or out of scope): target file size in KB, ratio/longest-side resize modes, freehand crop, watermark, borders, batch processing, GIF export (photos degrade to 256 colours), DPI metadata writing.

## Not implemented yet
- Freehand crop tool, background removal (ML Kit), compression by target KB, DPI metadata writing.
- Batch processing (Stage 6), Print Size & DPI full screen (Stage 7).
- Verified preset data (Stage 2B research), AdMob/UMP, signed release AAB, Play Store listing.

## Known limitations
- The image loader downsamples very large images to a 4096-pixel long side; full-resolution processing is planned for a later stage.
- "Replace original" is only offered on Android 11+ for gallery photos whose format is recognised.
- On Android 9 and older the app requests storage access when saving (scoped storage did not exist yet).
- PNG output ignores the quality slider (PNG is lossless).
- "Recently edited" only lists photos opened after installing this version.

## Test status
- v0.3.0 source was verified on a local Android toolchain (JDK 17, Android SDK 36, Gradle 8.13) BEFORE delivery: "gradle :app:testDebugUnitTest" BUILD SUCCESSFUL (compile + unit tests) and "gradle :app:assembleDebug" BUILD SUCCESSFUL with the APK signed by the repo's persistent debug key.
- GitHub Actions re-runs the same steps on upload; results are reported honestly from the actual run.
- On-device behaviour must be confirmed on the user's phone after installing the debug APK.

## Next stage
Stage 2B: research official platform documentation and populate presets.json only with verified sizes (source URL + verification date each).

## v0.16.8 (56) - Text Effects Pack (his "Yes all 8", 2026-10-30)
- New "Effects" Create tab: 8 ready-made one-tap styles (Sunset/Ocean/Candy Fade, Neon Pink/Blue/Green, Hollow White/Gold), effect chips (None/Gradient/Neon/Hollow), gradient colour presets, neon glow colour picker. (VETOABLE: template set + gradient presets are my picks.)
- Effects render in the shared draw path (straight + curved) - preview = export. Neon = layered soft strokes under a bright core (no blur filters, identical on every phone); Hollow = stroke only; Gradient = top-to-bottom fade.
- New "Stickers" Create tab: drawn shapes (Heart/Star/Arrow/Burst) as image layers under text, 6 emoji categories (60 emoji) as editable text layers. (VETOABLE: shapes + colours + emoji set.)
- Create grid now 10 tiles: Add Text, Add Image, Image Size, Layers, Filters, Process, Pick Colour, Flip & Clone, Effects, Stickers.
- Known gap: saved text styles (user-created presets) do not carry the new effect fields.
- Tests +7 (129 total): gradient direction, neon glow spread, hollow centre, template fields, sticker ink, effects-level UI + Neon Pink click, stickers level + Heart click.


## v0.16.9 (57) - New Tools (his "Yes all 8" #6-8, 2026-10-30)
- Collage Maker: new home tile, 2-9 photos, square 1600px PNG, gap slider + white/black background; the preview is the export (same engine call). 5-9 photos use a uniform grid (Transforms.collageGrid; 2-4 keep the classic Shop-flow layouts). An unused last cell (5 or 7 photos) stays the background colour. (VETOABLE: layouts + fixed square size.)
- Meme Maker: new tile inside Text on Photo - top/bottom fields, one tap creates classic meme captions (sans-serif-black, white fill, black outline, auto-uppercase); the captions are normal layers, so every editor control (drag, effects, fonts) still works.
- Background Remover: the standalone tool already existed on Home; v0.16.9 ALSO adds "Remove background" to image layers inside Text on Photo (logo/sticker cut-outs), fully on-device via the existing U-2-Netp engine. (ImageLayer.bitmap became var for this.)
- His two Process-button messages: the Process tile left the grid; Process is now a full-width orange rectangle at the very bottom of the Create menu (same emphasis as Export Image), still one tap. Create grid = 10 tiles (+ Meme); Home grid now 11 features (+ Collage Maker).
- Tests +5 (134 total): collageGrid 5- and 9-photo cell placement, meme UI flow (type + apply -> 2 layers), collage screen UI, Create menu with full-width Process.


## v0.16.10 (58) - FIX: re-tapping a selected layer re-opens its options (his report, 2026-10-30)
- His report: his logo image layer showed selected (X + rotate handle) but the sheet stayed on the Create grid, so the Remove background button never appeared.
- Root cause: the sheet only jumped to a layer's options when the selection CHANGED. With the layer already selected (e.g. after pressing Menu), tapping it again changed nothing and the panel stayed hidden.
- Fix: every layer select (canvas tap or Layers row) now bumps a selection tick that re-opens the layer's options panel, even when the selection itself did not change. Applies to text and image layers alike.
- Regression test: phoneWindowReopenImagePanel drives his exact path (select > Menu > re-select via the Layers list > Remove background must show) + screenshot v01610_phone_imagelayer. Tests 135 total.


## v0.20.4 (71) - FIX: Replace-original crash + Are-you-sure flow + quality slider overflow (his on-device reports, 2026-10-30)
- CRASH FIX (his words: "If I click replace original app is exit"): the single-photo Replace path launched the MediaStore write-consent intent with NO exception handling (the batch twin was already guarded). A source URI the system rejects (e.g. some photo-picker URIs) made createWriteRequest throw inside a LaunchedEffect, killing the whole app. Every intent launch on the export paths is now guarded: single replace, batch replace, camera capture, folder picker, legacy storage permission. On any failure the app stays open with a plain-English snackbar pointing at "Save as new file".
- NEW FLOW (his approved reference, "Like this" + competitor screenshots): tapping Replace original (single or batch) first shows an in-app "Are you sure?" dialog - "The original photo will be overwritten by the new one. This cannot be undone." / "The N original files will be overwritten by the new ones." with Cancel / Replace. Only Replace continues to the system consent dialog ("Allow A2Z Image Craft to modify photos?"). After Allow, the save completes and the app STAYS OPEN (Success screen / in-app summary). All 4 save options stay on the Export screen (his "No keep").
- Because every tool (Resize, Crop & Rotate, Remove Background, Circle Cutter, Convert, Compress, Presets, Social, Shop) funnels into one shared Export screen + one batch replace path, this fix covers every section at once.
- UI FIX (his screenshot): the Export screen Quality slider ran off the right edge of the screen, thumb half cut off. Root cause: the slider sat in a Row next to the "Quality: N%" label with no width constraint, so it measured itself wider than the screen. It now takes weight(1f) inside the Row and stays inside the padding. Audited all ~25 sliders in the app: the collage quality sliders are standalone in Columns (fill the padded width, safe); every other slider in a Row already had weight(1f). The Export slider was the only broken one.
- Deep audit pass ("Every section look deeply and fix"): all activity-result launch sites reviewed (5 unguarded ones hardened above); all sliders reviewed (1 broken, fixed); save/cancel/deny paths re-traced: Cancel on the in-app dialog closes it and keeps the Export screen; Deny on the system consent shows "Replace cancelled. Nothing was changed." and stays in-app.
- Tests +3 (138 total): qualitySliderStaysInsideScreen (slider bounds inside root bounds at w360dp-h640dp + phone screenshot), replaceOriginalShowsAreYouSureDialogAndCancelClosesIt, confirmReplaceNeverExitsTheApp (regression for his exact crash: confirm Replace, app must stay alive).
- On-device note: the MediaStore write-consent round-trip itself (system Allow/Deny dialog) cannot run on the JVM test host; the in-app dialog, the guard and the stay-alive behaviour are fully test-covered. The final Allow->overwrite->Success step should be verified once on his phone with a gallery photo.


## v0.20.5 (72) - FIX: Replace original now reaches the system consent dialog (his on-device retest) + Quality slider rebuilt + black info bars gone (2026-10-30)
- REPLACE, ROOT CAUSE FOUND: the Android photo picker hands out content://media/picker/... URIs (older phones: media document URIs "image:<id>"). MediaStore.createWriteRequest only accepts real MediaStore item URIs (content://media/external/images/media/<id>), so it threw and our v0.20.4 guard showed the fallback snackbar. NEW: SaveUtils.replaceTargetFor resolves every picked URI to its MediaStore item (both picker URI shapes carry the same row id as their trailing number; document URIs via DocumentsContract id). The system "Allow A2Z Image Craft to modify this photo?" dialog now appears, and after Allow the write goes to the resolved MediaStore URI (single AND batch). Test-covered up to the system boundary; the Allow->overwrite round trip is the standard MediaStore flow the reference app uses on the same phone - please confirm once on-device.
- QUALITY SLIDER REBUILT (his repeat report): pixel analysis of his v0.20.3 and v0.20.4 screenshots showed the track always ended exactly at the 16dp padding - what read as "cut off" was Material3's detached inactive-track stub beyond the thumb plus the label+slider Row squeezing the track. Rebuilt to the app's proven Compress-screen layout: "Quality" label row with the % value right-aligned, full-width smooth slider below (step ticks removed), "Smaller file / Higher quality" end captions. Visibly different, nothing hugs the thumb.
- BLACK INFO BARS REMOVED (his "No need down in black background"): the informational snackbars ("Photo loaded ...", "Processed: ... x ... px" x2) are gone entirely. Remaining messages (errors/confirmations only) now render as a white rounded card in app style instead of the plain black bar. (VETOABLE: the white-card styling.)
- Tests +5 (214 total): 5 replaceTargetFor URI-shape tests (picker, legacy picker, media document, plain MediaStore, file/garbage); Export screen screenshots renamed v0205 with the same bounds + no-crash guards.

## v1.0.0 (versionCode 73) - Google Play launch build

Implements the full "A2Z IMAGE CRAFT - FINAL CHANGES BEFORE GOOGLE PLAY LAUNCH" spec in one update. The app stays FREE, with NO ads and NO payments.

Part A - fixes:
- A1: the side menu header no longer sits under the status bar (status-bar padding added).
- A2: the Rate us subtitle in the side menu now says "Enjoying the app? Rate it on Google Play".
- A3: side menu rows no longer clip two-line subtitles (line height fixed).
- A4: the Presets tab in Settings now uses a size/layout icon instead of the duplicate collections icon.
- A5: every long task (loading, processing, background removal, collage, convert, compress, print, save, share) now catches phone-out-of-memory errors and shows a plain-English message instead of crashing.
- A6: the unused presets/presets.json file was deleted.

Part B - additions:
- B1: Help & tips page (in the side menu and in Settings > Support), written per tool in plain English.
- B2: Email us now pre-fills the app version, phone model and Android version in the email body.
- B3: Open-source licences page (in the side menu and in Settings > About).
- B4: What's new page (Settings > About) describing version 1.0.0.
- B5: versionName is now 1.0.0, shown at the bottom of the side menu.

Parts C and D: the Terms of Use and Privacy Policy texts were replaced with the final launch versions. Two privacy details were corrected so every sentence is true for this app: dark mode is not stored between sessions (removed from the stored-data list), and the list now also names the saved collage project, the last used resize size, and the temporary private cache copies used for share/print/export.

Part E - Google Play upload key (CI):
- The release build is signed with the "upload" key when the CI provides UPLOAD_KEYSTORE_PATH and UPLOAD_KEY_PASSWORD; without them it stays unsigned, so nothing breaks.
- android-build.yml now decodes the UPLOAD_KEYSTORE_BASE64 secret, exports the signing environment, and always deletes the keystore from the runner afterwards.
- New manual workflow create-upload-key.yml creates the PKCS12 key (alias "upload", RSA 2048, 10000 days) and uploads it as an artifact that auto-deletes after 1 day.

Part F - safety checks run before delivery:
- F1: merged manifest permissions inspected on the built APK (only storage on Android 9 and older; ML Kit adds its standard model-download access).
- F2: stored-data inventory done; privacy text updated to match exactly (see Parts C/D above).
- F3: no network code and no networking libraries in the app; all tools work fully offline (background removal's Google ML Kit fallback downloads its model once via Play services, as the privacy text already explains).
- F4: Help & tips text verified against the real behaviour of every tool (resize has no aspect lock, collage swaps by holding and dropping a photo onto another frame, the cutter is called Circle Cutter and offers Circle/Square/Rounded/Heart shapes).
- F5: every release library is covered by the Open-source licences page.
- F6: policy greps - no rating prompts asking for stars, no premium/ads/payments remnants anywhere in the app.

## v1.0.1 (versionCode 74) - his polish batch (three lists, "Now that's it")

Doc pages: Help & tips shows each tool name as a bold dark heading with the text below; Open-source licences shows each component name bold on its own line; every doc page (Help, Licences, What's new, Terms, Privacy) has bigger, darker body text; What's new is a tidy list with small gaps. "Circle Cutter" is renamed "Shape Cutter" everywhere in the UI (it offers Circle/Square/Rounded/Heart).
Home: top-right settings gear removed (Settings stays in the bottom bar); "Resize. Remove. Convert. Create." always fits one line; all tool icons are real Material icons, bigger and clearer (Crop, Print, Compress, Share, Storefront, AutoFixHigh magic wand, Tune sliders, TextFields; picks for the unnamed three: Resize = PhotoSizeSelectLarge, Shape Cutter = Interests, Collage = GridOn - vetoable); small texts updated ("Cut out any subject", "JPG, PNG, WebP & PDF", "Exact sizes for every platform").
Bottom bar: purple selection highlight changed to light sky blue; Presets tab has its own icon (Style, vetoable).
Presets page: top line is now "Ready-made sizes for shops and social media"; the green badge moved under the size line and is renamed "Official size" so long titles never touch it; the search box has a search icon and a sky-blue border; Gumroad Cover image (1280 x 720) and Gumroad Thumbnail (600 x 600) added under "Shop & creator" from the official Gumroad help page. Payhip was checked too: its official help says it recommends no exact dimensions (aspect-ratio guidance only), so nothing was added for Payhip.
