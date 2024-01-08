package com.example.sejong2washertimer.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.sejong2washertimer.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseCloudMessagingService :FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("CloudMessage", "Notification ${message.notification}")


        // firebase 콘솔에서 보내오는 메시지를 확인하기 위한 코드
        if (message.notification != null) {
            Log.e("CloudMessage", "Notification ${message.notification}")
            Log.e("CloudMessage", "Notification Title  ${message.notification!!.title}")
            Log.e("CloudMessage", "Notification Body ${message.notification!!.body}")

        }

        // 지정한 형태의 데이터 메시지를 위한 코드
        val channelId = "washerChannel"
        val notificationId= 0

        val title= message.data["title"].toString()
        val body = message.data["content"].toString()

        Log.d("Cloud",message.data.toString())


        createNotificationChannel(channelId)
        showSimpleNotification(channelId,notificationId,title,body)

    }




    private fun createNotificationChannel(channelId:String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "washerChannel"
            val descriptionText = "세탁이 완료되었어요"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId,name,importance).apply {
                description=descriptionText
            }

            val notificationManager:NotificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


    }

    @SuppressLint("MissingPermission")
    fun showSimpleNotification(
        channelId:String,
        notificationId: Int,
        title:String,
        body:String,
        priority:Int = NotificationCompat.PRIORITY_DEFAULT
    ) {

        var builder = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.washing_machine)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(priority)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId,builder.build())
        }

    }




}

