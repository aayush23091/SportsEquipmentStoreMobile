

package com.example.sportsequipmentstore.view

import OrderViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.sportsequipmentstore.LoginActivity
import com.example.sportsequipmentstore.R
import com.example.sportsequipmentstore.model.CartItemModel
import com.example.sportsequipmentstore.model.ProductModel
import com.example.sportsequipmentstore.model.WishlistItemModel
import com.example.sportsequipmentstore.repository.*
import com.example.sportsequipmentstore.viewmodel.*

class UserDashboardActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cartViewModel = ViewModelProvider(this, CartViewModelFactory(CartRepositoryImpl()))[CartViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this, WishlistViewModelFactory(WishlistRepositoryImpl))[WishlistViewModel::class.java]
        userViewModel = ViewModelProvider(this, UserViewModelFactory(UserRepositoryImplementation()))[UserViewModel::class.java]
        orderViewModel = ViewModelProvider(this, OrderViewModelFactory(OrderRepositoryImpl()))[OrderViewModel::class.java]

        setContent {
            UserDashboardBody(cartViewModel, wishlistViewModel, userViewModel, orderViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUserId = userViewModel.getCurrentUser()?.uid
        currentUserId?.let {
            userViewModel.getUserById(it)
            orderViewModel.loadOrdersByUser(it)  // Load orders only for current user!
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardBody(
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    userViewModel: UserViewModel,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val productRepo = remember { ProductRepositoryImpl() }
    val productViewModel = remember { ProductViewModel(productRepo) }

    val currentUserId = userViewModel.getCurrentUser()?.uid
    val user by userViewModel.users.observeAsState()
    val filteredProducts by productViewModel.filteredProducts.observeAsState(emptyList())
    val orders by orderViewModel.userOrders.observeAsState(emptyList()) // ✅ Fix
    val loading by productViewModel.loading.observeAsState(true)

    var menuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(currentUserId) {
        currentUserId?.let {
            userViewModel.getUserById(it)
            orderViewModel.loadOrdersByUser(it)  // Ensure orders load for current user
        }
        productViewModel.getAllProducts()
    }

    LaunchedEffect(searchQuery) {
        productViewModel.filterProducts(searchQuery)
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
                        context.startActivity(Intent(context, EditProfileActivity::class.java))
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
                                text = { Text("Address Book") },
                                onClick = {
                                    menuExpanded = false
                                    context.startActivity(Intent(context, AddressActivity::class.java))
                                }
                            )
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
                    onClick = { context.startActivity(Intent(context, CartActivity::class.java)) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                    label = { Text("Cart") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { context.startActivity(Intent(context, WishlistActivity::class.java)) },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist") },
                    label = { Text("Wishlist") }
                )
            }
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            UserHeader(user)



            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )

                val categories = listOf("All", "Cricket", "Football", "Rugby", "Tennis")
                var selectedCategory by remember { mutableStateOf("All") }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, bottom = 8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                productViewModel.filterByCategoryAndSearch(category, searchQuery)
                            },
                            label = { Text(category) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(filteredProducts) { product ->
                        product?.let {
                            ProductCard(it, cartViewModel, wishlistViewModel, context)
                        }
                    }
                }
            }

            if (orders.isNotEmpty()) {
                Text(
                    text = "Your Orders",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(0.4f) // restrict height to avoid scroll conflict
                ) {
                    items(orders) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Order ID: ${order.orderId}")
                                Text("Total: Rs. ${order.totalAmount}")
                                Text("Status: ${order.orderStatus}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserHeader(user: com.example.sportsequipmentstore.model.UserModel?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(12.dp)
    ) {
        val imageModifier = Modifier
            .size(48.dp)
            .background(Color.LightGray, CircleShape)

        if (!user?.image.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user?.image)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .build(),
                contentDescription = "Profile Picture",
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.profilepicplaceholder)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile",
                modifier = imageModifier.padding(8.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Welcome, ${user?.firstName ?: "User"}!",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            AsyncImage(
                model = product.image,
                contentDescription = product.productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.imageplaceholder)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = product.productName ?: "Unnamed", style = MaterialTheme.typography.titleMedium)
            Text(text = "Rs. ${product.productPrice ?: 0}", style = MaterialTheme.typography.bodyLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    val cartItem = CartItemModel(
                        productId = product.productId ?: "",
                        productName =  product.productName ?: "",
                        productPrice = product.productPrice ?: 0.0,
                        image = product.image ?: "",
                        quantity = 1
                    )
                    cartViewModel.addToCart(cartItem)
                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Add to Cart")
                }

                IconButton(onClick = {
                    val wishlistItem = WishlistItemModel(
                        productName = product.productName ?: "",
                        productPrice = product.productPrice?: 0.0,
                        image = product.image ?: ""
                    )
                    wishlistViewModel.addToWishlist(wishlistItem)
                    Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist")
                }
            }
        }
    }
}





