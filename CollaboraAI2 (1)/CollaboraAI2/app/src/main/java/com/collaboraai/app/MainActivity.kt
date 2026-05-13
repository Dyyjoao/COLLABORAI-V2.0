package com.collaboraai.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.collaboraai.app.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech

    companion object {
        const val COLLABORAAI_URL = "https://app.collaborai.io/"
        const val EXTRA_QUERY = "extra_query"
    }

    private val voiceLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val matches = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = matches?.firstOrNull() ?: return@registerForActivityResult
            binding.webView.loadUrl("$COLLABORAAI_URL?q=${Uri.encode(text)}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        setupTts()
        setupFab()

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_ASSIST,
            Intent.ACTION_VOICE_COMMAND -> openAssistant()
            Intent.ACTION_SEND -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (!text.isNullOrBlank()) {
                    binding.webView.loadUrl("$COLLABORAAI_URL?q=${Uri.encode(text)}")
                }
            }
        }

        val query = intent.getStringExtra(EXTRA_QUERY)
        if (!query.isNullOrBlank()) {
            binding.webView.loadUrl("$COLLABORAAI_URL?q=${Uri.encode(query)}")
        }
    }

    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                mediaPlaybackRequiresUserGesture = false
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                    binding.progressBar.visibility =
                        if (newProgress < 100) android.view.View.VISIBLE
                        else android.view.View.GONE
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return if (url.startsWith("http://") || url.startsWith("https://")) {
                        false
                    } else {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        true
                    }
                }
            }

            loadUrl(COLLABORAAI_URL)
        }
    }

    private fun setupTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("pt", "BR")
            }
        }
    }

    private fun setupFab() {
        binding.fabAssistant.setOnClickListener {
            openAssistant()
        }
    }

    private fun openAssistant() {
        tts.speak(
            "Olá! Como posso ajudar?",
            TextToSpeech.QUEUE_FLUSH,
            null,
            "greeting"
        )
        startActivity(Intent(this, AssistantActivity::class.java))
    }

    fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale com o CollaboraAI...")
        }
        voiceLauncher.launch(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}
