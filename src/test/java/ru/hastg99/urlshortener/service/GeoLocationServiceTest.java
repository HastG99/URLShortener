package ru.hastg99.urlshortener.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import ru.hastg99.urlshortener.configuration.AppConfig;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoLocationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppConfig appConfig;

    @Mock
    private AppConfig.IpApiConfig ipApiConfig;

    @InjectMocks
    private GeoLocationService geoLocationService;

    @Test
    void getCountryCodeByIp_ExternalApiEnabled_ReturnsCountry() throws ExecutionException, InterruptedException {
        // Arrange
        String ip = "8.8.8.8";
        when(appConfig.getIpApi()).thenReturn(ipApiConfig);
        when(ipApiConfig.isEnabled()).thenReturn(true);
        when(ipApiConfig.getUrl()).thenReturn("http://ip-api.com/json/");

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("status", "success", "country", "United States"));

        // Act
        String result = geoLocationService.getCountryCodeByIp(ip).get();

        // Assert
        assertEquals("United States", result);
    }

    @Test
    void getCountryCodeByIp_ApiDisabled_ReturnsDisabled() throws ExecutionException, InterruptedException {
        // Arrange
        String ip = "8.8.8.8";
        when(appConfig.getIpApi()).thenReturn(ipApiConfig);
        when(ipApiConfig.isEnabled()).thenReturn(false);

        // Act
        String result = geoLocationService.getCountryCodeByIp(ip).get();

        // Assert
        assertEquals("Disabled", result);
    }

    @Test
    void getCountryCodeByIp_LocalIp_ReturnsLocal() throws ExecutionException, InterruptedException {
        // Arrange
        String ip = "192.168.1.1";
        when(appConfig.getIpApi()).thenReturn(ipApiConfig);
        when(ipApiConfig.isEnabled()).thenReturn(true);

        // Act
        String result = geoLocationService.getCountryCodeByIp(ip).get();

        // Assert
        assertEquals("Local", result);
    }
}