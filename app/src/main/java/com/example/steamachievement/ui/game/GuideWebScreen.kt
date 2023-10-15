package com.example.steamachievement.ui.game

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun GuideWebScreen(link: String) {
    AndroidView(factory = {
        WebView(it).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(link)
        }
    })
}