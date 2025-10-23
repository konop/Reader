package com.example.reader.ui.story

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import com.example.reader.data.Page
import com.example.reader.data.Story
import com.example.reader.data.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class StoryViewModel(private val application: Application) : ViewModel(), TextToSpeech.OnInitListener, RecognitionListener {

    private val _story = MutableStateFlow<Story?>(null)
    val story: StateFlow<Story?> = _story

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val tts: TextToSpeech = TextToSpeech(application, this)
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)

    init {
        _story.value = createDummyStory()
        speechRecognizer.setRecognitionListener(this)
    }

    private fun createDummyStory(): Story {
        val pages = listOf(
            Page(
                image = "https://via.placeholder.com/300",
                words = "The quick brown fox jumps over the lazy dog".split(" ").map { Word(it) }
            ),
            Page(
                image = "https://via.placeholder.com/300",
                words = "This is the second page".split(" ").map { Word(it) }
            )
        )
        return Story(title = "My First Story", pages = pages)
    }

    fun speakWord(word: Word) {
        tts.speak(word.text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
    }

    private fun updateWordAsRead(text: String) {
        _story.update { story ->
            story?.let {
                val newPages = story.pages.map { page ->
                    val newWords = page.words.map { word ->
                        if (word.text.equals(text, ignoreCase = true)) {
                            word.copy(isRead = true)
                        } else {
                            word
                        }
                    }
                    page.copy(words = newWords)
                }
                story.copy(pages = newPages)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle language not supported
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
        tts.shutdown()
        speechRecognizer.destroy()
    }

    // RecognitionListener methods
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onError(error: Int) {}
    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null) {
            for (result in matches) {
                updateWordAsRead(result)
            }
        }
    }
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
