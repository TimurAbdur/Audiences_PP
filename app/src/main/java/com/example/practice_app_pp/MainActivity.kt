package com.example.practice_app_pp

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalTime
import schedule.scheduling
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import schedule.animationBtn
import schedule.animationLoadBtn
import schedule.animationTitle
import java.lang.Exception
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Practice_app_PP)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        whichTheme(findViewById<ConstraintLayout>(R.id.constraintLayout))
        setTitle("Главное меню")
        checkInternetConnect(findViewById(R.id.loadSchedule))
    }
    var checkConnect : Int = 0
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkInternetConnect(view: View) {
        val loadBtn : Button = view as Button
        animationLoadBtn(findViewById<Button>(R.id.loadSchedule))
        if(isInternetConnected(this)) {
            if(checkConnect > 0) {
                Toast.makeText(applicationContext, "Подключение к интернету восстановлено!",Toast.LENGTH_SHORT).show()
            }
            findViewById<TextView>(R.id.textViewOnMain).text = getString(R.string.desc)
            loadBtn.text = "Загрузить расписание"
            loadBtn.setOnClickListener(::getAudienceFromTextView)
            //Отображает списки на каждую пару
            writeShelderAtTextView()
            //Отображает пару и список в зависимости от времени
            showListByTime()
        }
        else {
            if(checkConnect > 0) {
                Toast.makeText(applicationContext, "Подключение к интернету всё ещё отсутствует!",Toast.LENGTH_SHORT).show()
            }
            checkConnect++

            loadBtn.text = "Проверить подключение"
            loadBtn.setOnClickListener(::checkInternetConnect)
            findViewById<TextView>(R.id.textViewOnMain).text = "К сожалению, отсутствует подключение к интернету. Пожалуйста, проверьте свое подключение и перезайдите в приложение."
        }
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun whichTheme(cons : ConstraintLayout) {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                cons.setBackgroundColor(getColor(R.color.md_theme_light_background))
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                cons.setBackgroundColor(getColor(R.color.md_theme_dark_background))
            }
        }
    }

    //Создание меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.settings_menu_item -> {
                val intent : Intent = Intent(this, Setting::class.java)
                startActivity(intent)
            }
            R.id.about_menu_item -> {
                val intent : Intent = Intent(this, About::class.java)
                startActivity(intent)
            }
            R.id.reference_menu_item -> {
                val intent : Intent = Intent(this, Reference::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var lessonNum1 : Array<Audience>
    lateinit var lessonNum2 : Array<Audience>
    lateinit var lessonNum3 : Array<Audience>
    lateinit var lessonNum4 : Array<Audience>
    lateinit var lessonNum5 : Array<Audience>
    lateinit var lessonNum6 : Array<Audience>
    val database = FirebaseDatabase.getInstance()
    val reference = database.getReference("schedule")
    var numOfTable : Int = 0

    //Функция записывает во все textView информацию о конкретной аудитории
    fun writeShelderAtTextView() {
        //Аудитрия 11
        writeToTextViewUncorrectFormatAudiece(1,11,findViewById(R.id.a11))
        writeToTextViewUncorrectFormatAudiece(2,11,findViewById(R.id.b11))
        writeToTextViewUncorrectFormatAudiece(3,11,findViewById(R.id.c11))
        writeToTextViewUncorrectFormatAudiece(4,11,findViewById(R.id.d11))
        writeToTextViewUncorrectFormatAudiece(5,11,findViewById(R.id.e11))
        writeToTextViewUncorrectFormatAudiece(6,11,findViewById(R.id.f11))
        //Аудитрия 12
        writeToTextViewUncorrectFormatAudiece(1,12,findViewById(R.id.a12))
        writeToTextViewUncorrectFormatAudiece(2,12,findViewById(R.id.b12))
        writeToTextViewUncorrectFormatAudiece(3,12,findViewById(R.id.c12))
        writeToTextViewUncorrectFormatAudiece(4,12,findViewById(R.id.d12))
        writeToTextViewUncorrectFormatAudiece(5,12,findViewById(R.id.e12))
        writeToTextViewUncorrectFormatAudiece(6,12,findViewById(R.id.f12))
        //Аудитрия 13
        writeToTextViewUncorrectFormatAudiece(1,13,findViewById(R.id.a13))
        writeToTextViewUncorrectFormatAudiece(2,13,findViewById(R.id.b13))
        writeToTextViewUncorrectFormatAudiece(3,13,findViewById(R.id.c13))
        writeToTextViewUncorrectFormatAudiece(4,13,findViewById(R.id.d13))
        writeToTextViewUncorrectFormatAudiece(5,13,findViewById(R.id.e13))
        writeToTextViewUncorrectFormatAudiece(6,13,findViewById(R.id.f13))
        //Аудитрия 14
        writeToTextViewUncorrectFormatAudiece(1,14,findViewById(R.id.a14))
        writeToTextViewUncorrectFormatAudiece(2,14,findViewById(R.id.b14))
        writeToTextViewUncorrectFormatAudiece(3,14,findViewById(R.id.c14))
        writeToTextViewUncorrectFormatAudiece(4,14,findViewById(R.id.d14))
        writeToTextViewUncorrectFormatAudiece(5,14,findViewById(R.id.e14))
        writeToTextViewUncorrectFormatAudiece(6,14,findViewById(R.id.f14))
        //Аудитрия 15
        writeToTextViewUncorrectFormatAudiece(1,15,findViewById(R.id.a15))
        writeToTextViewUncorrectFormatAudiece(2,15,findViewById(R.id.b15))
        writeToTextViewUncorrectFormatAudiece(3,15,findViewById(R.id.c15))
        writeToTextViewUncorrectFormatAudiece(4,15,findViewById(R.id.d15))
        writeToTextViewUncorrectFormatAudiece(5,15,findViewById(R.id.e15))
        writeToTextViewUncorrectFormatAudiece(6,15,findViewById(R.id.f15))
        //Аудитрия 16
        writeToTextViewUncorrectFormatAudiece(1,16,findViewById(R.id.a16))
        writeToTextViewUncorrectFormatAudiece(2,16,findViewById(R.id.b16))
        writeToTextViewUncorrectFormatAudiece(3,16,findViewById(R.id.c16))
        writeToTextViewUncorrectFormatAudiece(4,16,findViewById(R.id.d16))
        writeToTextViewUncorrectFormatAudiece(5,16,findViewById(R.id.e16))
        writeToTextViewUncorrectFormatAudiece(6,16,findViewById(R.id.f16))
        //Аудитрия 21
        writeToTextViewUncorrectFormatAudiece(1,21,findViewById(R.id.a21))
        writeToTextViewUncorrectFormatAudiece(2,21,findViewById(R.id.b21))
        writeToTextViewUncorrectFormatAudiece(3,21,findViewById(R.id.c21))
        writeToTextViewUncorrectFormatAudiece(4,21,findViewById(R.id.d21))
        writeToTextViewUncorrectFormatAudiece(5,21,findViewById(R.id.e21))
        writeToTextViewUncorrectFormatAudiece(6,21,findViewById(R.id.f21))
        //Аудитрия 22
        writeToTextViewUncorrectFormatAudiece(1,22,findViewById(R.id.a22))
        writeToTextViewUncorrectFormatAudiece(2,22,findViewById(R.id.b22))
        writeToTextViewUncorrectFormatAudiece(3,22,findViewById(R.id.c22))
        writeToTextViewUncorrectFormatAudiece(4,22,findViewById(R.id.d22))
        writeToTextViewUncorrectFormatAudiece(5,22,findViewById(R.id.e22))
        writeToTextViewUncorrectFormatAudiece(6,22,findViewById(R.id.f22))
        //Аудитрия 23
        writeToTextViewUncorrectFormatAudiece(1,23,findViewById(R.id.a23))
        writeToTextViewUncorrectFormatAudiece(2,23,findViewById(R.id.b23))
        writeToTextViewUncorrectFormatAudiece(3,23,findViewById(R.id.c23))
        writeToTextViewUncorrectFormatAudiece(4,23,findViewById(R.id.d23))
        writeToTextViewUncorrectFormatAudiece(5,23,findViewById(R.id.e23))
        writeToTextViewUncorrectFormatAudiece(6,23,findViewById(R.id.f23))
        //Аудитрия 24
        writeToTextViewUncorrectFormatAudiece(1,24,findViewById(R.id.a24))
        writeToTextViewUncorrectFormatAudiece(2,24,findViewById(R.id.b24))
        writeToTextViewUncorrectFormatAudiece(3,24,findViewById(R.id.c24))
        writeToTextViewUncorrectFormatAudiece(4,24,findViewById(R.id.d24))
        writeToTextViewUncorrectFormatAudiece(5,24,findViewById(R.id.e24))
        writeToTextViewUncorrectFormatAudiece(6,24,findViewById(R.id.f24))
        //Аудитрия 25
        writeToTextViewUncorrectFormatAudiece(1,25,findViewById(R.id.a25))
        writeToTextViewUncorrectFormatAudiece(2,25,findViewById(R.id.b25))
        writeToTextViewUncorrectFormatAudiece(3,25,findViewById(R.id.c25))
        writeToTextViewUncorrectFormatAudiece(4,25,findViewById(R.id.d25))
        writeToTextViewUncorrectFormatAudiece(5,25,findViewById(R.id.e25))
        writeToTextViewUncorrectFormatAudiece(6,25,findViewById(R.id.f25))
        //Аудитрия 26
        writeToTextViewUncorrectFormatAudiece(1,26,findViewById(R.id.a26))
        writeToTextViewUncorrectFormatAudiece(2,26,findViewById(R.id.b26))
        writeToTextViewUncorrectFormatAudiece(3,26,findViewById(R.id.c26))
        writeToTextViewUncorrectFormatAudiece(4,26,findViewById(R.id.d26))
        writeToTextViewUncorrectFormatAudiece(5,26,findViewById(R.id.e26))
        writeToTextViewUncorrectFormatAudiece(6,26,findViewById(R.id.f26))
        //Аудитрия 31
        writeToTextViewUncorrectFormatAudiece(1,31,findViewById(R.id.a31))
        writeToTextViewUncorrectFormatAudiece(2,31,findViewById(R.id.b31))
        writeToTextViewUncorrectFormatAudiece(3,31,findViewById(R.id.c31))
        writeToTextViewUncorrectFormatAudiece(4,31,findViewById(R.id.d31))
        writeToTextViewUncorrectFormatAudiece(5,31,findViewById(R.id.e31))
        writeToTextViewUncorrectFormatAudiece(6,31,findViewById(R.id.f31))
        //Аудитрия 32
        writeToTextViewUncorrectFormatAudiece(1,32,findViewById(R.id.a32))
        writeToTextViewUncorrectFormatAudiece(2,32,findViewById(R.id.b32))
        writeToTextViewUncorrectFormatAudiece(3,32,findViewById(R.id.c32))
        writeToTextViewUncorrectFormatAudiece(4,32,findViewById(R.id.d32))
        writeToTextViewUncorrectFormatAudiece(5,32,findViewById(R.id.e32))
        writeToTextViewUncorrectFormatAudiece(6,32,findViewById(R.id.f32))
        //Аудитрия 33
        writeToTextViewUncorrectFormatAudiece(1,33,findViewById(R.id.a33))
        writeToTextViewUncorrectFormatAudiece(2,33,findViewById(R.id.b33))
        writeToTextViewUncorrectFormatAudiece(3,33,findViewById(R.id.c33))
        writeToTextViewUncorrectFormatAudiece(4,33,findViewById(R.id.d33))
        writeToTextViewUncorrectFormatAudiece(5,33,findViewById(R.id.e33))
        writeToTextViewUncorrectFormatAudiece(6,33,findViewById(R.id.f33))
        //Аудитрия 34
        writeToTextViewUncorrectFormatAudiece(1,34,findViewById(R.id.a34))
        writeToTextViewUncorrectFormatAudiece(2,34,findViewById(R.id.b34))
        writeToTextViewUncorrectFormatAudiece(3,34,findViewById(R.id.c34))
        writeToTextViewUncorrectFormatAudiece(4,34,findViewById(R.id.d34))
        writeToTextViewUncorrectFormatAudiece(5,34,findViewById(R.id.e34))
        writeToTextViewUncorrectFormatAudiece(6,34,findViewById(R.id.f34))
        //Аудитрия 35
        writeToTextViewUncorrectFormatAudiece(1,35,findViewById(R.id.a35))
        writeToTextViewUncorrectFormatAudiece(2,35,findViewById(R.id.b35))
        writeToTextViewUncorrectFormatAudiece(3,35,findViewById(R.id.c35))
        writeToTextViewUncorrectFormatAudiece(4,35,findViewById(R.id.d35))
        writeToTextViewUncorrectFormatAudiece(5,35,findViewById(R.id.e35))
        writeToTextViewUncorrectFormatAudiece(6,35,findViewById(R.id.f35))
        //Аудитрия 36
        writeToTextViewUncorrectFormatAudiece(1,36,findViewById(R.id.a36))
        writeToTextViewUncorrectFormatAudiece(2,36,findViewById(R.id.b36))
        writeToTextViewUncorrectFormatAudiece(3,36,findViewById(R.id.c36))
        writeToTextViewUncorrectFormatAudiece(4,36,findViewById(R.id.d36))
        writeToTextViewUncorrectFormatAudiece(5,36,findViewById(R.id.e36))
        writeToTextViewUncorrectFormatAudiece(6,36,findViewById(R.id.f36))
        //Аудитрия 41
        writeToTextViewUncorrectFormatAudiece(1,41,findViewById(R.id.a41))
        writeToTextViewUncorrectFormatAudiece(2,41,findViewById(R.id.b41))
        writeToTextViewUncorrectFormatAudiece(3,41,findViewById(R.id.c41))
        writeToTextViewUncorrectFormatAudiece(4,41,findViewById(R.id.d41))
        writeToTextViewUncorrectFormatAudiece(5,41,findViewById(R.id.e41))
        writeToTextViewUncorrectFormatAudiece(6,41,findViewById(R.id.f41))
        //Аудитрия 42
        writeToTextViewUncorrectFormatAudiece(1,42,findViewById(R.id.a42))
        writeToTextViewUncorrectFormatAudiece(2,42,findViewById(R.id.b42))
        writeToTextViewUncorrectFormatAudiece(3,42,findViewById(R.id.c42))
        writeToTextViewUncorrectFormatAudiece(4,42,findViewById(R.id.d42))
        writeToTextViewUncorrectFormatAudiece(5,42,findViewById(R.id.e42))
        writeToTextViewUncorrectFormatAudiece(6,42,findViewById(R.id.f42))
        //Аудитрия 43
        writeToTextViewUncorrectFormatAudiece(1,43,findViewById(R.id.a43))
        writeToTextViewUncorrectFormatAudiece(2,43,findViewById(R.id.b43))
        writeToTextViewUncorrectFormatAudiece(3,43,findViewById(R.id.c43))
        writeToTextViewUncorrectFormatAudiece(4,43,findViewById(R.id.d43))
        writeToTextViewUncorrectFormatAudiece(5,43,findViewById(R.id.e43))
        writeToTextViewUncorrectFormatAudiece(6,43,findViewById(R.id.f43))
        //Аудитрия 44
        writeToTextViewUncorrectFormatAudiece(1,44,findViewById(R.id.a44))
        writeToTextViewUncorrectFormatAudiece(2,44,findViewById(R.id.b44))
        writeToTextViewUncorrectFormatAudiece(3,44,findViewById(R.id.c44))
        writeToTextViewUncorrectFormatAudiece(4,44,findViewById(R.id.d44))
        writeToTextViewUncorrectFormatAudiece(5,44,findViewById(R.id.e44))
        writeToTextViewUncorrectFormatAudiece(6,44,findViewById(R.id.f44))
        //Аудитрия 45
        writeToTextViewUncorrectFormatAudiece(1,45,findViewById(R.id.a45))
        writeToTextViewUncorrectFormatAudiece(2,45,findViewById(R.id.b45))
        writeToTextViewUncorrectFormatAudiece(3,45,findViewById(R.id.c45))
        writeToTextViewUncorrectFormatAudiece(4,45,findViewById(R.id.d45))
        writeToTextViewUncorrectFormatAudiece(5,45,findViewById(R.id.e45))
        writeToTextViewUncorrectFormatAudiece(6,45,findViewById(R.id.f45))
        //Аудитрия 46
        writeToTextViewUncorrectFormatAudiece(1,46,findViewById(R.id.a46))
        writeToTextViewUncorrectFormatAudiece(2,46,findViewById(R.id.b46))
        writeToTextViewUncorrectFormatAudiece(3,46,findViewById(R.id.c46))
        writeToTextViewUncorrectFormatAudiece(4,46,findViewById(R.id.d46))
        writeToTextViewUncorrectFormatAudiece(5,46,findViewById(R.id.e46))
        writeToTextViewUncorrectFormatAudiece(6,46,findViewById(R.id.f46))
    }
    fun writeToTextViewUncorrectFormatAudiece(numberLesson: Int, id : Int, textView : TextView) {
        reference.child(numberLesson.toString()).child(id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val aud = dataSnapshot.getValue(Audience::class.java)
                textView.text = "${aud!!.numberAudience.toString()}|${aud!!.teacher}"
            }
            override fun onCancelled(databaseError: DatabaseError) {
                textView.text = "0|-"
            }
        })
    }

    //Функция в которой много раз вызывается функция getAudiendeFromTextView для всех аудиторий, и составляются массива с номером пары
    fun getAudienceFromTextView(view: View) {
        try {
            //Пара 1
            val audienceA11 : Audience = getAudiendeFromTextView(findViewById(R.id.a11)) //Аудитория 11
            val audienceA12 : Audience = getAudiendeFromTextView(findViewById(R.id.a12)) //Аудитория 12
            val audienceA13 : Audience = getAudiendeFromTextView(findViewById(R.id.a13)) //Аудитория 13
            val audienceA14 : Audience = getAudiendeFromTextView(findViewById(R.id.a14)) //Аудитория 14
            val audienceA15 : Audience = getAudiendeFromTextView(findViewById(R.id.a15)) //Аудитория 15
            val audienceA16 : Audience = getAudiendeFromTextView(findViewById(R.id.a16)) //Аудитория 16
            val audienceA21 : Audience = getAudiendeFromTextView(findViewById(R.id.a21)) //Аудитория 21
            val audienceA22 : Audience = getAudiendeFromTextView(findViewById(R.id.a22)) //Аудитория 22
            val audienceA23 : Audience = getAudiendeFromTextView(findViewById(R.id.a23)) //Аудитория 23
            val audienceA24 : Audience = getAudiendeFromTextView(findViewById(R.id.a24)) //Аудитория 24
            val audienceA25 : Audience = getAudiendeFromTextView(findViewById(R.id.a25)) //Аудитория 25
            val audienceA26 : Audience = getAudiendeFromTextView(findViewById(R.id.a26)) //Аудитория 26
            val audienceA31 : Audience = getAudiendeFromTextView(findViewById(R.id.a31)) //Аудитория 31
            val audienceA32 : Audience = getAudiendeFromTextView(findViewById(R.id.a32)) //Аудитория 32
            val audienceA33 : Audience = getAudiendeFromTextView(findViewById(R.id.a33)) //Аудитория 33
            val audienceA34 : Audience = getAudiendeFromTextView(findViewById(R.id.a34)) //Аудитория 34
            val audienceA35 : Audience = getAudiendeFromTextView(findViewById(R.id.a35)) //Аудитория 35
            val audienceA36 : Audience = getAudiendeFromTextView(findViewById(R.id.a36)) //Аудитория 36
            val audienceA41 : Audience = getAudiendeFromTextView(findViewById(R.id.a41)) //Аудитория 41
            val audienceA42 : Audience = getAudiendeFromTextView(findViewById(R.id.a42)) //Аудитория 42
            val audienceA43 : Audience = getAudiendeFromTextView(findViewById(R.id.a43)) //Аудитория 43
            val audienceA44 : Audience = getAudiendeFromTextView(findViewById(R.id.a44)) //Аудитория 44
            val audienceA45 : Audience = getAudiendeFromTextView(findViewById(R.id.a45)) //Аудитория 45
            val audienceA46 : Audience = getAudiendeFromTextView(findViewById(R.id.a46)) //Аудитория 46
            //Пара номер 2
            val audienceB11 : Audience = getAudiendeFromTextView(findViewById(R.id.b11)) //Аудитория 11
            val audienceB12 : Audience = getAudiendeFromTextView(findViewById(R.id.b12)) //Аудитория 12
            val audienceB13 : Audience = getAudiendeFromTextView(findViewById(R.id.b13)) //Аудитория 13
            val audienceB14 : Audience = getAudiendeFromTextView(findViewById(R.id.b14)) //Аудитория 14
            val audienceB15 : Audience = getAudiendeFromTextView(findViewById(R.id.b15)) //Аудитория 15
            val audienceB16 : Audience = getAudiendeFromTextView(findViewById(R.id.b16)) //Аудитория 16
            val audienceB21 : Audience = getAudiendeFromTextView(findViewById(R.id.b21)) //Аудитория 21
            val audienceB22 : Audience = getAudiendeFromTextView(findViewById(R.id.b22)) //Аудитория 22
            val audienceB23 : Audience = getAudiendeFromTextView(findViewById(R.id.b23)) //Аудитория 23
            val audienceB24 : Audience = getAudiendeFromTextView(findViewById(R.id.b24)) //Аудитория 24
            val audienceB25 : Audience = getAudiendeFromTextView(findViewById(R.id.b25)) //Аудитория 25
            val audienceB26 : Audience = getAudiendeFromTextView(findViewById(R.id.b26)) //Аудитория 26
            val audienceB31 : Audience = getAudiendeFromTextView(findViewById(R.id.b31)) //Аудитория 31
            val audienceB32 : Audience = getAudiendeFromTextView(findViewById(R.id.b32)) //Аудитория 32
            val audienceB33 : Audience = getAudiendeFromTextView(findViewById(R.id.b33)) //Аудитория 33
            val audienceB34 : Audience = getAudiendeFromTextView(findViewById(R.id.b34)) //Аудитория 34
            val audienceB35 : Audience = getAudiendeFromTextView(findViewById(R.id.b35)) //Аудитория 35
            val audienceB36 : Audience = getAudiendeFromTextView(findViewById(R.id.b36)) //Аудитория 36
            val audienceB41 : Audience = getAudiendeFromTextView(findViewById(R.id.b41)) //Аудитория 41
            val audienceB42 : Audience = getAudiendeFromTextView(findViewById(R.id.b42)) //Аудитория 42
            val audienceB43 : Audience = getAudiendeFromTextView(findViewById(R.id.b43)) //Аудитория 43
            val audienceB44 : Audience = getAudiendeFromTextView(findViewById(R.id.b44)) //Аудитория 44
            val audienceB45 : Audience = getAudiendeFromTextView(findViewById(R.id.b45)) //Аудитория 45
            val audienceB46 : Audience = getAudiendeFromTextView(findViewById(R.id.b46)) //Аудитория 46
            //Пара номер 3
            val audienceC11 : Audience = getAudiendeFromTextView(findViewById(R.id.c11)) //Аудитория 11
            val audienceC12 : Audience = getAudiendeFromTextView(findViewById(R.id.c12)) //Аудитория 12
            val audienceC13 : Audience = getAudiendeFromTextView(findViewById(R.id.c13)) //Аудитория 13
            val audienceC14 : Audience = getAudiendeFromTextView(findViewById(R.id.c14)) //Аудитория 14
            val audienceC15 : Audience = getAudiendeFromTextView(findViewById(R.id.c15)) //Аудитория 15
            val audienceC16 : Audience = getAudiendeFromTextView(findViewById(R.id.c16)) //Аудитория 16
            val audienceC21 : Audience = getAudiendeFromTextView(findViewById(R.id.c21)) //Аудитория 21
            val audienceC22 : Audience = getAudiendeFromTextView(findViewById(R.id.c22)) //Аудитория 22
            val audienceC23 : Audience = getAudiendeFromTextView(findViewById(R.id.c23)) //Аудитория 23
            val audienceC24 : Audience = getAudiendeFromTextView(findViewById(R.id.c24)) //Аудитория 24
            val audienceC25 : Audience = getAudiendeFromTextView(findViewById(R.id.c25)) //Аудитория 25
            val audienceC26 : Audience = getAudiendeFromTextView(findViewById(R.id.c26)) //Аудитория 26
            val audienceC31 : Audience = getAudiendeFromTextView(findViewById(R.id.c31)) //Аудитория 31
            val audienceC32 : Audience = getAudiendeFromTextView(findViewById(R.id.c32)) //Аудитория 32
            val audienceC33 : Audience = getAudiendeFromTextView(findViewById(R.id.c33)) //Аудитория 33
            val audienceC34 : Audience = getAudiendeFromTextView(findViewById(R.id.c34)) //Аудитория 34
            val audienceC35 : Audience = getAudiendeFromTextView(findViewById(R.id.c35)) //Аудитория 35
            val audienceC36 : Audience = getAudiendeFromTextView(findViewById(R.id.c36)) //Аудитория 36
            val audienceC41 : Audience = getAudiendeFromTextView(findViewById(R.id.c41)) //Аудитория 41
            val audienceC42 : Audience = getAudiendeFromTextView(findViewById(R.id.c42)) //Аудитория 42
            val audienceC43 : Audience = getAudiendeFromTextView(findViewById(R.id.c43)) //Аудитория 43
            val audienceC44 : Audience = getAudiendeFromTextView(findViewById(R.id.c44)) //Аудитория 44
            val audienceC45 : Audience = getAudiendeFromTextView(findViewById(R.id.c45)) //Аудитория 45
            val audienceC46 : Audience = getAudiendeFromTextView(findViewById(R.id.c46)) //Аудитория 46
            //Пара номер 4
            val audienceD11 : Audience = getAudiendeFromTextView(findViewById(R.id.d11)) //Аудитория 11
            val audienceD12 : Audience = getAudiendeFromTextView(findViewById(R.id.d12)) //Аудитория 12
            val audienceD13 : Audience = getAudiendeFromTextView(findViewById(R.id.d13)) //Аудитория 13
            val audienceD14 : Audience = getAudiendeFromTextView(findViewById(R.id.d14)) //Аудитория 14
            val audienceD15 : Audience = getAudiendeFromTextView(findViewById(R.id.d15)) //Аудитория 15
            val audienceD16 : Audience = getAudiendeFromTextView(findViewById(R.id.d16)) //Аудитория 16
            val audienceD21 : Audience = getAudiendeFromTextView(findViewById(R.id.d21)) //Аудитория 21
            val audienceD22 : Audience = getAudiendeFromTextView(findViewById(R.id.d22)) //Аудитория 22
            val audienceD23 : Audience = getAudiendeFromTextView(findViewById(R.id.d23)) //Аудитория 23
            val audienceD24 : Audience = getAudiendeFromTextView(findViewById(R.id.d24)) //Аудитория 24
            val audienceD25 : Audience = getAudiendeFromTextView(findViewById(R.id.d25)) //Аудитория 25
            val audienceD26 : Audience = getAudiendeFromTextView(findViewById(R.id.d26)) //Аудитория 26
            val audienceD31 : Audience = getAudiendeFromTextView(findViewById(R.id.d31)) //Аудитория 31
            val audienceD32 : Audience = getAudiendeFromTextView(findViewById(R.id.d32)) //Аудитория 32
            val audienceD33 : Audience = getAudiendeFromTextView(findViewById(R.id.d33)) //Аудитория 33
            val audienceD34 : Audience = getAudiendeFromTextView(findViewById(R.id.d34)) //Аудитория 34
            val audienceD35 : Audience = getAudiendeFromTextView(findViewById(R.id.d35)) //Аудитория 35
            val audienceD36 : Audience = getAudiendeFromTextView(findViewById(R.id.d36)) //Аудитория 36
            val audienceD41 : Audience = getAudiendeFromTextView(findViewById(R.id.d41)) //Аудитория 41
            val audienceD42 : Audience = getAudiendeFromTextView(findViewById(R.id.d42)) //Аудитория 42
            val audienceD43 : Audience = getAudiendeFromTextView(findViewById(R.id.d43)) //Аудитория 43
            val audienceD44 : Audience = getAudiendeFromTextView(findViewById(R.id.d44)) //Аудитория 44
            val audienceD45 : Audience = getAudiendeFromTextView(findViewById(R.id.d45)) //Аудитория 45
            val audienceD46 : Audience = getAudiendeFromTextView(findViewById(R.id.d46)) //Аудитория 46
            //Пара номер 5
            val audienceE11 : Audience = getAudiendeFromTextView(findViewById(R.id.e11)) //Аудитория 11
            val audienceE12 : Audience = getAudiendeFromTextView(findViewById(R.id.e12)) //Аудитория 12
            val audienceE13 : Audience = getAudiendeFromTextView(findViewById(R.id.e13)) //Аудитория 13
            val audienceE14 : Audience = getAudiendeFromTextView(findViewById(R.id.e14)) //Аудитория 14
            val audienceE15 : Audience = getAudiendeFromTextView(findViewById(R.id.e15)) //Аудитория 15
            val audienceE16 : Audience = getAudiendeFromTextView(findViewById(R.id.e16)) //Аудитория 16
            val audienceE21 : Audience = getAudiendeFromTextView(findViewById(R.id.e21)) //Аудитория 21
            val audienceE22 : Audience = getAudiendeFromTextView(findViewById(R.id.e22)) //Аудитория 22
            val audienceE23 : Audience = getAudiendeFromTextView(findViewById(R.id.e23)) //Аудитория 23
            val audienceE24 : Audience = getAudiendeFromTextView(findViewById(R.id.e24)) //Аудитория 24
            val audienceE25 : Audience = getAudiendeFromTextView(findViewById(R.id.e25)) //Аудитория 25
            val audienceE26 : Audience = getAudiendeFromTextView(findViewById(R.id.e26)) //Аудитория 26
            val audienceE31 : Audience = getAudiendeFromTextView(findViewById(R.id.e31)) //Аудитория 31
            val audienceE32 : Audience = getAudiendeFromTextView(findViewById(R.id.e32)) //Аудитория 32
            val audienceE33 : Audience = getAudiendeFromTextView(findViewById(R.id.e33)) //Аудитория 33
            val audienceE34 : Audience = getAudiendeFromTextView(findViewById(R.id.e34)) //Аудитория 34
            val audienceE35 : Audience = getAudiendeFromTextView(findViewById(R.id.e35)) //Аудитория 35
            val audienceE36 : Audience = getAudiendeFromTextView(findViewById(R.id.e36)) //Аудитория 36
            val audienceE41 : Audience = getAudiendeFromTextView(findViewById(R.id.e41)) //Аудитория 41
            val audienceE42 : Audience = getAudiendeFromTextView(findViewById(R.id.e42)) //Аудитория 42
            val audienceE43 : Audience = getAudiendeFromTextView(findViewById(R.id.e43)) //Аудитория 43
            val audienceE44 : Audience = getAudiendeFromTextView(findViewById(R.id.e44)) //Аудитория 44
            val audienceE45 : Audience = getAudiendeFromTextView(findViewById(R.id.e45)) //Аудитория 45
            val audienceE46 : Audience = getAudiendeFromTextView(findViewById(R.id.e46)) //Аудитория 46
            //Пара номер 5
            val audienceF11 : Audience = getAudiendeFromTextView(findViewById(R.id.f11)) //Аудитория 11
            val audienceF12 : Audience = getAudiendeFromTextView(findViewById(R.id.f12)) //Аудитория 12
            val audienceF13 : Audience = getAudiendeFromTextView(findViewById(R.id.f13)) //Аудитория 13
            val audienceF14 : Audience = getAudiendeFromTextView(findViewById(R.id.f14)) //Аудитория 14
            val audienceF15 : Audience = getAudiendeFromTextView(findViewById(R.id.f15)) //Аудитория 15
            val audienceF16 : Audience = getAudiendeFromTextView(findViewById(R.id.f16)) //Аудитория 16
            val audienceF21 : Audience = getAudiendeFromTextView(findViewById(R.id.f21)) //Аудитория 21
            val audienceF22 : Audience = getAudiendeFromTextView(findViewById(R.id.f22)) //Аудитория 22
            val audienceF23 : Audience = getAudiendeFromTextView(findViewById(R.id.f23)) //Аудитория 23
            val audienceF24 : Audience = getAudiendeFromTextView(findViewById(R.id.f24)) //Аудитория 24
            val audienceF25 : Audience = getAudiendeFromTextView(findViewById(R.id.f25)) //Аудитория 25
            val audienceF26 : Audience = getAudiendeFromTextView(findViewById(R.id.f26)) //Аудитория 26
            val audienceF31 : Audience = getAudiendeFromTextView(findViewById(R.id.f31)) //Аудитория 31
            val audienceF32 : Audience = getAudiendeFromTextView(findViewById(R.id.f32)) //Аудитория 32
            val audienceF33 : Audience = getAudiendeFromTextView(findViewById(R.id.f33)) //Аудитория 33
            val audienceF34 : Audience = getAudiendeFromTextView(findViewById(R.id.f34)) //Аудитория 34
            val audienceF35 : Audience = getAudiendeFromTextView(findViewById(R.id.f35)) //Аудитория 35
            val audienceF36 : Audience = getAudiendeFromTextView(findViewById(R.id.f36)) //Аудитория 36
            val audienceF41 : Audience = getAudiendeFromTextView(findViewById(R.id.f41)) //Аудитория 41
            val audienceF42 : Audience = getAudiendeFromTextView(findViewById(R.id.f42)) //Аудитория 42
            val audienceF43 : Audience = getAudiendeFromTextView(findViewById(R.id.f43)) //Аудитория 43
            val audienceF44 : Audience = getAudiendeFromTextView(findViewById(R.id.f44)) //Аудитория 44
            val audienceF45 : Audience = getAudiendeFromTextView(findViewById(R.id.f45)) //Аудитория 45
            val audienceF46 : Audience = getAudiendeFromTextView(findViewById(R.id.f46)) //Аудитория 46

            lessonNum1 = arrayOf(audienceA11,audienceA12,audienceA13,audienceA14,audienceA15, audienceA16,
                audienceA21,audienceA22,audienceA23,audienceA24,audienceA25,audienceA26,
                audienceA31,audienceA32,audienceA33,audienceA34,audienceA35,audienceA36,
                audienceA41,audienceA42,audienceA43,audienceA44,audienceA45,audienceA46)
            lessonNum2 = arrayOf(audienceB11,audienceB12,audienceB13,audienceB14,audienceB15,audienceB16,
                audienceB21,audienceB22,audienceB23,audienceB24,audienceB25,audienceB26,
                audienceB31,audienceB32,audienceB33,audienceB34,audienceB35,audienceB36,
                audienceB41,audienceB42,audienceB43,audienceB44,audienceB45,audienceB46)
            lessonNum3 = arrayOf(audienceC11,audienceC12,audienceC13,audienceC14,audienceC15,audienceC16,
                audienceC21,audienceC22,audienceC23,audienceC24,audienceC25,audienceC26,
                audienceC31,audienceC32,audienceC33,audienceC34,audienceC35,audienceC36,
                audienceC41,audienceC42,audienceC43,audienceC44,audienceC45,audienceC46)
            lessonNum4 = arrayOf(audienceD11,audienceD12,audienceD13,audienceD14,audienceD15,audienceD16,
                audienceD21,audienceD22,audienceD23,audienceD24,audienceD25,audienceD26,
                audienceD31,audienceD32,audienceD33,audienceD34,audienceD35,audienceD36,
                audienceD41,audienceD42,audienceD43,audienceD44,audienceD45,audienceD46)
            lessonNum5 = arrayOf(audienceE11,audienceE12,audienceE13,audienceE14,audienceE15,audienceE16,
                audienceE21,audienceE22,audienceE23,audienceE24,audienceE25,audienceE26,
                audienceE31,audienceE32,audienceE33,audienceE34,audienceE35,audienceE36,
                audienceE41,audienceE42,audienceE43,audienceE44,audienceE45,audienceE46)
            lessonNum6 = arrayOf(audienceF11,audienceF12,audienceF13,audienceF14,audienceF15,audienceF16,
                audienceF21,audienceF22,audienceF23,audienceF24,audienceF25,audienceF26,
                audienceF31,audienceF32,audienceF33,audienceF34,audienceF35,audienceF36,
                audienceF41,audienceF42,audienceF43,audienceF44,audienceF45,audienceF46)
            //Визуальная часть (убирает кнопку, показывает таблицу
            val conL : ConstraintLayout = findViewById(R.id.constraintLayout)
            conL.visibility = View.GONE
            //Кнопки управления, делаю их видимыми
            animationBtn(findViewById<Button>(R.id.backBtn))
            animationBtn(findViewById<Button>(R.id.forwardBtn))
            val objectAnimator = ObjectAnimator.ofFloat(view as Button, "translationY", 200f)
            objectAnimator.duration = 800
            objectAnimator.start()
            animationTitle(findViewById<TextView>(R.id.textViewTitle))

            writeAudienceAtTextView()
        }
        catch (e : Exception) {
            Toast.makeText(this, "Подождите...", Toast.LENGTH_SHORT).show()
        }
    }
    //Функция разбивает textView и получается массив, элементы
    //которого идут параметрами для создания объекта аудитория, а после возращает его
    fun getAudiendeFromTextView(textView : TextView) : Audience {
        val str : String = textView.text.toString()
        val infoList = str.split(("|"))
        return Audience(infoList[0].toInt(),infoList[1])
    }

    //Функция вписывает в textView данные в правильном формате
    fun writeAudienceAtTextView() {
        //Пара номер 1
        doWriteAudienceAtTextView(lessonNum1,0,findViewById(R.id.a11)) //Аудитория 11
        doWriteAudienceAtTextView(lessonNum1,1,findViewById(R.id.a12)) //Аудитория 12
        doWriteAudienceAtTextView(lessonNum1,2,findViewById(R.id.a13)) //Аудитория 13
        doWriteAudienceAtTextView(lessonNum1,3,findViewById(R.id.a14)) //Аудитория 14
        doWriteAudienceAtTextView(lessonNum1,4,findViewById(R.id.a15)) //Аудитория 15
        doWriteAudienceAtTextView(lessonNum1,5,findViewById(R.id.a16)) //Аудитория 16
        doWriteAudienceAtTextView(lessonNum1,6,findViewById(R.id.a21)) //Аудитория 21
        doWriteAudienceAtTextView(lessonNum1,7,findViewById(R.id.a22)) //Аудитория 22
        doWriteAudienceAtTextView(lessonNum1,8,findViewById(R.id.a23)) //Аудитория 23
        doWriteAudienceAtTextView(lessonNum1,9,findViewById(R.id.a24)) //Аудитория 24
        doWriteAudienceAtTextView(lessonNum1,10,findViewById(R.id.a25))//Аудитория 24
        doWriteAudienceAtTextView(lessonNum1,11,findViewById(R.id.a26))//Аудитория 26
        doWriteAudienceAtTextView(lessonNum1,12,findViewById(R.id.a31))//Аудитория 31
        doWriteAudienceAtTextView(lessonNum1,13,findViewById(R.id.a32))//Аудитория 32
        doWriteAudienceAtTextView(lessonNum1,14,findViewById(R.id.a33))//Аудитория 33
        doWriteAudienceAtTextView(lessonNum1,15,findViewById(R.id.a34))//Аудитория 34
        doWriteAudienceAtTextView(lessonNum1,16,findViewById(R.id.a35))//Аудитория 35
        doWriteAudienceAtTextView(lessonNum1,17,findViewById(R.id.a36))//Аудитория 36
        doWriteAudienceAtTextView(lessonNum1,18,findViewById(R.id.a41))//Аудитория 41
        doWriteAudienceAtTextView(lessonNum1,19,findViewById(R.id.a42))//Аудитория 42
        doWriteAudienceAtTextView(lessonNum1,20,findViewById(R.id.a43))//Аудитория 43
        doWriteAudienceAtTextView(lessonNum1,21,findViewById(R.id.a44))//Аудитория 44
        doWriteAudienceAtTextView(lessonNum1,22,findViewById(R.id.a45))//Аудитория 45
        doWriteAudienceAtTextView(lessonNum1,23,findViewById(R.id.a46))//Аудитория 46
        //Пара номер 2
        doWriteAudienceAtTextView(lessonNum2,0,findViewById(R.id.b11)) //Аудитория 11
        doWriteAudienceAtTextView(lessonNum2,1,findViewById(R.id.b12)) //Аудитория 12
        doWriteAudienceAtTextView(lessonNum2,2,findViewById(R.id.b13)) //Аудитория 13
        doWriteAudienceAtTextView(lessonNum2,3,findViewById(R.id.b14)) //Аудитория 14
        doWriteAudienceAtTextView(lessonNum2,4,findViewById(R.id.b15)) //Аудитория 15
        doWriteAudienceAtTextView(lessonNum2,5,findViewById(R.id.b16)) //Аудитория 16
        doWriteAudienceAtTextView(lessonNum2,6,findViewById(R.id.b21)) //Аудитория 21
        doWriteAudienceAtTextView(lessonNum2,7,findViewById(R.id.b22)) //Аудитория 22
        doWriteAudienceAtTextView(lessonNum2,8,findViewById(R.id.b23)) //Аудитория 23
        doWriteAudienceAtTextView(lessonNum2,9,findViewById(R.id.b24)) //Аудитория 24
        doWriteAudienceAtTextView(lessonNum2,10,findViewById(R.id.b25))//Аудитория 25
        doWriteAudienceAtTextView(lessonNum2,11,findViewById(R.id.b26))//Аудитория 26
        doWriteAudienceAtTextView(lessonNum2,12,findViewById(R.id.b31))//Аудитория 31
        doWriteAudienceAtTextView(lessonNum2,13,findViewById(R.id.b32))//Аудитория 32
        doWriteAudienceAtTextView(lessonNum2,14,findViewById(R.id.b33))//Аудитория 33
        doWriteAudienceAtTextView(lessonNum2,15,findViewById(R.id.b34))//Аудитория 34
        doWriteAudienceAtTextView(lessonNum2,16,findViewById(R.id.b35))//Аудитория 35
        doWriteAudienceAtTextView(lessonNum2,17,findViewById(R.id.b36))//Аудитория 36
        doWriteAudienceAtTextView(lessonNum2,18,findViewById(R.id.b41))//Аудитория 41
        doWriteAudienceAtTextView(lessonNum2,19,findViewById(R.id.b42))//Аудитория 42
        doWriteAudienceAtTextView(lessonNum2,20,findViewById(R.id.b43))//Аудитория 43
        doWriteAudienceAtTextView(lessonNum2,21,findViewById(R.id.b44))//Аудитория 44
        doWriteAudienceAtTextView(lessonNum2,22,findViewById(R.id.b45))//Аудитория 45
        doWriteAudienceAtTextView(lessonNum2,23,findViewById(R.id.b46))//Аудитория 46
        //Пара номер 3
        doWriteAudienceAtTextView(lessonNum3,0,findViewById(R.id.c11)) //Аудитория 11
        doWriteAudienceAtTextView(lessonNum3,1,findViewById(R.id.c12)) //Аудитория 12
        doWriteAudienceAtTextView(lessonNum3,2,findViewById(R.id.c13)) //Аудитория 13
        doWriteAudienceAtTextView(lessonNum3,3,findViewById(R.id.c14)) //Аудитория 14
        doWriteAudienceAtTextView(lessonNum3,4,findViewById(R.id.c15)) //Аудитория 15
        doWriteAudienceAtTextView(lessonNum3,5,findViewById(R.id.c16)) //Аудитория 16
        doWriteAudienceAtTextView(lessonNum3,6,findViewById(R.id.c21)) //Аудитория 21
        doWriteAudienceAtTextView(lessonNum3,7,findViewById(R.id.c22)) //Аудитория 22
        doWriteAudienceAtTextView(lessonNum3,8,findViewById(R.id.c23)) //Аудитория 23
        doWriteAudienceAtTextView(lessonNum3,9,findViewById(R.id.c24)) //Аудитория 24
        doWriteAudienceAtTextView(lessonNum3,10,findViewById(R.id.c25))//Аудитория 25
        doWriteAudienceAtTextView(lessonNum3,11,findViewById(R.id.c26))//Аудитория 26
        doWriteAudienceAtTextView(lessonNum3,12,findViewById(R.id.c31))//Аудитория 31
        doWriteAudienceAtTextView(lessonNum3,13,findViewById(R.id.c32))//Аудитория 32
        doWriteAudienceAtTextView(lessonNum3,14,findViewById(R.id.c33))//Аудитория 33
        doWriteAudienceAtTextView(lessonNum3,15,findViewById(R.id.c34))//Аудитория 34
        doWriteAudienceAtTextView(lessonNum3,16,findViewById(R.id.c35))//Аудитория 35
        doWriteAudienceAtTextView(lessonNum3,17,findViewById(R.id.c36))//Аудитория 36
        doWriteAudienceAtTextView(lessonNum3,18,findViewById(R.id.c41))//Аудитория 41
        doWriteAudienceAtTextView(lessonNum3,19,findViewById(R.id.c42))//Аудитория 42
        doWriteAudienceAtTextView(lessonNum3,20,findViewById(R.id.c43))//Аудитория 43
        doWriteAudienceAtTextView(lessonNum3,21,findViewById(R.id.c44))//Аудитория 44
        doWriteAudienceAtTextView(lessonNum3,22,findViewById(R.id.c45))//Аудитория 45
        doWriteAudienceAtTextView(lessonNum3,23,findViewById(R.id.c46))//Аудитория 46
        //Пара номер 4
        doWriteAudienceAtTextView(lessonNum4,0,findViewById(R.id.d11)) //Аудитория 11
        doWriteAudienceAtTextView(lessonNum4,1,findViewById(R.id.d12)) //Аудитория 12
        doWriteAudienceAtTextView(lessonNum4,2,findViewById(R.id.d13)) //Аудитория 13
        doWriteAudienceAtTextView(lessonNum4,3,findViewById(R.id.d14)) //Аудитория 14
        doWriteAudienceAtTextView(lessonNum4,4,findViewById(R.id.d15)) //Аудитория 15
        doWriteAudienceAtTextView(lessonNum4,5,findViewById(R.id.d16)) //Аудитория 16
        doWriteAudienceAtTextView(lessonNum4,6,findViewById(R.id.d21)) //Аудитория 21
        doWriteAudienceAtTextView(lessonNum4,7,findViewById(R.id.d22)) //Аудитория 22
        doWriteAudienceAtTextView(lessonNum4,8,findViewById(R.id.d23)) //Аудитория 23
        doWriteAudienceAtTextView(lessonNum4,9,findViewById(R.id.d24)) //Аудитория 24
        doWriteAudienceAtTextView(lessonNum4,10,findViewById(R.id.d25))//Аудитория 25
        doWriteAudienceAtTextView(lessonNum4,11,findViewById(R.id.d26))//Аудитория 26
        doWriteAudienceAtTextView(lessonNum4,12,findViewById(R.id.d31))//Аудитория 31
        doWriteAudienceAtTextView(lessonNum4,13,findViewById(R.id.d32))//Аудитория 32
        doWriteAudienceAtTextView(lessonNum4,14,findViewById(R.id.d33))//Аудитория 33
        doWriteAudienceAtTextView(lessonNum4,15,findViewById(R.id.d34))//Аудитория 34
        doWriteAudienceAtTextView(lessonNum4,16,findViewById(R.id.d35))//Аудитория 35
        doWriteAudienceAtTextView(lessonNum4,17,findViewById(R.id.d36))//Аудитория 36
        doWriteAudienceAtTextView(lessonNum4,18,findViewById(R.id.d41))//Аудитория 41
        doWriteAudienceAtTextView(lessonNum4,19,findViewById(R.id.d42))//Аудитория 42
        doWriteAudienceAtTextView(lessonNum4,20,findViewById(R.id.d43))//Аудитория 43
        doWriteAudienceAtTextView(lessonNum4,21,findViewById(R.id.d44))//Аудитория 44
        doWriteAudienceAtTextView(lessonNum4,22,findViewById(R.id.d45))//Аудитория 45
        doWriteAudienceAtTextView(lessonNum4,23,findViewById(R.id.d46))//Аудитория 46
        //Пара номер 5
        doWriteAudienceAtTextView(lessonNum5,0,findViewById(R.id.e11)) //Аудитория 11
        doWriteAudienceAtTextView(lessonNum5,1,findViewById(R.id.e12)) //Аудитория 12
        doWriteAudienceAtTextView(lessonNum5,2,findViewById(R.id.e13)) //Аудитория 13
        doWriteAudienceAtTextView(lessonNum5,3,findViewById(R.id.e14)) //Аудитория 14
        doWriteAudienceAtTextView(lessonNum5,4,findViewById(R.id.e15)) //Аудитория 15
        doWriteAudienceAtTextView(lessonNum5,5,findViewById(R.id.e16)) //Аудитория 16
        doWriteAudienceAtTextView(lessonNum5,6,findViewById(R.id.e21)) //Аудитория 21
        doWriteAudienceAtTextView(lessonNum5,7,findViewById(R.id.e22)) //Аудитория 22
        doWriteAudienceAtTextView(lessonNum5,8,findViewById(R.id.e23)) //Аудитория 23
        doWriteAudienceAtTextView(lessonNum5,9,findViewById(R.id.e24)) //Аудитория 24
        doWriteAudienceAtTextView(lessonNum5,10,findViewById(R.id.e25))//Аудитория 25
        doWriteAudienceAtTextView(lessonNum5,11,findViewById(R.id.e26))//Аудитория 26
        doWriteAudienceAtTextView(lessonNum5,12,findViewById(R.id.e31))//Аудитория 31
        doWriteAudienceAtTextView(lessonNum5,13,findViewById(R.id.e32))//Аудитория 32
        doWriteAudienceAtTextView(lessonNum5,14,findViewById(R.id.e33))//Аудитория 33
        doWriteAudienceAtTextView(lessonNum5,15,findViewById(R.id.e34))//Аудитория 34
        doWriteAudienceAtTextView(lessonNum5,16,findViewById(R.id.e35))//Аудитория 35
        doWriteAudienceAtTextView(lessonNum5,17,findViewById(R.id.e36))//Аудитория 36
        doWriteAudienceAtTextView(lessonNum5,18,findViewById(R.id.e41))//Аудитория 41
        doWriteAudienceAtTextView(lessonNum5,19,findViewById(R.id.e42))//Аудитория 42
        doWriteAudienceAtTextView(lessonNum5,20,findViewById(R.id.e43))//Аудитория 43
        doWriteAudienceAtTextView(lessonNum5,21,findViewById(R.id.e44))//Аудитория 44
        doWriteAudienceAtTextView(lessonNum5,22,findViewById(R.id.e45))//Аудитория 45
        doWriteAudienceAtTextView(lessonNum5,23,findViewById(R.id.e46))//Аудитория 46
        //Пара номер 6
        doWriteAudienceAtTextView(lessonNum6,0,findViewById(R.id.f11)) //Аудитория 11
        doWriteAudienceAtTextView(lessonNum6,1,findViewById(R.id.f12)) //Аудитория 12
        doWriteAudienceAtTextView(lessonNum6,2,findViewById(R.id.f13)) //Аудитория 13
        doWriteAudienceAtTextView(lessonNum6,3,findViewById(R.id.f14)) //Аудитория 14
        doWriteAudienceAtTextView(lessonNum6,4,findViewById(R.id.f15)) //Аудитория 15
        doWriteAudienceAtTextView(lessonNum6,5,findViewById(R.id.f16)) //Аудитория 16
        doWriteAudienceAtTextView(lessonNum6,6,findViewById(R.id.f21)) //Аудитория 21
        doWriteAudienceAtTextView(lessonNum6,7,findViewById(R.id.f22)) //Аудитория 22
        doWriteAudienceAtTextView(lessonNum6,8,findViewById(R.id.f23)) //Аудитория 23
        doWriteAudienceAtTextView(lessonNum6,9,findViewById(R.id.f24)) //Аудитория 24
        doWriteAudienceAtTextView(lessonNum6,10,findViewById(R.id.f25))//Аудитория 25
        doWriteAudienceAtTextView(lessonNum6,11,findViewById(R.id.f26))//Аудитория 26
        doWriteAudienceAtTextView(lessonNum6,12,findViewById(R.id.f31))//Аудитория 31
        doWriteAudienceAtTextView(lessonNum6,13,findViewById(R.id.f32))//Аудитория 32
        doWriteAudienceAtTextView(lessonNum6,14,findViewById(R.id.f33))//Аудитория 33
        doWriteAudienceAtTextView(lessonNum6,15,findViewById(R.id.f34))//Аудитория 34
        doWriteAudienceAtTextView(lessonNum6,16,findViewById(R.id.f35))//Аудитория 35
        doWriteAudienceAtTextView(lessonNum6,17,findViewById(R.id.f36))//Аудитория 36
        doWriteAudienceAtTextView(lessonNum6,18,findViewById(R.id.f41))//Аудитория 41
        doWriteAudienceAtTextView(lessonNum6,19,findViewById(R.id.f42))//Аудитория 42
        doWriteAudienceAtTextView(lessonNum6,20,findViewById(R.id.f43))//Аудитория 43
        doWriteAudienceAtTextView(lessonNum6,21,findViewById(R.id.f44))//Аудитория 44
        doWriteAudienceAtTextView(lessonNum6,22,findViewById(R.id.f45))//Аудитория 45
        doWriteAudienceAtTextView(lessonNum6,23,findViewById(R.id.f46))//Аудитория 46

        //Добавляет textView событые двойного клика
        addObrEvenForBtn()
    }
    fun doWriteAudienceAtTextView(lessonArray : Array<Audience>,num : Int, texyView : TextView) {
        val builder = SpannableStringBuilder()
        val firstPart = lessonArray[num].numberAudience.toString() + "\n"
        val firstSpannable = SpannableString(firstPart)
        firstSpannable.setSpan(
            AbsoluteSizeSpan(26, true),
            0,
            firstPart.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if(lessonArray[num].teacher != "-") {
            var secondPart = lessonArray[num].teacher
            val secondSpannable = SpannableString(secondPart)
            secondSpannable.setSpan(
                AbsoluteSizeSpan(16, true),
                0,
                secondPart.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(firstSpannable)
            builder.append(secondSpannable)

            texyView.setText(builder, TextView.BufferType.SPANNABLE)
            texyView.setTextColor(getColor(R.color.red))
        }
        else {
            var secondPart = "Свободен"
            val secondSpannable = SpannableString(secondPart)
            secondSpannable.setSpan(
                AbsoluteSizeSpan(16, true),
                0,
                secondPart.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(firstSpannable)
            builder.append(secondSpannable)
            texyView.setText(builder, TextView.BufferType.SPANNABLE)
            texyView.setTextColor(getColor(R.color.green))}
    }

    fun backTable(view: View) {
        var numberLesson = findViewById<TextView>(R.id.textViewNumberLesson)
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        numberLesson.startAnimation(animation)
        findViewById<Button>(R.id.forwardBtn).isEnabled = true
        numOfTable--
        if(numOfTable == 1) {
            view.isEnabled = false
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_1)
            findViewById<ScrollView>(R.id.audienceWrap1).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap1).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap2).visibility = View.INVISIBLE
        }
        else if(numOfTable == 2) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_2)
            findViewById<ScrollView>(R.id.audienceWrap2).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap2).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap3).visibility = View.INVISIBLE
        }
        else if(numOfTable == 3) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_3)
            findViewById<ScrollView>(R.id.audienceWrap3).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap3).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap4).visibility = View.INVISIBLE
        }
        else if(numOfTable == 4) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_4)
            findViewById<ScrollView>(R.id.audienceWrap4).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap4).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap5).visibility = View.INVISIBLE
        }
        else if(numOfTable == 5) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_5)
            findViewById<ScrollView>(R.id.audienceWrap5).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap5).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap6).visibility = View.INVISIBLE
        }
    }
    fun forwardTable(view: View) {
        var numberLesson = findViewById<TextView>(R.id.textViewNumberLesson)
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        numberLesson.startAnimation(animation)
        findViewById<Button>(R.id.backBtn).isEnabled = true
        numOfTable++
        if(numOfTable == 2) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_2)
            findViewById<ScrollView>(R.id.audienceWrap2).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap2).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap1).visibility = View.INVISIBLE
        }
        else if(numOfTable == 3) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_3)
            findViewById<ScrollView>(R.id.audienceWrap3).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap3).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap2).visibility = View.INVISIBLE
        }
        else if(numOfTable == 4) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_4)
            findViewById<ScrollView>(R.id.audienceWrap4).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap4).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap3).visibility = View.INVISIBLE
        }
        else if(numOfTable == 5) {
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_5)
            findViewById<ScrollView>(R.id.audienceWrap5).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap5).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap4).visibility = View.INVISIBLE
        }
        else if(numOfTable == 6) {
            view.isEnabled = false
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_6)
            findViewById<ScrollView>(R.id.audienceWrap6).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.audienceWrap6).smoothScrollTo(0, 0)
            findViewById<ScrollView>(R.id.audienceWrap5).visibility = View.INVISIBLE
        }
    }

    //Вспомогательные функции
    //Отображается нужный список в зависимости от времени
    var isLessonOver : Array<Boolean> = arrayOf(false,false,false,false,false,false)
    @RequiresApi(Build.VERSION_CODES.O)
    fun showListByTime() {
        val currentTime = LocalTime.now()// текущее время
        findViewById<ScrollView>(R.id.audienceWrap1).visibility = View.INVISIBLE
        if (currentTime.isAfter(LocalTime.of(9, 50)) && currentTime.isBefore(LocalTime.of(11, 20))) {
            isLessonOver[0] = true
            numOfTable = 2
            findViewById<ScrollView>(R.id.audienceWrap2).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_2)
        } else if (currentTime.isAfter(LocalTime.of(11, 20)) && currentTime.isBefore(LocalTime.of(13, 0))) {
            isLessonOver[0] = true
            isLessonOver[1] = true
            numOfTable = 3
            findViewById<ScrollView>(R.id.audienceWrap3).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_3)
        }
        else if (currentTime.isAfter(LocalTime.of(13, 0)) && currentTime.isBefore(LocalTime.of(14, 40))) {
            isLessonOver[0] = true
            isLessonOver[1] = true
            isLessonOver[2] = true
            numOfTable = 4
            findViewById<ScrollView>(R.id.audienceWrap4).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_4)
        }
        else if (currentTime.isAfter(LocalTime.of(14, 40)) && currentTime.isBefore(LocalTime.of(16, 20))) {
            isLessonOver[0] = true
            isLessonOver[1] = true
            isLessonOver[2] = true
            isLessonOver[3] = true
            numOfTable = 5
            findViewById<ScrollView>(R.id.audienceWrap5).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_5)
        }
        else if (currentTime.isAfter(LocalTime.of(16, 20)) && currentTime.isBefore(LocalTime.of(17, 50))) {
            isLessonOver[0] = true
            isLessonOver[1] = true
            isLessonOver[2] = true
            isLessonOver[3] = true
            isLessonOver[4] = true
            numOfTable = 6
            findViewById<Button>(R.id.forwardBtn).isEnabled = false
            findViewById<ScrollView>(R.id.audienceWrap6).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textViewNumberLesson).text = getString(R.string.lesson_6)
        }
        else {
            findViewById<ScrollView>(R.id.audienceWrap1).visibility = View.VISIBLE
            findViewById<Button>(R.id.backBtn).isEnabled = false
            numOfTable = 1
        }
    }

    var mStartForResult: ActivityResultLauncher<Intent?>? =
        registerForActivityResult(StartActivityForResult()) {
                result ->
            if (result.resultCode == RESULT_OK) {
                val intent: Intent? = result.data
                val idTextViewStr: String? = intent!!.getStringExtra("idTextView")
                val tvId = getResources().getIdentifier(idTextViewStr, "id",packageName)
                val builder = SpannableStringBuilder()
                val firstPart = intent!!.getStringExtra("numberAudience")
                val firstSpannable = SpannableString(firstPart)
                if (firstPart != null) {
                    firstSpannable.setSpan(
                        AbsoluteSizeSpan(24, true),
                        0,
                        firstPart.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                var secondPart = "\nСвободен"
                val secondSpannable = SpannableString(secondPart)
                secondSpannable.setSpan(
                    AbsoluteSizeSpan(16, true),
                    0,
                    secondPart.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.append(firstSpannable)
                builder.append(secondSpannable)
                findViewById<TextView>(tvId).setText(builder, TextView.BufferType.SPANNABLE)
                findViewById<TextView>(tvId).setTextColor(getColor(R.color.green))
            }
        }
    var mStartForResult2: ActivityResultLauncher<Intent?>? =
        registerForActivityResult(StartActivityForResult()) {
                result ->
            if (result.resultCode == RESULT_OK) {
                val intent: Intent? = result.data
                val idTextViewStr: String? = intent!!.getStringExtra("idTextView")
                val tvId = getResources().getIdentifier(idTextViewStr, "id",packageName)

                val builder = SpannableStringBuilder()
                val firstPart = intent!!.getStringExtra("numberAudience")
                val secondPart = intent!!.getStringExtra("teacher")
                val firstSpannable = SpannableString(firstPart)
                if (firstPart != null) {
                    firstSpannable.setSpan(
                        AbsoluteSizeSpan(24, true),
                        0,
                        firstPart.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                val secondSpannable = SpannableString(secondPart)
                if (secondPart != null) {
                    secondSpannable.setSpan(
                        AbsoluteSizeSpan(16, true),
                        0,
                        secondPart.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                builder.append(firstSpannable)
                builder.append(secondSpannable)
                findViewById<TextView>(tvId).setText(builder, TextView.BufferType.SPANNABLE)
                findViewById<TextView>(tvId).setTextColor(getColor(R.color.red))
            }
        }

    //Добавляет всем textView событие двойного клика
    fun addObrEvenForBtn() {
        //Для первой пары
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a11),"11", getResources().getIdentifier("a11", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a12),"12", getResources().getIdentifier("a12", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a13),"13", getResources().getIdentifier("a13", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a14),"14", getResources().getIdentifier("a14", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a15),"15", getResources().getIdentifier("a15", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a16),"16", getResources().getIdentifier("a16", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a21),"21", getResources().getIdentifier("a21", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a22),"22", getResources().getIdentifier("a22", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a23),"23", getResources().getIdentifier("a23", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a24),"24", getResources().getIdentifier("a24", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a25),"25", getResources().getIdentifier("a25", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a26),"26", getResources().getIdentifier("a26", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a31),"31", getResources().getIdentifier("a31", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a32),"32", getResources().getIdentifier("a32", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a33),"33", getResources().getIdentifier("a33", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a34),"34", getResources().getIdentifier("a34", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a35),"35", getResources().getIdentifier("a35", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a36),"36", getResources().getIdentifier("a36", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a41),"41", getResources().getIdentifier("a41", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a42),"42", getResources().getIdentifier("a42", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a43),"43", getResources().getIdentifier("a43", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a44),"44", getResources().getIdentifier("a44", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a45),"45", getResources().getIdentifier("a45", "id",packageName),"a","1")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.a46),"46", getResources().getIdentifier("a46", "id",packageName),"a","1")
        //Для второй пары
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b11),"11", getResources().getIdentifier("b11", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b12),"12", getResources().getIdentifier("b12", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b13),"13", getResources().getIdentifier("b13", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b14),"14", getResources().getIdentifier("b14", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b15),"15", getResources().getIdentifier("b15", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b16),"16", getResources().getIdentifier("b16", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b21),"21", getResources().getIdentifier("b21", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b22),"22", getResources().getIdentifier("b22", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b23),"23", getResources().getIdentifier("b23", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b24),"24", getResources().getIdentifier("b24", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b25),"25", getResources().getIdentifier("b25", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b26),"26", getResources().getIdentifier("b26", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b31),"31", getResources().getIdentifier("b31", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b32),"32", getResources().getIdentifier("b32", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b33),"33", getResources().getIdentifier("b33", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b34),"34", getResources().getIdentifier("b34", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b35),"35", getResources().getIdentifier("b35", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b36),"36", getResources().getIdentifier("b36", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b41),"41", getResources().getIdentifier("b41", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b42),"42", getResources().getIdentifier("b42", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b43),"43", getResources().getIdentifier("b43", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b44),"44", getResources().getIdentifier("b44", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b45),"45", getResources().getIdentifier("b45", "id",packageName),"b","2")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.b46),"46", getResources().getIdentifier("b46", "id",packageName),"b","2")
        //Для третьей пары
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c11),"11", getResources().getIdentifier("c11", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c12),"12", getResources().getIdentifier("c12", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c13),"13", getResources().getIdentifier("c13", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c14),"14", getResources().getIdentifier("c14", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c15),"15", getResources().getIdentifier("c15", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c16),"16", getResources().getIdentifier("c16", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c21),"21", getResources().getIdentifier("c21", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c22),"22", getResources().getIdentifier("c22", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c23),"23", getResources().getIdentifier("c23", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c24),"24", getResources().getIdentifier("c24", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c25),"25", getResources().getIdentifier("c25", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c26),"26", getResources().getIdentifier("c26", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c31),"31", getResources().getIdentifier("c31", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c32),"32", getResources().getIdentifier("c32", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c33),"33", getResources().getIdentifier("c33", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c34),"34", getResources().getIdentifier("c34", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c35),"35", getResources().getIdentifier("c35", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c36),"36", getResources().getIdentifier("c36", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c41),"41", getResources().getIdentifier("c41", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c42),"42", getResources().getIdentifier("c42", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c43),"43", getResources().getIdentifier("c43", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c44),"44", getResources().getIdentifier("c44", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c45),"45", getResources().getIdentifier("c45", "id",packageName),"c","3")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.c46),"46", getResources().getIdentifier("c46", "id",packageName),"c","3")
        //Для четвертой пары
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d11),"11", getResources().getIdentifier("d11", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d12),"12", getResources().getIdentifier("d12", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d13),"13", getResources().getIdentifier("d13", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d14),"14", getResources().getIdentifier("d14", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d15),"15", getResources().getIdentifier("d15", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d16),"16", getResources().getIdentifier("d16", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d21),"21", getResources().getIdentifier("d21", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d22),"22", getResources().getIdentifier("d22", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d23),"23", getResources().getIdentifier("d23", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d24),"24", getResources().getIdentifier("d24", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d25),"25", getResources().getIdentifier("d25", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d26),"26", getResources().getIdentifier("d26", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d31),"31", getResources().getIdentifier("d31", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d32),"32", getResources().getIdentifier("d32", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d33),"33", getResources().getIdentifier("d33", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d34),"34", getResources().getIdentifier("d34", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d35),"35", getResources().getIdentifier("d35", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d36),"36", getResources().getIdentifier("d36", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d41),"41", getResources().getIdentifier("d41", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d42),"42", getResources().getIdentifier("d42", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d43),"43", getResources().getIdentifier("d43", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d44),"44", getResources().getIdentifier("d44", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d45),"45", getResources().getIdentifier("d45", "id",packageName),"d","4")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.d46),"46", getResources().getIdentifier("d46", "id",packageName),"d","4")
        //Для пятой пары
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e11),"11", getResources().getIdentifier("e11", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e12),"12", getResources().getIdentifier("e12", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e13),"13", getResources().getIdentifier("e13", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e14),"14", getResources().getIdentifier("e14", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e15),"15", getResources().getIdentifier("e15", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e16),"16", getResources().getIdentifier("e16", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e21),"21", getResources().getIdentifier("e21", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e22),"22", getResources().getIdentifier("e22", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e23),"23", getResources().getIdentifier("e23", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e24),"24", getResources().getIdentifier("e24", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e25),"25", getResources().getIdentifier("e25", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e26),"26", getResources().getIdentifier("e26", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e31),"31", getResources().getIdentifier("e31", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e32),"32", getResources().getIdentifier("e32", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e33),"33", getResources().getIdentifier("e33", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e34),"34", getResources().getIdentifier("e34", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e35),"35", getResources().getIdentifier("e35", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e36),"36", getResources().getIdentifier("e36", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e41),"41", getResources().getIdentifier("e41", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e42),"42", getResources().getIdentifier("e42", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e43),"43", getResources().getIdentifier("e43", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e44),"44", getResources().getIdentifier("e44", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e45),"45", getResources().getIdentifier("e45", "id",packageName),"e","5")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.e46),"46", getResources().getIdentifier("e46", "id",packageName),"e","5")
        //Для шестой пары
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f11),"11", getResources().getIdentifier("f11", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f12),"12", getResources().getIdentifier("f12", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f13),"13", getResources().getIdentifier("f13", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f14),"14", getResources().getIdentifier("f14", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f15),"15", getResources().getIdentifier("f15", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f16),"16", getResources().getIdentifier("f16", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f21),"21", getResources().getIdentifier("f21", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f22),"22", getResources().getIdentifier("f22", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f23),"23", getResources().getIdentifier("f23", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f24),"24", getResources().getIdentifier("f24", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f25),"25", getResources().getIdentifier("f25", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f26),"26", getResources().getIdentifier("f26", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f31),"31", getResources().getIdentifier("f31", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f32),"32", getResources().getIdentifier("f32", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f33),"33", getResources().getIdentifier("f33", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f34),"34", getResources().getIdentifier("f34", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f35),"35", getResources().getIdentifier("f35", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f36),"36", getResources().getIdentifier("f36", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f41),"41", getResources().getIdentifier("f41", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f42),"42", getResources().getIdentifier("f42", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f43),"43", getResources().getIdentifier("f43", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f44),"44", getResources().getIdentifier("f44", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f45),"45", getResources().getIdentifier("f45", "id",packageName), "f","6")
        doAddObrEvenForBtn1(findViewById<TextView>(R.id.f46),"46", getResources().getIdentifier("f46", "id",packageName), "f","6")
    }
    fun doAddObrEvenForBtn1(textView: TextView, numberAudience : String, idTextView : Int, word : String, numberLesson: String) {
        textView.setOnTouchListener(object : View.OnTouchListener {
            private var lastClickTime: Long = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP) {
                    val clickTime = System.currentTimeMillis()
                    if (clickTime - lastClickTime < 500) {
                        if(isLessonOver[numberLesson.toInt() - 1] == false) {
                            if(findViewById<TextView>(idTextView).currentTextColor == getColor(R.color.red)) {
                                val intent : Intent = Intent(this@MainActivity, FreeTheAudience::class.java)
                                intent.putExtra("numberLesson", numberLesson)
                                intent.putExtra("word", word)
                                intent.putExtra("numberAudience", numberAudience)
                                mStartForResult?.launch(intent)
                            }
                            else {
                                val intent : Intent = Intent(this@MainActivity, OccupyTheAudiences::class.java)
                                intent.putExtra("numberLesson", numberLesson)
                                intent.putExtra("word", word)
                                intent.putExtra("numberAudience", numberAudience)
                                mStartForResult2?.launch(intent)
                            }
                        }
                        else {
                            Toast.makeText(applicationContext, "Пара уже прошла! Занять/освободить аудиторию невозможно!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    lastClickTime = clickTime
                }
                return true
            }
        })
    }
}