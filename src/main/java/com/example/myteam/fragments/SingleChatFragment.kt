package com.example.myteam.fragments

import android.view.View
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.example.myteam.utilits.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.toolbar_info.view.*

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    //Слушатель изменений в toolbar (имя, статус)
    private lateinit var mListenerInfoToolbar: AppValueEventListener

    //Инициализация пользователя
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View

    //Ссылка для подключения данных
    private lateinit var mRefUser: DatabaseReference

    override fun onResume() {
        super.onResume()
        //Скрывали его в маин в xml надо сделать видимым
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE

        //При изменении в листенере -> обновляем юзера
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            //Обновим toolbar
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        //Слушатель
        mRefUser.addValueEventListener(mListenerInfoToolbar)
    }

    private fun initInfoToolbar() {

        //Провека
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.toolbar_chat_fullname.text = contact.fullname
        } else mToolbarInfo.toolbar_chat_fullname.text = mReceivingUser.fullname

        mToolbarInfo.toolbar_chat_image.downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.toolbar_chat_status.text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        //Делаем его опять невидимым
        mToolbarInfo.visibility = View.GONE
        //Отключить слушатель
        mRefUser.removeEventListener(mListenerInfoToolbar)
    }
}