package com.example.tfg_raulmontero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class TaskActivity : AppCompatActivity() {
    lateinit var taskTitleTextView : TextView
    lateinit var task :ListElement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        taskTitleTextView = findViewById(R.id.taskTitleTextView)

        task = intent.getSerializableExtra("TaskElement") as ListElement

        taskTitleTextView.text = task.getName()
    }


}