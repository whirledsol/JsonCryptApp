package me.whirledsol.jsoncrypt

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import me.whirledsol.jsoncrypt.util.PreferenceUtil

class MainActivity : JsonCryptActivity() {

    private lateinit var _pager: ViewPager2 // creating object of ViewPager
    private lateinit var _tabLayout: TabLayout  // creating object of TabLayout
    private val _pages by lazy {
        arrayOf(
            Pair("Decrypt",DecryptFragment()),
            Pair("Encrypt",EncryptFragment()),
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //confirm we are allowed to be here
        confirmAcknowledgment()

        setContentView(R.layout.activity_main)
        onCreateActionBar(true)

        // set the references of the declared objects above
        _pager = findViewById(R.id.pager)
        _tabLayout = findViewById(R.id.tabs)

        _pager.adapter =  ScreenSlidePagerAdapter(this)

        // bind the viewPager with the TabLayout.
        TabLayoutMediator(_tabLayout, _pager) { tab, position ->
            tab.text = _pages[position].first
        }.attach()

    }

    fun confirmAcknowledgment(){
        //confirmAcknowledgment
        val key = getString(R.string.pref_user_acknowledged)
        val acknowledged = PreferenceUtil(applicationContext).getPreferenceInt(key)
        if(acknowledged != 1) {
            val i = Intent(applicationContext, AboutActivity::class.java)
            startActivity(i)
            return
        }
    }

    override fun onBackPressed() {
        if (_pager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            _pager.currentItem = _pager.currentItem - 1
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = _pages.size

        override fun createFragment(position: Int): Fragment = _pages[position].second
    }

}