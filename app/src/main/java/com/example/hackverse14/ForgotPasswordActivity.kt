package com.example.hackverse14

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var buttonResetPassword: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize Views
        editTextEmail = findViewById(R.id.edittext_email)
        buttonResetPassword = findViewById(R.id.button_reset_password)

        // Handle Reset Password button click
        buttonResetPassword.setOnClickListener {
            val email = editTextEmail.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmail.error = "Email is required"
                editTextEmail.requestFocus()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.error = "Please enter a valid email address"
                editTextEmail.requestFocus()
            } else {
                sendPasswordResetEmail(email)
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Toast message after sending email
                    Toast.makeText(
                        this,
                        "Password reset email sent. Please check your inbox.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Improved error handling with a meaningful error message
                    val exception = task.exception
                    val errorMessage = exception?.message ?: "Failed to send reset email. Please try again."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}