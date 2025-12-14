package androidlead.weatherappui.data.local.entity

import androidlead.weatherappui.data.model.WeatherResponse
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey
    val cityName: String,
    val weatherResponse: WeatherResponse,
    val lastUpdated: Long = System.currentTimeMillis()
)
