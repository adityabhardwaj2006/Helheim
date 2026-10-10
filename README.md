# ⚡ Helheim — AI-Powered Expense Tracker

**Take control of your finances with intelligent expense tracking, automated transaction detection, and personalized AI-powered financial insights.**

Helheim is a modern, offline-first Android application built with **Kotlin, Jetpack Compose, Clean Architecture, and MVVM**. It simplifies personal finance management by combining automated transaction tracking, interactive financial analytics, multi-currency support, and an AI-powered financial assistant in a single application.

> 🚀 **Project Status:** Available on Google Play through Closed Testing.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Screenshots](#-screenshots)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Build and Run](#-build-and-run)
- [Permissions and Privacy](#-permissions-and-privacy)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [Developer](#-developer)
- [License](#-license)

---

## 🌟 Overview

Managing personal finances manually can be time-consuming and makes it difficult to understand spending patterns. Helheim addresses this problem by bringing transaction management, financial analytics, and AI-powered assistance together in one application.

Designed with modern Android development practices, Helheim focuses on usability, maintainability, offline accessibility, and reliable synchronization between local storage and a remote backend.

### What makes Helheim different?

- **Automated transaction tracking** — Detect supported banking and UPI transaction notifications and extract relevant financial details.
- **AI financial assistant** — Ask questions about spending habits, financial summaries, and budgeting.
- **Insightful analytics** — Visualize income, expenses, savings, and category-wise spending.
- **Offline-first experience** — Access locally stored financial information even without an internet connection.
- **Modern Android UI** — Built using Jetpack Compose and Material Design 3.
- **Scalable architecture** — Organized into presentation, domain, and data layers for easier maintenance and testing.

---

## ✨ Key Features

### 📲 1. Automated Transaction Detection

Reduce manual data entry with intelligent transaction detection.

- **Notification-based monitoring:** Uses Android's `NotificationListenerService` to monitor supported financial notifications, including bank and UPI transaction alerts.
- **Smart transaction parsing:** Extracts relevant information such as transaction amount, debit or credit type, merchant name, and payment method when available.
- **Transaction classification:** Helps organize supported transactions into categories such as income and expenses.
- **Duplicate prevention:** Uses caching and transaction-handling logic to reduce duplicate entries.
- **User-controlled tracking:** Review detected transaction details and confirm them according to the application's workflow.

Supported notification formats and the availability of extracted fields depend on the originating bank, payment application, and notification content.

### 🤖 2. AI-Powered Financial Assistant

Make financial information easier to understand with an integrated conversational assistant.

- Ask questions about your financial activity.
- Understand spending patterns and expense categories.
- Get personalized budgeting suggestions.
- Explore financial summaries through natural-language conversations.
- Make financial data more accessible without manually navigating multiple screens.

The assistant uses the application's available financial context and configured AI backend to generate relevant responses.

### 📊 3. Interactive Financial Analytics

Understand where your money goes with clear visualizations and financial summaries.

- **Income and expense summaries:** Review financial activity over selected periods.
- **Category-wise breakdowns:** Identify major spending categories.
- **Monthly comparisons:** Compare income, expenses, and savings with previous months.
- **Spending trends:** Explore changes in financial activity over time.
- **Financial overview:** Access key metrics through a centralized dashboard.

### 🔄 4. Offline-First Data Management

Stay productive even when your internet connection is unavailable.

- **Local persistence:** Room Database stores locally available application data.
- **Offline access:** Work with cached financial information without requiring a continuous internet connection.
- **Background synchronization:** Synchronize supported local changes with the backend when connectivity is restored.
- **Connectivity monitoring:** A `ConnectivityObserver` helps the application respond to network availability.
- **Repository-based data access:** Separates data sources from the presentation layer.

Synchronization behavior depends on the configured backend and the operation being performed.

### 🔐 5. Authentication and Profile Management

Access personal financial information through supported authentication methods.

- Google sign-in integration.
- Firebase Authentication integration where configured.
- Email and password authentication.
- Profile information management.
- Profile picture support.
- Password management through the supported authentication workflow.

Authentication providers and backend configuration must be set up correctly before these features can be used.

### 💱 6. Multi-Currency Support

Manage financial information using supported currencies.

- Select a preferred currency.
- Display financial values using the selected currency.
- Support currency conversion where the configured conversion functionality is available.
- Present monetary values consistently throughout the application.

Real-time conversion depends on the availability and configuration of the relevant exchange-rate source.

### 📱 7. Adaptive and Responsive UI

Helheim uses modern Android UI technologies to provide an adaptable user experience.

- Declarative UI with Jetpack Compose.
- Material Design 3 components and styling.
- Navigation using Navigation Compose.
- Reusable UI components.
- Adaptive navigation patterns, including bottom navigation and navigation rail where appropriate.
- Layouts designed to accommodate phones, foldables, and tablet-sized screens.

---

## 📱 Screenshots

Screenshots of the actual application can be added below to showcase its interface and core functionality.

| Home Dashboard | Analytics & Statistics |
|:---:|:---:|
| *Add dashboard screenshot* | *Add analytics screenshot* |

| AI Financial Assistant | Transaction History |
|:---:|:---:|
| *Add AI assistant screenshot* | *Add transaction history screenshot* |

### Adding your screenshots

Create a `screenshots/` directory in the repository and place your actual application screenshots inside it.

Replace the placeholders above with Markdown image references, for example:

`![Home Dashboard](screenshots/home-dashboard.png)`

Use screenshots captured from the real application so that the documentation accurately reflects its current functionality.

---

## 🏗️ Architecture

Helheim follows **Clean Architecture principles and the MVVM (Model–View–ViewModel) pattern** to separate UI rendering, business logic, and data management.

The architecture is organized into three primary layers.

```text
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│                                                             │
│  Jetpack Compose │ Material 3 │ Navigation Compose          │
│  Screens │ Reusable Components │ ViewModels                 │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                           │
│                                                             │
│  Repository Interfaces │ Business Rules │ Use Cases         │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                            │
│                                                             │
│  Repository Implementations │ Room Database │ Retrofit      │
│  DataStore Preferences │ DTOs │ Remote Data Sources         │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  EXTERNAL SERVICES                           │
│                                                             │
│  REST API Backend │ Authentication Services │ AI Services   │
└─────────────────────────────────────────────────────────────┘
```

### Architectural Responsibilities

| Layer | Responsibility |
|---|---|
| **Presentation** | Renders Compose UI, manages screen state, and handles user interactions through ViewModels. |
| **Domain** | Defines business rules, repository contracts, and use cases where implemented. |
| **Data** | Coordinates local and remote data sources, repositories, persistence, and API communication. |
| **External Services** | Provides backend APIs, authentication, and AI functionality. |

### Design Principles

- **Separation of concerns:** Each layer has a defined responsibility.
- **Maintainability:** UI, business logic, and data operations are organized separately.
- **Reusability:** Shared UI components and data-handling logic can be reused.
- **Reactive state management:** Kotlin Flow APIs support observing asynchronous data and state changes.
- **Dependency injection:** Hilt helps manage dependencies and application components.
- **Offline-first data access:** Local persistence supports access to cached information when network connectivity is unavailable.

---

## 🛠️ Technology Stack

Helheim is developed primarily with Kotlin and modern Android development libraries.

### Android Development

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary application programming language |
| **Jetpack Compose** | Declarative UI development |
| **Material Design 3** | UI components and visual styling |
| **Navigation Compose** | In-app navigation |
| **ViewModel** | UI state and lifecycle-aware state management |
| **Kotlin Coroutines** | Asynchronous programming |
| **StateFlow / SharedFlow** | Reactive state and event streams |
| **Hilt / Dagger** | Dependency injection |

### Data and Networking

| Technology | Purpose |
|---|---|
| **Room Database** | Local structured data persistence |
| **DataStore Preferences** | Storage of application preferences |
| **Retrofit** | REST API communication |
| **OkHttp** | HTTP client and network interceptors |
| **ConnectivityObserver** | Network connectivity monitoring |

### Authentication and Supporting Libraries

| Technology | Purpose |
|---|---|
| **Firebase Authentication** | Authentication integration |
| **Google Credential Manager** | Modern credential and sign-in integration |
| **Coil 3** | Image loading |
| **Lottie Compose** | Animated UI assets |

### Backend Integration

The Android application communicates with a remote REST API for supported operations such as authentication, transaction management, and AI chat.

The backend and its external integrations must be configured separately. Refer to the backend project's documentation for its runtime requirements, environment variables, and deployment instructions.

---

## 📁 Project Structure

The main application code is organized under the `com.aditya.expensetracker` package.

```text
com.aditya.expensetracker/
│
├── BaseApplication.kt
├── MainActivity.kt
│
├── common/
│   └── UI result and shared utility classes
│
├── data/
│   ├── Api/
│   │   └── Retrofit API interfaces
│   ├── DAO/
│   │   └── Room data access objects
│   ├── Database/
│   │   └── Local database configuration
│   ├── Di/
│   │   └── Dependency injection modules
│   ├── ViewModel/
│   │   └── ViewModels and UI state
│   ├── connectivity/
│   │   └── Network connectivity monitoring
│   └── model/
│       └── Data models and DTOs
│
├── domain/
│   └── Repository interfaces and domain logic
│
├── presentation/
│   ├── Screens/
│   │   └── Dashboard, statistics, history, profile, chat
│   ├── components/
│   │   └── Reusable Compose UI components
│   └── navigation/
│       └── Navigation routes and host
│
├── service/
│   └── Transaction notification listener
│
└── ui/
    └── theme/
        └── Material 3 colors, typography, and shapes
```

*Note: This tree is a high-level overview. Actual filenames and package contents may vary as the project evolves.*

---

## 🚀 Getting Started

Follow these steps to configure and run Helheim locally.

### Prerequisites

Ensure that your development environment includes:

- **Android Studio:** Ladybug (2024.2.1) or a compatible newer release.
- **JDK:** 17 recommended, subject to the project's Gradle configuration.
- **Android SDK:** Installed SDK platforms and build tools required by the project.
- **Minimum Android version:** API 26 (Android 8.0).
- **Target Android version:** API 36 in the current configuration.
- **Git:** To clone and manage the repository.
- **Backend API:** A reachable backend instance for features that require remote services.

### 1. Clone the Repository

```bash
git clone https://github.com/aditya-bhardwaj/Helheim.git

cd Helheim
```

### 2. Configure Local Properties

Create or update `local.properties` in the project root.

```properties
GOOGLE_CLIENT_ID=your_google_oauth_client_id
BASE_URL=https://your-backend-url.com/
```

Replace the example values with your actual configuration.

- `GOOGLE_CLIENT_ID` should match the OAuth client configuration expected by the Android application's authentication implementation.
- `BASE_URL` should point to your deployed backend API, including the appropriate trailing slash if required by your Retrofit configuration.

**Important:** Do not commit real credentials, private keys, access tokens, or other secrets to GitHub.

### 3. Configure Firebase

Obtain the appropriate `google-services.json` file from your Firebase project and place it in the Android application module:

```text
Helheim/
├── app/
│   └── google-services.json
├── build.gradle.kts
├── settings.gradle.kts
└── local.properties
```

Ensure the Firebase Android app is registered with the correct application ID:

```text
com.aditya.expensetracker
```

If Google sign-in is enabled, verify that the authentication configuration matches the sign-in implementation and the certificates used for the relevant build variant.

**Security note:** The Firebase client configuration is intended for use by the client application, but it does not replace appropriate Firebase security rules, backend authorization, or API-side token verification.

### 4. Configure the Backend

Helheim depends on a separately configured backend for supported remote features.

Before testing the complete application:

- Deploy or start the backend service.
- Configure the required server-side environment variables.
- Set up authentication and token verification.
- Configure any required AI service credentials on the server.
- Confirm that the API URL is accessible from your device or emulator.

Refer to the backend repository's documentation for the exact environment variable names and setup instructions.

### 5. Sync and Build

Open the project in Android Studio and allow Gradle to synchronize dependencies.

Alternatively, build the debug APK from the terminal:

```bash
./gradlew assembleDebug
```

To run the project's available unit tests:

```bash
./gradlew test
```

The exact tasks available depend on the Gradle configuration.

---

## 🔧 Build and Run

### Run on an Android Device or Emulator

1. Open the project in Android Studio.
2. Wait for Gradle synchronization to finish.
3. Configure Firebase and the backend URL.
4. Connect an Android device or start an emulator.
5. Select the appropriate run configuration.
6. Click **Run** to build and launch the application.

### Generate a Release Build

To generate a release Android App Bundle:

```bash
./gradlew bundleRelease
```

The generated bundle is typically located at:

```text
app/build/outputs/bundle/release/
```

The precise output location depends on the project's Gradle configuration.

Before distributing a release, verify signing configuration, API endpoints, authentication behavior, privacy disclosures, and required runtime permissions.

---

## 🔒 Permissions and Privacy

Helheim uses certain Android capabilities to support its features.

### Notification Access

Notification access enables the application to detect supported financial transaction alerts from compatible banking and payment applications.

- Notification access must be explicitly granted by the user through Android settings.
- Transaction extraction depends on the information available in notifications.
- Notification access can expose sensitive information, so its use should be clearly explained to users.
- Access should be limited to the data required for the application's stated functionality.

### Internet Access

Network access supports communication with the configured backend and other remote services.

### Data Handling

The application combines local storage with remote services for supported features. Users should review the application's privacy policy for details about collected information, synchronization, authentication, and data deletion.

**For contributors:** Never add real user financial information, production credentials, private keys, or personal notification contents to source control, issue reports, logs, or test fixtures.

---

## 🗺️ Future Enhancements

Potential areas for continued development include:

- [ ] Advanced budgeting and category-specific spending limits.
- [ ] Custom financial goals and progress tracking.
- [ ] Recurring transaction detection and reminders.
- [ ] Exportable financial reports in CSV or PDF format.
- [ ] Enhanced transaction search and filtering.
- [ ] Improved automated transaction categorization.
- [ ] Expanded analytics and customizable date ranges.
- [ ] Additional accessibility and localization improvements.
- [ ] Expanded automated unit and UI test coverage.
- [ ] Further improvements to synchronization and conflict handling.

*This list represents potential improvements, not a claim that these features are already implemented.*

---

## 🤝 Contributing

Contributions, bug reports, suggestions, and feature requests are welcome.

If you would like to contribute:

### Contribution Workflow

1. **Fork** the repository.
2. Create a feature branch.
3. Implement your changes.
4. Test the changes locally.
5. Commit your work with a clear message.
6. Push the branch to your fork.
7. Open a Pull Request describing your changes.

Example:

```bash
git checkout -b feature/your-feature-name

git add .

git commit -m "feat: add your feature"

git push origin feature/your-feature-name
```

Then open a Pull Request against the original repository.

### Reporting Issues

Before opening an issue:

- Check whether the issue has already been reported.
- Include steps to reproduce the problem.
- Describe the expected and actual behavior.
- Include relevant, sanitized logs when necessary.
- Remove API keys, authentication tokens, and personal financial information.

Report bugs and request features through the [GitHub Issues page](https://github.com/aditya-bhardwaj/Helheim/issues).

---

## 👨‍💻 Developer

**Aditya Bhardwaj**

Android Developer | Kotlin | Jetpack Compose | Software Engineering

Helheim is a personal project focused on modern Android development, personal finance management, offline-first data handling, and AI-powered application experiences.

- **GitHub:** [@aditya-bhardwaj](https://github.com/aditya-bhardwaj)
- **Repository:** [Helheim — AI Expense Tracker](https://github.com/aditya-bhardwaj/Helheim)
- **Platform:** Android
- **Distribution:** Google Play Closed Testing

If you find this project useful, consider giving the repository a ⭐ on GitHub.

---

## 📄 License

This project is distributed under the MIT License.

See the [`LICENSE`](LICENSE) file for the complete license terms.

---

<p align="center">
  <b>Built with ❤️ using Kotlin and Jetpack Compose</b>
  <br>
  <sub>Helheim — Smarter insights. Better financial awareness.</sub>
</p>
