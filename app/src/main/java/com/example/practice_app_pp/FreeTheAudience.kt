package com.example.practice_app_pp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import schedule.animationLoadBtn
import schedule.sendScheduleToFirebase

class FreeTheAudience : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_the_audience)
        setTitle("Освободить аудиторию")
        animationLoadBtn(findViewById<Button>(R.id.freeBtn))
        val args : Bundle? = intent.extras
        if(args != null) {
            val numberLesson = args.getString("numberLesson")
            val numberAudience = args.getString("numberAudience")
            findViewById<TextView>(R.id.textViewTitle2).text = "Аудитория номер ${numberAudience}\nпара ${numberLesson}"
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

    fun sendChangeBtn2(view: View) {
        val args : Bundle? = intent.extras
        if(args != null) {
            val checkBox = findViewById<CheckBox>(R.id.checkBoxConfirmation)
            if(checkBox.isChecked) {
                val numberLesson = args.getString("numberLesson")
                val numberAudience = args.getString("numberAudience")
                sendScheduleToFirebase(Audience(numberAudience!!.toInt(), "-"), numberAudience!!.toInt(),numberLesson!!.toInt())
                Toast.makeText(applicationContext, "Аудитория номер ${numberAudience} освобождена!", Toast.LENGTH_SHORT).show()

                var data: Intent = Intent()
                data.putExtra("idTextView", args.getString("word")+"${numberAudience}")
                data.putExtra("numberAudience", "${numberAudience}")
                setResult(RESULT_OK, data)
                finish()

                finish()
            }
            else {
                Toast.makeText(applicationContext, "Подтвердите ваше действие!", Toast.LENGTH_LONG).show()
            }
        }
    }
}