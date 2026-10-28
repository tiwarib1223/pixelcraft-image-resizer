# Phone-only GitHub setup (no command line)

This route lets you upload the project as one ZIP file, then have GitHub Actions unpack and build it in the cloud.

## A. Upload the archive

1. Create a GitHub account at https://github.com if you do not already have one.
2. Create a new repository named `pixelcraft-image-resizer`. For a personal project, choose Private if you prefer; GitHub Actions availability and usage limits depend on account settings.
3. Download `pixelcraft-image-resizer-stage1.zip` from this conversation and upload that ZIP file to the repository root using **Add file → Upload files**. Commit it to `main`.
4. In the repository, choose **Add file → Create new file**.
5. For the file name, enter `.github/workflows/android-build.yml`.
6. Paste the workflow below into the editor and commit it to `main`.
7. Open **Actions → Android build** and choose **Run workflow**. If a workflow run starts automatically after the commit, you can open that run instead.

## B. Workflow file contents

Copy everything in the following YAML block into `.github/workflows/android-build.yml`:

```yaml
name: Android build

on:
  workflow_dispatch:
  push:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Check out repository
        uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Gradle 8.13
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.13'

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3

      - name: Install Android SDK packages
        run: sdkmanager "platforms;android-36" "build-tools;36.0.0" "platform-tools"

      - name: Unpack source project
        run: unzip -q pixelcraft-image-resizer-stage1.zip -d source

      - name: Run tests and build APK/AAB
        run: gradle -p source/pixelcraft_android_v1 testDebugUnitTest assembleDebug bundleRelease --no-daemon

      - name: Upload APK and AAB artifacts
        uses: actions/upload-artifact@v4
        with:
          name: pixelcraft-stage1-builds
          path: |
            source/pixelcraft_android_v1/app/build/outputs/apk/debug/app-debug.apk
            source/pixelcraft_android_v1/app/build/outputs/bundle/release/app-release.aab
          if-no-files-found: error
          retention-days: 14
```

## C. Download the APK if the build succeeds

1. Open the completed workflow run in **Actions**.
2. Scroll to **Artifacts**.
3. Download `pixelcraft-stage1-builds`.
4. Extract the artifact ZIP and install `app-debug.apk` on your Android phone.
5. If Android warns about installing apps from that file manager/browser, allow it only for this file you built from your own repository. Turn the permission off again after installation if you prefer.

## Notes

- This cloud build requires an internet connection and downloads Gradle dependencies and Android SDK packages.
- GitHub Actions limits depend on account type and current GitHub policy. Do not upgrade or pay for anything without checking the plan first.
- The APK is a debug build for testing. The AAB is unsigned and is not ready for Play Store submission.
- The ZIP must be named exactly `pixelcraft-image-resizer-stage1.zip` in the repository for the workflow above to find it.
