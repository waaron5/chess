package service;

public class ServiceException extends Exception {
    private final int statusCode;

    public ServiceException(int statusCode, String message) {
        super("Error: " + message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}