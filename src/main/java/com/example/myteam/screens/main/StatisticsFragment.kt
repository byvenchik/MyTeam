package com.example.myteam.screens.main

import androidx.fragment.app.Fragment
import com.example.myteam.R
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.hideKeyboard

//Главный фрагмент
class StatisticsFragment : Fragment(R.layout.fragment_main) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Моя статистика"
        APP_ACTIVITY.mAppDrawer.enableDrawer()  //Его надо включать после входа
        hideKeyboard()
    }


}




