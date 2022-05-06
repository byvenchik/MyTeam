package com.example.myteam.screens.settings

import com.example.myteam.R
import com.example.myteam.database.USER
import com.example.myteam.database.setBioToDatabase
import com.example.myteam.screens.base.BaseChangeFragment
import kotlinx.android.synthetic.main.fragment_change_bio.*

//Фрагмент для изменения информации о пользователе
class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = settings_input_bio.text.toString()
        setBioToDatabase(newBio)
    }
}

