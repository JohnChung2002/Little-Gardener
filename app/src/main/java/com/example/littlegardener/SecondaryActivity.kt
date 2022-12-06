package com.example.littlegardener

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView


class SecondaryActivity : AppCompatActivity(), UserFragment.OnUserInteraction, MessageAdapter.MessageListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    private var chatRecyclerList: MutableList<ChatItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary)
        initUI()
    }

    private fun initUI() {
        val tabItems = listOf("Home", "Chat", "Notification", "Cart", "User")
        viewPager = findViewById(R.id.view_pager)
        bottomNavigationView = findViewById(R.id.bottom_nav_bar)
        bottomNavigationView.menu.clear()
        FirestoreHelper.getRole {
            bottomNavigationView.menu.clear()
            if (it == "Admin") {
                bottomNavigationView.inflateMenu(R.menu.admin_menu)
                initAdminNavigationClickListener()
            } else {
                bottomNavigationView.inflateMenu(R.menu.user_menu)
                initUserNavigationClickListener()
            }
            viewPager.isUserInputEnabled = false
            viewPager.adapter = ViewPagerAdapter(this, chatRecyclerList, tabItems, it)
        }
        FirestoreHelper.getChatList { chats ->
            for (chatId in chats) {
                RealtimeDBHelper.loadUserName(chatId) {
                    chatRecyclerList.add(ChatItem(chatId, it, "chatImage"))
                }
            }
        }
    }

    private fun initUserNavigationClickListener() {
        bottomNavigationView.setOnItemSelectedListener {
            val manager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (manager != null) {
                manager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
            }
            when (it.itemId) {
                R.id.home_icon -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.chat_icon -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.notification_icon -> {
                    viewPager.currentItem = 2
                    true
                }
                R.id.cart_icon -> {
                    viewPager.currentItem = 3
                    true
                }
                else -> {
                    viewPager.currentItem = 4
                    true
                }
            }
        }
    }

    private fun initAdminNavigationClickListener() {
        bottomNavigationView.setOnItemSelectedListener {
            val manager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (manager != null) {
                manager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
            }
            when (it.itemId) {
                R.id.home_icon -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.chat_icon -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.notification_icon -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> {
                    viewPager.currentItem = 3
                    true
                }
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun signOut() {
        AuthenticationHelper.signOut()
        setResult(RESULT_OK)
        finish()
    }

    override fun onMessageClicked(chatId: String, chatName: String) {
        val intent = Intent(this, LiveChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        intent.putExtra("chatName", chatName)
        startActivity(intent)
    }
}