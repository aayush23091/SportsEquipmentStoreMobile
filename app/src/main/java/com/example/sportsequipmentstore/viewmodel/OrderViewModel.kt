package com.example.sportsequipmentstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportsequipmentstore.model.OrderModel
import com.example.sportsequipmentstore.repository.OrderRepository

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _orders = MutableLiveData<List<OrderModel>>()
    val orders: LiveData<List<OrderModel>> get() = _orders

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun placeOrder(order: OrderModel) {
        orderRepository.placeOrder(order) { success, message ->
            if (success) {
                // Refresh orders after placing one
                loadOrders(order.userId)
            } else {
                _error.postValue(message)
            }
        }
    }

    fun loadOrders(userId: String) {
        orderRepository.getOrdersByUser(userId) { list, success, message ->
            if (success) {
                _orders.postValue(list)
            } else {
                _error.postValue(message)
            }
        }
    }

    fun cancelOrder(orderId: String) {
        orderRepository.cancelOrder(orderId) { success, message ->
            if (!success) {
                _error.postValue(message)
            }
        }
    }

    fun clearError() {
        _error.postValue(null)
    }
}
