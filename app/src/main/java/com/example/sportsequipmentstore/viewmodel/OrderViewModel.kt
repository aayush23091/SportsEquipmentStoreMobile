import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportsequipmentstore.model.OrderModel
import com.example.sportsequipmentstore.repository.OrderRepository

//package com.example.sportsequipmentstore.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.sportsequipmentstore.model.OrderModel
//import com.example.sportsequipmentstore.repository.OrderRepository
//
//class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {
//
//    private val _orders = MutableLiveData<List<OrderModel>>()
//    val orders: LiveData<List<OrderModel>> get() = _orders
//
//    private val _error = MutableLiveData<String?>()
//    val error: LiveData<String?> get() = _error
//
//    private val _userOrders = MutableLiveData<List<OrderModel>>()
//    val userOrders: LiveData<List<OrderModel>> = _userOrders
//
//    // Load all orders (not just by user)
//    fun loadAllOrders() {
//        orderRepository.getAllOrders { list, success, message ->
//            if (success) {
//                _orders.postValue(list)
//                _error.postValue(null)
//            } else {
//                _error.postValue(message)
//            }
//        }
//    }
//
//    // Get orders by specific userId
//    fun getOrdersByUser(userId: String) {
//        orderRepository.getOrdersByUser(userId) { list, success, message ->
//            if (success) {
//                _orders.postValue(list)
//                _error.postValue(null)
//            } else {
//                _error.postValue(message)
//            }
//        }
//    }
//
//    fun loadOrdersByUser(userId: String) {
//        orderRepository.getOrdersByUser(userId) { list, success, message ->
//            if (success) {
//                _orders.postValue(list)
//                _error.postValue(null)
//            } else {
//                _error.postValue(message)
//            }
//        }
//    }
//
//    fun placeOrder(order: OrderModel) {
//        orderRepository.placeOrder(order) { success, message ->
//            if (success) {
//                loadAllOrders() // Refresh after placing order
//            } else {
//                _error.postValue(message)
//            }
//        }
//    }
//
//    fun cancelOrder(orderId: String) {
//        orderRepository.cancelOrder(orderId) { success, message ->
//            if (success) {
//                loadAllOrders() // Refresh after cancel
//            } else {
//                _error.postValue(message)
//            }
//        }
//    }
//
//    fun clearError() {
//        _error.postValue(null)
//    }
//}



class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _userOrders = MutableLiveData<List<OrderModel>>()
    val userOrders: LiveData<List<OrderModel>> = _userOrders

    private val _allOrders = MutableLiveData<List<OrderModel>>()
    val allOrders: LiveData<List<OrderModel>> = _allOrders

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Load all orders (for admin or global views)
    fun loadAllOrders() {
        orderRepository.getAllOrders { list, success, message ->
            if (success) {
                _allOrders.postValue(list)
                _error.postValue(null)
            } else {
                _error.postValue(message)
            }
        }
    }

    // Load orders for a specific user (this is what you want for dashboard)
    fun loadOrdersByUser(userId: String) {
        orderRepository.getOrdersByUser(userId) { list, success, message ->
            if (success) {
                _userOrders.postValue(list)
                _error.postValue(null)
            } else {
                _error.postValue(message)
            }
        }
    }

    // Place order and reload user orders on success
    fun placeOrder(order: OrderModel) {
        orderRepository.placeOrder(order) { success, message ->
            if (success) {
                loadOrdersByUser(order.userId)  // Reload user's orders
                _error.postValue(null)
            } else {
                _error.postValue(message)
            }
        }
    }

    // Cancel order and reload user orders on success
    fun cancelOrder(orderId: String, userId: String) {
        orderRepository.cancelOrder(orderId) { success, message ->
            if (success) {
                loadOrdersByUser(userId) // Reload user's orders
                _error.postValue(null)
            } else {
                _error.postValue(message)
            }
        }
    }

    fun clearError() {
        _error.postValue(null)
    }
}
