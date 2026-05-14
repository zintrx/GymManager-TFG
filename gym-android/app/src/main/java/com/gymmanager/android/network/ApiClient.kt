package com.gymmanager.android.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Note: Use your PC's local IP (e.g. 192.168.1.14) when testing on a physical device over WiFi
    private const val BASE_URL = "http://192.168.1.17:8081/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
