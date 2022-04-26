package com.example.myteam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myteam.activities.RegisterActivity
import com.example.myteam.databinding.ActivityMainBinding
import com.example.myteam.fragments.ChatFragment
import com.example.myteam.objects.AppDrawer
import com.example.myteam.utilits.*

//Главная активность
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    private lateinit var mToolbar: Toolbar

    // Функция запускается один раз, при создании активити
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this //Ссылка на мейн
        initFirebase()
        initUser{       //Запускается инициализация юзера, а потом запускается все приложение
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
        mAppDrawer = AppDrawer(this, mToolbar)
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
}