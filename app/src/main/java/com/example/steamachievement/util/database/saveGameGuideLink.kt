package com.example.steamachievement.util.database

import android.content.ContentValues
import android.content.Context
import com.example.steamachievement.AppDatabase
import com.example.steamachievement.Database
import com.example.steamachievement.Game

fun saveGameGuideLink(
    context: Context,
    game: Game
) {
    val writer = AppDatabase(context).writableDatabase

    val selection = "${Database.Games.COLUMN_NAME_APPID} = ?"
    val selectionArgs = arrayOf(game.appID)

    val values = ContentValues().apply {
        put(Database.Games.COLUMN_NAME_GUIDELINK, game.guideLink)
        put(Database.Games.COLUMN_NAME_ACHIEVEMENTSLINK, game.achievementsLink)
    }

    writer.update(
        Database.Games.TABLE_NAME,
        values,
        selection,
        selectionArgs
    )
}