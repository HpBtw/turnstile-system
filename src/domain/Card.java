package domain;

import javax.swing.*;

public class Card {
    public double fee = 5.20;
    public User user;
    private long cardId = 0;
    private double balance;

    public Card(User user, long cardId) {
        this.user = user;
        this.cardId = cardId;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public double getBalance() {
        return this.balance;
    }

    public void recharge(double charge) {
        this.balance += charge;
    }

    public void passTurnstile() {
        double cost = switch (this.user.feeType) {
            case "Full" -> fee;
            case "Half" -> fee / 2;
            case "Free" -> 0;
            default -> 0;
        };

        if (this.balance >= cost) {
            this.balance -= cost;
            JOptionPane.showMessageDialog(null, "You can passed through the turnstile.\nNew balance: " + this.balance);
        }
        else
            JOptionPane.showMessageDialog(null, "Insuficient balance");
    }

}
