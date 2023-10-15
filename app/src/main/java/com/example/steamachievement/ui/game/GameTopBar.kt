package com.example.steamachievement.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.steamachievement.R

@Composable
fun TopBarWithoutSearch(
    onClickFilter: () -> Unit,
    onClickSearch: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .height(55.dp)
    ) {
        Text(
            text = "Game Library",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )

        TopBarButton(onClick = onClickSearch, icon = R.drawable.baseline_search_24)
        TopBarButton(onClick = onClickFilter, icon = R.drawable.baseline_filter_list_24)
    }
}

@Composable
fun TopBarWithSearch(
    focusRequester: FocusRequester,
    onClickReturn: () -> Unit,
    onClickFilter: () -> Unit,
    onClickClear: () -> Unit,
    text: String,
    onValueChangeTextField: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .height(55.dp)
    ) {
        OutlinedTextField(
            value = text,
            singleLine = true,
            onValueChange = onValueChangeTextField,
            placeholder = { Text(stringResource(id = R.string.placeholder_search)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.background,
                unfocusedBorderColor = MaterialTheme.colorScheme.background
            ),
            leadingIcon = {
                IconButton(
                    onClick = onClickReturn
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Return")
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = onClickClear
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .focusRequester(focusRequester)
                .weight(1f)
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        TopBarButton(onClick = onClickFilter, icon = R.drawable.baseline_filter_list_24)
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