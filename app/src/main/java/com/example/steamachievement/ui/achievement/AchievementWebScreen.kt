package com.example.steamachievement.ui.achievement

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AchievementWebScreen(url: String) {
    AndroidView(
        factory = {
            WebView(it).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, urlIn: String?) {
                        if (url.contains("https://www.trueachievements.com/")) {
                            evaluateJavascript(
                                "document.getElementsByTagName('header')[0].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByTagName('aside')[0].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByTagName('footer')[0].style.display='none';",
                                null
                            )
                            for (i in 0 until 8) {
                                evaluateJavascript(
                                    "document.getElementsByTagName('main')[0].children[$i].style.display='none';",
                                    null
                                )
                            }
                        } else if (url.contains(Regex("https:\\/\\/www\\.playstationtrophies\\.org\\/game\\/.+\\/trophy\\/.+"))) {
                            evaluateJavascript(
                                "document.getElementsByClassName('col-md-4')[0].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByClassName('col-12 col-md-6 col-sm-12')[0].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByClassName('col-12 col-md-6 col-sm-12')[1].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByClassName('breadcrumbs')[0].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByClassName('section__header')[0].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementById('vnt-lb-a').style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementById('MobileTakeover').style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByTagName('footer')[0].style.display='none';",
                                null
                            )
                            evaluateJavascript(
                                "document.getElementsByTagName('header')[0].style.display='none';",
                                null
                            )
                        }
                    }
                }
                settings.javaScriptEnabled = true
                settings.builtInZoomControls = true
                loadUrl(url)
            }
        }
    )
}