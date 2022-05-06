package com.example.myteam.screens.group_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.screens.single_chat.SingleChatFragment
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.replaceFragment
import com.example.myteam.utilits.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacts_item.view.*
import kotlinx.android.synthetic.main.chat_list_item.view.*

class AddContactsAdapter: RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private var listItems = mutableListOf<CommonModel>()    //Для отображения

    class AddContactsHolder(view: View): RecyclerView.ViewHolder(view){
        val itemName:TextView = view.add_contacts_item_name
        val itemLastMessage:TextView = view.add_contacts_last_message
        val itemPhoto:CircleImageView = view.add_contacts_item_photo
        val itemChoice:CircleImageView = view.add_contacts_item_choice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item, parent, false)
        //Клики
        val holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener {
            if(listItems[holder.adapterPosition].choice){
                holder.itemChoice.visibility = View.INVISIBLE
                listItems[holder.adapterPosition].choice = false
                AddContactsFragment.listContacts.remove(listItems[holder.adapterPosition])
            } else {
                //Ставим галочку
                holder.itemChoice.visibility = View.VISIBLE
                listItems[holder.adapterPosition].choice = true
                AddContactsFragment.listContacts.add(listItems[holder.adapterPosition])
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item:CommonModel){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}