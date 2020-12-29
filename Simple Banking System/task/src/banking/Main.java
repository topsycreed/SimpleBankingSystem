package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static banking.DBUtils.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();
    static List<Account> accounts = new ArrayList<>();
    static String url = "";

    public static void main(String[] args) {
        if (args.length == 0) {
            url = "jdbc:sqlite:test.db";
        } else {
            String fileName = args[1];
            url = "jdbc:sqlite:" + fileName;
        }
        init();
        menu();
    }

    /**
     * Initialization of SQL Lite DB
     */
    static void init() {
        createDB();
        //dropTable();
        createTable();
    }

    static void menu() {
        do {
            System.out.println("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            String str = scanner.nextLine();
            switch (str) {
                case "1":
                    createAnAccount();
                    break;
                case "2":
                    logIntoAccount();
                    break;
                case "0":
                    System.out.print("\nBye!");
                    closeConnection();
                    return;
            }
        }
        while (true);
    }

    static void createAnAccount() {
        Account account = generateAccount();
        System.out.print("\nYour card has been created");
        System.out.print("\nYour card number:");
        System.out.print("\n" + account.getCardNumber());
        System.out.print("\nYour card PIN:");
        System.out.print("\n" + account.getPIN());
        System.out.print("\n");
        System.out.print("\n");
        addCard(account.getCardNumber(), account.getPIN());
    }

    static Account generateAccount() {
        Account account = new Account(generateCardNumber(), generatePIN());
        accounts.add(account);
        return account;
    }

    static String generateCardNumber() {
        final String IIN = "400000";
        final int upper = 999999999;
        final int lower = 000000000;
        final int intervalLength = upper - lower + 1;
        StringBuilder customerAccountNumber = new StringBuilder(String.valueOf(random.nextInt(intervalLength) + lower));
        while (customerAccountNumber.length() < 9) {
            customerAccountNumber.insert(0, "0");
        }
        String total = IIN + customerAccountNumber;
        String checksum = getChecksum(total);
        return total + checksum;
    }

    static String getChecksum(String customerAccountNumber) {
        int sum = 0;
        for (int i = 0; i < customerAccountNumber.length(); i++) {
            int number = Integer.parseInt(customerAccountNumber.substring(i, i + 1));
            if ((i + 1) % 2 != 0) {
                number = number * 2;
            }
            if (number > 9) {
                number = number - 9;
            }
            sum += number;
        }
        int lastDigit = getLastDigit(sum);
        return String.valueOf(getLastDigit(10 - lastDigit));
    }

    static int getLastDigit(int number) {
        String numberAsString = String.valueOf(number);
        return Integer.parseInt(String.valueOf(numberAsString.charAt(numberAsString.length() - 1)));
    }

    static String generatePIN() {
        final int upper = 9999;
        final int lower = 0;
        final int intervalLength = upper - lower + 1;
        StringBuilder PIN = new StringBuilder(String.valueOf(random.nextInt(intervalLength) + lower));
        while (PIN.length() < 4) {
            PIN.insert(0, "0");
        }
        return PIN.toString();
    }

    static void logIntoAccount() {
        boolean found = false;
        String inputedCardNumber, inputedPIN;
        System.out.print("\nEnter your card number:\n");
        inputedCardNumber = scanner.nextLine();
        System.out.print("Enter your PIN:\n");
        inputedPIN = scanner.nextLine();

        boolean cardExists = checkCardExists(inputedCardNumber) > 0;
        if (!cardExists) {
            System.out.println("\nWrong card number or PIN!\n");
        } else {
            if (getPinByCardNumber(inputedCardNumber).equals(inputedPIN)) {
                found = true;
                System.out.println("\nYou have successfully logged in!\n");
                accountMenu(inputedCardNumber);
            } else {
                System.out.println("\nWrong card number or PIN!\n");
            }
        }
    }

    static void accountMenu(String cardNumber) {
        do {
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            String str = scanner.nextLine();
            switch (str) {
                case "1":
                    showBalance(cardNumber);
                    break;
                case "2":
                    addIncome(cardNumber);
                    break;
                case "3":
                    doTransfer(cardNumber);
                    break;
                case "4":
                    closeAccount(cardNumber);
                    System.out.println();
                    return;
                case "5":
                    System.out.println();
                    return;
                case "0":
                    exit();
            }
        }
        while (true);
    }

    static void showBalance(String cardNumber) {
        System.out.println("\nBalance: " + getBalanceByCardNumber(cardNumber) + "\n");
    }

    static void addIncome(String cardNumber) {
        System.out.print("\nEnter income:\n");
        int income = Integer.parseInt(scanner.nextLine());
        increaseBalance(cardNumber, income);
        System.out.print("Income was added!\n");
        System.out.print("\n");
    }

    static void doTransfer(String cardNumber) {
        System.out.print("\nTransfer");
        System.out.print("\nEnter your card number:\n");
        String inputedCardNumber = scanner.nextLine();
        if (inputedCardNumber.equals(cardNumber)) {
            System.out.print("You can't transfer money to the same account!\n");
            System.out.print("\n");
            accountMenu(cardNumber);
        }
        int last = Integer.parseInt(inputedCardNumber.substring(inputedCardNumber.length() - 1));
        int last2 = Integer.parseInt(getChecksum(inputedCardNumber.substring(0, inputedCardNumber.length() - 1)));
        if (last != last2) {
            System.out.print("Probably you made mistake in the card number. Please try again!\n");
            System.out.print("\n");
            accountMenu(cardNumber);
        }
        boolean cardExists = checkCardExists(inputedCardNumber) > 0;
        if (!cardExists) {
            System.out.print("Such a card does not exist.\n");
            System.out.print("\n");
            accountMenu(cardNumber);
        }
        System.out.print("Enter how much money you want to transfer:\n");
        int transfer = Integer.parseInt(scanner.nextLine());
        int currentBalance = getBalanceByCardNumber(cardNumber);
        if (transfer > currentBalance) {
            System.out.print("Not enough money!\n");
            System.out.print("\n");
            accountMenu(cardNumber);
        }
        decreaseBalance(cardNumber, transfer);
        increaseBalance(inputedCardNumber, transfer);
        System.out.print("Success!\n");
        System.out.println();
    }

    static void closeAccount(String cardNumber) {
        deleteAccount(cardNumber);
        System.out.print("The account has been closed!\n");
    }

    static void exit() {
        closeConnection();
        System.out.print("\nBye!\n");
        System.exit(0);
    }
}
