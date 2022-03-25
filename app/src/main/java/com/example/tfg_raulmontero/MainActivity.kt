package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var btnlogoff : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnlogoff = findViewById(R.id.btnLogout)
        val db = Firebase.firestore

        btnlogoff.setOnClickListener{
            /*Firebase.auth.signOut()
            val logout = Intent(this,LoginActivity::class.java)
            startActivity(logout)*/

            /*val group = hashMapOf("group1" to "prueba")
            db.collection("groups").add(group)
                .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
            */
            db.collection("groups").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }





            Toast.makeText(this, "Prueba", Toast.LENGTH_SHORT).show(); //Correcto
        }

    }
}