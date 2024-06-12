package com.example.pdmchat.ui

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pdmchat.controller.SaveRtDbFrController
import com.example.pdmchat.databinding.SendChatActivityBinding
import com.example.pdmchat.model.Chat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SendChatActivity : AppCompatActivity() {
    private lateinit var sendChatBinding: SendChatActivityBinding
    private val chatController: SaveRtDbFrController by lazy {
        SaveRtDbFrController()
    }
    private lateinit var destinatarioEditText: AutoCompleteTextView
    private lateinit var destinatariosAdapter: ArrayAdapter<String>
    private lateinit var destinatariosList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendChatBinding = SendChatActivityBinding.inflate(layoutInflater)
        setContentView(sendChatBinding.root)

        destinatarioEditText = sendChatBinding.recipientEditText

        val conteudoEditText: EditText = sendChatBinding.chatEditText
        val enviarButton: Button = sendChatBinding.sendButton

        destinatariosList = mutableListOf()

        val sharedPreferences = getSharedPreferences("Destinatarios", Context.MODE_PRIVATE)
        val destinatariosSet = sharedPreferences.getStringSet("destinatarios", emptySet())
        destinatariosList.addAll(destinatariosSet ?: emptySet())

        destinatariosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            destinatariosList
        )
        destinatarioEditText.setAdapter(destinatariosAdapter)

        val remetente = intent.getStringExtra("remetente")

        enviarButton.setOnClickListener {
            val destinatario = destinatarioEditText.text.toString()
            val conteudo = conteudoEditText.text.toString()

            if (conteudo.isNotEmpty()) {
                if (destinatario.isNotEmpty()) {
                    enviarChat(remetente, destinatario, conteudo)

                    if (!destinatariosList.contains(destinatario)) {
                        destinatariosList.add(destinatario)
                        destinatariosAdapter.notifyDataSetChanged()

                        val editor = sharedPreferences.edit()
                        editor.putStringSet("destinatarios", destinatariosList.toSet())
                        editor.apply()
                    }
                } else {
                    Toast.makeText(this, "Digite o destinatário", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Digite o conteúdo da mensagem", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun enviarChat(remetente: String?, destinatario: String, conteudo: String) {
        val mensagem = if (conteudo.length > 150) conteudo.substring(0, 150) else conteudo
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val data = dateFormat.format(cal.time)
        val horario = timeFormat.format(cal.time)

        val chat = Chat(
            remetente = remetente ?: "",
            destinatario = destinatario,
            data = data,
            horario = horario,
            chat = mensagem
        )

        chatController.insertChat(chat)
        Toast.makeText(this, "Mensagem enviada", Toast.LENGTH_SHORT).show()

        finish()
    }
}
