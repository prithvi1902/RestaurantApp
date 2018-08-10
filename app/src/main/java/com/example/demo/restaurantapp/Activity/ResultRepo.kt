package com.example.demo.restaurantapp.Repo

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import android.util.Log
import com.example.demo.restaurantapp.Model.NearByPlaces
import com.example.demo.restaurantapp.Model.Result
import com.example.demo.restaurantapp.Network.ApiCLient
import com.example.demo.restaurantapp.Network.ApiInterface
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat

class ResultRepo {

    var mutableRestaurants = mutableListOf<Result?>()
    var restaurants: List<Result?> = mutableRestaurants
    var sendingResult: MutableLiveData<List<Result?>?> = MutableLiveData()
    var next_page_token: MutableLiveData<String> = MutableLiveData()
    var status_info: MutableLiveData<String?> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    companion object {
        //val lat = 12.871190
        //val long = 74.842212
        val type = "restaurant"
        val apiKey = "AIzaSyAoPCXkIvU3bm5_maUz_aazOpMzbrO-8Dc"
        val rankBy = "distance"
        val retrofit = ApiCLient().getApiClient()?.create(ApiInterface::class.java)
    }

    fun getRetrofitData(npt: String?, mLatLng: LatLng?) {
        Log.d("Map","Inside getRetrofitData")
        Log.d("Map", "LatLng value $mLatLng")
        Log.d("Map", "NPT value $npt")

        val loc = mLatLng?.latitude.toString() + "," + mLatLng?.longitude.toString()
        val data = HashMap<String?, String?>()

        data.put("location", loc)
        data.put("types", type)
        data.put("key", apiKey)
        data.put("rankby", rankBy)

        if (npt == null)
            data.put("pagetoken", "")
        else
            data.put("pagetoken", npt)


        mutableRestaurants.clear()
        val call = retrofit?.getNextRestaurants(data)

        call?.enqueue(object : Callback<NearByPlaces> {
            override fun onFailure(call: Call<NearByPlaces>?, t: Throwable?) {
                Log.d("Error", "Cant fetch data" + t.toString())
            }

            override fun onResponse(call: Call<NearByPlaces>?, response: Response<NearByPlaces>?) {
                //val result: List<Result?>?
                //result = response?.body()?.results!!
                Log.d("Map", "Call enqueue response: ")
                next_page_token.value = response?.body()?.next_page_token
                isLoading.value = next_page_token.value.equals(null)

                status_info.value = response?.body()?.status
                mutableRestaurants.addAll(response?.body()?.results!!)

                Log.d("Map", "NPT " + next_page_token.value.toString())
                Log.d("Map", "STATUS " + status_info.value.toString())
                Log.d("Map", "Mut Rest " + mutableRestaurants.toString())

                for (position in restaurants.indices) {  //withIndex()

                    Log.d("Map", "Locatoin A: " + mLatLng?.latitude + " " + mLatLng?.longitude)
                    //current location
                    val locA = Location("Location A")
                    locA.latitude = mLatLng?.latitude!!
                    locA.longitude = mLatLng.longitude

                    //one of the location's
                    val locB = Location("Location B")
                    try {
                        locB.latitude = restaurants.get(position)?.geometry?.location?.lat!!
                        locB.longitude = restaurants.get(position)?.geometry?.location?.lng!!
                    } catch (e: NullPointerException) {
                        locB.latitude = mLatLng.latitude
                        locB.longitude = mLatLng.longitude
                    }

                    //calculate distance
                    val num = locA.distanceTo(locB) / 1000

                    //check the unit based on the value
                    val unit = checkForKmOrMt(num)

                    //display only 2 decimal digits
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.CEILING

                    val dist = df.format(num) + unit
                    mutableRestaurants.get(position)?.distance = dist
                }
                sendingResult.value = mutableRestaurants
            }
        })
    }

    private fun checkForKmOrMt(num: Float): String {
        return if (num < 0.000000)
            " mt"
        else
            " km"
    }

    fun getResults(): MutableLiveData<List<Result?>?> {
        return sendingResult
    }

    fun getNextPageToken(): MutableLiveData<String> {
        return next_page_token
    }

    fun getStatusInfo(): MutableLiveData<String?> {
        return status_info
    }

    fun getIsLoading(): MutableLiveData<Boolean> {
        //isLoading.value = model.nextPageToken == null
        return isLoading
    }
}

    /*fun getRetrofitData() {

    var mutableRestaurants = mutableListOf<Result?>()

    var sendingResult: MutableLiveData<List<Result?>?>? = null
    sendingResult?.value = mutableRestaurants

        val call = retrofit?.getRestaurants(data)


        call?.enqueue(object : Callback<NearByPlaces> {

            override fun onFailure(call: Call<NearByPlaces>?, t: Throwable?) {
                Log.d("Error", "Cant fetch data" + t.toString())

                //Toast.makeText(activity, "Cannot fetch data", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<NearByPlaces>?, response: Response<NearByPlaces>?) {
                val nearByPlaces = response?.body()

                next_page_token?.value = nearByPlaces?.next_page_token
                Log.d("NPT", next_page_token.toString())
                restaurants?.value = nearByPlaces?.results
                Log.d("Restaurants", restaurants?.value.toString())
            }
        })
    }*/
