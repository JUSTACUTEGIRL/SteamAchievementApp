package com.example.steamachievement

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val SQL_CREATE_USERDATA =
    "CREATE TABLE ${Database.UserData.TABLE_NAME} (" +
            "${Database.UserData.COLUMN_NAME_STEAMID} TEXT," +
            "${Database.UserData.COLUMN_NAME_STEAMAPIKEY} TEXT)"

private const val SQL_CREATE_GAME =
    "CREATE TABLE ${Database.Games.TABLE_NAME} (" +
            "${Database.Games.COLUMN_NAME_APPID} TEXT PRIMARY KEY," +
            "${Database.Games.COLUMN_NAME_GAMENAME} TEXT," +
            "${Database.Games.COLUMN_NAME_ISCOMPLETED} INTEGER," +
            "${Database.Games.COLUMN_NAME_HASACHIEVEMENT} INTEGER," +
            "${Database.Games.COLUMN_NAME_PERCENTAGECOMPLETION} INTEGER," +
            "${Database.Games.COLUMN_NAME_GUIDELINK} TEXT," +
            "${Database.Games.COLUMN_NAME_ACHIEVEMENTSLINK} TEXT," +
            "${Database.Games.COLUMN_NAME_LOADED} INTEGER)"

private const val SQL_CREATE_ACHIEVEMENT =
    "CREATE TABLE ${Database.Achievements.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${Database.Achievements.COLUMN_NAME_NAME} TEXT," +
            "${Database.Achievements.COLUMN_NAME_APINAME} TEXT," +
            "${Database.Achievements.COLUMN_NAME_DISPLAYNAME} TEXT," +
            "${Database.Achievements.COLUMN_NAME_DESCRIPTION} TEXT," +
            "${Database.Achievements.COLUMN_NAME_ACHIEVED} INTEGER," +
            "${Database.Achievements.COLUMN_NAME_GUIDELINK} TEXT," +
            "${Database.Achievements.COLUMN_NAME_NOTE} TEXT," +
            "${Database.Achievements.COLUMN_NAME_ICON} TEXT," +
            "${Database.Achievements.COLUMN_NAME_ICONGRAY} TEXT," +
            "${Database.Achievements.COLUMN_NAME_GAME} INTEGER," +
            "FOREIGN KEY (${Database.Achievements.COLUMN_NAME_GAME}) REFERENCES ${Database.Games.TABLE_NAME}(${Database.Games.COLUMN_NAME_APPID}));"

private const val SQL_CREATE_PREFERENCES =
    "CREATE TABLE ${Database.Preferences.TABLE_NAME} (" +
            "${Database.Preferences.COLUMN_NAME_FIRST_TIME_START} INTEGER," +
            "${Database.Preferences.COLUMN_NAME_FILTER} TEXT," +
            "${Database.Preferences.COLUMN_NAME_SORT} TEXT," +
            "${Database.Preferences.COLUMN_NAME_DISPLAY} INTEGER);"

private const val SQL_ADD_PREFERENCES =
    "INSERT INTO ${Database.Preferences.TABLE_NAME} " +
            "VALUES (1, '0000', '10', 0);"

private const val SQL_DELETE_USERDATA =
    "DROP TABLE IF EXISTS ${Database.UserData.TABLE_NAME}"

private const val SQL_DELETE_GAMES =
    "DROP TABLE IF EXISTS ${Database.Games.TABLE_NAME}"

private const val SQL_DELETE_ACHIEVEMENTS =
    "DROP TABLE IF EXISTS ${Database.Achievements.TABLE_NAME}"

private const val SQL_DELETE_PREFERENCES =
    "DROP TABLE IF EXISTS ${Database.Preferences.TABLE_NAME}"

class AppDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_USERDATA)
        db.execSQL(SQL_CREATE_GAME)
        db.execSQL(SQL_CREATE_ACHIEVEMENT)
        db.execSQL(SQL_CREATE_PREFERENCES)
        db.execSQL(SQL_ADD_PREFERENCES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_USERDATA)
        db.execSQL(SQL_DELETE_GAMES)
        db.execSQL(SQL_DELETE_ACHIEVEMENTS)
        db.execSQL(SQL_DELETE_PREFERENCES)
        onCreate(db)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "GameAndAchievement.db"
    }
}