package com.example.practice_app_pp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import schedule.animationLoadBtn
import schedule.animationTitle
import schedule.sendScheduleToFirebase

class OccupyTheAudiences : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_occupy_the_audiences)
        setTitle("Занять аудиторию")
        animationLoadBtn(findViewById<Button>(R.id.occupBtn))
        animationTitle(findViewById<TextView>(R.id.textViewTitle3))
        val args : Bundle? = intent.extras
        if(args != null) {
            val numberLesson = args.getString("numberLesson")
            val numberAudience = args.getString("numberAudience")
            findViewById<TextView>(R.id.textViewTitle3).text = "Аудитория номер ${numberAudience}\nпара ${numberLesson}"
        }
    }
    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
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
        if(isInternetConnected(this)) {
            val args : Bundle? = intent.extras
            if(args != null) {
                val teacher = findViewById<EditText>(R.id.teacher)
                if(teacher.text.toString() != "") {
                    val containsNumbers = teacher.text.any { it.isDigit() }
                    if (!containsNumbers) {
                        if(teacher.text.length >= 3) {
                            val firstChar = teacher.text.firstOrNull()
                            if(firstChar!!.isUpperCase()) {
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
                                Toast.makeText(applicationContext, "Фамилия преподавателя должна начинаться с заглавной буквы!", Toast.LENGTH_LONG).show()
                            }
                        }
                        else {
                            Toast.makeText(applicationContext, "Фамилия преподавателя должна состоят минимум из 3 символов!", Toast.LENGTH_LONG).show()
                        }
                    }
                    else {
                        Toast.makeText(applicationContext, "Фамилия преподавателя не может содержать цифры!", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(applicationContext, "Ошибка! Вы не ввели фамилию преподавателя!", Toast.LENGTH_LONG).show()
                }
            }
        }
        else {
            Toast.makeText(applicationContext, "Отсутствует подключение к интернету! Невозможно занять аудиторию!", Toast.LENGTH_SHORT).show()
        }
    }
}