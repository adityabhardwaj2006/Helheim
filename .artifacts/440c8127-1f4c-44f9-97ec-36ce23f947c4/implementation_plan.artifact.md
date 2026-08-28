# Fix Navigation after Notification Refresh

The user reported that the automatic navigation to the Home screen after refreshing notifications is not working. I will update the navigation logic to be more explicit and ensure the signal is caught and acted upon correctly.

## Proposed Changes

### [Component Name] Presentation Layer

#### [MODIFY] [NotificationScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/NotificationScreen.kt)
- Update the `LaunchedEffect` to use a more explicit navigation call to `Routes.HomeUi` instead of just `popBackStack()`.
- This ensures that no matter how the user reached the Notification screen, they are taken back to the main dashboard.
- I will also ensure the navigation options match those used in the bottom bar for consistency (e.g., `popUpTo`, `launchSingleTop`).

## Verification Plan

### Manual Verification
1. Open the Notification screen (from Home or Profile).
2. Pull down to refresh.
3. Verify that once the scan completes, the app reliably navigates back to the `HomeScreen`.
