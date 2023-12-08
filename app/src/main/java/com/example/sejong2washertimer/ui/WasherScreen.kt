package com.example.sejong2washertimer.ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sejong2washertimer.R
import com.example.sejong2washertimer.data.Datasource
import com.example.sejong2washertimer.model.Washer
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Calendar
import java.util.Date

enum class RoutingScreen() {
    Timer,
    Washer,
    Dryer
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WasherApp(
    modifier: Modifier=Modifier
) {
    val navController= rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RoutingScreen.Washer.name,
    ){
        composable(RoutingScreen.Washer.name) {
            WasherList(washerList = Datasource().washers, navController= navController)
        }

        composable(RoutingScreen.Timer.name) {
            TimerScreen()
            }


        composable(RoutingScreen.Dryer.name){
            DryerApp()
        }
    }

}

@Composable
fun StartWasherAlertDialog() {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {

            TextButton(onClick = {}) {
                Text(text = "세탁 시작하기")

            }

        },
        dismissButton = {
            TextButton(onClick ={} ) {
                Text(text = "세탁 취소하기")

            }
        }
    )


}






@Composable
fun WasherList(
    navController: NavController,
    washerList: List<Washer>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier=modifier
    ) {
        items(washerList) {
                washer -> WasherCard(
            washer = washer,
            modifier= Modifier

                .padding(8.dp)
                .clickable {
                    if (washer.isAvailable) {
                    //                        navController.navigate("${RoutingScreen.Timer.name}")

                    }
                }
        )
        }
    }
}
@Composable
fun WasherCard(
    washer: Washer,
    modifier: Modifier = Modifier
) {
    var remainingTime by remember { mutableStateOf(0L) }

    var updatedTime by remember { mutableStateOf("") }

    val database = Firebase.database
    val myRef = database.getReference("washer1_startTime")


    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val washer1_startTime = snapshot.getValue(String::class.java)
            if(washer1_startTime!= null)    {
                val startDate = SimpleDateFormat("HH:mm").parse(washer1_startTime)
                val updateDate = Calendar.getInstance().apply {
                    time=startDate
                    add(Calendar.MINUTE,45)
                }.time

                updatedTime = SimpleDateFormat("HH:mm").format(updateDate)




            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError","Error reading completion time: $error")
        }

    })


    Card(
        modifier = modifier

    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = washer.washerImageResourceId),
                contentDescription = stringResource(id = washer.washerStringResourceId),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = stringResource(id = washer.washerStringResourceId),
            )
            Spacer(modifier = Modifier.size(50.dp))


            if (washer.isAvailable) {
                WasherCardClickableContent(washer=washer,updatedTime=updatedTime)
                Text(
                    text = "사용 가능",

                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color.Blue
                        )

                )
                Spacer(modifier = Modifier.size(10.dp))

                Image(
                    painter = painterResource(id = R.drawable.playicon),
                    contentDescription =null
                )


            }
            else{
                Text(text = "완료 시간 : ${updatedTime}",
                    style = TextStyle(fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                        )
                    )
            }


        }
    }




}



@Composable
fun WasherCardClickableContent(washer: Washer, updatedTime: String) {
    // Washer가 사용 가능한 상태일 때 클릭하면 AlertDialog 띄우기
    val showDialog = remember { mutableStateOf(false) }

    val database = Firebase.database

    //todo : 각 item에 맞는 파이어베이스 경로를 설정해줘야한다.
    val myRef = database.getReference("washer${washer.washerId}_startTime")


    fun saveCurrentTimeDatabase() {
        val startedTime = System.currentTimeMillis()
        val formattedTime = SimpleDateFormat("HH:mm").format(startedTime)
        myRef.setValue( formattedTime)

    }
    if (showDialog.value) {
        // AlertDialog 호출
        StartWasherAlertDialog(
            dialogTitle = "${washer.washerId}번 세탁기 시작",
            dialogText =" 50분동안 세탁이 진행되어요! 세탁을 시작할까요?" ,
            onConfirm = {
                // TODO: 세탁 시작 시 필요한 작업 수행
                saveCurrentTimeDatabase()
                washer.isAvailable=false
                showDialog.value = false

            },
            onDismiss = {
                // TODO: 세탁 취소 시 필요한 작업 수행
                showDialog.value = false

            }
        )
    }

    // Washer가 사용 가능한 상태일 때 클릭하면 AlertDialog 호출
    Spacer(modifier = Modifier.size(10.dp))
    Image(
        painter = painterResource(id = R.drawable.playicon),
        contentDescription = null,
        modifier = Modifier
            .size(50.dp)
            .clickable {
                showDialog.value = true
            }
    )

    Spacer(modifier = Modifier.size(10.dp))

    Text(
        text = "사용 가능",
        style = TextStyle(fontSize = 13.sp, color = Color.Blue)
    )
}

@Composable
fun StartWasherAlertDialog(
    dialogTitle: String,
    dialogText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
                Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "세탁 시작하기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "세탁 취소하기")
            }
        }
    )
}
