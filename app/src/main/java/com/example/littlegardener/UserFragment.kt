package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sign

class UserFragment : Fragment() {
    interface OnUserInteraction {
        fun signOut()
    }
    private lateinit var callback: OnUserInteraction
    private lateinit var profileTextView: TextView
    private lateinit var signOutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        callback = activity as OnUserInteraction
        initUI(view)
        initClickListeners(view)
        return view
    }

    private fun initUI(view: View) {
        profileTextView = view.findViewById(R.id.profile_name)
        FirestoreHelper.getAccountName(AuthenticationHelper.getCurrentUserUid()) {
            profileTextView.text = it
        }
        signOutButton = view.findViewById(R.id.log_out_button)
    }

    private fun initClickListeners(view: View) {
        signOutButton.setOnClickListener {
            callback.signOut()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserFragment()
    }
}