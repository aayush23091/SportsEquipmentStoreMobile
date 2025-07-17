package com.example.sportsequipmentstore.model

data class CartItemModel(
    var id: String = "",
    var productId: String = "",
    var productName: String = "",
    var productPrice: Double = 0.0,
    var quantity: Int = 1,
    var image: String = ""
)