package com.example.reader.data

import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val image: String,
    val words: List<Word>
)
