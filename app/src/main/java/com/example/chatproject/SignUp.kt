package com.example.chatproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.project.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {


    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()
        mAuth = Firebase.auth


        viewInitializations()


        btnSignUp.setOnClickListener{
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            is(validateInput())
            {

                signUp(name,email,password)
            }
        }
    }

    private fun viewInitializations() {
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)

    }

    private fun validateInput(): Boolean {
        if (edtName.text.toString() == "") {
            edtName.error = "Please Enter Last Name"
            return false
        }
        if (edtEmail.text.toString() == "") {
            edtEmail.error = "Please Enter Email"
            return false
        }
        // checking the proper email format
        if (!isEmailValid(edtEmail.text.toString())) {
            edtEmail.error = "Please Enter Valid Email"
            return false
        }
        if (edtPassword.text.toString() == "") {
            edtPassword.error = "Please Enter Password"
            return false
        }
        val MIN_PASSWORD_LENGTH = 8
        if (edtPassword.text.length < MIN_PASSWORD_LENGTH) {
            edtPassword.error = "Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters"
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signUp(name: String, email: String, password: String){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("ITM","success signup")
                    // code for jumping to home
                    val user = mAuth.currentUser
                    if (user != null) {
                        addUserToDatabase(name, email, mAuth.currentUser?.uid!!)
                    }

                } else {
                    Log.d("ITM","fail signup")
                    Toast.makeText(this@SignUp, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name:String, email:String, uid:String){

        mDbRef = Firebase.database.reference
        mDbRef.child("user").child(uid).setValue(User(name, email, uid))

        val sharedPreference =  getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("UserId",uid)
        editor.apply()

        val intent = Intent(this@SignUp,MainActivity::class.java)
        //intent.putExtra("UserId", userid)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

    }
}