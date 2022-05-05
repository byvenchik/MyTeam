package com.example.myteam.screens.chat_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.utilits.downloadAndSetImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.chat_list_item.view.*

class ChatsListAdapter: RecyclerView.Adapter<ChatsListAdapter.ChatsListHolder>() {

    private var listItems = mutableListOf<CommonModel>()    //Для отображения

    class ChatsListHolder(view: View): RecyclerView.ViewHolder(view){
        val itemName:TextView = view.chat_list_item_name
        val itemLastMessage:TextView = view.chat_list_item_last_message
        val itemPhoto:CircleImageView = view.chat_list_item_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ChatsListHolder(view)
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: ChatsListHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item:CommonModel){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}