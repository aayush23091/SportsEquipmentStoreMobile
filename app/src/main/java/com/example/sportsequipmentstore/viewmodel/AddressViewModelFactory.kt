package com.example.sportsequipmentstore.viewmodel

import AddressRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddressViewModelFactory(private val repo: AddressRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddressViewModel(repo) as T
    }
}
