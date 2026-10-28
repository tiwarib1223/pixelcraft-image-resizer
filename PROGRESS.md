# PixelCraft Image Resizer — Progress

## Current stage
Stage 2: process-then-export workflow, aspect-ratio adaptation, camera capture, recently edited photos, quality control.

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
Not added on purpose (later stages or out of scope): target file size in KB, cm/mm/inch units, ratio/longest-side resize modes, freehand crop, watermark, borders, batch processing, format picker UI, DPI metadata writing.

## Not implemented yet
- Freehand crop tool, format picker UI, background removal (ML Kit), compression by target KB, DPI metadata writing.
- Batch processing (Stage 6), Print Size & DPI full screen (Stage 7).
- Verified preset data (Stage 2B research), AdMob/UMP, signed release AAB, Play Store listing.

## Known limitations
- The image loader downsamples very large images to a 4096-pixel long side; full-resolution processing is planned for a later stage.
- "Replace original" is only offered on Android 11+ for gallery photos whose format is recognised.
- On Android 9 and older the app requests storage access when saving (scoped storage did not exist yet).
- PNG output ignores the quality slider (PNG is lossless).
- "Recently edited" only lists photos opened after installing this version.

## Test status
- Static checks done in the build environment: XML/JSON validity, code structure review.
- Automated Gradle unit tests and Android compilation run in GitHub Actions on upload; results are reported honestly from the actual run.
- On-device behaviour must be confirmed on the user's phone after installing the debug APK.

## Next stage
Stage 2B: research official platform documentation and populate presets.json only with verified sizes (source URL + verification date each).
