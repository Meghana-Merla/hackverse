package com.example.hackverse14

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_notifications

        // Set up the navigation listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    // Navigate to HomeActivity
                    navigateTo(HomeActivity::class.java)
                    true
                }
                R.id.bottom_dashboard -> {
                    // Navigate to DashboardActivity
                    navigateTo(DashboardActivity::class.java)
                    true
                }
                R.id.bottom_notifications -> true // Stay on the current activity
                R.id.bottom_profile -> {
                    // Navigate to ProfileActivity
                    navigateTo(ProfileActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    // Helper function for activity navigation with transition
    private fun <T> navigateTo(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
}