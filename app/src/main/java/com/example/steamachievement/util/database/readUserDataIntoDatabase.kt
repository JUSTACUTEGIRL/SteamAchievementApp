package com.example.steamachievement.util.database

import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract.Data
import com.example.steamachievement.AppDatabase
import com.example.steamachievement.Database

fun readUserDataIntoDatabase(
    context: Context,
    SteamID: String,
    SteamAPIKey: String
) {
    if (SteamID.isEmpty() || SteamAPIKey.isEmpty()) {
        return
    }
    val writer = AppDatabase(context).writableDatabase
    val values = ContentValues().apply {
        put(Database.UserData.COLUMN_NAME_STEAMID, SteamID)
        put(Database.UserData.COLUMN_NAME_STEAMAPIKEY, SteamAPIKey)
    }

    writer.insert(Database.UserData.TABLE_NAME, null, values)
}