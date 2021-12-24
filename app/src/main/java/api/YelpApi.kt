package api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpApi {
    @GET(
        "businesses/search"
    )
    fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String,
        @Query("limit") limit: Int,
        @Query("sort_by") sort_by: String
    ): Call<YelpSearch>
    @GET(
        "businesses/search"
    )
    fun searchRestaurantsPrice(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String,
        @Query("limit") limit: Int,
        @Query("price") price: String,
        @Query("sort_by") sort_by: String
    ): Call<YelpSearch>
    @GET(
        "businesses/search"
    )
    fun searchRestaurantsCoordinates(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("limit") limit: Int,
        @Query("sort_by") sort_by: String
    ): Call<YelpSearch>
}