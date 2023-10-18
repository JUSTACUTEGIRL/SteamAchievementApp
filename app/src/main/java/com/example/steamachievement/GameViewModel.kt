package com.example.steamachievement

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.steamachievement.util.database.changeFirstTime
import com.example.steamachievement.util.database.isFirstTime
import com.example.steamachievement.util.database.loadGamesFromDatabase
import com.example.steamachievement.util.database.loadSteamAPIKeyFromDatabase
import com.example.steamachievement.util.database.loadSteamIDFromDatabase
import com.example.steamachievement.util.database.readGamesIntoDatabase
import com.example.steamachievement.util.database.readPartialAchievementsIntoDatabase
import com.example.steamachievement.util.database.readPreferences
import com.example.steamachievement.util.database.savePreferences
import com.example.steamachievement.util.network.downloadGamesImage
import com.example.steamachievement.util.network.fetchAllGameData
import com.example.steamachievement.util.network.isNetworkAvailable
import com.example.steamachievement.util.network.refreshGameList

class GameViewModel : ViewModel() {
    private val _originalGames = mutableStateListOf<Game>()
    private val _games = mutableStateListOf<Game>()
    private var _filter = mutableStateListOf("0", "0", "0", "0")
    private var _sort = mutableStateListOf("1", "0")
    private var _display by mutableIntStateOf(0)
    private var _loadingLog by mutableStateOf("")
    private var _clickedGame by mutableStateOf(Game(appID = "0", gameName = "Default"))

    val games: List<Game>
        get() = _games
    val filter: SnapshotStateList<String>
        get() = _filter
    val sort: SnapshotStateList<String>
        get() = _sort

    val display: Int
        get() = _display

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

        // Read Preferences from Database
        val (filter, sort, display) = readPreferences(reader)
        _filter = filter.toMutableStateList()
        _sort = sort.toMutableStateList()
        _display = display

        applyFilter()
        applySort()
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
        applySort()
    }

    fun changeSort(index: Int, context: Context) {
        _sort[index] = (_sort[index].toInt() + 1).toString()
        if (_sort[index] == "3") {
            _sort[index] = "0"
        }

        for (i in 0 until _sort.size) {
            if (i != index) {
                _sort[i] = "0"
            }
        }

        savePreferences(_filter, _sort, _display, AppDatabase(context).writableDatabase)

        applySort()
    }

    private fun applySort() {
        if (_sort[0] == "1") {
            _games.sortBy { it.gameName }
        } else if (_sort[0] == "2") {
            _games.sortByDescending { it.gameName }
        }

        if (_sort[1] == "1") {
            _games.sortByDescending { it.percentageCompletion }
        } else if (_sort[1] == "2") {
            _games.sortBy { it.percentageCompletion }
        }
    }

    fun changeFilter(index: Int, context: Context) {
        _filter[index] = (_filter[index].toInt() + 1).toString()
        if (_filter[index] == "3") {
            _filter[index] = "0"
        }

        savePreferences(_filter, _sort, _display, AppDatabase(context).writableDatabase)

        applyFilter()
        applySort()
    }

    private fun applyFilter() {
        for (game in _originalGames) {
            if (!_games.contains(game)) {
                _games.add(game)
            }
        }

        if (_filter[0] == "1") {
            _games.retainAll { game -> game.isCompleted }
        } else if (_filter[0] == "2") {
            _games.retainAll { game -> !game.isCompleted }
        }

        if (_filter[1] == "1") {
            _games.retainAll { game -> game.percentageCompletion in 1..100 }
        } else if (_filter[1] == "2") {
            _games.retainAll { game -> !(game.percentageCompletion in 1..100) }
        }

        if (_filter[2] == "1") {
            _games.retainAll { game -> game.hasAchievement }
        } else if (_filter[2] == "2") {
            _games.retainAll { game -> !game.hasAchievement }
        }
    }

    fun selectDisplay(display: Int, context: Context) {
        _display = display
        savePreferences(_filter, _sort, _display, AppDatabase(context).writableDatabase)
    }

    fun replaceGame(old: Game, new: Game) {
        var index = _originalGames.indexOf(old)
        _originalGames[index] = new
        index = _games.indexOf(old)
        _games[index] = new
    }
}

private fun createGame() = List(30) { i -> Game(appID = i.toString(), gameName = "There is nothing wrong or right, but thinking makes it so", achievements = mutableListOf() ) }
