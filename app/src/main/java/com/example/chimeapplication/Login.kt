package com.example.chimeapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var btnsignup : Button
    private lateinit var btnlogin : Button
    private lateinit var editemail : EditText
    private lateinit var editpassword : EditText

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        btnsignup = findViewById(R.id.btnsignup)
        btnlogin = findViewById(R.id.btnlogin)
        editemail = findViewById(R.id.loginemail)
        editpassword = findViewById(R.id.loginpass)

        btnsignup.setOnClickListener {
            val i = Intent(this, SignUp::class.java)
            startActivity(i)
        }

        btnlogin.setOnClickListener {
            val email = editemail.text.toString()
            val password = editpassword.text.toString()

            logintheuser(email,password)
        }

    }

    private fun logintheuser(email : String, password : String) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {

                    val i = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(i)
                }
                else {
                    Toast.makeText(this@Login, "User does not exist! Create a new account", Toast.LENGTH_SHORT).show()
                }
            }
    }
}