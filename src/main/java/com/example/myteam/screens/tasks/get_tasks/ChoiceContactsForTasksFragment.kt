package com.example.myteam.screens.tasks.get_tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ChoiceContactsForTasksFragment : Fragment(R.layout.fragment_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    //Адаптер от Firebase
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    //Нужна ссылка от куда мы будем скачивать наши данные
    private lateinit var mRefContacts: DatabaseReference
    //Ссылка на User
    private lateinit var mRefUser: DatabaseReference
    //Исправлял утечку памяти из-за слушателей
    private lateinit var mRefUserListener: AppValueEventListener
    //Для того чтобы собрать все слушатели <ключ,значение>
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        APP_ACTIVITY.title = "Выберите пользователя"
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = contacts_recycle_view
        //Ссылка на телефонный номер в БД
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            //Задаем запрос
            .setQuery(mRefContacts, CommonModel::class.java)    //CommonModel - здесь сохраняем
            .build()

        //Будет принимать options, надо указать какие данные он должен получить
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            //Отрабатывает тогда, когда адаптер получает доступ к View
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            //Заполняет холдер
            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                //Надо попасть в тот id, который идет с моделью
                mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                mRefUserListener = AppValueEventListener {
                    val contact = it.getCommonModel()

                    //Проверка пустых имен через записную книжку
                    if (contact.fullname.isEmpty()) {
                        holder.name.text = model.fullname   //Тот который приняли из тел. книги
                    } else holder.name.text = contact.fullname  //Из БД

                    holder.status.text = contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    //Установим на каждый контакт слушатель клика
                    holder.itemView.setOnClickListener { replaceFragment(GetTaskFragment(contact)) }
                }
                mRefUser.addValueEventListener(mRefUserListener)
                mapListeners[mRefUser] = mRefUserListener
            }
        }
        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.contact_fullname
        val status: TextView = view.contact_status
        val photo: CircleImageView = view.contact_photo
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        println()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
        println()
    }
}

