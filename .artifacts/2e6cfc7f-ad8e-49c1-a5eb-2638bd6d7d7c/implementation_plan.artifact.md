# Implement Transaction Deletion

The goal is to allow users to delete transactions from the History screen, ensuring the change is reflected in the UI immediately, synced with the Node.js backend, and removed from the local Room database.

## User Review Required

> [!IMPORTANT]
> - Transactions will be deletable via a **Swipe-to-Dismiss** gesture on the History screen.
> - The deletion is **optimistic**: the item will disappear from the UI immediately, but the backend/Room deletion will happen in the background.
> - We assume the backend endpoint is `/deleteTransaction/{id}`.

## Proposed Changes

### [Data Layer]

#### [MODIFY] [TransactionApi.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/data/Api/TransactionApi.kt)
- Add `@DELETE("/deleteTransaction/{id}")` method.

#### [MODIFY] [TransactionRepo.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/domain/repo/TransactionRepo.kt) and [TransactionRepoImpl.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/data/repoImpl/TransactionRepoImpl.kt)
- Add `deleteTransaction` to the interface and implementation.

#### [MODIFY] [TransactionVm.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/data/ViewModel/TransactionVm.kt)
- Add `deleteTransaction(transactionItem: TransactionItem)`:
    - Update `_getTransactionState` locally for immediate UI feedback.
    - Launch coroutine to delete from backend.
    - Delete from `localRepo` (Room) using the mapped `Transaction` object.

### [Presentation Layer]

#### [MODIFY] [HistoryScreen.kt](file:///Users/adityabhardwaj426gmail.com/AndroidStudioProjects/ExpenseTracker/app/src/main/java/com/aditya/expensetracker/presentation/Screens/HistoryScreen.kt)
- Wrap `TransactionHistoryItem` in `SwipeToDismissBox`.
- Use `Modifier.animateItem()` on the list items for smooth removal.
- Trigger `transactionVm.deleteTransaction(transaction)` when swiped.

## Verification Plan

### Manual Verification
1. Navigate to the **Transaction History** screen.
2. Swipe a transaction item to the left (or right) to delete.
3. Verify the item disappears immediately from the list with an animation.
4. Restart the app and verify the item is gone (proving it was deleted from Room/Backend).
5. Add a new transaction and verify it appears, then try deleting it to ensure the cycle works.
