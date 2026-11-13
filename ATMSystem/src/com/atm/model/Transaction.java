package com.atm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents an immutable record of an individual transaction.
 * Demonstrates Encapsulation through private final fields and getter methods.
 */
public class Transaction {
    private final String transactionId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final String description;
    private final boolean successful;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(TransactionType type, double amount, double balanceAfter, String description, boolean successful) {
        this.transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.successful = successful;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | %-16s | Amount: $%,10.2f | Balance: $%,10.2f | Status: %s | %s",
                getFormattedTimestamp(),
                transactionId,
                type,
                amount,
                balanceAfter,
                (successful ? "SUCCESS" : "FAILED"),
                description);
    }
}
