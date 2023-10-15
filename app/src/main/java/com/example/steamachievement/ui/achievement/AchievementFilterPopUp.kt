package com.example.steamachievement.ui.achievement

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steamachievement.AchievementViewModel
import com.example.steamachievement.R
import com.example.steamachievement.ui.game.FilterItem
import com.example.steamachievement.ui.game.SortItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AchievementFilterPopUp(
    achievementViewModel: AchievementViewModel = viewModel()
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val currentSection = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val density = LocalDensity.current
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        MenuHeader(
            onClick1 = {
                coroutineScope.launch {
                    val currentVisibleItem = calculateCurrentVisibleItem(currentSection)
                    if (currentVisibleItem > 0) {
                        currentSection.animateScrollBy(
                            value = with(density) { screenWidth.toPx() * currentVisibleItem * -1 },
                            animationSpec = tween(durationMillis = 500)
                        )
                    }
                }
            },
            onClick2 = {
                coroutineScope.launch {
                    val currentVisibleItem = calculateCurrentVisibleItem(currentSection)
                    if (currentVisibleItem == 2) {
                        currentSection.animateScrollBy(
                            value = with(density) { screenWidth.toPx() * -1 },
                            animationSpec = tween(durationMillis = 500)
                        )
                    } else if (currentVisibleItem == 0) {
                        currentSection.animateScrollBy(
                            value = with(density) { screenWidth.toPx() },
                            animationSpec = tween(durationMillis = 500)
                        )
                    }
                }
            },
            currentSection = currentSection)

        Spacer(
            modifier = Modifier
                .height(0.3f.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        LazyRow(
            state = currentSection,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = currentSection),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
            modifier = Modifier
                .height(150.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item { FilterBody(achievementViewModel) }
            item { SortBody(achievementViewModel) }
        }
    }
}

@Composable
private fun FilterBody(
    achievementViewModel: AchievementViewModel = viewModel()
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier.width(screenWidth)
    ) {
        FilterItem(
            text = stringResource(R.string.filter_completed),
            iconState = achievementViewModel.filterList[0],
            onClick = { achievementViewModel.changeFilter(0) }
        )
    }
}

@Composable
private fun SortBody(
    achievementViewModel: AchievementViewModel = viewModel()
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier.width(screenWidth)
    ) {
        SortItem(
            text = stringResource(R.string.sort_alphabetically),
            iconState = achievementViewModel.sortList[0],
            onClick = { achievementViewModel.changeSort(0) }
        )
        SortItem(
            text = stringResource(R.string.steamachiorder),
            iconState = achievementViewModel.sortList[1],
            onClick = { achievementViewModel.changeSort(1) }
        )
    }
}

@Composable
private fun MenuHeader(
    onClick1: () -> Unit,
    onClick2: () -> Unit,
    currentSection: LazyListState
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val currentVisibleItem = calculateCurrentVisibleItem(currentSection)

        Button(
            onClick = onClick1,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = if (currentVisibleItem == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            ),
        ) {
            Text(stringResource(R.string.filter))
        }

        Button(
            onClick = onClick2,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = if (currentVisibleItem == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            ),
        ) {
            Text(stringResource(R.string.sort))
        }
    }
}

private fun calculateCurrentVisibleItem(currentSection: LazyListState): Int {
    if (currentSection.firstVisibleItemScrollOffset > 100) {
        return currentSection.firstVisibleItemIndex + 1
    }
    return currentSection.firstVisibleItemIndex
}
