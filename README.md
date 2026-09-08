# SoundVault - Audiophile Offline Audio Player & Lossless Vault

SoundVault is a high-fidelity offline audio player and library manager built with modern Android, Jetpack Compose, Material 3, and Room.

## Features

- **Offline Sound Vault**: Local audio library indexing lossless FLAC, ALAC, WAV, and MP3 files directly from device storage.
- **Synthesized Fallback Engine**: Built-in procedural harmonic tone generator via Android `AudioTrack` when no media files are present.
- **Interactive Player Screen**:
  - Vinyl disc rotating visualization with neon lighting
  - 60-bar interactive waveform scrubber
  - 10-band hardware-style EQ presets (Audiophile Flat, Bass Warmth, Vocal Clarity, Studio Monitor, Electronic Club)
  - Audio output route simulation (Internal DAC, USB Type-C DAC, LDAC 990kbps, High-Res ALSA)
  - Scrubbing, repeat modes (All / One / Off), and shuffle
- **Import & Tag Sanitizer**: Auto-scrubs messy filenames, rip brackets, and metadata tags upon import.
- **SoundBundles**: Package and organize albums/playlists into encrypted bundles with QR code synchronization.
- **Custom Cover Art**: Support for gallery uploads, image URLs, and pre-rendered audiophile art presets.

---

## Building in GitHub

This repository comes pre-configured with a **GitHub Actions CI/CD workflow** (`.github/workflows/android.yml`).

### 1. Pushing this Project to GitHub from AI Studio
1. Open the project settings or menu in **AI Studio**.
2. Select **Export to GitHub** (or **Push to GitHub**).
3. Connect your GitHub account and choose a new or existing repository.
4. Once pushed, navigate to your GitHub repository in your web browser.

### 2. Automatic GitHub Actions Build
- Every push to `main` or `master` (and every Pull Request) automatically triggers the workflow.
- You can also manually run the workflow at any time by going to the **Actions** tab in your repository, selecting **Build Android APK**, and clicking **Run workflow**.

### 3. Downloading the Built APK from GitHub
1. Go to the **Actions** tab on your GitHub repository.
2. Click on the latest workflow run.
3. Scroll down to the **Artifacts** section at the bottom of the page.
4. Click **SoundVault-Debug-APK** to download your ready-to-install Android APK (`app-debug.apk`).

---

## Building Locally

### Prerequisites
- JDK 17 or higher
- Android SDK (API 36 / Android 16)
- Android Studio Ladybug or newer (recommended)

### Command Line Build
```bash
# Clone the repository
git clone <your-repo-url>
cd <repo-name>

# Ensure environment file and keystore exist
cp .env.example .env
base64 -d debug.keystore.base64 > debug.keystore

# Build Debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
