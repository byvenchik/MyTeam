package com.example.myteam.screens.settings

import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.screens.BaseChangeFragment
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
                    updateCurrentUsername(mNewUsername)
                }
            }
    }
}



