package com.example.moodbites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodbites.databinding.ActivityRecommendationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import android.app.Application


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Configure Firestore settings here
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings
    }
}
class RecommendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationBinding
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() // No settings applied here
    }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Redirect to LoginActivity if not authenticated
            Toast.makeText(this, "Please log in to continue.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Get the mood passed through intent or use a default
        val mood = intent.getStringExtra("MOOD") ?: "Happy"
        Log.d("MoodSelection", "Selected Mood: $mood")

        // Set up RecyclerView
        binding.foodRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = FoodAdapter(emptyList()) { foodItem: FoodItem ->
            CartManager.instance.addToCart(foodItem)
            Toast.makeText(this, "Added ${foodItem.name} to cart", Toast.LENGTH_SHORT).show()
        }
        binding.foodRecyclerView.adapter = adapter

        // Fetch foods for the selected mood
        fetchFoodsForMood(mood, adapter)

        // Button to view the cart
        binding.viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            val cartItems = CartManager.instance.retrieveCartItems()
            intent.putParcelableArrayListExtra("CART_ITEMS", ArrayList(cartItems))
            startActivity(intent)
        }

        // Debugging - Fetch all moods dynamically (Optional feature)
        fetchAvailableMoods()
    }

    private fun fetchFoodsForMood(mood: String, adapter: FoodAdapter) {
        firestore.collection("Moods").document(mood)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreError", "Error fetching data", error)
                    Toast.makeText(this, "Failed to fetch food items", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("FirestoreData", "Snapshot for mood: $mood -> ${snapshot.data}")
                    val foodList = snapshot.get("foods") as? List<Map<String, Any>> ?: emptyList()
                    val foodItems = foodList.mapNotNull {
                        val name = it["name"] as? String
                        val price = it["price"] as? Double
                        if (name != null && price != null) {
                            FoodItem(name, price)
                        } else {
                            Log.w("FirestoreData", "Invalid food entry: $it")
                            null
                        }
                    }
                    adapter.updateFoodList(foodItems)
                } else {
                    Log.w("FirestoreData", "No data found for mood: $mood")
                    Toast.makeText(this, "No food items available for $mood", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchAvailableMoods() {
        firestore.collection("Moods").get()
            .addOnSuccessListener { documents ->
                val moodList = documents.map { it.id }
                Log.d("FirestoreMoods", "Available moods: $moodList")
            }
            .addOnFailureListener { error ->
                Log.e("FirestoreError", "Failed to fetch moods", error)
            }
    }
}
