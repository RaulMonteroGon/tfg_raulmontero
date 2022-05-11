package com.example.tfg_raulmontero

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
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
    lateinit var btndeleteGroup : Button
    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var editDescriptionEditText : EditText
    lateinit var invitationCodeTextView :TextView
    lateinit var groupNameSettingsEditText :EditText

    lateinit var idgroup :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_settings)


        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        idgroup = intent.getSerializableExtra("idgroup") as String

        listView = findViewById<View>(R.id.membersListView) as ListView
        submitBtn = findViewById(R.id.submitGroupBtn)
        btndeleteGroup = findViewById(R.id.deleteGrpBtn)
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText)
        dataModel = ArrayList<DataModel>()
        invitationCodeTextView = findViewById(R.id.invitationCodeTextView)
        groupNameSettingsEditText = findViewById(R.id.groupNameSettingsEditText)

        invitationCodeTextView.setText(idgroup)
        db.collection("groups").document(idgroup).get()
            .addOnSuccessListener {
                editDescriptionEditText.setText(it.get("descripcion") as String)
                groupNameSettingsEditText.setText(it.get("nombre") as String)
            }
        btndeleteGroup.setOnClickListener {
            db.collection("groups").document(idgroup)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

            val deletegroupIntent = Intent(this,MainActivity::class.java)
            startActivity(deletegroupIntent)
            finish()
        }
        db.collection("groups").document(idgroup)
            .get()
            .addOnSuccessListener {
                val memberslist = it["participantes"] as List<String>
                for (members in memberslist.indices!!){
                    dataModel!!.add(DataModel(memberslist[members],true))
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






        submitBtn.setOnClickListener {
            var memberschecked = mutableListOf<String>()
            //memberschecked!!.add(dataModel!![0].name.toString())

            for (i in 0 until dataModel!!.size){
                if (dataModel!![i].checked == true){
                    memberschecked!!.add(dataModel!![i].name.toString())
                }
            }
            db.collection("groups").document(idgroup).update("participantes",memberschecked )
            db.collection("groups").document(idgroup).update("descripcion",editDescriptionEditText.text.toString() )
            db.collection("groups").document(idgroup).update("nombre",groupNameSettingsEditText.text.toString() )

            val mainActivityIntent = Intent(this,MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }



    }
}