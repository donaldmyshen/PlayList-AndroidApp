package com.example.playlistapp_mingyuan

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.provider.BaseColumns
import android.util.Log


class MusicViewModel(application: Application): AndroidViewModel(application) {
    private var musicDBHelper: MusicDatabaseHelper = MusicDatabaseHelper(application)
    private var musicList: MutableLiveData<ArrayList<TopTrack>> = MutableLiveData()
    // API key of Last.fm
    private var myKey = "d06adb345b051c07acdeeef6977d4b8f"

    fun getTopTrack(): MutableLiveData<ArrayList<TopTrack>> {
        var topTrack = "?method=chart.gettoptracks&api_key=" + myKey + "&format=json"
        loadTopTrack(topTrack)
        return musicList
    }

    fun getTopTrackByQueryText(query: String): MutableLiveData<ArrayList<TopTrack>> {
        var artistTopTrack = "?method=artist.gettoptracks&artist=$query&api_key=" + myKey + "&format=json"
        loadArtistTopTrack(artistTopTrack)
        return  musicList
    }

    private fun loadTopTrack(query: String) {
        MusicAsyncTask().execute(query)
    }

    private fun loadArtistTopTrack(query: String) {
        ArtistMusicAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class MusicAsyncTask: AsyncTask<String, Unit, ArrayList<TopTrack>>() {
        override fun doInBackground(vararg params: String?): ArrayList<TopTrack>? {
            return QueryUtils.fetchTopTrackData(params[0]!!)
        }
        // throw exception if search not found
        override fun onPostExecute(result: ArrayList<TopTrack>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                musicList.value = result
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class ArtistMusicAsyncTask: AsyncTask<String, Unit, ArrayList<TopTrack>>() {
        override fun doInBackground(vararg params: String?): ArrayList<TopTrack>? {
            return QueryUtils.fetchArtistTopTrackData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<TopTrack>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                musicList.value = result
            }
        }
    }

    fun getPlaylist(): MutableLiveData<ArrayList<TopTrack>> {
        this.loadPlaylist()
        return this.musicList
    }

    private fun loadPlaylist(): ArrayList<TopTrack> {
        val playlist: ArrayList<TopTrack> = ArrayList()
        val database = this.musicDBHelper.readableDatabase
        //val projection = arrayOf(BaseColumns._ID, DbSettings.DBPlaylistEntry.NAME,
         //   DbSettings.DBPlaylistEntry.ARTIST, DbSettings.DBPlaylistEntry.URL)
        // use cursor
        val cursor = database.query(
            DbSettings.DBPlaylistEntry.TABLE,
           // projection,
            null,
            null, null, null, null, null
        )
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(DbSettings.DBPlaylistEntry.NAME))
                val artist = getString(getColumnIndexOrThrow(DbSettings.DBPlaylistEntry.ARTIST))
                val url = getString(getColumnIndexOrThrow(DbSettings.DBPlaylistEntry.URL))
                val music = TopTrack(name, ArrayList(), artist, "", url, "")
                playlist.add(music)
            }
        }
        musicList.value = playlist
        cursor.close()
        database.close()
        return playlist
    }
    // add certain music to playlist
    fun addPlayList(music: TopTrack) {
        val database: SQLiteDatabase = this.musicDBHelper.writableDatabase
        val listValues = ContentValues()
        listValues.put(DbSettings.DBPlaylistEntry.NAME, music.getName())
        listValues.put(DbSettings.DBPlaylistEntry.ARTIST, music.getArtist())
        listValues.put(DbSettings.DBPlaylistEntry.URL, music.getURL())
        database.insert(DbSettings.DBPlaylistEntry.TABLE, null, listValues)
        database.close()
    }
    // delete certain music from playlist
    fun deletePlayList (name: String, isFromResultList: Boolean = false) {
        val db : SQLiteDatabase = this.musicDBHelper.writableDatabase
        db.delete(DbSettings.DBPlaylistEntry.TABLE, "${DbSettings.DBPlaylistEntry.NAME}=?", arrayOf(name))
        db.close()
        val playList: ArrayList<TopTrack>? = musicList.value
        if (playList != null) {
            var i = 0
            playList.forEachIndexed {index, music ->
                if (music.getName() == name) {
                    i = index
                }
            }
            if (isFromResultList) playList[i].isInPlaylist = false
            else playList.remove(playList[i])
        this.musicList.value = playList
        }
    }
}