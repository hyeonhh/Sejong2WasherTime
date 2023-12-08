package com.example.sejong2washertimer.data

import android.app.Application
import com.google.firebase.FirebaseApp

class TimerDatabase :Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}