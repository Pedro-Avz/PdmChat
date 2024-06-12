package com.example.pdmchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.pdmchat.model.Chat
import com.example.pdmchat.R

class ChatAdapter(context: Context, private val chatList: MutableList<Chat>):
    ArrayAdapter<Chat>(context, R.layout.tile_chat, chatList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val chat = chatList[position]
        val chatTileView: View
        val holder: TileContactHolder

        if (convertView == null) {
            chatTileView = LayoutInflater.from(context).inflate(R.layout.tile_chat, parent, false)
            holder = TileContactHolder(
                chatTileView.findViewById(R.id.nameTv),
                chatTileView.findViewById(R.id.chatTv),
                chatTileView.findViewById(R.id.dateTimeTv)
            )
            chatTileView.tag = holder
        } else {
            chatTileView = convertView
            holder = chatTileView.tag as TileContactHolder
        }

        holder.apply {
            nameTv.text = chat.remetente
            chatTv.text = chat.chat
            dateTimeTv.text = "${chat.data} ${chat.horario}"
        }

        return chatTileView
    }

    private data class TileContactHolder(
        val nameTv: TextView,
        val chatTv: TextView,
        val dateTimeTv: TextView
    )
}
