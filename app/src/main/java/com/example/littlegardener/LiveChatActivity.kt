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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ListenerRegistration

class LiveChatActivity : AppCompatActivity() {
    private lateinit var type: String
    private lateinit var chatItem: ChatItem
    private lateinit var toolbar: Toolbar
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addMessageImageView: ImageView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private var messageHashmap: MutableMap<String, Message> = mutableMapOf()
    private lateinit var eventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_chat)
        type = intent.getStringExtra("type")!!
        chatItem = intent.getParcelableExtra("chatItem")!!
        if (type != "exists") {
            FirestoreHelper.getAccountInfo(chatItem.receiver) { name, image ->
                chatItem.name = name
                println(image)
                FirestoreHelper.checkIfChatExists(chatItem.receiver) { chatId ->
                    if (chatId != "") {
                        chatItem.id = chatId
                        type = "exists"
                    }
                    initialise()
                }
            }
        } else {
            initialise()
        }
    }

    private fun initialise() {
        initUI()
        initListeners()
        if (type == "exists") {
            realtimeDBListener()
        }
    }

    private fun initUI() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = chatItem.name
        recyclerView = findViewById(R.id.live_chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(messageHashmap)
        recyclerView.adapter = chatAdapter
        addMessageImageView = findViewById(R.id.add_message_image_view)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_button)
    }

    private fun initListeners() {
        addMessageImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            attachImage.launch(intent)
        }
        messageEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                sendButton.setOnClickListener {
                    sendMessage()
                }
            } else {
                sendButton.setOnClickListener(null)
            }
        }
    }

    private fun realtimeDBListener() {
        val ref = RealtimeDBHelper.getChatReference(chatItem.id)
        eventListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val message = child.getValue(Message::class.java)
                    message?.let {
                        messageHashmap[child.key!!] = it
                    }
                }
                RealtimeDBHelper.setMessageStatus(chatItem.id, AuthenticationHelper.getCurrentUserUid(), "Read")
                chatAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messageHashmap.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private val attachImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                StorageHelper.uploadImage(this, uri) {
                    val message = Message(AuthenticationHelper.getCurrentUserUid(), chatItem.receiver,"image", it)
                    if (type != "exists") {
                        RealtimeDBHelper.createChat(chatItem.receiver) { id ->
                            chatItem.id = id
                            type = "exists"
                            realtimeDBListener()
                            RealtimeDBHelper.pushMessage(chatItem.id, chatItem.receiver, message)
                        }
                    } else {
                        RealtimeDBHelper.pushMessage(chatItem.id, chatItem.receiver, message)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        eventListener.let {
            RealtimeDBHelper.getChatReference(chatItem.id).removeEventListener(it)
        }
        super.onBackPressed()
    }

    private fun sendMessage() {
        messageEditText.text.toString().let {
            val message = Message(AuthenticationHelper.getCurrentUserUid(), chatItem.receiver,"message", it, FirestoreHelper.getCurrTimestamp())
            if (type != "exists") {
                RealtimeDBHelper.createChat(chatItem.receiver) { id ->
                    chatItem.id = id
                    type = "exists"
                    realtimeDBListener()
                    RealtimeDBHelper.pushMessage(chatItem.id, chatItem.receiver, message)
                }
            } else {
                RealtimeDBHelper.pushMessage(chatItem.id, chatItem.receiver, message)
            }
            messageEditText.text.clear()
        }
    }
}