package com.example.playlistapp_mingyuan

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MusicDatabaseHelper(context: Context): SQLiteOpenHelper(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        /*
         create database, what need to notice is it will only create once, if we need add a new column,
         we need to clear the storage.
          */
        val createToptrackTableQuery = "CREATE TABLE " + DbSettings.DBPlaylistEntry.TABLE +
                " ( " +
                DbSettings.DBPlaylistEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DBPlaylistEntry.NAME + " TEXT NULL, " +
                DbSettings.DBPlaylistEntry.ARTIST + " TEXT NULL, " +
                DbSettings.DBPlaylistEntry.URL + " TEXT NULL " +
                " ); "
        db?.execSQL(createToptrackTableQuery)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + DbSettings.DBPlaylistEntry.TABLE)
        onCreate(db)
    }
}