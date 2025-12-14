# Weather App Project Report
## Screenshots

![WeatherApp screenshots: search, current location, detailed view](https://github.com/user-attachments/assets/ca4ef541-c267-48bc-bdb0-e77769129318)

## Installation

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
## 1. Executive Summary
The **Weather App** is a modern, feature-rich Android application designed to provide users with accurate, real-time weather information and forecasts. Built using **Kotlin** and **Jetpack Compose**, the app leverages a robust **MVVM (Model-View-ViewModel)** architecture to ensures scalability and maintainability.

The application integrates **OpenWeatherMap API** for global weather data, **Google Play Services** for geolocation, and **Firebase Authentication** for secure user management. A key feature of the application is its **Offline Support**, implemented using the **Room Database**, allowing users to view previously fetched weather data even without an active internet connection.

---

## 2. Project Objectives
The primary objectives of this project are:
*   To develop a native Android application using modern development standards (Jetpack Compose).
*   To implement a secure authentication system using Firebase.
*   To provide real-time weather updates based on the user's current location or manual city search.
*   To ensure application functionality in offline modes using local caching (Room).
*   To demonstrate the implementation of Clean Architecture and Dependency Separation.

---

## 3. Technology Stack

### 3.1. Development Environment
*   **IDE:** Android Studio
*   **Language:** Kotlin (100%)
*   **Build System:** Gradle (Kotlin DSL)

### 3.2. Core Libraries & Frameworks
*   **User Interface:** Jetpack Compose (Material Design 3) for declarative UI.
*   **Architecture Components:**
    *   **ViewModel:** For managing UI-related data in a lifecycle-conscious way.
    *   **LiveData / Flow:** For reactive data streams.
*   **Networking:**
    *   **Retrofit:** For type-safe HTTP client operations.
    *   **OkHttp:** For HTTP requests and logging.
    *   **Gson:** For JSON serialization/deserialization.
*   **Database (Offline Caching):**
    *   **Room:** An abstraction layer over SQLite for robust database access.
    *   **KSP (Kotlin Symbol Processing):** For efficient annotation processing.
*   **Authentication:**
    *   **Firebase Authentication:** For managing user login and registration securely.
*   **Image Loading:**
    *   **Coil:** For fast, coroutine-based image loading.
*   **Location:**
    *   **Play Services Location:** For retrieving the device's GPS coordinates.

---

## 4. System Architecture

The application follows the **MVVM (Model-View-ViewModel)** architectural pattern, separating concerns into three distinct layers:

### 4.1. The Data Layer (Model)
Responsible for handling data operations. It creates a "Single Source of Truth" by mediating between:
*   **Remote Data Source:** `WeatherApi` (Retrofit) fetches live data from the internet.
*   **Local Data Source:** `WeatherDao` (Room) caches data locally.
*   **Repository (`WeatherRepository`, `AuthRepository`):** The central point that decides whether to fetch data from the network or the local database.

### 4.2. The UI Layer (View)
Built entirely with **Jetpack Compose**. It observes the state from the ViewModel and renders the UI accordingly.
*   **Screens:** `LoginScreen`, `SignUpScreen`, `WeatherScreen`.
*   **State:** The UI is reactive—it rebuilds automatically when the `WeatherState` or `AuthState` changes.

### 4.3. The ViewModel Layer
Acts as a bridge between the Model and the View.
*   `WeatherViewModel`: Fetches weather data and exposes `WeatherState` (Loading, Success, Error) to the UI.
*   `AuthViewModel`: Manages login/signup logic and exposes `AuthState` (Authenticated, Unauthenticated) to the UI.

---

## 5. Key Features

### 5.1. User Authentication
*   **Sign Up:** Users can create an account using their name, email, and password.
*   **Login:** Secure login with email and password.
*   **Validation:** Input fields have validation for empty states and password matching.
*   **UI:** Glassmorphism-inspired design with professional input fields and gradient buttons.

### 5.2. Real-Time Weather
*   Displays current temperature, weather condition (e.g., Rain, Clear), and "Feels Like" temperature.
*   Showcases detailed metrics: Wind Speed, Humidity, and an animated visual representation of the weather.

### 5.3. Location-Based Services
*   Automatically detects the user's current location (Latitude/Longitude) to fetch precise local weather upon startup.

### 5.4. Search Functionality
*   Users can search for any city globally to get its current weather report.

### 5.5. Offline Capability (Caching)
*   **Smart Caching:** When weather data is fetched successfully, it is saved to the local Room database (`weather_table`).
*   **Offline Mode:** If the internet is unavailable, the app retrieves and displays the last known weather data from the database, ensuring a seamless user experience.

### 5.6. Pull-to-Refresh
*   Users can swipe down on the screen to force a weather data update from the server.

---

## 6. Database Design

The local database is structured using **Room**.

**Entity: `WeatherEntity`**
| Column Name | Data Type | Description |
| :--- | :--- | :--- |
| `id` | Integer (PK) | Primary Key (auto-generated) |
| `cityName` | String | Name of the city |
| `temperature` | Double | Current temperature |
| `windSpeed` | Double | Wind speed |
| `humidity` | Integer | Humidity percentage |
| `weatherCondition` | String | Main weather description (e.g., "Clouds") |
| `timestamp` | Long | Time of last update |

---

## 7. Conclusion
The Weather App project successfully demonstrates the creation of a robust, modern Android application. By combining **Jetpack Compose** for a beautiful UI, **Firebase** for security, and **Room** for offline reliability, the detailed objectives were met. The modular **MVVM** architecture ensures that the codebase is clean, testable, and easy to extend for future features like detailed 7-day forecasts or settings customization.
