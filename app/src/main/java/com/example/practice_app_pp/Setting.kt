package com.example.practice_app_pp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setTitle("Настройки")
        whatTheme(findViewById<Switch>(R.id.switch1))
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

    //Меняет тему приложения
        fun changeTheme(view: View) {
        val sw: Switch = findViewById(R.id.switch1)
        if (sw.isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun whatTheme(switch: Switch) {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                switch.text = "Изменить на темную тему"
                switch.isChecked = false
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                switch.text = "Изменить на светлую тему"
                switch.isChecked = true
            }
        }
    }
}