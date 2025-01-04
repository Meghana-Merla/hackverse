package com.example.hackverse14

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextRegisterFullName: EditText
    private lateinit var editTextRegisterEmail: EditText
    private lateinit var editTextRegisterPwd: EditText
    private lateinit var editTextRegisterConfirmPwd: EditText
    private lateinit var editTextRegisterMobile: EditText
    private lateinit var editTextRegisterDob: EditText
    private lateinit var editTextRegisterAddress: EditText
    private lateinit var editTextRegisterEducation: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var radioGroupRegisterGender: RadioGroup
    private lateinit var imageViewShowHidePwd: ImageView
    private lateinit var imageViewShowHideConfirmPwd: ImageView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title = "Register"
        Toast.makeText(this, "You can register now", Toast.LENGTH_LONG).show()

        // Initialize views
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name)
        editTextRegisterEmail = findViewById(R.id.edittext_register_mail_id)
        editTextRegisterPwd = findViewById(R.id.editText_register_password)
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirmation)
        editTextRegisterDob = findViewById(R.id.editText_register_dob)
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile)
        editTextRegisterAddress = findViewById(R.id.edittext_register_address)
        editTextRegisterEducation = findViewById(R.id.edittext_register_education)
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender)
        progressBar = findViewById(R.id.progress_bar)
        imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd)
        imageViewShowHideConfirmPwd = findViewById(R.id.imageView_show_hide_cpwd)

        // Set initial icons to "Show Password" state
        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd)
        imageViewShowHideConfirmPwd.setImageResource(R.drawable.ic_show_pwd)

        // Set up DatePickerDialog for DOB field
        editTextRegisterDob.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle toggle password visibility for Password field
        imageViewShowHidePwd.setOnClickListener {
            togglePasswordVisibility(editTextRegisterPwd, imageViewShowHidePwd)
        }

        // Handle toggle password visibility for Confirm Password field
        imageViewShowHideConfirmPwd.setOnClickListener {
            togglePasswordVisibility(editTextRegisterConfirmPwd, imageViewShowHideConfirmPwd)
        }

        // Register button logic
        val registerButton: Button = findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            handleRegistration()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
            editTextRegisterDob.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView) {
        if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            // Show password
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageView.setImageResource(R.drawable.ic_hide_pwd) // Replace with "hide" icon
        } else {
            // Hide password
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageView.setImageResource(R.drawable.ic_show_pwd) // Replace with "show" icon
        }
        // Move cursor to the end of the text
        editText.setSelection(editText.text.length)
    }

    private fun handleRegistration() {
        val textFullName = editTextRegisterFullName.text.toString()
        val textEmail = editTextRegisterEmail.text.toString()
        val textPwd = editTextRegisterPwd.text.toString()
        val textConfirmPwd = editTextRegisterConfirmPwd.text.toString()
        val textDob = editTextRegisterDob.text.toString()
        val textMobile = editTextRegisterMobile.text.toString()
        val textAddress = editTextRegisterAddress.text.toString()
        val textEducation = editTextRegisterEducation.text.toString()

        val mobileNumberPattern = Regex("^[6-9]\\d{9}$") // Matches valid 10-digit mobile numbers

        // Validations
        when {
            textFullName.isEmpty() -> {
                editTextRegisterFullName.error = "Full Name is required"
                editTextRegisterFullName.requestFocus()
            }
            textEmail.isEmpty() -> {
                editTextRegisterEmail.error = "Email is required"
                editTextRegisterEmail.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(textEmail).matches() -> {
                editTextRegisterEmail.error = "Valid email is required"
                editTextRegisterEmail.requestFocus()
            }
            textDob.isEmpty() -> {
                editTextRegisterDob.error = "Date of Birth is required"
                editTextRegisterDob.requestFocus()
            }
            radioGroupRegisterGender.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_LONG).show()
            }
            textMobile.isEmpty() -> {
                editTextRegisterMobile.error = "Mobile number is required"
                editTextRegisterMobile.requestFocus()
            }
            !mobileNumberPattern.matches(textMobile) -> {
                editTextRegisterMobile.error = "Enter a valid 10-digit mobile number starting with 6, 7, 8, or 9"
                editTextRegisterMobile.requestFocus()
            }
            textAddress.isEmpty() -> {
                editTextRegisterAddress.error = "Address is required"
                editTextRegisterAddress.requestFocus()
            }
            textEducation.isEmpty() -> {
                editTextRegisterEducation.error = "Education details are required"
                editTextRegisterEducation.requestFocus()
            }
            textPwd.isEmpty() -> {
                editTextRegisterPwd.error = "Password is required"
                editTextRegisterPwd.requestFocus()
            }
            textPwd.length < 6 -> {
                editTextRegisterPwd.error = "Password too weak"
                editTextRegisterPwd.requestFocus()
            }
            textConfirmPwd.isEmpty() -> {
                editTextRegisterConfirmPwd.error = "Password Confirmation is required"
                editTextRegisterConfirmPwd.requestFocus()
            }
            textPwd != textConfirmPwd -> {
                editTextRegisterConfirmPwd.error = "Passwords do not match"
                editTextRegisterPwd.text.clear()
                editTextRegisterConfirmPwd.text.clear()
                editTextRegisterConfirmPwd.requestFocus()
            }
            else -> {
                val selectedGenderId = radioGroupRegisterGender.checkedRadioButtonId
                val radioButtonGenderSelected = findViewById<RadioButton>(selectedGenderId)
                val textGender = radioButtonGenderSelected.text.toString()

                progressBar.visibility = View.VISIBLE
                registerUser(textFullName, textEmail, textDob, textGender, textMobile, textAddress, textEducation, textPwd)
            }
        }
    }

    private fun registerUser(
        textFullName: String,
        textEmail: String,
        textDob: String,
        textGender: String,
        textMobile: String,
        textAddress: String,
        textEducation: String,
        textPwd: String
    ) {
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth
        auth.createUserWithEmailAndPassword(textEmail, textPwd)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = auth.currentUser
                    // Update display name
                    val profileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(textFullName)
                        .build()

                    firebaseUser?.updateProfile(profileChangeRequest)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            // Send email verification
                            firebaseUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    Toast.makeText(this, "Verification email sent to ${firebaseUser.email}", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this, "Failed to send verification email: ${verificationTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }

                            // Save additional user details to Firebase Database
                            val userId = firebaseUser.uid
                            val databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers")

                            val userDetails = ReadWriteUserDetails(
                                textFullName,
                                textDob,
                                textGender,
                                textMobile,
                                textAddress,
                                textEducation
                            )

                            databaseReference.child(userId).setValue(userDetails)
                                .addOnCompleteListener { saveTask ->
                                    if (saveTask.isSuccessful) {
                                        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                                        // Navigate to HomeActivity
                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Failed to save user details: ${saveTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Failed to update profile: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
