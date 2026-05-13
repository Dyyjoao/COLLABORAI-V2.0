package com.collaboraai.app

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.collaboraai.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val COLLABORAAI_URL = "https://app.collaborai.io/"
        const val EXTRA_ASSISTANT_MESSAGE = "extra_assistant_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWebView()
        setupUI()

        // Se veio do assistente com uma mensagem, injeta no site após carregar
        intent?.getStringExtra(EXTRA_ASSISTANT_MESSAGE)?.let { msg ->
            pendingMessage = msg
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra(EXTRA_ASSISTANT_MESSAGE)?.let { msg ->
            injectMessage(msg)
        }
    }

    private var pendingMessage: String? = null

    private fun injectMessage(msg: String) {
        // Tenta colocar o texto no campo de input do CollaboraAI via JS
        val js = """
            (function() {
                var inputs = document.querySelectorAll('textarea, input[type=text]');
                if (inputs.length > 0) {
                    inputs[inputs.length-1].value = ${msg.replace("\"","\\\"").let { "\"$it\"" }};
                    inputs[inputs.length-1].dispatchEvent(new Event('input', {bubbles:true}));
                }
            })();
        """.trimIndent()
        binding.webView.evaluateJavascript(js, null)
    }

    private fun setupWebView() {
        with(binding.webView) {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                setSupportZoom(false)
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                    pendingMessage?.let {
                        injectMessage(it)
                        pendingMessage = null
                    }
                }
                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    if (request?.isForMainFrame == true) {
                        binding.layoutError.visibility = View.VISIBLE
                        binding.webView.visibility = View.GONE
                    }
                }
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return when {
                        url.startsWith("mailto:") || url.startsWith("tel:") -> {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            true
                        }
                        else -> false
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest?) {
                    runOnUiThread { request?.grant(request.resources) }
                }
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                }
            }

            loadUrl(COLLABORAAI_URL)
        }
    }

    private fun setupUI() {
        binding.fabAssistant.setOnClickListener {
            startActivity(Intent(this, AssistantActivity::class.java))
        }
        binding.btnRetry.setOnClickListener {
            binding.layoutError.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE
            binding.webView.reload()
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }

    override fun onResume() { super.onResume(); binding.webView.onResume() }
    override fun onPause() { super.onPause(); binding.webView.onPause() }
    override fun onDestroy() { binding.webView.destroy(); super.onDestroy() }
}
