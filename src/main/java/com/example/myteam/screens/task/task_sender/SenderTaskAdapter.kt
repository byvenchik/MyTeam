package com.example.myteam.screens.task.task_sender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.asTime
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.task_sender_item.view.*


class SenderTaskAdapter : RecyclerView.Adapter<SenderTaskAdapter.SenderHolder>() {

    private var mListMessagesCache = emptyList<CommonModel>()
    private lateinit var mRefMessages: DatabaseReference

    class SenderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blocSenderMessage: ConstraintLayout = view.bloc_sender_task
        val chatSenderMessage: TextView = view.message_sender_task
        val chatSenderMessageTime: TextView = view.message_time_sender_task

        val titleTask: TextView = view.sender_task_title
        val titleDescription: TextView = view.sender_task_title_description
        val titleReceivedTask: TextView = view.sender_task_title_received

        val taskDescription: TextView = view.message_sender_description
        val taskReceived: TextView = view.sender_task_received

        val blocReceiverMessage: ConstraintLayout = view.bloc_fake_message
        val chatReceiverMessage: TextView = view.chat_fake_message
        val chatReceiverMessageTime: TextView = view.chat_fake_message_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SenderHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_sender_item, parent, false)
        return SenderHolder(view)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    override fun onBindViewHolder(holder: SenderHolder, position: Int) {

        if (mListMessagesCache[position].from == CURRENT_UID) {
            holder.blocSenderMessage.visibility = View.VISIBLE
            holder.blocReceiverMessage.visibility = View.GONE
            holder.chatSenderMessage.text = mListMessagesCache[position].task
            holder.chatSenderMessageTime.text =
                mListMessagesCache[position].timeStamp.toString().asTime()

            holder.taskDescription.text = mListMessagesCache[position].description
            holder.taskReceived.text = mListMessagesCache[position].received
        } else {
            holder.blocSenderMessage.visibility = View.VISIBLE
            holder.blocReceiverMessage.visibility = View.GONE
            holder.chatReceiverMessage.text = mListMessagesCache[position].text
            holder.chatReceiverMessageTime.text =
                mListMessagesCache[position].timeStamp.toString().asTime()
        }
    }
    fun setListSender(list: List<CommonModel>) {
        mListMessagesCache = list
        notifyDataSetChanged()
    }
}