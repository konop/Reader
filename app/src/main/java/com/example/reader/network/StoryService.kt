package com.example.reader.network

import com.example.reader.data.Story
import retrofit2.http.GET

interface StoryService {
    @GET("stories.json") // Assuming stories are available at this endpoint
    suspend fun getStories(): List<Story>
}