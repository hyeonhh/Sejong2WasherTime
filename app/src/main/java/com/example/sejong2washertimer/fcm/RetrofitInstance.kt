package com.example.sejong2washertimer.fcm

import com.example.sejong2washertimer.fcm.Repo.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager


class RetrofitInstance {

    companion object {
        private val retrofit by lazy {

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        val api = retrofit.create(NotiAPI::class.java)
    }


}