package com.example.tfg_raulmontero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class GroupSettingsActivity : AppCompatActivity() {

    var dataModel :ArrayList<DataModel>? =null
    lateinit var listView: ListView
    lateinit var membersadapter: MembersAdapter

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    lateinit var idgroup :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_settings)


        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        idgroup = intent.getSerializableExtra("idgroup") as String

        listView = findViewById<View>(R.id.membersListView) as ListView
        dataModel = ArrayList<DataModel>()

        db.collection("groups").document(idgroup)
            .get()
            .addOnSuccessListener {
                val memberslist = it["participantes"] as ArrayList<String>?
                /*for (members in memberslist!!){
                    dataModel!!.add(DataModel(members,false))
                }*/
                dataModel!!.add(DataModel("hola",false))
                Toast.makeText(this, memberslist?.get(0) ?: String(), Toast.LENGTH_SHORT).show()



                membersadapter = MembersAdapter(dataModel!!, applicationContext)
                listView.adapter = membersadapter


            }.addOnFailureListener { exception ->
                dataModel!!.add(DataModel("no funciona",false))
            }


        dataModel!!.add(DataModel("ApplePie",false))
        /*dataModel!!.add(DataModel("Manzana",false))
        dataModel!!.add(DataModel("Pera",false))
        dataModel!!.add(DataModel("Albaricoque",false))*/






        listView.onItemClickListener = OnItemClickListener{_,_,position,_ ->
            val dataModel : DataModel = dataModel!![position] as DataModel
            dataModel.checked = !dataModel.checked
            membersadapter.notifyDataSetChanged()
        }

    }
}