package com.example.chimeapplication

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class Profile : AppCompatActivity() {

    private lateinit var txtprofilename : TextView
    private lateinit var txtprofilestatus : TextView
    private lateinit var profilepic : CircleImageView
    private lateinit var profilepicadd : ImageView
    private lateinit var editprofilename : EditText
    private lateinit var editprofilestatus : EditText
    private lateinit var updateprofile : Button
    private lateinit var saveprofile : Button

    private lateinit var auth : FirebaseAuth
    private lateinit var fstore : FirebaseFirestore
    private lateinit var db : DocumentReference
    private lateinit var userid : String
    private lateinit var image : ByteArray
    private lateinit var storageReference : StorageReference

    val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        uploadImage(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile"
        supportActionBar?.title = Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>");

        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        userid = auth.currentUser!!.uid

        storageReference = FirebaseStorage.getInstance().reference.child("$userid/profilepicture")

        txtprofilename = findViewById(R.id.txtprofilename)
        txtprofilestatus = findViewById(R.id.txtprofilestatus)
        profilepic = findViewById(R.id.profileimage)
        profilepicadd = findViewById(R.id.addprofileimage)
        editprofilename = findViewById(R.id.etprofilename)
        editprofilestatus = findViewById(R.id.etprofilestatus)
        updateprofile = findViewById(R.id.updateprofile)
        saveprofile = findViewById(R.id.saveprofile)

        updateprofile.visibility= View.VISIBLE

        db = fstore.collection("users").document(userid)
        db.addSnapshotListener {value,error->
            if(error!=null) {
                Log.d("Error","Unable to fetch data")
            }
            else {
                txtprofilename.text = value?.getString("Username")
                txtprofilestatus.text = value?.getString("UserStatus")
                Picasso.get().load(value?.getString("UserProfilePic")).error(R.drawable.user).into(profilepic)
            }
        }

        updateprofile.setOnClickListener{
            txtprofilename.visibility = View.GONE
            txtprofilestatus.visibility = View.GONE
            editprofilename.visibility = View.VISIBLE
            editprofilestatus.visibility = View.VISIBLE
            saveprofile.visibility = View.VISIBLE
            updateprofile.visibility = View.GONE

            editprofilename.text = Editable.Factory.getInstance().newEditable(txtprofilename.text.toString())
            editprofilestatus.text = Editable.Factory.getInstance().newEditable(txtprofilestatus.text.toString())

        }

        saveprofile.setOnClickListener {
            txtprofilename.visibility = View.VISIBLE
            txtprofilestatus.visibility = View.VISIBLE
            editprofilename.visibility = View.GONE
            editprofilestatus.visibility = View.GONE
            saveprofile.visibility = View.GONE
            updateprofile.visibility = View.VISIBLE


            val obj = mutableMapOf<String,String>()
            obj["UID"] = auth.currentUser?.uid!!
            obj["Username"] = editprofilename.text.toString()
            obj["UserStatus"] = editprofilestatus.text.toString()
            obj["UserProfilePic"] = R.drawable.user.toString()
            db.set(obj).addOnSuccessListener {
                Log.d("Success", "Data added successfully")
            }
        }

        profilepicadd.setOnClickListener {
            register.launch(null)
        }
    }

    private fun uploadImage(it: Bitmap?) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        it?.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
        image = byteArrayOutputStream.toByteArray()
        storageReference.putBytes(image).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                val obj = mutableMapOf<String,String>()
                obj["UserProfilePic"] = it.toString()
                db.update(obj as Map<String,Any>).addOnSuccessListener {
                    Log.d("Success", "Profile picture updated!")
                }
            }
        }

    }
}