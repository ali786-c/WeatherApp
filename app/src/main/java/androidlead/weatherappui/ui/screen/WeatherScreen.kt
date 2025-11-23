package androidlead.weatherappui.ui.screen

import androidlead.weatherappui.R
import androidlead.weatherappui.data.model.WeatherResponse
import androidlead.weatherappui.ui.screen.components.ActionBar
import androidlead.weatherappui.ui.screen.components.AirQuality
import androidlead.weatherappui.ui.screen.components.DailyForecast
import androidlead.weatherappui.ui.screen.components.WeeklyForecast
import androidlead.weatherappui.ui.screen.util.AirQualityItem
import androidlead.weatherappui.ui.screen.util.ForecastItem
import androidlead.weatherappui.ui.theme.ColorBackground
import androidlead.weatherappui.ui.theme.ColorSurface
import androidlead.weatherappui.ui.theme.ColorTextPrimary
import androidlead.weatherappui.ui.theme.ColorGradient1
import androidlead.weatherappui.ui.theme.ColorGradient2
import androidlead.weatherappui.ui.theme.ColorGradient3
import androidlead.weatherappui.ui.theme.ColorTextSecondary
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    val weatherState by viewModel.weatherState.collectAsState()
    var showApiErrorDialog by remember { mutableStateOf(false) }
    var isFahrenheit by remember { mutableStateOf(false) }
    var isAboutExpanded by remember { mutableStateOf(false) }
    
    // Search state
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Listen for errors and show popup, instead of replacing screen content
    LaunchedEffect(weatherState.error) {
        if (weatherState.error != null && !weatherState.isLoading) {
            showApiErrorDialog = true
        }
    }

    if (showApiErrorDialog && weatherState.error != null) {
        AlertDialog(
            onDismissRequest = { 
                showApiErrorDialog = false 
                viewModel.clearError()
            },
            title = { Text("Error") },
            text = { Text(weatherState.error ?: "Unknown error") },
            confirmButton = {
                Button(
                    onClick = { 
                        showApiErrorDialog = false 
                        viewModel.clearError()
                        isSearching = true // Open search on error so user can try again
                    }
                ) {
                    Text("Try Again")
                }
            },
            dismissButton = {
                Button(onClick = { 
                    showApiErrorDialog = false 
                    viewModel.clearError()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                drawerContainerColor = Color.Transparent,
                drawerContentColor = ColorTextSecondary,
                modifier = Modifier.width(300.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ColorGradient1,
                                    ColorGradient2,
                                    ColorGradient3
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Weather Forecast",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ColorTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Divider(color = ColorTextSecondary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Unit Toggle Item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isFahrenheit = !isFahrenheit
                                    scope.launch { drawerState.close() }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isFahrenheit) "Switch to Celsius (°C)" else "Switch to Fahrenheit (°F)",
                                style = MaterialTheme.typography.titleMedium,
                                color = ColorTextSecondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // About Item (Expandable)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isAboutExpanded = !isAboutExpanded
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "About App",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ColorTextSecondary
                                )
                            }
                            
                            if (isAboutExpanded) {
                                Text(
                                    text = "Weather App v1.0\nBuilt with Jetpack Compose\nDesigned and developed by Muhammad Aliyan",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ColorTextSecondary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ColorBackground
        ) { paddings ->
            Box(modifier = Modifier.padding(paddings).fillMaxSize()) {
                // Main Content always visible if we have data
                if (weatherState.weatherData != null) {
                    WeatherContent(
                        weatherViewModel = viewModel,
                        weatherData = weatherState.weatherData!!,
                        forecastItems = weatherState.forecastItems,
                        airQualityItems = weatherState.airQualityItems,
                        onLocationClick = { isSearching = true },
                        onRefresh = {
                            viewModel.fetchWeather(weatherState.weatherData!!.city.name)
                        },
                        onSettingsClick = { 
                            scope.launch { drawerState.open() }
                        },
                        isFahrenheit = isFahrenheit,
                        isSearching = isSearching,
                        searchText = searchText,
                        onSearchTextChange = { 
                            searchText = it
                            viewModel.searchCities(it)
                        },
                        onSearch = { 
                            viewModel.fetchWeather(it)
                            isSearching = false
                            searchText = ""
                            viewModel.clearSuggestions()
                        },
                        onCancelSearch = {
                            isSearching = false
                            searchText = ""
                            viewModel.clearSuggestions()
                        }
                    )
                }

                // Scrim to dismiss search on outside click
                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 70.dp) // Adjust top padding based on ActionBar height
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isSearching = false
                                searchText = ""
                                viewModel.clearSuggestions()
                            }
                    )
                }

                // Search Suggestions Overlay
                if (isSearching && weatherState.searchSuggestions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, start = 16.dp, end = 16.dp) // Adjust top padding based on ActionBar height
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(8.dp))
                                .background(ColorSurface, RoundedCornerShape(8.dp))
                        ) {
                            items(weatherState.searchSuggestions) { location ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.fetchWeatherByLocation(location.lat, location.lon)
                                            isSearching = false
                                            searchText = ""
                                            viewModel.clearSuggestions()
                                        }
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "${location.name}, ${location.country}",
                                        color = ColorTextPrimary,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Divider(color = ColorTextSecondary.copy(alpha = 0.1f))
                            }
                        }
                    }
                }

                // Loading Indicator (Overlay)
                if (weatherState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .clickable(enabled = false) {}, // Block interaction
                        contentAlignment = Alignment.Center
                    ) {
                        CustomFrostyLoader(modifier = Modifier.size(80.dp))
                    }
                }
                
                // Initial Empty/Error State (only if no data yet)
                if (weatherState.weatherData == null && !weatherState.isLoading && weatherState.error == null) {
                     CustomFrostyLoader(modifier = Modifier.size(80.dp).align(Alignment.Center))
                }
                
                // Initial Error State
                if (weatherState.weatherData == null && weatherState.error != null && !showApiErrorDialog) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = weatherState.error ?: "Error",
                            color = ColorTextPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = { isSearching = true }) {
                            Text("Search Again")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomFrostyLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader_transition")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation_angle"
    )

    Image(
        painter = painterResource(id = R.drawable.ic_frosty),
        contentDescription = "Loading...",
        modifier = modifier
            .rotate(angle)
    )
}

