package com.example.quizzo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.util.DateInterval
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_answer.*
import okhttp3.*
import java.io.IOException
import java.util.Collections.shuffle
import java.util.concurrent.TimeoutException
import kotlin.concurrent.timer


data class TriviaRequest (

    val results: List<TriviaResult>
)

class TriviaResult(

    val category: String,
    val type: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>

)

private val client = OkHttpClient()


class AnswerActivity : AppCompatActivity() {

    private var score = 0
    private var progressCount = 0
    private lateinit var counter: CountDownTimer
    private val millisInFuture = 11000
    private val countDownInterval = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        fetch{

            Log.d("fetchDOne", "fetch is done with task")
            barProgressTimer()
        }
    }

    private fun barProgressTimer(){

        progressCount = 0

        counter = object : CountDownTimer(millisInFuture.toLong(), countDownInterval.toLong()) {

            override fun onFinish() {
                counter.cancel()
                fetch {
                   barProgressTimer()
                }
            }

            override fun onTick(millisUntilFinished: Long) {

                if(progressCount == 101){
                    counter.onFinish()
                } else {
                    progressBar.progress = progressCount
                    progressCount++
                }
            }
        }.start()

    }


    private fun fetch(callback: ()->Unit){

        val theme = intent.getIntExtra("sport", 21)
        val difficulty = intent.getStringExtra("medium")

        val url = "https://opentdb.com/api.php?amount=10&category=21&difficulty=medium&type=multiple"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val triviaRequest = gson.fromJson(body, TriviaRequest::class.java)
                val result = triviaRequest.results[0]
                val question = result.question.toSpanned()
                val questionCategory = result.category.toSpanned()
                val correctAnswer = result.correct_answer.toSpanned()
                val incorrectAnswer1 = result.incorrect_answers[0].toSpanned()
                val incorrectAnswer2 = result.incorrect_answers[1].toSpanned()
                val incorrectAnswer3 = result.incorrect_answers[2].toSpanned()
                val questions = listOf(
                    correctAnswer,
                    incorrectAnswer1,
                    incorrectAnswer2,
                    incorrectAnswer3
                )

                shuffle(questions)

                runOnUiThread {

                    val button1 = findViewById<Button>(R.id.alternative1)
                    val button2 = findViewById<Button>(R.id.alternative2)
                    val button3 = findViewById<Button>(R.id.alternative3)
                    val button4 = findViewById<Button>(R.id.alternative4)

                    questionText.text = question
                    themeText.text = questionCategory

                    button1.text = questions[0]
                    button1.isEnabled = true

                    button2.text = questions[1]
                    button2.isEnabled = true

                    button3.text = questions[2]
                    button3.isEnabled = true

                    button4.text = questions[3]
                    button4.isEnabled = true

                    button1.setOnClickListener {

                        button1.isClickable = false
                        if (button1.text == correctAnswer && counter != null) {
                            score += 1
                            counter.cancel()
                            fetch {
                                barProgressTimer()
                            }
                        } else {
                            button1.text = "---"
                        }
                    }

                    button2.setOnClickListener {

                        button2.isClickable = false
                        if (button2.text == correctAnswer && counter != null) {
                            score += 1
                            counter.cancel()

                            fetch {
                                barProgressTimer()
                            }
                        } else {
                            button2.text = "---"
                        }
                    }

                    button3.setOnClickListener {

                        button3.isClickable = false
                        if (button3.text == correctAnswer && counter != null) {
                            score += 1
                            counter.cancel()

                            fetch {
                                barProgressTimer()
                            }
                        } else {
                            button3.text = "---"
                        }
                    }

                    button4.setOnClickListener {

                        button4.isClickable = false
                        if (button4.text == correctAnswer && counter != null) {
                            score += 1
                            counter.cancel()
                            fetch {
                                barProgressTimer()
                            }
                        } else {
                            button4.text = "---"
                        }
                    }
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

        })
        callback()
    }
}
