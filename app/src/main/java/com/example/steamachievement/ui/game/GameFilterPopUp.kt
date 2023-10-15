package com.example.steamachievement.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steamachievement.GameViewModel
import com.example.steamachievement.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameFilterPopUp(
    gameViewModel: GameViewModel = viewModel()
) {
    val currentSection = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
            onClick3 = {
                coroutineScope.launch {
                    val currentVisibleItem = calculateCurrentVisibleItem(currentSection)
                    if (currentVisibleItem < 2) {
                        currentSection.animateScrollBy(
                            value = with(density) { screenWidth.toPx() * (2 - currentVisibleItem) },
                            animationSpec = tween(durationMillis = 300)
                        )
                    }
                }
            },
            currentSection = currentSection
        )

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
            modifier = Modifier.height(190.dp)
        ) {
            item { FilterBody(gameViewModel) }
            item { SortBody(gameViewModel) }
            item { DisplayBody(gameViewModel) }
        }
    }
}

@Composable
private fun MenuHeader(
    onClick1: () -> Unit,
    onClick2: () -> Unit,
    onClick3: () -> Unit,
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
            Text(stringResource(id = R.string.filter))
        }
        Button(
            onClick = onClick2,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = if (currentVisibleItem == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            ),
        ) {
            Text(stringResource(id = R.string.sort))
        }
        Button(
            onClick = onClick3,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = if (currentVisibleItem == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            ),
        ) {
            Text(stringResource(id = R.string.sort))
        }
    }
}

@Composable
private fun FilterBody(
    gameViewModel: GameViewModel = viewModel()
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val checkboxStateList = gameViewModel.filterList

    Column(
        modifier = Modifier.width(screenWidth)
    ) {
        FilterItem(
            text = stringResource(id = R.string.filter_completed),
            iconState = checkboxStateList[0],
            onClick = { gameViewModel.changeFilter(0) }
        )
        FilterItem(
            text = stringResource(id = R.string.filter_started),
            iconState = checkboxStateList[1],
            onClick = { gameViewModel.changeFilter(1) }
        )
        FilterItem(
            text = stringResource(id = R.string.filter_hasachievement),
            iconState = checkboxStateList[2],
            onClick = { gameViewModel.changeFilter(2) }
        )
    }
}

@Composable
private fun SortBody(
    gameViewModel: GameViewModel = viewModel()
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val iconStateList = gameViewModel.sortList

    Column(
        modifier = Modifier.width(screenWidth)
    ) {
        SortItem(
            text = stringResource(id = R.string.sort_alphabetically),
            iconState = iconStateList[0],
            onClick = { gameViewModel.changeSort(0) }
        )
        SortItem(
            text = stringResource(id = R.string.sort_complepercent),
            iconState = iconStateList[2],
            onClick = { gameViewModel.changeSort(2) }
        )
    }
}

@Composable
private fun DisplayBody(
    gameViewModel: GameViewModel = viewModel()
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val radioButtonStateList = gameViewModel.displayList

    Column(
        modifier = Modifier.width(screenWidth)
    ) {
        DisplayItem(
            text = stringResource(id = R.string.display_compact),
            radioButtonState = radioButtonStateList[0],
            onClick = { gameViewModel.selectDisplay(0) })
        DisplayItem(
            text = stringResource(id = R.string.display_comfort),
            radioButtonState = radioButtonStateList[1],
            onClick = { gameViewModel.selectDisplay(1) })
        DisplayItem(
            text = stringResource(id = R.string.display_coveronly),
            radioButtonState = radioButtonStateList[2],
            onClick = { gameViewModel.selectDisplay(2) })
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FilterItem(
    text: String,
    iconState: Int,
    onClick: () -> Unit
) {
    GeneralItem(
        text = text,
        onClick = onClick
    ) {
        AnimatedContent(
            targetState = iconState,
            transitionSpec = { fadeIn() with fadeOut() }
        ) {
            Icon(
                painter = when (iconState) {
                    0 -> painterResource(R.drawable.round_check_box_outline_blank_24)
                    1 -> painterResource(R.drawable.baseline_check_box_24)
                    2 -> painterResource(R.drawable.close_square_filled)
                    else -> painterResource(R.drawable.round_check_box_outline_blank_24)
                },
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SortItem(
    text: String,
    iconState: Int,
    onClick: () -> Unit
) {
    GeneralItem(
        text = text,
        onClick = onClick
    ) {
        AnimatedContent(
            targetState = iconState,
            transitionSpec = { fadeIn() with fadeOut() }
        ) {
            Icon(
                painter = when (iconState) {
                    0 -> painterResource(R.drawable.empty_icon)
                    1 -> painterResource(R.drawable.baseline_arrow_downward_24)
                    2 -> painterResource(R.drawable.baseline_arrow_upward_24)
                    else -> painterResource(R.drawable.round_check_box_outline_blank_24)
                },
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
fun DisplayItem(
    text: String,
    radioButtonState: Boolean,
    onClick: () -> Unit
) {
    GeneralItem(
        text = text,
        onClick = onClick
    ) {
        RadioButton(selected = radioButtonState, onClick = onClick)
    }
}

@Composable
fun GeneralItem(
    text: String,
    onClick: () -> Unit = {},
    element: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            element()
            Text(text = text)
        }
    }
}

private fun calculateCurrentVisibleItem(currentSection: LazyListState): Int {
    if (currentSection.firstVisibleItemScrollOffset > 100) {
        return currentSection.firstVisibleItemIndex + 1
    }
    return currentSection.firstVisibleItemIndex
}

