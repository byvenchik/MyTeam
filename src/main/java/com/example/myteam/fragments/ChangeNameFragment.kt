package com.example.myteam.fragments

import com.example.myteam.R
import com.example.myteam.utilits.*
import kotlinx.android.synthetic.main.fragment_change_name.*

//Фрагмент для изменения имени пользователя
class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    override fun onResume() {
        super.onResume()
        initFullnameList()
    }

    //Лист для разделения fullname
    private fun initFullnameList() {
        val fullnameList = USER.fullname.split(" ") //Возникала ошибка при добавлении пользователя
        if (fullnameList.size > 1) {
            settings_input_name.setText(fullnameList[0])
            settings_input_surname.setText(fullnameList[1])
        } else
            settings_input_name.setText(fullnameList[0])
    }

    override fun change() {
        val name = settings_input_name.text.toString()
        val surname = settings_input_surname.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullname = "$name $surname"
            //Обращение к БД
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
                .setValue(fullname).addOnCompleteListener { //Передаем значение и вешаем слушатель
                    if (it.isSuccessful) {
                        showToast(getString(R.string.toast_data_update)) //Обновлены
                        USER.fullname = fullname
                        APP_ACTIVITY.mAppDrawer.updateHeader()  //Обновление имени в шапке
                        fragmentManager?.popBackStack() //Переход по стеку назад
                    }
                }
        }
    }
}


