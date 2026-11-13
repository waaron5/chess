package ui;

import java.util.Scanner;

public class Repl {
    private Client client;

    public Repl(Client client) {
        this.client = client;
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type help to get started.");
        Scanner scanner = new Scanner(System.in);
        while (client != null) {
            System.out.print("\n>>> ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String line = scanner.nextLine();
            try {
                System.out.println(client.eval(line));
            } catch (Exception e) {
                System.out.println("Something went wrong. Please try again.");
            }
            client = client.nextClient();
        }
    }
}
