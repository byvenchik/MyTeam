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
    var from_username: String = "",

//Свойства для сообщений
    var text: String = "",
    var type: String = "",      //текст, файлы и тд
    var from: String = "",      //Автор
    var timeStamp: Any = "",   //Время
    var fileUrl: String = "empty",

    var lastMessage: String = "",
    var choice: Boolean = false


) {
    //Сравнение сообщений
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}