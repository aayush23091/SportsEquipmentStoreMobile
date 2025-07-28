
package com.example.sportsequipmentstore.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sportsequipmentstore.model.AddressModel
import com.example.sportsequipmentstore.repository.AddressRepositoryImpl
import com.example.sportsequipmentstore.viewmodel.AddressViewModel
import java.util.*

class AddressActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use the muted rose background color for the entire surface
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFFCECEC) // light muted rose background
            ) {
                AddressScreen(userId = "demo_user_id") // Replace with real userId
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(userId: String) {
    val primaryColor = Color(0xFFE57373) // muted rose
    val cardBackground = Color(0xFFF8D7DA) // soft pink for cards
    val textColor = primaryColor.copy(alpha = 0.9f)
    val errorColor = Color(0xFFB00020) // strong red for errors

    val viewModel = remember { AddressViewModel(AddressRepositoryImpl()) }
    val context = LocalContext.current

    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var addresses by remember { mutableStateOf(emptyList<AddressModel>()) }

    fun loadAddresses() {
        viewModel.getAddresses(userId) {
            addresses = it
        }
    }

    LaunchedEffect(Unit) { loadAddresses() }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = if (selectedId == null) "Add Address" else "Update Address",
            style = MaterialTheme.typography.titleLarge,
            color = primaryColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = addressLine,
            onValueChange = { addressLine = it },
            label = { Text("Address Line", color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = textColor)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City", color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = textColor)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("District", color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = textColor)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text("Postal Code", color = textColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = textColor)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val id = selectedId ?: UUID.randomUUID().toString()
                val address = AddressModel(
                    id = id,
                    userId = userId,
                    street = addressLine,
                    city = city,
                    district = state,
                    postalCode = postalCode
                )

                val action = if (selectedId == null) viewModel::addAddress else viewModel::updateAddress

                action(address) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        addressLine = ""
                        city = ""
                        state = ""
                        postalCode = ""
                        selectedId = null
                        loadAddresses()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
        ) {
            Text(
                if (selectedId == null) "Save Address" else "Update Address"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Saved Addresses",
            style = MaterialTheme.typography.titleMedium,
            color = primaryColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        addresses.forEach { address ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Address: ${address.street}", color = textColor)
                    Text("City: ${address.city}", color = textColor)
                    Text("District: ${address.district}", color = textColor)
                    Text("Postal Code: ${address.postalCode}", color = textColor)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            "Edit",
                            color = primaryColor,
                            modifier = Modifier.clickable {
                                selectedId = address.id
                                addressLine = address.street
                                city = address.city
                                state = address.district
                                postalCode = address.postalCode
                            }
                        )
                        Text(
                            "Delete",
                            color = errorColor,
                            modifier = Modifier.clickable {
                                viewModel.deleteAddress(address.id) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) loadAddresses()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
