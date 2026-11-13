package ui;

public interface Client {
    /** Runs one line of user input and returns the text to show the user. */
    String eval(String input);

    /** The client that handles the next line of input, or null to quit. */
    Client nextClient();
}
