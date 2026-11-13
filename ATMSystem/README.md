# 🏧 Java ATM System (CLI-Based)

A comprehensive, production-grade **Automated Teller Machine (ATM) System** built with **Java** adhering strictly to **Object-Oriented Programming (OOP)** principles and clean architectural separation of concerns.

---

## 🌟 Project Highlights & Features

- 🔐 **Card & PIN Security**: 4-digit PIN authentication with automatic 3-strike card lockout protection.
- 👥 **Multi-Account Support**: Single customer can manage multiple accounts (Savings & Checking) under one card.
- 💵 **Smart Cash Dispenser Vault**: Simulates physical ATM cash hardware, tracking notes (`$100`, `$50`, `$20`, `$10`) and using greedy change-making algorithms.
- 💸 **Diverse Operations**:
  - Check Balance & Account Details
  - Cash Withdrawal (Quick Cash presets & custom amounts)
  - Cash Deposit (Quick Flat Deposit & Multi-Denomination counter)
  - Inter-Account Fund Transfer (with dual transaction logging)
  - Mini Statement (Last 5 transactions) & Full History
  - Change ATM PIN
  - Switch Active Account
  - Realistic Printed Slip Receipts
- 🛠️ **Admin Control Panel**:
  - Live ATM Cash Vault status & note breakdown
  - ATM Cash refilling
  - View all customer accounts & balances
  - Unblock locked accounts and reset PINs

---

## 🧩 Core OOP Concepts Demonstrated

### 1. Encapsulation (Data Hiding & Validation)
- All fields across `Account`, `User`, `AtmCard`, and `Transaction` are marked `private` or `protected`.
- Mutation of sensitive state (like balance and PIN) is strictly governed by validation barriers:
  - Account balance cannot be directly modified; only validated `withdraw()` or `deposit()` methods can change it.
  - Card PIN and account numbers are masked (`****-****-****-4444`, `ACC-****-1001`) to protect sensitive customer data.
  - Immutable `Transaction` objects store audit trails with private final fields.

### 2. Abstraction (Contracts & Layering)
- **Interfaces**:
  - `Transactable`: Defines core banking actions (`deposit()`, `withdraw()`, `getBalance()`).
  - `Authenticatable`: Defines security policies (`verifyPin()`, `changePin()`, `isAccountLocked()`).
  - `Auditable`: Defines record-keeping methods (`recordTransaction()`, `getMiniStatement()`).
- **Abstract Class `Account`**:
  - Captures generic bank account attributes (`accountNumber`, `balance`, `card`, `transactionHistory`).
  - Exposes abstract methods `getAccountType()` and `withdraw(double amount)` ensuring derived classes implement domain-specific business rules.

### 3. Inheritance (Code Reuse & Hierarchy)
- `SavingsAccount extends Account`: Adds minimum balance enforcement, daily withdrawal limits, and interest calculations.
- `CheckingAccount extends Account`: Adds overdraft protection limits and overdraft fee policies.

### 4. Polymorphism
#### A. Runtime Polymorphism (Dynamic Method Dispatch)
- Overridden `withdraw(double amount)`:
  - In `SavingsAccount`: Verifies balance does not drop below the minimum threshold (`$500.00`) and stays within daily limit.
  - In `CheckingAccount`: Allows withdrawals into negative balances up to the authorized overdraft limit (`$3,000.00`) and applies overdraft charges.
- Overridden `getAccountType()`: Returns specialized account identity.

#### B. Compile-Time Polymorphism (Method Overloading)
- `deposit(double amount)` vs `deposit(double amount, String description)`
- `transfer(Account target, double amount)` vs `transfer(Account target, double amount, String remarks)`
- Multiple constructor overloads in `SavingsAccount` and `CheckingAccount`.

---

## 📂 Project Architecture & Package Structure

```
ATMSystem/
├── bin/                          # Compiled bytecode (.class files)
├── src/
│   └── com/
│       └── atm/
│           ├── Main.java         # Application Entry Point
│           ├── interfaces/       # Abstraction Layer
│           │   ├── Authenticatable.java
│           │   ├── Auditable.java
│           │   └── Transactable.java
│           ├── model/            # Domain Entities & Data Models
│           │   ├── Account.java  # Abstract Base Account
│           │   ├── AtmCard.java
│           │   ├── CheckingAccount.java
│           │   ├── SavingsAccount.java
│           │   ├── Transaction.java
│           │   ├── TransactionType.java (Enum)
│           │   ├── User.java
│           │   └── WithdrawResult.java
│           ├── repository/       # Data Access Layer
│           │   └── BankDatabase.java
│           ├── service/          # Business Logic & Hardware Simulation
│           │   ├── AtmDispenser.java
│           │   ├── BankService.java
│           │   └── ReceiptPrinter.java
│           └── ui/               # Presentation Layer (CLI)
│               └── ATMConsoleUI.java
├── run.bat                       # One-click Windows CMD Runner
├── build_and_run.ps1             # PowerShell Runner
└── README.md                     # Documentation
```

---

## 💳 Demo Accounts & Credentials

| Role / User | Card Number (Shortcut) | PIN | Account(s) | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Alice Johnson** | `1111222233334444` (`1`) | `1234` | `ACC1001` (Savings: $12,500)<br>`ACC1002` (Checking: $4,200) | Has 2 accounts, high limit |
| **Bob Williams** | `5555666677778888` (`2`) | `4321` | `ACC2001` (Savings: $7,500) | Normal balance |
| **Charlie Davis** | `9999888877776666` (`3`) | `9876` | `ACC3001` (Savings: $800) | Near min balance ($500 limit) |
| **System Admin** | User: `admin` | `9999` | All accounts | Vault status, refills, unlock |

> **Tip:** In the login prompt, you can simply type `1`, `2`, or `3` instead of the full 16-digit card number!

---

## 🚀 How to Compile & Run

### Method 1: Using the Batch file (Windows)
Double-click `run.bat` or run in terminal:
```cmd
cd ATMSystem
run.bat
```

### Method 2: Using PowerShell Script
```powershell
cd ATMSystem
.\build_and_run.ps1
```

### Method 3: Standard Terminal Commands
```powershell
# 1. Navigate to ATMSystem directory
cd "c:\Users\Ayush Samanta\cv projects\ATMSystem"

# 2. Compile Java sources to bin directory
javac -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)

# 3. Run application
java -cp bin com.atm.Main
```

---

## 🧾 Sample Terminal Receipt Output

```
================================================
              GLOBAL APEX BANK ATM              
            Terminal ID: ATM-NY-092             
          Location: Central Ave Branch          
================================================
Date & Time : 2026-11-13 22:03:27
Txn Ref ID  : TXN4563C45C
Card Number : ****-****-****-4444
Account No  : ACC-****-1001
Account Type: Savings Account
Card Holder : Alice Johnson
------------------------------------------------
Txn Type    : WITHDRAWAL
Amount      : $    100.00
Status      : SUCCESSFUL
Avail Bal   : $ 12,400.00
------------------------------------------------
         Thank you for banking with us!         
         Please take your card & cash.          
        For 24/7 Support: 1-800-ATM-BANK        
================================================
```
