package com.example.steamachievement.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.steamachievement.Game
import com.example.steamachievement.R
import java.io.File


@Composable
fun GameItemCompact(
    game: Game,
    onClickGame: () -> Unit,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = modifier.clickable(onClick = onClickGame)
    ) {
        GamePercentageComplete(game = game)

        Box {
//            Image(
//                painter = painterResource(id = R.drawable.image),
//                contentDescription = null,
//                contentScale = ContentScale.FillHeight,
//                modifier = Modifier
//                    .aspectRatio(6f / 9f)
//            )

            GameImage(appID = game.appID)
            Box(modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)), 375.0f))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Text(
                    text = game.gameName,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    modifier = Modifier.padding(7.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        shadow = Shadow(color = Color.Black, offset = Offset(0f, 0f), blurRadius = 8f)
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
fun GameItemCompactPreview() {
    GameItemCompact(game = Game(gameName = "Jitsu", appID = "0"), onClickGame = { /*TODO*/ }, modifier = Modifier)
}

@Composable
fun GameItemComfortable(
    game: Game,
    onClickGame: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClickGame)
    ) {
        Card(
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.clickable(onClick = onClickGame)
        ) {
            GamePercentageComplete(game = game)

            Box {
                GameImage(game.appID)
            }
        }
        Text(
            text = game.gameName,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(7.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun GameItemCoverOnly(
    game: Game,
    onClickGame: () -> Unit,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = modifier.clickable(onClick = onClickGame)
    ) {
        GamePercentageComplete(game = game)

        Box {
            GameImage(game.appID)
        }
    }
}

@Composable
private fun GameImage(appID: String) {
    val context = LocalContext.current
    val verticalDir = File(context.filesDir.toString() + "/game/vertical/$appID.jpg")
    val horizontalDir = File(context.filesDir.toString() + "/game/horizontal/$appID.jpg")

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(if (verticalDir.length() == 0L) horizontalDir else verticalDir)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillHeight,
        placeholder = painterResource(R.drawable.transparent),
        fallback = painterResource(R.drawable.transparent),
        modifier = Modifier.aspectRatio(6f / 9f)
    )
}

@Composable
private fun GamePercentageComplete(game: Game) {
    val percent = game.percentageCompletion
    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = if (percent == 100) Color(0xFF1A9FFF)
            else Color(0xFF3D4450),
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (percent == 100) {
            Text(
                text = stringResource(id = R.string.completed100),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        } else if (percent == -1) {
            Text(
                text = stringResource(id = R.string.no_achievement),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        } else {
            Text(
                text = "$percent" + stringResource(id = R.string.completed_any),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }
    }
}
