package com.example.steamachievement.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steamachievement.GameViewModel
import com.example.steamachievement.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoadingScreen(
    gameViewModel: GameViewModel = viewModel(),
    navChange: () -> Unit
) {
    val context = LocalContext.current
    var log = gameViewModel.loadingLog

    LaunchedEffect(key1 = true) {
        withContext(Dispatchers.IO) {
            gameViewModel.initialization(context)
        }
        navChange()
    }

    Surface() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            LinearProgressIndicator()
            Text(
                text = stringResource(R.string.fetchgamedata),
                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 5.dp)
            )
            Text(
                text = stringResource(R.string.loadingwarning),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            Text(
                text = log,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen(navChange = {})
}