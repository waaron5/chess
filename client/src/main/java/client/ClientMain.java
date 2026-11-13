package client;

import ui.PreloginClient;
import ui.Repl;

public class ClientMain {
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        var facade = new ServerFacade(SERVER_PORT);
        new Repl(new PreloginClient(facade)).run();
    }
}
