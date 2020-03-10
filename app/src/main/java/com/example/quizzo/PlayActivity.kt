package com.example.quizzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    private var isLoggedIn = false

    companion object {
        const val sport = 21
        const val generalKnowledge = 9
        const val geography = 22
        const val videoGames = 15
        const val film = 11
    }

    private fun sendThemeAndDifficulty(theme: Int, difficulty: String) {
        val intent = Intent(this, AnswerActivity::class.java)
        intent.putExtra("theme", theme)
        intent.putExtra("difficulty", difficulty)
        Log.d("LoggedInPlay", isLoggedIn.toString())
        intent.putExtra("LoggedIn", isLoggedIn)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)


        val radioThemeGroup = findViewById<RadioGroup>(R.id.themeRadioGroup)
        val radioDifficultyGroup = findViewById<RadioGroup>(R.id.difficultyRadioGroup)
        isLoggedIn = intent.getBooleanExtra("LoggedIn", false)
        if(!isLoggedIn){
            videoGamesButton.isEnabled = false
            generalKnowledgeButton.isEnabled = false
            filmButton.isEnabled = false
            mediumButton.isEnabled = false
            hardButton.isEnabled = false
        }

        radioThemeGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio : RadioButton = findViewById(checkedId)

            when(radio){
                sportButton -> {
                    Toast.makeText(this, "Sport", Toast.LENGTH_SHORT).show()

                }
                geographyButton -> {
                    Toast.makeText(this, "Geography", Toast.LENGTH_SHORT).show()
                }
                videoGamesButton -> {
                    Toast.makeText(this, "Food", Toast.LENGTH_SHORT).show()
                }
                filmButton -> {
                    Toast.makeText(this, "Tv-shows", Toast.LENGTH_SHORT).show()
                }
                generalKnowledgeButton -> {
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

            when {
                geographyButton.isChecked && easyButton.isChecked -> sendThemeAndDifficulty(geography, "easy")
                geographyButton.isChecked && mediumButton.isChecked -> sendThemeAndDifficulty(geography, "medium")
                geographyButton.isChecked && hardButton.isChecked -> sendThemeAndDifficulty(geography, "hard")

                sportButton.isChecked && easyButton.isChecked -> sendThemeAndDifficulty(sport, "easy")
                sportButton.isChecked && mediumButton.isChecked -> sendThemeAndDifficulty(sport, "medium")
                sportButton.isChecked && hardButton.isChecked -> sendThemeAndDifficulty(sport, "hard")

                filmButton.isChecked  && easyButton.isChecked -> sendThemeAndDifficulty(film, "easy")
                filmButton.isChecked  && mediumButton.isChecked -> sendThemeAndDifficulty(film, "medium")
                filmButton.isChecked  && hardButton.isChecked -> sendThemeAndDifficulty(film, "hard")

                generalKnowledgeButton.isChecked && easyButton.isChecked -> sendThemeAndDifficulty(generalKnowledge, "easy")
                generalKnowledgeButton.isChecked && mediumButton.isChecked -> sendThemeAndDifficulty(generalKnowledge, "medium")
                generalKnowledgeButton.isChecked && hardButton.isChecked -> sendThemeAndDifficulty(generalKnowledge, "hard")

                videoGamesButton.isChecked && easyButton.isChecked -> sendThemeAndDifficulty(videoGames, "easy")
                videoGamesButton.isChecked && mediumButton.isChecked -> sendThemeAndDifficulty(videoGames, "medium")
                videoGamesButton.isChecked && hardButton.isChecked -> sendThemeAndDifficulty(videoGames, "hard")
            }
        }
    }
}
