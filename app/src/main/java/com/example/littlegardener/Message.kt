package com.example.littlegardener

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ChatGroup(
    val id: String = "",
    val parties: HashMap<String, String> = hashMapOf(),
    var messages: HashMap<String, Message> = hashMapOf()
)

@Parcelize
data class ChatItem(
    var id: String = "",
    var name: String = "",
    var receiver: String = "",
    var image: String = ""
): Parcelable

data class Message(
    val sender: String = "",
    val receiver: String = "",
    val type: String = "",
    val message: String = ""
)
