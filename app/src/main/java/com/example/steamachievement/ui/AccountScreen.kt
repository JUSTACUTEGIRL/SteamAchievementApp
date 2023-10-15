package com.example.steamachievement.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.steamachievement.R
import com.example.steamachievement.util.database.readUserDataIntoDatabase

@Composable
fun AccountScreen(
    navChangeConfirm: () -> Unit
) {
    var isHelpShown by rememberSaveable { mutableStateOf(false) }
    var steamID by rememberSaveable { mutableStateOf("") }
    var steamAPIKey by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.width(310.dp)
            ) {
                Text(
                    text = stringResource(R.string.steamachievementmanager),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(0.dp, 120.dp, 0.dp, 0.dp)
                )

                TextField(
                    value = steamID,
                    placeholder = { Text(stringResource(R.string.steamid)) },
                    onValueChange = { steamID = it },
                    singleLine = true,
                    modifier = Modifier
                        .padding(0.dp, 40.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                )
                TextField(
                    value = steamAPIKey,
                    placeholder = { Text(stringResource(R.string.steamapikey)) },
                    onValueChange = { steamAPIKey = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = { isHelpShown = true },
                    contentPadding = PaddingValues(vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.dontknow))
                }

                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        readUserDataIntoDatabase(
                            context = context,
                            SteamID = steamID,
                            SteamAPIKey = steamAPIKey
                        )
                        navChangeConfirm()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.confirm))
                }

                Text(
                    text = stringResource(id = R.string.account_warning),
                    modifier = Modifier.padding(vertical = 5.dp)
                    )
            }
        }

        if (isHelpShown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                HelpDialog(onDismissRequest = { isHelpShown = false })
            }
        }
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    AccountScreen(
        navChangeConfirm = {}
    )
}