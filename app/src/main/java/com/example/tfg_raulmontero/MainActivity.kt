package com.example.tfg_raulmontero

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var btnlogoff : Button
    lateinit var btncreategrp: Button
    lateinit var btnjoingrp: Button

    lateinit var joinedtxt: EditText
    lateinit var createedtxtcreate: EditText

    lateinit var recyclerview :RecyclerView
    lateinit var mytaskrecyclerview: RecyclerView

    lateinit var db :FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var googleSignInOptions: GoogleSignInOptions



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnlogoff = findViewById(R.id.btnLogout)
        btncreategrp = findViewById(R.id.Creategrpbtn)
        btnjoingrp = findViewById(R.id.Joingrpbtn)


        createedtxtcreate = findViewById(R.id.Createrptxt)
        joinedtxt = findViewById(R.id.Joingrptxt)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()


        initgroups()
        inittasks()
        Thread.sleep(2_000)

        btnlogoff.setOnClickListener{
            logout()
        }
        btnjoingrp.setOnClickListener{
            var joingrptxt = joinedtxt.text.toString()
            //joingroup("pPzid2LqMC6ESIUJ5c9m")
            joingroup(joingrptxt)
        }
        btncreategrp.setOnClickListener {
            var creategrptxt = createedtxtcreate.text.toString()
            creategroup(creategrptxt)
        }


    }



    fun logout(){
        FirebaseAuth.getInstance().signOut();
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("545641554460-k37cma92ov7nhvqj664meni9nq4bop7a.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        mGoogleSignInClient.signOut()
        val logout = Intent (this, LoginActivity::class.java)
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        startActivity(logout)
        finish()
    }


    fun joingroup (iddoc : String){
        val document = db.collection("groups").document(iddoc)
        document.update("participantes",FieldValue.arrayUnion(auth.currentUser?.email.toString()))
    }
    fun creategroup (name: String){
        class grupo(
            val nombre: String = name,
            val compra: List<String>? = null,
            val participantes: List<String> = listOf(auth.currentUser?.email.toString())
        )
        val data = grupo()

        db.collection("groups")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")

                class economia()/*
                class tareas (
                    val asignacion : List<String>? = null,
                    val configuracion: String? = null,
                        )
                val data = tareas()
                db.collection("groups").document(documentReference.id).collection("tareas")
                    .add(data)*/
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }


    fun initgroups(){

        var elementslst = mutableListOf<ListElement>()
        //ListElement("#775447", "Grupo1", "Resi Oslo", "1", "123")

        db.collection("groups").whereArrayContains("participantes", auth.currentUser?.email.toString())

            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    elementslst.add(ListElement("#775447", document.get("nombre") as String?,document.get("nombre") as String?,"1",document.id))

                    //Toast.makeText(this, "Prueba", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        class Listener : ListAdapter.OnItemClickListener{
            override fun onItemClick(item: ListElement?) {
                if (item != null) {
                    moveToDescriptionGroups(item)
                }
            }
        }

        var listadapter = ListAdapter(elementslst,this,Listener())
        recyclerview = findViewById(R.id.groupsRecyclerView)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = listadapter

    }
    fun moveToDescriptionGroups (item: ListElement){
        val gotogroupIntent = Intent(this,GroupActivity::class.java)
        gotogroupIntent.putExtra("GroupElement", item)
        startActivity(gotogroupIntent)

        //Toast.makeText(this, "Hola", Toast.LENGTH_LONG).show()
    }
    fun inittasks() {

        var elementslst = mutableListOf<ListElement>()


        db.collectionGroup("tareas").whereArrayContains("asignacion",auth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Toast.makeText(this, "soy rapido", Toast.LENGTH_SHORT).show()

                    //elementslst.add(ListElement("#775447", "Grupo3", "Resi Oslo", "1", "123"))


                    elementslst.add(
                        ListElement(
                            "#775447",
                            document.get("nombre") as String?,
                            document.reference.parent.parent?.id,
                            "1",
                            document.id
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                elementslst.add(ListElement("#775447", "Grupoerror", "Resi Oslo", "1", "123"))
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }


        class Listener : ListAdapter.OnItemClickListener{
            override fun onItemClick(item: ListElement?) {
                if (item != null) {
                    moveToDescription(item)
                }
            }
        }


        var listadaptertask = ListAdapter(elementslst,this,Listener())
        mytaskrecyclerview = findViewById(R.id.mytaskRecyclerView)
        mytaskrecyclerview.setHasFixedSize(true)
        mytaskrecyclerview.layoutManager = LinearLayoutManager(this)
        mytaskrecyclerview.adapter = listadaptertask

    }
    fun moveToDescription (item: ListElement){
        val gototaskIntent = Intent(this,TaskActivity::class.java)
        gototaskIntent.putExtra("TaskElement", item)
        gototaskIntent.putExtra("idgroup", item.description)
        startActivity(gototaskIntent)


    }
}