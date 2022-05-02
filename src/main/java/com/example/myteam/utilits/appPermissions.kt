package com.example.myteam.utilits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//Для управления всеми разрешениями

//Длинный вызов заменили константой
const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
const val PERMISSION_REQUEST = 200

//Сюда будет приходить READ_CONTACTS и вохвращать bool
fun checkPermission(permission: String):Boolean{
    //Если SDK>=23 и разрешение не было выдано,то тогда запрашиваем разрешение у пользователя
    return if(Build.VERSION.SDK_INT >= 23
        && ContextCompat.checkSelfPermission(APP_ACTIVITY,permission)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(APP_ACTIVITY, arrayOf(permission),PERMISSION_REQUEST)
        false
    }else true
}