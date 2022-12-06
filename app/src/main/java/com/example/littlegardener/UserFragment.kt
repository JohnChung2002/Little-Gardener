package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserFragment : Fragment() {
    private lateinit var callback: OnUserInteraction

    interface OnUserInteraction {
        fun signOut()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        callback = activity as OnUserInteraction
        initClickListeners(view)
        return view
    }

    private fun initClickListeners(view: View) {
        view.findViewById<Button>(R.id.log_out_button).setOnClickListener {
            callback.signOut()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserFragment()
    }
}