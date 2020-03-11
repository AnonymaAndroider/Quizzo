package com.example.quizzo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        var score = intent.getIntExtra("score", 0)
        val theme = intent.getIntExtra("theme", 0)
        val difficulty = intent.getStringExtra("difficulty")
        val token = intent.getStringExtra("token")
        if(score < 0){
            score = 0
        }
        scoreText.text = "Your score: $score"
        val isLoggedIn = intent.getBooleanExtra("LoggedIn", false)
        if(!isLoggedIn){
            highscoreButton.isEnabled = false
        }
        val currentUsersId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("userUid", currentUsersId.toString())
        updateScore(score, currentUsersId.toString(), isLoggedIn)

        retryButton.setOnClickListener {
            val onResumeCalled = true
            val intent = Intent(this, AnswerActivity::class.java)
            intent.putExtra("theme", theme)
            intent.putExtra("difficulty", difficulty)
            intent.putExtra("token", token)
            intent.putExtra("onResumeCalled", onResumeCalled)
            intent.putExtra("LoggedIn", isLoggedIn)
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
            intent.putExtra("LoggedIn", isLoggedIn)
            startActivity(intent)
            this.finish()
        }
    }

    private fun updateScore(score: Int, userId: String, isLoggedIn: Boolean){

        if(isLoggedIn) {
            var ref = FirebaseDatabase.getInstance().reference

            ref.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("onCancelledError", "onCancelled", error.toException())
                    }

                    override fun onDataChange(dataSnapShot: DataSnapshot) {

                        var highscore = dataSnapShot.child("highscore").getValue(Int::class.java)
                        Log.d("highscore", highscore.toString())
                        if (highscore != null) {
                            if (highscore < score) {
                                highscore = score
                                dataSnapShot.child("highscore").ref.setValue(highscore)
                            }
                        }
                    }
            })
        }else{
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.Score))
                .setMessage(getString(R.string.need_to_signed_up_save_score))
                .setPositiveButton(getString(R.string.I_want_to_sign_up)){ dialog, whichButton ->

                    val intent = Intent(this, SignUpActivity::class.java)
                    startActivity(intent)
                } .setNegativeButton(getString(R.string.another_time))
                 { dialog, whichButton ->
                    dialog.dismiss()
                 } .show()
        }
    }
}
