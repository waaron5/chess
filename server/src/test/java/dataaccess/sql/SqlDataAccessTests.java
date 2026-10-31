package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
    private SqlDataAccess dataAccess;

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

    private void insertFixtures() throws Exception {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                statement.setString(1, "player");
                statement.setString(2, BCrypt.hashpw("password", BCrypt.gensalt()));
                statement.setString(3, "player@example.com");
                statement.executeUpdate();
            }
            try (var statement = connection.prepareStatement(
                    "INSERT INTO auths (auth_token, username) VALUES (?, ?)")) {
                statement.setString(1, "test-token");
                statement.setString(2, "player");
                statement.executeUpdate();
            }
            try (var statement = connection.prepareStatement(
                    "INSERT INTO games (white_username, game_name, game) VALUES (?, ?, ?)")) {
                statement.setString(1, "player");
                statement.setString(2, "test game");
                statement.setString(3, new Gson().toJson(new ChessGame()));
                statement.executeUpdate();
            }
        }
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
