package com.example.steamachievement.util

import com.example.steamachievement.Achievement
import com.example.steamachievement.Game
import kotlin.math.roundToInt

fun calculatePercentageGameCompletion(achievements: List<Achievement>): Int {
    var count = 0
    for (achievement in achievements) {
        if (achievement.achieved == 1) {
            count++
        }
    }

    if (achievements.isEmpty()) {
        return 0
    }
    return (count.toFloat() / achievements.size * 100).roundToInt()
}