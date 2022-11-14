package com.example.littlegardener

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.common.SignInButton

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    interface OnLoginListener {
        fun onCredentialsLogin(email: String, password: String)
        fun onSignUp()
        fun onGoogleLogin()
        fun onFacebookLogin(token: AccessToken)
    }

    private lateinit var callBack: OnLoginListener
    private lateinit var callbackManager: CallbackManager
    private lateinit var signInWithGoogle : SignInButton
    private lateinit var signInWithFacebook : com.facebook.login.widget.LoginButton
    private lateinit var loginButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signUp: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        callBack = activity as OnLoginListener
        initUI(view)
        initListeners()
        return view
    }

    private fun initUI(view: View) {
        loginButton = view.findViewById(R.id.login_button)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        signUp = view.findViewById(R.id.sign_up)
        signInWithGoogle = view.findViewById(R.id.sign_in_with_google)
        signInWithFacebook = view.findViewById(R.id.sign_in_with_facebook)
        signInWithFacebook.setPermissions("email", "public_profile")
    }

    private fun initListeners() {
        loginButton.setOnClickListener() {
            var emailText = email.text.toString()
            var passwordText = password.text.toString()
            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                callBack.onCredentialsLogin(emailText, passwordText)
            }
        }
        signUp.setOnClickListener {
            callBack.onSignUp()
        }
        signInWithGoogle.setOnClickListener {
            callBack.onGoogleLogin()
        }
        signInWithFacebook.setPermissions("email", "public_profile")
        callbackManager = CallbackManager.Factory.create()
        signInWithFacebook.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                callBack.onFacebookLogin(loginResult!!.accessToken)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }
        })
    }


    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}