package com.atm.interfaces;

import java.util.List;
import com.atm.model.Transaction;

/**
 * Interface defining audit and transaction logging operations.
 * Demonstrates Abstraction for record keeping.
 */
public interface Auditable {
    void recordTransaction(Transaction transaction);
    List<Transaction> getTransactionHistory();
    List<Transaction> getMiniStatement(int count);
}
