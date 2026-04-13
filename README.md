<div align="center">
  <img src="logo.svg" width="200" height="200" alt="Aura Logo">
  <h1>Aura Launcher</h1>
  <p><b>A minimalist, hyper-modern Android Launcher featuring a 3D animated mesh aura, physics-based kinetic scrolling, and secure work-profile isolation.</b></p>
  
  [![Release](https://img.shields.io/github/v/release/chetangoswami/AuraLauncher?style=for-the-badge&color=00E5FF)](https://github.com/chetangoswami/AuraLauncher/releases)
  [![License](https://img.shields.io/github/license/chetangoswami/AuraLauncher?style=for-the-badge&color=D500F9)](LICENSE)
</div>

---

## ✨ Features

- **Kinetic Physics Engine**: Scroll vertically through your apps, rendered natively on an infinitely spinning 3D cylinder. Apps naturally decelerate with lifelike momentum.
- **Micro-Dock**: Immediate quick-launch access to universally critical endpoints (Phone, Messages, Web, Camera) sitting quietly at the bottom of the screen.
- **Mesh Background Renderer**: A GPU-accelerated background that breathes in response to your state. It elegantly switches between an ethereal cyan/purple geometry for Personal and a structured amber/gold mesh for Work.
- **Work Profile Native**: Native Android `UserManager` integration to seamlessly split apps into Personal and Secure Work domains without intrusive multi-launch steps.
- **Ultra-Minimalist**: Pitch-black themes designed to save OLED battery life and keep digital distractions precisely minimal.

## 🚀 Installation

You can get the latest stable version of Aura Launcher straight from the GitHub Releases:

1. Go to the [Releases page](https://github.com/chetangoswami/AuraLauncher/releases).
2. Download the latest `AuraLauncher.apk`.
3. Open the file on your Android device to install. 
4. Set **Aura** as your default home app when prompted.

## 🛠️ Building From Source

This project leverages the standard Gradle compile tooling.
Clone the repository and build it natively using Android Studio or your CLI:

```bash
git clone https://github.com/chetangoswami/AuraLauncher.git
cd AuraLauncher
./gradlew assembleDebug
```

## 📸 Overview

The core paradigm is to strip away the complex grid.

You are greeted with a beautifully minimalist screen holding only a massive structural clock, segmented profiles, a quick dock, and a cylinder of apps. Sliding your finger anywhere up or down perfectly tracks the rotation of your installed applications around this virtual wheel.

<br>

<div align="center">
  <sub>Built with ❤️ by Chetan Goswami</sub>
</div>
