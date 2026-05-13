package com.collaboraai.app

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.collaboraai.app.databinding.ActivityAssistantBinding
import java.util.Locale

class AssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssistantBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech

    private var isListening = false
    private var ttsReady = false
    private var pulseAnimator: ObjectAnimator? = null

    companion object {
        private const val PERMISSION_RECORD_AUDIO = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTTS()
        initSpeech()
        setupUI()

        if (intent?.action == Intent.ACTION_ASSIST) {
            Handler(Looper.getMainLooper()).postDelayed({ requestListening() }, 400)
        }
    }

    private fun initTTS() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val r = textToSpeech.setLanguage(Locale("pt", "BR"))
                ttsReady = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    private fun initSpeech() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            binding.tvStatus.text = "Reconhecimento de voz não disponível"
            binding.btnMic.isEnabled = false
            return
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p: Bundle?) = runOnUiThread { setListeningState() }
            override fun onBeginningOfSpeech() = runOnUiThread { binding.tvStatus.text = "Escutando…" }
            override fun onPartialResults(b: Bundle?) {
                val t = b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: return
                runOnUiThread { binding.etInput.setText(t); binding.etInput.setSelection(t.length) }
            }
            override fun onResults(b: Bundle?) {
                val t = b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: return
                runOnUiThread { isListening = false; binding.etInput.setText(t); processInput(t) }
            }
            override fun onError(e: Int) = runOnUiThread {
                isListening = false; setIdleState()
                binding.tvStatus.text = when(e) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Não entendi, tente novamente"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tempo esgotado"
                    else -> "Erro no microfone"
                }
            }
            override fun onEndOfSpeech() = runOnUiThread { isListening = false; binding.tvStatus.text = "Processando…" }
            override fun onRmsChanged(rms: Float) = runOnUiThread {
                val s = 1f + (rms.coerceIn(0f, 10f) / 10f) * 0.5f
                binding.viewMicRing.scaleX = s; binding.viewMicRing.scaleY = s
            }
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEvent(t: Int, p: Bundle?) {}
        })
    }

    private fun setupUI() {
        binding.btnMic.setOnClickListener { if (isListening) stopListening() else requestListening() }
        binding.btnSend.setOnClickListener {
            val t = binding.etInput.text.toString().trim()
            if (t.isNotEmpty()) processInput(t)
            else Toast.makeText(this, "Digite algo primeiro", Toast.LENGTH_SHORT).show()
        }
        binding.etInput.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEND || action == EditorInfo.IME_ACTION_DONE) {
                val t = binding.etInput.text.toString().trim()
                if (t.isNotEmpty()) processInput(t)
                true
            } else false
        }
        binding.btnClose.setOnClickListener { finish() }
        binding.root.setOnClickListener { finish() }
        binding.cardAssistant.setOnClickListener { }
        setIdleState()
    }

    private fun setIdleState() {
        pulseAnimator?.cancel()
        binding.btnMic.setImageResource(R.drawable.ic_mic)
        binding.btnMic.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
        binding.tvStatus.text = "Toque no microfone ou escreva"
        binding.viewMicRing.scaleX = 1f; binding.viewMicRing.scaleY = 1f
    }

    private fun setListeningState() {
        isListening = true
        pulseAnimator?.cancel()
        pulseAnimator = ObjectAnimator.ofFloat(binding.viewMicRing, "alpha", 0.3f, 1f).apply {
            duration = 700; repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE; start()
        }
        binding.btnMic.setImageResource(R.drawable.ic_mic_off)
        binding.btnMic.backgroundTintList = ContextCompat.getColorStateList(this, R.color.listening)
        binding.tvStatus.text = "Pronto — pode falar!"
        binding.etInput.setText("")
    }

    private fun requestListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_RECORD_AUDIO)
            return
        }
        speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        })
    }

    private fun stopListening() {
        speechRecognizer.stopListening(); isListening = false; setIdleState()
    }

    private fun processInput(input: String) {
        getSystemService(InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.tvStatus.text = "Abrindo CollaboraAI…"
        if (ttsReady) textToSpeech.speak("Abrindo com sua mensagem", TextToSpeech.QUEUE_FLUSH, null, "id")
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_ASSISTANT_MESSAGE, input)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }, 600L)
    }

    override fun onRequestPermissionsResult(rc: Int, p: Array<out String>, gr: IntArray) {
        super.onRequestPermissionsResult(rc, p, gr)
        if (rc == PERMISSION_RECORD_AUDIO && gr.firstOrNull() == PackageManager.PERMISSION_GRANTED) requestListening()
        else binding.tvStatus.text = "Permissão de microfone negada"
    }

    override fun onDestroy() {
        if (::speechRecognizer.isInitialized) { speechRecognizer.stopListening(); speechRecognizer.destroy() }
        if (::textToSpeech.isInitialized) { textToSpeech.stop(); textToSpeech.shutdown() }
        pulseAnimator?.cancel()
        super.onDestroy()
    }
}
