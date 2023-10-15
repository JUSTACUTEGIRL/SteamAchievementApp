package com.example.steamachievement.ui.achievement

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.steamachievement.Achievement
import com.example.steamachievement.R
import com.example.steamachievement.util.database.saveAchievementNote
import com.example.steamachievement.util.database.saveOneAchievementGuideLink
import java.io.File

@Composable
fun AchievementItem(
    focusRequester: FocusRequester,
    appID: String,
    achievement: Achievement,
    modifier: Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isNoteShown by rememberSaveable { mutableStateOf(false) }
    var isMenuShown by rememberSaveable { mutableStateOf(false) }
    var isLinkDialogShown by rememberSaveable { mutableStateOf(false) }
    var isTextFieldEnabled by rememberSaveable { mutableStateOf(false) }
    var link by rememberSaveable { mutableStateOf(achievement.guideLink) }
    var note by rememberSaveable { mutableStateOf(achievement.note) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val backgroundColor =
        if (achievement.achieved == 1) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)


    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures {
                    if (isTextFieldEnabled) {
                        focusManager.clearFocus()
                        isTextFieldEnabled = false
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row {
                    AchievementImage(
                        appID = appID,
                        achievement = achievement,
                        onClickImage = {
                            if (achievement.guideLink.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_urlempty),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                expanded = !expanded
                            }
                        }
                    )

                    Column(
                        modifier = Modifier.padding(10.dp, 10.dp, 25.dp, 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = achievement.displayName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = achievement.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(vertical = 7.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    AchievementWebScreen(url = achievement.guideLink)
                }

                AnimatedVisibility(visible = isNoteShown) {
                    TextField(
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        value = note,
                        enabled = isTextFieldEnabled,
                        onValueChange = {
                            note = it
                            achievement.note = it
                            saveAchievementNote(context, achievement)
                        },
                        modifier = Modifier.fillMaxWidth()
                            .focusRequester(focusRequester)
                            .clickable { isTextFieldEnabled = true }
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box {
                    DropdownMenu(
                        expanded = isMenuShown,
                        onDismissRequest = { isMenuShown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.change_guide_link)) },
                            onClick = {
                                isLinkDialogShown = true
                                isMenuShown = false
                            },
                            modifier = Modifier.background(Color.Transparent)
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.change_achievement_note)) },
                            onClick = {
                                isNoteShown = true
                                isMenuShown = false
                                isTextFieldEnabled = !isTextFieldEnabled
                            },
                            modifier = Modifier.background(Color.Transparent)
                        )
                    }

                    IconButton(
                        onClick = { isMenuShown = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_more_vert_24),
                            contentDescription = null
                        )
                    }
                }

                IconButton(
                    onClick = { isNoteShown = !isNoteShown }
                ) {
                    if (isNoteShown) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_keyboard_arrow_up_24),
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = null
                        )
                    }
                }
            }

            if (isLinkDialogShown) {
                val context = LocalContext.current

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    LinkDialog(
                        placeholder = stringResource(R.string.achievement_link_placeholder),
                        link = link,
                        onDismissRequest = { isLinkDialogShown = false },
                        onValueChange = { link = it },
                        onClickConfirm = {
                            achievement.guideLink = link
                            saveOneAchievementGuideLink(achievement, context)
                            isLinkDialogShown = false
                            isMenuShown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementImage(
    appID: String,
    achievement: Achievement,
    onClickImage: () -> Unit
) {
    val context = LocalContext.current
    val iconDir = File(context.filesDir.toString() + "/achievement/$appID/${achievement.apiname}_icon.jpg")
    val icongrayDir = File(context.filesDir.toString() + "/achievement/$appID/${achievement.apiname}_icongray.jpg")

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(if (achievement.achieved == 1) iconDir else icongrayDir)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        placeholder = painterResource(R.drawable.transparent),
        fallback = painterResource(R.drawable.transparent),
        modifier = Modifier
            .size(100.dp)
            .padding(10.dp)
            .clickable(onClick = onClickImage)
    )
}