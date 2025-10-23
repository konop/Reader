package com.example.reader.data

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val text: String,
    val isRead: Boolean = false
)
