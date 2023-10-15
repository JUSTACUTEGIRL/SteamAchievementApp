package com.example.steamachievement.util.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.ContactsContract.Data
import com.example.steamachievement.Database
import com.example.steamachievement.Game

fun isGameLoaded(reader: SQLiteDatabase, game: Game): Boolean {
    val selection = "${Database.Games.COLUMN_NAME_APPID} = ?"
    val selectionArgs = arrayOf(game.appID)
    val cursor = reader.query(
        Database.Games.TABLE_NAME,
        arrayOf(Database.Games.COLUMN_NAME_LOADED),
        selection,
        selectionArgs,
        null,
        null,
        null
    )
    cursor.moveToNext()
    val isGameLoaded = cursor.getInt(cursor.getColumnIndexOrThrow(Database.Games.COLUMN_NAME_LOADED)) == 1
    cursor.close()
    return isGameLoaded
}

fun changeGameLoaded(writer: SQLiteDatabase, game: Game) {
    val value = ContentValues().apply {
        put(Database.Games.COLUMN_NAME_LOADED, 1)
    }

    val selection = "${Database.Games.COLUMN_NAME_APPID} = ?"
    val selectionArgs = arrayOf(game.appID)

    writer.update(
        Database.Games.TABLE_NAME,
        value,
        selection,
        selectionArgs
    )
}