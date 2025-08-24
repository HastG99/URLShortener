package ru.hastg99.urlshortener.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CodeGenerationService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        }
        return sb.toString();
    }

    public String generateUniqueCode(int length, CodeUniquenessChecker uniquenessChecker, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            String code = generateRandomCode(length);
            if (uniquenessChecker.isCodeUnique(code)) {
                return code;
            }
        }
        throw new RuntimeException("Не удалось сгенерировать уникальный код после " + maxRetries + " попыток");
    }

    @FunctionalInterface
    public interface CodeUniquenessChecker {
        boolean isCodeUnique(String code);
    }
}
