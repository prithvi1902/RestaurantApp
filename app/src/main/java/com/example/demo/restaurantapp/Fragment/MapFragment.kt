package com.example.demo.restaurantapp.Fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.restaurantapp.Model.Result

import com.example.demo.restaurantapp.R
import com.example.demo.restaurantapp.ViewModel.RestaurantViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var myViewModel: RestaurantViewModel
    var mLatLng: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        //myViewModel.getRestaurants(mLatLng)

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.container) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        myViewModel.result?.observe(this, Observer {
                if (it != null) {
                    val codeCraft = LatLng(12.8719, 74.8425)
                    mMap.addMarker(MarkerOptions().position(codeCraft).title("You are here")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(codeCraft))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20F))

                        setDataToMap(it)

                }
            })

    }

    private fun setDataToMap(it: List<Result?>?) {
        for (i in it?.indices!!) {
            val res = it.get(i)
            val lat = res?.geometry?.location?.lat as Double
            val lng = res.geometry.location.lng as Double
            val name = res.name

            val location = LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(location).title(name)).showInfoWindow()
            if(i == it.size/2)
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20F))
        }

    }
}
