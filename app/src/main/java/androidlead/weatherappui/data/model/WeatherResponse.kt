package androidlead.weatherappui.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val list: List<WeatherData>,
    val city: City
)

data class WeatherData(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val pop: Double,
    @SerializedName("dt_txt") val dtTxt: String
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    val humidity: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class City(
    val name: String,
    val country: String
)
