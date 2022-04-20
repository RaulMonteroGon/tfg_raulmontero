package com.example.tfg_raulmontero

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class TaskActivity : AppCompatActivity() {
    lateinit var taskTitleTextView : TextView
    lateinit var task :ListElement

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        taskTitleTextView = findViewById(R.id.taskTitleTextView)

        task = intent.getSerializableExtra("TaskElement") as ListElement

        taskTitleTextView.text = task.getName()
    }

    fun editarTarea(groupid : String,
                    taskid: String,
                    nombretarea : String,
                    asignaciontarea : List<String>,
                    configuraciontarea : String,
                    descripciontarea : String,
                    asignacionsiguientetarea : List<String>,
                    proximarealizaciontarea: Timestamp
    ){


        class tareas (
            val asignacion : List<String>? = asignaciontarea,
            val configuracion: String? = configuraciontarea,
            val descripcion: String?= descripciontarea,
            val asignacionsiguiente : List<String>? = asignacionsiguientetarea,
            val nombre : String = nombretarea,
            val proximarealizacion: Timestamp = proximarealizaciontarea)


        val data = tareas()
        db.collection("groups").document(groupid).collection("tareas").document(taskid)
            .set(data)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
}