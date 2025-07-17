
package com.example.sportsequipmentstore.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sportsequipmentstore.LoginActivity
import com.example.sportsequipmentstore.model.CartItemModel
import com.example.sportsequipmentstore.model.WishlistItemModel
import com.example.sportsequipmentstore.repository.CartRepositoryImpl
import com.example.sportsequipmentstore.repository.ProductRepositoryImpl
import com.example.sportsequipmentstore.repository.WishlistRepositoryImpl
import com.example.sportsequipmentstore.viewmodel.*

class UserDashboardActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var wishlistViewModel: WishlistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepo = CartRepositoryImpl()
        val wishlistRepo = WishlistRepositoryImpl // singleton instance

        cartViewModel = androidx.lifecycle.ViewModelProvider(
            this,
            CartViewModelFactory(cartRepo)
        )[CartViewModel::class.java]

        wishlistViewModel = androidx.lifecycle.ViewModelProvider(
            this,
            WishlistViewModelFactory(wishlistRepo)
        )[WishlistViewModel::class.java]

        setContent {
            UserDashboardBody(cartViewModel, wishlistViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardBody(
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel
) {
    val context = LocalContext.current
    val repo = remember { ProductRepositoryImpl() }
    val productViewModel = remember { ProductViewModel(repo) }

    val products by productViewModel.allProducts.observeAsState(initial = emptyList())
    val loading by productViewModel.loading.observeAsState(initial = true)
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productViewModel.getAllProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RetroCrugSports") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, EditProfileActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Person, contentDescription = "Edit Profile", tint = Color.White)
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    menuExpanded = false
                                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, CartActivity::class.java))
                    },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                    label = { Text("Cart") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, WishlistActivity::class.java))
                    },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist") },
                    label = { Text("Wishlist") }
                )
            }
        },
        modifier = Modifier.background(Color(0xFF4CAF50))
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
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
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(onClick = {
                                    val cartItem = CartItemModel(
                                        id = "",
                                        productName = product?.productName ?: "",
                                        productPrice = product?.productPrice ?: 0.0,
                                        image = product?.image ?: "",
                                        quantity = 1
                                    )
                                    cartViewModel.addToCart(cartItem)
                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                }) {
                                    Text("Add to Cart")
                                }

                                OutlinedButton(onClick = {
                                    val item = WishlistItemModel(
                                        productName = product?.productName ?: "",
                                        productPrice = product?.productPrice ?: 0.0,
                                        image = product?.image ?: ""
                                    )
                                    wishlistViewModel.addToWishlist(item)
                                    Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show()
                                }) {
                                    Text("❤️ Wishlist")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
