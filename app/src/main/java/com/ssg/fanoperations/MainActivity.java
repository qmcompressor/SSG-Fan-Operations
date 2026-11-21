
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

    private WebView web;
    private ValueCallback<Uri[]> uploadCallback;
    private static final int PICK_FILE = 101;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();

        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(true);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setSupportMultipleWindows(false);
        s.setBuiltInZoomControls(false);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance()
            .setAcceptThirdPartyCookies(web, true);

        web.setWebViewClient(new WebViewClient() {

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
