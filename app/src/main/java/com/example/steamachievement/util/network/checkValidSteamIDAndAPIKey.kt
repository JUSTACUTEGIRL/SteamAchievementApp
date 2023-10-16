package com.example.steamachievement.util.network

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

fun checkValidSteamIDAndAPIKey(
    steamID: String,
    steamAPIKey: String
): Boolean {
    val (_, _, result) = ("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=${steamAPIKey}&steamid=${steamID}&format=json").httpGet().responseString()
    return when (result) {
        is Result.Failure -> false
        is Result.Success -> true
    }
}