package util;

import domain.Card;
import domain.User;
import security.Security;

import javax.swing.*;
import java.util.Random;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static javax.swing.JOptionPane.*;

public class Util {
    Object[] menu;
    Security sec = new Security();
    Card[] card = new Card[10]; // change the number between [] to control the maximum number of cards
    int index = 0;
    int option = 0;
    int minRange = 1;
    int maxRange = 5;

    public void menu() {
        new User("", "", 0);

        menu = new Object[]{"Admin", "User", "Close application"};

        while (true) {
        option = showOptionDialog(null,
                "Login as",
                "Sign in",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, menu, menu[0]);

        System.out.println(option);

            switch (option) {
                case 0:
                    checkPassword();
                    break;
                case 1:
                    if (index == 0) {
                        showMessageDialog(null, "There are no registered users/cards");
                        menu();
                    } else {
                        menuUser();
                        break;
                    }
                case 2:
                    showMessageDialog(null, "App closed");
                    System.exit(0);
                case 99:
                    break;
                default:
                    return;
            }
        }
    }

    private void menuUser() {
        long login = parseLong(showInputDialog("Login with your ID"));
        int pointer = searchLogin(login);
        if (pointer == -1) {
            showMessageDialog(null, "ID not found");
            menu();
        }
        menu = new Object[]{"Check your balance", "Recharge your card", "Pass through the turnstile", "Return"};

        while (true) {
            option = showOptionDialog(null, "Hi " + card[pointer].user.name + "! Choose an option",
                    "Select",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE,
                    null, menu, menu[0]);

            switch (option) {
                case 0:
                    showMessageDialog(null, "Current balance: " + card[pointer].getBalance());
                    break;
                case 1:
                    recharge(pointer);
                    break;
                case 2:
                    card[pointer].passTurnstile();
                    break;
                default:
                    menu();
                    break;
            }
        }
    }

    private void menuAdm() {
        menu = new Object[] {"Generate a new card", "List all cards", "Delete a card", "Change password", "Return"};

        while (true) {
            option = showOptionDialog(null,
                    "What are you going to do?",
                    "Select an option",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE,
                    null, menu, menu[0]);

            switch (option) {
                case 0:
                    genCard();
                    break;
                case 1:
                    printCards();
                    break;
                case 2:
                    delCard();
                    break;
                case 3:
                    changePassword();
                    break;
                case 4:
                    menu();
            }
        }
    }

    public void recharge(int pointer) {
        double charge = 0;

        try {
            charge = parseDouble(showInputDialog("How much are you going to add to your balance?"));
        } catch (Exception e) {
            showMessageDialog(null, "Insert a numeric value (##.## or ##,##, it can change depending on your OS)");
            recharge(pointer);
        }
        if (charge < 0) {
            showMessageDialog(null, "You can't add a negative value");
            recharge(pointer);
        }
        if (charge == 0)
            menuUser();

        card[pointer].recharge(charge);
    }
    public int searchLogin (long login) {
        int pointer = -1;

        for (int i = 0; i < index; i++) {
            if (login == card[i].user.id)
                pointer = i;
        }

        return pointer;
    }

    private void printCards() {
        String aux = "";
        for (int i = 0; i < index; i++) {
            aux += "Card ID: " + card[i].getCardId() + "\n";
            aux += "User's name: " + card[i].user.name + "\n";
            aux += "Balance: " + card[i].getBalance() + "\n";
            aux += "Fee type: " + card[i].user.feeType + "\n";
            aux += "=================================================\n";
        }
        showMessageDialog(null, aux);
    }

    private void genCard() {
        long id = 0;
        try {
            id = parseLong(showInputDialog("Insert the user's ID (At least 3 digits, max 9 digits)"));
        } catch (Exception e) {
            showInputDialog("Insert a numeric ID");
            genCardId();
        }

        if (id == 0) {
            showMessageDialog(null, "Null or invalid input, returning...");
            menuAdm();
        }

        if (checkEquals(id)) {
            showMessageDialog(null, "There is already an user with this ID");
            genCard();
        } else if (id < 1000 || id > 999999999) {
            showMessageDialog(null, "Invalid ID, follow the size instructions");
            genCard();
        }
        String name = showInputDialog("Insert the user's name");

        if (name.isEmpty()) {
            showMessageDialog(null, "Null or invalid input, returning...");
            menuAdm();
        }

        menu = new Object[]{"Free", "Half", "Full"};
        option = showOptionDialog(null,
                "Select the user's type of fee",
                "Type of fee",
                YES_NO_OPTION,
                QUESTION_MESSAGE,
                null, menu, menu[0]);
        String feeType = switch (option) {
            case 0 -> "Full";
            case 1 -> "Half";
            case 2 -> "Free";
            default -> "";
        };

        card[index] = new Card(new User(feeType, name, id), genCardId());
        showMessageDialog(null, "Card generated with the following ID: " + card[index++].getCardId());
        menuAdm();
    }

    private void delCard() {
        long id = -1;

        try {
            id = parseLong(showInputDialog("Insert the ID of the card you want to delete"));
        } catch (Exception e) {
            showMessageDialog(null, "Please insert a numeric ID");
            delCard();
        }
        int pointer = search(id);
        if (pointer == -1)
            showMessageDialog(null, "No CardID found");
        else {
            String aux = "Card user: " + card[pointer].user.name + "\n";
            aux += "Are you sure you want to delete this card?";

            int ans = showConfirmDialog(null, aux);
            if (ans == 0) {
                finishDel(pointer);
            } else if (ans == 1) {
                delCard();
            } else {
                menuAdm();
            }
        }
    }

    private void finishDel(int pointer) {
        card[pointer] = card[index-1];
        index--;
        showMessageDialog(null, "Card successful deleted");
    }

    private int search(long id) {
        int pointer = -1;

        for (int i = 0; i < index; i++) {
            if (id == card[i].getCardId() || id == card[i].user.id)
                pointer = i;
        }

        return pointer;
    }

    private boolean checkEquals(long id) {
        boolean equals = false;

        for (int i = 0; i < index; i++) {
            if (card[i].user.id == id) {
                equals = true;
                break;
            }
        }
        return equals;
    }

    private void checkPassword() {
        String password = showInputDialog("Insert the password");

        if (password == null) {}
        else if  (sec.checkPassword(password))
            menuAdm();
        else {
            showMessageDialog(null, "Invalid password");
            checkPassword();
        }
    }

    private void changePassword() {
            String password = showInputDialog(null, "Current password: " + sec.getPassword() + "\nInsert a new one");
            if (password == null || password.isEmpty()) {
                showMessageDialog(null, "Cancel/invalid input, returning...");
            } else {
                int c = showConfirmDialog(null, "Are you sure?");
                System.out.println(c);
                switch (c) {
                    case 0:
                        sec.setPassword(password);
                        showMessageDialog(null, "Password changed");
                        break;
                    case 1:
                        changePassword();
                        break;
                    case 2:
                        menuAdm();
                        break;
                }
            }
    }

    private long genCardId() {
        Random rng = new Random();
        long cardId = rng.nextLong(minRange, maxRange);

        minRange += 5;
        maxRange += 5;

        return cardId;
    }
}

// Entire code by: github.com/HpBtw