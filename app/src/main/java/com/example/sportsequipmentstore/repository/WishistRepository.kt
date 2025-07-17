package com.example.sportsequipmentstore.repository

import com.example.sportsequipmentstore.model.WishlistItemModel

interface WishlistRepository {
    fun getWishlist(): List<WishlistItemModel>
    fun addToWishlist(item: WishlistItemModel)
    fun removeFromWishlist(item: WishlistItemModel)
    fun clearWishlist()
}
