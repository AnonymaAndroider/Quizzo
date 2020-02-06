package com.example.quizzo

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val myUsername = "Admin"
        val myPassword = "abc123"
        var isLoggedIn = false

        loginUsername.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(count > 10){
                    loginUsername.error = getString(R.string.usernameToLong)
                    loginButton.isEnabled = false
                }
                if(count == 0){
                    loginButton.isEnabled = false
                }

                if(count in 1..10){
                    loginButton.isEnabled = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })

        loginButton.setOnClickListener {

            val username = loginUsername.findViewById<EditText>(
                R.id.loginUsername
            ).editableText.toString()

            val password = loginPassword.findViewById<EditText>(
                R.id.loginPassword
            ).editableText.toString()

            if(username == myUsername && password == myPassword){
                isLoggedIn = true
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("LoggedIn", isLoggedIn)
                startActivity(intent)
            }
        }
    }
}
