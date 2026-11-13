package com.atm.model;

import java.util.Map;

/**
 * Encapsulates the outcome of a withdrawal operation,
 * including notes dispensed and the resulting transaction record.
 */
public class WithdrawResult {
    private final boolean successful;
    private final String message;
    private final Map<Integer, Integer> dispensedNotes;
    private final Transaction transaction;

    public WithdrawResult(boolean successful, String message, Map<Integer, Integer> dispensedNotes, Transaction transaction) {
        this.successful = successful;
        this.message = message;
        this.dispensedNotes = dispensedNotes;
        this.transaction = transaction;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public Map<Integer, Integer> getDispensedNotes() {
        return dispensedNotes;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
