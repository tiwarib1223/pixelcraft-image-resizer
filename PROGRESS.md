# PixelCraft Image Resizer — Progress

## Current stage
Stage 1 starter project: native Kotlin + Jetpack Compose project files and cloud build workflow prepared.

## Implemented in source (not yet built on Android)
- Sky-blue, orange, white and deep-navy theme.
- Home screen and bottom navigation (Home, Presets, Settings).
- Android photo picker for one image and a multiple-selection entry point.
- Image decode with EXIF orientation handling and downsampling to a maximum 4096-pixel long side for this starter.
- Width/height resize fields, aspect-ratio lock, percentage quick actions, dimension/pixel-count validation.
- Basic PNG export to `Pictures/PixelCraft` using MediaStore and a user-editable output name.
- Print-size-to-pixels calculator (cm input and custom DPI).
- Starter preset list UI and a JSON schema file with no fabricated/verified sizes.
- GitHub Actions workflow for unit tests, debug APK and release AAB build artifacts.
- Unit tests for percentage resizing, aspect-ratio calculation and invalid dimensions.

## Not implemented yet
- Confirmed Android build / APK installation (must run in GitHub Actions and test on a phone).
- Officially verified preset data for all requested platforms.
- Crop, rotate controls, format selection, JPEG/WebP export UI, compression and target file size.
- On-device background removal via ML Kit Subject Segmentation.
- Full-resolution image processing workflow, batch processing, undo/redo, share sheet, replace-original flow.
- DPI metadata writing, full print calculator modes, quality indicator, dark/system preference persistence.
- AdMob, UMP consent, final privacy policy URL, signed release AAB and Play Store listing.

## Known limitations
- This is a Stage 1 starter, not a complete app and not a verified APK.
- The current image loader downsamples large images to reduce memory use. It must be improved for a production-quality full-resolution workflow.
- The current export path defaults to PNG. Do not advertise other output formats yet.
- The build workflow uploads an unsigned release AAB as a build artifact; it is not ready for Play Store submission until signing and release configuration are added securely.
- The current Presets screen is a navigation placeholder and must not be presented as a verified preset library.

## Test status
- Source-level review only so far.
- Automated Gradle tests and Android compilation have NOT been run in this environment because the Android SDK and Gradle executable are not installed here.
- Run `gradle testDebugUnitTest` and `gradle assembleDebug` through the supplied GitHub Actions workflow, then install the generated APK and perform manual phone tests.

## Next stage
Upload this project to a GitHub repository from the Android phone, run the GitHub Actions workflow, inspect the build result, and install the debug APK if the build succeeds.
