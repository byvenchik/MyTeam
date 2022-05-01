package com.example.myteam.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.CURRENT_UID
import com.example.myteam.utilits.DiffUtilCallback
import com.example.myteam.utilits.asTime
import kotlinx.android.synthetic.main.message_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var mlistMessagesCache = mutableListOf<CommonModel>()
    private lateinit var mDiffResult: DiffUtil.DiffResult   //Результат проверки


    class SingleChatHolder(view: View) : RecyclerView.ViewHolder(view) {

        //Для отправленного
        val blocUserMessage: ConstraintLayout = view.bloc_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_message_time

        //Для принятого
        val blocReceivedMessage: ConstraintLayout = view.bloc_received_message
        val chatReceivedMessage: TextView = view.chat_received_message
        val chatReceivedMessageTime: TextView = view.chat_received_message_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    //Здесь передаем размер нашего mlistMessagesCache
    override fun getItemCount(): Int = mlistMessagesCache.size

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        //Если сообщение от текущего пользователя
        if (mlistMessagesCache[position].from == CURRENT_UID) {
            //То мы его будем отражать в блоке юзера
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceivedMessage.visibility = View.GONE
            //То есть сообщение отправителя видимое, получателя нет

            holder.chatUserMessage.text = mlistMessagesCache[position].text
            holder.chatUserMessageTime.text =
                mlistMessagesCache[position].timeStamp.toString().asTime()
        } else {
            //Если сообщение не принадлежит текущему пользователю
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceivedMessage.visibility = View.VISIBLE
            //То есть сообщение отправителя видимое, получателя нет

            holder.chatReceivedMessage.text = mlistMessagesCache[position].text
            holder.chatReceivedMessageTime.text =
                mlistMessagesCache[position].timeStamp.toString().asTime()
        }
    }


    fun addItem(item: CommonModel,
                toBottom: Boolean,
    onSuccess:() -> Unit
    ) {
        //Если нужно вниз
        if (toBottom) {
            //Убираем дубликаты, если не содержит
            if (!mlistMessagesCache.contains(item)) {
                //Добавил в лист
                mlistMessagesCache.add(item)
                //Обновление элементов списка
                notifyItemInserted(mlistMessagesCache.size)
            }
        } else {
            if (!mlistMessagesCache.contains(item)) {
                //Добавил в лист
                mlistMessagesCache.add(item)
                //Сортировка
                mlistMessagesCache.sortBy { it.timeStamp.toString() }
                //Обновление элементов списка
                notifyItemInserted(0)
            }
        }
        onSuccess()
    }
}

