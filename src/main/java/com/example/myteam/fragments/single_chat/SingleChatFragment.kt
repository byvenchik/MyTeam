package com.example.myteam.fragments.single_chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.fragments.BaseFragment
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.example.myteam.utilits.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
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

    //Нужно получить ссылку на сообщение
    private lateinit var mRefMessages: DatabaseReference

    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView

    //Чтобы избегать утечки памяти нужен объект слушателя
    private lateinit var mMessagesListener: AppValueEventListener

    //Лист для передачи
    private var mListMessages = emptyList<CommonModel>()


    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = chat_recycle_view
        mAdapter = SingleChatAdapter()
        //Ссылка на сообщение
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppValueEventListener { dataSnapshot ->
            mListMessages =
                dataSnapshot.children.map { it.getCommonModel() }//Способ получить все ноды
            //Как только получили наше значение
            mAdapter.setList(mListMessages)

            //Чтобы двигался recycleView
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
        }
        //Подключили слушатель к ссылке
        mRefMessages.addValueEventListener(mMessagesListener)
    }

    private fun initToolbar() {
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
        //Слушатель на кнопку отправки сообщения
        chat_btn_send_message.setOnClickListener {
            //Текст сообщения
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessage(message, contact.id, TYPE_TEXT) {
                chat_input_message.setText("")
            }
        }
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
        //Чтобы избежать утечек памяти
        mRefMessages.removeEventListener(mMessagesListener)
    }
}