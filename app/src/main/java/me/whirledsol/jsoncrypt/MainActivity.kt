package me.whirledsol.jsoncrypt

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_main)


        // set the references of the declared objects above
        _pager = findViewById(R.id.pager)
        _tabLayout = findViewById(R.id.tabs)


        _pager.adapter =  ScreenSlidePagerAdapter(this)

        setSupportActionBar( findViewById(R.id.toolbar))


        // bind the viewPager with the TabLayout.
        TabLayoutMediator(_tabLayout, _pager) { tab, position ->
            tab.text = _pages[position].first
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_close -> {onClose(); return true}
            R.id.action_erase -> {onErase(); return true}
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onClose() {
        finishAffinity();
    }

    fun onErase(){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Are you sure you want to erase all encrypted files?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                CryptUtil(applicationContext).eraseAll()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
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