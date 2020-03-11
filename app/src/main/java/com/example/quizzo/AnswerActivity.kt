package com.example.quizzo


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_answer.*
import okhttp3.*
import java.io.IOException
import java.util.Collections.shuffle



data class TriviaRequest (

    val results: List<TriviaResult>

)

class TriviaResult(

    val category: String,
    val token: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>

)


class AnswerActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var score = 0
    private val amountOfQuestions = 5
    private var questionAnswered = 0
    private var progressCount = 0
    private lateinit var counter: CountDownTimer
    private val millisInFuture = 11000
    private val countDownInterval = 100
    private var token: String = ""
    private var onResumeCalled = true
    private var isLoggedIn = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        isLoggedIn = intent.getBooleanExtra("LoggedIn", false)
        Log.d("inside onCreate", "Inside onCreate")
        onResumeCalled = false
        val resumeTrue = intent.getBooleanExtra("onResumeCalled", false)
        onResumeCalled = resumeTrue
        Log.d("onResumeCalled OnCretae", onResumeCalled.toString())
        if(!onResumeCalled) {
            val loadBar = findViewById<ProgressBar>(R.id.loadBar)
            Thread(Runnable {
                runOnUiThread {
                    loadBar.visibility = View.VISIBLE
                }
                try {
                    Thread.sleep(3000)
                    startQuiz()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    loadBar.visibility = View.GONE
                }

            }).start()
        }
    }



    override fun onPause() {
        super.onPause()
        Log.d("onPause", "In onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("onStop", "Inside onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "Inside destroy")
        cancelCallToAPI()
    }

    override fun onResume() {
        super.onResume()
        Log.d("inside onresume", "Inside onResume()")
        val resumeTrue = intent.getBooleanExtra("onResumeCalled", false)
        onResumeCalled = resumeTrue
        Log.d("onResumeCalled OnResume", onResumeCalled.toString())
        if(onResumeCalled){
            Log.d("inside if onresume", "Inside if statement in onResume")
            val retryToken = intent.getStringExtra("token")
            if(retryToken != null){
                token = retryToken
                Log.d("retryToken", token)
                val loadBar = findViewById<ProgressBar>(R.id.loadBar)
                Thread(Runnable {
                    runOnUiThread {
                        loadBar.visibility = View.VISIBLE
                    }
                    try {
                        Thread.sleep(3000)
                        fetchQuestions(token){
                            barProgressTimer()
                        }
                    } catch (e: InterruptedException){
                        e.printStackTrace()
                    }
                    runOnUiThread{
                        loadBar.visibility = View.GONE
                    }

                }).start()
            }
        }
    }


    private fun sendThemeAndDifficultyAndScoreAndToken(score: Int, theme: Int, difficulty: String) {
        val intent = Intent(this, ShowScoreActivity::class.java)
        Log.d("LoggedInAnswer", isLoggedIn.toString())
        intent.putExtra("score", score)
        intent.putExtra("theme", theme)
        intent.putExtra("difficulty", difficulty)
        intent.putExtra("token", token)
        intent.putExtra("LoggedIn", isLoggedIn)
        startActivity(intent)
        finish()
    }

    private fun barProgressTimer(){

        runOnUiThread {

            progressCount = 0
            val theme = intent.getIntExtra("theme", 0)
            val difficulty = intent.getStringExtra("difficulty")

            counter = object : CountDownTimer(millisInFuture.toLong(), countDownInterval.toLong()) {

                override fun onFinish() {
                    questionAnswered += 1
                    counter.cancel()
                    if(questionAnswered == amountOfQuestions){
                        sendThemeAndDifficultyAndScoreAndToken(score, theme, difficulty)
                    }
                    fetchQuestions(token){
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
    }

    private fun startQuiz(){

        fetchTokens {
            fetchedToken ->
            Log.d("Fetched token:", token)
            fetchQuestions(fetchedToken){
                barProgressTimer()
            }
        }
    }

    private fun fetchTokens(callback: (String)->Unit){
        val tokenUrl = "https://opentdb.com/api_token.php?command=request"
        val retrieveToken = Request.Builder()
            .url(tokenUrl).tag("FetchTokens")
            .build()
        client.newCall(retrieveToken).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val triviaRequest = gson.fromJson(body, TriviaResult::class.java)
                token = triviaRequest.token
                callback(token)
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback("Error")
            }
        })
    }

    private fun cancelCallToAPI(){
        Log.d("cancelCall", "inside cancelCalls")
        Log.d("client before:", client.toString())
        for(call : Call in client.dispatcher.runningCalls() ){
            Log.d("runningcalls", "inside runningcalls")
            if(call.request().tag()?.equals("FetchQuestions")!!){
                Log.d("inside if", "Inside if fetchquestions")
                call.cancel()
                Log.d("client after:", client.toString())

            }
            if(call.request().tag()?.equals("FetchTokens")!!){
                Log.d("inside if", "Inside if fetchtoken")
                call.cancel()
            }
        }
        for(call : Call in client.dispatcher.queuedCalls()){
            Log.d("queuedcalls", "inside queuedcalls")
            if(call.request().tag()?.equals("FetchQuestions")!!){
                call.cancel()
            }
            if(call.request().tag()?.equals("FetchTokens")!!){
                call.cancel()
            }
        }
    }


    private fun fetchQuestions(aToken: String, callback: ()->Unit){

        val theme = intent.getIntExtra("theme", 0)
        val difficulty = intent.getStringExtra("difficulty")

        //val resetToken = "https://opntdb.com/api_token.php?command=reset&token=$token"
        val url = "https://opentdb.com/api.php?amount=1&category=$theme&difficulty=$difficulty&type=multiple&token=$aToken"
        Log.d("token:", aToken)
        val request = Request.Builder()
            .url(url).tag("FetchQuestions")
            .build()
        //Log.d("request", request.toString())
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
                val alternatives = listOf(
                    correctAnswer,
                    incorrectAnswer1,
                    incorrectAnswer2,
                    incorrectAnswer3
                )

                shuffle(alternatives)

                runOnUiThread {

                    val button1 = findViewById<Button>(R.id.alternative1)
                    val button2 = findViewById<Button>(R.id.alternative2)
                    val button3 = findViewById<Button>(R.id.alternative3)
                    val button4 = findViewById<Button>(R.id.alternative4)

                    questionText.text = question
                    themeText.text = questionCategory

                    button1.text = alternatives[0]
                    button1.setTextColor(Color.BLACK)
                    button1.isEnabled = true

                    button2.text = alternatives[1]
                    button2.setTextColor(Color.BLACK)
                    button2.isEnabled = true

                    button3.text = alternatives[2]
                    button3.setTextColor(Color.BLACK)
                    button3.isEnabled = true

                    button4.text = alternatives[3]
                    button4.setTextColor(Color.BLACK)
                    button4.isEnabled = true

                    button1.setOnClickListener {
                        button1.isClickable = false
                        questionAnswered += 1
                        if(questionAnswered == amountOfQuestions){
                            counter.cancel()
                            sendThemeAndDifficultyAndScoreAndToken(score, theme, difficulty)
                        }
                        else if (button1.text == correctAnswer) {
                            button1.setTextColor(Color.GREEN)
                            when (difficulty) {
                                "easy" -> score += 1
                                "medium" -> score += 3
                                "hard" -> score += 5
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                        else if(button1.text != correctAnswer){
                            button1.setTextColor(Color.RED)
                            when(difficulty) {
                                "medium" -> score -= 1
                                "hard" -> score -= 2
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                    }

                    button2.setOnClickListener {

                        button2.isClickable = false
                        questionAnswered += 1
                        if(questionAnswered == amountOfQuestions){
                            counter.cancel()
                            sendThemeAndDifficultyAndScoreAndToken(score, theme, difficulty)
                        }
                        else if (button2.text == correctAnswer) {
                            button2.setTextColor(Color.GREEN)
                            when (difficulty) {
                                "easy" -> score += 1
                                "medium" -> score += 3
                                "hard" -> score += 5
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                        else if(button2.text != correctAnswer){
                            button2.setTextColor(Color.RED)
                            when(difficulty) {
                                "medium" -> score -= 1
                                "hard" -> score -= 2
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                    }

                    button3.setOnClickListener {

                        button3.isClickable = false
                        questionAnswered += 1
                        if(questionAnswered == amountOfQuestions){
                            counter.cancel()
                            sendThemeAndDifficultyAndScoreAndToken(score, theme, difficulty)
                        }
                        else if (button3.text == correctAnswer) {
                            button3.setTextColor(Color.GREEN)
                            when (difficulty) {
                                "easy" -> score += 1
                                "medium" -> score += 3
                                "hard" -> score += 5
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                        else if(button3.text != correctAnswer){
                            button3.setTextColor(Color.RED)
                            when(difficulty) {
                                "medium" -> score -= 1
                                "hard" -> score -= 2
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                    }

                    button4.setOnClickListener {

                        button4.isClickable = false
                        questionAnswered += 1
                        if(questionAnswered == amountOfQuestions){
                            counter.cancel()
                            sendThemeAndDifficultyAndScoreAndToken(score, theme, difficulty)
                        }
                        else if (button4.text == correctAnswer) {
                            button4.setTextColor(Color.GREEN)
                            when (difficulty) {
                                "easy" -> score += 1
                                "medium" -> score += 3
                                "hard" -> score += 5
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                        else if(button4.text != correctAnswer){
                            button4.setTextColor(Color.RED)
                            when(difficulty) {
                                "medium" -> score -= 1
                                "hard" -> score -= 2
                            }
                            counter.cancel()
                            fetchQuestions(token){
                                barProgressTimer()
                            }
                        }
                    }
                }
                callback()
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

        })
    }
}
