package com.example.quizzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_answer.*
import okhttp3.*
import java.util.*
import java.io.IOException



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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        fetch()
    }

    private fun fetch(){

        val theme = intent.getIntExtra("sport", 0)
        val difficulty = intent.getStringExtra("medium")

        Log.d("fetch", "fetching")
        val url = "https://opentdb.com/api.php?amount=10&category=$theme&difficulty=$difficulty&type=multiple"
        Log.d("url", "url='" + url + "'")
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("request", request.toString())
        Log.d("client", client.toString())
        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                Log.d("body", body)
                val gson = GsonBuilder().create()
                val triviaRequest = gson.fromJson(body, TriviaRequest::class.java)
                val result = triviaRequest.results[0]
                val question = result.question.toSpanned()
                Log.d("question", question.toString())
                val questionCategory = result.category.toSpanned()
                Log.d("category", questionCategory.toString())
                val correctAnswer = result.correct_answer.toSpanned()
                Log.d("correct", correctAnswer.toString())
                val incorrectAnswer1 = result.incorrect_answers[0].toSpanned()
                val incorrectAnswer2 = result.incorrect_answers[1].toSpanned()
                val incorrectAnswer3 = result.incorrect_answers[2].toSpanned()

                val questions = listOf(
                    correctAnswer,
                    incorrectAnswer1,
                    incorrectAnswer2,
                    incorrectAnswer3
                )

                Collections.shuffle(questions)

                runOnUiThread {
                    questionText.text = question
                    themeText.text = questionCategory

                    alternative1.text = questions[0]
                    alternative1.isEnabled = true

                    alternative2.text = questions[1]
                    alternative2.isEnabled = true

                    alternative3.text = questions[2]
                    alternative3.isEnabled = true

                    alternative4.text = questions[3]
                    alternative4.isEnabled = true

                    alternative1.setOnClickListener {

                        alternative1.isClickable = false
                        if (alternative1.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative1.text = "---"
                        }
                    }

                    alternative2.setOnClickListener {

                        alternative2.isClickable = false
                        if (alternative2.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative2.text = "---"
                        }
                    }

                    alternative3.setOnClickListener {

                        alternative3.isClickable = false
                        if (alternative3.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative3.text = "---"
                        }
                    }

                    alternative4.setOnClickListener {

                        alternative4.isClickable = false
                        if (alternative4.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative4.text = "---"
                        }
                    }
                }
                response.close()
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

        })

           /* override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(!response.isSuccessful) throw IOException("Unexpected code $response")
                }
                Log.d("onResponse", "Inside OnResponse" )
                val body = response.body?.string()
                //Log.d("body", body)
                val gson = GsonBuilder().create()
                val triviaRequest = gson.fromJson(body, TriviaRequest::class.java)

                val result = triviaRequest.results[0]
                val question = result.question.toSpanned()
                val questionCategory = result.category.toSpanned()
                val correctAnswer = result.correct_answer.toSpanned()
                val incorrectAnswer1 = result.incorrect_answer[0].toSpanned()
                val incorrectAnswer2 = result.incorrect_answer[1].toSpanned()
                val incorrectAnswer3 = result.incorrect_answer[2].toSpanned()

                val questions = listOf(
                    correctAnswer,
                    incorrectAnswer1,
                    incorrectAnswer2,
                    incorrectAnswer3
                )

                Collections.shuffle(questions)

                runOnUiThread {

                    questionText.text = question
                    themeText.text =  questionCategory

                    alternative1.text = questions[0]
                    alternative1.isEnabled = true

                    alternative2.text = questions[1]
                    alternative2.isEnabled = true

                    alternative3.text = questions[2]
                    alternative3.isEnabled = true

                    alternative4.text = questions[3]
                    alternative4.isEnabled = true

                    alternative1.setOnClickListener {
                        alternative1.isClickable = false
                        if (alternative1.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative1.text = "---"
                        }
                    }

                    alternative2.setOnClickListener {
                        alternative2.isClickable = false
                        if (alternative2.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative2.text = "---"
                        }
                    }

                    alternative3.setOnClickListener {
                        alternative3.isClickable = false
                        if (alternative3.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative3.text = "---"
                        }
                    }

                    alternative4.setOnClickListener {
                        alternative4.isClickable = false
                        if (alternative4.text == correctAnswer) {
                            fetch()
                        } else {
                            alternative4.text = "---"
                        }
                    }
                }
            }
        })*/
    }
}
