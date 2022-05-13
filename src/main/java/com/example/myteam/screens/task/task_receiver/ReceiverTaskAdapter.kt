package com.example.myteam.screens.task.task_receiver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.asTime
import kotlinx.android.synthetic.main.task_receiver_item.view.*

class ReceiverTaskAdapter : RecyclerView.Adapter<ReceiverTaskAdapter.ReceiverHolder>() {

    private var mListMessagesCache = emptyList<CommonModel>()

    class ReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blocSenderReceiverMessage: ConstraintLayout = view.bloc_receiver_sender_task
        val chatSenderReceiverMessage: TextView = view.receiver_message_sender_task
        val chatSenderReceiverMessageTime: TextView = view.receiver_message_time_sender_task

        val blocReceiverMessage: ConstraintLayout = view.bloc_receiver_received_task
        val chatReceiverMessage: TextView = view.receiver_message_receiver_task
        val chatReceiverMessageTime: TextView = view.receiver_message_time_receiver_task
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_receiver_item, parent, false)
        return ReceiverHolder(view)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    override fun onBindViewHolder(holder: ReceiverHolder, position: Int) {
        if (mListMessagesCache[position].from == CURRENT_UID) {
            holder.blocSenderReceiverMessage.visibility = View.GONE
            holder.blocReceiverMessage.visibility = View.GONE
            holder.chatSenderReceiverMessage.text = mListMessagesCache[position].text
            holder.chatSenderReceiverMessageTime.text =
                mListMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blocSenderReceiverMessage.visibility = View.GONE
            holder.blocReceiverMessage.visibility = View.VISIBLE
            holder.chatReceiverMessage.text = mListMessagesCache[position].text
            holder.chatReceiverMessageTime.text =
                mListMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    fun setListReceiver(list: List<CommonModel>) {
        mListMessagesCache = list
        notifyDataSetChanged()
    }
}