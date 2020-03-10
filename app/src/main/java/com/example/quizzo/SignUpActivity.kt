package com.example.quizzo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Tag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()

        SignUpButton.setOnClickListener{

            val email = signUpEmail.findViewById<EditText>(
                R.id.signUpEmail
            ).editableText.toString()

            val username = signUpUsername.findViewById<EditText>(
                R.id.signUpUsername
            ).editableText.toString()

            val password = signUpPassword.findViewById<EditText>(
                R.id.signUpPassword
            ).editableText.toString()

            val passwordCheck = signUpPasswordRepeat.findViewById<EditText>(
                R.id.signUpPasswordRepeat
            ).editableText.toString()

            if(email.length < 5){
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }

            else if(username.length < 2){
                Toast.makeText(this, "Username must be at least 2 characters", Toast.LENGTH_SHORT).show()
            }

            else if(password.length < 5){
                Toast.makeText(this, "Password must be at least 5 characters", Toast.LENGTH_SHORT).show()
            }

            else if(password != passwordCheck){
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
            }

            else{
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful){
                            val user = auth.currentUser
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else{
                            Toast.makeText(this, "Failed signing up, try again", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
    private fun saveUserToDatabase(){
        val uid: String? = FirebaseAuth.getInstance().uid
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference( "/Users/$uid")

        val user = User(uid.toString(), signUpUsername.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "User was saved in the database")
            }
    }
}

class User(val uid: String, val Username: String)
