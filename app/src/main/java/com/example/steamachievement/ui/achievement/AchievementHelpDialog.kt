package com.example.steamachievement.ui.achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.steamachievement.R

@Composable
fun AchievementHelpDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.small,
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(stringResource(R.string.info_changegameguidelink))
                    Text(stringResource(R.string.info_changegamepttalink))
                    Text(stringResource(R.string.info_reloadgameachievements))
                    Text(stringResource(R.string.info_imageicon))
                    Text(stringResource(R.string.info_changeindividually))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AchievementHelpDialogPreview() {
    AchievementHelpDialog(
        onDismiss = {}
    )
}