package com.example.myteam.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.message_recycler_view.views.MessageView
import com.example.myteam.utilits.asTime
import kotlinx.android.synthetic.main.message_item_voice.view.*

class HolderVoiceMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    val blocReceivedVoiceMessage: ConstraintLayout = view.bloc_received_voice_message
    val blocUserVoiceMessage: ConstraintLayout = view.bloc_user_voice_message
    val chatUserVoiceMessageTime: TextView = view.chat_user_voice_message_time
    val chatReceivedVoiceMessageTime: TextView = view.chat_received_voice_message_time

    val chatReceivedBtnPlay: ImageView = view.chat_received_btn_play
    val chatReceivedBtnStop: ImageView = view.chat_received_btn_stop

    val chatUserBtnPlay: ImageView = view.chat_user_btn_play
    val chatUserBtnStop: ImageView = view.chat_user_btn_stop

    //Логика отрисовки
    override fun drawMessage(view: MessageView) {
        //Если сообщение от текущего пользователя
        if (view.from == CURRENT_UID) {
            //То мы его будем отражать в блоке юзера
            blocReceivedVoiceMessage.visibility = View.GONE
            blocUserVoiceMessage.visibility = View.VISIBLE
            chatUserVoiceMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blocReceivedVoiceMessage.visibility = View.VISIBLE
            blocUserVoiceMessage.visibility = View.GONE
            chatReceivedVoiceMessageTime.text =
                view.timeStamp.asTime()
        }
    }

}