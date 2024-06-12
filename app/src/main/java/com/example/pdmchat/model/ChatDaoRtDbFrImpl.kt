package com.example.pdmchat.model

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatDaoRtDbFrImpl : ChatDao {
    companion object {
        private const val CHAT_LIST_ROOT_NODE = "chat"
    }

    private val chatRtDbFbReference = Firebase.database.getReference(CHAT_LIST_ROOT_NODE)
    private var isFirstValueEvent = true


    private val chatList = mutableListOf<Chat>()

    init {

        chatRtDbFbReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chat = snapshot.getValue(Chat::class.java)
                if (chat != null && !chatList.contains(chat)) {
                    chatList.add(chat)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chat = snapshot.getValue(Chat::class.java)
                if (chat != null) {
                    val index = chatList.indexOfFirst { it.id == chat.id }
                    if (index != -1) {
                        chatList[index] = chat
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chat = snapshot.getValue(Chat::class.java)
                if (chat != null) {
                    chatList.remove(chat)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // NSA
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })

        chatRtDbFbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isFirstValueEvent) {
                    isFirstValueEvent = false
                    val chats = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
                    if (chats.isNotEmpty()) {
                        chatList.addAll(chats.filterNot { chatList.contains(it) })
                        for (chat in chats) {
                            println(chat)
                        }
                    } else {
                        println("Nenhuma mensagem encontrada.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })
    }

    override fun createChat(chat: Chat): Int {
        val newChatRef = chatRtDbFbReference.push()
        val id = newChatRef.key ?: return -1
        chat.id = id
        newChatRef.setValue(chat)
        Log.d("Firebase", "Enviando chat para o Firebase: $chat")
        return 1
    }

    override fun retrieveChats(): MutableList<Chat> {
        return chatList
    }

    private fun createOrUpdateChat(chat: Chat) {
        Log.d("Firebase", "Atualizando chat no banco de dados: $chat")
        chatRtDbFbReference.child(chat.id.toString()).setValue(chat)
    }
}
