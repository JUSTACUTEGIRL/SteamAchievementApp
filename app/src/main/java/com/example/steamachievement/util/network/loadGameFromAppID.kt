package com.example.steamachievement.util.network

import com.example.steamachievement.Game
import com.example.steamachievement.util.calculatePercentageGameCompletion
import com.example.steamachievement.util.getRandomString
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

fun loadGameFromAppID(
    steamID: String,
    steamAPIKey: String,
    appID: String
): Game {
    var game = Game(appID = "0", gameName = "Error", hasAchievement = false)

    try {
        var (_, _, result) =
            ("https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=$appID&key=$steamAPIKey&steamid=$steamID&unused=${getRandomString(20)}")
                .httpGet()
                .responseString()

        var json = result.get()

        json = "{\"appID\":\"" + appID + "\"," + json.drop(16).dropLast(1)
        game = Json { ignoreUnknownKeys = true }.decodeFromString(json)
    } catch (e: FuelError) {
        val (_, _, newJson) = ("https://store.steampowered.com/api/appdetails?appids=$appID&unused=${getRandomString(20)}")
            .httpGet()
            .responseString()

        val element = Json.parseToJsonElement(newJson.get())
        game = Game(
            appID = appID,
            gameName = element
                .jsonObject["$appID"]!!
                .jsonObject["data"]!!
                .jsonObject["name"].toString().replace("\"", ""),
            hasAchievement = false,
        )
    } catch (e: Exception) {
        println(e.message)
    }

    if (game.achievements.isEmpty()) {
        game.percentageCompletion = -1
    } else {
        game.percentageCompletion = calculatePercentageGameCompletion(game.achievements)
    }
    if (game.percentageCompletion == 100) {
        game.isCompleted = true
    }

    return game
}