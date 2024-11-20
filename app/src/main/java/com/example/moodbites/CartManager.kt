package com.example.moodbites

class CartManager private constructor() {
    private val cartItems: MutableList<FoodItem> = mutableListOf()

    // Singleton instance
    companion object {
        val instance: CartManager by lazy { CartManager() }
    }

    fun retrieveCartItems(): List<FoodItem> {
        return cartItems
    }

    fun addToCart(item: FoodItem) {
        // Check if the item is already in the cart and increase the quantity
        val existingItem = cartItems.find { it.name == item.name }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            cartItems.add(item)
        }
    }

    fun removeItem(item: FoodItem) {
        cartItems.remove(item)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun updateItemQuantity(item: FoodItem, newQuantity: Int) {
        val existingItem = cartItems.find { it.name == item.name }
        if (existingItem != null) {
            existingItem.quantity = newQuantity
        }
    }
}
