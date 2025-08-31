Insightra (Android)
===================

Kotlin + Jetpack Compose app to compare 2–3 products using OpenAI.
Primary color: #0088CC. App ID: com.insightra.app

Features
- Settings: enter OpenAI API key, verify, save encrypted, edit via pencil icon
- Home: last 2 comparisons “N Products Compared on DATE”, open saved comparisons
- Wizard: add 2–3 items via Camera or Gallery; vision parses Brand/Name/Size; edit and save; compare
- Comparison: dynamic table, ratings/review counts (from public sources), pros/cons, and recommendations; cached locally

Stack
- Compose Material 3 + Material Icons
- Hilt DI, Room (history), EncryptedSharedPreferences (API key)
- CameraX + Android Photo Picker, Coil
- OpenAI: GPT-4o (multipart image upload) and 4o-mini (text)

Setup
1) Open in Android Studio Hedgehog+.
2) Build.
3) Run on emulator/device (API 24+).
4) In Settings, paste your OpenAI API key and Verify & Save.

Notes
- Images are uploaded to OpenAI only for extraction at capture time.
- Saved comparisons are cached locally; reopening does not call AI again.
