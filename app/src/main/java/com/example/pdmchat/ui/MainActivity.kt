package com.example.pdmchat.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.pdmchat.R
import com.example.pdmchat.adapter.ChatAdapter
import com.example.pdmchat.controller.ChatRtDbFrController
import com.example.pdmchat.databinding.ActivityMainBinding
import com.example.pdmchat.model.Chat

class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_CHAT = 1
        const val GET_CHATS_INTERVAL = 2000L
    }
    private lateinit var toolbar: Toolbar

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val chatList: MutableList<Chat> = mutableListOf()

    private val chatController: ChatRtDbFrController by lazy {
        ChatRtDbFrController(this)
    }

    private val chatAdapter: ChatAdapter by lazy {
        ChatAdapter(this, chatList)
    }

    private lateinit var usernameInput: EditText
    private lateinit var saveButton: View
    private lateinit var messageLv: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        usernameInput = findViewById(R.id.usernameInput)
        saveButton = findViewById(R.id.saveButton)
        messageLv = findViewById(R.id.messageLv)

        uiUpdaterHandler.apply {
            sendMessageDelayed(
                android.os.Message.obtain().apply {
                    what = GET_CHAT
                },
                GET_CHATS_INTERVAL
            )
        }

        amb.messageLv.adapter = chatAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_chat -> {
                val intent = Intent(this, SendChatActivity::class.java)
                intent.putExtra("remetente", usernameInput.text.toString().trim())
                startActivity(intent)
                true
            }
            R.id.exit_app -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun saveUsername(view: View) {
        val username = usernameInput.text.toString().trim()
        if (username.isNotEmpty()) {
            Toast.makeText(this, "Username salvo: $username", Toast.LENGTH_SHORT).show()
            messageLv.visibility = View.VISIBLE
            usernameInput.visibility = View.GONE
            saveButton.visibility = View.GONE
        } else {
            Toast.makeText(this, "Digite um username válido", Toast.LENGTH_SHORT).show()
        }
    }

    val uiUpdaterHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            Log.d("MainActivity", "Mensagem recebida: ${msg.what}")
            if (msg.what == GET_CHAT) {
                sendMessageDelayed(
                    android.os.Message.obtain().apply {
                        what = GET_CHAT
                    },
                    GET_CHATS_INTERVAL
                )
                postDelayed({
                    chatController.getChats()
                }, 1000)
            } else {
                msg.data.getParcelableArrayList<Chat>("CHAT_ARRAY")?.let { _chatArray ->
                    Log.d("MainActivity", "Chats recebidos: $_chatArray")
                    val username = usernameInput.text.toString().trim()
                    updateChatsList(_chatArray.toMutableList(), username)
                }
            }
        }
    }
    private var isFirstLoad = true
    fun updateChatsList(chats: MutableList<Chat>, username: String) {
        val filteredChats = chats.filter { it.destinatario == username }.sortedByDescending { it.getDateTime() }

        if (isFirstLoad) {
            chatList.clear()
            isFirstLoad = false
        }

        if (chatList.isEmpty()) {
            chatList.addAll(filteredChats)
        } else {
            for (chat in filteredChats) {
                if (!chatList.contains(chat)) {
                    chatList.add(0, chat)
                }
            }
            chatList.retainAll(filteredChats)
        }

        chatAdapter.notifyDataSetChanged()
    }
}
