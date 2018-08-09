package com.example.demo.restaurantapp.Activity

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.demo.restaurantapp.Fragment.MapFragment
import com.example.demo.restaurantapp.Fragment.RestaurantFragment
import com.example.demo.restaurantapp.R
import com.example.demo.restaurantapp.ViewModel.RestaurantViewModel

class RestaurantListActivity : AppCompatActivity() {

    var flag = 0

    //private lateinit var myViewModel: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)
/*
        myViewModel = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        myViewModel.getRestaurants()*/

        supportFragmentManager.beginTransaction().add(R.id.container, RestaurantFragment()).addToBackStack(null).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toggle_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.toggle -> {
                if(flag == 0){
                    supportFragmentManager.beginTransaction().replace(R.id.container, MapFragment()).addToBackStack(null).commit()
                    flag = 1
                    item.setIcon(R.drawable.ic_list_24px)
                }else if(flag == 1){
                    supportFragmentManager.beginTransaction().replace(R.id.container, RestaurantFragment()).addToBackStack(null).commit()
                    flag = 0
                    item.setIcon(R.drawable.ic_map_24px)
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
