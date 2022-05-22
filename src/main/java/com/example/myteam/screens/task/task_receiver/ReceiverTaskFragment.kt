package com.example.myteam.screens.task.task_receiver

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.AppValueEventListener
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_task_receiver.*

class ReceiverTaskFragment()  : Fragment(R.layout.fragment_task_receiver) {

    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: ReceiverTaskAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppValueEventListener
    private var mListMessages = emptyList<CommonModel>()


    override fun onResume() {
        super.onResume()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = receiver_task_recycle_view
        mAdapter = ReceiverTaskAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_RECEIVER).child(CURRENT_UID)//.child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppValueEventListener { dataSnapshot ->
            mListMessages = dataSnapshot.children.map { it.getCommonModel() }
            mAdapter.setListReceiver(mListMessages)
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
        }
        mRefMessages.addValueEventListener(mMessagesListener)
       // mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)

    }

    override fun onPause() {
        super.onPause()
        mRefMessages.removeEventListener(mMessagesListener)
    }
}