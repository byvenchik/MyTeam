package com.example.myteam.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.database.CHILD_USERNAME
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.database.NODE_USERNAMES
import com.example.myteam.database.REF_DATABASE_ROOT
import com.example.myteam.message_recycler_view.views.MessageView
import com.example.myteam.utilits.asTime
import kotlinx.android.synthetic.main.message_item_text.view.*

class HolderTextMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val blocUserMessage: ConstraintLayout = view.bloc_user_message
    private val chatUserMessage: TextView = view.chat_user_message
    private val chatUserMessageTime: TextView = view.chat_user_message_time

    private val blocReceivedMessage: ConstraintLayout = view.bloc_received_message
    private val chatReceivedMessage: TextView = view.chat_received_message
    private val chatReceivedMessageTime: TextView = view.chat_received_message_time


    override fun drawMessage(view: MessageView) {
        //Если сообщение от текущего пользователя
        if (view.from == CURRENT_UID) {
            //То мы его будем отражать в блоке юзера
            blocUserMessage.visibility = View.VISIBLE
            blocReceivedMessage.visibility = View.GONE
            //То есть сообщение отправителя видимое, получателя нет

            chatUserMessage.text = view.text
            chatUserMessageTime.text = view.timeStamp.asTime()


        } else {
            //Если сообщение не принадлежит текущему пользователю
            blocUserMessage.visibility = View.GONE
            blocReceivedMessage.visibility = View.VISIBLE
            //То есть сообщение отправителя видимое, получателя нет

            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text = view.timeStamp.asTime()
        }
    }

    //Отслеживание появления холдера на экране
    override fun onAttach(view: MessageView) {

    }

    //Отслеживание когда холдер ушел за экран
    override fun onDetach() {

    }

}