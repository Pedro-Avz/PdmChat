package com.example.pdmchat.model

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Chat(
    @get:NonNull
    var id: String = "",
    @get:NonNull
    var remetente: String = "",
    @get:NonNull
    var destinatario: String = "",
    @get:NonNull
    var data: String = "",
    @get:NonNull
    var horario: String = "",
    @get:NonNull
    var chat: String = ""
) : Parcelable{
    fun getDateTime(): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return try {
            format.parse("$data $horario")
        } catch (e: Exception) {
            null
        }
    }
}