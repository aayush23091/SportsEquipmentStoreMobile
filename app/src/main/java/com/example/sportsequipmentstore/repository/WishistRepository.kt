package com.example.sportsequipmentstore.repository

import com.example.sportsequipmentstore.model.WishlistItem

interface WishlistRepository {
    fun getWishlist(): List<WishlistItem>
    fun addToWishlist(item: WishlistItem)
    fun removeFromWishlist(item: WishlistItem)
    fun clearWishlist()
}
