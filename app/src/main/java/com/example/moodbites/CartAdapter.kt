package com.example.moodbites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodbites.databinding.ItemCartBinding

class CartAdapter(
    private val cartItems: MutableList<FoodItem>,
    private val onQuantityChanged: (FoodItem) -> Unit,
    private val onItemRemoved: (FoodItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val foodItem = cartItems[position]
        holder.bind(foodItem)

        // Handle Plus and Minus Button Clicks
        holder.binding.plusButton.setOnClickListener {
            foodItem.quantity += 1
            onQuantityChanged(foodItem)  // Trigger the quantity change callback
            notifyItemChanged(position)
        }

        holder.binding.minusButton.setOnClickListener {
            if (foodItem.quantity > 1) {
                foodItem.quantity -= 1
                onQuantityChanged(foodItem)  // Trigger the quantity change callback
                notifyItemChanged(position)
            } else {
                cartItems.removeAt(position)
                onItemRemoved(foodItem)  // Trigger the item removal callback
                notifyItemRemoved(position)
            }
        }
    }

    fun removeItem(item: FoodItem) {
        val position = cartItems.indexOf(item)
        if (position >= 0) {
            cartItems.removeAt(position) // This removes the item from the list
            notifyItemRemoved(position)  // Notifies the adapter to update the view
        }
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(foodItem: FoodItem) {
            binding.foodName.text = foodItem.name
            binding.foodPrice.text = "$${foodItem.price}"
            binding.foodQuantity.text = "${foodItem.quantity}" // Display quantity
        }
    }
}
