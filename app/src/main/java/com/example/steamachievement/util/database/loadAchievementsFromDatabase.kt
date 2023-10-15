package com.example.steamachievement.util.database

import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Achievement
import com.example.steamachievement.Database
import com.example.steamachievement.Game

fun loadAchievementsFromDatabase(reader: SQLiteDatabase, game: Game): List<Achievement> {
    val listOfAchievements: MutableList<Achievement> = mutableListOf()
    val selection = "${Database.Achievements.COLUMN_NAME_GAME} = ?"
    val selectionArgs = arrayOf(game.appID)
    val cursor = reader.query(
        Database.Achievements.TABLE_NAME,
        null,
        selection,
        selectionArgs,
        null,
        null,
        null
    )
    with(cursor) {
        while (moveToNext()) {
            listOfAchievements.add(
                Achievement(
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_NAME)),
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_DISPLAYNAME)),
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_APINAME)),
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_DESCRIPTION)),
                    getInt(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_ACHIEVED)),
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_ICON)),
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_ICONGRAY)),
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_GUIDELINK)),
                    getString(getColumnIndexOrThrow(Database.Achievements.COLUMN_NAME_NOTE)),
                )
            )
        }
    }

    cursor.close()
    return listOfAchievements
}