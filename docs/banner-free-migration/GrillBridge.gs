/**
 * Staging-only Grill Factory V1.0.1 HTTP bridge.
 * Paste into the SAME Apps Script project as the existing Code.gs.
 * Set Script Property GF_BRIDGE_SECRET to a long random secret.
 * Deploy a NEW test Web App deployment. Do not overwrite the production deployment.
 *
 * IMPORTANT: Existing functions must enforce their original per-user token,
 * role and permission checks. The bridge secret authenticates the proxy,
 * not the individual employee.
 */
const GF_BRIDGE_METHODS_ = Object.freeze([
  'bootstrap','getAccess','getMyEntries','getPgPerformance','getReport',
  'getStockCatalog','getStockLedger','getStockReport','gfAdminResetPassword',
  'gfAdminReview','gfAuthAdminData','gfChangeMyPassword',
  'gfLatestProductionMonth','gfPasswordRequest','gfRegistrationRequest',
  'login','logout','manageAccess','saveEntry','saveProductionBulk','saveStockBatch'
]);
function doPost(e) {
  try {
    const raw = e && e.postData && e.postData.contents;
    if (!raw || raw.length > 262144) throw Error('Invalid request');
    const p = JSON.parse(raw);
    const secret = PropertiesService.getScriptProperties().getProperty('GF_BRIDGE_SECRET');
    if (!secret || secret.length < 32 || p.secret !== secret) throw Error('Unauthorized');
    if (!GF_BRIDGE_METHODS_.includes(p.method) || !Array.isArray(p.args)) throw Error('Forbidden method');
    // No dynamic property lookup on globalThis. Dispatch explicit, audited functions.
    let result;
    switch (p.method) {
      case 'bootstrap': result=bootstrap.apply(null,p.args);break;
      case 'getAccess': result=getAccess.apply(null,p.args);break;
      case 'getMyEntries': result=getMyEntries.apply(null,p.args);break;
      case 'getPgPerformance': result=getPgPerformance.apply(null,p.args);break;
      case 'getReport': result=getReport.apply(null,p.args);break;
      case 'getStockCatalog': result=getStockCatalog.apply(null,p.args);break;
      case 'getStockLedger': result=getStockLedger.apply(null,p.args);break;
      case 'getStockReport': result=getStockReport.apply(null,p.args);break;
      case 'gfAdminResetPassword': result=gfAdminResetPassword.apply(null,p.args);break;
      case 'gfAdminReview': result=gfAdminReview.apply(null,p.args);break;
      case 'gfAuthAdminData': result=gfAuthAdminData.apply(null,p.args);break;
      case 'gfChangeMyPassword': result=gfChangeMyPassword.apply(null,p.args);break;
      case 'gfLatestProductionMonth': result=gfLatestProductionMonth.apply(null,p.args);break;
      case 'gfPasswordRequest': result=gfPasswordRequest.apply(null,p.args);break;
      case 'gfRegistrationRequest': result=gfRegistrationRequest.apply(null,p.args);break;
      case 'login': result=login.apply(null,p.args);break;
      case 'logout': result=logout.apply(null,p.args);break;
      case 'manageAccess': result=manageAccess.apply(null,p.args);break;
      case 'saveEntry': result=saveEntry.apply(null,p.args);break;
      case 'saveProductionBulk': result=saveProductionBulk.apply(null,p.args);break;
      case 'saveStockBatch': result=saveStockBatch.apply(null,p.args);break;
      default: throw Error('Forbidden method');
    }
    return ContentService.createTextOutput(JSON.stringify({ok:true,value:result}))
      .setMimeType(ContentService.MimeType.JSON);
  } catch (err) {
    // Avoid returning secrets, stack traces or credentials to clients.
    const msg = /Unauthorized|Forbidden method|Invalid request/.test(String(err.message)) ? String(err.message) : 'Request failed';
    return ContentService.createTextOutput(JSON.stringify({ok:false,error:msg}))
      .setMimeType(ContentService.MimeType.JSON);
  }
}
