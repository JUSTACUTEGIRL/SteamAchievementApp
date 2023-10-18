package com.example.steamachievement.util.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.ContactsContract.Data
import com.example.steamachievement.Database

fun savePreferences(
    filter: List<String>,
    sort: List<String>,
    display: Int,
    writer: SQLiteDatabase
) {
    val selection = "${Database.Preferences.COLUMN_NAME_FIRST_TIME_START} = ?"
    val selectionArgs = arrayOf("0")
    var filterString = ""
    var sortString = ""

    for (string in filter) {
        filterString += string
    }

    for (string in sort) {
        sortString += string
    }

    val values = ContentValues().apply {
        put(Database.Preferences.COLUMN_NAME_FILTER, filterString)
        put(Database.Preferences.COLUMN_NAME_SORT, sortString)
        put(Database.Preferences.COLUMN_NAME_DISPLAY, display)
    }

    writer.update(
        Database.Preferences.TABLE_NAME,
        values,
        selection,
        selectionArgs
    )
}