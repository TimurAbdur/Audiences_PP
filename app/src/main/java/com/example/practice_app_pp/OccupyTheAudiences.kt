package com.example.practice_app_pp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import schedule.sendScheduleToFirebase

class OccupyTheAudiences : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_occupy_the_audiences)
        setTitle("Занять аудиторию")

        val args : Bundle? = intent.extras
        if(args != null) {
            val numberLesson = args.getString("numberLesson")
            val numberAudience = args.getString("numberAudience")
            findViewById<TextView>(R.id.textViewTitle3).text = "Аудитория номер ${numberAudience}\nпара ${numberLesson}"
        }
    }

    //Создание меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.second_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.theme -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendChangeBtn(view: View) {
        val args : Bundle? = intent.extras
        if(args != null) {
            val teacher = findViewById<EditText>(R.id.teacher)
            if(teacher.text.toString() != "") {
                val numberLesson = args.getString("numberLesson")
                val numberAudience = args.getString("numberAudience")
                sendScheduleToFirebase(Audience(numberAudience!!.toInt(), teacher.text.toString()), numberAudience!!.toInt(),numberLesson!!.toInt())
                Toast.makeText(applicationContext, "Аудитория номер ${numberAudience} успешно занята!", Toast.LENGTH_SHORT).show()

                var data: Intent = Intent()
                data.putExtra("idTextView", args.getString("word")+"${numberAudience}")
                data.putExtra("numberAudience", "${numberAudience}")
                data.putExtra("teacher", "\n${teacher.text}")
                setResult(RESULT_OK, data)
                finish()
            }
            else {
                Toast.makeText(applicationContext, "Ошибка! Вы не ввели фамилию преподавателя!", Toast.LENGTH_LONG).show()
            }
        }
    }
}