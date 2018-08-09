package com.example.demo.restaurantapp.Fragment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.restaurantapp.Adapter.RestaurantDetailAdapter
import com.example.demo.restaurantapp.Model.Result
import com.example.demo.restaurantapp.R
import com.example.demo.restaurantapp.ViewModel.RestaurantViewModel
import kotlinx.android.synthetic.main.fragment_result.*
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng

class RestaurantFragment : Fragment() {

    var totalItemCount: Int = 0
    var lastVisibleItem: Int = 0
    var isLoading: Boolean = false
    var mLatLng: LatLng? = null

    companion object {
        lateinit var model: RestaurantViewModel
    }

    init {
        model = RestaurantViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mySwipeResfresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                Handler().postDelayed({
                    Toast.makeText(activity, "Loading...", Toast.LENGTH_SHORT).show()
                    //model.getRestaurants(mLatLng)
                    if(model.status.equals("OVER_QUERY_LIMIT"))
                        Toast.makeText(activity, "Over Query Limit reached!", Toast.LENGTH_SHORT).show()
                }, 1000)
                mySwipeResfresh.isRefreshing = false
            }

        })

        model = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        //model.getRestaurants(mLatLng)
        model.result?.observe(this, Observer {

            if (it != null) {
                val arrList = it as ArrayList<Result?>?
                setDataToRecyclerView(arrList)
                Log.d("Android:","Data in the array"+ arrList.toString())
            }
        })
    }

    private fun setDataToRecyclerView(list: ArrayList<Result?>?) {

        val linearLayoutManager = LinearLayoutManager(activity)
        result_recyclerView.layoutManager = linearLayoutManager
        result_recyclerView.setHasFixedSize(true)
        val adapter = RestaurantDetailAdapter(list, activity as Activity)
        result_recyclerView.adapter = adapter

        result_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = linearLayoutManager.itemCount;
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!isLoading && (lastVisibleItem == totalItemCount - 1)) {
                    if(model.nextPageToken != null){

                        if (list?.size!! <= 20) {

                            //list.lastIndexOf(null)
                            list.add(null)
                            result_recyclerView.post(Runnable { adapter.notifyItemInserted(list.size - 1) })
                            Handler().postDelayed(Runnable {

                                list.removeAt(list.size - 1)
                                result_recyclerView.post(Runnable { adapter.notifyItemRemoved(list.size - 1) })
                                //model.getRestaurants(mLatLng)
                                Log.d("Restaurant: ", model.result.toString())

                                adapter.notifyDataSetChanged()
                                //adapter.notifyItemInserted(list.size - 1)
                                //adapter.setLoaded()
                            }, 2000)
                        }
                    } else {
                        Toast.makeText(activity, "Couldn't load data!\nSwipe to refresh", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })




      /*  adapter.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                Log.d("Restaurant: ", "Inside onLoadMore()")

                if(model.next_page_token != null){

                if (list?.size!! <= 20) {

                    //list.lastIndexOf(null)
                    list.add(null)
                    result_recyclerView.post(Runnable { adapter.notifyItemInserted(list.size - 1) })
                    Handler().postDelayed(Runnable {

                        list.removeAt(list.size - 1)
                        result_recyclerView.post(Runnable { adapter.notifyItemRemoved(list.size - 1) })
                        model.getRestaurants()
                        Log.d("Restaurant: ", model.result.toString())

                        adapter.notifyItemInserted(list.size - 1)
                        adapter.setLoaded()
                    }, 2000)
                    }
                } else {
                    Toast.makeText(activity, "Loading data completed", Toast.LENGTH_SHORT).show()
                }
            }
        })*/
    }
}

