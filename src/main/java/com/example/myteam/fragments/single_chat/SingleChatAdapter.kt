package com.example.myteam.fragments.single_chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.fragments.message_recycler_view.view_holder.AppHolderFactory
import com.example.myteam.fragments.message_recycler_view.view_holder.HolderImageMessage
import com.example.myteam.fragments.message_recycler_view.view_holder.HolderTextMessage
import com.example.myteam.fragments.message_recycler_view.view_holder.HolderVoiceMessage
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
            is HolderImageMessage -> holder.drawMessageImage(holder, mlistMessagesCache[position])
            is HolderTextMessage -> holder.drawMessageText(holder,mlistMessagesCache[position])
            is HolderVoiceMessage -> holder.drawMessageVoice(holder,mlistMessagesCache[position])
            else -> {
            }
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

