/** Use this in a separately hosted COPY of Grill Factory Index.html only.
 * Replace the google.script.run transport inside api(name,...args) with:
 * const value = await gfBridgeCall(name,args);
 * Keep the existing caching and invalidation logic.
 */
const GF_BRIDGE_URL = 'REPLACE_WITH_CLOUDFLARE_WORKER_URL';
async function gfBridgeCall(name,args) {
  if (!GF_BRIDGE_URL.startsWith('https://')) throw Error('Staging bridge not configured');
  const response = await fetch(GF_BRIDGE_URL,{
    method:'POST',
    headers:{'Content-Type':'application/json'},
    body:JSON.stringify({method:name,args}),
    cache:'no-store'
  });
  if (!response.ok) throw Error('Network error ('+response.status+')');
  const data = await response.json();
  if (!data.ok) throw Error(data.error||'Request failed');
  return data.value;
}
