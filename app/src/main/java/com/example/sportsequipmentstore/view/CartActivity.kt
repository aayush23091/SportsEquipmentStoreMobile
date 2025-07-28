
package com.example.sportsequipmentstore.view

import OrderViewModel
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState
import coil.compose.rememberAsyncImagePainter
import com.example.sportsequipmentstore.model.CartItemModel
import com.example.sportsequipmentstore.model.OrderModel
import com.example.sportsequipmentstore.repository.CartRepositoryImpl
import com.example.sportsequipmentstore.repository.OrderRepositoryImpl
import com.example.sportsequipmentstore.viewmodel.CartViewModel
import com.example.sportsequipmentstore.viewmodel.CartViewModelFactory
import com.example.sportsequipmentstore.viewmodel.OrderViewModelFactory
import com.example.sportsequipmentstore.ui.theme.SportsEquipmentStoreTheme

class CartActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepo = CartRepositoryImpl()
        val orderRepo = OrderRepositoryImpl()

        val cartFactory = CartViewModelFactory(cartRepo)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        val orderFactory = OrderViewModelFactory(orderRepo)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        cartViewModel.loadCartItems()

        setContent {
            SportsEquipmentStoreTheme {
                CartScreen(cartViewModel = cartViewModel, orderViewModel = orderViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartViewModel: CartViewModel, orderViewModel: OrderViewModel) {
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    val errorMessage by cartViewModel.error.observeAsState()
    val orderError by orderViewModel.error.observeAsState()
    val context = LocalContext.current

    // Colors matching Wishlist's muted rose theme
    val mutedRose = Color(0xFFE57373)
    val lightMutedRose = Color(0xFFFCECEC)
    val cardBackground = Color(0xFFF8D7DA) // soft pinkish for card background
    val errorColor = Color(0xFFB00020)
    val textColor = mutedRose.copy(alpha = 0.9f)

    val totalPrice = cartItems.sumOf { it.productPrice * it.quantity }

    LaunchedEffect(orderError) {
        orderError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            orderViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", fontSize = 20.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mutedRose)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(lightMutedRose)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (!errorMessage.isNullOrEmpty()) {
                    Text(text = errorMessage ?: "", color = errorColor)
                }

                if (cartItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your cart is empty.", color = mutedRose)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(cartItems) { item ->
                            CartItemCard(
                                item = item,
                                onIncrease = {
                                    cartViewModel.updateQuantity(item.id, item.quantity + 1)
                                },
                                onDecrease = {
                                    if (item.quantity > 1) {
                                        cartViewModel.updateQuantity(item.id, item.quantity - 1)
                                    }
                                },
                                onRemove = {
                                    cartViewModel.removeCartItem(item.id)
                                },
                                cardColor = cardBackground,
                                textColor = textColor,
                                priceColor = mutedRose,
                                errorColor = errorColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total:", fontSize = 18.sp, color = textColor)
                        Text("Rs. ${"%.2f".format(totalPrice)}", fontSize = 20.sp, color = textColor)
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        onClick = {
                            if (cartItems.isNotEmpty()) {
                                val userId = "USR001" // Replace with actual user ID
                                val order = OrderModel(
                                    orderId = "",
                                    userId = userId,
                                    items = cartItems,
                                    totalAmount = totalPrice,
                                    orderStatus = "Pending"
                                )
                                orderViewModel.placeOrder(order)
                                Toast.makeText(context, "Order placed!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = mutedRose, contentColor = Color.White)
                    ) {
                        Text("Proceed to Checkout")
                    }
                }
            }
        }
    )
}

@Composable
fun CartItemCard(
    item: CartItemModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    cardColor: Color,
    textColor: Color,
    priceColor: Color,
    errorColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.image),
                contentDescription = item.productName,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.productName, fontSize = 18.sp, color = textColor)
                Text(text = "Rs. ${item.productPrice}", fontSize = 14.sp, color = priceColor)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Button(onClick = onDecrease, contentPadding = PaddingValues(4.dp), colors = ButtonDefaults.buttonColors(containerColor = priceColor, contentColor = Color.White)) {
                        Text("-", fontSize = 20.sp)
                    }
                    Text(
                        text = "${item.quantity}",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontSize = 18.sp,
                        color = textColor
                    )
                    Button(onClick = onIncrease, contentPadding = PaddingValues(4.dp), colors = ButtonDefaults.buttonColors(containerColor = priceColor, contentColor = Color.White)) {
                        Text("+", fontSize = 20.sp)
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = errorColor)
            }
        }
    }
}
