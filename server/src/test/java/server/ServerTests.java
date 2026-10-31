package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTests {
    @Test
    public void databaseInitializationFailurePreventsServerCreation() throws Exception {
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

        try {
            loadProperties.invoke(null, properties);

            IllegalStateException exception = assertThrows(IllegalStateException.class, Server::new);

            assertEquals("Unable to initialize database", exception.getMessage());
            assertInstanceOf(DataAccessException.class, exception.getCause());
        } finally {
            restoreProperties.invoke(null);
        }
    }
}
