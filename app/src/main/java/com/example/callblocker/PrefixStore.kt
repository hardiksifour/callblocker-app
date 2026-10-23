package com.example.callblocker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Stores blocked prefixes (e.g. "+1400") in a SQLite database
 * kept in the app's private storage on the phone.
 */
class PrefixStore(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                prefix TEXT NOT NULL UNIQUE,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No migrations yet (version 1).
    }

    fun getAll(): List<String> {
        val result = mutableListOf<String>()
        readableDatabase.query(TABLE, arrayOf("prefix"), null, null, null, null, "prefix ASC")
            .use { c ->
                while (c.moveToNext()) result.add(c.getString(0))
            }
        return result
    }

    /** Returns false if the prefix is invalid or already exists. */
    fun add(raw: String): Boolean {
        val prefix = normalize(raw)
        if (prefix.length < 2) return false
        val values = ContentValues().apply {
            put("prefix", prefix)
            put("created_at", System.currentTimeMillis())
        }
        return writableDatabase.insertWithOnConflict(
            TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE
        ) != -1L
    }

    fun remove(prefix: String) {
        writableDatabase.delete(TABLE, "prefix = ?", arrayOf(prefix))
    }

    /** True if the number starts with any stored prefix. */
    fun isBlocked(number: String): Boolean {
        val n = normalize(number)
        if (n.isEmpty()) return false
        readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLE WHERE ? LIKE prefix || '%' LIMIT 1",
            arrayOf(n)
        ).use { c -> return c.moveToFirst() }
    }

    companion object {
        private const val DB_NAME = "callblocker.db"
        private const val DB_VERSION = 1
        private const val TABLE = "blocked_prefixes"

        /** Strips spaces, dashes, brackets, dots; keeps a leading + and digits. */
        fun normalize(s: String): String {
            val trimmed = s.trim()
            val plus = trimmed.startsWith("+")
            val digits = trimmed.filter { it.isDigit() }
            return if (plus) "+$digits" else digits
        }
    }
}
