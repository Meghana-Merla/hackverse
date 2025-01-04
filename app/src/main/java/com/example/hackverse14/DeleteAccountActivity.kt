package com.example.hackverse14

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var deleteButton: Button

    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize UI elements
        emailEditText = findViewById(R.id.editText_Email)
        passwordEditText = findViewById(R.id.editText_password)
        deleteButton = findViewById(R.id.button_delete_account)

        deleteButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateInputs(email, password)) {
                reauthenticateAndDeleteAccount(email, password)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun reauthenticateAndDeleteAccount(email: String, password: String) {
        val user = firebaseAuth.currentUser

        if (user != null) {
            val credential = EmailAuthProvider.getCredential(email, password)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.delete()
                        .addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                // Navigate the user to the login or main screen
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to delete account: ${deleteTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
