package com.example.playlistapp_mingyuan

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_resultlist.*
import kotlinx.android.synthetic.main.item_resultlist.view.*

@SuppressLint("ValidFragment")
class Fragment_ResultList(context: Context, query: String): Fragment() {
    private var adapter = ResultAdapter()
    private var parentContext: Context = context
    private lateinit var viewModel: MusicViewModel
    private var listInitialized = false
    private var queryString: String = query
    private var musicList: ArrayList<TopTrack> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_resultlist, container, false)
    }

    override fun onStart() {
        super.onStart()
        result_list.layoutManager = LinearLayoutManager(parentContext)
        result_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(MusicViewModel::class.java)

        val observer = Observer<ArrayList<TopTrack>> {
            result_list.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return musicList[p0].getName() == musicList[p1].getName()
                }

                override fun getOldListSize(): Int {
                    return musicList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return (musicList[p0] == musicList[p1])
                }
            })
            result.dispatchUpdatesTo(adapter)
            musicList = it ?: ArrayList()
        }
        viewModel.getTopTrackByQueryText(queryString).observe(this, observer)
        this.listInitialized = true
    }

    inner class ResultAdapter: RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ResultViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.item_resultlist, p0, false)
            return ResultViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ResultViewHolder, p1: Int) {
            val music = musicList[p1]
            val musicImages = music.getImage()
            if (musicImages.size == 0) {
            }
            else {
                Picasso.with(this@Fragment_ResultList.context).load(musicImages[0]).into(p0.musicImg)
            }
            p0.musicTitle.text = music.getName()

            p0.musicArtist.text = music.getArtist()
            // click the item in toptrack list will navigate to music details activity
            p0.row.setOnClickListener {
                val intent = Intent(this@Fragment_ResultList.parentContext, MusicDetailsActivity::class.java)
                intent.putExtra("music", music)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return musicList.size
        }
        // view holder : information
        inner class ResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val row = itemView
            var musicImg: ImageView = itemView.music_img
            var musicTitle: TextView = itemView.music_title
            var musicArtist: TextView = itemView.music_artist
        }
    }
}