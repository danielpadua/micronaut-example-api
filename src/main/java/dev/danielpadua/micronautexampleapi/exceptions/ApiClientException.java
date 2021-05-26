package dev.danielpadua.micronautexampleapi.exceptions;

public class ApiClientException extends RuntimeException {
    private final String errorCode;

    public ApiClientException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiClientException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}
