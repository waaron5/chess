package service;

import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;

public class ChessServiceTests {
    private ChessService service;

    @BeforeEach
    public void setup() {
        service = new ChessService(new MemoryDataAccess());
    }

    // Add your @Test methods here.
}