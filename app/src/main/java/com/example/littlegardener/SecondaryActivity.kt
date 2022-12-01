package com.example.littlegardener

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SecondaryActivity : AppCompatActivity(), UserFragment.OnUserInteraction {
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
    }

    override fun signOut() {
        AuthenticationHelper.signOut()
        setResult(RESULT_OK)
        finish()
    }
}