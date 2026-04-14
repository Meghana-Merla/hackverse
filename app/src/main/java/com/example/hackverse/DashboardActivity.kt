package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackverse.databinding.ActivityDashboardBinding
import com.example.hackverse.databinding.ItemHackathonBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var hackathonAdapter: HackathonAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch hackathons from Firestore
        fetchHackathons()

        // Initialize BottomNavigationView
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        // RecyclerView initialization should be done in the displayHackathons method
    }

    private fun fetchHackathons() {
        db.collection("hackathons")
            .get()
            .addOnSuccessListener { documents ->
                val currentDate = System.currentTimeMillis()
                val activeHackathons = mutableListOf<Hackathon>()
                val registrationCompletedHackathons = mutableListOf<Hackathon>()
                val completedHackathons = mutableListOf<Hackathon>()

                for (document in documents) {
                    val hackathon = document.toObject(Hackathon::class.java)
                    hackathon.id = document.id
                    val hackathonDate = parseDate(hackathon.date) // Parses date string to timestamp
                    val deadlineDate = parseDate(hackathon.deadline)

                    when {
                        currentDate <= deadlineDate -> activeHackathons.add(hackathon)
                        currentDate in (deadlineDate + 1)..hackathonDate -> registrationCompletedHackathons.add(hackathon)
                        currentDate > hackathonDate -> completedHackathons.add(hackathon)
                    }
                }

                displayHackathons(activeHackathons, registrationCompletedHackathons, completedHackathons)
            }
            .addOnFailureListener { e ->
                // Handle error
                e.printStackTrace()
            }
    }

    // Helper function to parse date strings
    private fun parseDate(dateString: String?): Long {
        if (dateString.isNullOrEmpty()) {
            return 0 // Return 0 if the date string is invalid or empty
        }

        return try {
            // If the date is just a year (e.g., "2025"), add default month and day
            val formattedDateString = if (dateString.length == 4) {
                "$dateString-01-01" // Default to January 1st
            } else {
                dateString
            }

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(formattedDateString)?.time ?: 0
        } catch (e: ParseException) {
            // Log the error and return 0 if the date format is not as expected
            e.printStackTrace()
            0
        }
    }


    // Display hackathons in separate RecyclerViews or sections
    private fun displayHackathons(
        active: List<Hackathon>,
        registrationCompleted: List<Hackathon>,
        completed: List<Hackathon>
    ) {
        // Set up active hackathons RecyclerView
        binding.recyclerViewActiveHackathons.layoutManager = LinearLayoutManager(this)
        val activeAdapter = HackathonAdapter()
        binding.recyclerViewActiveHackathons.adapter = activeAdapter
        activeAdapter.submitList(active)

        // Set up registration completed RecyclerView
        binding.recyclerViewRegistrationCompletedHackathons.layoutManager = LinearLayoutManager(this)
        val regCompletedAdapter = HackathonAdapter()
        binding.recyclerViewRegistrationCompletedHackathons.adapter = regCompletedAdapter
        regCompletedAdapter.submitList(registrationCompleted)

        // Set up completed hackathons RecyclerView
        binding.recyclerViewCompletedHackathons.layoutManager = LinearLayoutManager(this)
        val completedAdapter = HackathonAdapter()
        binding.recyclerViewCompletedHackathons.adapter = completedAdapter
        completedAdapter.submitList(completed)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.bottom_dashboard

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    navigateTo(HomeActivity::class.java)
                    true
                }
                R.id.bottom_dashboard -> true // Stay on the current activity
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
    }

    private fun <T> navigateTo(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun registerForHackathon(hackathon: Hackathon) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please log in to register", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if registration is still open
        val currentDate = System.currentTimeMillis()
        val deadlineDate = parseDate(hackathon.deadline)
        if (currentDate > deadlineDate) {
            Toast.makeText(this, "Registration deadline has passed", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if already registered (simple check, can be improved)
        db.collection("registrations")
            .whereEqualTo("userId", user.uid)
            .whereEqualTo("hackathonId", hackathon.id)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "Already registered for this hackathon", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Register
                val registrationData = hashMapOf(
                    "userId" to user.uid,
                    "hackathonId" to hackathon.id,
                    "hackathonTitle" to hackathon.title,
                    "hackathonDate" to hackathon.date,
                    "registeredAt" to System.currentTimeMillis()
                )

                db.collection("registrations")
                    .add(registrationData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully registered!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error checking registration: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

