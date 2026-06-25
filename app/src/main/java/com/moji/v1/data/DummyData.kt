package com.moji.v1.data

import com.moji.v1.model.JournalEntry
import com.moji.v1.model.Mood

object DummyData {
    val entries = mutableListOf(
        JournalEntry(1, Mood.HAPPY, "Hari ini sangat menyenangkan!", "11 Apr 2026", "2026-04-11"),
        JournalEntry(2, Mood.NEUTRAL, "Hari biasa aja, tidak ada yang spesial.", "10 Apr 2026", "2026-04-10"),
        JournalEntry(3, Mood.SAD, "Kangen rumah. Hari ini terasa berat.", "09 Apr 2026", "2026-04-09"),
        JournalEntry(4, Mood.TIRED, "Begadang ngerjain tugas sampai jam 3 pagi.", "08 Apr 2026", "2026-04-08"),
        JournalEntry(5, Mood.ANGRY, "Tugas numpuk dan deadline semua bareng.", "07 Apr 2026", "2026-04-07")
    )
}