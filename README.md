# Spam Call Blocker (Android)

Blocks incoming calls whose number starts with a prefix you set (e.g. `+1400`) before the phone rings.
Prefixes are stored in a SQLite database on the phone itself.

## Get the APK (no Android Studio needed)
1. Create a new GitHub repository and upload everything in this folder (keep the `.github` folder).
2. Open the repo's **Actions** tab -> **Build APK** -> wait for the green check (about 3-5 minutes).
3. Open the finished run, download the **SpamCallBlocker-debug-apk** artifact, unzip it, and you get `app-debug.apk`.
4. Copy the APK to your phone, tap it, and allow "Install unknown apps" when asked.

## Or build with Android Studio
Open this folder in Android Studio, let Gradle sync, then Build -> Build APK(s). Or run `gradle assembleDebug` with the Android SDK installed.

## Use it
1. Open the app and tap **Enable call blocking**; accept the "Spam Call Blocker as call screening app" dialog (Android 10+).
2. Add prefixes such as `+1400`.
3. Matching incoming calls are rejected before they ring.

## Notes
- Private/withheld numbers are allowed through.
- Some carriers deliver numbers without `+` or in local format; add both forms if needed.
- Blocked calls remain in the call log.
- The APK is a debug build (signed with the standard debug key), fine for installing on your own phone.
