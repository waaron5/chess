package server;

import com.google.gson.Gson;
import service.ChessService;
import io.javalin.http.Context;
import dataaccess.DataAccessException;
import java.util.Map;

public class ServerHandler {
    private final ChessService service;
    private final Gson gson = new Gson();

    public ServerHandler(ChessService service) {
        this.service = service;
    }

    public void clear(Context context) throws DataAccessException {
        service.clear();
        respond(context, Map.of());
    }

    private void respond(Context context, Object result) {
        context.contentType("application/json")
                .result(gson.toJson(result));
    }
}