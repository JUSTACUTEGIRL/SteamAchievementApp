package com.example.steamachievement.util.database

import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database

fun readPreferences(reader: SQLiteDatabase): Triple<List<String>, List<String>, Int> {
    val selection = "${Database.Preferences.COLUMN_NAME_FIRST_TIME_START} = ?"
    val selectionArgs = arrayOf("0")

    val cursor = reader.query(
        Database.Preferences.TABLE_NAME,
        null,
        selection,
        selectionArgs,
        null,
        null,
        null
    )

    with(cursor) {
        moveToNext()

        val filter = splitString(getString(getColumnIndexOrThrow(Database.Preferences.COLUMN_NAME_FILTER)))
        val sort = splitString(getString(getColumnIndexOrThrow(Database.Preferences.COLUMN_NAME_SORT)))
        val display = getInt(getColumnIndexOrThrow(Database.Preferences.COLUMN_NAME_DISPLAY))

        return Triple(filter, sort, display)
    }
}

fun splitString(string: String): List<String> {
    val list = mutableListOf<String>()

    for (char in string) {
        list.add(char.toString())
    }

    return list
}