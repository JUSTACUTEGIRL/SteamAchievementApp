package com.example.steamachievement.util.network

import com.example.steamachievement.Achievement
import com.example.steamachievement.Game
import com.example.steamachievement.util.getRandomString
import com.github.kittinunf.fuel.httpGet
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

fun updateGameAchievementCompletion(
    achievements: MutableList<Achievement>,
    game: Game,
    steamID: String,
    steamAPIKey: String
) {
    var (_, _, result) = "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=${game.appID}&key=${steamAPIKey}&steamid=${steamID}&unused=${getRandomString(20)}"
        .httpGet()
        .responseString()

    val json = result.get()
    val element = Json.parseToJsonElement(json)

    try {
        element
            .jsonObject["playerstats"]!!
            .jsonObject["achievements"]!!
            .jsonArray.parallelStream().forEach { achievementElement ->
                val achievementParsed =
                    Json { ignoreUnknownKeys = true }.decodeFromJsonElement<Achievement>(achievementElement)

                val achievement =
                    achievements.find { achievement -> achievement.apiname == achievementParsed.apiname }

                if (achievement == null) {
                    achievements.add(Json { ignoreUnknownKeys = true }.decodeFromJsonElement(achievementElement))
                } else {
                    achievement.achieved = achievementParsed.achieved
                }
            }
    } catch (e: Exception) {

    }
}