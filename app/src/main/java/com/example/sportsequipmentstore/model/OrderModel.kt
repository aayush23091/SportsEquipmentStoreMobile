package com.example.sportsequipmentstore.model

import com.example.sportsequipmentstore.model.CartItemModel

data class OrderModel(
    var orderId: String = "",
    var userId: String = "",
    var items: List<CartItemModel> = emptyList(),
    var totalAmount: Double = 0.0,
    var orderStatus: String = "Pending", // e.g., Pending, Shipped, Delivered, Cancelled
    var orderDate: Long = System.currentTimeMillis()
)
