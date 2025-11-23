package androidlead.weatherappui.ui.screen

import androidlead.weatherappui.R
import androidlead.weatherappui.data.model.City
import androidlead.weatherappui.data.model.GeoLocation
import androidlead.weatherappui.data.model.Main
import androidlead.weatherappui.data.model.Weather
import androidlead.weatherappui.data.model.WeatherData
import androidlead.weatherappui.data.model.WeatherResponse
import androidlead.weatherappui.data.model.Wind
import androidlead.weatherappui.data.network.RetrofitClient
import androidlead.weatherappui.ui.screen.util.AirQualityItem
import androidlead.weatherappui.ui.screen.util.ForecastItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

class WeatherViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow(WeatherUiState())
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    // OpenWeather API Key
    private val apiKey = "94656f8ac592f27c36b009927eba16de"
    
    private var searchJob: Job? = null

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _weatherState.update { it.copy(isLoading = true, error = null, searchSuggestions = emptyList()) }
            
            try {
                val response = RetrofitClient.instance.getForecast(city, apiKey)
                updateStateWithResponse(response)
            } catch (e: HttpException) {
                handleError(e)
            } catch (e: Exception) {
                _weatherState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherState.update { it.copy(isLoading = true, error = null, searchSuggestions = emptyList()) }

            try {
                val response = RetrofitClient.instance.getForecastByCoordinates(lat, lon, apiKey)
                updateStateWithResponse(response)
            } catch (e: HttpException) {
                handleError(e)
            } catch (e: Exception) {
                _weatherState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
    
    fun searchCities(query: String) {
        searchJob?.cancel()
        if (query.length < 3) {
            _weatherState.update { it.copy(searchSuggestions = emptyList()) }
            return
        }
        
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            try {
                val suggestions = RetrofitClient.instance.searchCities(query, apiKey = apiKey)
                _weatherState.update { it.copy(searchSuggestions = suggestions) }
            } catch (e: Exception) {
                // Ignore search errors for now or log them
                e.printStackTrace()
            }
        }
    }
    
    fun clearSuggestions() {
        _weatherState.update { it.copy(searchSuggestions = emptyList()) }
    }

    private fun getMockData(): WeatherResponse {
        val currentTimestamp = System.currentTimeMillis() / 1000
        val mockList = ArrayList<WeatherData>()
        
        // Generate mock data for 5 days (every 3 hours = 8 items/day * 5 = 40 items)
        for (i in 0 until 40) {
            mockList.add(
                WeatherData(
                    dt = currentTimestamp + (i * 3600 * 3),
                    main = Main(
                        temp = 25.0 + (i % 5), 
                        feelsLike = 28.0, 
                        humidity = 60 + (i % 10)
                    ),
                    weather = listOf(
                        Weather(
                            id = if (i % 3 == 0) 800 else 500, 
                            main = if (i % 3 == 0) "Clear" else "Rain", 
                            description = if (i % 3 == 0) "Clear Sky" else "Light Rain", 
                            icon = if (i % 3 == 0) "01d" else "10d"
                        )
                    ),
                    wind = Wind(speed = 12.0, deg = 90),
                    pop = if (i % 3 == 0) 0.0 else 0.8,
                    dtTxt = "2023-01-01 12:00:00"
                )
            )
        }

        return WeatherResponse(
            list = mockList,
            city = City(name = "Demo City", country = "Test")
        )
    }

    private fun updateStateWithResponse(response: WeatherResponse) {
        val forecastItems = mapResponseToForecastItems(response)
        val airQualityItems = mapResponseToAirQualityItems(response)
        _weatherState.update { 
            it.copy(
                isLoading = false,
                weatherData = response,
                forecastItems = forecastItems,
                airQualityItems = airQualityItems,
                error = null
            )
        }
    }

    private fun handleError(e: HttpException) {
        val errorMsg = if (e.code() == 404) {
            "City not found. Please enter a valid city name."
        } else {
            val errorBody = e.response()?.errorBody()?.string()
            "API Error: $errorBody"
        }
        _weatherState.update { it.copy(isLoading = false, error = errorMsg) }
    }
    
    fun clearError() {
        _weatherState.update { it.copy(error = null) }
    }

    private fun mapResponseToForecastItems(response: WeatherResponse): List<ForecastItem> {
        val dailyData = response.list.groupBy {
            val instant = Instant.ofEpochSecond(it.dt)
            instant.atZone(ZoneId.systemDefault()).toLocalDate()
        }

        return dailyData.map { (date, dataList) ->
            val weatherItem = dataList.find {
                val time = Instant.ofEpochSecond(it.dt).atZone(ZoneId.systemDefault()).toLocalTime()
                time.hour == 12
            } ?: dataList.first()

            val dayOfWeek = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            val dayMonth = date.format(DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH))
            
            ForecastItem(
                image = mapIconToDrawable(weatherItem.weather.firstOrNull()?.icon ?: ""),
                dayOfWeek = dayOfWeek,
                date = dayMonth,
                temperature = "${weatherItem.main.temp.toInt()}°",
                airQuality = (10..200).random().toString(),
                airQualityIndicatorColorHex = listOf("#ff7676", "#2dbe8d", "#f9cf5f").random()
            )
        }.take(7)
    }

    private fun mapResponseToAirQualityItems(response: WeatherResponse): List<AirQualityItem> {
        val current = response.list.firstOrNull() ?: return emptyList()
        
        return listOf(
            AirQualityItem(
                title = "Real Feel",
                value = "${current.main.feelsLike.roundToInt()}°",
                icon = R.drawable.ic_real_feel
            ),
            AirQualityItem(
                title = "Wind",
                value = "${current.wind.speed} km/h",
                icon = R.drawable.ic_wind_qality,
            ),
            AirQualityItem(
                title = "SO2",
                value = "0.9", // Mock data
                icon = R.drawable.ic_so2
            ),
            AirQualityItem(
                title = "Rain",
                value = "${(current.pop * 100).roundToInt()}%",
                icon = R.drawable.ic_rain_chance
            ),
            AirQualityItem(
                title = "UV Index",
                value = "3", // Mock data
                icon = R.drawable.ic_uv_index
            ),
            AirQualityItem(
                title = "O3",
                value = "50", // Mock data
                icon = R.drawable.ic_o3
            )
        )
    }

    fun mapIconToDrawable(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.img_sun
            "01n" -> R.drawable.img_moon_stars
            "02d", "02n" -> R.drawable.img_cloudy
            "03d", "03n", "04d", "04n" -> R.drawable.img_clouds
            "09d", "09n", "10d", "10n" -> R.drawable.img_rain
            "11d", "11n" -> R.drawable.img_thunder
            "13d", "13n" -> R.drawable.ic_frosty
            else -> R.drawable.img_sun
        }
    }
}

data class WeatherUiState(
    val isLoading: Boolean = true,
    val weatherData: WeatherResponse? = null,
    val forecastItems: List<ForecastItem> = emptyList(),
    val airQualityItems: List<AirQualityItem> = emptyList(),
    val searchSuggestions: List<GeoLocation> = emptyList(),
    val error: String? = null
)
