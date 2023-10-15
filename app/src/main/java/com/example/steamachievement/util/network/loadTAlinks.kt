package com.example.steamachievement.util.network

import com.example.steamachievement.Achievement
import com.example.steamachievement.util.getRandomString
import org.jsoup.Jsoup

fun loadTAlinks(
    achievements: List<Achievement>, link: String
): String {
    var error = 0
    var total = 0
    val randomizedLink = link + "?unused=" + getRandomString(20)

    val webpage = Jsoup
        .connect(randomizedLink)
        .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.900 Safari/537.36 MRCHROME SOC")
        .get()
    val listOfUL = webpage.select("main").select("ul.ach-panels")
    for (ul in listOfUL) {
        val lis = ul.select("li")
        for (li in lis) {
            total++
            val achievementName = li.select("a.title").text()
            val achievementLink = li.select("a.title").attr("href")
            val achievement =
                achievements.find { achievement ->
                    achievement.displayName.trim().equals(achievementName.trim(), true)
                }
            if (achievement == null) error++ else achievement.guideLink =
                "https://www.trueachievements.com$achievementLink"
        }
    }

    return "$error / $total"
}