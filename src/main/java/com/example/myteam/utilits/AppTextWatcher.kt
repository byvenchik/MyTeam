package com.example.myteam.utilits

import android.text.Editable
import android.text.TextWatcher

//Для удобства сделал кастомный
class AppTextWatcher(val onSuccess: (Editable?) -> Unit) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        onSuccess(s)
    }

    //Дефолтные методы
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

}