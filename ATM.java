import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ============================================================================
// OOP CONCEPT 1: ABSTRACTION (Interfaces)
// Defining clear behavioral contracts for banking operations
// ============================================================================
interface Transactable {
    boolean deposit(double amount);
    boolean deposit(double amount, String description); // Method Overloading
    boolean withdraw(double amount);
    double getBalance();
}

interface Authenticatable {
    boolean verifyPin(int pin);
    void changePin(int oldPin, int newPin);
}

// ============================================================================
// OOP CONCEPT 2: ABSTRACTION & ENCAPSULATION (Abstract Base Class)
// Data hiding with private/protected fields and strict accessors
// ============================================================================
abstract class Account implements Transactable, Authenticatable {
    // Encapsulated attributes
    private final String accountNumber;
    private final String holderName;
    private int pin;
    protected double balance; // Protected so subclasses can modify through inheritance rules

    public Account(String accountNumber, String holderName, double initialBalance, int pin) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.pin = pin;
    }

    // Encapsulation: Read-only accessors
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    // Authenticatable implementation
    @Override
    public boolean verifyPin(int enteredPin) {
        return this.pin == enteredPin;
    }

    @Override
    public void changePin(int oldPin, int newPin) {
        if (this.pin == oldPin) {
            this.pin = newPin;
            System.out.println("[Security] PIN changed successfully.");
        } else {
            System.out.println("[Security Error] Incorrect old PIN.");
        }
    }

    // OOP Polymorphism: Compile-Time (Method Overloading)
    @Override
    public boolean deposit(double amount) {
        return deposit(amount, "Standard Deposit");
    }

    @Override
    public boolean deposit(double amount, String description) {
        if (amount <= 0) {
            System.out.println("[Error] Deposit amount must be positive.");
            return false;
        }
        this.balance += amount;
        System.out.printf("[Deposit: %s] Added $%.2f | New Balance: $%.2f%n", description, amount, this.balance);
        return true;
    }

    // Abstract methods enforced on subclasses (Abstraction + Polymorphism)
    @Override
    public abstract boolean withdraw(double amount);
    public abstract String getAccountType();

    public void displayAccountInfo() {
        System.out.println("----------------------------------------");
        System.out.println(" Account Number : " + accountNumber);
        System.out.println(" Account Holder : " + holderName);
        System.out.println(" Account Type   : " + getAccountType());
        System.out.printf(" Current Balance: $%.2f%n", balance);
        System.out.println("----------------------------------------");
    }
}

// ============================================================================
// OOP CONCEPT 3: INHERITANCE & RUNTIME POLYMORPHISM (Savings Account)
// Inherits from Account and overrides withdraw() with minimum balance rules
// ============================================================================
class SavingsAccount extends Account {
    private final double minimumBalance;

    public SavingsAccount(String accountNumber, String holderName, double initialBalance, int pin) {
        super(accountNumber, holderName, initialBalance, pin);
        this.minimumBalance = 500.0;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    // Runtime Polymorphism: Dynamic Method Overriding
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("[Error] Withdrawal amount must be greater than zero.");
            return false;
        }

        if (this.balance - amount < minimumBalance) {
            System.out.printf("[Declined] Minimum balance requirement of $%.2f violated! Max withdrawable: $%.2f%n",
                    minimumBalance, Math.max(0, this.balance - minimumBalance));
            return false;
        }

        this.balance -= amount;
        System.out.printf("[Success] Withdrawn $%.2f | Remaining Balance: $%.2f%n", amount, this.balance);
        return true;
    }
}

// ============================================================================
// OOP CONCEPT 4: INHERITANCE & RUNTIME POLYMORPHISM (Current Account)
// Inherits from Account and overrides withdraw() with overdraft protection rules
// ============================================================================
class CurrentAccount extends Account {
    private final double overdraftLimit;

    public CurrentAccount(String accountNumber, String holderName, double initialBalance, int pin) {
        super(accountNumber, holderName, initialBalance, pin);
        this.overdraftLimit = 2000.0;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    // Runtime Polymorphism: Dynamic Method Overriding
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("[Error] Withdrawal amount must be greater than zero.");
            return false;
        }

        double availableFunds = this.balance + overdraftLimit;
        if (amount > availableFunds) {
            System.out.printf("[Declined] Exceeds balance + overdraft limit ($%.2f). Max withdrawable: $%.2f%n",
                    overdraftLimit, availableFunds);
            return false;
        }

        this.balance -= amount;
        System.out.printf("[Success] Withdrawn $%.2f | Remaining Balance: $%.2f%n", amount, this.balance);
        if (this.balance < 0) {
            System.out.printf("[Notice] Overdraft used! Available overdraft remaining: $%.2f%n", (overdraftLimit + this.balance));
        }
        return true;
    }
}

