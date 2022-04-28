package com.example.myteam

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.myteam.activities.RegisterActivity
import com.example.myteam.databinding.ActivityMainBinding
import com.example.myteam.fragments.ChatFragment
import com.example.myteam.objects.AppDrawer
import com.example.myteam.utilits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//Главная активность
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar

    // Функция запускается один раз, при создании активити
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this //Ссылка на мейн
        initFirebase()
        //Запускается инициализация юзера, а потом запускается все приложение
        initUser {
            //Корутина - сопрограмма, которая работает в фоновом потоке
            //ЖЦ - Input-Output
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }

    //Функция инициализирует функциональность приложения
    private fun initFunc() {
        if (AUTH.currentUser != null) {
            setSupportActionBar(mToolbar)
            mAppDrawer.create()
            replaceFragment(ChatFragment(), false)
        } else {
            replaceActivity(RegisterActivity())
        }
    }

    //Функция инициализирует переменные
    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    //Отрабатывает, когда разворачиваем приложение
    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    //Отрабатывает, когда сворачиваем приложение
    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    //Окно разрешения на чтение контактов
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Если доступ предоставлен,то идет инициализация
        if (ContextCompat.checkSelfPermission(
                APP_ACTIVITY,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }
}