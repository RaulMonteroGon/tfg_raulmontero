package com.example.tfg_raulmontero

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyTaskActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    lateinit var mytaskrecyclerview: RecyclerView
    lateinit var element: ListElement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_task)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        initcards();
        Thread.sleep(3_000)
    }


    fun initcards() {

        var elementslst = mutableListOf<ListElement>()


        db.collectionGroup("tareas").whereArrayContains("asignacion",auth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    elementslst.add(
                        ListElement(
                            "#775447",
                            document.get("nombre") as String?,
                            document.get("Descripcion") as String?,
                            "1",
                            document.id
                        )
                    )

                    //Toast.makeText(this, "Prueba", Toast.LENGTH_LONG).show()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

        class Listener : ListAdapter.OnItemClickListener{
            override fun onItemClick(item: ListElement?) {
                if (item != null) {
                    moveToDescription(item)
                }
            }
        }

        var listadapter = ListAdapter(elementslst,this,Listener())
        mytaskrecyclerview = findViewById(R.id.mytaskRecyclerView)
        mytaskrecyclerview.setHasFixedSize(true)
        mytaskrecyclerview.layoutManager = LinearLayoutManager(this)
        mytaskrecyclerview.adapter = listadapter
    }
    fun moveToDescription (item: ListElement){
        val gototaskIntent = Intent(this,TaskActivity::class.java)
        gototaskIntent.putExtra("TaskElement", item)
        startActivity(gototaskIntent)


    }
}