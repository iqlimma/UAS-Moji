package com.moji.v1.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.moji.v1.model.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "moji.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USER = "users"
        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USER (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT NOT NULL,
                $COL_EMAIL TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
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
        val cleanEmail = email.trim().lowercase()
        val cleanPassword = password.trim()

        val cursor = db.query(
            TABLE_USER,
            null,
            "$COL_EMAIL = ? AND $COL_PASSWORD = ?",
            arrayOf(cleanEmail, cleanPassword),
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
            TABLE_USER,
            null,
            "$COL_EMAIL = ?",
            arrayOf(email.trim().lowercase()),
            null, null, null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }
}