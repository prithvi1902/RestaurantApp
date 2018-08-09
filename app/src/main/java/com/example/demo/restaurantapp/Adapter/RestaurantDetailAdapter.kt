package com.example.demo.restaurantapp.Adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.demo.restaurantapp.Model.Result
import com.example.demo.restaurantapp.R
import com.squareup.picasso.Picasso

class RestaurantDetailAdapter(val result: List<Result?>?, val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun getItemViewType(position: Int): Int {
        return if (result!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        lateinit var vh: RecyclerView.ViewHolder

        if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(activity).inflate(R.layout.item_restaurant_detail, parent, false)
            vh = RestaurantViewHolder(view, result)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val view = LayoutInflater.from(activity).inflate(R.layout.progress_bar, parent, false)
            vh = LoadingViewHolder(view)
        }
        return vh
    }


    override fun getItemCount(): Int {
        return result?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is RestaurantViewHolder) {
            val res: Result = result?.get(position)!!
            holder.name.text = res.name
            holder.address.text = res.vicinity
            val dist = res.distance
            holder.distance.text = dist
            Picasso.get().load(res.icon).into(holder.iconImage)

        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    class RestaurantViewHolder(view: View, result: List<Result?>?) : RecyclerView.ViewHolder(view) {

        val name = view.findViewById<TextView>(R.id.name_textView)
        val address = view.findViewById<TextView>(R.id.address_textview)
        val distance = view.findViewById<TextView>(R.id.dist_textView)
        val iconImage = view.findViewById<ImageView>(R.id.iconImage)

        init {
            view.setOnClickListener {
                val lat = result?.get(adapterPosition)?.geometry?.location?.lat
                val lng = result?.get(adapterPosition)?.geometry?.location?.lng
                val name = result?.get(adapterPosition)?.name
                val vicinity = result?.get(adapterPosition)?.vicinity
                val locationUri = Uri.parse("geo:" + lat + "," + lng + "?z=15&q=" + name + "," + vicinity)

                //show the location on map
                val mapIntent = Intent(Intent.ACTION_VIEW, locationUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                if (mapIntent.resolveActivity(view.context.packageManager) != null) {
                    view.context.startActivity(mapIntent)
                }
                Log.d("Android:", result?.get(adapterPosition)?.name)
            }
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
    }

}
