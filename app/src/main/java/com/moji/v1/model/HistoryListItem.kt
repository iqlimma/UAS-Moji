package com.moji.v1.model

sealed class HistoryListItem {
    data class DateHeader(val dayNumber: String, val month: String, val label: String, val dayName: String) : HistoryListItem()
    data class Entry(val journalEntry: JournalEntry) : HistoryListItem()
}