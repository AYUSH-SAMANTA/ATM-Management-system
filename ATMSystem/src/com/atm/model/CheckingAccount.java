package com.atm.model;

/**
 * Represents a Checking / Current Account with Overdraft Protection.
 *
 * Demonstrates:
 * 1. Inheritance: Extends Account base class.
 * 2. Runtime Polymorphism: Overrides withdraw() and getAccountType() with overdraft logic.
 * 3. Encapsulation: Manages overdraft limits and fees privately.
 */
public class CheckingAccount extends Account {
    private final double overdraftLimit;
    private final double overdraftFee;

    public CheckingAccount(String accountNumber, String accountHolderName, double initialBalance, AtmCard card,
                           double overdraftLimit, double overdraftFee) {
        super(accountNumber, accountHolderName, initialBalance, card);
        this.overdraftLimit = overdraftLimit;
        this.overdraftFee = overdraftFee;
    }

    // Convenience constructor
    public CheckingAccount(String accountNumber, String accountHolderName, double initialBalance, AtmCard card) {
        this(accountNumber, accountHolderName, initialBalance, card, 5000.0, 15.0);
    }

    @Override
    public String getAccountType() {
        return "Checking Account";
    }

    /**
     * Polymorphic withdraw implementation adhering to Checking Account Overdraft rules:
     * Allows balance to go negative up to -overdraftLimit, with an overdraft fee when applicable.
     */
    @Override
    public boolean withdraw(double amount) {
        if (isAccountLocked()) {
            System.out.println("[Error] Account is locked due to security policy.");
            return false;
        }

        if (amount <= 0) {
            System.out.println("[Error] Withdrawal amount must be greater than zero.");
            return false;
        }

        double maxWithdrawable = this.balance + overdraftLimit;
        if (amount > maxWithdrawable) {
            System.out.printf("[Error] Insufficient funds! Current Balance: $%,.2f, Overdraft Limit: $%,.2f. Max withdrawable: $%,.2f%n",
                    this.balance, overdraftLimit, maxWithdrawable);
            recordTransaction(new Transaction(
                    TransactionType.WITHDRAWAL,
                    amount,
                    this.balance,
                    "Failed Withdrawal: Exceeds balance + overdraft limit ($" + overdraftLimit + ")",
                    false
            ));
            return false;
        }

        // Check if this withdrawal enters or deepens overdraft
        boolean usedOverdraft = (amount > this.balance);
        this.balance -= amount;

        if (usedOverdraft && overdraftFee > 0) {
            this.balance -= overdraftFee;
            System.out.printf("[Notice] Overdraft utilized! An overdraft fee of $%,.2f has been charged.%n", overdraftFee);
        }

        recordTransaction(new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                this.balance,
                usedOverdraft ? "ATM Cash Withdrawal (Overdraft Used, Fee: $" + overdraftFee + ")" : "ATM Cash Withdrawal",
                true
        ));
        return true;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getOverdraftFee() {
        return overdraftFee;
    }

    public double getAvailableOverdraft() {
        if (this.balance >= 0) {
            return overdraftLimit;
        } else {
            return Math.max(0, overdraftLimit + this.balance);
        }
    }
}
