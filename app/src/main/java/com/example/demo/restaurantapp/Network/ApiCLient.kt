package com.example.demo.restaurantapp.Network

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiCLient {

    val BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/"

    var mRetrofit: Retrofit? = null
    val interceptor = HttpLoggingInterceptor()
//    interceptor.level = HttpLoggingInterceptor.Level.BODY

    fun getApiClient(): Retrofit? {
        if (mRetrofit == null) {
            mRetrofit = Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return mRetrofit
    }
}