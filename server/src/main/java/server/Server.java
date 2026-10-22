package server;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import service.ChessService;
import service.ServiceException;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        ServerHandler handler = new ServerHandler(new ChessService(new MemoryDataAccess()));
        Gson gson = new Gson();

        javalin.exception(Exception.class, (exception, context) ->
                context.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: internal server error"))));
        javalin.exception(ServiceException.class, (exception, context) ->
                context.status(exception.getStatusCode()).contentType("application/json")
                        .result(gson.toJson(Map.of("message", exception.getMessage()))));
        javalin.exception(JsonParseException.class, (exception, context) ->
                context.status(400).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: bad request"))));

        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);
        javalin.get("/game", handler::listGames);
        javalin.post("/game", handler::createGame);
        javalin.put("/game", handler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
