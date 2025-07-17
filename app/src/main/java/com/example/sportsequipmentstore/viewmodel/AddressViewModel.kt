package com.example.sportsequipmentstore.viewmodel

import AddressRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportsequipmentstore.model.AddressModel

class AddressViewModel(private val repo: AddressRepository) : ViewModel() {

    private val _addresses = MutableLiveData<List<AddressModel>>()
    val addresses: LiveData<List<AddressModel>> get() = _addresses

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun loadAddresses() {
        _loading.postValue(true)
        repo.getAllAddresses {
            _addresses.postValue(it)
            _loading.postValue(false)
        }
    }

    fun addAddress(address: AddressModel, callback: (Boolean, String) -> Unit) {
        _loading.postValue(true)
        repo.addAddress(address) { success, msg ->
            _loading.postValue(false)
            callback(success, msg)
        }
    }

    fun deleteAddress(id: String, callback: (Boolean, String) -> Unit) {
        _loading.postValue(true)
        repo.deleteAddress(id) { success, msg ->
            _loading.postValue(false)
            callback(success, msg)
        }
    }
}
