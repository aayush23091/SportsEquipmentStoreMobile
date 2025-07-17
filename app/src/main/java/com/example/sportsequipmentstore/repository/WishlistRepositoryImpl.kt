package com.example.sportsequipmentstore.repository

import com.example.sportsequipmentstore.model.WishlistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WishlistRepositoryImplementation {

    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> = _wishlistItems

    fun addToWishlist(item: WishlistItem) {
        _wishlistItems.value = _wishlistItems.value + item
    }

    fun removeFromWishlist(item: WishlistItem) {
        _wishlistItems.value = _wishlistItems.value.filter { it.id != item.id }
    }
}
