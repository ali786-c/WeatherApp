# WeatherApp

WeatherApp is a user-friendly Android application built entirely in **Kotlin** that provides real-time weather updates based on your current location or any city you search for. Designed to showcase best practices in Kotlin development, including clean architecture, responsive UI, and robust error handling, WeatherApp is ideal for both users seeking fast weather insights and developers looking to learn from a well-organized mobile project.

## Features

- **Current Weather Updates:** Get instant weather details for any city worldwide, including temperature, humidity, wind speed, and general weather conditions.
- **Location-Based Detection:** Utilizes your device’s GPS to automatically fetch weather data for your current location, even when you’re traveling.
- **City Search Functionality:** Quickly find weather information for different cities via an intuitive search bar.
- **Intuitive & Responsive UI:** Crafted with Material Design principles for a smooth, visually appealing, and easy-to-navigate experience on any device size.
- **Detailed Weather Display:** View not only temperature but also humidity, wind speed, weather descriptions, and representative icons or images.
- **Network & Error Handling:** Manages network errors gracefully with clear feedback, retry options, and offline notifications.
- **Unit Preferences:** (Optional) Easily toggle between Celsius/Fahrenheit and metric/imperial wind speed units.
- **Local Caching:** (Optional) Stores recent weather results for offline viewing using Room database.
- **Light/Dark Mode:** Adapts to system theme preferences for eye comfort.
- **Weather Icons:** Visual representation of weather conditions using dynamic icons.
- **Multi-Language Support:** (Optional) Easily customizable to support multiple languages.

## Screenshots

![WeatherApp screenshots: search, current location, detailed view](https://github.com/user-attachments/assets/ca4ef541-c267-48bc-bdb0-e77769129318)

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest version recommended)
- Android device or emulator running API level 21 (Android 5.0 Lollipop) or above
- Internet connection for real-time weather data
- Location permission enabled if using location-based detection

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ali786-c/WeatherApp.git
   ```
2. **Open Project in Android Studio:**
   - Launch Android Studio and select "Open an Existing Project."
   - Navigate to the cloned repository folder.
3. **Sync and Build:**
   - Wait for Gradle sync to complete and resolve dependencies.
   - Build the project using the “Run” button or Shift + F10.
4. **Run on Device or Emulator:**
   - Connect a physical device or start an emulator.
   - Install the app and grant location permission if prompted.

## Usage

- **View Current Location Weather:** Launch the app; it automatically detects your location and displays current weather details.
- **Search by City:** Enter a city name in the search bar and select it to view weather information.
- **Refresh Data:** Use the refresh button to update weather info manually.
- **Offline Access:** (If enabled) View last loaded weather data without an internet connection.
- **Toggle Units & Theme:** Switch between Celsius/Fahrenheit or light/dark mode as per your preference.

## Tech Stack

- **Kotlin** (100%)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) for API calls
- **Async:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for background operations
- **Data:** [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) & [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) for UI reactivity
- **Image Loading:** [Glide](https://github.com/bumptech/glide) or [Picasso](https://square.github.io/picasso/)
- **Persistence:** [Room](https://developer.android.com/jetpack/androidx/releases/room) (optional, for local caching)
- **UI Components:** Material Components, RecyclerView, ConstraintLayout
- **Permissions:** AndroidX libraries, runtime permission checks

## API

- Utilizes public weather APIs (e.g., [OpenWeatherMap](https://openweathermap.org/api); you may need to register for an API key and add it to your local config).
- Supports geolocation-based queries as well as city name lookups.

## Contributing

We welcome community contributions to enhance WeatherApp! You can help by:

- Reporting bugs and issues
- Suggesting new features or improvements
- Submitting pull requests for code enhancements, refactoring, or documentation updates

### Development Guidelines

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Adhere to MVVM architecture principles
- Write clear, concise commit messages and PR descriptions
- Ensure features and UI are tested on multiple device types

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Author

- [ali786-c](https://github.com/ali786-c)

For questions, feedback, or collaboration requests, feel free to open an issue or contact via GitHub!
