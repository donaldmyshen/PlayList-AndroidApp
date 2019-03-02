package com.example.playlistapp_mingyuan

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_toptracklist.*
import kotlinx.android.synthetic.main.item_toptracklist.view.*

@SuppressLint("ValidFragment")
class Fragment_TopTrack(context: Context) : Fragment() {
    private var adapter = MusicAdapter()
    private var parentContext: Context = context
    private lateinit var viewModel: MusicViewModel

    private var MusicList: ArrayList<TopTrack> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_toptracklist, container, false)
    }

    override fun onStart() {
        super.onStart()

        toptrack_list.layoutManager = LinearLayoutManager(this.context)
        toptrack_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(MusicViewModel::class.java)

        val observer = Observer<ArrayList<TopTrack>> {
            toptrack_list.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
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
        viewModel.getTopTrack().observe(this, observer)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
        }
    }

    inner class MusicAdapter: RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MusicViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.item_toptracklist, p0, false)
            return MusicViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: MusicViewHolder, p1: Int) {
            val music = MusicList[p1]
            val musicImages = music.getImage()
            if (musicImages.size == 0) {

            } else {
                Picasso.with(this@Fragment_TopTrack.parentContext).load(musicImages[3]).into(p0.musicImg)            }
            p0.musicTitle.text = music.getName()

            p0.row.setOnClickListener {
                val intent = Intent(this@Fragment_TopTrack.parentContext, MusicDetailsActivity::class.java)
                intent.putExtra("music", music)
                (activity as MainActivity).startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return MusicList.size
        }
        // information show on the top track page
        inner class MusicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var row = itemView
            var musicImg: ImageView = itemView.music_img
            var musicTitle: TextView = itemView.music_title
        }
    }
}
