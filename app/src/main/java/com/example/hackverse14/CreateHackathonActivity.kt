package com.example.hackverse14

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateHackathonActivity : AppCompatActivity() {

    private lateinit var hackathonName: EditText
    private lateinit var hackathonDescription: EditText
    private lateinit var hackathonDate: EditText
    private lateinit var hackathonTime: EditText
    private lateinit var hackathonVenue: EditText
    private lateinit var hackathonPrizeMoney: EditText
    private lateinit var hackathonDeadline: EditText
    private lateinit var createButton: Button
    private lateinit var progressBar: ProgressBar

    private val firestore = FirebaseFirestore.getInstance() // Firestore instance
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_hackathon)

        // Initialize Views
        hackathonName = findViewById(R.id.editText_cr_name)
        hackathonDescription = findViewById(R.id.editText_cr_des)
        hackathonDate = findViewById(R.id.editText_cr_date)
        hackathonTime = findViewById(R.id.editText_cr_time)
        hackathonVenue = findViewById(R.id.editText_cr_address)
        hackathonPrizeMoney = findViewById(R.id.editText_cr_money)
        hackathonDeadline = findViewById(R.id.editText_cr_dl)
        createButton = findViewById(R.id.cr_button)
        progressBar = findViewById(R.id.progress_bar)

        // Set onClickListeners for Date fields
        hackathonDate.setOnClickListener { showDatePickerDialog(hackathonDate) }
        hackathonDeadline.setOnClickListener { showDatePickerDialog(hackathonDeadline) }

        createButton.setOnClickListener {
            createHackathon()
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        // Get the current date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the date and set it to the EditText
                val formattedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                editText.setText(formattedDate)
            },
            year, month, day
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }

    private fun createHackathon() {
        // Get input values
        val name = hackathonName.text.toString().trim()
        val description = hackathonDescription.text.toString().trim()
        val date = hackathonDate.text.toString().trim()
        val time = hackathonTime.text.toString().trim()
        val venue = hackathonVenue.text.toString().trim()
        val prizeMoney = hackathonPrizeMoney.text.toString().trim()
        val deadline = hackathonDeadline.text.toString().trim()

        // Validate input fields
        if (name.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty() || venue.isEmpty() || prizeMoney.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress bar
        progressBar.visibility = View.VISIBLE

        // Prepare data to send to Firestore
        val hackathonData = hashMapOf(
            "name" to name,
            "description" to description,
            "date" to date,
            "time" to time,
            "venue" to venue,
            "prizeMoney" to prizeMoney,
            "deadline" to deadline
        )

        // Add data to Firestore under the "Hackathons" collection
        firestore.collection("Hackathons")
            .add(hackathonData)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Hackathon created successfully!", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        hackathonName.text.clear()
        hackathonDescription.text.clear()
        hackathonDate.text.clear()
        hackathonTime.text.clear()
        hackathonVenue.text.clear()
        hackathonPrizeMoney.text.clear()
        hackathonDeadline.text.clear()
    }
}
