package com.example.steamachievement.ui.achievement

import android.view.Gravity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steamachievement.AchievementViewModel
import com.example.steamachievement.Game
import com.example.steamachievement.GameViewModel
import com.example.steamachievement.R
import com.example.steamachievement.util.database.saveAchievementsLinks
import com.example.steamachievement.util.database.saveGameGuideLink
import com.example.steamachievement.util.network.isNetworkAvailable
import com.example.steamachievement.util.network.loadPTlinks
import com.example.steamachievement.util.network.loadTAlinks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun AchievementScreen(
    achievementViewModel: AchievementViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel(),
    onClickGuideLink: () -> Unit,
    onClickBack: () -> Unit,
    game: Game
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchShown by remember { mutableStateOf(false) }
    var isMenuShown by remember { mutableStateOf(false) }
    var isGuideDialogShown by remember { mutableStateOf(false) }
    var isDialogShown by remember { mutableStateOf(false) }
    var isFilterShown by remember { mutableStateOf(false) }
    var isHelpShown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var loaded by remember { mutableStateOf(false) }
    var guideLink by remember { mutableStateOf(game.guideLink) }
    var achievementsLink by remember { mutableStateOf(game.achievementsLink) }

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        if (isNetworkAvailable(context)) {
            refreshing = true

            withContext(Dispatchers.IO) {
                achievementViewModel.refreshAchievements(context, game)
            }

            Toast.makeText(
                context,
                context.getString(R.string.toast_achievement_updated),
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

    Column(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                focusManager.clearFocus()
            }
        }
    ) {
        LaunchedEffect(Unit) {
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                achievementViewModel.clearAchievements()
                achievementViewModel.initialization(context, game)
            }
            loaded = true
        }

        Box {
            if (isSearchShown) {
                AchievementTopBarWithSearch(
                    focusRequester = focusRequester,
                    onClickGuideLink = onClickGuideLink,
                    onClickReturn = {
                        focusManager.clearFocus()
                        isSearchShown = !isSearchShown
                    },
                    onClickFilter = {
                        focusManager.clearFocus()
                        isFilterShown = !isFilterShown
                    },
                    onClickMore = {
                        focusManager.clearFocus()
                        isMenuShown = !isMenuShown
                    },
                    text = searchText,
                    onValueChangeTextField = {
                        searchText = it
                        achievementViewModel.filterByName(searchText)
                    },
                    onClickClear = { searchText = "" },
                    game = game
                )
            } else {
                AchievementTopBarWithoutSearch(
                    onClickGuideLink = onClickGuideLink,
                    onClickBack = onClickBack,
                    onClickFilter = { isFilterShown = !isFilterShown },
                    onClickSearch = { isSearchShown = !isSearchShown },
                    onClickMore = { isMenuShown = !isMenuShown },
                    game = game
                )
            }

            DropdownMenu(
                expanded = isMenuShown,
                onDismissRequest = { isMenuShown = false },
                offset = DpOffset(x = 200.dp, y = -50.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.change_game_guide_link)) },
                    onClick = {
                        isMenuShown = false
                        isGuideDialogShown = true
                    },
                    modifier = Modifier.background(Color.Transparent)
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.change_game_ptta_link)) },
                    onClick = {
                        isMenuShown = false
                        isDialogShown = true
                    },
                    modifier = Modifier.background(Color.Transparent)
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.reloadgameachievement)) },
                    onClick = {
                        isMenuShown = false
                        achievementViewModel.reloadGame(context, game, gameViewModel)
                    },
                    modifier = Modifier.background(Color.Transparent)
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.help)) },
                    onClick = {
                        isMenuShown = false
                        isHelpShown = true
                    },
                    modifier = Modifier.background(Color.Transparent)
                )
            }
        }

        if (!loaded && !game.loaded) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.doing_first_time_setup))
                }
            }
        } else {
            Box(
                modifier = Modifier.pullRefresh(state)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = "Total: ${achievementViewModel.achievements.size}   Completed: ${achievementViewModel.achievements.filter { it.achieved == 1 }.size}",
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxHeight()
                    ) {
                        items(
                            items = achievementViewModel.achievements,
                            key = { it.apiname }
                        ) { achievement ->
                            AchievementItem(
                                appID = game.appID,
                                achievement = achievement,
                                modifier = Modifier.animateItemPlacement(),
                                focusRequester = focusRequester
                            )
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = refreshing,
                    state = state,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    if (isGuideDialogShown) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            LinkDialog(
                placeholder = stringResource(R.string.game_guide_dialog_placeholder),
                link = guideLink,
                onDismissRequest = { isGuideDialogShown = false },
                onValueChange = { guideLink = it },
                onClickConfirm = {
                    game.guideLink = guideLink
                    saveGameGuideLink(context, game)
                    isGuideDialogShown = false
                    isMenuShown = false
                }
            )
        }
    }

    if (isDialogShown) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            LinkDialog(
                placeholder = stringResource(R.string.achievement_link_placeholder),
                link = achievementsLink,
                onDismissRequest = { isDialogShown = false },
                onValueChange = { achievementsLink = it },
                onClickConfirm = {
                    isDialogShown = false
                    isMenuShown = false

                    achievementViewModel.serializeAchievements(
                        context = context,
                        achievementsLink = achievementsLink,
                        game = game
                    )
                }
            )
        }
    }

    AnimatedVisibility(visible = isFilterShown) {
        ModalBottomSheet(
            onDismissRequest = { isFilterShown = false },
            sheetState = rememberModalBottomSheetState(),
            dragHandle = {},
            shape = MaterialTheme.shapes.medium,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            AchievementFilterPopUp(achievementViewModel)
        }
    }

    if (isHelpShown) {
        AchievementHelpDialog(onDismiss = { isHelpShown = false })
    }
}