package com.example.steamachievement.util.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database
import com.example.steamachievement.Game

fun readPartialAchievementsIntoDatabase(
    writer: SQLiteDatabase,
    games: List<Game>
) {
    writer.beginTransaction()

    try {
        games.forEach { game ->
            game.achievements.forEach { achievement ->
                val values = ContentValues().apply {
                    put(Database.Achievements.COLUMN_NAME_NAME, achievement.name)
                    put(Database.Achievements.COLUMN_NAME_DISPLAYNAME, achievement.displayName)
                    put(Database.Achievements.COLUMN_NAME_DESCRIPTION, achievement.description)
                    put(Database.Achievements.COLUMN_NAME_ICON, achievement.icon)
                    put(Database.Achievements.COLUMN_NAME_ICONGRAY, achievement.icongray)
                    put(Database.Achievements.COLUMN_NAME_GUIDELINK, achievement.guideLink)
                    put(Database.Achievements.COLUMN_NAME_NOTE, achievement.note)
                    put(Database.Achievements.COLUMN_NAME_APINAME, achievement.apiname)
                    put(Database.Achievements.COLUMN_NAME_ACHIEVED, achievement.achieved)
                    put(Database.Achievements.COLUMN_NAME_GAME, game.appID)
                }

                writer.insert(Database.Achievements.TABLE_NAME, null, values)
            }
        }
        writer.setTransactionSuccessful()
    } finally {
        writer.endTransaction()
    }
}