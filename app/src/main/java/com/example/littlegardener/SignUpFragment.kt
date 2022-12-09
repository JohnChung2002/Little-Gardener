package com.example.littlegardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class SignUpFragment : Fragment() {

    interface OnSignUpListener {
        fun onSignUp(email: String, name: String, password: String)
    }

    private lateinit var callBack: OnSignUpListener
    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var password1EditText: EditText
    private lateinit var password2EditText: EditText
    private lateinit var signUpButton: Button

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
        emailEditText = view.findViewById(R.id.email)
        nameEditText = view.findViewById(R.id.name)
        password1EditText = view.findViewById(R.id.password1)
        password2EditText = view.findViewById(R.id.password2)
        signUpButton = view.findViewById(R.id.sign_up)
    }

    private fun initListeners() {
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val name = nameEditText.text.toString()
            val password1 = password1EditText.text.toString()
            val password2 = password2EditText.text.toString()
            val validEmail = validateEmail(email)
            val validName = validateName(name)
            val validPassword = matchPasswords(password1, password2)
            if (validEmail && validName && validPassword) {
                callBack.onSignUp(email, name, password1)
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        val valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!valid) {
            emailEditText.error = "Invalid email"
        }
        return valid
    }

    private fun matchPasswords(password1: String, password2: String): Boolean {
        var valid = (password1 == password2)
        if (!valid) {
            password2EditText.error = "Password does not match!"
        } else  {
            valid = validatePassword(password1)
        }
        return valid
    }

    private fun validatePassword(password: String): Boolean {
        val valid = (password.length >= 6)
        if (!valid) {
            password1EditText.error = "Password must be at least 6 characters!"
            password2EditText.error = "Password must be at least 6 characters!"
        }
        return password.length >= 6
    }

    private fun validateName(name: String): Boolean {
        val valid = (name.length >= 3)
        if (!valid) {
            nameEditText.error = "Name must be at least 3 characters!"
        }
        return valid
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}