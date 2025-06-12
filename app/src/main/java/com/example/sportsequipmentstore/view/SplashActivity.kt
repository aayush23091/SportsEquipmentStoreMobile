package com.example.sportsequipmentstore.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.sportsequipmentstore.LoginActivity

import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody();

        }
    }
}

@Composable
fun SplashBody (){
    val context = LocalContext.current
    val activity = context as Activity

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    val localEmail = sharedPreferences.getString("email","").toString()


    LaunchedEffect(Unit) {
        delay(3000)
        if (localEmail.isEmpty()) {


            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            activity.finish()
        }
    }
    Scaffold {
            innerPadding->

        Column (
            modifier = Modifier.fillMaxWidth().padding(innerPadding)
        ){

        }

    }


}



@Preview
@Composable
fun PreviewSplash(){
    SplashBody()

}