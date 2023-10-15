package com.example.steamachievement

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steamachievement.util.calculatePercentageGameCompletion
import com.example.steamachievement.util.database.changeGameLoaded
import com.example.steamachievement.util.database.deleteGameData
import com.example.steamachievement.util.database.isGameLoaded
import com.example.steamachievement.util.database.loadAchievementsFromDatabase
import com.example.steamachievement.util.database.loadSteamAPIKeyFromDatabase
import com.example.steamachievement.util.database.loadSteamIDFromDatabase
import com.example.steamachievement.util.database.readAchievementsIntoDatabase
import com.example.steamachievement.util.database.readGamesIntoDatabase
import com.example.steamachievement.util.database.readPartialAchievementsIntoDatabase
import com.example.steamachievement.util.database.saveAchievementsLinks
import com.example.steamachievement.util.database.saveGameGuideLink
import com.example.steamachievement.util.database.updateGamePercentageCompletion
import com.example.steamachievement.util.network.downloadAchievementsImage
import com.example.steamachievement.util.network.downloadGamesImage
import com.example.steamachievement.util.network.fetchGameAchievement
import com.example.steamachievement.util.network.isNetworkAvailable
import com.example.steamachievement.util.network.loadGameFromAppID
import com.example.steamachievement.util.network.loadPTlinks
import com.example.steamachievement.util.network.loadTAlinks
import com.example.steamachievement.util.network.updateGameAchievementCompletion
import com.example.steamachievement.util.storage.deleteAchievementImage
import com.example.steamachievement.util.storage.deleteGameImage
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.lang.NullPointerException

class AchievementViewModel: ViewModel() {
    private val _achievementsOriginal = mutableStateListOf<Achievement>()
    private val _achievements = mutableStateListOf<Achievement>()
    private val _filterList = listOf(0).toMutableStateList()
    private val _sortList = listOf(0, 0).toMutableStateList()

    val achievements: List<Achievement>
        get() = _achievements

    val filterList: List<Int>
        get() = _filterList

    val sortList: List<Int>
        get() = _sortList

    fun clearAchievements() {
        _achievementsOriginal.clear()
        _achievements.clear()
    }

    fun initialization(context: Context, game: Game) {
        val db = AppDatabase(context)
        val reader = db.readableDatabase
        val writer = db.writableDatabase
        val steamID = loadSteamIDFromDatabase(reader)
        val steamAPIKey = loadSteamAPIKeyFromDatabase(reader)

        if (isGameLoaded(reader, game)) {
            _achievementsOriginal.addAll(loadAchievementsFromDatabase(reader, game))
            if (isNetworkAvailable(context)) {
                updateGameAchievementCompletion(_achievementsOriginal, game, steamID, steamAPIKey)
                game.percentageCompletion = calculatePercentageGameCompletion(_achievementsOriginal)
                updateGamePercentageCompletion(writer, game)
                readAchievementsIntoDatabase(writer, _achievementsOriginal)
                downloadAchievementsImage(game.appID, context, _achievementsOriginal)
            }
            _achievements.addAll(_achievementsOriginal)
        } else {
            if (!isNetworkAvailable(context)) {
                _achievementsOriginal.add(
                    Achievement(
                        displayName = "No Internet",
                        description = "This game achievements are not loaded and need internet connection to load"
                    )
                )
                _achievements.addAll(_achievementsOriginal)
            } else {
                if (game.achievements.isEmpty()) {
                    game.achievements.addAll(loadAchievementsFromDatabase(reader, game))
                }
                if (game.achievements.isEmpty()) {
                    _achievementsOriginal.add(
                        Achievement(
                            displayName = "No Achievement",
                            description = "This game has no achievement"
                        )
                    )
                    _achievements.addAll(_achievementsOriginal)
                } else {
                    fetchGameAchievement(
                        achievements = _achievementsOriginal,
                        game = game,
                        steamAPIKey = steamAPIKey
                    )
                    downloadAchievementsImage(game.appID, context, _achievementsOriginal)
                    _achievements.addAll(_achievementsOriginal)
                    readAchievementsIntoDatabase(writer, _achievementsOriginal)
                    changeGameLoaded(writer, game)
                    game.loaded = true
                }
            }
        }
    }

    fun refreshAchievements(context: Context, game: Game) {
        val db = AppDatabase(context)
        val reader = db.readableDatabase
        val writer = db.writableDatabase
        val steamID = loadSteamIDFromDatabase(reader)
        val steamAPIKey = loadSteamAPIKeyFromDatabase(reader)

        updateGameAchievementCompletion(_achievementsOriginal, game, steamID, steamAPIKey)
        readAchievementsIntoDatabase(writer, _achievementsOriginal)
        downloadAchievementsImage(game.appID, context, _achievementsOriginal)
        game.percentageCompletion = calculatePercentageGameCompletion(_achievementsOriginal)
        updateGamePercentageCompletion(writer, game)
        _achievements.clear()
        _achievements.addAll(_achievementsOriginal)
    }

