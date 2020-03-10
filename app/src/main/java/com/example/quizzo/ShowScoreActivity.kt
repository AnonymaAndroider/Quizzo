package com.example.quizzo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_show_score.*

class ShowScoreActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_score)

        val layout = showScoreLayout.findViewById<ConstraintLayout>(
            R.id.showScoreLayout
        )
        val frameAnimation: AnimationDrawable = layout.background as AnimationDrawable
        frameAnimation.setEnterFadeDuration(4500)
        frameAnimation.setExitFadeDuration(4500)
        frameAnimation.start()

        val score = intent.getIntExtra("score", 0)
        val theme = intent.getIntExtra("theme", 0)
        val difficulty = intent.getStringExtra("difficulty")
        val token = intent.getStringExtra("token")
        scoreText.text = "Your score: $score"


        retryButton.setOnClickListener {
            var onResumeCalled = true
            val intent = Intent(this, AnswerActivity::class.java)
            intent.putExtra("theme", theme)
            intent.putExtra("difficulty", difficulty)
            intent.putExtra("token", token)
            intent.putExtra("onResumeCalled", onResumeCalled)
            startActivity(intent)
            this.finish()
        }
        highscoreButton.setOnClickListener {
            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}
