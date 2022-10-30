package me.whirledsol.jsoncrypt.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class PreferenceUtil(var _context: Context, var _preferenceStoreName : String = "jsoncrypt.main") {

    private fun getSharedPreferences() : SharedPreferences{
        //getSharedPreferences
        return _context.getSharedPreferences(_preferenceStoreName,AppCompatActivity.MODE_PRIVATE)
    }

    fun getPreferenceInt(key: String) : Int {
        //getPreferenceInt
        val sharedPref = getSharedPreferences()
        return sharedPref?.getInt(key, -1) ?: -1
    }


    fun setPreferenceInt(key: String, value: Int){
        //setPreferenceInt
        val sharedPref = getSharedPreferences()
        with (sharedPref.edit()) {
            this.putInt(key,value)
            apply()
        }
    }

}