package com.example.moodbites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView  // <-- Add this line to declare signUpTextView
    private lateinit var backButton: Button
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        var isPasswordVisible = false
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Bind views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpTextView = findViewById(R.id.signUpDescription)  // <-- Bind the TextView here
        backButton = findViewById(R.id.backButton)
        // Set up login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in with Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login success, go to main activity
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MoodSelectionActivity::class.java)
                        startActivity(intent)

                    } else {
                        // Login failed, display message
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = passwordEditText.compoundDrawables[2] // DrawableEnd is at index 2
                if (drawableEnd != null && event.rawX >= (passwordEditText.right - drawableEnd.bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        // Show password and set open-eye icon
                        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.lock_24px, 0, R.drawable.visibility_24px, 0
                        )
                    } else {
                        // Hide password and set closed-eye icon
                        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.lock_24px, 0, R.drawable.visibility_off_24px, 0
                        )
                    }
                    // Keep cursor position intact
                    passwordEditText.setSelection(passwordEditText.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Set up sign-up text view click listener
        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        backButton.setOnClickListener {
            finish()
        }

    }
}
