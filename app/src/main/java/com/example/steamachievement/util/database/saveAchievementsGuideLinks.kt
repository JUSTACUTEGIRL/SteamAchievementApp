package com.example.steamachievement.util.database

import android.content.ContentValues
import android.content.Context
import com.example.steamachievement.Achievement
import com.example.steamachievement.AppDatabase
import com.example.steamachievement.Database

fun saveAchievementsLinks(
    context: Context,
    achievements: List<Achievement>
) {
    val writer = AppDatabase(context).writableDatabase
    writer.beginTransaction()

    try {
        achievements.forEach { achievement ->
            val selection = "${Database.Achievements.COLUMN_NAME_APINAME} = ?"
            val selectionArgs = arrayOf(achievement.apiname)

            val values = ContentValues().apply {
                put(Database.Achievements.COLUMN_NAME_GUIDELINK, achievement.guideLink)
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

fun saveOneAchievementGuideLink(
    achievement: Achievement,
    context: Context
) {
    val writer = AppDatabase(context).writableDatabase

    val selection = "${Database.Achievements.COLUMN_NAME_APINAME} = ?"
    val selectionArgs = arrayOf(achievement.apiname)

    val values = ContentValues().apply {
        put(Database.Achievements.COLUMN_NAME_GUIDELINK, achievement.guideLink)
    }

    writer.update(
        Database.Achievements.TABLE_NAME,
        values,
        selection,
        selectionArgs
    )
}