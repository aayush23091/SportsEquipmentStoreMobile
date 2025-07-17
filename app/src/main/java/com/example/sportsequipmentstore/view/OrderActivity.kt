
package com.example.sportsequipmentstore.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportsequipmentstore.model.OrderModel

class OrderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen() {
    val dummyOrders = listOf(
        OrderModel(
            orderId = "ORD001",
            userId = "USR001",
            totalAmount = 1500.0,
            orderStatus = "Delivered"
        ),
        OrderModel(
            orderId = "ORD002",
            userId = "USR002",
            totalAmount = 3000.0,
            orderStatus = "Pending"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(dummyOrders) { order ->
                OrderCard(order)
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50) // Green background
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.orderId}", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("Status: ${order.orderStatus}", color = Color.White)
            Text("Total: Rs. ${order.totalAmount}", color = Color.White)
        }
    }
}
