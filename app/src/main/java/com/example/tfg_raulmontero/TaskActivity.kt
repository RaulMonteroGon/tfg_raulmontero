package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
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

    lateinit var inprocessBtn : MaterialButton
    lateinit var doneBtn :MaterialButton
    lateinit var notdoneBtn:MaterialButton

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var idgroup :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        toggleGroup = findViewById(R.id.toggleButtonGroup)
        taskTitleTextView = findViewById(R.id.taskTitleTextView)
        inprocessBtn = findViewById(R.id.inprogressBtn)
        doneBtn = findViewById(R.id.doneBtn)
        notdoneBtn = findViewById(R.id.notdoneBtn)

        task = intent.getSerializableExtra("TaskElement") as ListElement
        idgroup = intent.getSerializableExtra("idgroup") as String
        Toast.makeText(this, task.name, Toast.LENGTH_SHORT).show()
        setupcheck()
        taskTitleTextView.text = task.getName()
        toggleGroup.addOnButtonCheckedListener { toggleGroup, checkedId, isChecked ->
            if(isChecked){
                when (checkedId){
                    R.id.inprogressBtn -> setinprocess()
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
        db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup).update("estado","Finalizado")
        Toast.makeText(this, "Has marcado la tarea como finalizada", Toast.LENGTH_SHORT).show()
    }
    fun setinprocess(){
        db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup).update("estado","En proceso")
        Toast.makeText(this, "Has marcado la tarea como en proceso", Toast.LENGTH_SHORT).show()
    }
    fun setnotdone(){
        db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup).update("estado","Sin hacer")
        Toast.makeText(this, "Has marcado la tarea como pendiente", Toast.LENGTH_SHORT).show()
    }
    fun setupcheck(){
        db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup)
            .get()
            .addOnSuccessListener {
                val estado = it["estado"] as String?
                when (estado){
                    "Sin hacer" -> toggleGroup.check(R.id.notdoneBtn)
                    "En proceso" -> toggleGroup.check(R.id.inprogressBtn)
                    "Finalizado" -> toggleGroup.check(R.id.doneBtn)
                }
            }

        //toggleGroup.check(R.id.inprocessBtn)
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