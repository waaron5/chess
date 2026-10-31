package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SqlDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    private static final String[] CREATE_TABLES = {
            """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                password VARCHAR(60) NOT NULL,
                email VARCHAR(255) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auths (
                auth_token VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                game_id INT PRIMARY KEY AUTO_INCREMENT,
                white_username VARCHAR(255),
                black_username VARCHAR(255),
                game_name VARCHAR(255) NOT NULL,
                game TEXT NOT NULL
            )
            """
    };

    public SqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (String sql : CREATE_TABLES) {
                try (var statement = connection.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create tables", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            for (String sql : new String[]{"DELETE FROM auths", "DELETE FROM games", "DELETE FROM users"}) {
                try (var statement = connection.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear database", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new UserData(result.getString("username"), result.getString("password"),
                            result.getString("email"));
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get user", ex);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auths (auth_token, username) VALUES (?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create auth", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT auth_token, username FROM auths WHERE auth_token = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, authToken);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new AuthData(result.getString("auth_token"), result.getString("username"));
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get auth", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auths WHERE auth_token = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to delete auth", ex);
        }
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        String sql = "INSERT INTO games (game_name, game) VALUES (?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, gameName);
            statement.setString(2, gson.toJson(game));
            statement.executeUpdate();
            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new GameData(keys.getInt(1), null, null, gameName, game);
                }
                throw new DataAccessException("failed to create game: no generated ID");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT game_id, white_username, black_username, game_name, game FROM games WHERE game_id = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameID);
            try (var result = statement.executeQuery()) {
                return result.next() ? readGame(result) : null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get game", ex);
        }
    }

    private GameData readGame(ResultSet result) throws SQLException {
        return new GameData(result.getInt("game_id"), result.getString("white_username"),
                result.getString("black_username"), result.getString("game_name"),
                gson.fromJson(result.getString("game"), ChessGame.class));
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT game_id, white_username, black_username, game_name, game FROM games";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql);
             var result = statement.executeQuery()) {
            Collection<GameData> games = new ArrayList<>();
            while (result.next()) {
                games.add(readGame(result));
            }
            return games;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to list games", ex);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
