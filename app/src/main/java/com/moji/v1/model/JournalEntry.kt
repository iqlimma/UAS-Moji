package com.moji.v1.model

data class JournalEntry(
    val id: Int,
    val mood: Mood,
    val content: String,
    val date: String,
    val dateKey: String
)