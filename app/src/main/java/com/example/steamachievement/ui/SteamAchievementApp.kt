package com.example.steamachievement.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.steamachievement.AchievementViewModel
import com.example.steamachievement.AppDatabase
import com.example.steamachievement.Database
import com.example.steamachievement.GameViewModel
import com.example.steamachievement.R
import com.example.steamachievement.ui.achievement.AchievementScreen
import com.example.steamachievement.ui.game.GameScreen
import com.example.steamachievement.ui.game.GuideWebScreen

enum class AppScreen(val title: String) {
    Account(title = "Account Screen"),
    Loading(title = "Loading Screen"),
    Main(title = "Main Screen"),
    Achievement(title = "Achievement Screen"),
    Guide(title = "Guide Web Screen")
}

@Composable
fun SteamAchievementApp(
    navController: NavHostController = rememberNavController()
) {
    val gameViewModel: GameViewModel = viewModel()
    val achievementViewModel: AchievementViewModel = viewModel()
    val context = LocalContext.current

    val db = AppDatabase(context)
    val reader = db.readableDatabase
    val cursor = reader.query(
        Database.UserData.TABLE_NAME,
        arrayOf(Database.UserData.COLUMN_NAME_STEAMID),
        null,
        null,
        null,
        null,
        null
    )

    NavHost(
        navController = navController,
        startDestination = if (cursor.count == 0) AppScreen.Account.name else AppScreen.Loading.name
    ) {
        composable(route = AppScreen.Account.name) {
            AccountScreen(
                navChangeConfirm = { navController.navigate(AppScreen.Loading.name) }
            )
        }
        composable(route = AppScreen.Loading.name) {
            BackHandler(true) {
            }

            LoadingScreen(
                navChange = { navController.navigate(AppScreen.Main.name) },
                gameViewModel = gameViewModel
            )
        }
        composable(route = AppScreen.Main.name) {
            BackHandler(true) {
            }

            GameScreen(
                gameViewModel = gameViewModel,
                navChange = { navController.navigate(AppScreen.Achievement.name) }
            )
        }
        composable(route = AppScreen.Achievement.name) {
            AchievementScreen(
                achievementViewModel = achievementViewModel,
                gameViewModel = gameViewModel,
                onClickBack = { navController.popBackStack() },
                onClickGuideLink = {
                    if (gameViewModel.clickedGame.guideLink.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.toast_urlempty), Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate(AppScreen.Guide.name)
                    }
                },
                game = gameViewModel.clickedGame
            )
        }
        composable(route = AppScreen.Guide.name) {
            GuideWebScreen(link = gameViewModel.clickedGame.guideLink)
        }
    }
}