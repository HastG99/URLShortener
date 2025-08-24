package ru.hastg99.urlshortener.exception;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String code) {
        super("Link not found for code: " + code);
    }
}
