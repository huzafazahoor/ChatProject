package com.example.chatproject

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter


    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    private var userid : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.title = ""
        actionBar.setDisplayHomeAsUpEnabled(false)
        mAuth = Firebase.auth

        val sharedPreference =  getSharedPreferences("User", Context.MODE_PRIVATE)
        userid = sharedPreference.getString("UserId","").toString()

        Toast.makeText(baseContext, userid,Toast.LENGTH_SHORT).show()
        if (userid == "")
        {
            loadLoginActivity()
        }
        else
        {
            mDbRef = FirebaseDatabase.getInstance().reference //getReference()

            userList = ArrayList()
            adapter = UserAdapter(this, userList)

            // use recyclerview
            userRecyclerView = findViewById(R.id.userRecyclerView)

            userRecyclerView.layoutManager = LinearLayoutManager(this)
            userRecyclerView.adapter = adapter

            mDbRef.child("user").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        val currentUser = postSnapshot.getValue(User::class.java)
                        if(mAuth.currentUser?.uid != currentUser?.uid){
                            userList.add(currentUser!!)
                        }
                        else if (mAuth.currentUser?.uid == currentUser?.uid)
                        {
                            val actionBar = supportActionBar
                            actionBar!!.title = currentUser?.name
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }
    private fun loadLoginActivity() {
        val sharedPreference =  getSharedPreferences("User", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("UserId",userid)
        editor.commit()

        val intent = Intent(this, LogIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout -> {
            Firebase.auth.signOut()
            userid = ""

            loadLoginActivity()

            Toast.makeText(baseContext, "LOGOUT", Toast.LENGTH_SHORT).show()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}