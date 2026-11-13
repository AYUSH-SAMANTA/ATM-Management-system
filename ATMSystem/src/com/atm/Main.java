package com.atm;

import com.atm.repository.BankDatabase;
import com.atm.service.AtmDispenser;
import com.atm.service.BankService;
import com.atm.ui.ATMConsoleUI;

/**
 * Main application entry point for the ATM System.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize Core System Components
        BankDatabase database = new BankDatabase();
        BankService bankService = new BankService(database);
        AtmDispenser dispenser = new AtmDispenser();

        // Launch Console User Interface
        ATMConsoleUI ui = new ATMConsoleUI(bankService, dispenser);
        ui.start();
    }
}
