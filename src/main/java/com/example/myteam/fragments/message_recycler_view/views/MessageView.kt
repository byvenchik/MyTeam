package com.example.myteam.fragments.message_recycler_view.views

interface MessageView {

    val id: String
    val from: String
    val timeStamp: String
    val fileUrl: String
    val text: String

    //Здесь надо отобразить переменные, которые будут отображать тип View
    companion object {
        val MESSAGE_IMAGE: Int
            get() = 0
        val MESSAGE_TEXT: Int
            get() = 1
        val MESSAGE_VOICE: Int
            get() = 2
    }

    fun getTypeView():Int
}