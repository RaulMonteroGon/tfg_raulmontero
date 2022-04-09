package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class CreateTaskActivity : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()


    }


    fun creartarea(documentid : String,
                   nombretarea : String,
                   asignaciontarea : List<String>,
                   configuraciontarea : String,
                   descripciontarea : String,
                   asignacionsiguientetarea : List<String>,
                   proximarealizaciontarea:Timestamp){

        class tareas (
            val asignacion : List<String>? = asignaciontarea,
            val configuracion: String? = configuraciontarea,
            val descripcion: String?= descripciontarea,
            val estado : String = "Sin hacer",
            val asignacionsiguiente : List<String>? = asignacionsiguientetarea,
            val nombre : String = nombretarea,
            val ultimarealizacion : Timestamp? = null,
            val proximarealizacion: Timestamp = proximarealizaciontarea)


        val data = tareas()
        db.collection("groups").document(documentid).collection("tareas")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}