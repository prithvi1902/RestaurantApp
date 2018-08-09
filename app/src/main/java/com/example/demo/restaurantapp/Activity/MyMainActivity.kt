package com.example.demo.restaurantapp.Activity

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast

import com.example.demo.restaurantapp.Adapter.RestaurantDetailAdapter
import com.example.demo.restaurantapp.Model.Result
import com.example.demo.restaurantapp.R
import com.example.demo.restaurantapp.ViewModel.RestaurantViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import kotlinx.android.synthetic.main.activity_my_main.*

class MyMainActivity : AppCompatActivity(), OnMapReadyCallback {

    val mTAG = "Map Criteria: "

    var totalItemCount = 0
    var lastVisibleItem = 0
    var vFlag = 0

    private var mLocationPermissionGranted = false
    var isLoading: Boolean = false

    var mLatLng: LatLng = LatLng(0.000000, 0.000000)
    lateinit var arrList: ArrayList<Result?>
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val visibleThreshold = 5
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    companion object {
        lateinit var model: RestaurantViewModel
    }

    init {
        model = RestaurantViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)

        val locationPermission = getLocationPermission()

        model = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        arrList = ArrayList()
        mySwipeResfresh.visibility = VISIBLE
        mapContainer.visibility = INVISIBLE
        setObserver()

        //set up the swipe refresh layout
        mySwipeResfresh.setOnRefreshListener {
            Handler().postDelayed({
                Toast.makeText(this@MyMainActivity, "Loading...", Toast.LENGTH_SHORT).show()
                setDataToRecyclerView()

            }, 1000)
            mySwipeResfresh.isRefreshing = false
        }

        if (locationPermission) {
            getDeviceLocation()
        }else {
            if (getLocationPermission()) {
                getDeviceLocation()
            }
        }
    }

    //inflate the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toggle_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.toggle -> {
                if (vFlag == 1) {
                    //inflate RecyclerView
                    mySwipeResfresh.visibility = VISIBLE
                    mapContainer.visibility = INVISIBLE

                    //reset flag to toggle; and switch the icon
                    vFlag = 0
                    item.setIcon(R.drawable.ic_map_24px)
                } else
                    if (vFlag == 0) {

                        mySwipeResfresh.visibility = INVISIBLE
                        mapContainer.visibility = VISIBLE
                        initMap()
                        //reset flag to toggle; and switch the icon
                        vFlag = 1
                        item.setIcon(R.drawable.ic_list_24px)
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //initialize Map
    private fun initMap() {
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.container) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
    }

    //called after getMapAsync
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (mLocationPermissionGranted) {

            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return
            }

            mMap.isMyLocationEnabled = true
            moveCamera(mLatLng)
        }
        setDataToMap(arrList)
    }

    //set data on Map
    private fun setDataToMap(it: List<Result?>) {
        for (i in it.indices) {
            val res = it.get(i)
            try {
                val lat = res!!.geometry.location.lat as Double
                val lng = res.geometry.location.lng as Double
                val name = res.name
                val location = LatLng(lat, lng)
                mMap.addMarker(MarkerOptions().position(location).title(name)).showInfoWindow()
                if (i == it.size / 2)
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20F))
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }

    //get Location Permission from User
    private fun getLocationPermission(): Boolean {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (ContextCompat.checkSelfPermission(this.applicationContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.applicationContext, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
            return true
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
            return false
        }
    }

    //On getting the request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mLocationPermissionGranted = false

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false
                            return
                        }
                    }
                    Log.d(mTAG, "All permissions granted ")
                    mLocationPermissionGranted = true
                    getDeviceLocation()
                }
            }
        }
    }

    //get device location
    private fun getDeviceLocation(){
        Log.d(mTAG, "Getting devices current location")
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (mLocationPermissionGranted) {
                Log.d(mTAG, "The permission is granted")
                val location = mFusedLocationProviderClient.lastLocation
                Log.d(mTAG, "Fetching last location")
                location.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(mTAG, "Found last Location")
                        val currentLocation = it.result as Location
                        mLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                        model.getRestaurants(mLatLng)
                        Log.d(mTAG, "LatLng after fetching: " + mLatLng)
                    } else {
                        Log.d(mTAG, "Could not find Location")
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(mTAG, "Security Exception: " + e.message)
        }
        Log.d(mTAG, "LatLng at the end: " + mLatLng)
    }

    private fun moveCamera(mLatLng: LatLng) {
        mMap.addMarker(MarkerOptions().position(mLatLng).title("You are here")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20F))
    }

    //set the observer
    private fun setObserver() {
        model.result?.observe(this, Observer {
            if (arrList.isEmpty())
                arrList = it as ArrayList<Result?>
            else {
                val temp = it as ArrayList<Result?>
                arrList.addAll(temp)
            }

            //isLoading = model.nextPageToken != null

            setDataToRecyclerView()
        })

        /*  model.nextPageToken.observe(this, Observer {
              isLoading = it.equals(null)
          })*/

        model.status.observe(this, Observer {
            if (it.equals("OVER_QUERY_LIMIT"))
                Toast.makeText(this@MyMainActivity, "Over Query Limit reached!", Toast.LENGTH_SHORT).show()
        })
    }

    //set data to RecyclerView
    fun setDataToRecyclerView() {

        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = RestaurantDetailAdapter(arrList, this)

        result_recyclerView.layoutManager = linearLayoutManager
        result_recyclerView.adapter = adapter

        result_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                totalItemCount = linearLayoutManager.itemCount

                if (!isLoading && (totalItemCount <= lastVisibleItem + visibleThreshold)) {

                    if (arrList.size <= totalItemCount) {
                        arrList.add(null)
                        result_recyclerView.post(Runnable { adapter.notifyItemInserted(arrList.size - 1) })

                        Handler().postDelayed(Runnable {
                            arrList.removeAt(arrList.size - 1)
                            result_recyclerView.post(Runnable { adapter.notifyItemRemoved(arrList.size) })
                        }, 50)

                        isLoading = false
                        Handler().postDelayed({
                            Log.d(mTAG, "LatLng value inside Load more" + mLatLng)
                            model.getRestaurants(mLatLng)
                        }, 100)
                        //adapter.notifyDataSetChanged()
                    }
                    //isLoading = true
                }
            }
        })
    }
}
