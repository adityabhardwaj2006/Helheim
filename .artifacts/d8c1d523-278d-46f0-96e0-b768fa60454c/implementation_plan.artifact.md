# Implementation Plan - Login and Registration UI

Implement the Login and Registration screens based on the provided design image for the Peppino Pizza application.

## User Review Required

> [!IMPORTANT]
> - I will use `Color(0xFFE96131)` for the primary orange color.
> - I will use placeholder icons for the logo and social login buttons (Google, Apple, Facebook) since the specific assets are not yet in the project.
> - I will add `Login` and `SignUp` routes to the navigation graph.

## Proposed Changes

### Navigation

#### [MODIFY] [Routes.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/navigation/Routes.kt)
- Add `Login` and `SignUp` objects to the `Routes` sealed class.

#### [MODIFY] [App.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/navigation/App.kt)
- Add the `Login` and `SignUp` screens to the `NavHost`.
- Set the start destination to `SubNavigation.LoginSignUpScreen`.

### Screens

#### [MODIFY] [LoginScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/LoginScreen.kt)
- Implement the UI: Logo, Title, Subtitle, Email Field, Password Field, Remember Me, Forgot Password, Login Button, Social Login, and Sign Up link.

#### [MODIFY] [SignUpScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/SignUpScreen.kt)
- Implement the UI: Logo, Title, Subtitle, Name Field, Email Field, Password Field, Remember Me, Sign Up Button, Social Login, and Sign In link.

## Verification Plan

### Manual Verification
- Run the app and verify the Login screen is the first screen shown.
- Navigate from Login to Sign Up and back.
- Verify the UI matches the design (colors, layouts, buttons).
- Verify the password visibility toggle works.
