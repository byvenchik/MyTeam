package com.example.myteam.fragments.single_chat

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myteam.R
import com.example.myteam.fragments.BaseFragment
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.example.myteam.utilits.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
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
    private lateinit var mMessagesListener: AppChildEventListener  //Было двойное обновление, будем дочернюю ноду слушать

    //Переменная для необходимого количества подгруженных сообщений
    private var mCountMessages = 10

    //Для отслеживания состояния RecycleView
    private var mIsScrolling = false

    //Контролировать перемещения
    private var mSmoothScrollToPosition = true

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mLayoutManager: LinearLayoutManager


    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecycleView()

    }

    private fun initFields() {
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        chat_input_message.addTextChangedListener(AppTextWatcher{
            val string = chat_input_message.text.toString()
            if(string.isEmpty()){
                chat_btn_send_message.visibility = View.GONE
                chat_btn_attach.visibility = View.VISIBLE
            } else {
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
            }
        })

        chat_btn_attach.setOnClickListener { attachFile() }
    }

    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1, 1)    //Чтобы окно было пропорционально
            .setRequestedSize(250, 250)  //Размер картинки
            .start(APP_ACTIVITY, this)
    }

    private fun initRecycleView() {
        mRecyclerView = chat_recycle_view
        mAdapter = SingleChatAdapter()
        //Ссылка на сообщение
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        //Указываем, что все холдеры будут одного размера
        mRecyclerView.setHasFixedSize(true)
        //Скрулла нет, отключаем
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        //Анонимный класс
        mMessagesListener = AppChildEventListener {
            //Получим сообщение отдельным элементом
            val message = it.getCommonModel()
            //Если правда, то нужно опуститься вниз
            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(message) {
                    //Чтобы двигался вниз recycleView
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(message) {
                    //Отключил
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }


        //Подключили слушатель к ссылке с ограничением последнего кол-ва сообщений
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

        //Подгрузка следующих сообщений
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            //Отрабатывает когда произошло изменение
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //Пулл
                println(mRecyclerView.recycledViewPool.getRecycledViewCount(0))

                //Если есть движения вверх по dy, то делаем обновление
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            //Здесь можно проверить какое состояние на данный момент в RecycleView
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //Какое на данный момент состояние
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {//Состояние когда пользователь начинает движение
                    mIsScrolling = true
                }
            }

        })
        //Коснулись за резинку и пошло обновление
        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        //Удалим старого слушателя
        mRefMessages.removeEventListener(mMessagesListener)
        //Подключим нового слушателя
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
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
            mSmoothScrollToPosition = true  //При отправке возврат вниз
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

    //Активность которая запускается для получения картинки для фото пользователя
    //Перехват фото-активити
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {  //Тогда нам нужен URI
            val uri = CropImage.getActivityResult(data).uri
            val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
                .child(contact.id).push().key.toString()//Получили ключ

            val path = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_IMAGE)
                .child(messageKey)
            //Функции высшего порядка
            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    sendMessageAsImage(contact.id,it,messageKey)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //Делаем его опять невидимым
        mToolbarInfo.visibility = View.GONE
        //Отключить слушатель
        mRefUser.removeEventListener(mListenerInfoToolbar)
        //Чтобы избежать утечек памяти, удалил
        mRefMessages.removeEventListener(mMessagesListener)
    }
}