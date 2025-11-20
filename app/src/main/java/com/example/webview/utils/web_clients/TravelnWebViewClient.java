package com.example.webview.utils.web_clients;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * TravelnWebViewClient
 * --------------------
 * Plug & play WebViewClient for Android apps with built-in:
 *  - Auto WebView settings (JS, DOM storage, cache, mixed content)
 *  - Cookie + session persistence
 *  - Sends Api-Key + user identity in HTTP headers
 *  - ProgressBar handling
 */
public class TravelnWebViewClient extends WebViewClient {
    private final static String TAG = "TravelnWebViewClient";

    @Nullable
    private final ProgressBar progressBar;

    // Base URL
    private final static String BASE_URL = "https://app.traveln.ai/";

    private final String apiKey;
    private final String firstName, lastName, picture, phone, email;

    // Cookies
    private String lastCookies = "";

    private TravelnWebViewClient(@Nullable ProgressBar progressBar,
                                 @NonNull String apiKey,
                                 @Nullable String firstName,
                                 @Nullable String lastName,
                                 @Nullable String picture,
                                 @Nullable String phone,
                                 @Nullable String email) {
        this.progressBar = progressBar;
        this.apiKey = apiKey;
        this.firstName = firstName;
        this.lastName = lastName;
        this.picture = picture;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Attach TravelnWebViewClient to a WebView and auto-configure everything.
     */
    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    public static void attach(@NonNull Context context,
                              @NonNull WebView webView,
                              @Nullable ProgressBar progressBar,
                              @NonNull String apiKey,
                              @Nullable String firstName,
                              @Nullable String lastName,
                              @Nullable String picture,
                              @Nullable String phone,
                              @Nullable String email) {

        // --- Configure WebView settings ---
        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // --- Enable cookies ---
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        // --- Attach custom client ---
        webView.setWebViewClient(
                new TravelnWebViewClient(progressBar, apiKey, firstName, lastName, picture, phone, email)
        );

        // --- Attach WebChromeClient ---
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "JS Alert: " + message);
                result.confirm();
                return true;
            }

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                Log.d(TAG, "File chooser triggered");
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });

        // --- Add headers instead of query params ---
        Map<String, String> headers = buildHeaders(apiKey, firstName, lastName, picture, phone, email);

        // Load base URL with headers
        webView.loadUrl(BASE_URL, headers);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String targetUrl = request.getUrl().toString();

        // Always inject headers (instead of query params)
        Map<String, String> headers = buildHeaders(apiKey, firstName, lastName, picture, phone, email);

        // Preserve cookies
        if (lastCookies != null && !lastCookies.isEmpty()) {
            CookieManager.getInstance().setCookie(targetUrl, lastCookies);
        }

        Log.d(TAG, "Redirecting to: " + targetUrl + " with Authorization + identity headers");
        view.loadUrl(targetUrl, headers);

        return true;
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        // Capture cookies
        lastCookies = CookieManager.getInstance().getCookie(url);
        Log.d(TAG, "Cookies for " + url + ": " + lastCookies);

        // Sync cookies
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }

        // Hide progress bar
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    // --- Helper to build headers ---
    private static Map<String, String> buildHeaders(String apiKey,
                                                    String firstName,
                                                    String lastName,
                                                    String picture,
                                                    String phone,
                                                    String email) {
        Map<String, String> headers = new HashMap<>();

        // Authorization
        headers.put("Authorization", "Api-Key " + apiKey);

        // User identity headers (must match backend SilentLoginRequestHeadersKeys)
        if (firstName != null) headers.put("TRAVELN-FIRST-NAME", firstName);
        if (lastName != null)  headers.put("TRAVELN-LAST-NAME", lastName);
        if (email != null)     headers.put("TRAVELN-EMAIL", email);
        if (phone != null)     headers.put("TRAVELN-PHONE", phone);
        if (picture != null)   headers.put("TRAVELN-PICTURE", picture);

        return headers;
    }
}
