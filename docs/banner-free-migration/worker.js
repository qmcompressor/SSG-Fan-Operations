/**
 * Staging-only transport for Grill Factory V1.0.1.
 * Deploy as a Cloudflare Worker. Configure secrets:
 * GAS_URL = Apps Script /exec URL
 * BRIDGE_SECRET = same secret in Apps Script Script Properties
 * ALLOWED_ORIGIN = exact staging frontend origin
 *
 * This transport does not replace the application's user permissions.
 * The Apps Script backend must validate the session on every privileged call.
 */
export default {
  async fetch(request, env) {
    const origin = request.headers.get("Origin") || "";
    const allowed = env.ALLOWED_ORIGIN || "";
    const headers = {
      "Access-Control-Allow-Origin": allowed,
      "Vary": "Origin",
      "Access-Control-Allow-Methods": "POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
      "Cache-Control": "no-store",
      "X-Content-Type-Options": "nosniff"
    };
    if (!allowed || origin !== allowed) return new Response("Origin not allowed", {status:403});
    if (request.method === "OPTIONS") return new Response(null, {status:204, headers});
    if (request.method !== "POST") return new Response("Method not allowed", {status:405, headers});
    if (!env.GAS_URL || !env.BRIDGE_SECRET || !env.GAS_URL.startsWith("https://script.google.com/macros/s/")) {
      return new Response("Bridge is not configured", {status:503, headers});
    }
    if (Number(request.headers.get("Content-Length") || 0) > 262144) return new Response("Payload too large", {status:413, headers});
    let payload;
    try { payload = await request.json(); } catch { return new Response("Invalid JSON", {status:400, headers}); }
    if (!payload || typeof payload.method !== "string" || !Array.isArray(payload.args) || JSON.stringify(payload).length > 262144) {
      return new Response("Invalid request", {status:400, headers});
    }
    // Defense in depth; the Apps Script bridge independently enforces the same list.
    const allowedMethods = new Set([
      "bootstrap","getAccess","getMyEntries","getPgPerformance","getReport",
      "getStockCatalog","getStockLedger","getStockReport","gfAdminResetPassword",
      "gfAdminReview","gfAuthAdminData","gfChangeMyPassword",
      "gfLatestProductionMonth","gfPasswordRequest","gfRegistrationRequest",
      "login","logout","manageAccess","saveEntry","saveProductionBulk","saveStockBatch"
    ]);
    if (!allowedMethods.has(payload.method)) return new Response("Method not allowed", {status:403, headers});
    try {
      const upstream = await fetch(env.GAS_URL, {
        method:"POST", redirect:"follow",
        headers:{"Content-Type":"text/plain;charset=UTF-8"},
        body:JSON.stringify({secret:env.BRIDGE_SECRET,method:payload.method,args:payload.args})
      });
      const body = await upstream.text();
      if (!upstream.ok || body.length > 5000000) throw new Error("Backend transport failed");
      const result = JSON.parse(body);
      return new Response(JSON.stringify(result), {status:200,headers:{...headers,"Content-Type":"application/json;charset=UTF-8"}});
    } catch {
      return new Response(JSON.stringify({ok:false,error:"Backend unavailable"}),{status:502,headers:{...headers,"Content-Type":"application/json;charset=UTF-8"}});
    }
  }
};
