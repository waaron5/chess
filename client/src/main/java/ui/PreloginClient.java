package ui;

import client.ServerFacade;

public class PreloginClient implements Client {
    private final ServerFacade facade;
    private Client next = this;

    public PreloginClient(ServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        return switch (command) {
            case "quit" -> quit();
            default -> help();
        };
    }

    @Override
    public Client nextClient() {
        return next;
    }

    private String quit() {
        next = null;
        return "Goodbye!";
    }

    private String help() {
        return """
                help - show available commands
                quit - exit the program""";
    }
}
