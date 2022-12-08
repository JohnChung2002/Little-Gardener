package com.example.littlegardener

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView


class SecondaryActivity : AppCompatActivity(), HomeCategoryAdapter.CategoryListener, UserFragment.OnUserInteraction, MessageAdapter.MessageListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

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
            viewPager.adapter = ViewPagerAdapter(this, tabItems, it)
        }
    }

    private fun initUserNavigationClickListener() {
        bottomNavigationView.setOnItemSelectedListener {
            val manager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
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

    override fun onMessageClicked(chatItem: ChatItem) {
        val intent = Intent(this, LiveChatActivity::class.java)
        intent.putExtra("type", "exists")
        intent.putExtra("chatItem", chatItem)
        startActivity(intent)
    }

    override fun onCategoryClicked(category: String) {
        val intent = Intent(this, CrudActivity::class.java)
        intent.putExtra("type", "view_list")
        intent.putExtra("category", category)
        startActivity(intent)
    }
}