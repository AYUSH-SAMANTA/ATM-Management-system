package com.atm.ui;

import com.atm.model.Account;
import com.atm.model.CheckingAccount;
import com.atm.model.SavingsAccount;
import com.atm.model.Transaction;
import com.atm.model.User;
import com.atm.model.WithdrawResult;
import com.atm.service.AtmDispenser;
import com.atm.service.BankService;
import com.atm.service.ReceiptPrinter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Command-Line Interface (CLI) for the ATM System.
 * Handles user interactions, display formatting, menus, and input validation.
 */
public class ATMConsoleUI {
    private final BankService bankService;
    private final AtmDispenser dispenser;
    private final Scanner scanner;
    private Transaction lastTransaction;

    public ATMConsoleUI(BankService bankService, AtmDispenser dispenser) {
        this.bankService = bankService;
        this.dispenser = dispenser;
        this.scanner = new Scanner(System.in);
        this.lastTransaction = null;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printWelcomeBanner();
            printDemoCredentials();

            System.out.println("\n[1] Insert ATM Card & Login");
            System.out.println("[2] Admin Control Panel");
            System.out.println("[0] Power Off ATM");
            System.out.print("\nPlease select an option (0-2): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleUserLogin();
                    break;
                case "2":
                    handleAdminLogin();
                    break;
                case "0":
                    System.out.println("\nShutting down ATM System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid option. Please enter 0, 1, or 2.");
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            WELCOME TO GLOBAL APEX BANK ATM            ");
        System.out.println("              Smart & Secure 24/7 Banking               ");
        System.out.println("=".repeat(60));
    }

    private void printDemoCredentials() {
        System.out.println("\n--- [ QUICK DEMO TEST ACCOUNTS ] ---");
        System.out.println("1. Alice Johnson : Card: 1111222233334444 | PIN: 1234 (Savings + Checking)");
        System.out.println("2. Bob Williams  : Card: 5555666677778888 | PIN: 4321 (Savings)");
        System.out.println("3. Charlie Davis : Card: 9999888877776666 | PIN: 9876 (Low Balance - $800)");
        System.out.println("   Admin Panel   : User: admin            | PIN: 9999");
        System.out.println("------------------------------------");
    }

    private void handleUserLogin() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("               CARD AUTHENTICATION               ");
        System.out.println("-".repeat(50));
        System.out.print("Insert/Enter Card Number (or 1, 2, 3 for shortcuts): ");
        String cardInput = scanner.nextLine().trim();

        // Convenience shortcuts for quick testing
        if ("1".equals(cardInput)) cardInput = "1111222233334444";
        else if ("2".equals(cardInput)) cardInput = "5555666677778888";
        else if ("3".equals(cardInput)) cardInput = "9999888877776666";

        System.out.print("Enter 4-Digit Secret PIN: ");
        String pinInput = scanner.nextLine().trim();

        User user = bankService.authenticateUser(cardInput, pinInput);
        if (user != null) {
            System.out.println("\n[+] Authentication Successful! Welcome, " + user.getName() + ".");
            userSession(user, cardInput);
        } else {
            System.out.println("[!] Authentication failed. Card rejected or incorrect PIN.");
        }
    }

    private void userSession(User user, String cardNumber) {
        List<Account> userAccounts = user.getAccounts();
        if (userAccounts.isEmpty()) {
            System.out.println("[!] No active accounts found for this user.");
            return;
        }

        Account currentAccount = userAccounts.get(0);

        // If user has multiple accounts, let them choose initial account
        if (userAccounts.size() > 1) {
            currentAccount = selectAccount(userAccounts);
        }

        boolean sessionActive = true;
        while (sessionActive) {
            System.out.println("\n" + "=".repeat(56));
            System.out.printf(" ACTIVE ACCOUNT: %s (%s) | %s\n",
                    currentAccount.getAccountNumber(),
                    currentAccount.getAccountType(),
                    user.getName());
            System.out.println("=".repeat(56));
            System.out.println(" [1] Check Balance & Account Details");
            System.out.println(" [2] Cash Withdrawal");
            System.out.println(" [3] Cash Deposit");
            System.out.println(" [4] Fund Transfer to Another Account");
            System.out.println(" [5] Mini Statement (Recent Transactions)");
            System.out.println(" [6] Full Account Statement");
            System.out.println(" [7] Change ATM PIN");
            if (userAccounts.size() > 1) {
                System.out.println(" [8] Switch Account (Savings / Checking)");
            }
            System.out.println(" [9] Print Receipt of Last Transaction");
            System.out.println(" [0] Eject Card & Logout");
            System.out.println("-".repeat(56));
            System.out.print("Select an option: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    displayAccountDetails(currentAccount);
                    break;
                case "2":
                    handleWithdrawal(currentAccount);
                    break;
                case "3":
                    handleDeposit(currentAccount);
                    break;
                case "4":
                    handleTransfer(currentAccount);
                    break;
                case "5":
                    displayMiniStatement(currentAccount);
                    break;
                case "6":
                    displayFullStatement(currentAccount);
                    break;
                case "7":
                    handleChangePin(currentAccount);
                    break;
                case "8":
                    if (userAccounts.size() > 1) {
                        currentAccount = selectAccount(userAccounts);
                    } else {
                        System.out.println("[!] You only have one account registered.");
                    }
                    break;
                case "9":
                    printReceipt(currentAccount);
                    break;
                case "0":
                    System.out.println("\n[+] Ejecting Card: " + currentAccount.getCard().getMaskedCardNumber());
                    System.out.println("[+] Thank you for using Global Apex Bank. Have a nice day!");
                    sessionActive = false;
                    break;
                default:
                    System.out.println("[!] Invalid choice. Please try again.");
            }
        }
    }

