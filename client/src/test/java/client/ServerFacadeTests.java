package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.clear();
    }


    @Test
    public void clearSucceeds() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        facade.clear();
        Assertions.assertThrows(ResponseException.class, () -> facade.login("player1", "password"));
    }

    @Test
    public void clearFailsWhenServerUnreachable() {
        int unusedPort = 1;
        var unreachableFacade = new ServerFacade(unusedPort);
        Assertions.assertThrows(ResponseException.class, unreachableFacade::clear);
    }

    @Test
    public void registerSucceeds() throws ResponseException {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerFailsForDuplicateUsername() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(ResponseException.class,
                () -> facade.register("player1", "other", "other@email.com"));
    }

    @Test
    public void loginSucceeds() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        Assertions.assertEquals("player1", authData.username());
    }

    @Test
    public void loginFailsWithWrongPassword() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(ResponseException.class, () -> facade.login("player1", "wrong"));
    }

    @Test
    public void logoutSucceeds() throws ResponseException {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.logout(authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutFailsWithBadToken() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("badToken"));
    }

}
