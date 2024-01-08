package com.example.sejong2washertimer.fcm

class PushNotification(
    val data : NotiModel,
    //token -> to로 변수명 변경하니 제대로 작동함!
    val to: String="edd6OyJlQrilexZ3Bv4X50:APA91bEqHn25FWN0FZed6juhPcvjh5NQUTsQHQ7bERGzLT-hain-m6vtSVSu347EVX3o69UupjPIP8CycRgpBsVIJ1gUx-Kb2x9Ru1LYDQyJbzV3XLXss_P8iGApxd36ygmjsJRKwFpU"
)