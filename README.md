# SSG Fan Operations Android App

Android WebView wrapper for the provided Google Apps Script URL.

## Build APK with GitHub
1. Create an empty GitHub repository (or use an existing empty repository).
2. Upload all project files preserving folder paths, including `.github/workflows/build-apk.yml`.
3. Go to **Actions > Build Android APK > Run workflow**.
4. When the build finishes, open the run and download **SSG-Fan-Operations-APK** under Artifacts.
5. Extract the ZIP and install `app-debug.apk` on your Android phone.

The debug APK is for testing, not a production-signed Play Store release. Verify login, Google Apps Script redirects, file upload, and back navigation on your phone. Internet access is required.
