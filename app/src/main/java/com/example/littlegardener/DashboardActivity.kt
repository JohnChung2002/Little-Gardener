package com.example.littlegardener

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")
        db.collection("users").document(auth.currentUser?.uid?:"").get().addOnSuccessListener { documentSnapshot ->
            val name = documentSnapshot.getString("name")
            val role = documentSnapshot.getString("role")
            val displayText = email + "\n" + displayName + "\n" + role + "\n" + name
            findViewById<TextView>(R.id.email).text = displayText
        }
        findViewById<Button>(R.id.sign_out).setOnClickListener {
            auth.signOut()
            setResult(RESULT_OK)
            finish()
        }
        findViewById<Button>(R.id.test).setOnClickListener {
            val intent = Intent(this, LiveChatActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
    }
}