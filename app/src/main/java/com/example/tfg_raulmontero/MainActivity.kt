package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {
    lateinit var btnlogoff : Button
    lateinit var btncreategrp: Button
    lateinit var btnjoingrp: Button
    lateinit var joinedtxt: EditText
    lateinit var createedtxtcreate: EditText
    lateinit var recyclerview :RecyclerView
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


        initcards();
        Thread.sleep(1_000)

        btnlogoff.setOnClickListener{

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
        btnjoingrp.setOnClickListener{
            var joingrptxt = joinedtxt.text.toString()
            //joingroup("pPzid2LqMC6ESIUJ5c9m")
            joingroup(joingrptxt)
        }
        btncreategrp.setOnClickListener {
            var creategrptxt = createedtxtcreate.text.toString()
            creategroup(creategrptxt)
        }
        Toast.makeText(this, "Prueba2", Toast.LENGTH_LONG).show()
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

                class economia()
                class tareas (
                    val asignacion : List<String>? = null,
                    val configuracion: String? = null,
                        )
                val data = tareas()
                db.collection("groups").document(documentReference.id).collection("tareas")
                    .add(data)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }
    fun creartarea(documentid : String, nombretarea : String, asignaciontarea : List<String>,configuraciontarea : String ){
        class tareas (
            val asignacion : List<String>? = asignaciontarea,
            val configuracion: String? = configuraciontarea,
        )
        val data = tareas()
        db.collection("groups").document(documentid).collection("tareas").document(nombretarea)
            .set(data)
    }

    fun initcards(){

        var elementslst = mutableListOf<ListElement>()
        //ListElement("#775447", "Grupo1", "Resi Oslo", "1", "123")

        db.collection("groups").whereArrayContains("participantes", auth.currentUser?.email.toString())

            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    elementslst.add(ListElement("#775447", document.get("nombre") as String?,document.get("nombre") as String?,"1",document.id))

                    Toast.makeText(this, "Prueba", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        class Listener : ListAdapter.OnItemClickListener{
            override fun onItemClick(item: ListElement?) {
                if (item != null) {
                    moveToDescription(item)
                }
            }
        }

        var listadapter = ListAdapter(elementslst,this,Listener())
        recyclerview = findViewById(R.id.groupsRecyclerView)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = listadapter

    }
    fun moveToDescription (item: ListElement){
        Toast.makeText(this, "Hola", Toast.LENGTH_LONG).show()
    }
}