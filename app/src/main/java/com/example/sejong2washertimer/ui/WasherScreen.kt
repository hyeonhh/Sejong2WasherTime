package com.example.sejong2washertimer.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.*
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sejong2washertimer.R
import com.example.sejong2washertimer.data.Datasource
import com.example.sejong2washertimer.fcm.NotiModel
import com.example.sejong2washertimer.fcm.PushNotification
import com.example.sejong2washertimer.fcm.RetrofitInstance
import com.example.sejong2washertimer.model.Washer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

enum class RoutingScreen() {
    Timer,
    Washer,
    Dryer
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WasherApp() {
    val navController= rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RoutingScreen.Washer.name,
    ){
        composable(RoutingScreen.Washer.name) {
            WasherList(
                washerList = Datasource().washers)
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
                .padding(10.dp)

        )
        }
    }
}

@Composable
fun createWasherReference(washerId: String?): DatabaseReference {
    val database = Firebase.database
    return database.getReference("washer${washerId}startTime")
}





@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WasherCard(
    washer: Washer,
    modifier: Modifier = Modifier
) {
    var updatedTime by remember { mutableStateOf("") }
    var showToast by remember {
        mutableStateOf(false)
    }
    val myRef = createWasherReference(washer.washerId)


    fun isCurrentTimeEqualsCompletionTime(completionTime:String):Boolean{
        try {
            val currentTime = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
            return currentTime==completionTime
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }



    myRef.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            val washerStartTime = snapshot.getValue(String::class.java)
            if(washerStartTime!= null)    {
                val startDate = SimpleDateFormat("HH:mm").parse(washerStartTime)
                val updateDate = Calendar.getInstance().apply {
                    time=startDate
                    add(Calendar.MINUTE,1)
                }.time

                updatedTime = SimpleDateFormat("HH:mm").format(updateDate)


                if(isCurrentTimeEqualsCompletionTime(updatedTime)){
                    washer.isAvailable=true
                }

            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError","Error reading completion time: $error")
        }

    })



    LaunchedEffect(updatedTime){
            while(true) {
                if(isCurrentTimeEqualsCompletionTime(updatedTime)){
                    showToast = true
                    washer.isAvailable=true
                    break
                }

                kotlinx.coroutines.delay(1000*60)

        }
    }

    if(showToast) {
        Handler(Looper.getMainLooper()).postDelayed({
            showToast=false
        },500)

        Log.d("FCM1","여기까지는 OK")
        val notiModel = NotiModel("세탁완료알림!","세탁이 다 되었어요! ")
        Log.d("FCM2","여기까지는 OK")
        val pushModel = PushNotification(notiModel, token = "to")

        //todo : token 저장 후 해당 token 넣어주는 로직 구현 필요!

        LaunchedEffect(Unit){
            testPush(pushModel)
            Log.d("FCM3","여기까지는 OK")
        }


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
            Spacer(modifier = Modifier.size(30.dp))


            if (washer.isAvailable) {
                WasherCardClickableContent(washer = washer, updatedTime = updatedTime)
//                {
//                    isFirebaseDataAvailable=it
//                }
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
fun WasherCardClickableContent(
    washer: Washer,
    updatedTime: String,
) {



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

//                setIsFirebaseDataAvailable(true)



            },
            onDismiss = {
                // TODO: 세탁 취소 시 필요한 작업 수행
                showDialog.value = false
//                setIsFirebaseDataAvailable(false)

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


//@Composable
//fun NotificationFCM() {
//    val context = LocalContext.current
//    val channelId = "washerChannel"
//    val notificationId= 0
//
//
//    LaunchedEffect(Unit){
//        createNotificationChannel(channelId,context)
//    }
//
//    val notiModel = NotiModel("세탁완료알림!","세탁이 다 되었어요! ")
//    val pushModel = PushNotification(notiModel,"edd6OyJlQrilexZ3Bv4X50:APA91bHZjWHqKL4vQfst9d1FsmNsmx6EajYFzI8hTujr556uPf0tZzBsN7Jk9whOyGxBZ_lkhX7SmWQvVIZKiL-yWMW_hze0V9yDWHC2rH7DS5Pb0zs4PAraCDXBOQbtEdgAkcuT_z3o")
//    //todo : token 저장 후 해당 token 넣어주는 로직 구현 필요!
//
//
//    Button(onClick = { testPush(pushModel) })
//
//}


fun createNotificationChannel(channelId:String, context: Context) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = "washerChannel"
        val descriptionText = "세탁이 완료되었어요"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId,name,importance).apply {
            description=descriptionText
        }

        val notificationManager:NotificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}

fun showSimpleNotification(
    context:Context,
    channelId:String,
    notificationId: Int,
    textTitle:String,
    textContent:String,
    priority:Int = NotificationCompat.PRIORITY_DEFAULT
) {

    val builder = NotificationCompat.Builder(context,channelId)
        .setSmallIcon(R.drawable.washing_machine)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(notificationId,builder.build())
    }

}

//Push
private suspend fun testPush(notification:PushNotification)= CoroutineScope(Dispatchers.IO).launch {

    try {
        val response = RetrofitInstance.api.postNotification(notification)
        if(response.isSuccessful) {
            Log.d("testPush성공","레트로핏")


        }
        else {

            Log.e("실패","${response.errorBody()?.string()}")
            Log.e("실패","${response.code()}")
            Log.e("실패","${response.code()}")

            Log.e("실패","${response.headers()}")


        }
    }
    catch (e:Exception){
        e.printStackTrace()
    }

}


