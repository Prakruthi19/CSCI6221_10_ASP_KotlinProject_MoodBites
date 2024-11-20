package com.example.moodbites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodbites.databinding.ItemFoodBinding

class FoodAdapter(
    private var foodList: List<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit // Add this parameter for click handling
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(foodItem: FoodItem) {
            binding.foodName.text = foodItem.name
            binding.foodPrice.text = "$${foodItem.price}"
            binding.addToCartButton.setOnClickListener {
                onItemClick(foodItem) // Trigger the click listener when the button is clicked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int = foodList.size

    // Function to update the food list dynamically
    @SuppressLint("NotifyDataSetChanged")
    fun updateFoodList(newFoodList: List<FoodItem>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }
}
