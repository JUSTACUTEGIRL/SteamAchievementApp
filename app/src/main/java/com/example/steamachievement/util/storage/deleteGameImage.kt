package com.example.steamachievement.util.storage

import android.content.Context
import java.io.File

fun deleteGameImage(
    appID: String,
    context: Context
) {
    val storageDirVert = File(context.filesDir.toString() + "/game/vertical/$appID.jpg")
    val storageDirHorz = File(context.filesDir.toString() + "/game/horizontal/$appID.jpg")
    storageDirVert.delete()
    storageDirHorz.delete()
}