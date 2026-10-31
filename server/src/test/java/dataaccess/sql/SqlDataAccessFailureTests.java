package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessFailureTests {
    private SqlDataAccess dataAccess;
    private final UserData user = new UserData("player", BCrypt.hashpw("password", BCrypt.gensalt()),
            "player@example.com");
    private final AuthData auth = new AuthData("test-token", user.username());

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess = new SqlDataAccess();
        dataAccess.clear();
    }

    @AfterEach
    public void cleanup() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    public void userTableFailuresAreReported() throws Throwable {
        dataAccess.createUser(user);

        withUnavailableTable("users", () -> {
            assertSqlFailure(() -> dataAccess.createUser(user));
            assertSqlFailure(() -> dataAccess.getUser(user.username()));
        });

        assertEquals(user, dataAccess.getUser(user.username()));
    }

    @Test
    public void authTableFailuresAreReported() throws Throwable {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);

        withUnavailableTable("auths", () -> {
            assertSqlFailure(() -> dataAccess.createAuth(auth));
            assertSqlFailure(() -> dataAccess.getAuth(auth.authToken()));
            assertSqlFailure(() -> dataAccess.deleteAuth(auth.authToken()));
        });

        assertEquals(auth, dataAccess.getAuth(auth.authToken()));
    }

    @Test
    public void gameTableFailuresAreReported() throws Throwable {
        GameData game = dataAccess.createGame("game");

        withUnavailableTable("games", () -> {
            assertSqlFailure(() -> dataAccess.createGame("other game"));
            assertSqlFailure(() -> dataAccess.getGame(game.gameID()));
            assertSqlFailure(dataAccess::listGames);
            assertSqlFailure(() -> dataAccess.updateGame(game));
        });

        assertEquals(game, dataAccess.getGame(game.gameID()));
    }

    @Test
    public void clearFailurePreservesPreviouslyDeletedRows() throws Throwable {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);
        GameData game = dataAccess.createGame("game");

        withUnavailableTable("games", () -> assertSqlFailure(dataAccess::clear));

        assertEquals(user, dataAccess.getUser(user.username()));
        assertEquals(auth, dataAccess.getAuth(auth.authToken()));
        assertEquals(game, dataAccess.getGame(game.gameID()));
    }

    @Test
    public void unavailableConnectionIsReportedByEveryOperation() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("db.name", "unavailable");
        properties.setProperty("db.user", "invalid");
        properties.setProperty("db.password", "invalid");
        properties.setProperty("db.host", "localhost");
        properties.setProperty("db.port", "100000");
        var loadProperties = DatabaseManager.class.getDeclaredMethod("loadProperties", Properties.class);
        loadProperties.setAccessible(true);
        var restoreProperties = DatabaseManager.class.getDeclaredMethod("loadPropertiesFromResources");
        restoreProperties.setAccessible(true);
        GameData game = new GameData(1, null, null, "game", new ChessGame());

        try {
            loadProperties.invoke(null, properties);

            assertThrows(DataAccessException.class, SqlDataAccess::new);
            assertThrows(DataAccessException.class, dataAccess::clear);
            assertThrows(DataAccessException.class, () -> dataAccess.createUser(user));
            assertThrows(DataAccessException.class, () -> dataAccess.getUser(user.username()));
            assertThrows(DataAccessException.class, () -> dataAccess.createAuth(auth));
            assertThrows(DataAccessException.class, () -> dataAccess.getAuth(auth.authToken()));
            assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth(auth.authToken()));
            assertThrows(DataAccessException.class, () -> dataAccess.createGame("game"));
            assertThrows(DataAccessException.class, () -> dataAccess.getGame(game.gameID()));
            assertThrows(DataAccessException.class, dataAccess::listGames);
            assertThrows(DataAccessException.class, () -> dataAccess.updateGame(game));
        } finally {
            restoreProperties.invoke(null);
        }
    }

    private void assertSqlFailure(Executable operation) {
        DataAccessException exception = assertThrows(DataAccessException.class, operation);
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    private void withUnavailableTable(String table, Executable operation) throws Throwable {
        String backup = table + "_unavailable_" + UUID.randomUUID().toString().replace("-", "");
        renameTable(table, backup);
        try {
            operation.execute();
        } finally {
            renameTable(backup, table);
        }
    }

    private void renameTable(String original, String renamed) throws Exception {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement("RENAME TABLE " + original + " TO " + renamed)) {
            statement.executeUpdate();
        }
    }
}
