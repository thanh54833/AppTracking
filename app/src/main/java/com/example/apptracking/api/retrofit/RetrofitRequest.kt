package com.example.apptracking.api.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitRequest {
    private var retrofit: Retrofit? = null
    private val BASE_URL_LIVE = "http://10.10.11.201:4040/"
    private val BASE_URL_TEST = "http://10.0.1.15:3600/"

    private val URL = BASE_URL_TEST
    val retrofitInstance: Retrofit?
        get() {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
            return retrofit
        }
}