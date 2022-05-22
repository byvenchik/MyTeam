package com.example.myteam.screens.group_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.screens.base.BaseFragment
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.AppValueEventListener
import com.example.myteam.utilits.TYPE_GROUP
import com.example.myteam.utilits.hideKeyboard
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_group_list.*


class GroupListFragment : Fragment(R.layout.fragment_group_list) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: GroupListAdapter
    private val mRefChatsList = REF_DATABASE_ROOT.child(NODE_GROUP_LIST).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        APP_ACTIVITY.title = "Команда"
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = group_list_recycle_view
        mAdapter = GroupListAdapter()

        //1 запрос
        mRefChatsList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->

                //2 запрос
                REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getCommonModel()

                        //3 запрос
                        REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                            .limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                                val tempList = dataSnapshot2.children.map { it.getCommonModel() }

                                if (tempList.isEmpty()) {
                                    newModel.lastMessage = "Сообщений пока нет. Начните общение!"
                                } else {
                                    newModel.lastMessage = tempList[0].text
                                }
                                newModel.type = TYPE_GROUP
                                mAdapter.updateListItems(newModel)
                            })
                    })
            }
        })
        mRecyclerView.adapter = mAdapter
    }
}