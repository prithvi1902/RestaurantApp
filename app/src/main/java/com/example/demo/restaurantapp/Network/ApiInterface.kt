package com.example.demo.restaurantapp.Network

import com.example.demo.restaurantapp.Model.NearByPlaces
import com.example.demo.restaurantapp.Model.Result
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.OPTIONS
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiInterface {

    @GET("json")
    fun getNextRestaurants(@QueryMap options: Map<String?, String?>): Call<NearByPlaces>
}