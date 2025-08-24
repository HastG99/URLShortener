package ru.hastg99.urlshortener.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserAgentParserServiceTest {

    private final UserAgentParserService parserService = new UserAgentParserService();

    @Test
    void parse_ValidUserAgent_ReturnsParsedInfo() {
        // Arrange
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

        // Act
        UserAgentParserService.ParsedUserAgent result = parserService.parse(userAgent);

        // Assert
        assertNotNull(result);
        assertNotNull(result.operatingSystem());
        assertNotNull(result.browser());
        assertNotNull(result.deviceType());
    }

    @Test
    void parse_NullUserAgent_ReturnsUnknown() {
        // Act
        UserAgentParserService.ParsedUserAgent result = parserService.parse(null);

        // Assert
        assertEquals("Unknown", result.operatingSystem());
        assertEquals("Unknown", result.browser());
        assertEquals("Unknown", result.deviceType());
    }

    @Test
    void parse_EmptyUserAgent_ReturnsUnknown() {
        // Act
        UserAgentParserService.ParsedUserAgent result = parserService.parse("");

        // Assert
        assertEquals("Unknown", result.operatingSystem());
        assertEquals("Unknown", result.browser());
        assertEquals("Unknown", result.deviceType());
    }
}
