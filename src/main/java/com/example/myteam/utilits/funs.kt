package com.example.myteam.utilits

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myteam.R
import com.squareup.picasso.Picasso

//Файл для хранения утилитарных функции, доступных во всем приложении

//Функция показывает сообщение
fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

//Функция расширения для AppCompatActivity, позволяет запускать активити
fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
    this.finish()
}

//Функция расширения для AppCompatActivity, позволяет устанавливать фрагменты
fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    addStack: Boolean = true
) {
    if (addStack) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.dataContainer, fragment)
            .commit()
    } else {
        supportFragmentManager.beginTransaction()   //Добавляем фрагмент уже без добавления в стек
            .replace(R.id.dataContainer, fragment)
            .commit()
    }
}

//Функция расширения для Fragment, позволяет устанавливать фрагменты
fun Fragment.replaceFragment(fragment: Fragment) {
    this.fragmentManager?.beginTransaction()
        ?.addToBackStack(null)
        ?.replace(R.id.dataContainer, fragment)
        ?.commit()
}

//Функция скрывает клавиатуру
fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

//Функция раширения ImageView, скачивает и устанавливает картинку
fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get()   //Качаем изображение
        .load(url)
        .fit()  //Чтобы изображение встало в размер
        .placeholder(R.drawable.def_user)  //Картинка которая установится
        .into(this) //Куда установить картинку
}