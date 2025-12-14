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
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.draw.clip
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    user: FirebaseUser,
    viewModel: WeatherViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()
    var showApiErrorDialog by remember { mutableStateOf(false) }
    var isFahrenheit by remember { mutableStateOf(false) }
    var isAboutExpanded by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    
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
                modifier = Modifier.width(320.dp)
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
                        // Header
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_frosty),
                                contentDescription = "App Icon",
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Weather App",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ColorTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Forecast & Quality",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ColorTextSecondary.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        Divider(color = ColorTextSecondary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Menu Items
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.labelLarge,
                            color = ColorTextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Unit Toggle Item
                        DrawerItem(
                            icon = if (isFahrenheit) R.drawable.img_sun else R.drawable.ic_frosty, // Using existing icons as placeholder
                            text = if (isFahrenheit) "Celsius (°C)" else "Fahrenheit (°F)",
                            subText = "Tap to switch unit",
                            onClick = {
                                isFahrenheit = !isFahrenheit
                                scope.launch { drawerState.close() }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = ColorTextSecondary.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Info",
                            style = MaterialTheme.typography.labelLarge,
                            color = ColorTextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // About Item
                        DrawerItem(
                            icon = R.drawable.ic_uv_index, // Using placeholder icon
                            text = "About",
                            subText = "Version 1.0",
                            onClick = { isAboutExpanded = !isAboutExpanded }
                        )

                        if (isAboutExpanded) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp)
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Built with Jetpack Compose by Muhammad Aliyan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextSecondary.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = "© 2025 WeatherApp",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
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
                        user = user,
                        weatherViewModel = viewModel,
                        weatherData = weatherState.weatherData!!,
                        forecastItems = weatherState.forecastItems,
                        airQualityItems = weatherState.airQualityItems,
                        isLoading = weatherState.isLoading,
                        onLocationClick = { isSearching = true },
                        onProfileClick = { showProfileDialog = true },
                        onLogout = onLogout,
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

                // Loading Indicator (Overlay) - Only show if we don't have data yet
                if (weatherState.isLoading && weatherState.weatherData == null) {
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
                
                if (showProfileDialog) {
                     ProfileDialog(
                         user = user,
                         onDismiss = { showProfileDialog = false },
                         onLogout = {
                             showProfileDialog = false
                             onLogout()
                         }
                     )
                }
            }
        }
    }
}

@Composable
fun ProfileDialog(user: FirebaseUser, onDismiss: () -> Unit, onLogout: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorSurface, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_profile),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, ColorGradient2, CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user.email ?: "No Email",
                style = MaterialTheme.typography.titleLarge,
                color = ColorTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            

            Divider(color = ColorTextSecondary.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.1f),
                    contentColor = Color.Red
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun ProfileOptionItem(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ColorGradient1.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
             Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = ColorTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_control), // Re-using existing icon or use proper arrow
            contentDescription = null,
            tint = ColorTextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp).rotate(-90f)
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    user: FirebaseUser,
    weatherViewModel: WeatherViewModel,
    weatherData: WeatherResponse,
    forecastItems: List<ForecastItem>,
    airQualityItems: List<AirQualityItem>,
    isLoading: Boolean,
    onLocationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    isFahrenheit: Boolean,
    isSearching: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCancelSearch: () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            pullRefreshState.endRefresh()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
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
                onProfileClick = onProfileClick,
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
        
        if (pullRefreshState.isRefreshing || pullRefreshState.progress > 0) {
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
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

@Composable
fun DrawerItem(
    icon: Int,
    text: String,
    subText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = ColorTextSecondary
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary.copy(alpha = 0.5f)
            )
        }
    }
}
