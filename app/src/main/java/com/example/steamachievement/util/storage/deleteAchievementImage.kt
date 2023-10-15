package com.example.steamachievement.util.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.steamachievement.Database
import java.io.File

fun deleteAchievementImage(
    appID: String,
    context: Context
) {
    val storageDir = File(context.filesDir.toString() + "/achievement/$appID")
    storageDir.delete()
}