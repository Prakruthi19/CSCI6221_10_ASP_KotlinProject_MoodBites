package com.example.moodbites

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.moodbites.databinding.ActivityOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.placeOrderButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val addressLine1 = binding.addressLine1EditText.text.toString().trim()
            val addressLine2 = binding.addressLine2EditText.text.toString().trim()
            val city = binding.cityEditText.text.toString().trim()
            val state = binding.stateEditText.text.toString().trim()
            val postalCode = binding.postalCodeEditText.text.toString().trim()
            val paymentMethod = when (binding.paymentRadioGroup.checkedRadioButtonId) {
                binding.radioCash.id -> "Cash on Delivery"
                binding.radioCard.id -> "Credit/Debit Card"
                else -> ""
            }

            if (name.isEmpty() || addressLine1.isEmpty() || city.isEmpty() || state.isEmpty() || postalCode.isEmpty() || paymentMethod.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUserEmail = auth.currentUser?.email
            if (currentUserEmail == null) {
                Toast.makeText(this, "User is not logged in. Please log in to place an order.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cartItems = CartManager.instance.retrieveCartItems()
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val totalCost = cartItems.sumOf { it.price * it.quantity }
            val orderData = hashMapOf(
                "userEmail" to currentUserEmail,
                "name" to name,
                "address" to "$addressLine1, $addressLine2, $city, $state, $postalCode",
                "paymentMethod" to paymentMethod,
                "orderItems" to cartItems.map {
                    mapOf(
                        "itemName" to it.name,
                        "itemQuantity" to it.quantity,
                        "itemPrice" to it.price
                    )
                },
                "totalCost" to totalCost,
                "timestamp" to System.currentTimeMillis()
            )

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("orders")
                .add(orderData)
                .addOnSuccessListener {
                    Log.d("Firestore", "Order details successfully saved!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error saving order details: ${e.message}")
                }

            val orderSummary = generateOrderSummary(name, addressLine1, addressLine2, city, state, postalCode, paymentMethod, cartItems, totalCost)
            showOrderConfirmationPopup(orderSummary)
            sendOrderEmailSMTP(currentUserEmail, orderSummary)
        }
    }

    private fun generateOrderSummary(
        name: String, addressLine1: String, addressLine2: String,
        city: String, state: String, postalCode: String, paymentMethod: String,
        cartItems: List<FoodItem>, totalCost: Double
    ): String {
        val address = "$addressLine1, $addressLine2, $city, $state, $postalCode"
        val items = cartItems.joinToString("\n") {
            "${it.name} x${it.quantity} - $${"%.2f".format(it.price * it.quantity)}"
        }

        return """
        Order Summary:
        Name: $name
        Address: $address
        Payment Method: $paymentMethod

        Items:
        $items

        Total: $${"%.2f".format(totalCost)}
        Thank you for ordering from MoodBites!
    """.trimIndent()
    }

    private fun showOrderConfirmationPopup(orderSummary: String) {
        AlertDialog.Builder(this)
            .setTitle("Order Placed!")
            .setMessage("Thank you for your order. Here is your summary:\n\n$orderSummary")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun sendOrderEmailSMTP(email: String, orderSummary: String) {
        val smtpHost = "smtp.gmail.com"
        val smtpPort = "587"
        val smtpUsername = "moodbites.billing@gmail.com"
        val smtpPassword = "xwyu zeli oxvs iota "

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(smtpUsername, smtpPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(smtpUsername, "MoodBites"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                subject = "Your MoodBites Order Summary"
                setText(orderSummary)
            }

            Thread {
                try {
                    Transport.send(message)
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, "Order receipt sent successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("SMTP", "Error sending email", e)
                    runOnUiThread {
                        Toast.makeText(this@OrderActivity, "Failed to send email: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        } catch (e: Exception) {
            Log.e("SMTP", "Error setting up email", e)
            runOnUiThread {
                Toast.makeText(this@OrderActivity, "Email configuration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
