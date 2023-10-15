package com.example.steamachievement.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.steamachievement.R

@Composable
fun HelpDialog(
    onDismissRequest: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            shape = MaterialTheme.shapes.small,
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.steamid) + ":",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://steamdb.info/calculator/")
                        }
                    )

                    Text(
                        text = "\u2022 https://steamdb.info/calculator/",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://steamdb.info/calculator/")
                        }
                    )

                    Text(
                        text = "\u2022 " + stringResource(R.string.steamid_instruction),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)
                    )

                    Text(
                        text = stringResource(R.string.steamapikey) + ":",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://steamcommunity.com/dev/apikey")
                        }
                    )

                    Text(
                        text = "\u2022 https://steamcommunity.com/dev/apikey",
                        softWrap = false,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://steamcommunity.com/dev/apikey")
                        }
                    )

                    Text(
                        text = "\u2022 " + stringResource(R.string.steamapikey_instruction),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun HelpDialogPreview() {
    HelpDialog {

    }
}