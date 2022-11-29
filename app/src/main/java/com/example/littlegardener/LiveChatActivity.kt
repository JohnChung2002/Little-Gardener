package com.example.littlegardener

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener

class LiveChatActivity : AppCompatActivity() {
    private lateinit var addMessageImageView: ImageView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_chat)
        initUI()
    }

    private fun initUI() {
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