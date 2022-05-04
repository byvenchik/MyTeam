package com.example.myteam.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.database.CURRENT_UID
import com.example.myteam.message_recycler_view.views.MessageView
import com.example.myteam.utilits.AppVoicePlayer
import com.example.myteam.utilits.asTime
import kotlinx.android.synthetic.main.message_item_voice.view.*

class HolderVoiceMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val mAppVoicePlayer = AppVoicePlayer()


    private val blocReceivedVoiceMessage: ConstraintLayout = view.bloc_received_voice_message
    private val blocUserVoiceMessage: ConstraintLayout = view.bloc_user_voice_message
    private val chatUserVoiceMessageTime: TextView = view.chat_user_voice_message_time
    private val chatReceivedVoiceMessageTime: TextView = view.chat_received_voice_message_time

    private val chatReceivedBtnPlay: ImageView = view.chat_received_btn_play
    private val chatReceivedBtnStop: ImageView = view.chat_received_btn_stop

    private val chatUserBtnPlay: ImageView = view.chat_user_btn_play
    private val chatUserBtnStop: ImageView = view.chat_user_btn_stop

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

    //Отслеживание появления холдера на экране
    override fun onAttach(view: MessageView) {
        mAppVoicePlayer.init()
        //Если соощение написали мы
        if (view.from == CURRENT_UID) {
            chatUserBtnPlay.setOnClickListener {
                chatUserBtnPlay.visibility = View.GONE
                chatUserBtnStop.visibility = View.VISIBLE
                chatUserBtnStop.setOnClickListener {
                    stop {
                        chatUserBtnStop.setOnClickListener(null)
                        chatUserBtnPlay.visibility = View.VISIBLE
                        chatUserBtnStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.GONE

                }
            }
        } else {
            chatReceivedBtnPlay.setOnClickListener {
                chatReceivedBtnPlay.visibility = View.GONE
                chatReceivedBtnStop.visibility = View.VISIBLE
                chatReceivedBtnStop.setOnClickListener {
                    stop {
                        chatReceivedBtnStop.setOnClickListener(null)
                        chatReceivedBtnPlay.visibility = View.VISIBLE
                        chatReceivedBtnStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatReceivedBtnPlay.visibility = View.VISIBLE
                    chatReceivedBtnStop.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        mAppVoicePlayer.play(view.id, view.fileUrl) {
            function()
        }
    }

    fun stop(function: () -> Unit) {
        mAppVoicePlayer.stop {
            function()
        }
    }

    //Отслеживание когда холдер ушел за экран
    override fun onDetach() {
        //Удаляем слушателей
        chatUserBtnPlay.setOnClickListener(null)
        chatReceivedBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()
    }

}