package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlin.math.sign

class UserFragment : Fragment() {
    interface OnUserInteraction {
        fun viewOrders()
        fun editProfile(type: String)
        fun signOut()
    }
    private lateinit var callback: OnUserInteraction
    private lateinit var profileTextView: TextView
    private lateinit var profileImageView: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var viewOrdersButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var signOutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        callback = activity as OnUserInteraction
        initUI(view)
        initClickListeners()
        return view
    }

    private fun initUI(view: View) {
        profileTextView = view.findViewById(R.id.profile_name)
        profileImageView = view.findViewById(R.id.profile_image)
        loadInfo()
        viewOrdersButton = view.findViewById(R.id.view_orders_button)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        changePasswordButton = view.findViewById(R.id.change_password_button)
        signOutButton = view.findViewById(R.id.log_out_button)
    }

    private fun loadInfo() {
        FirestoreHelper.getAccountInfo(AuthenticationHelper.getCurrentUserUid()) { name, image ->
            profileTextView.text = name
            if (image != "") {
                Glide.with(this).load(image).into(profileImageView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadInfo()
    }

    private fun initClickListeners() {
        FirestoreHelper.getRole {
            if (it == "Admin") {
                viewOrdersButton.visibility = View.GONE
            } else {
                viewOrdersButton.setOnClickListener {
                    callback.viewOrders()
                }
            }
        }
        editProfileButton.setOnClickListener {
            callback.editProfile("edit_profile")
        }
        changePasswordButton.setOnClickListener {
            callback.editProfile("change_password")
        }
        signOutButton.setOnClickListener {
            callback.signOut()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserFragment()
    }
}