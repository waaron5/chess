package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChessServiceTests {
    private MemoryDataAccess dataAccess;
    private ChessService service;
    private final UserData user = new UserData("player", "password", "player@example.com");
    private AuthData auth;

    @BeforeEach
    public void setup() {
        service = new ChessService(new MemoryDataAccess());
    }

    @Test
    public void registerStoresUserAndAuth() {
        assertEquals(user, dataAccess.getUser(user.username()));
        assertEquals(user.username(), auth.username());
        assertNotNull(auth.authToken());
        assertFalse(auth.authToken().isBlank());
        assertEquals(auth, dataAccess.getAuth(auth.authToken()));
    }
}