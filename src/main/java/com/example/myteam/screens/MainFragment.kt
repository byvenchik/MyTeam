package com.example.myteam.screens

import androidx.fragment.app.Fragment
import com.example.myteam.R
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.hideKeyboard

//Главный фрагмент, содержит все чаты, группы и каналы с которыми взаимодействует пользователь
class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "MyTeam"
        APP_ACTIVITY.mAppDrawer.enableDrawer()  //Его надо включать после входа
        hideKeyboard()
    }
}