    fun reloadGame(context: Context, game: Game, gameViewModel: GameViewModel) {
        val db = AppDatabase(context)
        val reader = db.readableDatabase
        val writer = db.writableDatabase
        val steamID = loadSteamIDFromDatabase(reader)
        val steamAPIKey = loadSteamAPIKeyFromDatabase(reader)

        deleteGameImage(game.appID, context)
        deleteAchievementImage(game.appID, context)
        deleteGameData(game.appID, writer)
        _achievementsOriginal.clear()
        _achievements.clear()

        CoroutineScope(Dispatchers.IO).launch {
            val newGame = loadGameFromAppID(steamID, steamAPIKey, game.appID)
            fetchGameAchievement(_achievementsOriginal, game, steamAPIKey)
            downloadGamesImage(listOf(game), context, gameViewModel)
            downloadAchievementsImage(game.appID, context, _achievementsOriginal)
            gameViewModel.replaceGame(game, newGame)
            gameViewModel.changeClickedGame(newGame)
            readGamesIntoDatabase(writer, listOf(newGame))
            readPartialAchievementsIntoDatabase(writer, listOf(newGame))
            readAchievementsIntoDatabase(writer, _achievementsOriginal)
            _achievements.addAll(_achievementsOriginal)
        }
    }

    fun serializeAchievements(
        context: Context,
        achievementsLink: String,
        game: Game
    ) {
        var output = ""
        var siteName = ""

        if (!isNetworkAvailable(context)) {
            Toast.makeText(
                context,
                context.getString(R.string.toast_nointernet),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                if (achievementsLink.contains(Regex("https:\\/\\/www\\.trueachievements\\.com\\/game\\/.+\\/achievements"))) {
                    siteName = "TrueAchievement"
                    output = loadTAlinks(
                        achievements = _achievements,
                        link = achievementsLink
                    )
                    game.achievementsLink = achievementsLink
                    saveAchievementsLinks(context, _achievements)
                    saveGameGuideLink(context, game)
                } else if (achievementsLink.contains(Regex("https:\\/\\/www\\.playstationtrophies\\.org\\/game\\/.+\\/trophies"))) {
                    siteName = "PlaystationTrophies"
                    output = loadPTlinks(
                        achievements = _achievements,
                        link = achievementsLink
                    )
                    game.achievementsLink = achievementsLink
                    saveAchievementsLinks(context, _achievements)
                    saveGameGuideLink(context, game)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_invalid_url),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    if (output.split("/")[0].trim().toInt() != 0) {
                        val toast = Toast.makeText(
                            context,
                            output + context.getString(R.string.achievement_from) + siteName + context.getString(
                                R.string.failed_to_load
                            ),
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    } else {
                        val toast = Toast.makeText(
                            context,
                            context.getString(R.string.success_loaded_all_achievement_from) + siteName,
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }
            }
        }
    }

    fun changeFilter(index: Int) {
        _filterList[index] += 1
        if (_filterList[index] == 3) {
            _filterList[index] = 0
        }

        applyFilter()
    }

    private fun applyFilter() {
        for (achievement in _achievementsOriginal) {
            if (!_achievements.contains(achievement)) {
                _achievements.add(achievement)
            }
        }

        if (_filterList[0] == 1) {
            println("FIRST")
            _achievements.retainAll { it.achieved == 1 }
        } else if (_filterList[0] == 2) {
            println("SECOND")
            _achievements.retainAll { it.achieved == 0 }
        }
    }

    fun changeSort(index: Int) {
        _sortList[index] += 1
        if (_sortList[index] == 3) {
            _sortList[index] = 0
        }

        for (i in 0 until _sortList.size) {
            if (i != index) {
                _sortList[i] = 0
            }
        }

        applySort()
    }

    private fun applySort() {
        if (_sortList[0] == 1) {
            _achievements.sortBy { it.displayName }
        } else if (_sortList[0] == 2) {
            _achievements.sortByDescending { it.displayName }
        }

        if (_sortList[1] == 1) {
            _achievements.clear()
            _achievements.addAll(_achievementsOriginal)
        }
    }

    fun filterByName(searchText: String) {
        for (achievement in _achievementsOriginal) {
            if (!_achievements.contains(achievement)) {
                _achievements.add(achievement)
            }
        }

        _achievements.retainAll { it.displayName.contains(searchText, true) || it.description.contains(searchText, true) }
        _achievements.sortBy { it.displayName }
    }
}

private fun createAchievements() = List(30) { i -> Achievement(displayName = i.toString(), apiname = "API_NAME", description = "Description", achieved = 0) }