package com.moji.v1.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.moji.v1.model.JournalEntry
import com.moji.v1.model.Mood
import com.moji.v1.model.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "moji.db"
        const val DATABASE_VERSION = 3

        const val TABLE_USER = "users"
        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"

        const val TABLE_JOURNAL = "journals"
        const val COL_JOURNAL_ID = "id"
        const val COL_JOURNAL_USER_ID = "user_id"
        const val COL_JOURNAL_MOOD = "mood"
        const val COL_JOURNAL_CONTENT = "content"
        const val COL_JOURNAL_DATE = "date"
        const val COL_JOURNAL_DATE_KEY = "date_key"
        const val COL_JOURNAL_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT NOT NULL,
                $COL_EMAIL TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        val createJournalTable = """
            CREATE TABLE $TABLE_JOURNAL (
                $COL_JOURNAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_JOURNAL_USER_ID INTEGER NOT NULL,
                $COL_JOURNAL_MOOD TEXT NOT NULL,
                $COL_JOURNAL_CONTENT TEXT NOT NULL,
                $COL_JOURNAL_DATE TEXT NOT NULL,
                $COL_JOURNAL_DATE_KEY TEXT NOT NULL,
                $COL_JOURNAL_TIME TEXT NOT NULL,
                FOREIGN KEY($COL_JOURNAL_USER_ID) REFERENCES $TABLE_USER($COL_ID)
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createJournalTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_JOURNAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun registerUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, user.username.trim())
            put(COL_EMAIL, user.email.trim().lowercase())
            put(COL_PASSWORD, user.password.trim())
        }
        val result = db.insert(TABLE_USER, null, values)
        db.close()
        return result != -1L
    }

    fun loginUser(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER, null,
            "$COL_EMAIL = ? AND $COL_PASSWORD = ?",
            arrayOf(email.trim().lowercase(), password.trim()),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
            )
            cursor.close()
            db.close()
            user
        } else {
            cursor.close()
            db.close()
            null
        }
    }

    fun isEmailExist(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USER, null,
            "$COL_EMAIL = ?",
            arrayOf(email.trim().lowercase()),
            null, null, null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun insertJournal(entry: JournalEntry, userId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_JOURNAL_USER_ID, userId)
            put(COL_JOURNAL_MOOD, entry.mood.name)
            put(COL_JOURNAL_CONTENT, entry.content)
            put(COL_JOURNAL_DATE, entry.date)
            put(COL_JOURNAL_DATE_KEY, entry.dateKey)
            put(COL_JOURNAL_TIME, entry.time)
        }
        val result = db.insert(TABLE_JOURNAL, null, values)
        db.close()
        return result != -1L
    }

    fun getJournalsByUser(userId: Int): List<JournalEntry> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_JOURNAL, null,
            "$COL_JOURNAL_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null,
            "$COL_JOURNAL_ID DESC"
        )
        val entries = mutableListOf<JournalEntry>()
        while (cursor.moveToNext()) {
            entries.add(
                JournalEntry(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_JOURNAL_ID)),
                    mood = Mood.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_JOURNAL_MOOD))),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COL_JOURNAL_CONTENT)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COL_JOURNAL_DATE)),
                    dateKey = cursor.getString(cursor.getColumnIndexOrThrow(COL_JOURNAL_DATE_KEY)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COL_JOURNAL_TIME))
                )
            )
        }
        cursor.close()
        db.close()
        return entries
    }

    fun deleteJournal(journalId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_JOURNAL, "$COL_JOURNAL_ID = ?", arrayOf(journalId.toString()))
        db.close()
        return result > 0
    }

    fun deleteAllJournalsByUser(userId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_JOURNAL, "$COL_JOURNAL_USER_ID = ?", arrayOf(userId.toString()))
        db.close()
        return result >= 0
    }
}