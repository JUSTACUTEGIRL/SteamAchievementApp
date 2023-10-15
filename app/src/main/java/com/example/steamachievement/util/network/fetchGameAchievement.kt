package com.example.steamachievement.util.network

import androidx.compose.runtime.toMutableStateList
import com.example.steamachievement.Achievement
import com.example.steamachievement.Database
import com.example.steamachievement.Game
import com.example.steamachievement.util.getRandomString
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

fun fetchGameAchievement(
    achievements: MutableList<Achievement>,
    game: Game,
    steamAPIKey: String
) {
    achievements.addAll(game.achievements)

    runBlocking {
        var (_, _, result) = "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=$steamAPIKey&appid=${game.appID}&unused=${getRandomString(20)}"
            .httpGet()
            .responseString()

        val json = result.get()

        if (json.length == 11) {
            achievements.add(
                Achievement(
                    displayName = "No Achievement",
                    description = "This game has no achievements"
                )
            )
        } else {
            val element = Json.parseToJsonElement(json)

            try {
                element
                    .jsonObject["game"]!!
                    .jsonObject["availableGameStats"]!!
                    .jsonObject["achievements"]!!
                    .jsonArray.parallelStream().forEach { achievementElement ->
                        val achievementParsed =
                            Json {
                                ignoreUnknownKeys = true
                            }.decodeFromJsonElement<Achievement>(achievementElement)

                        val achievement =
                            achievements.find { achievement -> achievement.apiname == achievementParsed.name }

                        if (achievement == null) {
                            achievements.add(Json {
                                ignoreUnknownKeys = true
                            }.decodeFromJsonElement(achievementElement))
                        } else {
                            achievement.displayName = achievementParsed.displayName
                            achievement.description = achievementParsed.description
                            achievement.icon = achievementParsed.icon
                            achievement.icongray = achievementParsed.icongray
                        }
                    }
            } catch (e: Exception) {
                achievements.add(
                    Achievement(
                    displayName = "NULL_ERROR",
                    description = "NullPointerException Occured")
                )
            }
        }
    }

    val buffer = achievements.last()
    achievements.removeLast()
    achievements.add(buffer)
}