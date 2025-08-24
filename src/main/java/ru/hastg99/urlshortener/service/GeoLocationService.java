package ru.hastg99.urlshortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hastg99.urlshortener.configuration.AppConfig;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для определения геолокации по IP-адресу.
 * Использует внешний API (ip-api.com) для получения информации о стране.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GeoLocationService {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    /**
     * Асинхронно определяет страну по IP-адресу.
     *
     * @param ipAddress IP-адрес для определения геолокации
     * @return CompletableFuture с названием страны
     */
    @Async
    public CompletableFuture<String> getCountryCodeByIp(String ipAddress) {
        try {
            // Проверка включения сервиса в настройках
            if (!appConfig.getIpApi().isEnabled()) {
                return CompletableFuture.completedFuture("Disabled");
            }

            // Пропускаем локальные IP-адреса
            if (ipAddress == null || ipAddress.equals("127.0.0.1") || ipAddress.startsWith("192.168.")) {
                return CompletableFuture.completedFuture("Local");
            }

            // Запрос к внешнему API
            String url = appConfig.getIpApi().getUrl() + ipAddress + "?fields=country,status";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if ("success".equals(response.get("status"))) {
                return CompletableFuture.completedFuture((String) response.getOrDefault("country", "Unknown"));
            } else {
                return CompletableFuture.completedFuture("Unknown");
            }
        } catch (Exception e) {
            log.warn("Failed to fetch country for IP: {}", ipAddress, e);
            return CompletableFuture.completedFuture("Unknown");
        }
    }
}