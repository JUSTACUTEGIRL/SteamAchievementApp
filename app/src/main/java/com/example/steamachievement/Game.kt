package com.example.steamachievement

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val appID: String,
    val gameName: String,
    var isCompleted: Boolean = false,
    var hasAchievement: Boolean = true,
    var percentageCompletion: Int = 0,
    val achievements: MutableList<Achievement> = mutableListOf(),
    var guideLink: String = "",
    var achievementsLink: String = "",
    var loaded: Boolean = false
)
