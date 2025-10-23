package com.example.reader.ui.storylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.Story
import com.example.reader.network.StoryService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
class StoryListViewModel : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/your-username/your-repo/main/") // Replace with your base URL
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val storyService = retrofit.create(StoryService::class.java)

    init {
        fetchStories()
    }

    private fun fetchStories() {
        viewModelScope.launch {
            try {
                _stories.value = storyService.getStories()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
}
