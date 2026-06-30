# Glyph Dice — a Glyph Toy for the Nothing Phone (4a) Pro

A tiny, self-contained Android app that adds a **Dice** toy to your phone's
Glyph Matrix. Long-press the Glyph button to roll a die (1–6); the result shows
as classic pips on the 13×13 LED matrix, and it supports the Always-On Display.

This is a standalone Android project. It is **not** part of bitchat — it just
lives in this branch so it's easy to hand off.

> Status: written against the documented Glyph Matrix SDK API, **not yet
> compiled or tested on hardware** (the SDK `.aar` is proprietary and must be
> added by you — see step 2). Expect to possibly nudge a method signature or two
> the first time you build; Android Studio will point at anything that needs it.

## What's here

```
glyph-dice/
├── app/
│   ├── build.gradle.kts                 # module config (minSdk 34 = Android 14)
│   ├── libs/                            # <-- put GlyphMatrixSDK.aar here
│   └── src/main/
│       ├── AndroidManifest.xml          # registers the toy service
│       ├── java/com/example/glyphdice/
│       │   ├── DiceGlyphToyService.kt   # the toy: handles long-press, draws
│       │   ├── DiceRenderer.kt          # draws a dice face onto a bitmap
│       │   └── MainActivity.kt          # companion screen w/ "Add toy" button
│       └── res/                         # strings, layout, theme, toy icon
├── build.gradle.kts                     # top-level plugin versions
└── settings.gradle.kts
```

## Prerequisites

- **Nothing Phone (4a) Pro** on system version **20250801+** (basic matrix
  control). The in-app "Add toy" button needs **20250829+**; otherwise enable the
  toy manually in Settings.
- **Android Studio** (bundles JDK + Android SDK) on your computer.
- A **USB cable** and **USB debugging** enabled on the phone
  (Settings → About phone → tap *Build number* 7×, then Developer Options →
  USB debugging).

## Setup & run

1. **Open** the `glyph-dice/` folder in Android Studio. Let it sync Gradle (it
   will create the Gradle wrapper automatically).
2. **Download the SDK**: grab `GlyphMatrixSDK.aar` from the
   [Nothing Developer Programme repo](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit)
   and drop it into `app/libs/`. (It's gitignored on purpose — proprietary.)
3. **Plug in** your phone, authorize the USB debugging prompt.
4. Press **Run ▶**. The companion app installs and opens.
5. Tap **"Add Dice to Glyph Toys"** (or go to *Settings → Glyph Interface →
   Glyph Toys*) and drag **Dice** from *Disabled* into the active toys.
6. **Long-press the Glyph button** on the back of the phone to roll. 🎲

## How it works (the short version)

- `DiceGlyphToyService` is an Android `Service` the system recognizes because of
  the `com.nothing.glyph.TOY` intent-filter in the manifest.
- On connect it calls `GlyphMatrixManager.register(Glyph.DEVICE_25111p)` — the
  device code for the (4a) Pro.
- A long press arrives as a `GlyphToy.EVENT_CHANGE` message; we pick a random
  1–6, play a brief "tumble", and draw the final face.
- `DiceRenderer` paints white pips on a black 13×13 bitmap; the SDK maps pixel
  brightness to LED brightness.
- The last roll is saved in SharedPreferences so the Always-On Display
  (`EVENT_AOD`) can redraw it.

## Ideas to extend it

- Swap pips for the **digit** using `GlyphMatrixObject.Builder().setText()`.
- Roll **two dice** with a layered frame (`addTop` + `addMid`).
- Add a **vibration** on the final landing via the phone's vibrator.
- Make a **coin flip** or **magic-8-ball** variant from the same skeleton.