    private Account selectAccount(List<Account> accounts) {
        System.out.println("\n--- SELECT AN ACCOUNT ---");
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.printf("[%d] %s (%s) - Balance: $%,.2f\n",
                    (i + 1), acc.getAccountNumber(), acc.getAccountType(), acc.getBalance());
        }
        while (true) {
            System.out.print("Choose account number (1-" + accounts.size() + "): ");
            String input = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < accounts.size()) {
                    return accounts.get(idx);
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("[!] Invalid selection. Try again.");
        }
    }

    private void displayAccountDetails(Account account) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              ACCOUNT SUMMARY              ");
        System.out.println("=".repeat(50));
        System.out.printf(" Account Holder : %s\n", account.getAccountHolderName());
        System.out.printf(" Account Number : %s\n", account.getAccountNumber());
        System.out.printf(" Account Type   : %s\n", account.getAccountType());
        System.out.printf(" Card Number    : %s\n", account.getCard().getMaskedCardNumber());
        System.out.printf(" Current Balance: $%,.2f\n", account.getBalance());

        // Runtime Polymorphism in action: inspecting specific account types
        if (account instanceof SavingsAccount) {
            SavingsAccount sa = (SavingsAccount) account;
            System.out.printf(" Min Balance Req: $%,.2f\n", sa.getMinimumBalance());
            System.out.printf(" Daily Limit Rem: $%,.2f / $%,.2f\n", sa.getRemainingDailyLimit(), sa.getDailyWithdrawalLimit());
            System.out.printf(" Interest Rate  : %.2f%% p.a. (Est. Monthly: $%,.2f)\n",
                    sa.getAnnualInterestRate(), sa.calculateMonthlyInterest());
        } else if (account instanceof CheckingAccount) {
            CheckingAccount ca = (CheckingAccount) account;
            System.out.printf(" Overdraft Limit: $%,.2f (Available: $%,.2f)\n",
                    ca.getOverdraftLimit(), ca.getAvailableOverdraft());
            System.out.printf(" Overdraft Fee  : $%,.2f per overdraft txn\n", ca.getOverdraftFee());
        }
        System.out.println("=".repeat(50));
    }

    private void handleWithdrawal(Account account) {
        System.out.println("\n--- CASH WITHDRAWAL ---");
        System.out.println("[1] $20       [2] $40       [3] $60");
        System.out.println("[4] $100      [5] $200      [6] $500");
        System.out.println("[7] Other Custom Amount");
        System.out.println("[0] Cancel");
        System.out.print("Select withdrawal option: ");

        String choice = scanner.nextLine().trim();
        int amount = 0;
        switch (choice) {
            case "1": amount = 20; break;
            case "2": amount = 40; break;
            case "3": amount = 60; break;
            case "4": amount = 100; break;
            case "5": amount = 200; break;
            case "6": amount = 500; break;
            case "7":
                System.out.print("Enter custom amount (multiple of $10): $");
                String input = scanner.nextLine().trim();
                try {
                    amount = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("[!] Invalid amount format.");
                    return;
                }
                break;
            case "0":
                System.out.println("Withdrawal cancelled.");
                return;
            default:
                System.out.println("[!] Invalid option.");
                return;
        }

        WithdrawResult result = bankService.withdrawCash(account, amount, dispenser);
        if (result.isSuccessful()) {
            this.lastTransaction = result.getTransaction();
            System.out.println("\n[+] SUCCESS! " + result.getMessage());
            System.out.println("--- Dispensed Notes ---");
            for (Map.Entry<Integer, Integer> entry : result.getDispensedNotes().entrySet()) {
                System.out.printf("  $%-3d notes : %d\n", entry.getKey(), entry.getValue());
            }
            System.out.printf("Remaining Account Balance: $%,.2f\n", account.getBalance());

            askForReceipt(account, lastTransaction);
        } else {
            System.out.println("\n[!] TRANSACTION FAILED: " + result.getMessage());
        }
    }

    private void handleDeposit(Account account) {
        System.out.println("\n--- CASH DEPOSIT ---");
        System.out.println("[1] Quick Deposit (Flat Amount)");
        System.out.println("[2] Multi-Denomination Cash Insertion");
        System.out.println("[0] Cancel");
        System.out.print("Choose deposit method: ");

        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) {
            System.out.print("Enter amount to deposit (multiples of $10): $");
            String amtStr = scanner.nextLine().trim();
            try {
                double amt = Double.parseDouble(amtStr);
                if (bankService.depositCash(account, amt, dispenser)) {
                    List<Transaction> history = account.getTransactionHistory();
                    this.lastTransaction = history.get(history.size() - 1);
                    System.out.printf("[+] Successfully deposited $%,.2f! New Balance: $%,.2f\n", amt, account.getBalance());
                    askForReceipt(account, lastTransaction);
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid numeric input.");
            }
        } else if ("2".equals(choice)) {
            Map<Integer, Integer> noteCounts = new HashMap<>();
            System.out.println("Enter count of each note denomination to insert (0 for none):");

            for (int denom : new int[]{100, 50, 20, 10}) {
                System.out.printf("Number of $%d notes: ", denom);
                String input = scanner.nextLine().trim();
                try {
                    int count = Integer.parseInt(input);
                    if (count > 0) noteCounts.put(denom, count);
                } catch (NumberFormatException e) {
                    System.out.println("[!] Non-numeric value ignored (treated as 0).");
                }
            }

            if (bankService.depositDenominations(account, noteCounts, dispenser)) {
                List<Transaction> history = account.getTransactionHistory();
                this.lastTransaction = history.get(history.size() - 1);
                System.out.printf("[+] Cash notes accepted! New Balance: $%,.2f\n", account.getBalance());
                askForReceipt(account, lastTransaction);
            }
        }
    }

    private void handleTransfer(Account account) {
        System.out.println("\n--- FUND TRANSFER ---");
        System.out.print("Enter Destination Account Number (e.g. ACC2001): ");
        String targetAcc = scanner.nextLine().trim();

        System.out.print("Enter Amount to Transfer: $");
        String amtStr = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid transfer amount.");
            return;
        }

        System.out.print("Enter Remark / Purpose: ");
        String remarks = scanner.nextLine().trim();
        if (remarks.isEmpty()) remarks = "ATM Inter-Account Transfer";

        if (bankService.transferFunds(account, targetAcc, amount, remarks)) {
            List<Transaction> history = account.getTransactionHistory();
            this.lastTransaction = history.get(history.size() - 1);
            System.out.printf("\n[+] Transfer Successful! Transferred $%,.2f to Account %s.\n", amount, targetAcc);
            System.out.printf("Updated Balance: $%,.2f\n", account.getBalance());
            askForReceipt(account, lastTransaction);
        } else {
            System.out.println("\n[!] Transfer Failed. Please verify target account number and balance.");
        }
    }

    private void displayMiniStatement(Account account) {
        System.out.println("\n" + "=".repeat(78));
        System.out.println("                     MINI STATEMENT (LAST 5 TRANSACTIONS)                     ");
        System.out.println("=".repeat(78));
        List<Transaction> mini = account.getMiniStatement(5);
        if (mini.isEmpty()) {
            System.out.println("  No transactions recorded yet.");
        } else {
            for (Transaction txn : mini) {
                System.out.println(txn);
            }
        }
        System.out.println("=".repeat(78));
    }

    private void displayFullStatement(Account account) {
        System.out.println("\n" + "=".repeat(78));
        System.out.printf("                 FULL TRANSACTION HISTORY FOR %s                 \n", account.getAccountNumber());
        System.out.println("=".repeat(78));
        List<Transaction> all = account.getTransactionHistory();
        if (all.isEmpty()) {
            System.out.println("  No transactions on file.");
        } else {
            for (Transaction txn : all) {
                System.out.println(txn);
            }
        }
        System.out.println("=".repeat(78));
    }

    private void handleChangePin(Account account) {
        System.out.println("\n--- CHANGE ATM PIN ---");
        System.out.print("Enter Current 4-Digit PIN: ");
        String oldPin = scanner.nextLine().trim();

        System.out.print("Enter New 4-Digit PIN: ");
        String newPin = scanner.nextLine().trim();

        System.out.print("Confirm New 4-Digit PIN: ");
        String confirmPin = scanner.nextLine().trim();

        if (!newPin.equals(confirmPin)) {
            System.out.println("[!] New PIN and Confirmation do not match.");
            return;
        }

        if (bankService.changeCardPin(account, oldPin, newPin)) {
            System.out.println("[+] PIN successfully changed! Please remember your new PIN.");
        } else {
            System.out.println("[!] PIN change failed. Ensure current PIN is correct and new PIN has 4 digits.");
        }
    }

    private void askForReceipt(Account account, Transaction txn) {
        if (txn == null) return;
        System.out.print("\nWould you like a printed receipt? (y/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.startsWith("y")) {
            System.out.println(ReceiptPrinter.generateReceipt(account, txn, "Central Ave Branch"));
        }
    }

    private void printReceipt(Account account) {
        if (lastTransaction != null) {
            System.out.println(ReceiptPrinter.generateReceipt(account, lastTransaction, "Central Ave Branch"));
        } else {
            List<Transaction> history = account.getTransactionHistory();
            if (!history.isEmpty()) {
                System.out.println(ReceiptPrinter.generateReceipt(account, history.get(history.size() - 1), "Central Ave Branch"));
            } else {
                System.out.println("[!] No recent transaction to print receipt for.");
            }
        }
    }

    private void handleAdminLogin() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("            ADMINISTRATOR ACCESS PORTAL            ");
        System.out.println("=".repeat(50));
        System.out.print("Admin Username: ");
        String user = scanner.nextLine().trim();
        System.out.print("Admin PIN: ");
        String pin = scanner.nextLine().trim();

        if ("admin".equalsIgnoreCase(user) && "9999".equals(pin)) {
            System.out.println("\n[+] Admin authenticated.");
            adminSession();
        } else {
            System.out.println("[!] Invalid Admin Credentials.");
        }
    }

    private void adminSession() {
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println("\n--- [ ATM SYSTEM ADMIN MENU ] ---");
            System.out.println("[1] View ATM Vault Cash Inventory");
            System.out.println("[2] Refill ATM Vault Cash");
            System.out.println("[3] View All Bank Accounts & Balances");
            System.out.println("[4] Unlock / Unblock a Customer Account");
            System.out.println("[0] Exit Admin Portal");
            System.out.print("Select admin action: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("\n--- ATM CASH VAULT STATUS ---");
                    System.out.printf("Total Cash in ATM: $%,.2f\n", dispenser.getTotalCashInVault());
                    System.out.println("Denomination Breakdown:");
                    for (Map.Entry<Integer, Integer> entry : dispenser.getVaultInventory().entrySet()) {
                        System.out.printf("  $%-3d notes : %4d  (= $%,8d)\n",
                                entry.getKey(), entry.getValue(), (entry.getKey() * entry.getValue()));
                    }
                    break;
                case "2":
                    System.out.println("\n--- REFILL ATM CASH VAULT ---");
                    try {
                        System.out.print("Add $100 notes: ");
                        int c100 = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Add $50 notes: ");
                        int c50 = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Add $20 notes: ");
                        int c20 = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Add $10 notes: ");
                        int c10 = Integer.parseInt(scanner.nextLine().trim());

                        dispenser.refillVault(c100, c50, c20, c10);
                        System.out.printf("[+] Vault refilled! New Total Cash: $%,.2f\n", dispenser.getTotalCashInVault());
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Invalid input numbers.");
                    }
                    break;
                case "3":
                    System.out.println("\n--- ALL REGISTERED ACCOUNTS ---");
                    for (Account acc : bankService.getDatabase().getAllAccounts()) {
                        System.out.println("  " + acc);
                    }
                    break;
                case "4":
                    System.out.print("Enter Account Number to unblock (e.g. ACC1001): ");
                    String accNum = scanner.nextLine().trim();
                    if (bankService.adminUnlockAccount(accNum)) {
                        System.out.println("[+] Account " + accNum + " has been unblocked and PIN reset to 1234.");
                    } else {
                        System.out.println("[!] Account not found.");
                    }
                    break;
                case "0":
                    System.out.println("Exiting Admin Portal.");
                    inAdmin = false;
                    break;
                default:
                    System.out.println("[!] Invalid admin option.");
            }
        }
    }
}
