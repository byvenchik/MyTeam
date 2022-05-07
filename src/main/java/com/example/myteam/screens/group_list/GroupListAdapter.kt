package com.example.myteam.screens.group_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.screens.single_chat.SingleChatFragment
import com.example.myteam.utilits.TYPE_CHAT
import com.example.myteam.utilits.TYPE_GROUP
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.chat_list_item.view.*

class GroupListAdapter: RecyclerView.Adapter<GroupListAdapter.GroupListHolder>() {

    private var listItems = mutableListOf<CommonModel>()    //Для отображения

    class GroupListHolder(view: View): RecyclerView.ViewHolder(view){
        val itemName:TextView = view.chat_list_item_name
        val itemLastMessage:TextView = view.chat_list_item_last_message
        val itemPhoto:CircleImageView = view.chat_list_item_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        //Клики
        val holder = GroupListHolder(view)
        holder.itemView.setOnClickListener {
            when(listItems[holder.adapterPosition].type){
                TYPE_GROUP ->replaceFragment(GroupChatFragment(listItems[holder.adapterPosition]))
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: GroupListHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item:CommonModel){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}