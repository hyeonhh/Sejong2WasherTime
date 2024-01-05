package com.example.sejong2washertimer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sejong2washertimer.ui.WasherApp
import com.example.sejong2washertimer.ui.theme.Sejong2WasherTimerTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.messaging

class MainActivity : ComponentActivity() {


    private lateinit var databaseReference : DatabaseReference
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("timer")



        //todo : 추후 회원가입 화면에서 token
            // [START log_reg_token]
            Firebase.messaging.getToken().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = "$token"
                Log.d("token보기", token)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                print(token)
            }
            // [END log_reg_token]




        setContent {
            Sejong2WasherTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Box(contentAlignment = Alignment.Center) {
                        WasherApp()

                    }
                }

            }
        }









    }





}






@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Sejong2WasherTimerTheme {
    }
}