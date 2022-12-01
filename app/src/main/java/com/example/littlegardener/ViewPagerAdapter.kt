package com.example.littlegardener

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (private val appCompatActivity: AppCompatActivity, private val tabItems: List<String>, private val role: String) : FragmentStateAdapter(appCompatActivity) {
    override fun getItemCount(): Int = tabItems.size

    override fun createFragment(position: Int): Fragment {
        if (role == "Admin") {
            return when (position) {
                0 -> HomeFragment.newInstance("Test", "Test")
                1 -> ChatFragment.newInstance("Test", "Test")
                2 -> NotificationFragment.newInstance("Test", "Test")
                else -> UserFragment.newInstance("Test", "Test")
            }
        } else {
            return when (position) {
                0 -> HomeFragment.newInstance("Test", "Test")
                1 -> ChatFragment.newInstance("Test", "Test")
                2 -> NotificationFragment.newInstance("Test", "Test")
                3 -> CartFragment.newInstance("Test", "Test")
                else -> UserFragment.newInstance("Test", "Test")
            }
        }
    }
}
