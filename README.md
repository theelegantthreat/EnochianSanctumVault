# Enochian Magic ✦ Sacred Grimoire & Sigil Sanctum

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg)](https://developer.android.com/jetpack/compose)
[![Database](https://img.shields.io/badge/Storage-Room%20DB-orange.svg)](https://developer.android.com/training/data-storage/room)

**Enochian Magic** is a modern, comprehensive Android grimoire and ritual companion app designed for esoteric practitioners, researchers, and ceremonial magicians. Built natively with **Kotlin** and **Jetpack Compose**, it provides an offline-first suite of tools for exploring John Dee and Edward Kelley's Enochian system, tracing sigils, logging ritual workings, tracking invocations, and calculating planetary timing.

---

## ✦ Key Features

### 1. 📖 Enochian Sanctum Vault (Reference Grimoire)
* **48 Angelic Keys (Calls):** Full Enochian text, phonetic pronunciations, and English translations.
* **Chant Audio Synthesizer:** Built-in tone generator for sacred vowel chants and elemental audio resonance.
* **91 Aethyrs Library:** Comprehensive reference covering planetary rulers, elemental attributes, and mystical domains.
* **Watchtower Tablets:** Detailed breakdown of the Air, Fire, Water, and Earth Watchtowers and Holy Names.
* **Sigil Glossary:** Detailed catalog of traditional sigils with elemental, gemstone, and angelic correspondences.

### 2. ⚡ Interactive Enochian Sigil Generator & Tracing Engine
* **Rose Cross & Wheel Tracing:** Converts any custom intention phrase or sacred name into a geometric sigil path across the 21-letter Enochian wheel.
* **Custom Styling:** Adjustable stroke width, color palettes (Enochian Gold, Celestial Cyan, Mystic Violet, Elemental Green, etc.), and glow intensity.
* **Grimoire Storage:** Save generated sigils directly to local Room database.
* **PNG Image Export:** Renders high-resolution 1200x1200px framed PNG images with custom metadata and exports them to Android MediaStore (`Pictures/EnochianSigils`) or custom storage locations for physical ritual prints.

### 3. 📝 Sacred Ritual Journal
* **Working Logs:** Record ritual dates, operations, astral conditions, mood, elemental focus, and custom notes.
* **Moon Phase Auto-Tagging:** Automatically associates journal entries with current lunar phases.
* **Search & Filter:** Easily query past workings by tag, date, or elemental orientation.

### 4. 📿 Invocation & Chanting Progress Tracker
* **Repetition Counter:** Track recitation count, duration, and cadence for angelic calls.
* **Audio Tone Synthesizer:** Generate specific elemental frequencies (Air 432 Hz, Fire 528 Hz, Water 396 Hz, Earth 174 Hz) and harmonic intervals.
* **Practice Analytics:** Monitor practice consistency and historical invocation streaks.

### 5. 🌙 Lunar Calendar & Planetary Hours Engine
* **Real-time Lunar Phase:** Accurate moon phase calculation, illumination percentage, and astrological sign context.
* **Planetary Hours Calculator:** Computes precise planetary hours (Sun, Moon, Mercury, Venus, Mars, Jupiter, Saturn) for optimal ritual timing.

### 6. ☁️ GitHub Sync & Secure Vault Backup
* **GitHub Repository Sync:** Sync journal logs, saved sigils, and invocation history directly to a private GitHub repository.
* **Offline Protection:** Built-in Android `ConnectivityManager` pre-checks prevent network exceptions when working offline.
* **JSON Import / Export:** Create encrypted or plain JSON grimoire archives for local offline backup and restoration.

---

## 🏗️ Architecture & Tech Stack

* **Architecture:** MVVM (Model-View-ViewModel) + Clean Data Layer
* **UI Framework:** Jetpack Compose with Material Design 3 (M3) esoteric dark theme
* **Language:** 100% Modern Kotlin
* **Local Persistence:** Room Database with KSP
* **Asynchronous Processing:** Kotlin Coroutines & `StateFlow`
* **Network & API:** Retrofit 2 + OkHttp 4
* **Image Generation:** Android `Canvas` & `MediaStore` API for high-res PNG sigil exports
* **System Integration:** `ConnectivityManager` network awareness & edge-to-edge layout handling

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio:** Ladybug or newer recommended
* **JDK:** Java 17+
* **Minimum SDK:** Android 7.0 (API Level 24)
* **Target SDK:** Android 14 (API Level 34)

### Building the Project

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/your-username/enochian-magic-android.git
   cd enochian-magic-android
   ```

2. **Open in Android Studio:**
   * Select `File` -> `Open...` and select the cloned project directory.

3. **Build APK / Run App:**
   * Run the app directly on an emulator or connected Android device using:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🔐 Permissions & Privacy

* **`INTERNET`**: Required strictly for optional GitHub Vault synchronization and Lunar API updates.
* **`ACCESS_NETWORK_STATE`**: Used by `ConnectivityManager` to check network availability prior to sync operations.
* **`VIBRATE`**: Optional tactile feedback during ritual timer pulses and chanting triggers.
* **Data Privacy:** All ritual journals, sigils, and invocation logs remain 100% local on your device unless you explicitly enable GitHub Sync.

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
