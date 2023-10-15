package com.example.steamachievement.ui.game

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steamachievement.GameViewModel
import com.example.steamachievement.R
import com.example.steamachievement.util.network.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    navChange: () -> Unit
) {
    val context = LocalContext.current
    val gameList = gameViewModel.games
    val typeOfCard = gameViewModel.displayList
    var isFilterShown by remember { mutableStateOf(false) }
    var isSearchShown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var searchText by rememberSaveable { mutableStateOf("") }
    var sheetState = rememberModalBottomSheetState()

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        if (isNetworkAvailable(context)) {
            refreshing = true
            var count = 0

            withContext(Dispatchers.IO) {
                count = gameViewModel.refreshGames(context)
            }

            Toast.makeText(
                context,
                "$count " + context.getString(R.string.toast_gameadded),
                Toast.LENGTH_SHORT
            ).show()

            refreshing = false
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.toast_nointernet),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            if (isSearchShown) {
                TopBarWithSearch(
                    focusRequester = focusRequester,
                    onClickReturn = {
                        focusManager.clearFocus()
                        isSearchShown = !isSearchShown
                    },
                    text = searchText,
                    onValueChangeTextField = { newString ->
                        searchText = newString
                        gameViewModel.filterByName(searchText)
                    },
                    onClickClear = {
                        searchText = ""
                        gameViewModel.filterByName(searchText)
                    },
                    onClickFilter = {
                        focusManager.clearFocus()
                        isFilterShown = !isFilterShown
                    }
                )
            } else {
                TopBarWithoutSearch(
                    onClickFilter = { isFilterShown = !isFilterShown },
                    onClickSearch = { isSearchShown = !isSearchShown }
                )
            }

            Box(
                modifier = Modifier.pullRefresh(state)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    items(
                        items = gameList,
                        key = { it.appID }
                    ) { game ->
                        if (typeOfCard[0]) {
                            GameItemCompact(
                                game = game,
                                onClickGame = {
                                    gameViewModel.changeClickedGame(game)
                                    navChange()
                                },
                                modifier = Modifier.animateItemPlacement()
                            )
                        } else if (typeOfCard[1]) {
                            GameItemComfortable(
                                game = game,
                                onClickGame = {
                                    gameViewModel.changeClickedGame(game)
                                    navChange()
                                },
                                modifier = Modifier.animateItemPlacement()
                            )
                        } else if (typeOfCard[2]) {
                            GameItemCoverOnly(
                                game = game,
                                onClickGame = {
                                    gameViewModel.changeClickedGame(game)
                                    navChange()
                                },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = refreshing,
                    state = state,
                    Modifier.align(Alignment.TopCenter)
                )
            }
        }

        if (isFilterShown) {
            ModalBottomSheet(
                onDismissRequest = { isFilterShown = !isFilterShown },
                sheetState = sheetState,
                dragHandle = {},
                shape = MaterialTheme.shapes.medium,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                GameFilterPopUp(gameViewModel)
            }
        }
    }
}

