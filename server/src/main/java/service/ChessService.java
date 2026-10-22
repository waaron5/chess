package service;

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

    private AuthData createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        dataAccess.createAuth(auth);
        return auth;
    }

    private boolean isMissing(String value) {
        return value == null || value.isBlank();
    }
}