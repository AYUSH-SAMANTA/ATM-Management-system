package com.atm.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Simulates the physical cash dispenser / vault inside the ATM.
 * Handles denomination inventory, cash dispensing algorithms, and cash refills.
 */
public class AtmDispenser {
    // Stores denomination -> count, sorted descending (e.g. 100, 50, 20, 10)
    private final Map<Integer, Integer> cashInventory;

    public AtmDispenser() {
        // Reverse order so largest notes are evaluated first
        this.cashInventory = new TreeMap<>(Collections.reverseOrder());
        initializeVault();
    }

    private void initializeVault() {
        // Default initial stock:
        cashInventory.put(100, 50); // $5,000
        cashInventory.put(50, 60);  // $3,000
        cashInventory.put(20, 100); // $2,000
        cashInventory.put(10, 100); // $1,000
        // Total = $11,000
    }

    public synchronized double getTotalCashInVault() {
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cashInventory.entrySet()) {
            total += (entry.getKey() * entry.getValue());
        }
        return total;
    }

    public synchronized Map<Integer, Integer> getVaultInventory() {
        return Collections.unmodifiableMap(cashInventory);
    }

    /**
     * Checks if ATM can dispense the requested amount using available denominations.
     * Uses a greedy change algorithm with note availability validation.
     */
    public synchronized Map<Integer, Integer> calculateDispensePlan(int amount) {
        if (amount <= 0 || amount > getTotalCashInVault()) {
            return null;
        }

        Map<Integer, Integer> dispensePlan = new LinkedHashMap<>();
        int remaining = amount;

        for (Map.Entry<Integer, Integer> entry : cashInventory.entrySet()) {
            int denom = entry.getKey();
            int availableNotes = entry.getValue();

            if (remaining >= denom && availableNotes > 0) {
                int neededNotes = remaining / denom;
                int takeNotes = Math.min(neededNotes, availableNotes);

                dispensePlan.put(denom, takeNotes);
                remaining -= (denom * takeNotes);
            }
        }

        if (remaining == 0) {
            return dispensePlan;
        } else {
            // Cannot dispense exact change with available denominations
            return null;
        }
    }

    /**
     * Deducts dispensed notes from the ATM vault.
     */
    public synchronized boolean dispense(Map<Integer, Integer> dispensePlan) {
        if (dispensePlan == null || dispensePlan.isEmpty()) {
            return false;
        }

        // Validate availability once more
        for (Map.Entry<Integer, Integer> entry : dispensePlan.entrySet()) {
            int denom = entry.getKey();
            int qty = entry.getValue();
            if (cashInventory.getOrDefault(denom, 0) < qty) {
                return false;
            }
        }

        // Deduct
        for (Map.Entry<Integer, Integer> entry : dispensePlan.entrySet()) {
            int denom = entry.getKey();
            int qty = entry.getValue();
            cashInventory.put(denom, cashInventory.get(denom) - qty);
        }
        return true;
    }

    /**
     * Accepts deposited cash and adds to vault inventory.
     */
    public synchronized void addCash(int denomination, int count) {
        if (count > 0 && cashInventory.containsKey(denomination)) {
            cashInventory.put(denomination, cashInventory.get(denomination) + count);
        }
    }

    /**
     * Refill vault with fresh supply of notes (Admin functionality).
     */
    public synchronized void refillVault(int count100, int count50, int count20, int count10) {
        cashInventory.put(100, cashInventory.getOrDefault(100, 0) + count100);
        cashInventory.put(50, cashInventory.getOrDefault(50, 0) + count50);
        cashInventory.put(20, cashInventory.getOrDefault(20, 0) + count20);
        cashInventory.put(10, cashInventory.getOrDefault(10, 0) + count10);
    }
}
