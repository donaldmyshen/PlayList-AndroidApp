package com.example.playlistapp_mingyuan

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class QueryUtils {
    companion object {
        private val  LogTag = this::class.java.simpleName
        private const val BaseURL = " https://ws.audioscrobbler.com/2.0/"
        // JSon
        fun fetchTopTrackData(jsonQueryString: String): ArrayList<TopTrack>? {
            val url: URL? = createUrl("${this.BaseURL }$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractDataFromJson(jsonResponse)
        }

        fun fetchArtistTopTrackData(jsonQueryString: String): ArrayList<TopTrack>? {
            val url: URL? = createUrl("${this.BaseURL }$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractDataFromJson2(jsonResponse)
        }

        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            } catch (e: MalformedURLException) {
                Log.e(this.LogTag, "Problem building the URL.", e)
            }
            return url
        }

        private fun makeHttpRequest(url: URL?): String {
            var jsonResponse = ""

            if (url == null) {
                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000 // 10 seconds
                urlConnection.connectTimeout = 15000 // 15 seconds
                urlConnection.requestMethod = "GET"
                urlConnection.connect()
                // not found response 400
                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                }
                else {
                    Log.e(this.LogTag, "Error response code: ${urlConnection.responseCode}")
                }
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem retrieving the product data results: $url", e)
            }
            finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }
            return jsonResponse
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }
            return output.toString()
        }

        private fun extractDataFromJson(musicJson: String?): ArrayList<TopTrack>? {
            if (TextUtils.isEmpty(musicJson)) {
                return null
            }
            val musicList = ArrayList<TopTrack>()
            try {
                val baseJasonResponse = JSONObject(musicJson)
                val tracks = baseJasonResponse.getJSONObject("tracks")
                val trackArray = tracks.getJSONArray("track")
                for (i in 0 until trackArray.length()) {
                    val musicObject = trackArray.getJSONObject(i)
                    // Images
                    val images = returnValueOrDefault<JSONArray>(musicObject, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            imageArrayList.add((images[j] as JSONObject).getString("#text"))
                        }
                    }
                    // Artist
                    var artistName = ""
                    val artist = returnValueOrDefault<JSONObject>(musicObject, "artist") as JSONObject?
                    if (artist != null) {
                        artistName = artist.getString("name")
                    }
                    musicList.add(
                        TopTrack(
                            // information
                            returnValueOrDefault<String>(musicObject, "name") as String,
                            imageArrayList,
                            artistName,
                            returnValueOrDefault<String>(musicObject, "duration") as String,
                            returnValueOrDefault<String>(musicObject, "url") as String,
                            returnValueOrDefault<String>(musicObject, "playcount") as String
                        ))
                }
            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }
            return musicList
        }
        private fun extractDataFromJson2(musicJson: String?): ArrayList<TopTrack>? {
            if (TextUtils.isEmpty(musicJson)) {
                return null
            }
            val musicList = ArrayList<TopTrack>()
            try {
                val baseJasonResponse = JSONObject(musicJson)
                try {
                     baseJasonResponse.getJSONObject("toptracks")
                } catch (e: Exception) {
                    /*
                    AlertDialog.Builder(this)
                        .setTitle("Error!")
                        .setMessage("Not exist")
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                dialog, _ -> dialog.cancel()
                        })
                        .show()
                        */
                }
                val tracks = baseJasonResponse.getJSONObject("toptracks")
                val trackArray = tracks.getJSONArray("track")
                for (i in 0 until trackArray.length()) {
                    val musicObject = trackArray.getJSONObject(i)
                    // Images
                    val images = returnValueOrDefault<JSONArray>(musicObject, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            imageArrayList.add((images[j] as JSONObject).getString("#text"))
                        }
                    }
                    // Artist
                    var artistName = ""
                    val artist = returnValueOrDefault<JSONObject>(musicObject, "artist") as JSONObject?
                    if (artist != null) {
                        artistName = artist.getString("name")
                    }
                    musicList.add(
                        TopTrack(
                            //information
                            returnValueOrDefault<String>(musicObject, "name") as String,
                            imageArrayList,
                            artistName,
                            returnValueOrDefault<String>(musicObject, "duration") as String,
                            returnValueOrDefault<String>(musicObject, "url") as String,
                            returnValueOrDefault<String>(musicObject, "playcount") as String
                        ))
                }
            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }
            return musicList
        }
        private inline fun <reified T> returnValueOrDefault(json: JSONObject, key: String): Any? {
            when (T::class) {
                String::class -> {
                    return if (json.has(key)) {
                        json.getString(key)
                    } else {
                        " "
                    }
                }
                Int::class -> {
                    return if (json.has(key)) {
                        json.getInt(key)
                    }
                    else {
                        return -1
                    }
                }
                Double::class -> {
                    return if (json.has(key)) {
                        json.getDouble(key)
                    }
                    else {
                        return -1.0
                    }
                }
                Long::class -> {
                    return if (json.has(key)) {
                        json.getLong(key)
                    }
                    else {
                        return (-1).toLong()
                    }
                }
                JSONObject::class -> {
                    return if (json.has(key)) {
                        json.getJSONObject(key)
                    }
                    else {
                        return null
                    }
                }
                JSONArray::class -> {
                    return if (json.has(key)) {
                        json.getJSONArray(key)
                    }
                    else {
                        return null
                    }
                }
                else -> {
                    return null
                }
            }
        }
    }
}