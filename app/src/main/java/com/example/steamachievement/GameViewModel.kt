package com.example.steamachievement

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.steamachievement.util.database.changeFirstTime
import com.example.steamachievement.util.database.isFirstTime
import com.example.steamachievement.util.database.loadGamesFromDatabase
import com.example.steamachievement.util.database.loadSteamAPIKeyFromDatabase
import com.example.steamachievement.util.database.loadSteamIDFromDatabase
import com.example.steamachievement.util.database.readGamesIntoDatabase
import com.example.steamachievement.util.database.readPartialAchievementsIntoDatabase
import com.example.steamachievement.util.network.downloadGamesImage
import com.example.steamachievement.util.network.fetchAllGameData
import com.example.steamachievement.util.network.isNetworkAvailable
import com.example.steamachievement.util.network.refreshGameList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _originalGames = mutableStateListOf<Game>()
    private val _games = mutableStateListOf<Game>()
    private val _filterList = listOf(0, 0, 0, 0).toMutableStateList()
    private val _sortList = listOf(0, 0, 0).toMutableStateList()
    private val _displayList = listOf(true, false, false).toMutableStateList()
    private var _loadingLog by mutableStateOf("")
    private var _clickedGame by mutableStateOf(Game(appID = "0", gameName = "Default"))

    val games: List<Game>
        get() = _games

    val filterList: List<Int>
        get() = _filterList

    val sortList: List<Int>
        get() = _sortList

    val displayList: List<Boolean>
        get() = _displayList

    val loadingLog: String
        get() = _loadingLog

    val clickedGame: Game
        get() = _clickedGame

    fun changeClickedGame(game: Game) {
        _clickedGame = game
    }

    fun changeLoadingLog(text: String) {
        _loadingLog = text
    }

    fun initialization(context: Context) {
        val db = AppDatabase(context)
        val reader = db.readableDatabase
        val writer = db.writableDatabase
        val steamID = loadSteamIDFromDatabase(reader)
        val steamAPIKey = loadSteamAPIKeyFromDatabase(reader)

        if (isFirstTime(reader)) {
            if (isNetworkAvailable(context)) {
                _originalGames.addAll(
                    fetchAllGameData(
                        steamID = steamID,
                        steamAPIKey = steamAPIKey,
                        gameViewModel = this
                    )
                )
                downloadGamesImage(_originalGames, context, this)
                _games.addAll(_originalGames.toMutableStateList())
                changeLoadingLog("Changing First Time")
                changeFirstTime(writer)
                changeLoadingLog("Reading Games Into Databases")
                readGamesIntoDatabase(writer, _originalGames)
                changeLoadingLog("Reading Partial Achievements Into Databases")
                readPartialAchievementsIntoDatabase(writer, _originalGames)
            } else {
                _loadingLog = "NO INTERNET"
            }
        } else {
            _originalGames.addAll(loadGamesFromDatabase(reader))
            _games.addAll(_originalGames)
        }
    }

    fun refreshGames(context: Context): Int {
        val db = AppDatabase(context)
        val reader = db.readableDatabase
        val writer = db.writableDatabase
        val steamID = loadSteamIDFromDatabase(reader)
        val steamAPIKey = loadSteamAPIKeyFromDatabase(reader)

        val listOfNewGames = refreshGameList(_originalGames, steamID, steamAPIKey)

        readGamesIntoDatabase(writer, listOfNewGames)
        readPartialAchievementsIntoDatabase(writer, listOfNewGames)

        _originalGames.addAll(listOfNewGames)
        downloadGamesImage(_originalGames, context, this)

        _games.clear()
        _games.addAll(_originalGames)
        return listOfNewGames.size
    }

    fun filterByName(text: String) {
        for (game in _originalGames) {
            if (!_games.contains(game)) {
                _games.add(game)
            }
        }
        _games.retainAll { game -> game.gameName.contains(other = text, ignoreCase = true) }
        _games.sortBy { game -> game.gameName }
    }

    fun changeSort(index: Int) {
        _sortList[index] = _sortList[index] + 1
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
            _games.sortBy { it.gameName }
        } else if (_sortList[0] == 2) {
            _games.sortByDescending { it.gameName }
        }

        if (_sortList[1] == 1) {
            _games.sortBy { it.isCompleted }
        } else if (_sortList[1] == 2) {
            _games.sortByDescending { it.isCompleted }
        }

        if (_sortList[2] == 1) {
            _games.sortByDescending { it.percentageCompletion }
        } else if (_sortList[2] == 2) {
            _games.sortBy { it.percentageCompletion }
        }
    }

    fun changeFilter(index: Int) {
        _filterList[index] = _filterList[index] + 1
        if (_filterList[index] == 3) {
            _filterList[index] = 0
        }

        applyFilter()
        applySort()
    }

    private fun applyFilter() {
        for (game in _originalGames) {
            if (!_games.contains(game)) {
                _games.add(game)
            }
        }

        if (_filterList[0] == 1) {
            _games.retainAll { game -> game.isCompleted }
        } else if (_filterList[0] == 2) {
            _games.retainAll { game -> !game.isCompleted }
        }

        if (_filterList[1] == 1) {
            _games.retainAll { game -> game.percentageCompletion in 1..100 }
        } else if (_filterList[1] == 2) {
            _games.retainAll { game -> !(game.percentageCompletion in 1..100) }
        }

        if (_filterList[2] == 1) {
            _games.retainAll { game -> game.hasAchievement }
        } else if (_filterList[2] == 2) {
            _games.retainAll { game -> !game.hasAchievement }
        }
    }

    fun selectDisplay(index: Int) {
        for (i in 0 until _displayList.size) {
            _displayList[i] = false
        }
        _displayList[index] = true
    }

    fun replaceGame(old: Game, new: Game) {
        var index = _originalGames.indexOf(old)
        _originalGames[index] = new
        index = _games.indexOf(old)
        _games[index] = new
    }
}

private fun createGame() = List(30) { i -> Game(appID = i.toString(), gameName = "There is nothing wrong or right, but thinking makes it so", achievements = mutableListOf() ) }
