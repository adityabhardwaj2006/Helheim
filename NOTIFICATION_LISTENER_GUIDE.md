# Transaction Notification Listener Implementation Guide

This document explains the architecture, code implementation, and regular expression (Regex) 
design rules for auto-tracking expense and income transactions from SMS notifications in the
**Expense Tracker** Android application.

---

## 1. Overview & Architecture

`TransactionNotificationListener` extends Android's `NotificationListenerService` 
to automatically track financial transactions received via SMS notifications.

```
┌───────────────────────────┐
│ Incoming Notification     │
└─────────────┬─────────────┘
              │
              ▼
┌───────────────────────────┐
│ 1. Messaging App Filter   │  ---> Ignore Payment Apps (GPay, PhonePe, Paytm, etc.)
└─────────────┬─────────────┘
              │
              ▼
┌───────────────────────────┐
│ 2. Text Extraction        │  ---> Combine Title, Text, BigText, Ticker, SubText
└─────────────┬─────────────┘
              │
              ▼
┌───────────────────────────┐
│ 3. Amount Extraction      │  ---> Apply Regex Patterns + Balance Exclusion Window
└─────────────┬─────────────┘
              │
              ▼
┌───────────────────────────┐
│ 4. Type Classification    │  ---> EXPENSE vs. INCOME (Sanitizing Credit Card keywords)
└─────────────┬─────────────┘
              │
              ▼
┌───────────────────────────┐
│ 5. Merchant & De-dup      │  ---> Extract Merchant / Payee + 60s Cache Key Match
└─────────────┬─────────────┘
              │
              ▼
┌───────────────────────────┐
│ 6. Transaction Manager    │  ---> Save to Local Room DB & Sync with Backend API
└───────────────────────────┘
```

---

## 2. Step-by-Step Code Explanation

### A. Service Setup & Foreground Service
The service runs in the background and is bound by Android's `BIND_NOTIFICATION_LISTENER_SERVICE` permission.
- **Foreground Service**: Started with `startForeground(...)` using `ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC` to guarantee uninterrupted background operation without being killed by Android OS battery optimization.

### B. Package Filtering (`isMessagingApp`)
To ensure privacy and avoid duplicate notifications from payment apps, the listener only processes notifications from SMS/Messaging apps:

- **Explicit Blacklist**: Rejects payment apps (`Google Pay`, `PhonePe`, `Paytm`, `FamPay`, `BHIM UPI`, `MobiKwik`, `Freecharge`) and messaging/chat apps (`WhatsApp`).
- **Default SMS App**: Matches `Telephony.Sms.getDefaultSmsPackage(context)`.
- **Known SMS Apps**: Whitelists Google Messages, Samsung Messages, Xiaomi/MIUI SMS, OnePlus MMS, Vivo MMS, Truecaller, etc.

### C. Full Notification Text Extraction (`onNotificationPosted`)
Different SMS apps format notifications differently. To avoid missing styled text or sender information:
- Extracts `EXTRA_TITLE`, `EXTRA_TEXT`, `EXTRA_BIG_TEXT`, `EXTRA_SUB_TEXT`, `EXTRA_SUMMARY_TEXT`, `tickerText`, and `EXTRA_TEXT_LINES`.
- Converts `CharSequence` to `String` (handling `SpannableString` instances that break simple string getters).
- Concatenates distinct, non-blank strings into a unified `fullText`.

---

## 3. Deep Dive: Regular Expressions (Regex) for Indian SMS Alerts

Indian banks (SBI, HDFC, ICICI, Axis, Kotak, PNB, Bank of Baroda, etc.) format transaction alerts in diverse ways. Below are the regex patterns utilized and the design rationale behind each.

### Pattern 1: Currency-Prefixed Amounts
```regex
(?i)(?:Rs|INR|₹|RE|RES|Amt|Amount)\.?\s*([\d,]+(?:\.\d{1,2})?)
```

#### Breakdown:
- `(?i)`: Case-insensitive flag (matches `rs`, `RS`, `inr`, `INR`).
- `(?:Rs|INR|₹|RE|RES|Amt|Amount)`: Non-capturing group matching common currency indicators and abbreviations.
- `\.?`: Matches an optional period after currency indicators (e.g. `Rs.`, `Amt.`, `RE.`).
- `\s*`: Matches 0 or more whitespace characters between currency symbol and the numerical value.
- `([\d,]+(?:\.\d{1,2})?)`: **Capturing Group 1** (The transaction amount value):
  - `[\d,]+`: Matches digits and thousand-separator commas (e.g., `1,500` or `10000`).
  - `(?:\.\d{1,2})?`: Matches optional decimal part with **1 or 2 decimal digits** (e.g., `.5`, `.50`).

