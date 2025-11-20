package com.example.webview.activities;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webview.R;
import com.example.webview.utils.web_clients.TravelnWebViewClient;
import com.google.android.material.button.MaterialButton;

public class WebView extends AppCompatActivity {

    // Authentication Credentials
    private final static String AUTH_API_KEY = "ADD--YOUR--KEY--IN--HERE";
    private final static String AUTH_USER_FIRST_NAME = "John";
    private final static String AUTH_USER_LAST_NAME = "Doe";
    private final static String AUTH_USER_PHONE = "+96170123123";
    private final static String AUTH_USER_EMAIL = "user@traveln.ai";
    private final static String AUTH_USER_PICTURE = "https://app.traveln.ai/static/medias/common/logo.png";

    // Views
    private MaterialButton mBtnBack;
    private android.webkit.WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_view);
        initView();
        initWebView();

        // Handle hardware/system back button in modern way
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    // If no history left, finish activity
                    finish();
                }
            }
        });

        // Handle your custom UI back button too
        mBtnBack.setOnClickListener(v -> {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        });
    }

    private void initWebView() {
        TravelnWebViewClient.attach(
                this,
                mWebView,
                mProgressBar,
                AUTH_API_KEY,
                AUTH_USER_FIRST_NAME,
                AUTH_USER_LAST_NAME,
                AUTH_USER_PICTURE,
                AUTH_USER_PHONE,
                AUTH_USER_EMAIL
        );
    }

    private void initView() {
        mBtnBack = findViewById(R.id.webview_btn_back);
        mWebView = findViewById(R.id.webview_webview);
        mProgressBar = findViewById(R.id.webview_progress_bar);
    }
}
