package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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

    lateinit var submitTaskBtn :Button
    lateinit var editTaskBtn :Button

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var idgroup :String
    lateinit var estado :String
    lateinit var listView: ListView

    var dataModel :ArrayList<DataModel>? =null
    lateinit var membersadapter: MembersAdapter

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
        submitTaskBtn = findViewById(R.id.submitTaskBtn)
        editTaskBtn = findViewById(R.id.editTaskBtn)

        listView = findViewById<View>(R.id.taskmembersGroup) as ListView

        task = intent.getSerializableExtra("TaskElement") as ListElement
        idgroup = intent.getSerializableExtra("idgroup") as String

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
        submitTaskBtn.setOnClickListener{
            /*db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            val deletetaskIntent = Intent(this,GroupActivity::class.java)
            startActivity(deletetaskIntent)
            finish()*/

            var memberschecked = mutableListOf<String>()
            //memberschecked!!.add(dataModel!![0].name.toString())

            for (i in 0 until dataModel!!.size){
                if (dataModel!![i].checked == true){
                    memberschecked!!.add(dataModel!![i].name.toString())
                }
            }
            db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup).update("asignacion",memberschecked )
            db.collection("groups").document(idgroup).collection("tareas").document(task.idgroup).update("estado",estado)
            /*val submitTaskIntent = Intent(this,GroupActivity::class.java)
            startActivity(submitTaskIntent)*/
            finish()

        }

        editTaskBtn.setOnClickListener {
            val editTasIntent = Intent(this,EditTaskActivity::class.java)
            editTasIntent.putExtra("TaskElement", task)
            editTasIntent.putExtra("idgroup", idgroup)
            startActivity(editTasIntent)
        }

        dataModel = ArrayList<DataModel>()

        db.collection("groups").document(idgroup)
            .get()
            .addOnSuccessListener {
                val memberslist = it["participantes"] as List<String>
                for (members in memberslist.indices!!){
                    dataModel!!.add(DataModel(memberslist[members],false))
                }




                listView.setItemsCanFocus(false);
                membersadapter = MembersAdapter(dataModel!!, applicationContext)
                listView.adapter = membersadapter


                listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->

                    val dataModel1 : DataModel = dataModel!![position] as DataModel
                    dataModel1.checked = !dataModel1.checked
                    membersadapter.notifyDataSetChanged()
                })



            }.addOnFailureListener { exception ->
                dataModel!!.add(DataModel("no funciona",false))
            }



    }

    fun setdone(){
        estado = "Finalizado"
    }
    fun setinprocess(){
        estado = "En proceso"
    }
    fun setnotdone(){
        estado = "Sin hacer"
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