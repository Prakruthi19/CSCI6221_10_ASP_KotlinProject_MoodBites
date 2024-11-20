package com.example.moodbites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomePageActivity: AppCompatActivity() {

    private lateinit var createAnAccountButton: Button
    private lateinit var  loginText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)

        loginText= findViewById(R.id.alreadyHav)

        createAnAccountButton = findViewById(R.id.createAnAccount)
        // Set click listener for the login text
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        createAnAccountButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}

