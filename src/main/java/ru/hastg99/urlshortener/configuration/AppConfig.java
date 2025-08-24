package ru.hastg99.urlshortener.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурационный класс приложения.
 * Загружает настройки из application.yml с префиксом 'app'.
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private String baseUrl;
    private LinkConfig link;
    private IpApiConfig ipApi;

    /**
     * Конфигурация параметров ссылок.
     */
    @Data
    public static class LinkConfig {
        private int length;
        private int defaultExpirationDays;
    }

    /**
     * Конфигурация API для определения геолокации по IP.
     */
    @Data
    public static class IpApiConfig {
        private boolean enabled;
        private String url;
        private int timeoutMs;
        private int batchSize;
    }

    /**
     * Создает бин RestTemplate для HTTP-запросов.
     *
     * @return настроенный экземпляр RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}