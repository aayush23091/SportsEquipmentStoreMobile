package com.example.sportsequipmentstore.repository

import com.example.sportsequipmentstore.model.OrderModel
import com.google.firebase.database.*

class OrderRepositoryImpl {

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("orders")
    private val orderList = mutableListOf<OrderModel>()

    fun addOrder(order: OrderModel, callback: (Boolean, String) -> Unit) {
        val key = dbRef.push().key
        if (key != null) {
            order.orderId = key
            dbRef.child(key).setValue(order)
                .addOnSuccessListener { callback(true, "Order Placed") }
                .addOnFailureListener { callback(false, "Failed: ${it.message}") }
        } else {
            callback(false, "Failed to generate order ID")
        }
    }

    fun getAllOrders(callback: (List<OrderModel>) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (orderSnap in snapshot.children) {
                    val order = orderSnap.getValue(OrderModel::class.java)
                    order?.let { orderList.add(it) }
                }
                callback(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteOrder(orderId: String, callback: (Boolean, String) -> Unit) {
        dbRef.child(orderId).removeValue()
            .addOnSuccessListener {
                // Safe API-compatible removal (instead of removeIf)
                val iterator = orderList.iterator()
                var removed = false
                while (iterator.hasNext()) {
                    if (iterator.next().orderId == orderId) {
                        iterator.remove()
                        removed = true
                        break
                    }
                }
                callback(true, "Order deleted")
            }
            .addOnFailureListener {
                callback(false, "Failed: ${it.message}")
            }
    }
}
