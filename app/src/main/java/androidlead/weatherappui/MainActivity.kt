package androidlead.weatherappui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidlead.weatherappui.ui.screen.SplashScreen
import androidlead.weatherappui.ui.screen.WeatherScreen
import androidlead.weatherappui.ui.screen.WeatherViewModel
import androidlead.weatherappui.ui.theme.WeatherAppUiTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppUiTheme {
                val viewModel: WeatherViewModel = viewModel()
                val weatherState by viewModel.weatherState.collectAsState()
                var hasLocationPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { permissions ->
                        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    }
                )

                LaunchedEffect(hasLocationPermission) {
                    if (hasLocationPermission) {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
                                } else {
                                    // Fallback to default if location is null
                                    viewModel.fetchWeather("London")
                                }
                            }
                            .addOnFailureListener {
                                viewModel.fetchWeather("London")
                            }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                // Show Splash until we have data or an error occurs (handled by WeatherScreen)
                if (weatherState.weatherData == null && weatherState.error == null) {
                    SplashScreen()
                } else {
                    WeatherScreen(viewModel = viewModel)
                }
            }
        }
    }
}
