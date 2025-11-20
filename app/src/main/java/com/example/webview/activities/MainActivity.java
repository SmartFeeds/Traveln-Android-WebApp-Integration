package com.example.webview.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webview.R;

@SuppressLint({"SetJavaScriptEnabled", "FieldCanBeLocal"})
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        RelativeLayout mRootView = findViewById(R.id.main_root);

        mRootView.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, WebView.class));
        });
    }
}
