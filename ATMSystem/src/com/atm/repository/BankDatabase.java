package com.atm.repository;

import com.atm.model.Account;
import com.atm.model.AtmCard;
import com.atm.model.CheckingAccount;
import com.atm.model.SavingsAccount;
import com.atm.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory repository acting as the central banking database.
 * Demonstrates Repository Pattern and Data Management.
 */
public class BankDatabase {
    private final Map<String, User> users;                       // userId -> User
    private final Map<String, Account> accountsByNumber;         // accountNumber -> Account
    private final Map<String, Account> primaryAccountsByCard;    // cardNumber -> Primary Account
    private final Map<String, AtmCard> cardsByNumber;            // cardNumber -> AtmCard

    public BankDatabase() {
        this.users = new HashMap<>();
        this.accountsByNumber = new HashMap<>();
        this.primaryAccountsByCard = new HashMap<>();
        this.cardsByNumber = new HashMap<>();
        seedDemoData();
    }

    private void seedDemoData() {
        // --- User 1: Alice Johnson ---
        User alice = new User("USR101", "Alice Johnson", "alice@example.com", "+1-555-0101");
        AtmCard aliceCard = new AtmCard("1111222233334444", "1234", "Alice Johnson", "12/28");

        SavingsAccount aliceSavings = new SavingsAccount(
                "ACC1001", "Alice Johnson", 12500.00, aliceCard, 500.0, 20000.0, 3.75
        );
        CheckingAccount aliceChecking = new CheckingAccount(
                "ACC1002", "Alice Johnson", 4200.00, aliceCard, 3000.0, 15.0
        );

        alice.addAccount(aliceSavings);
        alice.addAccount(aliceChecking);

        registerUser(alice);
        registerAccount(aliceSavings, aliceCard, true);
        registerAccount(aliceChecking, aliceCard, false);

        // --- User 2: Bob Williams ---
        User bob = new User("USR102", "Bob Williams", "bob@example.com", "+1-555-0102");
        AtmCard bobCard = new AtmCard("5555666677778888", "4321", "Bob Williams", "08/29");

        SavingsAccount bobSavings = new SavingsAccount(
                "ACC2001", "Bob Williams", 7500.00, bobCard, 500.0, 15000.0, 3.50
        );

        bob.addAccount(bobSavings);
        registerUser(bob);
        registerAccount(bobSavings, bobCard, true);

        // --- User 3: Charlie Davis ---
        User charlie = new User("USR103", "Charlie Davis", "charlie@example.com", "+1-555-0103");
        AtmCard charlieCard = new AtmCard("9999888877776666", "9876", "Charlie Davis", "05/27");

        SavingsAccount charlieSavings = new SavingsAccount(
                "ACC3001", "Charlie Davis", 800.00, charlieCard, 500.0, 10000.0, 3.00
        );

        charlie.addAccount(charlieSavings);
        registerUser(charlie);
        registerAccount(charlieSavings, charlieCard, true);
    }

    private void registerUser(User user) {
        users.put(user.getUserId(), user);
    }

    private void registerAccount(Account account, AtmCard card, boolean isPrimary) {
        accountsByNumber.put(account.getAccountNumber().toUpperCase(), account);
        if (card != null) {
            cardsByNumber.put(card.getCardNumber(), card);
            if (isPrimary || !primaryAccountsByCard.containsKey(card.getCardNumber())) {
                primaryAccountsByCard.put(card.getCardNumber(), account);
            }
        }
    }

    public Account findAccountByNumber(String accountNumber) {
        if (accountNumber == null) return null;
        return accountsByNumber.get(accountNumber.trim().toUpperCase());
    }

    public Account findAccountByCardNumber(String cardNumber) {
        if (cardNumber == null) return null;
        return primaryAccountsByCard.get(cardNumber.replaceAll("[\\s-]", ""));
    }

    public AtmCard findCardByNumber(String cardNumber) {
        if (cardNumber == null) return null;
        return cardsByNumber.get(cardNumber.replaceAll("[\\s-]", ""));
    }

    public User findUserByCardNumber(String cardNumber) {
        String sanitized = cardNumber.replaceAll("[\\s-]", "");
        for (User user : users.values()) {
            if (user.getAccountByCardNumber(sanitized) != null) {
                return user;
            }
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accountsByNumber.values());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
