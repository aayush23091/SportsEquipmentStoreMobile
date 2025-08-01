
package com.example.sportsequipmentstore.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.sportsequipmentstore.LoginActivity
import com.example.sportsequipmentstore.R
import com.example.sportsequipmentstore.model.UserModel
import com.example.sportsequipmentstore.repository.UserRepositoryImplementation
import com.example.sportsequipmentstore.viewmodel.UserViewModel

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->
                RegBody(innerPadding)
            }
        }
    }
}

@Composable
fun RegBody(innerPaddingValues: PaddingValues) {
    val mutedRose = Color(0xFFE57373)
    val lightMutedRoseBackground = Color(0xFFFCECEC) // Changed background color

    val repo = remember { UserRepositoryImplementation() }
    val userViewModel = remember { UserViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    var firstName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Select Option") }
    val options = listOf("Nepal", "India", "China")
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = Modifier
            .padding(innerPaddingValues)
            .fillMaxSize()
            .background(lightMutedRoseBackground)  // <-- here
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {


            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.heroo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(230.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = { Text("Firstname") },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                placeholder = { Text("Lastname") },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("abc@gmail.com") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedOptionText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    }
                    .clickable { expanded = true },
                placeholder = { Text("Select Country") },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = mutedRose
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOptionText = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("*******") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                userViewModel.register(email, password) { success, message, userId ->
                    if (success) {
                        val userModel = UserModel(
                            userId, email, firstName, lastname,
                            "Male", "980805555"
                        )
                        userViewModel.addUserToDatabase(userId, userModel) { successAdd, messageAdd ->
                            Toast.makeText(context, messageAdd, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = mutedRose)
        ) {
            Text("Register", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // "Go to Login" Navigation
        TextButton(
            onClick = {
                activity?.startActivity(Intent(context, LoginActivity::class.java))
                activity?.finish()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? Login", color = mutedRose)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegPreview() {
    RegBody(innerPaddingValues = PaddingValues(0.dp))
}
