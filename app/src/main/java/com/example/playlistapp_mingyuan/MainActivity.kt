package com.example.playlistapp_mingyuan
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentAdapter = MyPagerAdapter(this, supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter
        tabs_main.setupWithViewPager(viewpager_main)
    }

    class MyPagerAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val parentContext = context
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    //navigate to home page
                    Fragment_Home(parentContext)
                }
                //navigate to playlist
                else -> Fragment_PlayList()
            }
        }

        override fun getCount(): Int {
            return 2
        }
        // text show on the bar
        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "Home"
                else -> {
                    return "Playlist"
                }
            }
        }
    }
}
