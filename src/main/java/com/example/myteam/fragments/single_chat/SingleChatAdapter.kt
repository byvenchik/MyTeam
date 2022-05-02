package com.example.myteam.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.*
import kotlinx.android.synthetic.main.message_item.view.*

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var mlistMessagesCache = mutableListOf<CommonModel>()


    class SingleChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Для текстового сообщения
        //Для отправителя
        val blocUserMessage: ConstraintLayout = view.bloc_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_message_time

        //Для получателя
        val blocReceivedMessage: ConstraintLayout = view.bloc_received_message
        val chatReceivedMessage: TextView = view.chat_received_message
        val chatReceivedMessageTime: TextView = view.chat_received_message_time

        //Для изображений
        //Для отправителя
        val blocUserImageMessage: ConstraintLayout = view.bloc_user_image_message
        val chatUserImage: ImageView = view.chat_user_image
        val chatUserImageMessageTime: TextView = view.chat_user_message_time

        //Для получателя
        val blocReceivedImageMessage: ConstraintLayout = view.bloc_received_image_message
        val chatReceivedImage: ImageView = view.chat_received_image
        val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time
    }

    //Создал холдер
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    //Здесь передаем размер нашего mlistMessagesCache
    override fun getItemCount(): Int = mlistMessagesCache.size


    //Отражает холдер
    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when (mlistMessagesCache[position].type) {
            TYPE_MESSAGE_TEXT -> drawMessageText(holder, position)
            TYPE_MESSAGE_IMAGE -> drawMessageImage(holder, position)
        }
    }

    private fun drawMessageImage(holder: SingleChatHolder, position: Int) {

        //Блоки текстовых сообщений
        holder.blocUserMessage.visibility = View.GONE
        holder.blocReceivedMessage.visibility = View.GONE

        //Если сообщение от текущего пользователя
        if (mlistMessagesCache[position].from == CURRENT_UID) {
            //То мы его будем отражать в блоке юзера
            //Холдер для изображений
            holder.blocReceivedImageMessage.visibility = View.GONE
            holder.blocUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mlistMessagesCache[position].fileUrl)
            holder.chatUserImageMessageTime.text = mlistMessagesCache[position].timeStamp.toString().asTime()
        } else {
            //Холдер для изображений
            holder.blocReceivedImageMessage.visibility = View.VISIBLE
            holder.blocUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(mlistMessagesCache[position].fileUrl)
            holder.chatReceivedImageMessageTime.text = mlistMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    private fun drawMessageText(holder: SingleChatHolder, position: Int) {
        //Отключил изображения
        holder.blocReceivedImageMessage.visibility = View.GONE
        holder.blocUserImageMessage.visibility = View.GONE

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

    //Добавить вниз
    fun addItemToBottom(item: CommonModel, onSuccess: () -> Unit) {
        //Убираем дубликаты, если не содержит
        if (!mlistMessagesCache.contains(item)) {
            //Добавил в лист
            mlistMessagesCache.add(item)
            //Обновление элементов списка
            notifyItemInserted(mlistMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: CommonModel, onSuccess: () -> Unit) {
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

