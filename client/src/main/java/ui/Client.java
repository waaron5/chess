package ui;

import client.ResponseException;

public interface Client {
    /** Runs one line of user input and returns the text to show the user. */
    String eval(String input) throws ResponseException;

    /** The client that handles the next line of input, or null to quit. */
    Client nextClient();
}
