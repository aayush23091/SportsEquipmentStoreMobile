
package com.example.sportsequipmentstore.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.sportsequipmentstore.LoginActivity
import com.example.sportsequipmentstore.repository.CartRepositoryImpl
import com.example.sportsequipmentstore.repository.ProductRepositoryImpl
import com.example.sportsequipmentstore.viewmodel.CartViewModel
import com.example.sportsequipmentstore.viewmodel.CartViewModelFactory
import com.example.sportsequipmentstore.viewmodel.ProductViewModel

class DashboardActivity : ComponentActivity() {
    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val cartRepo = CartRepositoryImpl()
        val cartFactory = CartViewModelFactory(cartRepo)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        setContent {
            DashboardBody(cartViewModel = cartViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

    val products = viewModel.allProducts.observeAsState(initial = emptyList())
    val loading = viewModel.loading.observeAsState(initial = true)

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }

    var selectedTab by remember { mutableStateOf(0) }

    // Modern soft light red
    val softRed = Color(0xFFE57373)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", color = Color.White) },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = softRed)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, AddProductActivity::class.java)
                context.startActivity(intent)
            }, containerColor = softRed) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
            }
        },
        bottomBar = {
            NavigationBar(containerColor = softRed) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        context.startActivity(Intent(context, OrderActivity::class.java))
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = "Orders") },
                    label = { Text("Orders") }
                )
            }
        }
    ) { innerPadding ->
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(color = Color.White)
            ) {
                if (loading.value) {
                    item {
                        Text(
                            text = "Loading products...",
                            color = softRed,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(products.value) { eachProduct ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCECEC)) // light soft background
                        ) {
                            Column(modifier = Modifier.padding(15.dp)) {
                                Text(text = eachProduct?.productName ?: "No Name")
                                Text(text = "Rs. ${eachProduct?.productPrice ?: 0}")
                                Text(text = eachProduct?.productDescription ?: "")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(context, UpdateProductActivity::class.java)
                                            intent.putExtra("productId", eachProduct?.productId ?: "")
                                            context.startActivity(intent)
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            contentColor = softRed
                                        )
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Product")
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteProduct(eachProduct?.productId.toString()) { success, message ->
                                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            contentColor = Color.Red
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Product")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

