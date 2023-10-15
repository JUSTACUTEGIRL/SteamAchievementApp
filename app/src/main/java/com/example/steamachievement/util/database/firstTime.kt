package com.example.steamachievement.util.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database

fun isFirstTime(reader: SQLiteDatabase): Boolean {
    val cursor = reader.query(
        Database.Preferences.TABLE_NAME,
        arrayOf(Database.Preferences.COLUMN_NAME_FIRST_TIME_START),
        null,
        null,
        null,
        null,
        null
    )
    cursor.moveToNext()
    val isFirstTime = cursor.getInt(cursor.getColumnIndexOrThrow(Database.Preferences.COLUMN_NAME_FIRST_TIME_START)) == 1
    cursor.close()
    return isFirstTime
}

fun changeFirstTime(writer: SQLiteDatabase) {
    val value = ContentValues().apply {
        put(Database.Preferences.COLUMN_NAME_FIRST_TIME_START, 0)
    }

    writer.update(
        Database.Preferences.TABLE_NAME,
        value,
        Database.Preferences.COLUMN_NAME_FIRST_TIME_START,
        arrayOf()
    )
}