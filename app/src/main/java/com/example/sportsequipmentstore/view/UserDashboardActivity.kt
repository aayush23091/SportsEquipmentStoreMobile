package com.example.sportsequipmentstore.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sportsequipmentstore.model.CartItemModel
import com.example.sportsequipmentstore.repository.CartRepositoryImpl
import com.example.sportsequipmentstore.repository.ProductRepositoryImpl
import com.example.sportsequipmentstore.viewmodel.CartViewModel
import com.example.sportsequipmentstore.viewmodel.CartViewModelFactory
import com.example.sportsequipmentstore.viewmodel.ProductViewModel

class UserDashboardActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepo = CartRepositoryImpl()
        val cartFactory = CartViewModelFactory(cartRepo)
        cartViewModel = androidx.lifecycle.ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        setContent {
            UserDashboardBody(cartViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardBody(cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val repo = remember { ProductRepositoryImpl() }
    val productViewModel = remember { ProductViewModel(repo) }

    val products by productViewModel.allProducts.observeAsState(initial = emptyList())
    val loading by productViewModel.loading.observeAsState(initial = true)

    LaunchedEffect(Unit) {
        productViewModel.getAllProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RetroCrugSports") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50) // Green background for TopAppBar
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, CartActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "View Cart")
            }
        },
        modifier = Modifier.background(Color(0xFF4CAF50)) // Green background for Scaffold content
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp)
            ) {
                items(products.size) { index ->
                    val product = products[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)) // Green background
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = product?.productName ?: "No Name",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Rs. ${product?.productPrice ?: 0}",
                                color = Color.White
                            )
                            Text(
                                text = product?.productDescription ?: "",
                                color = Color.White
                            )

                            Button(
                                onClick = {
                                    val cartItem = CartItemModel(
                                        id = "",
                                        productName = product?.productName ?: "",
                                        productPrice = product?.productPrice ?: 0.0,
                                        image = product?.image ?: "",
                                        quantity = 1
                                    )
                                    cartViewModel.addToCart(cartItem)
                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Add to Cart")
                            }
                        }
                    }
                }
            }
        }
    }
}
