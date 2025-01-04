package com.example.hackverse14

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // BottomNavigationView setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true // Stay on this activity
                R.id.bottom_dashboard -> {
                    navigateTo(DashboardActivity::class.java)
                    true
                }
                R.id.bottom_notifications -> {
                    navigateTo(NotificationActivity::class.java)
                    true
                }
                R.id.bottom_profile -> {
                    navigateTo(ProfileActivity::class.java)
                    true
                }
                else -> false
            }
        }

        // Button: Update Activity
        findViewById<Button>(R.id.btn_update_pwd).setOnClickListener {
            navigateTo(UpdatePwdActivity::class.java)
        }

        // Button: Update Email
        findViewById<Button>(R.id.btn_update_email).setOnClickListener {
            navigateTo(UpdateEmailActivity::class.java)
        }

        // Button: Delete Account
        findViewById<Button>(R.id.btn_delete_account).setOnClickListener {
            navigateTo(DeleteAccountActivity::class.java)
        }

        // Button: Logout
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Button: Create Hackathon
        findViewById<Button>(R.id.btn_create_hackathon).setOnClickListener {
            navigateTo(CreateHackathonActivity::class.java)
        }
    }

    // Helper function for navigation
    private fun <T> navigateTo(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
