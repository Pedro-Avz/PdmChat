package com.example.pdmchat.controller

import android.util.Log
import android.os.Message
import com.example.pdmchat.ui.MainActivity
import com.example.pdmchat.model.ChatDao
import com.example.pdmchat.model.ChatDaoRtDbFrImpl


class ChatRtDbFrController (private val mainActivity: MainActivity) {
        private val chatDaoImpl: ChatDao = ChatDaoRtDbFrImpl()

    fun getChats() {
        Log.d("ChatRtDbFrController", "Iniciando obtenção de chats do Firebase")
        val chatList = chatDaoImpl.retrieveChats()
        Log.d("ChatRtDbFrController", "Chats obtidos do Firebase: $chatList")

        if (chatList.isNotEmpty()) {
            val sortedChatList = chatList.sortedByDescending { it.getDateTime() }
            chatList.clear()
            chatList.addAll(sortedChatList)
            mainActivity.uiUpdaterHandler.sendMessage(
                Message.obtain().apply {
                    data.putParcelableArrayList(
                        "CHAT_ARRAY",
                        ArrayList(sortedChatList)
                    )
                }
            )
            Log.d("ChatRtDbFrController", "Encontrados ${sortedChatList.size} chats no Firebase")
        } else {
            Log.d("ChatRtDbFrController", "Nenhum chat encontrado no Firebase")
        }
    }

}
