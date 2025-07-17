package com.example.sportsequipmentstore.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sportsequipmentstore.model.WishlistItem
import com.example.sportsequipmentstore.repository.WishlistRepositoryImplementation
import kotlinx.coroutines.flow.StateFlow

class WishlistViewModel(
    private val repo: WishlistRepositoryImplementation
) : ViewModel() {

    val wishlistItems: StateFlow<List<WishlistItem>> = repo.wishlistItems

    fun addToWishlist(item: WishlistItem) {
        repo.addToWishlist(item)
    }

    fun removeFromWishlist(item: WishlistItem) {
        repo.removeFromWishlist(item)
    }
}
