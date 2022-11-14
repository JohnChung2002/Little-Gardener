package com.example.littlegardener

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), LoginFragment.OnLoginListener, SignUpFragment.OnSignUpListener {
    private lateinit var auth: FirebaseAuth
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
        val currentUser = auth.currentUser
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
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI()
                } else {
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
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

    override fun onSignUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI()
                } else {
                    Toast.makeText(
                        baseContext, "User already exists.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun initGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
    }

    override fun onGoogleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        googleActivityLauncher.launch(signInIntent)
    }

    override fun onFacebookLogin(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                updateUI()
            }
        }
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
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun handleGoogleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken , null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        updateUI()
                    }
                }
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        println(auth.currentUser?.uid)
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("email", auth.currentUser?.email)
        intent.putExtra("name", auth.currentUser?.displayName)
        dashboardActivityLauncher.launch(intent)
    }

}