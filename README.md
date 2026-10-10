# ⚡ Helheim — AI-Powered Expense Tracker

**Take control of your finances with intelligent expense tracking, automated transaction detection, and AI-powered financial insights.**

Helheim is an Android expense-tracking application designed to simplify personal finance management through transaction tracking, financial analytics, multi-currency support, and an AI-powered financial assistant.

Built primarily with **Kotlin and modern Android development technologies**, Helheim brings essential financial tools together in a clean, dark-themed interface.

> 🚀 **Project Status:** Available on Google Play through Closed Testing.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Screenshots](#-screenshots)
- [Architecture](#️-architecture)
- [Technology Stack](#️-technology-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Build and Run](#-build-and-run)
- [Permissions and Privacy](#-permissions-and-privacy)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [Developer](#-developer)
- [License](#-license)

---

## 🌟 Overview

Managing personal finances manually can be time-consuming and make it difficult to understand spending patterns. Helheim aims to simplify this process by combining transaction management, financial analytics, and conversational AI assistance in one application.

### What makes Helheim different?

- **Automated transaction tracking** — Detect supported financial notifications to help reduce manual transaction entry.
- **AI Financial Guide** — Ask questions about financial activity and explore transaction summaries.
- **Financial analytics** — Review income, expenses, savings, and spending trends.
- **Transaction management** — Keep track of transaction amounts, dates, and financial activity.
- **Multi-currency support** — Select a preferred currency for displaying financial values.
- **Personalized profile** — Manage profile information and application preferences.
- **Modern Android interface** — A consistent dark-themed experience designed for convenient navigation.

---

## ✨ Key Features

### 📲 1. Automated Transaction Detection

Reduce manual data entry with notification-based transaction tracking.

- Monitors supported financial notifications when notification access is enabled.
- Helps extract transaction details such as amounts and transaction types from supported notifications.
- Supports tracking income and expenses.
- Uses the information available in notifications to assist with transaction recording.
- Provides a user-controlled setting for automatic transaction tracking.

*Detection accuracy and available details depend on the notification format and the originating banking or payment application.*

### 🤖 2. AI-Powered Financial Guide

Make financial information easier to understand with a conversational assistant.

- Ask questions about recent transactions.
- Review summaries of income and expenses.
- Explore spending activity using natural-language questions.
- Get financial guidance based on the information available to the assistant.
- Interact with financial information through a dedicated chat interface.

AI responses depend on the financial context available to the application and the configured AI backend.

### 📊 3. Financial Analytics

Understand your financial activity through a dedicated statistics screen.

- Review financial summaries over weekly, monthly, and yearly periods.
- Switch between income, expenses, and savings views.
- Explore spending trends through charts.
- Identify major transactions contributing to spending.
- Access key financial information from the dashboard.

### 💳 4. Transaction Management

Keep financial activity organized in one place.

- View recent transactions from the dashboard.
- Review transaction amounts and dates.
- Distinguish income and expenses through visual indicators.
- Monitor changes in your overall balance.
- Use transaction information to better understand spending habits.

### 👤 5. Profile and Account Management

Manage account information and personal application settings.

- View profile information.
- Access profile-editing options.
- Access password-management options.
- Select a preferred currency.
- Enable or disable automatic transaction tracking.
- Access the application's privacy policy.

Available account features depend on the configured authentication workflow.

### 💱 6. Multi-Currency Support

Helheim includes a currency-selection option to help users manage financial information using their preferred currency.

- Select a supported currency.
- Display monetary values using the selected currency.
- Keep financial values consistent throughout the application.

### 🎨 7. Modern Android UI

The application uses a dark-themed interface with dedicated screens for its main financial features.

- Dashboard with balance and recent transactions.
- Statistics screen with time-period and financial-category filters.
- AI Financial Guide with conversational interaction.
- Profile screen with account and utility settings.
- Bottom navigation for accessing primary sections.

---

## 📱 Screenshots

The following screenshots show the actual Helheim application interface.

<p align="center">
  <img src="https://github.com/user-attachments/assets/7b852b7d-e9e2-4f23-b5ca-df2e23f9ebba" alt="Helheim Home Dashboard" width="230" />
  &nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/c0a333af-d29f-4cb5-b965-b777ed33ea33" alt="Helheim Statistics and Analytics" width="230" />
</p>

<p align="center">
  <strong>Home Dashboard</strong>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <strong>Statistics and Analytics</strong>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/ba262fe8-e98d-4c0f-ae57-830c77a56fec" alt="Helheim AI Financial Guide" width="230" />
  &nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/003f95b6-f714-4906-97c5-5c3c594d9f8d" alt="Helheim Profile and Settings" width="230" />
</p>

<p align="center">
  <strong>AI Financial Guide</strong>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <strong>Profile and Settings</strong>
</p>

---

## 🏗️ Architecture

Helheim's Android implementation uses Kotlin and separates application functionality into components for user-interface presentation, data handling, and communication with remote services.

The diagram below illustrates a **conceptual architecture**. Keep the specific layers and libraries aligned with the code currently implemented in the repository.

```text
┌─────────────────────────────────────────────┐
│             PRESENTATION LAYER              │
│                                             │
│       Screens · UI Components · State       │
│           Navigation · ViewModels           │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│              BUSINESS LOGIC                 │
│                                             │
│      Validation · Rules · Use Cases         │
│       Where implemented in the app          │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                 DATA LAYER                  │
│                                             │
│    Repositories · Local Data · API Calls    │
│       Models · Data Sources · Storage       │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│              EXTERNAL SERVICES              │
│                                             │
│       Backend API · Authentication          │
│           Configured AI Services            │
└─────────────────────────────────────────────┘
```

### Architectural Responsibilities

| Component | Responsibility |
|---|---|
| Presentation | Displays screens and handles user interactions. |
| Business logic | Contains application rules and operations where implemented. |
| Data | Manages available local data, repositories, and network communication. |
| External services | Provide configured backend, authentication, and AI functionality. |

---

## 🛠️ Technology Stack

Helheim is developed for Android using Kotlin. The libraries below describe the intended areas of the technology stack; verify the project's Gradle configuration before treating each library as a confirmed dependency.

### Android Development

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary programming language |
| **Android SDK** | Android application development |
| **Gradle** | Build automation and dependency management |
| **Jetpack Compose** | Declarative UI, if enabled in the current build |
| **Material Design 3** | UI components and styling, if configured |

### Data and Networking

| Technology | Purpose |
|---|---|
| **Room Database** | Local database persistence, if configured |
| **DataStore** | Application preferences, if configured |
| **Retrofit / OkHttp** | REST API communication, if configured |
| **Kotlin Coroutines / Flow** | Asynchronous operations and reactive data, if configured |

### Authentication and Supporting Services

| Technology | Purpose |
|---|---|
| **Google Sign-In** | Google account authentication, where configured |
| **Firebase** | Firebase-related integration, where configured |
| **Backend REST API** | Remote application operations |
| **AI service** | Conversational financial assistance |

---

## 📁 Project Structure

The main Android application uses the package name:

`com.aditya.expensetracker`

A simplified example of how an Android project can be organized is shown below. The actual directory and file names should match the repository.

```text
Helheim/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/aditya/expensetracker/
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── google-services.json
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── local.properties
├── README.md
├── LICENSE
└── screenshots/
```

> **Note:** `local.properties`, Firebase configuration files, and other environment-specific files should not be committed if they contain private configuration or are excluded by the project's `.gitignore`.

---

## 🚀 Getting Started

Follow these steps to set up the project locally.

### Prerequisites

- Android Studio
- Git
- An Android SDK compatible with the project
- A compatible JDK and Gradle version
- A physical Android device or emulator
- Backend access and API configuration for features requiring remote services

### 1. Clone the Repository

```bash
git clone https://github.com/adityabhardwaj2006/Helheim.git
cd Helheim
```

### 2. Open the Project

1. Launch Android Studio.
2. Select **Open** and choose the cloned `Helheim` folder.
3. Allow Gradle to synchronize dependencies.
4. Resolve any SDK or build configuration prompts.

### 3. Configure Application Properties

If the project reads configuration from `local.properties`, create or update the file in the project root using the property names expected by the Gradle configuration.

For example:

```properties
GOOGLE_CLIENT_ID=your_google_oauth_client_id
BASE_URL=https://your-backend-url.com/
```

Replace the example values with your own configuration.

- `GOOGLE_CLIENT_ID` must match the OAuth configuration required by the application's sign-in implementation.
- `BASE_URL` should point to the backend API used by the application.

**Important:** Keep private keys, server credentials, access tokens, and other secrets out of source control.

### 4. Configure Firebase and Authentication

If Firebase or Google authentication is enabled in your build:

1. Register the Android application in the appropriate project.
2. Verify that the application ID is `com.aditya.expensetracker`.
3. Configure the required authentication providers.
4. Place the appropriate `google-services.json` in the Android app module when required by the Google Services Gradle plugin.
5. Verify that the authentication configuration matches the installed build and signing certificates.

Firebase client configuration alone does not replace backend authorization, token verification, or appropriate security rules.

### 5. Configure the Backend

For features that depend on remote services:

- Configure the backend URL.
- Set up the required server-side environment variables.
- Configure authentication and token verification.
- Configure any AI service credentials on the server.
- Confirm that the backend is accessible from your device or emulator.

Follow the backend project's own documentation for the exact configuration requirements.

---

## 🔧 Build and Run

### Run the Application

1. Open the project in Android Studio.
2. Wait for Gradle synchronization to finish.
3. Configure the required application properties and services.
4. Start an Android emulator or connect a device.
5. Select the application run configuration.
6. Click **Run**.

### Build a Debug APK

From the project root, run:

```bash
./gradlew assembleDebug
```

### Run Unit Tests

```bash
./gradlew test
```

The tests executed depend on the tasks configured in the project.

### Build a Release App Bundle

To generate a release Android App Bundle, run:

```bash
./gradlew bundleRelease
```

The output is typically placed under:

```text
app/build/outputs/bundle/release/
```

The precise output location may vary with the build configuration.

Before distributing a release, verify signing configuration, authentication, backend endpoints, privacy disclosures, and runtime permissions.

---

## 🔒 Permissions and Privacy

Helheim may use Android capabilities to support its functionality.

### Notification Access

Notification access can enable transaction detection from supported banking and payment notifications.

- The user must grant notification access through Android settings.
- Extracted information depends on the contents of supported notifications.
- Notification access can expose sensitive information and should be used only with the user's informed consent.
- Only information required for the application's stated functionality should be processed.

### Internet Access

Network access supports communication with configured backend services and other remote functionality.

### Data Handling

The application may process account details, financial transaction information, and messages submitted to its AI assistant, depending on enabled features.

Review the application's privacy policy for information about data collection, processing, synchronization, retention, and deletion.

**For contributors:** Never commit real financial records, private credentials, authentication tokens, or personal notification contents to the repository.

---

## 🗺️ Future Enhancements

Potential areas for future development include:

- [ ] Budget planning and category-specific spending limits.
- [ ] Savings goals and progress tracking.
- [ ] Recurring transaction detection and reminders.
- [ ] Exportable financial reports in CSV or PDF formats.
- [ ] Improved transaction search and filtering.
- [ ] Enhanced automated transaction categorization.
- [ ] More detailed financial analytics and date filters.
- [ ] Additional accessibility and localization improvements.
- [ ] Expanded automated testing.
- [ ] Improvements to synchronization and conflict handling.

*These are potential enhancements, not a statement that every item is currently implemented.*

---

## 🤝 Contributing

Contributions, bug reports, suggestions, and feature requests are welcome.

### Contribution Workflow

1. Fork the repository.
2. Create a feature branch.
3. Implement your changes.
4. Test your changes locally.
5. Commit your work with a descriptive message.
6. Push the branch to your fork.
7. Open a Pull Request.

Example:

```bash
git checkout -b feature/your-feature-name
git add .
git commit -m "feat: describe your change"
git push origin feature/your-feature-name
```

### Reporting Issues

Before opening an issue:

- Check whether it has already been reported.
- Include steps to reproduce the problem.
- Describe the expected and actual behavior.
- Provide relevant logs after removing sensitive information.
- Never include API keys, authentication tokens, or personal financial data.

Report bugs and request features through the [Helheim GitHub Issues page](https://github.com/adityabhardwaj2006/Helheim/issues).

---

## 👨‍💻 Developer

**Aditya Bhardwaj**

Android Development · Kotlin · Personal Finance Applications

- **GitHub Profile:** [@adityabhardwaj2006](https://github.com/adityabhardwaj2006)
- **Repository:** [Helheim — AI Expense Tracker](https://github.com/adityabhardwaj2006/Helheim)
- **Platform:** Android
- **Distribution:** Google Play Closed Testing

If you find this project useful, consider giving the repository a ⭐ on GitHub.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

*Keep this section if you have added an MIT `LICENSE` file to your repository.*

---

<p align="center">
  <strong>Built with ❤️ using Kotlin</strong>
  <br/>
  <sub>Helheim — Smarter insights. Better financial awareness.</sub>
</p>
