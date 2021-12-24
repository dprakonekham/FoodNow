package api
import com.google.gson.annotations.SerializedName

data class YelpSearch(
    val total: Int,
    @SerializedName("businesses") val restaurants: List<YelpRestaurant>
)

data class YelpRestaurant(
    val name: String,
    val rating: Double,
    val price: String,
    val phone: String,
    val location: YelpAddress,
    val city: YelpAddress,
    val state: YelpAddress,
    val zip_code: String,
    val distance: Double,
    val categories: List<YelpCategories>,
    val image_url: String,
    val coordinates: YelpCoordinates

) {
    fun toMiles(): String{
        val distanceInMiles = "%.2f".format((distance/1609.344))
        return "$distanceInMiles mi"
    }
}

data class YelpCategories(
    val title: String
)

data class YelpAddress(
    @SerializedName("address1") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String
)

data class YelpCoordinates(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
)
