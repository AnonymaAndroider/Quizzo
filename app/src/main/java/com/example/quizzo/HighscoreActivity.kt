package com.example.quizzo

import android.graphics.Insets.add
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.opencensus.tags.unsafe.ContextUtils.getValue
import kotlinx.android.synthetic.main.activity_highscore.*
import javax.security.auth.callback.Callback

class HighscoreActivity : AppCompatActivity() {

    private lateinit var listAdapter: ArrayAdapter<String>
    private val scoreList = mutableListOf<User>()
    private lateinit var stringList:List<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore)

        retrieveHighScoreAndUsername {

            val listView = listViewHS.findViewById<ListView>(R.id.listViewHS)
            listAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                stringList
            )
            listView.adapter = listAdapter
            Log.d("listAdapter", listAdapter.toString())
        }
    }


    private fun retrieveHighScoreAndUsername(callback: ()->Unit){

        var ref = FirebaseDatabase.getInstance().reference

        ref.child("Users").orderByValue().addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelledError", "onCancelled", error.toException())
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                    for(ds in dataSnapShot.children){
                        var highScore = ds.child("highscore").getValue(Int::class.java)
                        var username = ds.child("username").getValue(String::class.java)
                        if(highScore != null && username != null) {
                            scoreList.add(User("", username.toString(), highScore))
                            stringList = scoreList.map { it.Username + " " + "Score:" + " " + it.highscore }
                            Log.d("stringList:", stringList.toString())
                        }
                    }
                callback()
            }
        })
    }
}
