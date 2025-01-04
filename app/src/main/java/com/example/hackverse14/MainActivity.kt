package com.example.hackverse14

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        progressBar = findViewById(R.id.progress_bar)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences to check first run
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        // First-time launch logic
        if (isFirstRun) {
            // Set "isFirstRun" to false after the first launch
            with(sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }

            // Show login/register buttons for new users
            progressBar.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
        } else {
            // If not first time, check if user is logged in
            checkUserLoginStatus()
        }

        // Handle Login button click
        loginButton.setOnClickListener {
            navigateToLoginActivity()
        }

        // Handle Register button click
        registerButton.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    private fun checkUserLoginStatus() {
        progressBar.visibility = View.VISIBLE
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, navigate to HomeActivity
            navigateToHomeActivity()
        } else {
            // User is not signed in, show login/register buttons
            progressBar.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
        }
    }

    private fun navigateToHomeActivity() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
