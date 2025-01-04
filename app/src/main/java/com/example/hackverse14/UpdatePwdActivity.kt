package com.example.hackverse14

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class UpdatePwdActivity : AppCompatActivity() {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var updateButton: Button

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pwd)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize UI elements
        currentPasswordEditText = findViewById(R.id.editText_change_pwd_current)
        newPasswordEditText = findViewById(R.id.editText_change_pwd_new)
        updateButton = findViewById(R.id.button_update_pwd)

        updateButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (validateInputs(currentPassword, newPassword, confirmPassword)) {
                updatePassword(newPassword)
            }
        }
    }

    private fun validateInputs(currentPassword: String, newPassword: String, confirmPassword: String): Boolean {
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Please enter your current password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updatePassword(newPassword: String) {
        val user = firebaseAuth.currentUser

        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
