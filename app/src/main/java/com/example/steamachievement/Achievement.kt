package com.example.steamachievement

import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    var name: String = "Default",
    var displayName: String = "",
    val apiname : String = "Default",
    var description: String = "Hidden (earn this achievement to reveal)",
    var achieved: Int = 0,
    var icon: String = "Default",
    var icongray: String = "Default",
    var guideLink: String = "",
    var note: String = ""
)