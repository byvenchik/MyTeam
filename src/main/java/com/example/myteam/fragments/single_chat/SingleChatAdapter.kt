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

    private var mlistMessagesCache = emptyList<CommonModel>()
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



    fun addItem(item: CommonModel){
        val newList = mutableListOf<CommonModel>()
        newList.addAll(mlistMessagesCache)
        //Проверка если не повторяется -> добавляем
        if(!newList.contains(item)) newList.add(item)
        //Сортировка
        newList.sortBy { it.timeStamp.toString() }
        //Старый и новый лист
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(mlistMessagesCache, newList))
        //Проверили, что все хорошо и делаем перерисовку разных элементов
        mDiffResult.dispatchUpdatesTo(this)
        mlistMessagesCache = newList
        //Покажем, что данные изменены и их надо обработать
    }
}

