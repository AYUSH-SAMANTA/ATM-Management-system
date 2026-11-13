package com.atm.interfaces;

/**
 * Interface defining security and authentication operations.
 * Demonstrates Abstraction for credential verification and security policies.
 */
public interface Authenticatable {
    boolean verifyPin(String enteredPin);
    boolean changePin(String oldPin, String newPin);
    boolean isAccountLocked();
    void lockAccount();
    void unlockAccount();
}
