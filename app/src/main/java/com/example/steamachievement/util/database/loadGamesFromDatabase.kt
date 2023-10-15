package com.example.steamachievement.util.database

import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database
import com.example.steamachievement.Game

fun loadGamesFromDatabase(reader: SQLiteDatabase): List<Game> {
    val listOfGames: MutableList<Game> = mutableListOf()
    val cursor = reader.query(
        Database.Games.TABLE_NAME,
        null,
        null,
        null,
        null,
        null,
        null
    )
    with(cursor) {
        while (moveToNext()) {
            listOfGames.add(
                Game(
                    getString(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_APPID)),
                    getString(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_GAMENAME)),
                    getInt(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_ISCOMPLETED)) == 1,
                    getInt(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_HASACHIEVEMENT)) == 1,
                    getInt(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_PERCENTAGECOMPLETION)),
                    mutableListOf(),
                    getString(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_GUIDELINK)),
                    getString(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_ACHIEVEMENTSLINK)),
                    getInt(getColumnIndexOrThrow(Database.Games.COLUMN_NAME_LOADED)) == 1
                )
            )
        }
    }

    cursor.close()
    return listOfGames
}