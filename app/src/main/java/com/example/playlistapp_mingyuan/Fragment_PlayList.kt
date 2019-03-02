package com.example.playlistapp_mingyuan

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.item_playlist.*
import kotlinx.android.synthetic.main.item_playlist.view.*

@SuppressLint("ValidFragment")
class Fragment_PlayList : Fragment() {
    private var adapter = MusicAdapter()
    private lateinit var viewModel: MusicViewModel
    private var MusicList: ArrayList<TopTrack> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onStart() {
        super.onStart()

        result.layoutManager = LinearLayoutManager(this.context)
        result.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(MusicViewModel::class.java)

        val observer = Observer<ArrayList<TopTrack>> {
            result.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    if(p0 >= MusicList.size || p1 >= MusicList.size) {
                        return false
                    }
                    return MusicList[p0].getName() == MusicList[p1].getName()
                }

                override fun getOldListSize(): Int {
                    return MusicList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return MusicList[p0] == MusicList[p1]
                }
            })
            result.dispatchUpdatesTo(adapter)
            MusicList = it ?: ArrayList()
        }
        // make it visable
        viewModel.getPlaylist().observe(this, observer)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
        }
    }
    inner class MusicAdapter: RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MusicViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.item_playlist, p0, false)
            return MusicViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: MusicViewHolder, p1: Int) {
            val music = MusicList[p1]
            p0.name.text = music.getName()
            p0.artist.text = music.getArtist()
            // delete the music from the playlist
            p0.delete.setOnClickListener {
                val music = MusicList[p1]
                viewModel.deletePlayList(music.getName())
                music.isInPlaylist = false
            }
            // play the music from the playlist
            p0.play.setOnClickListener {
                val music = MusicList[p1]
                val url = music.getURL()
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
            }
        }

        override fun getItemCount(): Int {
            return MusicList.size
        }
        // view we get in each item in playlist
        inner class MusicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var name: TextView = itemView.name
            var artist: TextView = itemView.artist
            var delete: Button = itemView.delete_music
            var play: Button = itemView.play_music
        }
    }
}
