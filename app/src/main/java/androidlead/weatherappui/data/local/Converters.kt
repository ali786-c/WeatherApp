package androidlead.weatherappui.data.local

import androidlead.weatherappui.data.model.WeatherResponse
import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromWeatherResponse(value: WeatherResponse): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toWeatherResponse(value: String): WeatherResponse {
        return Gson().fromJson(value, WeatherResponse::class.java)
    }
}
