package com.example.myteam.utilits

import com.example.myteam.database.*

//Типобезопасное перечисление состояний
enum class AppStates(val state: String) {
    ONLINE("в сети"),
    OFFLINE("был(а) недавно");

    //Для отправки состояния в БД
    companion object {
        fun updateState(appStates: AppStates) {
            //Устраняем баги. Появление null пользователя
            if(AUTH.currentUser != null){
                //Тогда делаем установку статуса
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATE)
                    .setValue(appStates.state)
                    .addOnSuccessListener { USER.state = appStates.state }  //Обновили
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
    }
}
