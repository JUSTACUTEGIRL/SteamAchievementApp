package com.example.steamachievement

import android.provider.BaseColumns

object Database {
    object UserData {
        const val TABLE_NAME = "userdata"
        const val COLUMN_NAME_STEAMID = "steamid"
        const val COLUMN_NAME_STEAMAPIKEY = "steamapikey"
    }

    object Games : BaseColumns {
        const val TABLE_NAME = "games"
        const val COLUMN_NAME_APPID = "appid"
        const val COLUMN_NAME_GAMENAME = "gamename"
        const val COLUMN_NAME_ISCOMPLETED = "iscompleted"
        const val COLUMN_NAME_HASACHIEVEMENT = "hasachievement"
        const val COLUMN_NAME_GUIDELINK = "guidelink"
        const val COLUMN_NAME_ACHIEVEMENTSLINK = "achievementslink"
        const val COLUMN_NAME_PERCENTAGECOMPLETION = "percentcompletion"
        const val COLUMN_NAME_LOADED = "loaded"
    }

    object Achievements: BaseColumns {
        const val TABLE_NAME = "achievements"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_DISPLAYNAME = "displayname"
        const val COLUMN_NAME_APINAME = "apiname"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_ACHIEVED = "achieved"
        const val COLUMN_NAME_GUIDELINK = "guidelink"
        const val COLUMN_NAME_NOTE = "note"
        const val COLUMN_NAME_ICON = "icon"
        const val COLUMN_NAME_ICONGRAY = "icongray"
        const val COLUMN_NAME_GAME = "gameappid"
    }

    object Preferences {
        const val TABLE_NAME = "preferences"
        const val COLUMN_NAME_FIRST_TIME_START = "first_time_start"
        const val COLUMN_NAME_FILTER = "filter"
        const val COLUMN_NAME_SORT = "sort"
        const val COLUMN_NAME_DISPLAY = "display"
    }
}