package com.example.littlegardener

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (private val appCompatActivity: AppCompatActivity, private var chatRecyclerList: List<ChatItem>, private val tabItems: List<String>, private val role: String) : FragmentStateAdapter(appCompatActivity) {
    override fun getItemCount(): Int = tabItems.size

    override fun createFragment(position: Int): Fragment {
        if (role == "Admin") {
            return when (position) {
                0 -> HomeFragment.newInstance()
                1 -> ChatFragment.newInstance(chatRecyclerList)
                2 -> NotificationFragment.newInstance()
                else -> UserFragment.newInstance()
            }
        } else {
            return when (position) {
                0 -> HomeFragment.newInstance()
                1 -> ChatFragment.newInstance(chatRecyclerList)
                2 -> NotificationFragment.newInstance()
                3 -> CartFragment.newInstance()
                else -> UserFragment.newInstance()
            }
        }
    }
}
