package com.example.chimeapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chimeapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var userrecyclerview : RecyclerView
    private lateinit var userlist : ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Chats"
        supportActionBar?.title = Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference()

        userlist = ArrayList()
        adapter = UserAdapter(this, userlist)
        userrecyclerview = findViewById(R.id.userrecyclerview)

        userrecyclerview.layoutManager = LinearLayoutManager(this)
        userrecyclerview.adapter = adapter

        database.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()
                for(snap in snapshot.children) {
                    val currentUser = snap.getValue(User::class.java)

                    if (auth.currentUser?.uid != currentUser?.uid) {
                        userlist.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                auth.signOut()
                val intent = Intent(this@MainActivity, Login::class.java)
                finish()
                startActivity(intent)
                return true
            }
            R.id.profile -> {
                val intent = Intent(this@MainActivity, Profile::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }
}