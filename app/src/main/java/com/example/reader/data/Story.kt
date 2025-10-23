package com.example.reader.data

import kotlinx.serialization.Serializable

@Serializable
data class Story(
    val title: String,
    val pages: List<Page>
)
