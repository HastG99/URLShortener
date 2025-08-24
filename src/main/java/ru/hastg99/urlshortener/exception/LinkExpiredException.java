package ru.hastg99.urlshortener.exception;

public class LinkExpiredException extends RuntimeException {
    public LinkExpiredException(String code) {
        super("Link expired for code: " + code);
    }
}
