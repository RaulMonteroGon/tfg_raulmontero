package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditTaskActivity : AppCompatActivity() {
    lateinit var deleteTaskBtn : Button


    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var idgroup :String
    lateinit var task :ListElement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        deleteTaskBtn = findViewById(R.id.editdeleteTaskBtn)



        task = intent.getSerializableExtra("TaskElement") as ListElement
        idgroup = intent.getSerializableExtra("idgroup") as String




        deleteTaskBtn.setOnClickListener {
            db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            val deletetaskIntent = Intent(this,MainActivity::class.java)
            startActivity(deletetaskIntent)
            finish()
        }
    }
}