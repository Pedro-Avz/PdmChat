package com.example.pdmchat.controller

import android.util.Log
import com.example.pdmchat.model.Chat
import com.example.pdmchat.model.ChatDao
import com.example.pdmchat.model.ChatDaoRtDbFrImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SaveRtDbFrController {
    private val chatDaoImpl: ChatDao = ChatDaoRtDbFrImpl()

    fun insertChat(chat: Chat) {
        GlobalScope.launch {
            chatDaoImpl.createChat(chat)
        }
        Log.d("Firebase", "Enviando chat para o Firebase: $chat")
    }

}