package com.example.littlegardener

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewStub
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth

class EditProfileActivity : AppCompatActivity() {
    private lateinit var type: String
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var viewStub: ViewStub
    private lateinit var profileImage: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var editIcon: ImageView
    private lateinit var profileName: EditText
    private lateinit var imageUri: Uri

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

    private val attachImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                Glide.with(this).load(uri).into(profileImage)
            }
        }
    }

    private fun loadChangePasswordLayout() {
        val currentPasswordEditText = findViewById<EditText>(R.id.current_password)
        val changePasswordButton = findViewById<Button>(R.id.change_password_button)
        changePasswordButton.text = "Change password"
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
        profileImage = findViewById(R.id.profile_image)
        profileName = findViewById(R.id.profile_name)
        loadProfile()
        profileImage.setOnClickListener {
            newProfileImage()
        }
        editIcon = findViewById(R.id.edit_icon)
        editIcon.setOnClickListener {
            newProfileImage()
        }
        val changePasswordButton = findViewById<Button>(R.id.change_password_button)
        changePasswordButton.text = "Edit Profile"
        changePasswordButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadProfile() {
        FirestoreHelper.getAccountInfo(AuthenticationHelper.getCurrentUserUid()) { name, image ->
            profileName.hint = name
            if (image != "") {
                Glide.with(this).load(image).into(profileImage)
            }
        }
    }

    private fun newProfileImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        attachImage.launch(intent)
    }

    private fun updateProfile() {
        if (profileName.text.toString().isNotEmpty()) {
            FirestoreHelper.updateProfileName(profileName.text.toString())
        }
        if (this::imageUri.isInitialized) {
            StorageHelper.uploadImage(this, imageUri) {
                FirestoreHelper.updateProfileImage(it)
            }
        }
        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
        finish()
    }
}