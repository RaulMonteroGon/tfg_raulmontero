package com.example.tfg_raulmontero

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GroupActivity : AppCompatActivity() {
    lateinit var groupTitleTextView : TextView
    lateinit var element :ListElement
    lateinit var taskrecyclerview : RecyclerView
    lateinit var btnCreateTask: Button
    lateinit var btndeleteGroup : Button
    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        groupTitleTextView = findViewById(R.id.groupTitleTextView)
        btnCreateTask = findViewById(R.id.createTaskButton)
        btndeleteGroup = findViewById(R.id.deletegrpBtn)

        element = intent.getSerializableExtra("GroupElement") as ListElement

        initcards();



        groupTitleTextView.text = element.getName()

        btnCreateTask.setOnClickListener{
            val createTaskIntent = Intent(this,CreateTaskActivity::class.java)
            createTaskIntent.putExtra("GroupElement", element)
            startActivity(createTaskIntent)
        }
        btndeleteGroup.setOnClickListener{
            val deletegroupIntent = Intent(this,GroupSettingsActivity::class.java)
            deletegroupIntent.putExtra("idgroup", element.getIdgroup())
            startActivity(deletegroupIntent)



            /*db.collection("groups").document(element.getIdgroup())
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            val deletegroupIntent = Intent(this,MainActivity::class.java)
            startActivity(deletegroupIntent)
            finish()*/
        }
    }



    fun initcards(){

        var elementslst = mutableListOf<ListElement>()
        //ListElement("#775447", "Grupo1", "Resi Oslo", "1", "123")

        db.collection("groups").document(element.getIdgroup()).collection("tareas")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    elementslst.add(ListElement("#775447", document.get("nombre") as String?,document.get("Descripcion") as String?,"1",document.id))

                    //Toast.makeText(this, "Prueba", Toast.LENGTH_LONG).show()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
                class Listener : ListAdapter.OnItemClickListener{
                    override fun onItemClick(item: ListElement?) {
                        if (item != null) {
                            moveToDescription(item)
                        }
                    }
                }

                var listadapter = ListAdapter(elementslst,this,Listener())
                taskrecyclerview = findViewById(R.id.taskRecyclerView)
                taskrecyclerview.setHasFixedSize(true)
                taskrecyclerview.layoutManager = LinearLayoutManager(this)
                taskrecyclerview.adapter = listadapter
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }



    }
    fun moveToDescription (item: ListElement){
        val gototaskIntent = Intent(this,TaskActivity::class.java)
        gototaskIntent.putExtra("TaskElement", item)
        gototaskIntent.putExtra("idgroup", element.getIdgroup())
        startActivity(gototaskIntent)


    }
}