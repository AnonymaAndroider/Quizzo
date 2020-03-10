package com.example.quizzo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signUpUsername.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                //if(count == null){
                 //   signUpButton.isEnabled = false
                //}

                if(count > 10){
                    signUpButton.isEnabled = false
                    signUpUsername.error = getString(R.string.usernameToLong)
                }

                if(count in 1..10){
                    signUpButton.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}
