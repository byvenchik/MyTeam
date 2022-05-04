package com.example.myteam.message_recycler_view.view_holders

import com.example.myteam.message_recycler_view.views.MessageView

interface MessageHolder {
    fun drawMessage(view:MessageView)

    //Отслеживание появления холдера на экране
    fun onAttach(view: MessageView)

    //Отслеживание когда холдер ушел за экран
    fun onDetach()
}