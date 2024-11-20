package com.example.moodbites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodbites.databinding.ItemMoodBinding

class MoodAdapter(
    private var moodList: List<String>,
    private val onMoodClick: (String) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(private val binding: ItemMoodBinding) :

        RecyclerView.ViewHolder(binding.root) {
        fun bind(mood: String) {
            binding.moodCard.findViewById<TextView>(R.id.moodText).text = mood
            binding.moodCard.setOnClickListener {
                onMoodClick(mood)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodList[position])
    }

    override fun getItemCount(): Int = moodList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateMoodList(newMoods: List<String>) {
        this.moodList = newMoods
        notifyDataSetChanged()
    }

}
