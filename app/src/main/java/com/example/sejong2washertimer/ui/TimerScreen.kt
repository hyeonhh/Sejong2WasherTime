package com.example.sejong2washertimer.ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sejong2washertimer.R
import com.example.sejong2washertimer.model.Washer
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Timer(
    totalTime:Long = 3000000L ,
    handleColor: Color,
    inactiveBarColor: Color,
    activeBarColor: Color,
    modifier: Modifier = Modifier,
    initialValue:Float =1f,
    strokeWidth: Dp = 5.dp

){
    val coroutineScope = rememberCoroutineScope()

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    var value by rememberSaveable {
        mutableStateOf(initialValue)
    }
    var currentTime by rememberSaveable {
        mutableStateOf(totalTime)
    }
    var isTimerRunning by rememberSaveable {
        mutableStateOf(false)
    }

    var updatedTime by remember { mutableStateOf("") }


    var scheduledCompletionTime by rememberSaveable { mutableStateOf(0L) }




    val database = Firebase.database
    val myRef = database.getReference("washer1_startTime")

    myRef.addValueEventListener(object :ValueEventListener {
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



    fun saveCurrentTimeDatabase() {
        val startedTime = System.currentTimeMillis()
        val formattedTime = SimpleDateFormat("HH:mm").format(startedTime)
        myRef.setValue( formattedTime)

    }
//
//    fun updateDatabase() {
//
//        coroutineScope.launch {
//            try {
//                myRef.setValue(value)
//                Log.d("saveTimer", "Updated database with value: $value")
//
//            }
//            catch(e:Exception) {
//                Log.e("saveTimer", "Error updating database: ${e.message}")
//            }
//        }
//    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if (currentTime > 0 && isTimerRunning) {
            try {

                delay(60_000L)
                currentTime -=  60_000L
                //todo : 타이머 시간은 50분으로 맞추기
                value = currentTime / totalTime.toFloat()
               // updateDatabase()
            } catch (e: Exception) {
                Log.e("saveTimer", "Error updating database: ${e.message}")

            }

        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier =modifier
            .onSizeChanged {
                size=it
            }){

        Canvas(modifier = modifier) {
            drawArc(
                color = inactiveBarColor,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size= Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color=activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f*value,
                useCenter = false,
                size= Size(size.width.toFloat(), size.height.toFloat()),
                style= Stroke(strokeWidth.toPx(),cap= StrokeCap.Round)

            )
            val center = Offset(size.width/2f,size.height/2f)
            val beta = (250f*value + 145f) * (PI /180f).toFloat()
            val r = size.width/2f
            val a = cos(beta) *r
            val b = sin(beta) *r

            drawPoints(
                listOf(Offset(center.x + a, center.y + b)),
                pointMode = PointMode.Points,
                color = handleColor,
                strokeWidth=(strokeWidth*3f).toPx(),
                cap = StrokeCap.Round

            )

        }


        Button(
            onClick = {
                saveCurrentTimeDatabase()
                if(currentTime<=0L) {
                    currentTime = totalTime
                    isTimerRunning=true
                } else {
                    isTimerRunning = !isTimerRunning
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),

            colors = ButtonDefaults.buttonColors(
                contentColor = if(!isTimerRunning || currentTime <= 0L) {
                    Color.Green
                }
                else {

                    Color.Red
                }
            )

        ) {
            Text(
                text = if(isTimerRunning && currentTime >= 0L) "Stop"
                else if (!isTimerRunning && currentTime >= 0L) "Start"
                else "Restart"
            )


        }



    }


    Text(
        text = "세탁 완료 시간: $updatedTime",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )

}


@Composable
fun TimerScreen(
    modifier: Modifier=Modifier

) {

    Column(
        modifier= modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

            Text(
                text = "1번 세탁기",
                //todo : 클릭한 세탁기 string으로 연결해주기
                textAlign= TextAlign.Center

            )

        Spacer(
            modifier = Modifier
                .size(50.dp)
        )
        Timer(
            totalTime = 3000L,
            handleColor = Color.Green,
            inactiveBarColor = Color.DarkGray,
            activeBarColor = Color(0xFF37B900),
            modifier = Modifier.size(200.dp)

        )

    }

}
