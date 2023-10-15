package com.example.steamachievement.util.database

import android.content.ContentValues
import android.content.Context
import com.example.steamachievement.Achievement
import com.example.steamachievement.AppDatabase
import com.example.steamachievement.Database

fun saveAchievementNote(
    context: Context,
    achievement: Achievement
) {
    val writer = AppDatabase(context).writableDatabase

    val selection = "${Database.Achievements.COLUMN_NAME_APINAME} = ?"
    val selectionArgs = arrayOf(achievement.apiname)

    val values = ContentValues().apply {
        put(Database.Achievements.COLUMN_NAME_NOTE, achievement.note)
    }

    writer.update(
        Database.Achievements.TABLE_NAME,
        values,
        selection,
        selectionArgs
    )
}