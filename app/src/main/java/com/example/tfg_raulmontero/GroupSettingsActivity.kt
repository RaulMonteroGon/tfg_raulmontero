package com.example.tfg_raulmontero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.AdapterView.OnItemClickListener
import java.util.*
import kotlin.collections.ArrayList

class GroupSettingsActivity : AppCompatActivity() {

    var dataModel :ArrayList<DataModel>? =null
    lateinit var listView: ListView
    lateinit var membersadapter: MembersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_settings)


        listView = findViewById<View>(R.id.membersListView) as ListView
        dataModel = ArrayList<DataModel>()

        dataModel!!.add(DataModel("ApplePie",false))
        dataModel!!.add(DataModel("Manzana",false))
        dataModel!!.add(DataModel("Pera",false))
        dataModel!!.add(DataModel("Albaricoque",false))




        membersadapter = MembersAdapter(dataModel!!, applicationContext)
        listView.adapter = membersadapter

        listView.onItemClickListener = OnItemClickListener{_,_,position,_ ->
            val dataModel : DataModel = dataModel!![position] as DataModel
            dataModel.checked = !dataModel.checked
            membersadapter.notifyDataSetChanged()
        }

    }
}