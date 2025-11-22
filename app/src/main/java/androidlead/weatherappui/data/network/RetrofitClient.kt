package androidlead.weatherappui.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            // Log the URL to Logcat to verify the request
            android.util.Log.d("API_REQUEST", request.url.toString())
            val response = chain.proceed(request)
            if (!response.isSuccessful) {
                android.util.Log.e("API_ERROR", "Code: ${response.code}, Message: ${response.message}")
            }
            response
        }
        .build()

    val instance: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
