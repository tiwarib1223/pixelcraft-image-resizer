# PixelCraft Image Resizer — Progress

## Current stage
Stage 2D v0.4.1 (built on Stage 2C v0.3.0): queue overview screen, the full "Select new dimensions" Process sheet, batch processing of the whole queue with live percent progress, a result screen with savings summary, and batch save (new copies or replace originals). Stage 2C delivered: "My Photos" library screen, export format picker (JPG/PNG/WEBP/PDF), size unit picker (pixels/inch/cm/mm), queue thumbnail strip, and a persistent debug signing key. Stage 2 delivered: process-then-export workflow, aspect-ratio adaptation, camera capture, multi-select (up to 50) with one-by-one queue, recently edited photos, quality control.

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
