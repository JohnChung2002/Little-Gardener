package com.example.littlegardener

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener

class LiveChatActivity : AppCompatActivity() {
    private lateinit var chatName: String
    private lateinit var toolbar: Toolbar
    private lateinit var addMessageImageView: ImageView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageHashmap: HashMap<String, Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_chat)
        intent.getStringExtra("chatId")?.let {
            RealtimeDBHelper.getChatMessages(it) { messages ->
                messageHashmap = messages
            }
        }
        intent.getStringExtra("chatName")?.let {
            chatName = it
        }
        initUI()
    }

    private fun initUI() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = chatName
        addMessageImageView = findViewById(R.id.add_message_image_view)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_button)
        addMessageImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            attachImage.launch(intent)
        }
        messageEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                setSendClickListener()
            } else {
                sendButton.setOnClickListener(null)
            }
        }
    }

    private val attachImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSendClickListener() {
        sendButton.setOnClickListener {
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessage() {
        TODO("Send message")
    }
}