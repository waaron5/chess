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
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
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
    public void initializesEmptyTables() throws Exception {
        assertEquals(List.of(0, 0, 0), rowCounts());
    }

    @Test
    public void repeatedInitializationPreservesData() throws Exception {
        insertFixtures();

        new SqlDataAccess();

        assertEquals(List.of(1, 1, 1), rowCounts());
    }

    @Test
    public void clearRemovesUsersAuthsAndGames() throws Exception {
        insertFixtures();
        assertEquals(List.of(1, 1, 1), rowCounts());

        dataAccess.clear();

        assertEquals(List.of(0, 0, 0), rowCounts());
    }

    @Test
    public void clearCanBeRepeated() throws Exception {
        insertFixtures();
        dataAccess.clear();

        assertDoesNotThrow(dataAccess::clear);
        assertEquals(List.of(0, 0, 0), rowCounts());
    }

    @Test
    public void createUserPersistsAllFieldsAcrossInstances() throws Exception {
        dataAccess.createUser(user);

        SqlDataAccess other = new SqlDataAccess();

        assertEquals(user, other.getUser(user.username()));
    }

    @Test
    public void getUnknownUserReturnsNull() throws Exception {
        dataAccess.createUser(user);

        assertNull(dataAccess.getUser("missing"));
        assertNull(dataAccess.getUser(null));
        assertNull(dataAccess.getUser("' OR '1'='1"));
    }

    @Test
    public void createDuplicateUsernamePreservesExistingUser() throws Exception {
        dataAccess.createUser(user);
        UserData duplicate = new UserData(user.username(), BCrypt.hashpw("different", BCrypt.gensalt()),
                "different@example.com");

        assertThrows(DataAccessException.class, () -> dataAccess.createUser(duplicate));
        assertEquals(user, dataAccess.getUser(user.username()));
        assertEquals(List.of(1, 0, 0), rowCounts());
    }

    @Test
    public void userFieldsWithQuotesRoundTrip() throws Exception {
        UserData quoted = new UserData("o'connor", user.password(), "o'connor@example.com");

        dataAccess.createUser(quoted);

        assertEquals(quoted, dataAccess.getUser(quoted.username()));
    }

    @Test
    public void createAuthPersistsAcrossInstances() throws Exception {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);

        SqlDataAccess other = new SqlDataAccess();

        assertEquals(auth, other.getAuth(auth.authToken()));
    }

    @Test
    public void getUnknownAuthReturnsNull() throws Exception {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);

        assertNull(dataAccess.getAuth("missing"));
        assertNull(dataAccess.getAuth(null));
        assertNull(dataAccess.getAuth("' OR '1'='1"));
    }

    @Test
    public void createDuplicateTokenPreservesExistingAuth() throws Exception {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);
        UserData other = new UserData("other", user.password(), "other@example.com");
        dataAccess.createUser(other);
        AuthData duplicate = new AuthData(auth.authToken(), other.username());

        assertThrows(DataAccessException.class, () -> dataAccess.createAuth(duplicate));
        assertEquals(auth, dataAccess.getAuth(auth.authToken()));
        assertEquals(List.of(2, 1, 0), rowCounts());
    }

    @Test
    public void deleteAuthOnlyRemovesRequestedToken() throws Exception {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);
        AuthData second = new AuthData("second-token", user.username());
        dataAccess.createAuth(second);

        dataAccess.deleteAuth(auth.authToken());
        assertDoesNotThrow(() -> dataAccess.deleteAuth(auth.authToken()));
        SqlDataAccess other = new SqlDataAccess();

        assertNull(other.getAuth(auth.authToken()));
        assertEquals(second, other.getAuth(second.authToken()));
        assertEquals(user, other.getUser(user.username()));
    }

    @Test
    public void deleteMissingAuthPreservesExistingTokens() throws Exception {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);

        assertDoesNotThrow(() -> dataAccess.deleteAuth("missing"));
        assertDoesNotThrow(() -> dataAccess.deleteAuth(null));
        assertDoesNotThrow(() -> dataAccess.deleteAuth("' OR '1'='1"));
        assertEquals(auth, dataAccess.getAuth(auth.authToken()));
    }

    @Test
    public void createGamePersistsInitialStateAcrossInstances() throws Exception {
        GameData created = dataAccess.createGame("King's game");
        SqlDataAccess other = new SqlDataAccess();

        assertTrue(created.gameID() > 0);
        GameData expected = new GameData(created.gameID(), null, null, "King's game", new ChessGame());
        assertEquals(expected, created);
        GameData stored = other.getGame(created.gameID());
        assertEquals(expected, stored);
        assertNotSame(created.game(), stored.game());
    }

    @Test
    public void createGamesWithSameNameUsesUniqueIDs() throws Exception {
        GameData first = dataAccess.createGame("game");
        GameData second = dataAccess.createGame("game");

        assertTrue(first.gameID() > 0);
        assertTrue(second.gameID() > 0);
        assertNotEquals(first.gameID(), second.gameID());
        assertEquals(first, dataAccess.getGame(first.gameID()));
        assertEquals(second, dataAccess.getGame(second.gameID()));
    }

    @Test
    public void getUnknownGameReturnsNull() throws Exception {
        GameData created = dataAccess.createGame("game");

        assertNull(dataAccess.getGame(0));
        assertNull(dataAccess.getGame(-1));
        assertNull(dataAccess.getGame(Integer.MAX_VALUE));
        assertEquals(created, dataAccess.getGame(created.gameID()));
    }

    @Test
    public void createGameWithNullNameFails() throws Exception {
        assertThrows(DataAccessException.class, () -> dataAccess.createGame(null));
        assertEquals(List.of(0, 0, 0), rowCounts());
    }

    private void insertFixtures() throws Exception {
        dataAccess.createUser(user);
        dataAccess.createAuth(auth);
        dataAccess.createGame("test game");
    }

    private List<Integer> rowCounts() throws Exception {
        List<Integer> counts = new ArrayList<>();
        try (var connection = DatabaseManager.getConnection()) {
            for (String sql : new String[]{"SELECT COUNT(*) FROM users", "SELECT COUNT(*) FROM auths",
                    "SELECT COUNT(*) FROM games"}) {
                try (var statement = connection.prepareStatement(sql);
                     var result = statement.executeQuery()) {
                    result.next();
                    counts.add(result.getInt(1));
                }
            }
        }
        return counts;
    }
}
