package com.example.steamachievement.util.network

import androidx.compose.runtime.toMutableStateList
import com.example.steamachievement.Game
import com.example.steamachievement.GameViewModel
import com.example.steamachievement.util.calculatePercentageGameCompletion
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

fun fetchAllGameData(
    steamID: String,
    steamAPIKey: String,
    gameViewModel: GameViewModel
): List<Game> {
    var listOfGame = mutableListOf<Game>()

    runBlocking(Dispatchers.IO) {
        var (_, _, resultGame) = ("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=$steamAPIKey&steamid=$steamID&format=json")
            .httpGet()
            .responseString()

        val element = Json.parseToJsonElement(resultGame.get())

        val listOfAppID = mutableListOf<String>()

        element
            .jsonObject["response"]!!
            .jsonObject["games"]!!
            .jsonArray.forEach { item -> listOfAppID.add(item.jsonObject["appid"].toString()) }
        listOfGame = listOfAppID.map { appID ->
            async(Dispatchers.IO) {
                gameViewModel.changeLoadingLog("AppID: $appID Loading")
                try {
                    var (_, _, result) =
                        ("https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=$appID&key=$steamAPIKey&steamid=$steamID")
                            .httpGet()
                            .responseString()

                    var json = result.get()

                    json = "{\"appID\":\"" + appID + "\"," + json.drop(16).dropLast(1)
                    return@async Json { ignoreUnknownKeys = true }.decodeFromString<Game>(json)
                } catch (e: FuelError) {
                    val (_, _, newJson) = ("https://store.steampowered.com/api/appdetails?appids=$appID")
                        .httpGet()
                        .responseString()

                    val element = Json.parseToJsonElement(newJson.get())
                    try {
                        return@async Game(
                            appID = appID,
                            gameName = element
                                .jsonObject["$appID"]!!
                                .jsonObject["data"]!!
                                .jsonObject["name"].toString().replace("\"", ""),
                            hasAchievement = false,
                        )
                    } catch(e: Exception) {
                        println(e.message)
                        return@async null
                    }
                }
            }
        }.awaitAll().filterNotNull().toMutableStateList()

        for (game in listOfGame) {
            if (game.achievements.isEmpty()) {
                game.percentageCompletion = -1
            } else {
                game.percentageCompletion = calculatePercentageGameCompletion(game.achievements)
            }
            if (game.percentageCompletion == 100) {
                game.isCompleted = true
            }
        }
    }

    return listOfGame
}