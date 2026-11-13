package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameListEntry;
import model.JoinGameRequest;
import model.ListGamesResult;
import model.UserData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        return makeRequest("POST", "/user", new UserData(username, password, email), null, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        return makeRequest("POST", "/session", new UserData(username, password, null), null, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        return makeRequest("POST", "/game", new CreateGameRequest(gameName), authToken, CreateGameResult.class).gameID();
    }

    public Collection<GameListEntry> listGames(String authToken) throws ResponseException {
        return makeRequest("GET", "/game", null, authToken, ListGamesResult.class).games();
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        makeRequest("PUT", "/game", new JoinGameRequest(color, gameID), authToken, null);
    }

    private <T> T makeRequest(String method, String path, Object body, String authToken, Class<T> responseClass)
            throws ResponseException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(serverUrl + path))
                .method(method, body == null
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
        if (authToken != null) {
            request.header("authorization", authToken);
        }

        HttpResponse<String> response;
        try {
            response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ResponseException(ResponseException.UNREACHABLE, "Unable to reach the server");
        }

        int status = response.statusCode();
        if (status / 100 != 2) {
            throw new ResponseException(status, errorMessage(response.body()));
        }
        return responseClass == null ? null : gson.fromJson(response.body(), responseClass);
    }

    private String errorMessage(String body) {
        Map<?, ?> error = gson.fromJson(body, Map.class);
        return error != null && error.get("message") != null ? error.get("message").toString() : "Unknown error";
    }
}
