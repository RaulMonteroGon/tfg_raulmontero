package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
    lateinit var asignationTask : ListView
    lateinit var dateTask : TextView
    lateinit var selectDay :Button
    lateinit var hourTextView : TextView
    lateinit var hourSwitch : Switch
    lateinit var submitnewtaskButton :Button

    var dataModel :ArrayList<DataModel>? =null
    lateinit var membersadapter: MembersAdapter

    lateinit var group :ListElement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)


        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        nameTask = findViewById(R.id.nameNewTaskEditText)
        descriptionTask = findViewById(R.id.descriptionNewTaskEditText)
        settingsTask = findViewById(R.id.configuracionNuevaTareaEditText)
        asignationTask = findViewById<View>(R.id.newtaskmembersGroup) as ListView
        dateTask = findViewById(R.id.startingDayTextView)
        selectDay = findViewById(R.id.selectDayButton)
        hourTextView = findViewById(R.id.hourTextView)
        hourSwitch = findViewById(R.id.hourSwitch)
        submitnewtaskButton = findViewById(R.id.submitNewTaskButton)

        selectDay.setOnClickListener { showDatePickerDialog() }

        group = intent.getSerializableExtra("GroupElement") as ListElement


        setupasignacion()



        hourSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hourTextView.text = "All day"
            } else {
                settime()
            }
        }

        submitnewtaskButton.setOnClickListener{
            var memberschecked = mutableListOf<String>()
            //memberschecked!!.add(dataModel!![0].name.toString())

            for (i in 0 until dataModel!!.size){
                if (dataModel!![i].checked == true){
                    memberschecked!!.add(dataModel!![i].name.toString())
                }
            }
            creartarea(group.idgroup,
                nameTask.text.toString(),
                memberschecked,
                settingsTask.text.toString(),
                descriptionTask.text.toString(),
                dateTask.text.toString(),
                hourTextView.text.toString()
                )
            val createTaskIntent = Intent(this,CreateTaskActivity::class.java)
            createTaskIntent.putExtra("GroupElement", group)
            startActivity(createTaskIntent)
            finish()

        }
    }


    private fun settime(){
        val timePicker = TimePickerFragment{onTimeSelected(it)}
        timePicker.show(supportFragmentManager,"time")
    }
    private fun onTimeSelected(time: String) {
        hourTextView.text = time
    }
    private fun setupasignacion(){
        dataModel = ArrayList<DataModel>()

        db.collection("groups").document(group.idgroup)
            .get()
            .addOnSuccessListener {
                val memberslist = it["participantes"] as List<String>
                for (members in memberslist.indices!!){
                    dataModel!!.add(DataModel(memberslist[members],false))
                }




                asignationTask.setItemsCanFocus(false);
                membersadapter = MembersAdapter(dataModel!!, applicationContext)
                asignationTask.adapter = membersadapter


                asignationTask.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                    val dataModel1 : DataModel = dataModel!![position] as DataModel
                    dataModel1.checked = !dataModel1.checked
                    membersadapter.notifyDataSetChanged()
                })



            }.addOnFailureListener { exception ->
                dataModel!!.add(DataModel("no funciona",false))
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