# PixelCraft Image Resizer — Stage 1 Android starter

A native Android project using Kotlin and Jetpack Compose, prepared for cloud builds with GitHub Actions. The name is temporary. The colour system uses sky blue `#42BFEF`, orange `#FF8A32`, deep navy `#172B4D`, and white.

## What this stage is

This archive contains real Android source and build files. It is a starter for Stage 1, not a finished Version 1 app and not an APK. The app has not been compiled or installed in this environment because Android SDK and Gradle are unavailable here.

## Stage 1 source features

- Native Compose home screen and navigation.
- Android system photo picker.
- Image preview and EXIF orientation correction.
- Basic width/height resizing, aspect ratio lock and percentage quick actions.
- Basic PNG export to `Pictures/PixelCraft`.
- A basic centimetre-to-pixels print calculator.
- Starter preset list interface; no sizes are claimed verified.
- Unit tests and a GitHub Actions workflow.

## Build in the cloud

1. Create/sign in to GitHub and create a new repository, e.g. `pixelcraft-image-resizer`.
2. Upload the contents of this archive to the repository's `main` branch. Keep the `.github/workflows/android-build.yml` file.
3. Open the repository on GitHub in your phone browser.
4. Tap **Actions**, choose **Android build**, then **Run workflow**. If Actions is not visible, open the repository menu and switch to the desktop website view.
5. Wait for the workflow. If it fails, open the failed step and copy the error text before changing files.
6. If it succeeds, open the run, scroll to **Artifacts**, and download `pixelcraft-debug-apk`. Extract the downloaded ZIP and install the APK on your phone. Android may ask you to allow installation from that browser/file manager; only install an APK you have just built from your own repository.

The first run may take several minutes while Gradle downloads dependencies and Android SDK packages. GitHub Actions usage limits depend on your account and current GitHub policy. Check before using paid features.

## Important limitations

- This is only the first development stage. Crop, compression profiles, verified presets, batch processing, background removal, full format controls, DPI metadata, AdMob and release signing are not complete.
- The image loader downsamples large photos to a 4096-pixel longest side for this starter. A production release needs a more careful full-resolution processing pipeline.
- The current export defaults to PNG.
- The workflow's release AAB is unsigned and is not ready for Play Store upload.
- No ad SDK or production ad IDs are included.

## Build versions selected

- Android Gradle Plugin 8.13.2
- Gradle 8.13 in GitHub Actions
- Kotlin 2.2.20
- Jetpack Compose BOM 2026.04.01
- compileSdk / targetSdk 36; minSdk 26

Recheck current Android and Google Play requirements before publishing.
