package com.example.myteam.fragments.single_chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.fragments.message_recycler_view.view_holder.AppHolderFactory
import com.example.myteam.fragments.message_recycler_view.view_holder.HolderImageMessage
import com.example.myteam.fragments.message_recycler_view.view_holder.HolderTextMessage
import com.example.myteam.fragments.message_recycler_view.views.MessageView
import com.example.myteam.utilits.asTime
import com.example.myteam.utilits.downloadAndSetImage

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mlistMessagesCache = mutableListOf<MessageView>()


    //Создал холдер
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mlistMessagesCache[position].getTypeView()
    }

    //Здесь передаем размер нашего mlistMessagesCache
    override fun getItemCount(): Int = mlistMessagesCache.size


    //Отражает холдер
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HolderImageMessage -> drawMessageImage(holder, position)
            is HolderTextMessage -> drawMessageText(holder,position)
            else -> {
            }
        }
    }

    private fun drawMessageImage(holder: HolderImageMessage, position: Int) {

        //Если сообщение от текущего пользователя
        if (mlistMessagesCache[position].from == CURRENT_UID) {
            //То мы его будем отражать в блоке юзера
            //Холдер для изображений
            holder.blocReceivedImageMessage.visibility = View.GONE
            holder.blocUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mlistMessagesCache[position].fileUrl)
            holder.chatUserImageMessageTime.text =
                mlistMessagesCache[position].timeStamp.asTime()
        } else {
            //Холдер для изображений
            holder.blocReceivedImageMessage.visibility = View.VISIBLE
            holder.blocUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(mlistMessagesCache[position].fileUrl)
            holder.chatReceivedImageMessageTime.text =
                mlistMessagesCache[position].timeStamp.asTime()
        }
    }

    private fun drawMessageText(holder: HolderTextMessage, position: Int) {

        //Если сообщение от текущего пользователя
        if (mlistMessagesCache[position].from == CURRENT_UID) {
            //То мы его будем отражать в блоке юзера
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceivedMessage.visibility = View.GONE
            //То есть сообщение отправителя видимое, получателя нет

            holder.chatUserMessage.text = mlistMessagesCache[position].text
            holder.chatUserMessageTime.text =
                mlistMessagesCache[position].timeStamp.asTime()
        } else {
            //Если сообщение не принадлежит текущему пользователю
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceivedMessage.visibility = View.VISIBLE
            //То есть сообщение отправителя видимое, получателя нет

            holder.chatReceivedMessage.text = mlistMessagesCache[position].text
            holder.chatReceivedMessageTime.text =
                mlistMessagesCache[position].timeStamp.asTime()
        }
    }

    //Добавить вниз
    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        //Убираем дубликаты, если не содержит
        if (!mlistMessagesCache.contains(item)) {
            //Добавил в лист
            mlistMessagesCache.add(item)
            //Обновление элементов списка
            notifyItemInserted(mlistMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mlistMessagesCache.contains(item)) {
            //Добавил в лист
            mlistMessagesCache.add(item)
            //Сортировка
            mlistMessagesCache.sortBy { it.timeStamp.toString() }
            //Обновление элементов списка
            notifyItemInserted(0)
        }
        onSuccess()
    }
}

