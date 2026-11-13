package ui;

import client.ResponseException;
import client.ServerFacade;
import model.GameListEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostloginClient implements Client {
    private final ServerFacade facade;
    private final String authToken;
    private Client next = this;
    private List<GameListEntry> lastListedGames = new ArrayList<>();

    public PostloginClient(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }

    @Override
    public String eval(String input) throws ResponseException {
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (command) {
            case "create" -> create(params);
            case "list" -> list();
            case "logout" -> logout();
            default -> help();
        };
    }

    @Override
    public Client nextClient() {
        return next;
    }

    private String create(String[] params) throws ResponseException {
        if (params.length == 0) {
            return "Usage: create <NAME>";
        }
        String gameName = String.join(" ", params);
        facade.createGame(authToken, gameName);
        return "Created game " + gameName + ".";
    }

    private String list() throws ResponseException {
        lastListedGames = new ArrayList<>(facade.listGames(authToken));
        if (lastListedGames.isEmpty()) {
            return "No games yet. Create one with: create <NAME>";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lastListedGames.size(); i++) {
            GameListEntry game = lastListedGames.get(i);
            out.append(i + 1).append(". ").append(game.gameName())
                    .append(" - white: ").append(playerName(game.whiteUsername()))
                    .append(", black: ").append(playerName(game.blackUsername()));
            if (i < lastListedGames.size() - 1) {
                out.append("\n");
            }
        }
        return out.toString();
    }

    private String playerName(String username) {
        return username == null ? "open" : username;
    }

    private String logout() throws ResponseException {
        facade.logout(authToken);
        next = new PreloginClient(facade);
        return "Logged out.";
    }

    private String help() {
        return """
                create <NAME> - create a game
                list - list games
                logout - log out
                help - show available commands""";
    }
}
