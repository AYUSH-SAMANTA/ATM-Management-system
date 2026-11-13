package com.atm.model;

/**
 * Enum representing different types of ATM transactions.
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_SENT,
    TRANSFER_RECEIVED,
    PIN_CHANGE,
    BALANCE_INQUIRY
}
