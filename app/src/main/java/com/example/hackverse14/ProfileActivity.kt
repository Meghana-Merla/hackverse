package com.example.hackverse14

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    // Declaring views
    private lateinit var textViewWelcome: TextView
    private lateinit var textViewFullName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewDob: TextView
    private lateinit var textViewGender: TextView
    private lateinit var textViewMobile: TextView
    private lateinit var textViewEducation: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var progressBar: ProgressBar

    // Firebase instance
    private lateinit var authProfile: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initializing the views
        textViewWelcome = findViewById(R.id.textView_show_welcome)
        textViewFullName = findViewById(R.id.textView_show_full_name)
        textViewEmail = findViewById(R.id.textView_show_email)
        textViewDob = findViewById(R.id.textView_show_dob)
        textViewGender = findViewById(R.id.textView_show_gender)
        textViewMobile = findViewById(R.id.textView_show_mobile)
        textViewEducation = findViewById(R.id.textView_show_education)
        textViewAddress = findViewById(R.id.textView_show_address)
        progressBar = findViewById(R.id.progress_bar)

        // Firebase authentication
        authProfile = FirebaseAuth.getInstance()

        // Check if user is logged in
        val firebaseUser = authProfile.currentUser
        if (firebaseUser == null) {
            Toast.makeText(this, "User is not logged in. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            progressBar.visibility = View.VISIBLE
            showUserProfile(firebaseUser)
        }

        // Set up bottom navigation
        setupBottomNavigation()
    }

    // Setting up Bottom Navigation
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> navigateTo(MainActivity::class.java)
                R.id.bottom_dashboard -> navigateTo(DashboardActivity::class.java)
                R.id.bottom_notifications -> navigateTo(NotificationActivity::class.java)
                R.id.bottom_profile -> true // Stay in the current activity
                else -> false
            }
        }
    }

    // Helper function to navigate between activities
    private fun <T> navigateTo(activity: Class<T>): Boolean {
        startActivity(Intent(applicationContext, activity))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
        return true
    }

    // Show user profile information from Firebase
    private fun showUserProfile(firebaseUser: FirebaseUser) {
        val userID = firebaseUser.uid
        val referenceProfile = FirebaseDatabase.getInstance().getReference("RegisteredUsers")

        referenceProfile.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val userDetails = snapshot.getValue(ReadWriteUserDetails::class.java)
                        if (userDetails != null) {
                            // Displaying user data
                            textViewWelcome.text = "Welcome, ${userDetails.fullName ?: "User"}!"
                            textViewFullName.text = userDetails.fullName ?: "N/A"
                            textViewEmail.text = firebaseUser.email ?: "N/A"
                            textViewDob.text = userDetails.dob ?: "N/A"
                            textViewGender.text = userDetails.gender ?: "N/A"
                            textViewMobile.text = userDetails.mobile ?: "N/A"
                            textViewEducation.text = userDetails.education ?: "N/A"
                            textViewAddress.text = userDetails.address ?: "N/A"
                        } else {
                            Toast.makeText(this@ProfileActivity, "User details not found.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileActivity", "Error parsing user data: ${e.message}", e)
                        Toast.makeText(this@ProfileActivity, "Error loading profile data.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "No user profile found in the database.", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Database error: ${error.message}", error.toException())
                Toast.makeText(this@ProfileActivity, "Failed to load user data: ${error.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }
}

// Data class for reading user details from Firebase
data class ReadWriteUserDetails(
    val fullName: String? = null,
    val dob: String? = null,
    val gender: String? = null,
    val mobile: String? = null,
    val education: String? = null,
    val address: String?=null
)