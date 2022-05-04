package com.example.myteam.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.message_recycler_view.view_holders.*
import com.example.myteam.message_recycler_view.views.MessageView

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mlistMessagesCache = mutableListOf<MessageView>()

    private var mListHolders = mutableListOf<MessageHolder>()


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
        (holder as MessageHolder).drawMessage(mlistMessagesCache[position])
    }

    //Функция отрабатывает, когда холдер появляется на экране
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(mlistMessagesCache[holder.adapterPosition])
        mListHolders.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        mListHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
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

    //Отрабатывает когда слушали голосовые и вышли из чата
    fun onDestroy() {
        mListHolders.forEach{
            it.onDetach()
        }
    }
}