@Composable
fun WeatherContent(
    weatherViewModel: WeatherViewModel,
    weatherData: WeatherResponse,
    forecastItems: List<ForecastItem>,
    airQualityItems: List<AirQualityItem>,
    onLocationClick: () -> Unit,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    isFahrenheit: Boolean,
    isSearching: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCancelSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        val current = weatherData.list.firstOrNull()
        // Temperature Conversion
        val rawTemp = current?.main?.temp ?: 0.0
        val rawFeelsLike = current?.main?.feelsLike ?: 0.0
        
        val displayTemp = if (isFahrenheit) (rawTemp * 9/5) + 32 else rawTemp
        val displayFeelsLike = if (isFahrenheit) (rawFeelsLike * 9/5) + 32 else rawFeelsLike
        
        val tempStr = displayTemp.toInt().toString()
        val feelsLikeStr = displayFeelsLike.toInt().toString()
        val unit = if (isFahrenheit) "°F" else "°C"

        val condition = current?.weather?.firstOrNull()?.main ?: ""
        val icon = current?.weather?.firstOrNull()?.icon ?: ""

        val timestamp = current?.dt ?: System.currentTimeMillis() / 1000
        val formattedDate = remember(timestamp) {
            SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(timestamp * 1000))
        }

        ActionBar(
            location = weatherData.city.name,
            updatedText = formattedDate,
            onLocationClick = onLocationClick,
            onSettingsClick = onSettingsClick,
            isSearching = isSearching,
            searchText = searchText,
            onSearchTextChange = onSearchTextChange,
            onSearch = onSearch,
            onCancelSearch = onCancelSearch
        )
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        DailyForecast(
            forecast = condition,
            date = "Today",
            temperature = tempStr,
            feelsLike = "Feels like $feelsLikeStr$unit",
            iconRes = weatherViewModel.mapIconToDrawable(icon)
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        AirQuality(
            data = airQualityItems,
            onRefresh = onRefresh
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        
        val convertedForecast = forecastItems.map { item ->
            val t = item.temperature.replace("°", "").toIntOrNull() ?: 0
            val newT = if (isFahrenheit) (t * 9/5) + 32 else t
            item.copy(temperature = "$newT°")
        }
        
        WeeklyForecast(data = convertedForecast)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionDialog(
    onDismiss: () -> Unit,
    onCitySelected: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(ColorSurface, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Enter City Name",
                style = MaterialTheme.typography.titleMedium,
                color = ColorTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("City") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = ColorTextPrimary,
                    cursorColor = ColorTextPrimary,
                    focusedLabelColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onCitySelected(text)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorTextPrimary,
                    contentColor = ColorSurface
                )
            ) {
                Text("Search")
            }
        }
    }
}
