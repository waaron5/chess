package server;

import service.ChessService;

public class ServerHandler {
    private final ChessService service;

    public ServerHandler(ChessService service) {
        this.service = service;
    }
}