package com.example.playlistapp_mingyuan

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_musicdetails.*


class MusicDetailsActivity: AppCompatActivity() {
    private lateinit var music: TopTrack
    private lateinit var viewModel: MusicViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musicdetails)
        music = intent.extras!!.getSerializable("music") as TopTrack
        viewModel = ViewModelProviders.of(this).get(MusicViewModel::class.java)
        // call to load ui build in
        this.loadUI(music)
    }
    override fun onBackPressed() {
        this.finish()
    }
    // top track information ui
    private fun loadUI(music: TopTrack) {
        detail_music_title.text = "title: " + music.getName()
        detail_music_artist.text = "artist: " + music.getArtist()
        detail_music_duration.text = "duration: " + music.getDuration()
        detail_music_url.text = "url: " + music.getURL()
        detail_music_playcount.text = "playcount: " + music.getPlaycount()
        // add to playlist
        add_playlist.setOnClickListener {
            viewModel.addPlayList(this.music)
            this.music.isInPlaylist = true
        }
        // delete from playlist
        delete_playlist.setOnClickListener {
            viewModel.deletePlayList(this.music.getName())
            this.music.isInPlaylist = false
        }
        val images = music.getImage()
        if (images.size > 0) {
            Picasso.with(this).load(music.getImage()[3]).into(detail_music_img)
        }
    }
}
