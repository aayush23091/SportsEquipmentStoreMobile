
package com.example.sportsequipmentstore.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportsequipmentstore.model.WishlistItemModel
import com.example.sportsequipmentstore.repository.WishlistRepositoryImpl
import com.example.sportsequipmentstore.viewmodel.WishlistViewModel
import com.example.sportsequipmentstore.viewmodel.WishlistViewModelFactory

class WishlistActivity : ComponentActivity() {
    private lateinit var wishlistViewModel: WishlistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wishlistViewModel = androidx.lifecycle.ViewModelProvider(
            this,
            WishlistViewModelFactory(WishlistRepositoryImpl)
        )[WishlistViewModel::class.java]

        setContent {
            WishlistScreen(wishlistViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(viewModel: WishlistViewModel) {
    val context = LocalContext.current

    val wishlistItems by viewModel.wishlistItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Wishlist", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0F0F0))
        ) {
            if (wishlistItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your wishlist is empty.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(wishlistItems) { item ->
                        WishlistItemCard(item = item, onRemove = {
                            viewModel.removeFromWishlist(item)
                            Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(item: WishlistItemModel, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color.White),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.productName, fontSize = 18.sp, color = Color.Black)
            Text(text = "Price: Rs. ${item.productPrice}", color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRemove) {
                Text("Remove from Wishlist")
            }
        }
    }
}

