package com.example.demo.restaurantapp.ViewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.demo.restaurantapp.Model.Result
import com.example.demo.restaurantapp.Repo.ResultRepo
import com.google.android.gms.maps.model.LatLng

class RestaurantViewModel : ViewModel() {

    var result: MutableLiveData<List<Result?>?>?
    var nextPageToken: MutableLiveData<String>
    var status: MutableLiveData<String?>
    val repoObject = ResultRepo()

    init {
        result = repoObject.getResults()
        nextPageToken = repoObject.getNextPageToken()
        status =  repoObject.getStatusInfo()
    }

    fun getRestaurants(mLatLng: LatLng?) {
        Log.d("Map","Inside getRestaurants")
        repoObject.getRetrofitData(nextPageToken.value, mLatLng)
    }

}