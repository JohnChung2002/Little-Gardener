package com.example.littlegardener

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), LoginFragment.OnLoginListener, SignUpFragment.OnSignUpListener {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initGoogleAuth()
        AppEventsLogger.activateApp(application) //facebook login init
    }

    private fun initUI() {
        loginFragment = LoginFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.constraint_layout, loginFragment)
            .addToBackStack("login")
            .commit()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = AuthenticationHelper.getAuth().currentUser
        if (currentUser != null) {
            updateUI()
        }
    }

    private var googleActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleResults(task)
        }
    }

    private var dashboardActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_OK) {
            googleSignInClient.revokeAccess()
            LoginManager.getInstance().logOut()
        }
    }

    override fun onCredentialsLogin(email: String, password: String) {
        AuthenticationHelper.signInEmailPassword(email, password) { result ->
            if (result) {
                updateUI()
            } else {
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSignUp() {
        supportFragmentManager.popBackStackImmediate("login",0)
        supportFragmentManager.beginTransaction()
            .replace(R.id.constraint_layout, SignUpFragment.newInstance())
            .addToBackStack("signUp")
            .commit()
    }

    override fun onForgotPassword() {
        supportFragmentManager.popBackStackImmediate("login",0)
        supportFragmentManager.beginTransaction()
            .replace(R.id.constraint_layout, ForgotPasswordFragment.newInstance())
            .addToBackStack("forgotPassword")
            .commit()
    }

    override fun onSignUp(email: String, name: String, password: String) {
        AuthenticationHelper.signUpEmailPassword(email, name, password) { result ->
            if (result) {
                supportFragmentManager.popBackStackImmediate("signUp",0)
                updateUI()
            } else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onGoogleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        googleActivityLauncher.launch(signInIntent)
    }

    override fun onFacebookLogin(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        signInOAuth(credential)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.fragments.last()) {
            is SignUpFragment -> {
                supportFragmentManager.popBackStackImmediate("signUp",0)
                supportFragmentManager.beginTransaction()
                    .add(R.id.constraint_layout, loginFragment)
                    .addToBackStack("login")
                    .commit()
            }
            is LoginFragment -> {
                finish()
            }
            is ForgotPasswordFragment -> {
                supportFragmentManager.popBackStackImmediate("forgotPassword",0)
                supportFragmentManager.beginTransaction()
                    .add(R.id.constraint_layout, loginFragment)
                    .addToBackStack("login")
                    .commit()
            }
            else -> {
                finishAffinity()
                finish()
            }
        }
    }

    private fun signInOAuth(credential: AuthCredential) {
        AuthenticationHelper.signInOAuth(credential) { result ->
            if (result) {
                updateUI()
            } else {
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken , null)
                signInOAuth(credential)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this, SecondaryActivity::class.java)
        dashboardActivityLauncher.launch(intent)
    }

}