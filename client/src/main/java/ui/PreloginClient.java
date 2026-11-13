package ui;

import client.ResponseException;
import client.ServerFacade;
import model.AuthData;

import java.util.Arrays;

public class PreloginClient implements Client {
    private static final int UNAUTHORIZED = 401;
    private static final int ALREADY_TAKEN = 403;

    private final ServerFacade facade;
    private Client next = this;

    public PreloginClient(ServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public String eval(String input) throws ResponseException {
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (command) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> quit();
            default -> help();
        };
    }

    @Override
    public Client nextClient() {
        return next;
    }

    private String register(String[] params) throws ResponseException {
        if (params.length != 3) {
            return "Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        try {
            return enterPostlogin(facade.register(params[0], params[1], params[2]));
        } catch (ResponseException e) {
            if (e.getStatusCode() == ALREADY_TAKEN) {
                return "That username is already taken.";
            }
            throw e;
        }
    }

    private String login(String[] params) throws ResponseException {
        if (params.length != 2) {
            return "Usage: login <USERNAME> <PASSWORD>";
        }
        try {
            return enterPostlogin(facade.login(params[0], params[1]));
        } catch (ResponseException e) {
            if (e.getStatusCode() == UNAUTHORIZED) {
                return "Incorrect username or password.";
            }
            throw e;
        }
    }

    private String enterPostlogin(AuthData authData) {
        next = new PostloginClient(facade, authData.authToken());
        return "Logged in as " + authData.username() + ". Type help to see what you can do.";
    }

    private String quit() {
        next = null;
        return "Goodbye!";
    }

    private String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - create an account
                login <USERNAME> <PASSWORD> - log in to play chess
                quit - exit the program
                help - show available commands""";
    }
}
