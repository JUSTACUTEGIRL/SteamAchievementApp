package com.example.steamachievement.util.network

import com.example.steamachievement.Game
import com.example.steamachievement.util.getRandomString
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

fun refreshGameList(
    games: MutableList<Game>,
    steamID: String,
    steamAPIKey: String
): List<Game> {
    var (_, _, resultGame) = ("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=$steamAPIKey&steamid=$steamID&format=json&unused=${getRandomString(20)}")
        .httpGet()
        .responseString()

    val element = Json.parseToJsonElement(resultGame.get())

    val listOfAppID = mutableListOf<String>()
    val listOfGames = mutableListOf<Game>()

    element
        .jsonObject["response"]!!
        .jsonObject["games"]!!
        .jsonArray.forEach { item -> listOfAppID.add(item.jsonObject["appid"].toString()) }



    listOfAppID.filter { appID ->
        games.find { game -> game.appID == appID } == null
    }.parallelStream().forEach { appID ->
        try {
            var (request, response, result) =
                ("https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=$appID&key=$steamAPIKey&steamid=$steamID&unused=${getRandomString(20)}")
                    .httpGet()
                    .responseString()

            var json = result.get()

            json = "{\"appID\":\"" + appID + "\"," + json.drop(16).dropLast(1)
            listOfGames.add(Json { ignoreUnknownKeys = true }.decodeFromString<Game>(json))
        } catch (e: FuelError) {
            val (_, _, newJson) = ("https://store.steampowered.com/api/appdetails?appids=$appID&unused=${getRandomString(20)}")
                .httpGet()
                .responseString()

            val element = Json.parseToJsonElement(newJson.get())
            try {
                listOfGames.add(
                    Game(
                        appID = appID,
                        gameName = element
                            .jsonObject["$appID"]!!
                            .jsonObject["data"]!!
                            .jsonObject["name"].toString().replace("\"", ""),
                        hasAchievement = false,
                    )
                )
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    return listOfGames
}