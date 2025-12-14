package androidlead.weatherappui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidlead.weatherappui.ui.screen.AuthState
import androidlead.weatherappui.ui.screen.AuthViewModel
import androidlead.weatherappui.ui.screen.LoginScreen
import androidlead.weatherappui.ui.screen.SignUpScreen
import androidlead.weatherappui.ui.screen.SplashScreen
import androidlead.weatherappui.ui.screen.WeatherScreen
import androidlead.weatherappui.ui.screen.WeatherViewModel
import androidlead.weatherappui.ui.theme.WeatherAppUiTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
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
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppUiTheme {
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.authState.collectAsState()
                
                when (val state = authState) {
                    is AuthState.Authenticated -> {
                        AuthorizedContent(user = state.user, onLogout = { authViewModel.logout() })
                    }
                    is AuthState.Unauthenticated -> {
                        AuthNavigation(authViewModel)
                    }
                    is AuthState.Error -> {
                        // In case of global error, show login
                        AuthNavigation(authViewModel) 
                    }
                    AuthState.Loading -> {
                        SplashScreen()
                    }
                }
            }
        }
    }

    @Composable
    fun AuthorizedContent(user: FirebaseUser, onLogout: () -> Unit) {
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
                try {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
                            } else {
                                viewModel.fetchWeather("London")
                            }
                        }
                        .addOnFailureListener {
                            viewModel.fetchWeather("London")
                        }
                } catch (e: SecurityException) {
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

        if (weatherState.weatherData == null && weatherState.error == null) {
            SplashScreen()
        } else {
            WeatherScreen(user = user, viewModel = viewModel, onLogout = onLogout)
        }
    }
    
    @Composable
    fun AuthNavigation(authViewModel: AuthViewModel) {
        var isLogin by remember { mutableStateOf(true) }
        
        if (isLogin) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = { isLogin = false }
            )
        } else {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { isLogin = true }
            )
        }
    }
}
