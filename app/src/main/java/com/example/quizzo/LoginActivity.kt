package com.example.quizzo

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleSignInBtn.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

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

    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch(e: ApiException){
                Log.w("googleAuthError", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount){
        Log.d("authWithGoogle", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d("googleSuccess", "signInWithCredential:success")
                    val user = auth.currentUser
                    saveUserToDatabase()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("LoggedIn", true)
                    startActivity(intent)
                    finish()
                } else{
                    Log.w("googleFailure", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onClick(v: View){
        when (v.id){
            R.id.googleSignInBtn -> signInWithGoogle()
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    private fun saveUserToDatabase(){
        val uid: String? = FirebaseAuth.getInstance().uid
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference( "/Users/$uid")

        val email = auth.currentUser!!.email.toString()
        val splitEmail = email.split("@")
        val username = splitEmail[0]

        val user = User(uid.toString(), username, 0)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "User was saved in the database")
            }
    }
}
