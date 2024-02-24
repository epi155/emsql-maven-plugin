package io.github.epi155.emsql.api;

public class InvalidQueryException extends Exception {
    public InvalidQueryException(String message) {
        super(message);
    }
}
