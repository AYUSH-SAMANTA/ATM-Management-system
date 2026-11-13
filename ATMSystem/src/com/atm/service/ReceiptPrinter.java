package com.atm.service;

import com.atm.model.Account;
import com.atm.model.Transaction;

/**
 * Service for generating formatted ATM Transaction Receipts.
 */
public class ReceiptPrinter {

    public static String generateReceipt(Account account, Transaction txn, String atmLocation) {
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(48);
        String dash = "-".repeat(48);

        sb.append("\n").append(line).append("\n");
        sb.append(centerText("GLOBAL APEX BANK ATM", 48)).append("\n");
        sb.append(centerText("Terminal ID: ATM-NY-092", 48)).append("\n");
        sb.append(centerText("Location: " + atmLocation, 48)).append("\n");
        sb.append(line).append("\n");

        sb.append(String.format("Date & Time : %s\n", txn.getFormattedTimestamp()));
        sb.append(String.format("Txn Ref ID  : %s\n", txn.getTransactionId()));
        sb.append(String.format("Card Number : %s\n", account.getCard() != null ? account.getCard().getMaskedCardNumber() : "N/A"));
        sb.append(String.format("Account No  : %s\n", account.getMaskedAccountNumber()));
        sb.append(String.format("Account Type: %s\n", account.getAccountType()));
        sb.append(String.format("Card Holder : %s\n", account.getAccountHolderName()));
        sb.append(dash).append("\n");

        sb.append(String.format("Txn Type    : %s\n", txn.getType()));
        sb.append(String.format("Amount      : $%,10.2f\n", txn.getAmount()));
        sb.append(String.format("Status      : %s\n", txn.isSuccessful() ? "SUCCESSFUL" : "DECLINED"));
        sb.append(String.format("Avail Bal   : $%,10.2f\n", txn.getBalanceAfter()));
        sb.append(dash).append("\n");

        sb.append(centerText("Thank you for banking with us!", 48)).append("\n");
        sb.append(centerText("Please take your card & cash.", 48)).append("\n");
        sb.append(centerText("For 24/7 Support: 1-800-ATM-BANK", 48)).append("\n");
        sb.append(line).append("\n");

        return sb.toString();
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int leftPadding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - leftPadding;
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }
}
