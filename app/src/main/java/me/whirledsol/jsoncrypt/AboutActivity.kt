package me.whirledsol.jsoncrypt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import me.whirledsol.jsoncrypt.util.PreferenceUtil


class AboutActivity : JsonCryptActivity() {


    private lateinit var text_disclaimers: TextView
    private lateinit var button_acknowledge: Button
    private lateinit var button_feedback: Button
    private lateinit var button_privacy: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        text_disclaimers = findViewById(R.id.text_disclaimers)
        button_acknowledge = findViewById(R.id.button_acknowledge)
        button_feedback = findViewById(R.id.button_feedback)
        button_privacy = findViewById(R.id.button_privacy)
        onSetup()
    }

   private fun onSetup(){
        //setup elements
        text_disclaimers.setOnClickListener{view->
            var url = getResources().getString(R.string.url_github)
            openUrl(url)
        }
        button_feedback.setOnClickListener{view->
           var url = getResources().getString(R.string.url_github)
           openUrl(url)
        }
       button_privacy.setOnClickListener{view->
           var url = getResources().getString(R.string.url_privacy)
           openUrl(url)
       }

       button_acknowledge.setOnClickListener{view->
           var key = getString(R.string.pref_user_acknowledged);
           PreferenceUtil(applicationContext).setPreferenceInt(key,1)
           val i = Intent(applicationContext, MainActivity::class.java)
           i.putExtra(getString(R.string.putExtra_fragment), getString(R.string.fragment_encrypt))
           startActivity(i)
       }
    }

}