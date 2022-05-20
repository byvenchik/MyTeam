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
import com.example.myteam.utilits.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.task_sender_item.view.*


class SenderTaskAdapter : RecyclerView.Adapter<SenderTaskAdapter.SenderHolder>() {

    private var mListMessagesCache = emptyList<CommonModel>()


    class SenderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blocSenderMessage: ConstraintLayout = view.bloc_sender_task
        val photoSenderTask: CircleImageView = view.senderTaskContactPhoto
        val fullnameSenderTask: TextView = view.senderTaskFullName
        val nameSenderTask: TextView = view.senderTaskNameTask
        val statusSenderTask: TextView = view.senderTaskStatus

        //Для передачи
        val dateSenderTask: TextView = view.senderTaskDate
        val timeSenderTask: TextView = view.senderTaskTime
        val descriptionSenderTask: TextView = view.senderTaskDescriptionTask
        val idSenderTask:TextView = view.senderTaskID
        val photoID:TextView = view.photoID
        val idReceiver:TextView = view.idReceiver
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
            holder.blocReceiverMessage.visibility = View.GONE
            holder.blocSenderMessage.visibility = View.VISIBLE
            holder.nameSenderTask.text = mListMessagesCache[position].task
            holder.timeSenderTask.text = mListMessagesCache[position].timeStamp.toString().asTime()
            holder.dateSenderTask.text = mListMessagesCache[position].timeStamp.toString().asDate()
            holder.idSenderTask.text = mListMessagesCache[position].task_id
            holder.descriptionSenderTask.text = mListMessagesCache[position].description
            holder.fullnameSenderTask.text = mListMessagesCache[position].received
            holder.statusSenderTask.text = mListMessagesCache[position].status_task
            holder.photoID.text = mListMessagesCache[position].received_photo
            holder.photoSenderTask.downloadAndSetImage(mListMessagesCache[position].received_photo)
            holder.idReceiver.text = mListMessagesCache[position].received_id


        } else {
            holder.blocSenderMessage.visibility = View.VISIBLE
            holder.blocReceiverMessage.visibility = View.GONE
            holder.chatReceiverMessage.text = mListMessagesCache[position].text
            //holder.chatReceiverMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        }

/*        if(holder.statusSenderTask.text  == "Отправлена") {
            holder.statusSenderTask.visibility = View.GONE
        }*/

        holder.blocSenderMessage.setOnClickListener {
            val task_id :String =  mListMessagesCache[position].task_id
            val task_message :String =  mListMessagesCache[position].task
            val task_description:String = mListMessagesCache[position].description
            val task_date: String = mListMessagesCache[position].timeStamp.toString().asDate()
            val task_time :String =  mListMessagesCache[position].timeStamp.toString().asTime()
            val task_received:String = mListMessagesCache[position].received
            val task_status:String = mListMessagesCache[position].status_task
            val photoID:String = mListMessagesCache[position].received_photo
            val idReceived:String = mListMessagesCache[position].received_id


            replaceFragment(DetailsSenderFragment(task_id,task_message,task_description,task_date, task_time,task_received,photoID, task_status,idReceived))

        }
    }
    fun setListSender(list: List<CommonModel>) {
        mListMessagesCache = list
        notifyDataSetChanged()
    }
}