package com.example.steamachievement.util.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Achievement
import com.example.steamachievement.Database

fun readAchievementsIntoDatabase(
    writer: SQLiteDatabase,
    achievements: List<Achievement>
) {
    writer.beginTransaction()

    try {
        achievements.forEach { achievement ->
            val selection = "${Database.Achievements.COLUMN_NAME_APINAME} = ?"
            val selectionArgs = arrayOf(achievement.apiname)

            val values = ContentValues().apply {
                put(Database.Achievements.COLUMN_NAME_NAME, achievement.name)
                put(Database.Achievements.COLUMN_NAME_DISPLAYNAME, achievement.displayName)
                put(Database.Achievements.COLUMN_NAME_DESCRIPTION, achievement.description)
                put(Database.Achievements.COLUMN_NAME_ICON, achievement.icon)
                put(Database.Achievements.COLUMN_NAME_ICONGRAY, achievement.icongray)
                put(Database.Achievements.COLUMN_NAME_GUIDELINK, achievement.guideLink)
                put(Database.Achievements.COLUMN_NAME_NOTE, achievement.note)
            }

            writer.update(
                Database.Achievements.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )
        }
        writer.setTransactionSuccessful()
    } finally {
        writer.endTransaction()
    }
}