package com.example.moodbites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodbites.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve cart items from CartManager
        val cartItems = CartManager.instance.retrieveCartItems()

        // Set up RecyclerView for cart
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(cartItems.toMutableList(),  // Ensure this is mutable
            onQuantityChanged = { foodItem ->
                CartManager.instance.updateItemQuantity(foodItem, foodItem.quantity)
                updateTotalPrice()
            },
            onItemRemoved = { foodItem ->
                CartManager.instance.removeItem(foodItem)
                updateTotalPrice()
            }
        )
        binding.cartRecyclerView.adapter = adapter

        updateTotalPrice()

        // Checkout button functionality
        binding.checkoutButton.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, OrderActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice() {
        val totalPrice = CartManager.instance.retrieveCartItems().sumOf { it.price * it.quantity }
        binding.totalPriceText.text = "Total: $${"%.2f".format(totalPrice)}"
    }
}
