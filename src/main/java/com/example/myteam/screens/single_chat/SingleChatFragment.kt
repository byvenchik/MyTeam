package com.example.myteam.screens.single_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.screens.BaseFragment
import com.example.myteam.message_recycler_view.views.AppViewFactory
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.example.myteam.utilits.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.choice_upload.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private lateinit var mAppVoiceRecorder: AppVoiceRecorder

    //Выдвижное меню выбора передачи файла
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>


    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecycleView()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_choice)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN   //Изначально скроем

        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        chat_input_message.addTextChangedListener(AppTextWatcher {
            val string = chat_input_message.text.toString()
            if (string.isEmpty()) {
                chat_btn_send_message.visibility = View.GONE
                chat_btn_attach.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
            } else {
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
            }
        })

        chat_btn_attach.setOnClickListener { attach() }

        CoroutineScope(Dispatchers.IO).launch {
            //Следит за зажимом
            chat_btn_voice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        //TODO record
                        chat_btn_voice.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.colorGreen
                            )
                        )
                        val messageKey = getMessageKey(contact.id)
                        //Старт рекодера
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        //TODO stop record
                        chat_input_message.setText("")
                        chat_btn_voice.colorFilter = null
                        //Запись произошла, остановка рекодера
                        mAppVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                contact.id,
                                TYPE_MESSAGE_VOICE
                            )
                            //Опуститься на последний элемент
                            mSmoothScrollToPosition = true
                        }
                    }
                }
                true
            }
        }
    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED //Выводи наверх
        btn_attach_file.setOnClickListener { attachFile() }
        btn_attach_image.setOnClickListener { attachImage() }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" //Любой тип
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
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
                mAdapter.addItemToBottom(AppViewFactory.getView(message)) {
                    //Чтобы двигался вниз recycleView
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(AppViewFactory.getView(message)) {
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
        //Проверка
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(contact.id)//Получили ключ
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    //Опуститься на последний элемент
                    mSmoothScrollToPosition = true
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)//Получили ключ
                    val fileName = getFilenameFromUri(uri!!)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_FILE, fileName)
                    //Опуститься на последний элемент
                    mSmoothScrollToPosition = true
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

    override fun onDestroyView() {
        super.onDestroyView()
        //Удаляем рекордер
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }
}