package com.example.moodbites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        // Bind views
        emailEditText = findViewById(R.id.emailEditText)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        backButton = findViewById(R.id.backButtonBlack)
        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase password reset function with error handling
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                        finish()  // Go back to LoginActivity
                    } else {
                        // Handle error if email is not found in Firebase Authentication
                        val exceptionMessage = task.exception?.message ?: ""
                        if (exceptionMessage.contains("no user record corresponding to this identifier", ignoreCase = true)) {
                            Toast.makeText(this, "There is no account linked to this email.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error: $exceptionMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        backButton.setOnClickListener {
            finish()
        }


    }
}
