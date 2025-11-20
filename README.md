# Android WebView Integration for Traveln.ai

This project provides a clean, production‑ready WebView integration that loads the Traveln.ai web app and handles authentication, session persistence, and modern UI feedback out of the box.

## Features
- Secure authentication via HTTP headers using `Authorization: Api-Key ...`
- Silent login with `TRAVELN-*` user identity headers
- Session persistence through captured and re-applied cookies
- Progress bar that reflects page loading status
- Back navigation support for both hardware and UI buttons
- Edge-to-edge layout for a modern, full-screen experience
- Optimized `WebSettings` (JavaScript, DOM storage, mixed content, caching)

## Project Structure
- `app/src/main/java/com/example/webview/activities/MainActivity.java`: Entry point launching the WebView activity
- `app/src/main/java/com/example/webview/activities/WebView.java`: Activity wiring views and credentials
- `app/src/main/java/com/example/webview/utils/web_clients/TravelnWebViewClient.java`: Custom `WebViewClient` with headers, cookies, and progress handling
- `app/src/main/AndroidManifest.xml`: Activity declarations and app configuration

## Requirements
- Android Studio
- Android device or emulator (API 26+)
- JDK 8+ (project sets `sourceCompatibility`/`targetCompatibility` to Java 8)

## Setup
1. Clone the repository:
   ```bash
   git clone <your-repository-url>
   ```
2. Open the project in Android Studio.
3. Configure authentication credentials in `app/src/main/java/com/example/webview/activities/WebView.java`:
   ```java
   // Replace placeholders with your real credentials
   private final static String AUTH_API_KEY = "ADD--YOUR--KEY--IN--HERE";
   private final static String AUTH_USER_FIRST_NAME = "John";
   private final static String AUTH_USER_LAST_NAME = "Doe";
   private final static String AUTH_USER_PHONE = "+96170123123";
   private final static String AUTH_USER_EMAIL = "user@traveln.ai";
   private final static String AUTH_USER_PICTURE = "https://app.traveln.ai/static/medias/common/logo.png";
   ```
4. Build and run on a device or emulator.

## How It Works
- `WebView` activity initializes views and attaches the client with credentials (`app/src/main/java/com/example/webview/activities/WebView.java`:60‑71).
- `TravelnWebViewClient.attach(...)` configures `WebSettings`, enables cookies, and loads the base URL with headers (`app/src/main/java/com/example/webview/utils/web_clients/TravelnWebViewClient.java`:80‑136).
- On navigation, headers are re-injected and cookies are preserved (`TravelnWebViewClient.java`:139‑154).
- After each page finishes loading, cookies are captured and synced, and the progress bar is hidden (`TravelnWebViewClient.java`:158‑176).

## Configuration Details
- Base URL is defined in `TravelnWebViewClient` as `https://app.traveln.ai/` (`app/src/main/java/com/example/webview/utils/web_clients/TravelnWebViewClient.java`:42).
- The following identity headers are sent when present:
  - `TRAVELN-FIRST-NAME`, `TRAVELN-LAST-NAME`, `TRAVELN-EMAIL`, `TRAVELN-PHONE`, `TRAVELN-PICTURE` (`TravelnWebViewClient.java`:190‑196).
- Authorization header format: `Authorization: Api-Key <YOUR_API_KEY>` (`TravelnWebViewClient.java`:187‑189).

## Build Config
- `minSdk`: 26, `targetSdk`: 34 (`app/build.gradle`:11‑13)
- Key dependencies:
  - `androidx.appcompat:appcompat` (`gradle/libs.versions.toml`:15)
  - `com.google.android.material:material` (`gradle/libs.versions.toml`:16)
  - `androidx.activity:activity` (`gradle/libs.versions.toml`:17)
  - `androidx.constraintlayout:constraintlayout` (`gradle/libs.versions.toml`:18)

## Usage Example
Attach the client in your activity to any `WebView` and `ProgressBar`:
```java
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
```

## Security Notes
- Do not place secrets in URLs or query parameters; headers are used instead.
- Keep production API keys out of source control. Prefer using secure storage or build-time injection.
- Review mixed content settings if you serve non-HTTPS assets.

## Troubleshooting
- Blank page: verify network connectivity and a valid API key.
- Not logged in: confirm identity headers and backend acceptance.
- Cookies not persisting: ensure cookies are enabled on the device.
- File chooser issues: check platform permissions and `WebChromeClient` handling.

## License
- Proprietary or internal use unless otherwise specified by the repository owner.