package com.example.tfg_raulmontero

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class TaskActivity : AppCompatActivity() {
    lateinit var taskTitleTextView : TextView
    lateinit var task :ListElement
    lateinit var toggleGroup: MaterialButtonToggleGroup

    lateinit var inprocessBtn : Button
    lateinit var doneBtn :Button
    lateinit var notdoneBtn:Button

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        toggleGroup = findViewById(R.id.toggleButtonGroup)
        taskTitleTextView = findViewById(R.id.taskTitleTextView)
        inprocessBtn = findViewById(R.id.inprocessBtn)
        doneBtn = findViewById(R.id.doneBtn)
        notdoneBtn = findViewById(R.id.notdoneBtn)

        task = intent.getSerializableExtra("TaskElement") as ListElement
        val idgroup = intent.getSerializableExtra("idgroup") as String


        taskTitleTextView.text = task.getName()
        toggleGroup.addOnButtonCheckedListener { toggleGroup, checkedId, isChecked ->
            if(isChecked){
                when (checkedId){
                    R.id.inprocessBtn -> setinprocess()
                    R.id.doneBtn -> setdone()
                    R.id.notdoneBtn -> setnotdone()
                }
            }else{
                if (toggleGroup.checkedButtonId == View.NO_ID){
                    setupcheck()
                }
            }
        }
    }

    fun setdone(){

    }
    fun setinprocess(){

    }
    fun setnotdone(){

    }
    fun setupcheck(){

        toggleGroup.check(R.id.inprocessBtn)
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