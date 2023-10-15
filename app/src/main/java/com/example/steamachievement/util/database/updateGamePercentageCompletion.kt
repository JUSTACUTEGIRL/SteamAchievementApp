package com.example.steamachievement.util.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database
import com.example.steamachievement.Game
import com.example.steamachievement.util.calculatePercentageGameCompletion

fun updateGamePercentageCompletion(
    writer: SQLiteDatabase,
    game: Game
) {
    val selection = "${Database.Games.COLUMN_NAME_APPID} = ?"
    val selectionArgs = arrayOf(game.appID)

    val values = ContentValues().apply {
        put(Database.Games.COLUMN_NAME_PERCENTAGECOMPLETION, game.percentageCompletion)
    }

    writer.update(
        Database.Games.TABLE_NAME,
        values,
        selection,
        selectionArgs
    )
}