package com.example.myteam.fragments

import android.view.View
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.APP_ACTIVITY
import kotlinx.android.synthetic.main.activity_main.view.*

class SingleChatFragment(contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {

    override fun onResume() {
        super.onResume()
        //Скрывали его в маин в xml надо сделать видимым
        APP_ACTIVITY.mToolbar.toolbar_info.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        //Делаем его опять невидимым
        APP_ACTIVITY.mToolbar.toolbar_info.visibility = View.GONE
    }
}