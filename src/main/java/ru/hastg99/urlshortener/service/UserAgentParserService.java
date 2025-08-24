package ru.hastg99.urlshortener.service;

import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

/**
 * Сервис для парсинга User-Agent строк.
 * Определяет операционную систему, браузер и тип устройства.
 */
@Service
@RequiredArgsConstructor
public class UserAgentParserService {

    private final UserAgentAnalyzer userAgentAnalyzer;

    public UserAgentParserService() {
        this.userAgentAnalyzer = UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .build();
    }

    /**
     * Парсит строку User-Agent и извлекает информацию об устройстве.
     *
     * @param userAgentString строка User-Agent из HTTP-запроса
     * @return распарсенная информация об устройстве
     */
    public ParsedUserAgent parse(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return new ParsedUserAgent("Unknown", "Unknown", "Unknown");
        }
        UserAgent agent = userAgentAnalyzer.parse(userAgentString);
        return new ParsedUserAgent(
                agent.getValue("OperatingSystemName"),
                agent.getValue("AgentName"),
                agent.getValue("DeviceClass")
        );
    }

    /**
     * Record содержащий распарсенную информацию из User-Agent.
     *
     * @param operatingSystem операционная система
     * @param browser         браузер
     * @param deviceType      тип устройства
     */
    public record ParsedUserAgent(String operatingSystem, String browser, String deviceType) {
    }
}