package com.example.sportsequipmentstore.repository

import com.example.sportsequipmentstore.model.WishlistItemModel

class WishlistRepositoryImpl : WishlistRepository {

    private val wishlist = mutableListOf<WishlistItemModel>()

    override fun getWishlist(): List<WishlistItemModel> {
        return wishlist.toList() // Return a copy to prevent external mutation
    }

    override fun addToWishlist(item: WishlistItemModel) {
        if (!wishlist.any { it.id == item.id }) {
            wishlist.add(item)
        }
    }

    override fun removeFromWishlist(item: WishlistItemModel) {
        wishlist.removeAll { it.id == item.id }
    }


    override fun clearWishlist() {
        wishlist.clear()
    }
}
