package ru.hastg99.urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.repository.LinkRepository;

import java.util.Random;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public String createShortLink(String originalUrl) {
        String shortCode;
        do {
            shortCode = generateRandomCode(7);
        } while (linkRepository.existsByShortCode(shortCode));

        Link link = new Link(originalUrl, shortCode);
        linkRepository.save(link);
        return shortCode;
    }

    public Link findByShortCode(String shortCode) {
        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Link not found for code: " + shortCode));
    }

    public String getOriginalUrl(String shortCode) {
        Link link = findByShortCode(shortCode);
        link.setClickCount(link.getClickCount() + 1);
        linkRepository.save(link);
        return link.getOriginalUrl();
    }

    private String generateRandomCode(int length) {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return sb.toString();
    }
}