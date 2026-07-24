# Fix UNIQUE constraint failed on transactions.id

The application crashes with a `SQLiteConstraintException` when attempting to insert multiple transactions. This is because the `Transaction` entity uses `0` as a default value for its auto-generating primary key `id`, which Room interprets as a literal value rather than a request for auto-generation when the type is nullable (`Int?`).

## Proposed Changes

### Domain Model

#### [MODIFY] [Transaction.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/domain/model/Transaction.kt)
- Change the default value of `id` from `0` to `null`. This ensures Room correctly triggers auto-generation for new records.

### UI Screens

#### [MODIFY] [AddIncome.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/utils/AddIncome.kt)
#### [MODIFY] [AddSaving.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/utils/AddSaving.kt)
#### [MODIFY] [AddScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/utils/AddScreen.kt)
- Fix a bug where the user-selected date was being ignored and `LocalDate.now()` was always used.

#### [MODIFY] [StatisticsScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/StatisticsScreen.kt)
- Remove hardcoded `id = 123` in sample data generation to avoid confusion.

## Verification Plan

### Manual Verification
- Deploy the app to a device/emulator.
- Navigate to "Add Income", "Add Expense", and "Add Saving" screens.
- Add multiple entries for each.
- Verify that the app no longer crashes and entries are correctly saved in the database.
- Verify that selected dates are correctly preserved.
