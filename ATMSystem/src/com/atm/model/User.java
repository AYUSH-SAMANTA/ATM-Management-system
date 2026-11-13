package com.atm.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Bank Customer / User who may hold one or more Accounts.
 * Demonstrates Aggregation / Composition in OOP.
 */
public class User {
    private final String userId;
    private final String name;
    private final String email;
    private final String phone;
    private final List<Account> accounts;

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public Account getAccountByNumber(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return acc;
            }
        }
        return null;
    }

    public Account getAccountByCardNumber(String cardNumber) {
        for (Account acc : accounts) {
            if (acc.getCard() != null && acc.getCard().getCardNumber().equals(cardNumber)) {
                return acc;
            }
        }
        return null;
    }

    public double getTotalNetWorth() {
        double total = 0.0;
        for (Account acc : accounts) {
            total += acc.getBalance();
        }
        return total;
    }

    public boolean hasMultipleAccounts() {
        return accounts.size() > 1;
    }
}
