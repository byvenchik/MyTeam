package com.example.myteam.screens.group_list

import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.screens.base.BaseFragment
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.AppValueEventListener
import com.example.myteam.utilits.replaceFragment
import com.example.myteam.utilits.showToast
import kotlinx.android.synthetic.main.fragment_add_contacts.*


class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefChatsList = REF_DATABASE_ROOT.child(NODE_CHAT_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        listContacts.clear()    //Очистка списка добавленных контактов
        APP_ACTIVITY.title = "Добавить участника"
        initRecyclerView()
        add_contacts_btn_next.setOnClickListener {
            if (listContacts.isEmpty()) {
                showToast("Добавьте участника")
            } else {
                replaceFragment(CreateGroupFragment(listContacts))
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = add_contacts_recycle_view
        mAdapter = AddContactsAdapter()

        //1 запрос
        mRefChatsList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->

                //2 запрос
                mRefUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getCommonModel()

                        //3 запрос
                        mRefMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                                val tempList = dataSnapshot2.children.map { it.getCommonModel() }

                                if (tempList.isEmpty()) {
                                    newModel.lastMessage = "История сообщений была удалена"
                                } else {
                                    newModel.lastMessage = tempList[0].text
                                }
                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                mAdapter.updateListItems(newModel)
                            })
                    })
            }
        })
        mRecyclerView.adapter = mAdapter
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }
}