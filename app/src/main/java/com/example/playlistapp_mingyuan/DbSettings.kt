package com.example.playlistapp_mingyuan

import android.provider.BaseColumns

class DbSettings {
    companion object {
        const val DB_NAME = "music.db"
        const val DB_VERSION = 1
    }
    class DBPlaylistEntry: BaseColumns {
        companion object {
            const val TABLE = "toptrack"
            const val ID = BaseColumns._ID
            const val NAME = "name"
            const val ARTIST = "artist"
            const val URL = "url"
        }
    }
}