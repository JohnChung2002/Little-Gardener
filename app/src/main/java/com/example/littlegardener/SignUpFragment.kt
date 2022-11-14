package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class SignUpFragment : Fragment() {

    interface OnSignUpListener {
        fun onSignUp(email: String, password: String)
    }

    private lateinit var callBack: OnSignUpListener
    private lateinit var email: TextView
    private lateinit var password1: TextView
    private lateinit var password2: TextView
    private lateinit var signUp: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        callBack = activity as OnSignUpListener
        initUI(view)
        initListeners()
        return view
    }

    private fun initUI(view: View) {
        email = view.findViewById(R.id.email)
        password1 = view.findViewById(R.id.password1)
        password2 = view.findViewById(R.id.password2)
        signUp = view.findViewById(R.id.sign_up)
    }

    private fun initListeners() {
        signUp.setOnClickListener {
            val email = email.text.toString()
            val password1 = password1.text.toString()
            val password2 = password2.text.toString()
            if (password1 == password2) {
                callBack.onSignUp(email, password1)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}