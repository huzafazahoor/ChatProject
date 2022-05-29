package com.example.chatproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LogIn : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //supportActionBar?.hide()

        mAuth = Firebase.auth

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        printSignUp()

        btnLogin.setOnClickListener{


            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            login(email,password)
        }

    }
/*
    private fun performSignin(email: String, password: String)
    {

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    db.collection("User")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                val databaseEmail = document.data.getValue("email").toString()
                                if(email == databaseEmail)
                                {

                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error getting documents.", exception)
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed." + task.exception,Toast.LENGTH_SHORT).show()
                }
            }

    }*/

    private fun login(email: String, password: String){


        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val sharedPreference =  getSharedPreferences("User", Context.MODE_PRIVATE)
                    val editor = sharedPreference.edit()
                    val userid = Firebase.auth.currentUser?.uid
                    editor.putString("UserId",userid)
                    editor.apply()

                    val intent = Intent(this@LogIn,MainActivity::class.java)

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LogIn, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun printSignUp(){
        val ss = SpannableString("Don't have an account? Sign Up now.")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(textView: View) {
                startActivity(Intent(this@LogIn, SignUp::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.CYAN
            }
        }
        ss.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val boldSpan = StyleSpan(Typeface.BOLD)
        ss.setSpan(boldSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val textView = findViewById<TextView>(R.id.signUpText)
        textView.text = ss

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }

}