package com.example.littlegardener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewStub
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.Auth

class EditProfileActivity : AppCompatActivity() {
    private lateinit var type: String
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var viewStub: ViewStub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        type = intent.getStringExtra("type")!!
        initUI()
    }

    private fun initUI() {
        rootLayout = findViewById(R.id.root_layout)
        viewStub = findViewById(R.id.view_stub)
        viewStub.layoutResource =
            when (type) {
                "change_password" -> R.layout.change_password_layout
                else -> R.layout.edit_profile_layout
            }
        viewStub.inflate()
        when (type) {
            "change_password" -> loadChangePasswordLayout()
            else -> loadEditProfileLayout()
        }
    }

    private fun loadChangePasswordLayout() {
        val currentPasswordEditText = findViewById<EditText>(R.id.current_password)
        val changePasswordButton = findViewById<Button>(R.id.change_password_button)
        changePasswordButton.setOnClickListener {
            if (currentPasswordEditText.text.toString().isNotEmpty()) {
                AuthenticationHelper.validateCurrentPassword(currentPasswordEditText.text.toString()) {
                    if (it) {
                        validateNewPassword() { success ->
                            if (success) {
                                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    } else {
                        currentPasswordEditText.error = "Incorrect password"
                    }
                }
            } else {
                currentPasswordEditText.error = "Please enter your current password"
            }
        }
    }

    private fun validateNewPassword(listener: (Boolean) -> Unit) {
        val newPassword1EditText = findViewById<EditText>(R.id.new_password_1)
        val newPassword2EditText = findViewById<EditText>(R.id.new_password_2)
        if (newPassword1EditText.text.toString().length >= 6 && newPassword2EditText.text.toString().length >= 6) {
            if (newPassword1EditText.text.toString() == newPassword2EditText.text.toString()) {
                AuthenticationHelper.changePassword(newPassword1EditText.text.toString()) {
                    if (it) {
                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        listener.invoke(true)
                    }
                }
            } else {
                newPassword2EditText.error = "Passwords do not match"
            }
        } else {
            if (newPassword1EditText.text.toString().length < 6) {
                newPassword1EditText.error = "Password should be at least 6 characters long"
            }
            if (newPassword2EditText.text.toString().length < 6) {
                newPassword2EditText.error = "Password should be at least 6 characters long"
            }
        }
        listener.invoke(false)
    }

    private fun loadEditProfileLayout() {

    }
}