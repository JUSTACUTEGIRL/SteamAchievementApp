package com.example.steamachievement.util.network

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.example.steamachievement.Achievement
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutionException

fun downloadAchievementsImage(
    appID: String,
    context: Context,
    achievements: List<Achievement>
) {
     achievements.parallelStream().forEach { achievement ->
        val storageDir = File(context.filesDir.toString() + "/achievement/$appID")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        //load colour achievement image
        val imageFile = File(storageDir, "${achievement.apiname}_icon.jpg")
        if (!imageFile.exists()) {
            val fOut = FileOutputStream(imageFile)
            val imageBitmap = try {
                Glide
                    .with(context)
                    .asBitmap()
                    .load(achievement.icon)
                    .submit()
                    .get()
            } catch (e: ExecutionException) {
                null
            }
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.close()
        }

        //load gray achievement image
        val imageFileGray = File(storageDir, "${achievement.apiname}_icongray.jpg")
        if (!imageFileGray.exists()) {
            val fOutGray = FileOutputStream(imageFileGray)
            val imageBitmapGray = try {
                Glide
                    .with(context)
                    .asBitmap()
                    .load(achievement.icongray)
                    .submit()
                    .get()
            } catch (e: ExecutionException) {
                null
            }
            imageBitmapGray?.compress(Bitmap.CompressFormat.JPEG, 100, fOutGray)
            fOutGray.close()
        }
    }
}