package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class EditTaskActivity : AppCompatActivity() {
    lateinit var deleteTaskBtn : Button


    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var idgroup :String
    lateinit var task :ListElement


    lateinit var nameEditTaskEditText : EditText
    lateinit var descriptionEditTaskEditText : EditText
    lateinit var configuracionEditTareaEditText2 : EditText
    lateinit var edittaskmembersGroup : ListView
    lateinit var startingDayEditTextView : TextView
    lateinit var selectDayEditButton : Button
    lateinit var hourEditTextView : TextView
    lateinit var hourEditSwitch : Switch
    lateinit var submitEditTaskButton4 : Button


    var dataModel :ArrayList<DataModel>? =null
    lateinit var membersadapter: MembersAdapter
    lateinit var memberslist : List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        deleteTaskBtn = findViewById(R.id.editdeleteTaskBtn)

        nameEditTaskEditText = findViewById(R.id.nameEditTaskEditText)
        descriptionEditTaskEditText = findViewById(R.id.descriptionEditTaskEditText)
        configuracionEditTareaEditText2 = findViewById(R.id.configuracionEditTareaEditText2)
        edittaskmembersGroup = findViewById(R.id.edittaskmembersGroup)
        startingDayEditTextView = findViewById(R.id.startingDayEditTextView)
        selectDayEditButton = findViewById(R.id.selectDayEditButton)
        hourEditTextView = findViewById(R.id.hourEditTextView)
        hourEditSwitch = findViewById(R.id.hourEditSwitch)
        submitEditTaskButton4 = findViewById(R.id.submitEditTaskButton4)



        task = intent.getSerializableExtra("TaskElement") as ListElement
        idgroup = intent.getSerializableExtra("idgroup") as String


        setdata()

        deleteTaskBtn.setOnClickListener {
            db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            val deletetaskIntent = Intent(this,MainActivity::class.java)
            startActivity(deletetaskIntent)
            finish()
        }
        submitEditTaskButton4.setOnClickListener {
            var memberschecked = mutableListOf<String>()
            //memberschecked!!.add(dataModel!![0].name.toString())

            for (i in 0 until dataModel!!.size){
                if (dataModel!![i].checked == true){
                    memberschecked!!.add(dataModel!![i].name.toString())
                }
            }
            editarTarea(
                nameEditTaskEditText.text.toString(),
                memberschecked,
                configuracionEditTareaEditText2.text.toString(),
                descriptionEditTaskEditText.text.toString(),
                startingDayEditTextView.text.toString(),
                hourEditTextView.text.toString()
            )
            val submitedittask = Intent(this,TaskActivity::class.java)
            submitedittask.putExtra("TaskElement", task)
            submitedittask.putExtra("idgroup", idgroup)
            startActivity(submitedittask)
            finish()

        }

        selectDayEditButton.setOnClickListener { showDatePickerDialog() }

        hourEditSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hourEditTextView.text = "All day"
            } else {
                settime()
            }
        }

    }

    private fun settime(){
        val timePicker = TimePickerFragment{onTimeSelected(it)}
        timePicker.show(supportFragmentManager,"time")
    }
    private fun onTimeSelected(time: String) {
        hourEditTextView.text = time
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment{day, month, year -> onDateSelected(day,month,year) }
        datePicker.show(supportFragmentManager,"datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        startingDayEditTextView.setText("$day/$month/$year")
    }




    fun setdata(){
        db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup)
            .get()
            .addOnSuccessListener {
                nameEditTaskEditText.setText(it.get("nombre") as String)
                descriptionEditTaskEditText.setText(it.get("descripcion") as String)
                configuracionEditTareaEditText2.setText(it.get("configuracion") as String)
                startingDayEditTextView.setText(it.get("dia") as String)
                if (it.get("hora") as String == "All day"){
                    hourEditTextView.setText(it.get("hora") as String)
                    hourEditSwitch.isChecked = true
                }else{
                    hourEditTextView.setText(it.get("hora") as String)

                }


            }

        dataModel = ArrayList<DataModel>()

        db.collection("groups").document(idgroup)
            .get()
            .addOnSuccessListener {
                memberslist = it["participantes"] as List<String>
                db.collection("groups").document(idgroup).collection("tareas")
                    .document(task.idgroup)
                    .get()
                    .addOnSuccessListener {

                        val asignacionList = it["asignacion"] as List<String>
                        for (members in memberslist.indices!!) {
                            if (asignacionList.contains(memberslist[members])) {
                                dataModel!!.add(DataModel(memberslist[members], true))
                            } else {
                                dataModel!!.add(DataModel(memberslist[members], false))
                            }

                        }




                        edittaskmembersGroup.setItemsCanFocus(false);
                        membersadapter = MembersAdapter(dataModel!!, applicationContext)
                        edittaskmembersGroup.adapter = membersadapter

                        edittaskmembersGroup.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                            val dataModel1 : DataModel = dataModel!![position] as DataModel
                            dataModel1.checked = !dataModel1.checked
                            membersadapter.notifyDataSetChanged()
                        })
                    }
            }

    }


    private fun editarTarea(
                           nombretarea : String,
                           asignaciontarea : List<String>,
                           configuraciontarea : String,
                           descripciontarea : String,
                           diatarea: String,
                           horatarea:String?
    ){
        db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup)
            .get()
            .addOnSuccessListener {
                var estado1 = it.get("estado") as String

                class tareas (
                    val asignacion : List<String>? = asignaciontarea,
                    val configuracion: String? = configuraciontarea,
                    val descripcion: String?= descripciontarea,
                    val nombre : String = nombretarea,
                    val dia : String= diatarea,
                    val hora : String? = horatarea,
                    val estado: String? = estado1
                )


                val data = tareas()
                db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup)
                    .set(data)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            }

    }
}