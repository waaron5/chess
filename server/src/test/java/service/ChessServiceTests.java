package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ChessServiceTests {
    private MemoryDataAccess dataAccess;
    private ChessService service;
    private final UserData user = new UserData("player", "password", "player@example.com");
    private AuthData auth;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        service = new ChessService(dataAccess);
        auth = service.register(user);
    }

    @Test
    public void registerStoresUserAndAuth() {
        assertEquals(user, dataAccess.getUser(user.username()));
        assertEquals(user.username(), auth.username());
        assertNotNull(auth.authToken());
        assertFalse(auth.authToken().isBlank());
        assertEquals(auth, dataAccess.getAuth(auth.authToken()));
    }

    @Test
    public void registerDuplicateUsername() {
        UserData duplicate = new UserData(user.username(), "different", "different@example.com");
        ServiceException exception = assertThrows(ServiceException.class, () -> service.register(duplicate));
        assertError(exception, 403, "already taken");
        assertEquals(user, dataAccess.getUser(user.username()));
    }

    @ParameterizedTest
    @MethodSource("invalidRegistrations")
    public void registerBadRequest(UserData request) {
        ServiceException exception = assertThrows(ServiceException.class, () -> service.register(request));
        assertError(exception, 400, "bad request");
    }

    static Stream<UserData> invalidRegistrations() {
        return Stream.of(null,
                new UserData(null, "password", "email"),
                new UserData("", "password", "email"),
                new UserData("new", null, "email"),
                new UserData("new", " ", "email"),
                new UserData("new", "password", null),
                new UserData("new", "password", ""));
    }

    private void assertError(ServiceException exception, int statusCode, String message) {
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals("Error: " + message, exception.getMessage());
    }
}