# Walkthrough - Login and Registration UI Implementation

I have implemented the Login and Registration screens as requested, following the provided design image for Peppino Pizza.

## Changes Made

### Screens

#### [LoginScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/LoginScreen.kt)
- Implemented the full Login UI:
    - Logo placeholder.
    - Headline: "Welcome to Peppino Pizza".
    - Subtitle: "Peppino Pizza handcrafted, and always delicious.".
    - Custom styled `OutlinedTextField` for Email and Password.
    - Password visibility toggle.
    - "Remember me" checkbox and "Forgot Password?" link.
    - Primary orange "Login" button.
    - "Or continue with" divider.
    - Social login icons (Google, Apple, Facebook).
    - "Sign up" link at the bottom.

#### [SignUpScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/SignUpScreen.kt)
- Implemented the full Registration UI:
    - Logo placeholder.
    - Headline: "Registration".
    - Subtitle: "Enter the fields below to get started".
    - Fields for Name, Email, and Password.
    - Password visibility toggle.
    - "Remember me" checkbox.
    - Primary orange "Sign in" button.
    - "Or continue with" divider.
    - Social login icons.
    - "Sign in" link at the bottom.

## Design Details
- **Primary Color:** Used `Color(0xFFE96131)` for all primary buttons and links.
- **Components:** Created `LoginTextField` and `SignUpTextField` helper composables for consistent styling.
- **Placeholders:** Used standard Android resource icons as placeholders for the logo and social icons.

## Verification
- Verified both screens in Compose Preview.
- Ensured all UI elements from the design image are present.
