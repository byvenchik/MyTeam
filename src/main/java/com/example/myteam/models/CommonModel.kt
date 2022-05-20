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
    var choice: Boolean = false,

//Cвойства для задач
    var description: String = "",
    var task: String = "",
    var task_id: String = "",
    var received:String="",
    val status_task:String = "",
    var received_id:String="",
    var received_photo:String="",
    var from_photo:String="",

    var completed_task:String = "",
    var unfulfilled_task:String = "",
    var decline_task:String = "",
    var get_completed_task:String = "",
    var get_unfulfilled_task:String = "",
    var get_decline_task:String = ""


    ) {
    //Сравнение сообщений
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}