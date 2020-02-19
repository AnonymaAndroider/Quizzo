package com.example.quizzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    private fun startActivity(activity: Class<*>, radioButtonId: Int) {
        val intent = Intent(this, activity)
        intent.putExtra("checkedRadioButtonID", radioButtonId)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)


        val radioThemeGroup = findViewById<RadioGroup>(R.id.ThemeRadioGroup)
        val radioDifficultyGroup = findViewById<RadioGroup>(R.id.DifficultyRadioGroup)

        radioThemeGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio : RadioButton = findViewById(checkedId)

            when(radio){
                sportButton -> {
                    Toast.makeText(this, "Sport", Toast.LENGTH_SHORT).show()

                }
                geographyButton -> {
                    Toast.makeText(this, "Geography", Toast.LENGTH_SHORT).show()
                }
                foodButton -> {
                    Toast.makeText(this, "Food", Toast.LENGTH_SHORT).show()
                }
                tvShowsButton -> {
                    Toast.makeText(this, "Tv-shows", Toast.LENGTH_SHORT).show()
                }
                gamesButton -> {
                    Toast.makeText(this, "Games", Toast.LENGTH_SHORT).show()
                }
            }
        }
        radioDifficultyGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio : RadioButton = findViewById(checkedId)
            when(radio){
                easyButton -> {
                    Toast.makeText(this, "Easy", Toast.LENGTH_SHORT).show()
                }
                mediumButton -> {
                    Toast.makeText(this, "Medium", Toast.LENGTH_SHORT).show()
                }
                hardButton -> {
                    Toast.makeText(this, "Hard", Toast.LENGTH_SHORT).show()
                }
            }
        }


        goButton.setOnClickListener {

            /*when {
                geographyButton.isChecked -> startActivity(AnswerActivity::class.java, 1)
                sportButton.isChecked -> startActivity(AnswerActivity::class.java, 2)
                foodButton.isChecked -> startActivity(AnswerActivity::class.java, 3)
                tvShowsButton.isChecked -> startActivity(AnswerActivity::class.java, 4)
                gamesButton.isChecked -> startActivity(AnswerActivity::class.java, 5)
            }*/

            if(sportButton.isChecked && mediumButton.isChecked){

                val intent = Intent(this, AnswerActivity::class.java)
                intent.putExtra("sport", sport)
                intent.putExtra("medium", "medium")
                startActivity(intent)
            }

            val intent = Intent(this, AnswerActivity::class.java)
            startActivity(intent)
        }

    }

    companion object {
        const val sport = 21
    }
}
