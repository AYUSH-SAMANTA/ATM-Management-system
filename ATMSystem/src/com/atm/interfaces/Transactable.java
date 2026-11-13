package com.atm.interfaces;

/**
 * Interface defining fundamental banking transaction operations.
 * Demonstrates Abstraction by specifying contract behaviors without implementation details.
 */
public interface Transactable {
    boolean deposit(double amount);
    boolean deposit(double amount, String description);
    boolean withdraw(double amount);
    double getBalance();
}
