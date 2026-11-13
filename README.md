# 🏧 ATM Management System (Java & Pure OOP)

A complete, robust, and clean **Automated Teller Machine (ATM)** system developed in **Java** adhering strictly to **Object-Oriented Programming (OOP)** principles.

This repository includes both a **Pure OOP Single-File Application** (`ATM.java`) and a **Full Modular Enterprise Package** (`ATMSystem/`), along with a comprehensive **Project Report** (`Java_ATM_Project_Report.docx`).

---

## 🌟 Key Features

- 🔐 **Secure PIN Authentication**: Secure 4-digit credential verification.
- 👥 **Multiple Account Types**: Support for **Savings Accounts** (minimum balance constraints) and **Current Accounts** (overdraft protection).
- 💵 **Real-World ATM Hardware Modeling**:
  - `Screen`: Formatted display menus and alerts.
  - `Keypad`: Encapsulated user input validation.
  - `CashDispenser`: Cash vault tracking and currency dispensing.
- 💳 **Banking Operations**:
  - Check Account Balance
  - Deposit Cash (with optional deposit remarks)
  - Withdraw Cash (governed by account-specific policies)
  - View Full Account Details
- 📄 **Complete Documentation**: Includes a ready-to-submit Word report (`Java_ATM_Project_Report.docx`) with test cases and architecture diagrams.

---

## 🧩 Core OOP Concepts Demonstrated

| Principle | Implementation Details |
| :--- | :--- |
| **Encapsulation** | Sensitive attributes (`balance`, `pin`, `accountNumber`) are `private`/`protected`. Balance can only be mutated through validated `withdraw()` and `deposit()` methods. |
| **Abstraction** | Contracts defined using `Transactable` and `Authenticatable` interfaces, with an abstract base class `Account` providing template definitions. |
| **Inheritance** | `SavingsAccount` and `CurrentAccount` extend `Account`, inheriting common behaviors while implementing specialized rules. |
| **Polymorphism** | **Runtime (Method Overriding)**: `withdraw()` executes dynamically depending on whether the account is Savings (min balance rule) or Current (overdraft limit).<br>**Compile-time (Method Overloading)**: `deposit(amount)` vs `deposit(amount, note)`. |
| **Composition & Aggregation** | ATM has-a Screen, Keypad, and CashDispenser (**Composition**), and connects to a Bank (**Aggregation**). |

---

## 📂 Repository Structure

```
├── ATM.java                        # Pure OOP Single-File System (Directly runnable)
├── Java_ATM_Project_Report.docx    # Full Technical Project Report & Documentation
├── run.bat                         # 1-Click Windows Batch Launcher
├── .gitignore                      # Git ignore rules for build artifacts
├── README.md                       # Project documentation
│
└── ATMSystem/                      # Modular Multi-Package Enterprise Architecture
    ├── src/com/atm/
    │   ├── Main.java               # Modular Entry Point
    │   ├── interfaces/             # Abstraction contracts
    │   ├── model/                  # Domain entities (User, Account, Card, Transaction)
    │   ├── repository/             # Central in-memory database
    │   ├── service/                # Business services & receipt printing
    │   └── ui/                     # Interactive CLI UI
    └── README.md
```

---

## 🚀 How to Run

### Method 1: Direct Execution (Java 11+)
```powershell
java ATM.java
```

### Method 2: Standard Compilation
```powershell
javac ATM.java
java ATM
```

### Method 3: Windows Batch Script
Double-click `run.bat` or run:
```cmd
run.bat
```

---

## 🔑 Demo Test Accounts

| Account Number | PIN | Account Type | Initial Balance | Special Rule |
| :--- | :--- | :--- | :--- | :--- |
| `ACC101` | `1234` | **Savings** | $5,000.00 | Min Balance: $500.00 |
| `ACC102` | `4321` | **Current** | $3,000.00 | Overdraft Limit: $2,000.00 |

---

## 👤 Author

Developed by **[Ayush Samanta](https://github.com/AYUSH-SAMANTA)**.
