package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import service.ChessService;
import io.javalin.http.Context;
import dataaccess.DataAccessException;
import service.ServiceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ServerHandler {
    private final ChessService service;
    private final Gson gson = new Gson();

    public ServerHandler(ChessService service) {
        this.service = service;
    }

    public void clear(Context context) throws DataAccessException {
        service.clear();
        respond(context, Map.of());
    }

    public void register(Context context) throws ServiceException, DataAccessException {
        respond(context, service.register(readBody(context, UserData.class)));
    }

    public void login(Context context) throws ServiceException, DataAccessException {
        respond(context, service.login(readBody(context, UserData.class)));
    }

    public void logout(Context context) throws ServiceException, DataAccessException {
        service.logout(context.header("authorization"));
        respond(context, Map.of());
    }

    public void listGames(Context context) throws ServiceException, DataAccessException {
        Collection<GameListEntry> games = new ArrayList<>();
        for (GameData game : service.listGames(context.header("authorization"))) {
            games.add(new GameListEntry(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        respond(context, Map.of("games", games));
    }

    public void createGame(Context context) throws ServiceException, DataAccessException {
        CreateGameRequest request = readBody(context, CreateGameRequest.class);
        GameData game = service.createGame(context.header("authorization"), request.gameName());
        respond(context, Map.of("gameID", game.gameID()));
    }

    public void joinGame(Context context) throws ServiceException, DataAccessException {
        JoinGameRequest request = readBody(context, JoinGameRequest.class);
        service.joinGame(context.header("authorization"), request.playerColor(), request.gameID());
        respond(context, Map.of());
    }

    private <T> T readBody(Context context, Class<T> type) throws ServiceException {
        T request = gson.fromJson(context.body(), type);
        if (request == null) {
            throw new ServiceException(400, "bad request");
        }
        return request;
    }

    private void respond(Context context, Object result) {
        context.contentType("application/json")
                .result(gson.toJson(result));
    }

    private record CreateGameRequest(String gameName) {
    }

    private record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) {
    }

    private record GameListEntry(int gameID, String whiteUsername, String blackUsername, String gameName) {
    }
}