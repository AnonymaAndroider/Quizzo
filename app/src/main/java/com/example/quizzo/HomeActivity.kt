package com.example.quizzo

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val layout = homeLayout.findViewById<ConstraintLayout>(
            R.id.homeLayout
        )
        val frameAnimation: AnimationDrawable = layout.background as AnimationDrawable
        frameAnimation.setEnterFadeDuration(4500)
        frameAnimation.setExitFadeDuration(4500)
        frameAnimation.start()

        val isLoggedIn = intent.getBooleanExtra("LoggedIn", false)
        if(!isLoggedIn){

            highScoreButton.isEnabled = false
            logoutButton.isEnabled = false
        }

        playButton.setOnClickListener {

            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
        }

        highScoreButton.setOnClickListener{

            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
        }

        helpButton.setOnClickListener {

            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