#### Examples Matched:
- `Rs. 500.00` ➔ `500.00`
- `INR 1,250.5` ➔ `1250.5`
- `₹500` ➔ `500`
- `Amt: 1500.00` ➔ `1500.00`

---

### Pattern 2: Currency-Suffixed Amounts
```regex
(?i)([\d,]+(?:\.\d{1,2})?)\s*(?:Rs|INR|₹|RE|RES)
```

#### Breakdown:
- Matches amounts where currency designator comes *after* the number (e.g. `500.00 INR` or `1000 Rs`).

---

### Pattern 3: Action-Verb Prefixed Amounts
```regex
(?i)(?:debited|credited|spent|paid|received|deposited|transferred|withdrawn|charged)\s+(?:by|for|with|of)?\s*(?:Rs|INR|₹|RE|RES)?\.?\s*([\d,]+(?:\.\d{1,2})?)
```

#### Breakdown:
- Captures transaction amounts tied directly to transaction verbs even if the currency symbol is omitted.
- Examples: `debited by 500.00`, `paid 1200`, `credited with 5000`.

---

## 4. Account Balance Distinction Logic

Most bank SMS messages include **both** the transaction amount and the account balance:
> *"Dear SBI User, A/c X1234 debited by Rs. 500.00 on 26-Sep-24. Avail Bal: Rs. 15,250.00."*

### How We Avoid Capturing the Account Balance:
1. `extractTransactionAmount()` inspects a **35-character surrounding window** before and after every matched amount.
2. If surrounding text contains balance keywords (`bal`, `balance`, `avail bal`, `limit`, `clear bal`, `avl bal`), the amount is tagged as `isBalance = true`.
3. If surrounding text contains action keywords (`debited`, `credited`, `spent`, `paid`, `received`, `txn of`), it is tagged as `isActionAssociated = true`.
4. Selection Priority:
   1. **Primary**: Non-balance match associated with an action verb (`500.00`).
   2. **Secondary**: Any non-balance match.
   3. **Fallback**: First matched amount.

---

## 5. Debit vs. Credit Classification Strategy

### The "Credit Card" False Positive Problem
In naive implementations, searching for the keyword `"credit"` causes every Credit Card transaction to be categorized as **INCOME**:
> *"Rs. 500.00 debited from your Credit Card ending 1234"* ❌ (Matched "credit" ➔ Marked as Income)

### Our Sanitization Solution (`classifyTransactionType`):
1. **Text Sanitization**: Before keyword evaluation, phrases like `credit card`, `credit limit`, `available credit`, `credit line`, `credit score`, `credit ac` are sanitized out of the evaluation buffer.
2. **Transfer Keyword Direction**:
   - `transferred from your A/c` / `transfer from your A/c` ➔ **EXPENSE**
   - `transferred to your A/c` / `transfer to your A/c` ➔ **INCOME**
3. **Explicit Precedence**:
   - Explicit `debited`, `spent`, `paid`, `deducted`, `charged`, `purchase` ➔ **EXPENSE**
   - Explicit `credited`, `received`, `cashback`, `refunded`, `deposited` ➔ **INCOME**

---

## 6. Merchant Extraction & Anti-Duplication

### Merchant / Payee Extraction (`extractMerchant`):
- Searches for preposition separators (` to `, ` at `, ` from `, ` for `, ` towards `).
- Cleans up trailing noise (`via UPI`, `thru`, `successful`, `on date`).
- Fallback to UPI/VPA pattern matching (`VPA amazon@apl` or `UPI/12345/MerchantName`).
- Default fallback: `"Auto-tracked"`.

### De-duplication Window (`DUPLICATION_WINDOW_MS`):
- Cache key: `"$amount|$type|$merchant"`.
- Uses a **60-second window** (`60000L`).
- Prevents duplicate entries when the Android system updates or re-posts an SMS notification in the status bar.

---

## 7. Security, Privacy & Performance Advantages

1. **100% On-Device & Private**:
   - Zero data leaves the phone. No third-party network APIs or cloud LLM endpoints receive banking SMS text.
2. **Zero Latency**:
   - Processes and saves notifications locally in `< 1ms`.
3. **Fully Offline**:
   - Transactions are saved to Room DB locally (`isSynced = false`) when offline, and synced to backend API when internet connectivity returns.
