package com.example.myteam.fragments

import com.example.myteam.R
import com.example.myteam.utilits.*
import kotlinx.android.synthetic.main.fragment_change_username.*
import java.util.*

//Фрагмент для изменения username пользователя
class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()
        //Разрешить создание опшин меню
        settings_input_username.setText(USER.username)
    }

    override fun change() {
        mNewUsername = settings_input_username.text.toString().toLowerCase(Locale.getDefault())
        //LowerCase - всегда маленькие буквы
        if (mNewUsername.isEmpty()) {
            showToast("Поле пустое")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES) //Проверка на повтор
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUsername)) {
                        showToast("Такой пользователь уже существует")
                    } else {
                        changeUsername()
                    }
                })
        }
    }

    //Изменение username в базе данных
    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername()
                }
            }
    }

    //Обновление username в базе данных у текущего пользователя
    private fun updateCurrentUsername() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
            .setValue(mNewUsername)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.toast_data_update))
                    deleteOldUsername()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    //Удаление старого username из базы данных
    private fun deleteOldUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.toast_data_update))
                    fragmentManager?.popBackStack()  //По стеку назад
                    USER.username = mNewUsername    //Обновить модель
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }
}