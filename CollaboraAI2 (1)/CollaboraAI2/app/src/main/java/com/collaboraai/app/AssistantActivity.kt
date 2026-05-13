package com.collaboraai.app

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.collaboraai.app.databinding.ActivityAssistantBinding
import java.util.Locale

data class ChatMessage(val text: String, val isUser: Boolean)

class AssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssistantBinding
    private lateinit var tts: TextToSpeech
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    companion object {
        const val EXTRA_ASSISTANT_MESSAGE = "extra_assistant_message"
    }

    private val voiceLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val matches = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = matches?.firstOrNull() ?: return@registerForActivityResult
            sendMessage(text)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupTts()
        setupListeners()

        // Trata mensagem recebida via intent (ex: vinda da MainActivity)
        val incoming = intent.getStringExtra(EXTRA_ASSISTANT_MESSAGE)
        if (!incoming.isNullOrBlank()) {
            addMessage(incoming, isUser = false)
        } else {
            addMessage("Olá! Como posso ajudar você hoje?", isUser = false)
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("pt", "BR")
            }
        }
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            val text = binding.editMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                binding.editMessage.setText("")
                sendMessage(text)
            }
        }

        binding.btnVoice.setOnClickListener {
            startVoiceInput()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun sendMessage(text: String) {
        addMessage(text, isUser = true)
        // Resposta automática — integre com sua API aqui
        val reply = "Você disse: \"$text\". Em breve responderei via CollaboraAI!"
        addMessage(reply, isUser = false)
        tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "reply")

        // Envia query para a MainActivity / WebView
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_QUERY, text)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun addMessage(text: String, isUser: Boolean) {
        messages.add(ChatMessage(text, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale com o CollaboraAI...")
        }
        voiceLauncher.launch(intent)
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}
