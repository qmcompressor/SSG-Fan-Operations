# Grill Factory V1.0.1 staging deployment checklist

**Not production ready.** Do not change the Common Portal's Grill Factory link until all checks pass.

## Current status
- [x] Existing GitHub Android Common APK project identified.
- [x] Original Grill Factory Code.gs and Index.html identified in user file library.
- [x] Existing 20 frontend RPC methods enumerated.
- [x] Staging-only Apps Script bridge scaffold added.
- [x] Cloudflare Worker transport scaffold added.
- [x] External frontend transport adapter scaffold added.
- [ ] Review backend functions for authorization, payload validation and sensitive error leakage.
- [ ] Create a staging copy of the full Grill Factory HTML and wire the adapter.
- [ ] Deploy a **new test** Apps Script deployment with the bridge; set GF_BRIDGE_SECRET in Script Properties (never commit it).
- [ ] Deploy Cloudflare Worker with GAS_URL, BRIDGE_SECRET and ALLOWED_ORIGIN as secrets/config.
- [ ] Verify the Worker can call the test deployment; Apps Script ContentService redirects must be followed.
- [ ] Configure frontend hosting and test on a real Android device.
- [ ] Security test: invalid credentials, guest cannot write, editor restricted, admin-only methods, brute-force controls, cross-origin requests, session expiry, file uploads, audit logs.
- [ ] Functional test: dashboard, reports, production, stock, admin, registration and password recovery.
- [ ] Only after sign-off, update **only** Grill Factory card URL and build a test APK.

## Operator action needed
A maintainer with edit access to the **original Google Apps Script project** must install the staging bridge and create a separate deployment. GitHub repository access alone cannot edit that Apps Script project.

## Security warnings
- The bridge secret authenticates the proxy, not individual users. Existing per-user session/role enforcement must be audited.
- A browser Origin check is not authentication. Add abuse/rate limiting and monitor logs before employee rollout.
- Do not deploy the scaffolds without testing error handling, CSRF assumptions and all exposed RPC methods.
- Never put a Script Property secret, Google API key or service-account credential in GitHub Pages or client JavaScript.
- Version remains Grill Factory V1.0.1.
