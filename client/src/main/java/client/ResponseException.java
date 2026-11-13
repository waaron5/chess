package client;

public class ResponseException extends Exception {
    public static final int UNREACHABLE = 0;

    private final int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
