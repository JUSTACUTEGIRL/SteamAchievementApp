package com.example.steamachievement.util.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database
import com.example.steamachievement.Game

fun readGamesIntoDatabase(
    writer: SQLiteDatabase,
    games: List<Game>
) {
    writer.beginTransaction()

    try {
        games.forEach { game ->
            val values = ContentValues().apply {
                put(Database.Games.COLUMN_NAME_APPID, game.appID)
                put(Database.Games.COLUMN_NAME_GAMENAME, game.gameName)
                put(Database.Games.COLUMN_NAME_ISCOMPLETED, game.isCompleted)
                put(Database.Games.COLUMN_NAME_HASACHIEVEMENT, game.hasAchievement)
                put(Database.Games.COLUMN_NAME_PERCENTAGECOMPLETION, game.percentageCompletion)
                put(Database.Games.COLUMN_NAME_GUIDELINK, game.guideLink)
                put(Database.Games.COLUMN_NAME_ACHIEVEMENTSLINK, game.achievementsLink)
                put(Database.Games.COLUMN_NAME_LOADED, game.loaded)
            }

            writer.insert(Database.Games.TABLE_NAME, null, values)
        }
        writer.setTransactionSuccessful()
    } finally {
        writer.endTransaction()
    }
}