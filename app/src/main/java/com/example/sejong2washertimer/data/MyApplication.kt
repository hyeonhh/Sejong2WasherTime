package com.example.sejong2washertimer.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore : DataStore<Preferences>  by preferencesDataStore(name="LocalStore")

class MyApplication : Application() {

}