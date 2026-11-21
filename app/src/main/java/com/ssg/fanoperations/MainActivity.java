
package com.ssg.fanoperations;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

public class MainActivity extends Activity {

    // Banner-free Common Portal hosted on GitHub Pages
    private static final String HOME =
        "https://qmcompressor.github.io/SSG-Fan-Operations/";

    // APK-only presentation changes. The six Apps Script sources remain untouched.
    private static final String MOBILE_CSS =
        "@media(max-width:760px){" +
        "html,body{max-width:100%!important;overflow-x:hidden!important}" +
        "main{padding:8px 8px 55px!important}" +
        ".mobilehead{padding:7px 11px!important;min-height:48px!important}" +
        ".mobilehead button{min-width:42px!important;min-height:42px!important}" +
        ".toolbar{padding:8px!important;gap:7px!important;margin-bottom:8px!important}" +
        ".toolbar select,.toolbar input{min-width:0!important;max-width:100%!important}" +
        ".hero,.eff-hero{padding:11px 13px!important;margin-bottom:8px!important}" +
        ".hero h1,.eff-hero h1{font-size:17px!important;line-height:1.25!important}" +
        ".hero p,.eff-hero p{font-size:11px!important;line-height:1.35!important}" +
        ".cards,.eff-kpis{gap:8px!important;margin-top:8px!important}" +
        ".kpi,.eff-kpi{min-height:88px!important;padding:10px!important;border-radius:12px!important}" +
        ".kpi strong,.eff-kpi b{font-size:clamp(18px,5.2vw,25px)!important;overflow-wrap:anywhere!important}" +
        ".stats,.insights,.twocol,.eff-grid{gap:8px!important;margin-top:8px!important}" +
        ".stat,.panel{padding:10px!important}" +
        ".sectiontitle{margin:10px 0 7px!important;font-size:15px!important}" +
        ".imm-extra .imm-head{margin:10px 0 7px!important;padding:10px!important}" +
        ".imm-extra .imm-panel,.imm-extra .imm-card{padding:10px!important}" +
        ".eff-filters{padding:9px!important;gap:7px!important}" +
        ".chartbox,.eff-chart{height:220px!important}" +
        ".tablewrap,.eff-table,.imm-table{max-width:100%!important}" +
        "button{touch-action:manipulation!important;-webkit-tap-highlight-color:transparent}" +
        ".primary,.toolbar button{min-height:42px!important;border-radius:10px!important}" +
        "}";

    private void applyAndroidLayout(WebView view, String url) {
        if (url == null) return;
        android.net.Uri uri = android.net.Uri.parse(url);
        String host = uri.getHost();
        if (host == null || !(host.equalsIgnoreCase("script.google.com") ||
                host.equalsIgnoreCase("script.googleusercontent.com"))) return;
        String js = "(function(){try{" +
            "var title=(document.title||'').toLowerCase();" +
            "var known=['injection molding','grill factory','assembly','crushing','iqc','meter'].some(function(x){return title.indexOf(x)>=0});" +
            "if(!known||!document.head||document.getElementById('ssg-android-compact-style'))return;" +
            "var style=document.createElement('style');style.id='ssg-android-compact-style';" +
            "style.textContent=" + org.json.JSONObject.quote(MOBILE_CSS) + ";document.head.appendChild(style);" +
            "}catch(e){}})();";
        view.evaluateJavascript(js, null);
    }

    private WebView web;
    private ValueCallback<Uri[]> uploadCallback;
    private static final int PICK_FILE = 101;


    // Best-effort suppression of the Apps Script author-information strip inside
    // this Android WebView only. Never affects desktop browsers or sign-in pages.
    private void hideAppsScriptInfoStrip(WebView view) {
        String url = view.getUrl();
        if (url == null) return;
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (!"https".equalsIgnoreCase(uri.getScheme()) ||
            host == null ||
            !("script.google.com".equalsIgnoreCase(host) ||
              "script.googleusercontent.com".equalsIgnoreCase(host))) return;

        String script = "(function(){"
            + "if(window.__ssgInfoStripObserver)return;"
            + "function hide(){"
            + "var phrases=['This application was created by a Google Apps Script user',"
            + "'This app was created by a Google Apps Script user',"
            + "'এই অ্যাপ্লিকেশন কোনও Google Apps লিপি ব্যবহারকারী দ্বারা তৈরি হয়েছে',"
            + "'এই অ্যাপ্লিকেশনটি Google Apps Script ব্যবহারকারী দ্বারা তৈরি'];"
            + "var nodes=document.querySelectorAll('div,section,aside');"
            + "for(var i=0;i<nodes.length;i++){var el=nodes[i];"
            + "if(el.children.length>12)continue;"
            + "var t=(el.innerText||'').trim();"
            + "if(t.length>450||!phrases.some(function(p){return t.indexOf(p)!==-1}))continue;"
            + "if(el.querySelector('input,textarea,form'))continue;"
            + "var rect=el.getBoundingClientRect();"
            + "if(rect.height>140||rect.top>180||rect.bottom<0)continue;"
            + "var parent=el;"
            + "while(parent.parentElement&&parent.parentElement!==document.body){"
            + "var pr=parent.parentElement.getBoundingClientRect();"
            + "if(pr.height>145||pr.top>180)break;parent=parent.parentElement;}"
            + "parent.style.setProperty('display','none','important');"
            + "break;}"
            + "}"
            + "hide();"
            + "window.__ssgInfoStripObserver=new MutationObserver(hide);"
            + "window.__ssgInfoStripObserver.observe(document.documentElement,"
            + "{childList:true,subtree:true});"
            + "})();";
        view.evaluateJavascript(script, null);
    }


