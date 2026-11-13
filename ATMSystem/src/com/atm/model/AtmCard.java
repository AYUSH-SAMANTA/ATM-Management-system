package com.atm.model;

/**
 * Represents an ATM Debit/Credit Card.
 * Demonstrates Encapsulation and Data Hiding.
 */
public class AtmCard {
    private final String cardNumber;
    private String pin;
    private final String cardHolderName;
    private final String expiryDate;
    private boolean isBlocked;
    private int failedAttempts;
    public static final int MAX_FAILED_ATTEMPTS = 3;

    public AtmCard(String cardNumber, String pin, String cardHolderName, String expiryDate) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.isBlocked = false;
        this.failedAttempts = 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        int length = cardNumber.length();
        String lastFour = cardNumber.substring(length - 4);
        return "****-****-****-" + lastFour;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public int getRemainingAttempts() {
        return Math.max(0, MAX_FAILED_ATTEMPTS - failedAttempts);
    }

    public boolean verifyPin(String enteredPin) {
        if (isBlocked) {
            return false;
        }
        if (this.pin.equals(enteredPin)) {
            resetFailedAttempts();
            return true;
        } else {
            incrementFailedAttempts();
            return false;
        }
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
        if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
            this.isBlocked = true;
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    public boolean changePin(String oldPin, String newPin) {
        if (isBlocked) {
            return false;
        }
        if (this.pin.equals(oldPin)) {
            if (newPin != null && newPin.matches("\\d{4}")) {
                this.pin = newPin;
                this.failedAttempts = 0;
                return true;
            }
        }
        return false;
    }

    public void unblock(String newPin) {
        this.isBlocked = false;
        this.failedAttempts = 0;
        if (newPin != null && newPin.matches("\\d{4}")) {
            this.pin = newPin;
        }
    }
}
