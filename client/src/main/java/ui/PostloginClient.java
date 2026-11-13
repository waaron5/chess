package ui;

import client.ResponseException;
import client.ServerFacade;

public class PostloginClient implements Client {
    private final ServerFacade facade;
    private final String authToken;
    private Client next = this;

    public PostloginClient(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }

    @Override
    public String eval(String input) throws ResponseException {
        String command = input.trim().split("\\s+")[0].toLowerCase();
        return switch (command) {
            case "logout" -> logout();
            default -> help();
        };
    }

    @Override
    public Client nextClient() {
        return next;
    }

    private String logout() throws ResponseException {
        facade.logout(authToken);
        next = new PreloginClient(facade);
        return "Logged out.";
    }

    private String help() {
        return """
                logout - log out
                help - show available commands""";
    }
}
