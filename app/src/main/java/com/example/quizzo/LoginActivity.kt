package com.example.quizzo

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {

            val email = loginEmail.findViewById<EditText>(
                R.id.loginEmail
            ).editableText.toString()

            val password = loginPassword.findViewById<EditText>(
                R.id.loginPassword
            ).editableText.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("LoggedIn", true)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Login failed or account does not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
