package com.example.myteam.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.myteam.R
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.hideKeyboard

//Базовый фрагмент, от него наследуются фрагменты где происходит изменение данных о пользователе
open class BaseChangeFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        APP_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
    }

    //Создание выпадающего меню
    //Чтобы появилась галочка
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    //Слушатель выбора пункта выпадающего меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {  //Находим нш пункт меню по id
            R.id.settings_confirm_change -> change() //Когда нажимаем -> запуск changeName
        }
        return true
    }

    open fun change() {

    }
}
