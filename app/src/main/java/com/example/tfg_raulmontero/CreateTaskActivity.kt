package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.text.SimpleDateFormat

class CreateTaskActivity : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    lateinit var nameTask : EditText
    lateinit var descriptionTask : EditText
    lateinit var settingsTask : EditText
    lateinit var asignationTask : MultiAutoCompleteTextView
    lateinit var dateTask : TextView
    lateinit var selectDay :Button
    lateinit var horaTask : EditText
    lateinit var hourSwitch : Switch
    lateinit var submitnewtaskButton :Button


    lateinit var group :ListElement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)


        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        nameTask = findViewById(R.id.nameNewTaskEditText)
        descriptionTask = findViewById(R.id.descriptionNewTaskEditText)
        settingsTask = findViewById(R.id.configuracionNuevaTareaEditText)
        asignationTask = findViewById(R.id.asignacionNewTaskTextView)
        dateTask = findViewById(R.id.startingDayTextView)
        selectDay = findViewById(R.id.selectDayButton)
        horaTask = findViewById(R.id.editTextTime)
        hourSwitch = findViewById(R.id.hourSwitch)
        submitnewtaskButton = findViewById(R.id.submitNewTaskButton)

        selectDay.setOnClickListener { showDatePickerDialog() }

        group = intent.getSerializableExtra("GroupElement") as ListElement


        setupasignacion()
        Thread.sleep(1_000)


        submitnewtaskButton.setOnClickListener{
            val asignacionarray = asignationTask.text.toString().split(",")
            creartarea(group.idgroup,
                nameTask.text.toString(),
                asignacionarray,
                settingsTask.text.toString(),
                descriptionTask.text.toString(),
                dateTask.text.toString(),
                horaTask.text.toString()
                )
        }
    }

    private fun setupasignacion(){
        db.collection("groups").document(group.idgroup).get()
            .addOnSuccessListener {

                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, it.get("participantes")as List<String>)
                asignationTask.setAdapter(adapter)
                asignationTask.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
            }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment{day, month, year -> onDateSelected(day,month,year) }
        datePicker.show(supportFragmentManager,"datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        dateTask.setText("$day/$month/$year")
    }


    private fun creartarea(documentid : String,
                   nombretarea : String,
                   asignaciontarea : List<String>,
                   configuraciontarea : String,
                   descripciontarea : String,
                   diatarea: String,
                   horatarea:String?
                   ){

        class tareas (
            val asignacion : List<String>? = asignaciontarea,
            val configuracion: String? = configuraciontarea,
            val descripcion: String?= descripciontarea,
            val estado : String = "Sin hacer",
            val nombre : String = nombretarea,
            val dia : String= diatarea,
            val hora : String? = horatarea
        )


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