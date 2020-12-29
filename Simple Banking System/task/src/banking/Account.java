package banking;

public class Account {
    private final String cardNumber;
    private final String PIN;
    private int balance;

    Account(String cardNumber, String PIN) {
        this.cardNumber = cardNumber;
        this.PIN = PIN;
        this.balance = 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public int getBalance() {
        return balance;
    }
}
