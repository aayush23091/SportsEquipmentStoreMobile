package com.example.sportsequipmentstore.view

import AddressRepositoryImpl
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.sportsequipmentstore.model.AddressModel
import com.example.sportsequipmentstore.ui.theme.SportsEquipmentStoreTheme
import com.example.sportsequipmentstore.viewmodel.AddressViewModel
import com.example.sportsequipmentstore.viewmodel.AddressViewModelFactory


class AddressActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = AddressRepositoryImpl()
        val viewModel = ViewModelProvider(
            this,
            AddressViewModelFactory(repo)
        )[AddressViewModel::class.java]

        setContent {
            SportsEquipmentStoreTheme {
                AddressScreen(viewModel = viewModel,
                    onAddressSelected = { address ->
                        Toast.makeText(
                            this,
                            "Selected: ${address.fullName}, ${address.city}",
                            Toast.LENGTH_SHORT
                        ).show()
                        // TODO: Handle selected address for checkout
                        finish()
                    })
            }
        }

        viewModel.loadAddresses()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AddressViewModel,
    onAddressSelected: (AddressModel) -> Unit
) {
    val addresses by viewModel.addresses.observeAsState(initial = emptyList())
    val loading by viewModel.loading.observeAsState(initial = false)
    var fullName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressLine by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manage Addresses") })
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // Input fields for adding new address
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = addressLine,
                onValueChange = { addressLine = it },
                label = { Text("Address Line") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || phone.isBlank() || city.isBlank() || addressLine.isBlank()) {
                        // Show error
                        return@Button
                    }
                    val newAddress = AddressModel(
                        id = "",  // ID can be autogenerated in repo
                        fullName = fullName,
                        phoneNumber = phone,
                        city = city,
                        streetAddress = addressLine
                    )
                    viewModel.addAddress(newAddress) { success, msg ->
                        if (success) {
                            fullName = ""
                            phone = ""
                            city = ""
                            addressLine = ""
                        }
                        // You can also show a Toast or Snackbar here with msg
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Address")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(addresses) { address ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = address.fullName, style = MaterialTheme.typography.titleMedium)
                                    Text(text = address.phoneNumber)
                                    Text(text = "${address.city}, ${address.streetAddress}")
                                }
                                Column {
                                    TextButton(onClick = {
                                        onAddressSelected(address)
                                    }) {
                                        Text("Select")
                                    }
                                    TextButton(onClick = {
                                        viewModel.deleteAddress(address.id) { success, msg ->
                                            // Optionally handle feedback
                                        }
                                    }) {
                                        Text("Delete", color = MaterialTheme.colorScheme.error)
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
