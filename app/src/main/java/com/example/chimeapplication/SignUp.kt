package com.example.chimeapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var btnsignup : Button
    private lateinit var editname : EditText
    private lateinit var editemail : EditText
    private lateinit var editpassword : EditText
    private lateinit var editconfirmpassword : EditText

    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var fstore : FirebaseFirestore
    private lateinit var db : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        btnsignup = findViewById(R.id.buttonsignup)
        editname = findViewById(R.id.signupname)
        editemail = findViewById(R.id.signupemail)
        editpassword = findViewById(R.id.signuppass)
        editconfirmpassword = findViewById(R.id.confirmsignuppass)

        btnsignup.setOnClickListener {
            val name = editname.text.toString()
            val email = editemail.text.toString()
            val password = editpassword.text.toString()
            val confirmpassword = editconfirmpassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Enter Email ID", Toast.LENGTH_SHORT ).show()
            }
            else if(TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Enter password", Toast.LENGTH_SHORT ).show()
            }
            else if(password.length<8) {
                Toast.makeText(applicationContext, "Password should be greater than 8 characters", Toast.LENGTH_SHORT ).show()
            }
            else if (password != confirmpassword){
                Toast.makeText(applicationContext, "Both passwords do not match", Toast.LENGTH_SHORT ).show()
            }
            else {
                createAccount(name, email, password)
            }

        }

    }

    private fun createAccount(name : String, email : String ,password : String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {

                    userstorage(name,auth.currentUser?.uid!!)
                    userdatabase(name, email, auth.currentUser?.uid!!)

                    val i = Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(i)
                }
                else {
                    Toast.makeText(this@SignUp, "Error occurred! Please Try Again!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun userstorage(name:String, uid: String) {
        db = fstore.collection("users").document(uid)
        val obj = mutableMapOf<String, String>()
        obj["UID"] = uid
        obj["Username"] = name
        obj["UserStatus"] = ""
        obj["UserProfilePic"] = R.drawable.user.toString()

        db.set(obj).addOnSuccessListener {
            Log.d("Success", "Success!!")
        }

    }

    private fun userdatabase(name: String, email: String, uid: String?) {
        database = FirebaseDatabase.getInstance().getReference()
        database.child("users").child(uid!!).setValue(User(name,email,uid))

    }
}