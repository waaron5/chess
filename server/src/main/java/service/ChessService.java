package service;

import chess.ChessGame.TeamColor;
import model.GameData;
import java.util.Collection;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public AuthData register(UserData user) throws ServiceException, DataAccessException {
        if (user == null || isMissing(user.username()) || isMissing(user.password()) || isMissing(user.email())) {
            throw new ServiceException(400, "bad request");
        }
        if (dataAccess.getUser(user.username()) != null) {
            throw new ServiceException(403, "already taken");
        }
        dataAccess.createUser(user);
        return createAuth(user.username());
    }

    public AuthData login(UserData request) throws ServiceException, DataAccessException {
        if (request == null || isMissing(request.username()) || isMissing(request.password())) {
            throw new ServiceException(400, "bad request");
        }
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new ServiceException(401, "unauthorized");
        }
        return createAuth(user.username());
    }

    public void logout(String authToken) throws ServiceException, DataAccessException {
        authenticate(authToken);
        dataAccess.deleteAuth(authToken);
    }

    public Collection<GameData> listGames(String authToken) throws ServiceException, DataAccessException {
        authenticate(authToken);
        return dataAccess.listGames();
    }

    public GameData createGame(String authToken, String gameName) throws ServiceException, DataAccessException {
        authenticate(authToken);
        if (isMissing(gameName)) {
            throw new ServiceException(400, "bad request");
        }
        return dataAccess.createGame(gameName);
    }

    public void joinGame(String authToken, TeamColor playerColor, int gameID)
            throws ServiceException, DataAccessException {
        AuthData auth = authenticate(authToken);
        GameData game = dataAccess.getGame(gameID);
        if (playerColor == null || game == null) {
            throw new ServiceException(400, "bad request");
        }

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();
        if (playerColor == TeamColor.WHITE) {
            if (whiteUsername != null) {
                throw new ServiceException(403, "already taken");
            }
            whiteUsername = auth.username();
        } else {
            if (blackUsername != null) {
                throw new ServiceException(403, "already taken");
            }
            blackUsername = auth.username();
        }
        dataAccess.updateGame(new GameData(gameID, whiteUsername, blackUsername, game.gameName(), game.game()));
    }

    private AuthData authenticate(String authToken) throws ServiceException, DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "unauthorized");
        }
        return auth;
    }

    private AuthData createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        dataAccess.createAuth(auth);
        return auth;
    }

    private boolean isMissing(String value) {
        return value == null || value.isBlank();
    }
}