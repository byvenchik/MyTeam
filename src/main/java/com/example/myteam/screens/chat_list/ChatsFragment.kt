package com.example.myteam.screens.chat_list

import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.screens.BaseFragment
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.AppValueEventListener
import com.example.myteam.utilits.TYPE_MESSAGE_VOICE
import kotlinx.android.synthetic.main.fragment_chats.*


class ChatsFragment : BaseFragment(R.layout.fragment_chats) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ChatsListAdapter
    private val mRefChatsList = REF_DATABASE_ROOT.child(NODE_CHAT_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Чаты"
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_list_recycle_view
        mAdapter = ChatsListAdapter()

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
                                newModel.lastMessage = tempList[0].text

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

}