package com.example.sejong2washertimer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.database.DatabaseReference
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
            WasherList(washerList = Datasource().washers)
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
fun WasherList(
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

        )
        }
    }
}

@Composable
fun createWasherReference(washerId: String?): DatabaseReference {
    val database = Firebase.database
    if (washerId != null) {
        Log.d("아이디","washer${washerId}")
    }
    return database.getReference("washer${washerId}startTime")
}



@Composable
fun WasherCard(
    washer: Washer,
    modifier: Modifier = Modifier
) {

    var updatedTime by remember { mutableStateOf("") }
    var isFirebaseDataAvailable by remember { mutableStateOf(false) }

    val myRef = createWasherReference(washer.washerId)

    fun isCurrentTimeEqualsCompletionTime(comletionTime:String):Boolean{
        try {
            val dateFormat = SimpleDateFormat("HH:mm")
            val parsedTime = dateFormat.parse(comletionTime)
            val currentTime = Calendar.getInstance().time

            // 시, 분, 초를 0으로 설정하여 날짜 부분을 무시하고 시간만 비교
            val calendar = Calendar.getInstance()
            calendar.time = currentTime
            calendar.set(Calendar.YEAR, 0)
            calendar.set(Calendar.MONTH, 0)
            calendar.set(Calendar.DAY_OF_MONTH, 0)

            val calendarParsedTime = Calendar.getInstance()
            calendarParsedTime.time = parsedTime
            calendarParsedTime.set(Calendar.YEAR, 0)
            calendarParsedTime.set(Calendar.MONTH, 0)
            calendarParsedTime.set(Calendar.DAY_OF_MONTH, 0)


            Log.d("시간이 맞나?","${calendarParsedTime.time}")

            return calendar.time == calendarParsedTime.time
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    @Composable
    fun showToast() {
        val context = LocalContext.current
        Toast.makeText(context,"세탁 완료",Toast.LENGTH_SHORT).show()
    }



    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val washerStartTime = snapshot.getValue(String::class.java)
            if(washerStartTime!= null)    {
                val startDate = SimpleDateFormat("HH:mm").parse(washerStartTime)
                val updateDate = Calendar.getInstance().apply {
                    time=startDate
                    add(Calendar.MINUTE,45)
                }.time

                updatedTime = SimpleDateFormat("HH:mm").format(updateDate)
                isFirebaseDataAvailable=true


            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError","Error reading completion time: $error")
        }

    })

    if(isCurrentTimeEqualsCompletionTime(updatedTime)) {
        showToast()
    }







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


            if (!isFirebaseDataAvailable) {
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

    val myRef = createWasherReference(washer.washerId)

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

    if(washer.isAvailable) {
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
    else{
        Text(
            text = "완료 시간 : $updatedTime",
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
        )
    }
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
            TextButton(onClick =onConfirm) {
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
