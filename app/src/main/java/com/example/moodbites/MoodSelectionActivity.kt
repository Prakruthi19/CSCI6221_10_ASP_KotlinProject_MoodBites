package com.example.moodbites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodbites.databinding.ActivityMoodSelectionBinding
import com.google.firebase.firestore.FirebaseFirestore

class MoodSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoodSelectionBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: MoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        adapter = MoodAdapter(emptyList()) { mood ->
            val intent = Intent(this, RecommendationActivity::class.java)
            intent.putExtra("MOOD", mood)
            startActivity(intent)
        }
        binding.moodRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.moodRecyclerView.adapter = adapter

        binding.moodRecyclerView.adapter = adapter

        // Fetch available moods from Firestore
        fetchAvailableMoods()
    }

    private fun fetchAvailableMoods() {
        firestore.collection("Moods")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreError", "Error fetching moods", error)
                    Toast.makeText(this, "Error fetching moods: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }



                if (snapshot != null && !snapshot.isEmpty) {
                    val moodList = snapshot.documents.map { it.id }
                    adapter.updateMoodList(moodList)
                } else {
                    Toast.makeText(this, "No moods available", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
