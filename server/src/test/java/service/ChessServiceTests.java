package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    public void loginCreatesIndependentTokens() throws Exception {
        UserData request = new UserData(user.username(), user.password(), null);
        AuthData first = service.login(request);
        AuthData second = service.login(request);

        assertNotEquals(auth.authToken(), first.authToken());
        assertNotEquals(first.authToken(), second.authToken());
        assertEquals(user.username(), first.username());
        assertEquals(first, dataAccess.getAuth(first.authToken()));
        assertEquals(second, dataAccess.getAuth(second.authToken()));
    }

    @ParameterizedTest
    @MethodSource("invalidLogins")
    public void loginBadRequest(UserData request) {
        ServiceException exception = assertThrows(ServiceException.class, () -> service.login(request));
        assertError(exception, 400, "bad request");
    }

    static Stream<UserData> invalidLogins() {
        return Stream.of(null, new UserData(null, "password", null),
                new UserData("player", null, null), new UserData("", "password", null));
    }

    @Test
    public void loginWrongPassword() {
        UserData request = new UserData(user.username(), "wrong", null);
        ServiceException exception = assertThrows(ServiceException.class, () -> service.login(request));
        assertError(exception, 401, "unauthorized");
    }

    @Test
    public void loginUnknownUser() {
        UserData request = new UserData("unknown", "password", null);
        ServiceException exception = assertThrows(ServiceException.class, () -> service.login(request));
        assertError(exception, 401, "unauthorized");
    }

    @Test
    public void logoutOnlyDeletesRequestedToken() throws Exception {
        AuthData other = service.login(user);
        service.logout(auth.authToken());

        assertNull(dataAccess.getAuth(auth.authToken()));
        assertEquals(other, dataAccess.getAuth(other.authToken()));
        assertEquals(user, dataAccess.getUser(user.username()));
        assertTrue(service.listGames(other.authToken()).isEmpty());
    }

    @Test
    public void logoutTwice() throws Exception {
        service.logout(auth.authToken());
        ServiceException exception = assertThrows(ServiceException.class, () -> service.logout(auth.authToken()));
        assertError(exception, 401, "unauthorized");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid"})
    public void protectedOperationsRejectInvalidTokens(String token) {
        assertError(assertThrows(ServiceException.class, () -> service.logout(token)), 401, "unauthorized");
        assertError(assertThrows(ServiceException.class, () -> service.listGames(token)), 401, "unauthorized");
        assertError(assertThrows(ServiceException.class, () -> service.createGame(token, "game")), 401, "unauthorized");
        assertError(assertThrows(ServiceException.class, () -> service.joinGame(token, TeamColor.WHITE, 1)),
                401, "unauthorized");
    }

    private void assertError(ServiceException exception, int statusCode, String message) {
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals("Error: " + message, exception.getMessage());
    }
}