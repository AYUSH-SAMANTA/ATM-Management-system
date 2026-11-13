package com.atm.model;

/**
 * Represents a Savings Bank Account.
 *
 * Demonstrates:
 * 1. Inheritance: Extends Account, inheriting core fields and methods.
 * 2. Runtime Polymorphism: Overrides withdraw() and getAccountType().
 * 3. Encapsulation: Protects savings-specific rules (minimum balance, daily limit, interest rate).
 */
public class SavingsAccount extends Account {
    private final double minimumBalance;
    private final double dailyWithdrawalLimit;
    private double dailyWithdrawnAmount;
    private final double annualInterestRate;

    public SavingsAccount(String accountNumber, String accountHolderName, double initialBalance, AtmCard card,
                          double minimumBalance, double dailyWithdrawalLimit, double annualInterestRate) {
        super(accountNumber, accountHolderName, initialBalance, card);
        this.minimumBalance = minimumBalance;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.annualInterestRate = annualInterestRate;
        this.dailyWithdrawnAmount = 0.0;
    }

    // Convenience constructor with sensible banking defaults
    public SavingsAccount(String accountNumber, String accountHolderName, double initialBalance, AtmCard card) {
        this(accountNumber, accountHolderName, initialBalance, card, 500.0, 25000.0, 4.0);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    /**
     * Polymorphic withdraw implementation adhering to Savings Account rules:
     * 1. Balance after withdrawal cannot drop below minimum balance.
     * 2. Cannot exceed daily withdrawal limit.
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

        if (dailyWithdrawnAmount + amount > dailyWithdrawalLimit) {
            double remainingLimit = Math.max(0, dailyWithdrawalLimit - dailyWithdrawnAmount);
            System.out.printf("[Error] Daily withdrawal limit ($%,.2f) exceeded! Remaining today: $%,.2f%n",
                    dailyWithdrawalLimit, remainingLimit);
            recordTransaction(new Transaction(
                    TransactionType.WITHDRAWAL,
                    amount,
                    this.balance,
                    "Failed Withdrawal: Daily limit exceeded",
                    false
            ));
            return false;
        }

        if (this.balance - amount < minimumBalance) {
            System.out.printf("[Error] Insufficient funds! Minimum balance requirement is $%,.2f. Max withdrawable: $%,.2f%n",
                    minimumBalance, Math.max(0, this.balance - minimumBalance));
            recordTransaction(new Transaction(
                    TransactionType.WITHDRAWAL,
                    amount,
                    this.balance,
                    "Failed Withdrawal: Below minimum balance ($" + minimumBalance + ")",
                    false
            ));
            return false;
        }

        // Deduct balance and update daily total
        this.balance -= amount;
        this.dailyWithdrawnAmount += amount;

        recordTransaction(new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                this.balance,
                "ATM Cash Withdrawal",
                true
        ));
        return true;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public double getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public double getDailyWithdrawnAmount() {
        return dailyWithdrawnAmount;
    }

    public double getRemainingDailyLimit() {
        return Math.max(0, dailyWithdrawalLimit - dailyWithdrawnAmount);
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public double calculateMonthlyInterest() {
        return (this.balance * (annualInterestRate / 100.0)) / 12.0;
    }

    public void resetDailyLimit() {
        this.dailyWithdrawnAmount = 0.0;
    }
}
