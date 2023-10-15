package com.example.steamachievement.util.network

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.example.steamachievement.Game
import com.example.steamachievement.GameViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutionException

fun downloadGamesImage(
    listOfGames: List<Game>,
    context: Context,
    gameViewModel: GameViewModel
) {
    listOfGames.parallelStream().forEach { game ->
        val appID = game.appID
        gameViewModel.changeLoadingLog("Loading Images for AppID: $appID")
        saveGameHorizontalImage(context, appID)
        saveGameVerticalImage(context, appID)
    }
}

fun saveGameVerticalImage(context: Context, appID: String) {
    val storageDir = File(context.filesDir.toString() + "/game/vertical")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    val imageFile = File(storageDir, "$appID.jpg")
    if (!imageFile.exists()) {
        val fOut = FileOutputStream(imageFile)
        val imageBitmap = try {
            Glide
                .with(context)
                .asBitmap()
                .load("https://steamcdn-a.akamaihd.net/steam/apps/$appID/library_600x900_2x.jpg")
                .submit()
                .get()
        } catch (e: ExecutionException) {
            null
        }
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.close()
    }
}

fun saveGameHorizontalImage(context: Context, appID: String) {
    val storageDir = File(context.filesDir.toString() + "/game/horizontal")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    val imageFile = File(storageDir, "$appID.jpg")
    if (!imageFile.exists()) {
        val fOut = FileOutputStream(imageFile)
        val imageBitmap = try {
            Glide
                .with(context)
                .asBitmap()
                .load("https://cdn.akamai.steamstatic.com/steam/apps/$appID/header.jpg")
                .submit()
                .get()
        } catch (e: ExecutionException) {
            null
        }
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.close()
    }
}