package com.example.myteam.models

//Общая модель
data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var photoUrl: String = "empty",  //Крашилось при новом пользователе
    var phone: String = "",

//Свойства для сообщений
    var text: String = "",
    var type: String = "",      //текст, файлы и тд
    var from: String = "",      //Автор
    var timeStamp: Any = ""   //Время сервера

)