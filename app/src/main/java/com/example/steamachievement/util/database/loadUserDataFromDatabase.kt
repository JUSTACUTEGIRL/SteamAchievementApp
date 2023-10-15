package com.example.steamachievement.util.database

import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import com.example.steamachievement.Database

fun loadSteamIDFromDatabase(
    reader: SQLiteDatabase
): String {
    val cursor = reader.query(
        Database.UserData.TABLE_NAME,
        arrayOf(Database.UserData.COLUMN_NAME_STEAMID),
        null,
        null,
        null,
        null,
        null
    )
    cursor.moveToNext()
    val steamID = cursor.getString(cursor.getColumnIndexOrThrow(Database.UserData.COLUMN_NAME_STEAMID))
    cursor.close()
    return steamID
}

fun loadSteamAPIKeyFromDatabase(
    reader: SQLiteDatabase
): String {
    val cursor = reader.query(
        Database.UserData.TABLE_NAME,
        arrayOf(Database.UserData.COLUMN_NAME_STEAMAPIKEY),
        null,
        null,
        null,
        null,
        null
    )
    cursor.moveToNext()
    val steamAPIKey = cursor.getString(cursor.getColumnIndexOrThrow(Database.UserData.COLUMN_NAME_STEAMAPIKEY))
    cursor.close()
    return steamAPIKey
}

