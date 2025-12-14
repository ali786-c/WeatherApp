package androidlead.weatherappui.data.repository

import androidlead.weatherappui.data.local.dao.WeatherDao
import androidlead.weatherappui.data.local.entity.WeatherEntity
import androidlead.weatherappui.data.model.WeatherResponse
import androidlead.weatherappui.data.network.RetrofitClient
import androidlead.weatherappui.data.network.WeatherApiService
import java.util.Locale

class WeatherRepository(private val weatherDao: WeatherDao) {
    private val apiService: WeatherApiService = RetrofitClient.instance

    suspend fun getWeather(city: String, apiKey: String): WeatherResponse {
        val normalizedCity = city.lowercase(Locale.ROOT)
        try {
            val response = apiService.getForecast(city, apiKey)
            // Save to DB
            weatherDao.insertWeather(WeatherEntity(normalizedCity, response))
            return response
        } catch (e: Exception) {
            // Fallback to DB
            val cachedWeather = weatherDao.getWeather(normalizedCity)
            if (cachedWeather != null) {
                return cachedWeather.weatherResponse
            } else {
                throw e
            }
        }
    }

    suspend fun getWeatherByLocation(lat: Double, lon: Double, apiKey: String): WeatherResponse {
        val uniqueId = "current_location" // Special ID for location-based weather
        try {
            val response = apiService.getForecastByCoordinates(lat, lon, apiKey)
            // Save to DB
            weatherDao.insertWeather(WeatherEntity(uniqueId, response))
            return response
        } catch (e: Exception) {
             val cachedWeather = weatherDao.getWeather(uniqueId)
            if (cachedWeather != null) {
                return cachedWeather.weatherResponse
            } else {
                throw e
            }
        }
    }
    
    suspend fun searchCities(query: String, apiKey: String) = apiService.searchCities(query = query, limit = 5, apiKey = apiKey)
}