// ============================================================================
// OOP CONCEPT 5: AGGREGATION (Bank Entity)
// Bank has-a collection of Accounts and encapsulates account lookups
// ============================================================================
class Bank {
    private final String bankName;
    private final List<Account> accounts;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new ArrayList<>();
        initializeDefaultAccounts();
    }

    private void initializeDefaultAccounts() {
        // Polymorphic list: Account reference storing child objects
        accounts.add(new SavingsAccount("ACC101", "Ayush Samanta", 5000.0, 1234));
        accounts.add(new CurrentAccount("ACC102", "Alex Smith", 3000.0, 4321));
    }

    public Account authenticate(String accountNumber, int pin) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equalsIgnoreCase(accountNumber) && acc.verifyPin(pin)) {
                return acc;
            }
        }
        return null;
    }

    public String getBankName() {
        return bankName;
    }

    public void listDemoAccounts() {
        System.out.println("Default Demo Accounts in " + bankName + ":");
        for (Account acc : accounts) {
            System.out.printf(" - Acc: %s | Type: %-15s | Holder: %s%n",
                    acc.getAccountNumber(), acc.getAccountType(), acc.getHolderName());
        }
    }
}

// ============================================================================
// OOP CONCEPT 6: COMPOSITION (Hardware Abstractions)
// Modeling physical ATM components as dedicated Objects
// ============================================================================
class Screen {
    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    public void displayBanner(String title) {
        System.out.println("\n" + "=".repeat(44));
        System.out.println("         " + title);
        System.out.println("=".repeat(44));
    }

    public void displayMenu() {
        System.out.println("\n[1] Check Balance");
        System.out.println("[2] Deposit Money");
        System.out.println("[3] Withdraw Cash");
        System.out.println("[4] Account Details");
        System.out.println("[5] Exit / Eject Card");
    }
}

class Keypad {
    private final Scanner scanner;

    public Keypad() {
        this.scanner = new Scanner(System.in);
    }

    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public double getDoubleInput(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }
}

class CashDispenser {
    private double totalCashInAtm;

    public CashDispenser(double initialCash) {
        this.totalCashInAtm = initialCash;
    }

    public boolean canDispense(double amount) {
        return amount <= totalCashInAtm;
    }

    public void dispense(double amount) {
        if (canDispense(amount)) {
            totalCashInAtm -= amount;
            System.out.printf("[ATM Dispenser] $$$ Dispensing $%.2f cash. Please collect your bills. $$$%n", amount);
        } else {
            System.out.println("[ATM Dispenser Error] ATM is temporarily out of cash!");
        }
    }

    public void acceptCash(double amount) {
        totalCashInAtm += amount;
    }
}

// ============================================================================
// OOP CONCEPT 7: ORCHESTRATION & COMPOSITION (The ATM Object)
// ATM has-a Screen, Keypad, CashDispenser, and connects to a Bank
// ============================================================================
public class ATM {
    // Composition: ATM owns its components
    private final Screen screen;
    private final Keypad keypad;
    private final CashDispenser dispenser;
    // Aggregation: ATM interacts with a Bank
    private final Bank bank;
    private Account currentAccount;

    public ATM(Bank bank) {
        this.bank = bank;
        this.screen = new Screen();
        this.keypad = new Keypad();
        this.dispenser = new CashDispenser(20000.0); // $20,000 cash in vault
        this.currentAccount = null;
    }

    public void powerOn() {
        screen.displayBanner("WELCOME TO " + bank.getBankName());
        bank.listDemoAccounts();

        while (true) {
            screen.displayMessage("\n>>> Please insert your card / login <<<");
            String accNum = keypad.getStringInput("Enter Account Number (or 'exit' to turn off): ");
            if (accNum.equalsIgnoreCase("exit")) {
                screen.displayMessage("ATM is shutting down. Goodbye!");
                break;
            }

            int pin = keypad.getIntInput("Enter 4-Digit PIN: ");
            this.currentAccount = bank.authenticate(accNum, pin);

            if (currentAccount != null) {
                screen.displayMessage("\n[+] Authentication Successful! Welcome, " + currentAccount.getHolderName() + ".");
                runSession();
            } else {
                screen.displayMessage("[!] Authentication Failed: Invalid Account Number or PIN.");
            }
        }
    }

    private void runSession() {
        boolean inSession = true;
        while (inSession) {
            screen.displayMenu();
            int choice = keypad.getIntInput("Select an option (1-5): ");

            switch (choice) {
                case 1:
                    screen.displayMessage(String.format("Current Balance: $%.2f", currentAccount.getBalance()));
                    break;

                case 2:
                    double depAmount = keypad.getDoubleInput("Enter amount to deposit: $");
                    if (currentAccount.deposit(depAmount)) {
                        dispenser.acceptCash(depAmount);
                    }
                    break;

                case 3:
                    double withAmount = keypad.getDoubleInput("Enter amount to withdraw: $");
                    if (!dispenser.canDispense(withAmount)) {
                        screen.displayMessage("[Error] ATM does not have sufficient cash to dispense this amount.");
                    } else if (currentAccount.withdraw(withAmount)) { // Polymorphic call!
                        dispenser.dispense(withAmount);
                    }
                    break;

                case 4:
                    currentAccount.displayAccountInfo();
                    break;

                case 5:
                    screen.displayMessage("\n[+] Card ejected. Thank you for banking with " + bank.getBankName() + "!");
                    inSession = false;
                    this.currentAccount = null;
                    break;

                default:
                    screen.displayMessage("[!] Invalid option. Please choose between 1 and 5.");
            }
        }
    }

    // ========================================================================
    // MAIN METHOD: Pure OOP Entry Point
    // Main does not contain business logic; it merely instantiates the objects!
    // ========================================================================
    public static void main(String[] args) {
        Bank bank = new Bank("Apex National Bank");
        ATM atm = new ATM(bank);
        atm.powerOn();
    }
}
