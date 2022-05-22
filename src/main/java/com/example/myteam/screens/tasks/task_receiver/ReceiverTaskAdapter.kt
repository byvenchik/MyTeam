package com.example.myteam.screens.tasks.task_receiver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.asDate
import com.example.myteam.utilits.asTime
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.task_receiver_item.view.*
import kotlinx.android.synthetic.main.task_receiver_item.view.bloc_fake_message
import kotlinx.android.synthetic.main.task_receiver_item.view.chat_fake_message
import kotlinx.android.synthetic.main.task_receiver_item.view.chat_fake_message_time

class ReceiverTaskAdapter : RecyclerView.Adapter<ReceiverTaskAdapter.ReceiverHolder>() {

    private var mListMessagesCache = emptyList<CommonModel>()


    class ReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blocReceiverMessage: ConstraintLayout = view.bloc_receiver_task
        val photoReceiverTask: CircleImageView = view.receiverTaskContactPhoto
        val fullnameReceiverTask: TextView = view.receiverTaskFullName
        val nameReceiverTask: TextView = view.receiverTaskNameTask
        val statusReceiverTask: TextView = view.receiverTaskStatus

        //Для передачи
        val dateReceiverTask: TextView = view.receiverTaskDate
        val timeReceiverTask: TextView = view.receiverTaskTime
        val idSender:TextView = view.idSender
        val descriptionReceiverTask: TextView = view.receiverTaskDescriptionTask
        val idReceiverTask:TextView = view.receiverTaskID
        val receiverPhotoID:TextView = view.receiverphotoID
        val blocFakeMessage: ConstraintLayout = view.bloc_fake_message
        val chatFakeMessage: TextView = view.chat_fake_message
        val chatFakeMessageTime: TextView = view.chat_fake_message_time

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_receiver_item, parent, false)
        return ReceiverHolder(view)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    override fun onBindViewHolder(holder: ReceiverHolder, position: Int) {

        if (mListMessagesCache[position].from == CURRENT_UID) {
            holder.blocFakeMessage.visibility = View.VISIBLE
            holder.blocReceiverMessage.visibility = View.GONE
            holder.chatFakeMessage.text = mListMessagesCache[position].text
            holder.chatFakeMessageTime.text = mListMessagesCache[position].timeStamp.toString().asTime()
        } else {
            holder.blocReceiverMessage.visibility = View.GONE
            holder.blocReceiverMessage.visibility = View.VISIBLE
            holder.nameReceiverTask.text = mListMessagesCache[position].task
            holder.timeReceiverTask.text = mListMessagesCache[position].timeStamp.toString().asTime()
            holder.dateReceiverTask.text = mListMessagesCache[position].timeStamp.toString().asDate()
            holder.idReceiverTask.text = mListMessagesCache[position].task_id
            holder.descriptionReceiverTask.text = mListMessagesCache[position].description
            holder.fullnameReceiverTask.text = mListMessagesCache[position].from_username
            holder.statusReceiverTask.text = mListMessagesCache[position].status_task
            holder.receiverPhotoID.text = mListMessagesCache[position].from_photo
            holder.photoReceiverTask.downloadAndSetImage(mListMessagesCache[position].from_photo)
            holder.idSender.text = mListMessagesCache[position].from
        }

/*        if(holder.statusSenderTask.text  == "Отправлена") {
            holder.statusSenderTask.visibility = View.GONE
        }*/

        holder.blocReceiverMessage.setOnClickListener {
            val task_id :String =  mListMessagesCache[position].task_id
            val task_message :String =  mListMessagesCache[position].task
            val task_description:String = mListMessagesCache[position].description
            val task_date: String = mListMessagesCache[position].timeStamp.toString().asDate()
            val task_time :String =  mListMessagesCache[position].timeStamp.toString().asTime()
            val task_received:String = mListMessagesCache[position].from_username
            val task_status:String = mListMessagesCache[position].status_task
            val photoID:String = mListMessagesCache[position].from_photo
            val idSender:String = mListMessagesCache[position].from


            replaceFragment(DetailsReceiverFragment(task_id,task_message,task_description,task_date, task_time,task_received,photoID, task_status, idSender))

        }
    }
    fun setListReceiver(list: List<CommonModel>) {
        mListMessagesCache = list
        notifyDataSetChanged()
    }
}