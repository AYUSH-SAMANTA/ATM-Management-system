package com.atm.model;

import com.atm.interfaces.Auditable;
import com.atm.interfaces.Authenticatable;
import com.atm.interfaces.Transactable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract Base Class representing a generic Bank Account.
 *
 * Demonstrates Core OOP Principles:
 * 1. Abstraction: Defines abstract methods like withdraw() and getAccountType().
 * 2. Encapsulation: Fields are protected/private with controlled accessors.
 * 3. Polymorphism: Method overloading (deposit, transfer) and base for dynamic dispatch.
 */
public abstract class Account implements Transactable, Authenticatable, Auditable {
    private final String accountNumber;
    private final String accountHolderName;
    protected double balance;
    private AtmCard card;
    private boolean isLocked;
    protected final List<Transaction> transactionHistory;

    public Account(String accountNumber, String accountHolderName, double initialBalance, AtmCard card) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.card = card;
        this.isLocked = false;
        this.transactionHistory = new ArrayList<>();

        // Record initial deposit transaction if positive
        if (initialBalance > 0) {
            recordTransaction(new Transaction(
                    TransactionType.DEPOSIT,
                    initialBalance,
                    this.balance,
                    "Initial Account Opening Balance",
                    true
            ));
        }
    }

    // --- Abstract Methods (Abstraction & Polymorphism) ---
    public abstract String getAccountType();

    @Override
    public abstract boolean withdraw(double amount);

    // --- Transactable Implementation ---
    @Override
    public double getBalance() {
        return this.balance;
    }

    // Compile-time Polymorphism: Method Overloading (deposit)
    @Override
    public boolean deposit(double amount) {
        return deposit(amount, "Cash Deposit at ATM");
    }

    @Override
    public boolean deposit(double amount, String description) {
        if (isLocked) {
            System.out.println("[Error] Account is locked. Cannot process deposit.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("[Error] Deposit amount must be greater than zero.");
            return false;
        }

        this.balance += amount;
        Transaction txn = new Transaction(
                TransactionType.DEPOSIT,
                amount,
                this.balance,
                description,
                true
        );
        recordTransaction(txn);
        return true;
    }

    // Compile-time Polymorphism: Method Overloading (transfer)
    public boolean transfer(Account targetAccount, double amount) {
        return transfer(targetAccount, amount, "Fund Transfer to " + targetAccount.getAccountNumber());
    }

    public boolean transfer(Account targetAccount, double amount, String remarks) {
        if (this.isLocked) {
            System.out.println("[Error] Source account is locked.");
            return false;
        }
        if (targetAccount == null) {
            System.out.println("[Error] Target account does not exist.");
            return false;
        }
        if (targetAccount.isAccountLocked()) {
            System.out.println("[Error] Beneficiary account is locked.");
            return false;
        }
        if (this.accountNumber.equals(targetAccount.getAccountNumber())) {
            System.out.println("[Error] Cannot transfer funds to the same account.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("[Error] Transfer amount must be positive.");
            return false;
        }

        // Attempt withdrawal from source using polymorphic withdraw logic
        if (this.withdraw(amount)) {
            // Deposit to target account
            targetAccount.deposit(amount, "Transfer received from " + this.accountNumber + " (" + remarks + ")");
            // Log transfer sent transaction for source
            recordTransaction(new Transaction(
                    TransactionType.TRANSFER_SENT,
                    amount,
                    this.balance,
                    "Transfer to " + targetAccount.getAccountNumber() + " - " + remarks,
                    true
            ));
            return true;
        } else {
            recordTransaction(new Transaction(
                    TransactionType.TRANSFER_SENT,
                    amount,
                    this.balance,
                    "Transfer failed (Insufficient funds or limit exceeded)",
                    false
            ));
            return false;
        }
    }

    // --- Authenticatable Implementation ---
    @Override
    public boolean verifyPin(String enteredPin) {
        if (isLocked || (card != null && card.isBlocked())) {
            return false;
        }
        boolean verified = (card != null) ? card.verifyPin(enteredPin) : false;
        if (card != null && card.isBlocked()) {
            this.isLocked = true;
        }
        return verified;
    }

    @Override
    public boolean changePin(String oldPin, String newPin) {
        if (card != null && card.changePin(oldPin, newPin)) {
            recordTransaction(new Transaction(
                    TransactionType.PIN_CHANGE,
                    0.0,
                    this.balance,
                    "ATM PIN successfully changed",
                    true
            ));
            return true;
        }
        return false;
    }

    @Override
    public boolean isAccountLocked() {
        return isLocked || (card != null && card.isBlocked());
    }

    @Override
    public void lockAccount() {
        this.isLocked = true;
        if (card != null) {
            card.setBlocked(true);
        }
    }

    @Override
    public void unlockAccount() {
        this.isLocked = false;
        if (card != null) {
            card.unblock("1234"); // reset to default PIN 1234
        }
    }

    // --- Auditable Implementation ---
    @Override
    public void recordTransaction(Transaction transaction) {
        this.transactionHistory.add(transaction);
    }

    @Override
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    @Override
    public List<Transaction> getMiniStatement(int count) {
        int total = transactionHistory.size();
        if (total == 0) {
            return Collections.emptyList();
        }
        int fromIndex = Math.max(0, total - count);
        return Collections.unmodifiableList(transactionHistory.subList(fromIndex, total));
    }

    // --- Encapsulation / Getters ---
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public AtmCard getCard() {
        return card;
    }

    public void setCard(AtmCard card) {
        this.card = card;
    }

    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return "ACC-****-" + accountNumber.substring(accountNumber.length() - 4);
    }

    @Override
    public String toString() {
        return String.format("[%s] Acc No: %s | Holder: %s | Balance: $%,.2f | Status: %s",
                getAccountType(),
                accountNumber,
                accountHolderName,
                balance,
                (isAccountLocked() ? "LOCKED" : "ACTIVE"));
    }
}
