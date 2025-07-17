
package com.example.sportsequipmentstore.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sportsequipmentstore.model.CartItemModel
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

        // Initialize CartViewModel
        val cartRepo = CartRepositoryImpl()
        val cartFactory = CartViewModelFactory(cartRepo)
        cartViewModel = androidx.lifecycle.ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        setContent {
            DashboardBody(cartViewModel = cartViewModel)
        }
    }
}

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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, AddProductActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = Green)
        ) {
            if (loading.value) {
                item {
                    // Show any loading UI here if you want
                }
            } else {
                items(products.value.size) { index ->
                    val eachProduct = products.value[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
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
                                        contentColor = Color.Black
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

                                IconButton(
                                    onClick = {
                                        eachProduct?.let {
                                            val cartItem = CartItemModel(
                                                id = "", // Let repo or backend generate ID
                                                productName = it.productName,
                                                productPrice = it.productPrice,
                                                image = it.image,
                                                quantity = 1
                                            )
                                            cartViewModel.addToCart(cartItem)
                                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = Color.Green
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add to Cart")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
