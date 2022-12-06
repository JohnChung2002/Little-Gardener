package com.example.littlegardener

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ChatGroup(
    val id: String = "",
    val parties: HashMap<String, Boolean> = hashMapOf(),
    var messages: HashMap<String, Message> = hashMapOf()
)

@Parcelize
data class ChatItem(
    val id: String,
    val name: String,
    val receiver: String,
    val image: String?
): Parcelable

data class Message(
    val sender: String = "",
    val receiver: String = "",
    val type: String = "",
    val message: String = ""
)
