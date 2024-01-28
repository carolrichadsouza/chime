package com.example.chimeapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Chat : AppCompatActivity() {

    private lateinit var chatrecyclerview : RecyclerView
    private lateinit var editsendmessage : EditText
    private lateinit var sendbtn : ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagelist : ArrayList<Message>
    private lateinit var database : DatabaseReference

    var receiverroom : String? = null
    var senderroom  : String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title = "Chats"

        val name = intent.getStringExtra("name")
        val receiveruid = intent.getStringExtra("uid")
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid

        database = FirebaseDatabase.getInstance().getReference()

        senderroom = receiveruid + senderuid
        receiverroom = senderuid + receiveruid

        supportActionBar?.title = name

        chatrecyclerview = findViewById(R.id.chatrecyclerview)
        editsendmessage  = findViewById(R.id.editsendmessage)
        sendbtn = findViewById(R.id.sendmessage)
        messagelist = ArrayList()
        messageAdapter = MessageAdapter(this,messagelist)

        chatrecyclerview.layoutManager = LinearLayoutManager(this)
        chatrecyclerview.adapter = messageAdapter

        database.child("chats").child(senderroom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messagelist.clear()

                    for(snap in snapshot.children) {
                        val message = snap.getValue(Message::class.java)
                        messagelist.add(message!!)
                   }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        sendbtn.setOnClickListener {
            val message = editsendmessage.text.toString()
            val messageobj = Message(message,senderuid)

            database.child("chats").child(senderroom!!).child("messages").push()
                .setValue(messageobj).addOnSuccessListener {
                    database.child("chats").child(receiverroom!!).child("messages").push()
                        .setValue(messageobj)
                }
            editsendmessage.setText("")

        }



    }
}