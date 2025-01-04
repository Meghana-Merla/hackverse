package com.example.hackverse14

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class UpdateEmailActivity : AppCompatActivity() {

    private lateinit var newEmailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var updateButton: Button

    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_email)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize UI elements
        newEmailEditText = findViewById(R.id.editText_change_pwd_current)
        passwordEditText = findViewById(R.id.editText_password)
        updateButton = findViewById(R.id.button_update_email)

        updateButton.setOnClickListener {
            val newEmail = newEmailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateInputs(newEmail, password)) {
                reauthenticateAndChangeEmail(newEmail, password)
            }
        }
    }

    private fun validateInputs(newEmail: String, password: String): Boolean {
        if (TextUtils.isEmpty(newEmail)) {
            Toast.makeText(this, "Please enter your new email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun reauthenticateAndChangeEmail(newEmail: String, password: String) {
        val user = firebaseAuth.currentUser

        if (user != null) {
            val currentEmail = user.email
            if (currentEmail == null) {
                Toast.makeText(this, "Unable to fetch current email. Try signing in again.", Toast.LENGTH_SHORT).show()
                return
            }

            val credential = EmailAuthProvider.getCredential(currentEmail, password)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updateEmail(newEmail)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("UpdateEmail", "Error updating email: ${updateTask.exception?.message}")
                                Toast.makeText(this, "Failed to update email: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Log.e("Reauthentication", "Error re-authenticating: ${task.exception?.message}")
                    Toast.makeText(this, "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
