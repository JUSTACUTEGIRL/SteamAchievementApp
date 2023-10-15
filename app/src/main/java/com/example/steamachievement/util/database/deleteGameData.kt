package com.example.steamachievement.util.database

import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database

fun deleteGameData(
    appID: String,
    writer: SQLiteDatabase
) {
    val selectionAchievement = "${Database.Achievements.COLUMN_NAME_GAME} = ?"
    val selectionArgsAchievement = arrayOf(appID)

    writer.delete(
        Database.Achievements.TABLE_NAME,
        selectionAchievement,
        selectionArgsAchievement
    )

    val selectionGame = "${Database.Games.COLUMN_NAME_APPID} = ?"
    val selectionArgsGame = arrayOf(appID)

    writer.delete(
        Database.Games.TABLE_NAME,
        selectionGame,
        selectionArgsGame
    )
}