    // Best-effort: hide the Google Apps Script information notice only
    // inside this Android WebView. No changes to the hosted web apps.
    private void hideAppsScriptNotice() {
        if (web == null) return;
        String js = "(function(){"
            + "if(!/\\.google\\.(com|usercontent\\.com)/.test(location.hostname))return;"
            + "var selectors=['.goog-apps-script-info-bar','.apps-script-banner'];"
            + "var style=document.getElementById('ssg-webview-banner-style');"
            + "if(!style){style=document.createElement('style');style.id='ssg-webview-banner-style';"
            + "style.textContent=selectors.join(',')+'{display:none!important}';"
            + "(document.head||document.documentElement).appendChild(style);}"
            + "function clean(){document.querySelectorAll(selectors.join(',')).forEach(function(el){el.style.setProperty('display','none','important');});}"
            + "clean();if(!window.__ssgBannerObserver){window.__ssgBannerObserver=new MutationObserver(clean);"
            + "window.__ssgBannerObserver.observe(document.documentElement,{childList:true,subtree:true});}"
            + "})();";
        web.evaluateJavascript(js, null);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();

        // Identify this Android wrapper without changing desktop browser layouts.
        // Keep standard user agent for Google authentication compatibility.

        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(true);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setSupportMultipleWindows(false);
        s.setBuiltInZoomControls(false);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance()
            .setAcceptThirdPartyCookies(web, true);

        web.setWebViewClient(new WebViewClient() {\n            @Override public void onPageFinished(WebView view, String url) {\n                super.onPageFinished(view, url);\n                applyAndroidLayout(view, url);\n            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideAppsScriptInfoStrip(view);
                hideAppsScriptNotice();
                // Persist cookies that the server marks as persistent.
                CookieManager.getInstance().flush();
            }


            @Override
            public boolean shouldOverrideUrlLoading(
                WebView view,
                android.webkit.WebResourceRequest request
            ) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                String host = uri.getHost();

                if ("https".equalsIgnoreCase(scheme)
                        && host != null) {

                    // GitHub Pages Common Portal
                    if ("qmcompressor.github.io".equalsIgnoreCase(host)) {
                        return false;
                    }

                    // Google Apps Script modules
                    if ("script.google.com".equalsIgnoreCase(host)
                            || "script.googleusercontent.com".equalsIgnoreCase(host)
                            || "accounts.google.com".equalsIgnoreCase(host)) {
                        return false;
                    }
                }

                // Other links open in external browser
                try {
                    startActivity(
                        new Intent(Intent.ACTION_VIEW, uri)
                    );
                } catch (Exception ignored) {
                }

                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(
                WebView view,
                ValueCallback<Uri[]> callback,
                FileChooserParams params
            ) {

                if (uploadCallback != null) {
                    uploadCallback.onReceiveValue(null);
                }

                uploadCallback = callback;

                try {
                    startActivityForResult(
                        params.createIntent(),
                        PICK_FILE
                    );
                    return true;

                } catch (Exception e) {
                    uploadCallback = null;
                    return false;
                }
            }
        });

        if (state == null) {
            web.loadUrl(HOME);
        } else {
            web.restoreState(state);
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE && uploadCallback != null) {

            uploadCallback.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            );

            uploadCallback = null;
        }
    }

    @Override
    protected void onPause() {
        CookieManager.getInstance().flush();
        super.onPause();
    }

    @Override
    protected void onStop() {
        CookieManager.getInstance().flush();
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        web.saveState(state);
        super.onSaveInstanceState(state);
    }
}
