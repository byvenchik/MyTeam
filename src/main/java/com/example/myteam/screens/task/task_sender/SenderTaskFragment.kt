package com.example.myteam.screens.task.task_sender

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.AppValueEventListener
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_task_sender.*

class SenderTaskFragment : Fragment(R.layout.fragment_task_sender) {

    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SenderTaskAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppValueEventListener
    private var mListMessages = emptyList<CommonModel>()


    override fun onResume() {
        super.onResume()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = sender_task_recycle_view
        mAdapter = SenderTaskAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_SENDER)
            .child(CURRENT_UID)//.child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppValueEventListener { dataSnapshot ->
            mListMessages = dataSnapshot.children.map { it.getCommonModel() }
            mAdapter.setListSender(mListMessages)
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