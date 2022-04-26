package com.example.myteam.models

data class User(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var photoUrl: String = "empty",  //Крашилось при новом пользователе
    var phone: String = ""
)