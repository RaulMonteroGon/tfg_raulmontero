package com.example.tfg_raulmontero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
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
    lateinit var submitBtn : Button
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
        submitBtn = findViewById(R.id.submitGroupBtn)

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



        /*dataModel!!.add(DataModel("Manzana",false))
        dataModel!!.add(DataModel("Pera",false))
        dataModel!!.add(DataModel("Albaricoque",false))*/




        submitBtn.setOnClickListener {
            var memberschecked = mutableListOf<String>()
            //memberschecked!!.add(dataModel!![0].name.toString())

            for (i in 0 until dataModel!!.size){
                if (dataModel!![i].checked == true){
                    memberschecked!!.add(dataModel!![i].name.toString())
                }
            }
            Toast.makeText(this, memberschecked[0], Toast.LENGTH_SHORT).show()
        }



    }
}