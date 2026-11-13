package com.atm.service;

import com.atm.model.Account;
import com.atm.model.AtmCard;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;
import com.atm.model.User;
import com.atm.model.WithdrawResult;
import com.atm.repository.BankDatabase;

import java.util.List;
import java.util.Map;

/**
 * Core Service Layer managing banking logic and coordinating operations
 * between Database, Accounts, ATM Dispenser, and Security layers.
 */
public class BankService {
    private final BankDatabase database;

    public BankService(BankDatabase database) {
        this.database = database;
    }

    public BankDatabase getDatabase() {
        return database;
    }

    /**
     * Authenticates a cardholder via Card Number and PIN.
     */
    public User authenticateUser(String cardNumber, String pin) {
        String sanitized = cardNumber.replaceAll("[\\s-]", "");
        AtmCard card = database.findCardByNumber(sanitized);

        if (card == null) {
            System.out.println("[Error] Card not recognized in the banking network.");
            return null;
        }

        if (card.isBlocked()) {
            System.out.println("[Security Alert] Card is blocked due to 3 consecutive failed PIN attempts.");
            System.out.println("Please contact your branch administrator to unblock.");
            return null;
        }

        if (card.verifyPin(pin)) {
            return database.findUserByCardNumber(sanitized);
        } else {
            int remaining = card.getRemainingAttempts();
            if (remaining > 0) {
                System.out.printf("[Security Alert] Incorrect PIN! %d attempt(s) remaining.%n", remaining);
            } else {
                System.out.println("[Security Alert] 3 consecutive incorrect PIN attempts! Card is now BLOCKED.");
            }
            return null;
        }
    }

    /**
     * Handles Cash Withdrawal with ATM Dispenser coordination.
     */
    public WithdrawResult withdrawCash(Account account, int amount, AtmDispenser dispenser) {
        if (amount <= 0) {
            return new WithdrawResult(false, "Withdrawal amount must be greater than zero.", null, null);
        }

        if (amount % 10 != 0) {
            return new WithdrawResult(false, "Amount must be a multiple of $10.", null, null);
        }

        if (dispenser.getTotalCashInVault() < amount) {
            return new WithdrawResult(false, "ATM temporarily has insufficient cash in vault for this amount.", null, null);
        }

        // Check if dispenser can dispense this amount with available note denominations
        Map<Integer, Integer> dispensePlan = dispenser.calculateDispensePlan(amount);
        if (dispensePlan == null) {
            return new WithdrawResult(false, "ATM cannot dispense the requested denomination combination.", null, null);
        }

        // Attempt account withdrawal
        boolean debited = account.withdraw(amount);
        if (!debited) {
            return new WithdrawResult(false, "Transaction declined by bank policy (Check balance or daily limit).", null, null);
        }

        // Deduct cash from dispenser
        dispenser.dispense(dispensePlan);

        // Fetch latest transaction record
        List<Transaction> history = account.getTransactionHistory();
        Transaction latestTxn = history.isEmpty() ? null : history.get(history.size() - 1);

        return new WithdrawResult(true, "Please collect your cash.", dispensePlan, latestTxn);
    }

    /**
     * Handles Quick Cash Deposit.
     */
    public boolean depositCash(Account account, double amount, AtmDispenser dispenser) {
        if (amount <= 0) {
            System.out.println("[Error] Deposit amount must be positive.");
            return false;
        }
        if (amount % 10 != 0) {
            System.out.println("[Error] Only note denominations ($10, $20, $50, $100) are accepted.");
            return false;
        }

        boolean deposited = account.deposit(amount);
        if (deposited) {
            // Rough estimation into dispenser vault: as $100s or $50s
            int amt = (int) amount;
            int c100 = amt / 100;
            int rem = amt % 100;
            if (c100 > 0) dispenser.addCash(100, c100);
            if (rem > 0) dispenser.addCash(50, rem / 50);
        }
        return deposited;
    }

    /**
     * Handles Cash Deposit with specific note counts.
     */
    public boolean depositDenominations(Account account, Map<Integer, Integer> notes, AtmDispenser dispenser) {
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
            int denom = entry.getKey();
            int count = entry.getValue();
            if (count < 0) {
                System.out.println("[Error] Note count cannot be negative.");
                return false;
            }
            total += (denom * count);
        }

        if (total <= 0) {
            System.out.println("[Error] No valid notes inserted.");
            return false;
        }

        String desc = String.format("ATM Note Deposit: [100x%d, 50x%d, 20x%d, 10x%d]",
                notes.getOrDefault(100, 0),
                notes.getOrDefault(50, 0),
                notes.getOrDefault(20, 0),
                notes.getOrDefault(10, 0));

        boolean deposited = account.deposit(total, desc);
        if (deposited) {
            for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
                dispenser.addCash(entry.getKey(), entry.getValue());
            }
        }
        return deposited;
    }

    /**
     * Handles Inter-account Fund Transfer.
     */
    public boolean transferFunds(Account sourceAccount, String targetAccountNumber, double amount, String remarks) {
        Account targetAccount = database.findAccountByNumber(targetAccountNumber);
        if (targetAccount == null) {
            System.out.println("[Error] Target Account " + targetAccountNumber + " not found!");
            return false;
        }
        return sourceAccount.transfer(targetAccount, amount, remarks);
    }

    /**
     * Changes Card PIN.
     */
    public boolean changeCardPin(Account account, String oldPin, String newPin) {
        if (!newPin.matches("\\d{4}")) {
            System.out.println("[Error] New PIN must be a 4-digit number.");
            return false;
        }
        return account.changePin(oldPin, newPin);
    }

    /**
     * Admin: Unblock locked account / card.
     */
    public boolean adminUnlockAccount(String accountNumber) {
        Account acc = database.findAccountByNumber(accountNumber);
        if (acc != null) {
            acc.unlockAccount();
            return true;
        }
        return false;
    }
}
