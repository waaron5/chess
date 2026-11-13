package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameListEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostloginClient implements Client {
    private static final String INVALID_GAME_NUMBER = "Pick a game number from the list. Type list to see the games.";

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
            case "play" -> play(params);
            case "observe" -> observe(params);
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

    private String play(String[] params) throws ResponseException {
        if (params.length != 2) {
            return "Usage: play <NUMBER> <WHITE|BLACK>";
        }
        GameListEntry game = gameByNumber(params[0]);
        if (game == null) {
            return INVALID_GAME_NUMBER;
        }
        ChessGame.TeamColor color = parseColor(params[1]);
        if (color == null) {
            return "Color must be WHITE or BLACK.";
        }
        try {
            facade.joinGame(authToken, game.gameID(), color);
        } catch (ResponseException e) {
            if (e.getStatusCode() == ResponseException.ALREADY_TAKEN) {
                return "That color is already taken in " + game.gameName() + ".";
            }
            throw e;
        }
        return "Joined " + game.gameName() + " as " + color.toString().toLowerCase() + ".\n" + drawBoard(color);
    }

    private String observe(String[] params) {
        if (params.length != 1) {
            return "Usage: observe <NUMBER>";
        }
        GameListEntry game = gameByNumber(params[0]);
        if (game == null) {
            return INVALID_GAME_NUMBER;
        }
        return "Observing " + game.gameName() + ".\n" + drawBoard(ChessGame.TeamColor.WHITE);
    }

    private GameListEntry gameByNumber(String number) {
        try {
            int index = Integer.parseInt(number) - 1;
            return index >= 0 && index < lastListedGames.size() ? lastListedGames.get(index) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private ChessGame.TeamColor parseColor(String color) {
        try {
            return ChessGame.TeamColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String drawBoard(ChessGame.TeamColor perspective) {
        return BoardRenderer.render(new ChessGame().getBoard(), perspective).stripTrailing();
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
                play <NUMBER> <WHITE|BLACK> - join a game from the list
                observe <NUMBER> - watch a game from the list
                logout - log out
                help - show available commands""";
    }
}
