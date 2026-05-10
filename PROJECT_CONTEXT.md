# Project Context: AirPower Customer

This document provides a comprehensive overview of the **AirPower Customer** Android application, developed as part of a graduation project (IFPE 2025). The application is a native Android client designed to monitor and manage energy-related data, working in conjunction with a **SpringBoot Server (AirPowerServer)** and a **ThingsBoard IoT Server**.

---

## 1. Project Overview
- **Project Name:** AirPower Customer
- **Nature:** Final Graduation Work (TCC) - IFPE 2025
- **Platform:** Native Android
- **Main Goal:** provide a user-friendly interface for monitoring device consumption, managing alarms, and visualizing energy metrics.

## 2. Architecture & Design Patterns
The project follows modern Android development practices, emphasizing separation of concerns and reactivity.

- **MVVM (Model-View-ViewModel):** Decouples the UI logic from the business logic.
- **Repository Pattern:** Acts as a single source of truth for data, mediating between local persistence (Room) and remote API (Retrofit).
- **Clean Architecture Principles:** Logic is divided into modules:
    - `:app`: Contains UI (Compose), ViewModels, and main Repository implementation.
    - `:core`: Handles networking abstractions, interfaces, and connection management.
    - `:common`: Shared UI components, constants, and generic state management.
- **Singleton Pattern:** Used for Managers and Repositories (e.g., `ConnectionManager`, `Repository`, `UIStateManager`) to ensure consistent state across the app.
- **Observer Pattern:** Implemented using **Kotlin Coroutines and Flow** (specifically `StateFlow` and `MutableStateFlow`) for reactive UI updates.

## 3. Technology Stack & Frameworks
- **Programming Language:** Kotlin (1.9.25)
- **UI Framework:** Jetpack Compose (Modern declarative UI)
- **Asynchronous Programming:** Kotlin Coroutines
- **Networking:** Retrofit 2 & OkHttp 3
- **Local Persistence:** Room Database
- **Navigation:** Jetpack Navigation Compose
- **Dependency Management:** Gradle Version Catalog (`libs.versions.toml`)
- **Data Visualization:** `compose-charts` (io.github.ehsannarmani:compose-charts)
- **JSON Parsing:** Gson

## 4. Main Components & Classes

### Core Logic
- **`AirPowerApplication`**: The entry point of the app; initializes the Repository and ViewModel providers.
- **`Repository`**: Orchestrates data flow. It handles user authentication, device fetching, notification management, and interacts with both `AirPowerServerManager` and `AirPowerDatabase`.
- **`AirPowerViewModel`**: The main ViewModel that manages the UI state for screens. It triggers periodic data fetchers (polling strategy) for devices, alarms, and notifications.

### Networking & Security
- **`ConnectionManager` (`:core`)**: Manages Retrofit instances and configures OkHttp clients with interceptors.
- **`AirPowerServerAPIService`**: Defines the REST API endpoints for communication with the SpringBoot server.
- **`JWTManager`**: Specialized object for decoding, validating (expiration check), and managing JWT and Refresh Tokens.
- **Interceptors:**
    - `JwtInterceptor`: Automatically attaches the bearer token to outgoing requests.
    - `DynamicHostInterceptor`: Allows for flexible base URL configurations.

### UI & UX
- **`MainScreen` / `NavHost`**: Handles app navigation between Auth, Home, Device Details, and Dashboards.
- **`UIStateManager`**: A centralized manager to handle loading, success, and error states globally, ensuring a consistent user experience.
- **`NotificationHelper`**: Manages local Android notifications triggered by the backend events.

## 5. Strategies & Implementation Details

### Data Aggregation & Polling
The app uses a polling strategy to keep data fresh:
- **Devices & Alarms:** Refreshed periodically (e.g., every 5 minutes for devices, 1 minute for alarms).
- **Notifications:** Refreshed every 30 seconds.
- **Cache Management:** The ViewModel implements a cache cleanup job for aggregated telemetry data to optimize memory usage.

### Security Approach
- **JWT-Based Authentication:** Users authenticate via the SpringBoot server.
- **Token Persistence:** JWT and Refresh Tokens are stored securely in the local Room database (`AirPowerToken`).
- **Session Management:** The app automatically checks for token expiration. If the JWT is expired but the Refresh Token is valid, it attempts a silent session update. If both are expired, it prompts the user to log in again.

### Error Handling
A robust error handling mechanism uses a `ResultWrapper` (Success, ApiError, NetworkError, Empty) to propagate issues from the network layer up to the UI, where they are handled by the `UIStateManager` to show appropriate error cards or messages.

## 6. System Framework Context
The Android app is one piece of a three-tier ecosystem:
1.  **AirPower Customer (Android):** The mobile front-end for the end-user.
2.  **AirPowerServer (SpringBoot):** The middle-ware/backend that manages business logic, user profiles, and acts as a gateway.
3.  **ThingsBoard Server:** The IoT core that handles telemetry data, device connectivity, and raw data storage.

This architecture ensures that the mobile app remains lightweight, offloading heavy IoT processing to the backend while maintaining real-time monitoring capabilities.
