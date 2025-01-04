package com.example.hackverse14

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginPwd: EditText
    private lateinit var imageViewShowHidePwd: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var authProfile: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "Login"
        Toast.makeText(this, "You can Login now", Toast.LENGTH_LONG).show()

        // Initialize Views
        editTextLoginEmail = findViewById(R.id.editText_login_email)
        editTextLoginPwd = findViewById(R.id.editText_login_pwd)
        imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd)
        progressBar = findViewById(R.id.progressBar)
        val buttonLogin: Button = findViewById(R.id.button_login)
        val textViewRegister: TextView = findViewById(R.id.textView_register_link)
        val textViewForgotPassword: TextView = findViewById(R.id.textView_forgot_password_link)

        // Initialize FirebaseAuth
        authProfile = FirebaseAuth.getInstance()

        // Handle Show/Hide Password functionality
        imageViewShowHidePwd.setOnClickListener {
            togglePasswordVisibility()
        }

        // Handle Login Button Click
        buttonLogin.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked.")
            val textEmail = editTextLoginEmail.text.toString().trim()
            val textPwd = editTextLoginPwd.text.toString().trim()

            if (validateInputs(textEmail, textPwd)) {
                showProgressBar(true)
                loginUser(textEmail, textPwd)
            }
        }

        // Navigate to RegisterActivity
        textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Navigate to ForgotPasswordActivity
        textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = authProfile.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                navigateToHomeActivity()
            } else {
                Toast.makeText(this, "Email not verified. Please verify.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide Password
            editTextLoginPwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd)
            isPasswordVisible = false
        } else {
            // Show Password
            editTextLoginPwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd)
            isPasswordVisible = true
        }
        // Move cursor to the end of the text
        editTextLoginPwd.setSelection(editTextLoginPwd.text.length)
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                editTextLoginEmail.error = "Email is required"
                editTextLoginEmail.requestFocus()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                editTextLoginEmail.error = "Valid email is required"
                editTextLoginEmail.requestFocus()
                false
            }
            password.isEmpty() -> {
                editTextLoginPwd.error = "Password is required"
                editTextLoginPwd.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun showProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun loginUser(textEmail: String, textPwd: String) {
        authProfile.signInWithEmailAndPassword(textEmail, textPwd)
            .addOnCompleteListener(this) { task ->
                showProgressBar(false)
                if (task.isSuccessful) {
                    val firebaseUser = authProfile.currentUser
                    if (firebaseUser != null && firebaseUser.isEmailVerified) {
                        Toast.makeText(this@LoginActivity, "You are logged in now", Toast.LENGTH_SHORT).show()
                        navigateToHomeActivity()
                    } else {
                        showEmailNotVerifiedDialog()
                    }
                } else {
                    handleLoginError(task.exception)
                }
            }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showEmailNotVerifiedDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email Not Verified")
        builder.setMessage("Please verify your email before logging in. Resend verification email?")
        builder.setPositiveButton("Resend") { _, _ ->
            val firebaseUser = authProfile.currentUser
            firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Verification email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun handleLoginError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                Toast.makeText(this@LoginActivity, "Email not registered.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this@LoginActivity, "Incorrect password.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this@LoginActivity, "Login failed! Check your credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
