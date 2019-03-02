package com.example.playlistapp_mingyuan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_home.*

@SuppressLint("ValidFragment")
class  Fragment_Home (context: Context): Fragment() {
    private var parentContext = context
    private var initialized: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (!this.initialized) {
            val fm = fragmentManager
            val ft = fm?.beginTransaction()
            //fragment: load top track
            ft?.add(R.id.list_holder, Fragment_TopTrack(this.parentContext), "NEW_FRAG")
            ft?.commit()
            //search artist by name
            search_text.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchText = search_text.text
                    search_text.setText("")
                    if (searchText.toString() == "") {
                        val toast = Toast.makeText(this.parentContext, "Please enter text", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        return@setOnEditorActionListener true
                    }
                    else {
                        performSearch(searchText.toString())
                        return@setOnEditorActionListener false
                    }
                }
                return@setOnEditorActionListener false
            }
            this.initialized = true
        }
    }

    private fun performSearch(query: String) {
        // Load Fragment into View
        val fm = fragmentManager
        //fragment: load search result
        val fragment = Fragment_ResultList(this.parentContext, query)
        val ft = fm?.beginTransaction()
        ft?.replace(R.id.list_holder, fragment, "RESULTS_FRAG")
        ft?.commit()
    }
}