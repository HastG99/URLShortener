package ru.hastg99.urlshortener.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hastg99.urlshortener.exception.InvalidUrlException;
import ru.hastg99.urlshortener.exception.LinkExpiredException;
import ru.hastg99.urlshortener.exception.LinkNotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(LinkNotFoundException.class)
    public String handleNotFound(LinkNotFoundException ex, Model model) {
        log.warn("Link not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Ссылка не найдена");
        return "error";
    }

    @ExceptionHandler(LinkExpiredException.class)
    public String handleExpired(LinkExpiredException ex, Model model) {
        log.info("Link expired: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Срок действия ссылки истёк");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        log.error("Unexpected error", ex);
        model.addAttribute("errorMessage", "Произошла неизвестная ошибка");
        return "error";
    }

    @ExceptionHandler(InvalidUrlException.class)
    public String handleInvalidUrl(InvalidUrlException ex, Model model) {
        log.warn("Invalid URL: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Некорректная ссылка. Используйте http:// или https://");
        return "error";
    }

}


