package com.bignerdranch.android.foodnow

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.YelpApi
import api.YelpRestaurant
import api.YelpSearch
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_food_now.*
import kotlinx.android.synthetic.main.restaurant_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

private const val TAG = "FoodNowActivity"
private const val API_KEY = "API_KEY_HERE"
private lateinit var fusedLocationClient: FusedLocationProviderClient

class FoodNowActivity : AppCompatActivity(),OnItemClickListener  {
    private var currentLocation: String = "Cal Poly Pomona"
    private var sort_by: String = "distance"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onItemClicked(restaurant: YelpRestaurant) {
        var x = restaurant.coordinates.latitude
        var y = restaurant.coordinates.longitude

        val gmmIntentUri = Uri.parse("google.navigation:q=$x,$y")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_now)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantAdapter(this, restaurants, this)
        restaurant_recycler_view.adapter = adapter
        restaurant_recycler_view.layoutManager = LinearLayoutManager(this)

        query(currentLocation)
    }

    private fun query(location: String){
        currentLocation = location
        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantAdapter(this, restaurants, this)
        restaurant_recycler_view.adapter = adapter
        restaurant_recycler_view.layoutManager = LinearLayoutManager(this)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.yelp.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val yelpApi = retrofit.create(YelpApi::class.java)
        val yelpRequest = yelpApi.searchRestaurants(
            "Bearer $API_KEY ",
            "Food",
            currentLocation,
            50,
            sort_by
        )
        yelpRequest.enqueue(object : Callback<YelpSearch> {
            override fun onFailure(call: Call<YelpSearch>, t: Throwable) {
                Log.e(TAG, "Failed", t)
            }

            override fun onResponse(call: Call<YelpSearch>, response: Response<YelpSearch>) {
                Log.d(TAG, "Response received $response")
                val yelpResponse = response.body()
                if (yelpResponse == null) {
                    Log.e(TAG, "Valid response not received")
                    return
                } else if (yelpResponse != null) {
                    restaurants.addAll(yelpResponse.restaurants)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun queryPrice(price: String){
        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantAdapter(this, restaurants, this)
        restaurant_recycler_view.adapter = adapter
        restaurant_recycler_view.layoutManager = LinearLayoutManager(this)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.yelp.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val yelpApi = retrofit.create(YelpApi::class.java)
        val yelpRequest = yelpApi.searchRestaurantsPrice(
            "Bearer $API_KEY ",
            "Food",
            currentLocation,
            50,
            price,
            sort_by
        )
        yelpRequest.enqueue(object : Callback<YelpSearch> {
            override fun onFailure(call: Call<YelpSearch>, t: Throwable) {
                Log.e(TAG, "Failed", t)
            }

            override fun onResponse(call: Call<YelpSearch>, response: Response<YelpSearch>) {
                Log.d(TAG, "Response received $response")
                val yelpResponse = response.body()
                if (yelpResponse == null) {
                    Log.e(TAG, "Valid response not received")
                    return
                } else if (yelpResponse != null) {
                    restaurants.addAll(yelpResponse.restaurants)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getLastKnownLocation() {
        //location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        //if permission is granted
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // get latitude , longitude
                    latitude = location.latitude
                    longitude = location.longitude
                }

            }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Enter Location...";
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    if (queryText != null) {
                        query(queryText)
                        searchItem.collapseActionView();
                    }
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextChange: $queryText")
                    return false
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.locationButton -> {
            getLastKnownLocation()
            val gcd = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = gcd.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                query(addresses[0].locality)
            }
            true
        }
        R.id.onedollar -> {
            queryPrice("1")
            true
        }
        R.id.onedollar -> {
            queryPrice("1")
            true
        }
        R.id.twodollar -> {
            queryPrice("2")
            true
        }
        R.id.threedollar -> {
            queryPrice("3")
            true
        }
        R.id.showall -> {
            query(currentLocation)
            true
        }
        R.id.distance -> {
            sort_by = "distance"
            query(currentLocation)
            true
        }
        R.id.rating -> {
            sort_by = "rating"
            query(currentLocation)
            true
        }
        R.id.bestmatch -> {
            sort_by = "best_match"
            query(currentLocation)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(restaurant: YelpRestaurant, clickListener: OnItemClickListener){
            itemView.rName.text = restaurant.name
            itemView.ratingBar.rating = restaurant.rating.toFloat()
            itemView.rAddress.text = restaurant.location.address + ", " + restaurant.location.city + ", " + restaurant.location.state
            itemView.rCategory.text = restaurant.categories[0].title
            itemView.rNumber.text = restaurant.phone
            itemView.rDistance.text = restaurant.toMiles()
            itemView.rPrice.text = restaurant.price
            Glide.with(itemView).load(restaurant.image_url).into(itemView.imageView)
            itemView.setOnClickListener {
                clickListener.onItemClicked(restaurant)
            }
        }
    }

    private inner class RestaurantAdapter(
        val context: Context,
        private val restaurants: List<YelpRestaurant>,
        val itemClickListener: OnItemClickListener
    ) :
        RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.restaurant_item, parent, false)
            return ViewHolder(view)
        }
        override fun getItemCount(): Int = restaurants.size
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val restaurant = restaurants[position]
            holder.bind(restaurant, itemClickListener)

        }
    }
}
interface OnItemClickListener{
    fun onItemClicked(restaurant: YelpRestaurant)
}
