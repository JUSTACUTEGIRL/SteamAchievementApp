package com.example.steamachievement.ui.achievement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.steamachievement.AchievementViewModel
import com.example.steamachievement.Game
import com.example.steamachievement.R
import java.io.File

@Composable
fun AchievementTopBarWithoutSearch(
    onClickGuideLink: () -> Unit,
    onClickBack: () -> Unit,
    onClickFilter: () -> Unit,
    onClickSearch: () -> Unit,
    onClickMore: () -> Unit,
    game: Game
) {
    Box(
        modifier = Modifier.height(110.dp)
    ){
        BackgroundImage(appID = game.appID)

        Column {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 0.dp)
                    .height(50.dp)
            ) {
                TopBarButton(onClick = onClickBack, icon = R.drawable.baseline_arrow_back_24)
                Spacer(modifier = Modifier.weight(1f))
                TopBarButton(onClick = onClickSearch, icon = R.drawable.baseline_search_24)
                TopBarButton(onClick = onClickFilter, icon = R.drawable.baseline_filter_list_24)
                TopBarButton(onClick = onClickMore, icon = R.drawable.baseline_more_vert_24)
            }

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = game.gameName,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.Center)
                        .clickable(onClick = onClickGuideLink)
                )
            }
        }
    }
}

@Composable
fun AchievementTopBarWithSearch(
    focusRequester: FocusRequester,
    onClickReturn: () -> Unit,
    onClickFilter: () -> Unit,
    onClickGuideLink: () -> Unit,
    onClickMore: () -> Unit,
    text: String,
    onValueChangeTextField: (String) -> Unit,
    onClickClear: () -> Unit,
    game: Game
) {
    Box(
        modifier = Modifier.height(110.dp)
    ){
        BackgroundImage(appID = game.appID)

        Column {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(6.5f.dp, 10.dp, 10.dp, 0.dp)
                    .height(50.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = text,
                        singleLine = true,
                        onValueChange = onValueChangeTextField,
                        placeholder = { Text(stringResource(R.string.placeholder_search)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        leadingIcon = {
                            IconButton(
                                onClick = onClickReturn
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            }
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = onClickClear
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .background(Color.Transparent)
                            .focusRequester(focusRequester)
                            .weight(1f)
                    )
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                TopBarButton(onClick = onClickFilter, icon = R.drawable.baseline_filter_list_24)
                TopBarButton(onClick = onClickMore, icon = R.drawable.baseline_more_vert_24)
            }

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = game.gameName,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(Alignment.Center)
                        .clickable(onClick = onClickGuideLink)
                )
            }
        }
    }
}

@Composable
private fun TopBarButton(
    onClick: () -> Unit,
    icon: Int
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(painterResource(icon), contentDescription = null)
    }
}

@Composable
private fun BackgroundImage(appID: String) {
    val context = LocalContext.current
    val horizontalDir = File(context.filesDir.toString() + "/game/horizontal/$appID.jpg")

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(horizontalDir)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        placeholder = painterResource(R.drawable.transparent),
        fallback = painterResource(R.drawable.transparent),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = 0.5f }
            .drawWithContent {
                val colors = listOf(Color.Black, Color.Transparent)
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(colors),
                    blendMode = BlendMode.DstIn
                )
            }
    )
}

@Preview
@Composable
private fun AchievementTopBarPreview() {
    AchievementTopBarWithoutSearch(
        onClickGuideLink = {},
        onClickBack = { /*TODO*/ },
        onClickFilter = { /*TODO*/ },
        onClickSearch = { /*TODO*/ },
        onClickMore = {},
        game = Game(gameName = "Test", appID = "0")
    )
}