package ru.hastg99.urlshortener.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hastg99.urlshortener.configuration.AppConfig;
import ru.hastg99.urlshortener.exception.InvalidUrlException;
import ru.hastg99.urlshortener.exception.LinkExpiredException;
import ru.hastg99.urlshortener.exception.LinkNotFoundException;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.repository.LinkRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private AppConfig appConfig;

    @Mock
    private AppConfig.LinkConfig linkConfig;

    @Mock
    private CodeGenerationService codeGenerationService;

    @InjectMocks
    private LinkService linkService;

    @Test
    void createShortLink_ValidUrl_ReturnsShortCode() {
        // Arrange
        String originalUrl = "https://example.com";
        when(codeGenerationService.generateUniqueCode(anyInt(), any(), anyInt()))
                .thenReturn("abc123");
        when(appConfig.getLink()).thenReturn(linkConfig);
        when(linkConfig.getDefaultExpirationDays()).thenReturn(30);
        when(linkConfig.getLength()).thenReturn(7);
        when(linkRepository.save(any(Link.class))).thenReturn(new Link());

        // Act
        String result = linkService.createShortLink(originalUrl, null);

        // Assert
        assertEquals("abc123", result);
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void createShortLink_InvalidUrl_ThrowsException() {
        // Arrange
        String invalidUrl = "invalid-url";

        // Act & Assert
        assertThrows(InvalidUrlException.class, () ->
                linkService.createShortLink(invalidUrl, null));
    }

    @Test
    void findByShortCode_ExistingCode_ReturnsLink() {
        // Arrange
        String shortCode = "abc123";
        Link link = new Link("https://example.com", shortCode);
        link.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(linkRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(link));

        // Act
        Link result = linkService.findByShortCode(shortCode);

        // Assert
        assertNotNull(result);
        assertEquals(shortCode, result.getShortCode());
    }

    @Test
    void findByShortCode_ExpiredLink_ThrowsException() {
        // Arrange
        String shortCode = "expired";
        Link link = new Link("https://example.com", shortCode);
        link.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(linkRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(link));

        // Act & Assert
        assertThrows(LinkExpiredException.class, () ->
                linkService.findByShortCode(shortCode));
    }

    @Test
    void getOriginalUrl_ValidCode_ReturnsUrlAndIncrementsCount() {
        // Arrange
        String shortCode = "abc123";
        Link link = new Link("https://example.com", shortCode);
        link.setId(1L);
        link.setClickCount(0L);
        link.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(linkRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(link));
        doNothing().when(linkRepository).incrementClickCount(1L);

        // Act
        String result = linkService.getOriginalUrl(shortCode);

        // Assert
        assertEquals("https://example.com", result);
        verify(linkRepository).incrementClickCount(1L);
        assertEquals(1L, link.getClickCount()); // Проверяем, что счетчик увеличился
    }
}