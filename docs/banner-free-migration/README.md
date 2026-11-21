# Grill Factory Management V1.0.1: Banner-free mobile migration

Status: **Design / investigation only. Not deployed or functional yet.**

## Existing architecture
- Common Android WebView loads a GitHub Pages common portal and opens Apps Script `/exec` URLs.
- Grill Factory `Index.html` uses `google.script.run` to call functions in `Code.gs`.
- Google's Apps Script security information strip belongs to the Apps Script host, not to the app HTML.

## Safe migration strategy
1. Preserve existing production `/exec`, Google Sheet, user table, password hashes, and app version V1.0.1.
2. Host a copy of the frontend under an isolated test URL, not the production portal.
3. Replace **all** `google.script.run` calls with an authenticated API adapter. Do not expose privileged Apps Script functions or Sheets API keys to the client. Do not use unauthenticated public endpoints for user administration or writes.
4. Use a trusted backend endpoint with server-side authorization and session validation for each read/write operation. Verify CORS, CSRF protection, audit trail, file uploads, and session expiry.
5. Test login, guest, registration, forgot password, admin approval, stock transactions, all reports, and permission boundaries on Android.
6. Only after acceptance testing, change the Grill Factory link in the Common Portal and build the APK. Other modules remain unchanged.

## Important
The existing Android JavaScript attempts to hide the banner are best-effort only and cannot reliably control a Google-owned frame. This document is not a claim that the banner has been removed.
