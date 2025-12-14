package androidlead.weatherappui.data.local.dao

import androidlead.weatherappui.data.local.entity.WeatherEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_table WHERE cityName = :cityName")
    suspend fun getWeather(cityName: String): WeatherEntity?
}
