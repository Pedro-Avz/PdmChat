package com.example.pdmchat.model

interface ChatDao {
    fun createChat(message: Chat): Int
    fun retrieveChats(): MutableList<Chat>
}