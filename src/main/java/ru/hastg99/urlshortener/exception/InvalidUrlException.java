package ru.hastg99.urlshortener.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(String url) {
        super("Invalid URL: " + url);
    }
}
