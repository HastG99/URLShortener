package ru.hastg99.urlshortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hastg99.urlshortener.configuration.AppConfig;
import ru.hastg99.urlshortener.exception.InvalidUrlException;
import ru.hastg99.urlshortener.exception.LinkExpiredException;
import ru.hastg99.urlshortener.exception.LinkNotFoundException;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.repository.LinkRepository;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Сервис для работы с короткими ссылками.
 * Обеспечивает создание, поиск и управление ссылками.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final AppConfig appConfig;
    private final CodeGenerationService codeGenerationService;

    // Регулярное выражение для валидации URL
    private static final Pattern URL_PATTERN =
            Pattern.compile("^(https?://)([\\w.-]+)(:[0-9]+)?(/.*)?$", Pattern.CASE_INSENSITIVE);

    /**
     * Создает короткую ссылку для указанного URL.
     *
     * @param originalUrl    оригинальный URL для сокращения
     * @param expirationDays срок действия ссылки в днях (опционально)
     * @return короткий код созданной ссылки
     * @throws InvalidUrlException если URL имеет неверный формат
     * @throws RuntimeException    если не удалось сгенерировать уникальный код
     */
    @Transactional
    public String createShortLink(String originalUrl, Integer expirationDays) {
        // Валидация URL
        if (originalUrl == null || !URL_PATTERN.matcher(originalUrl).matches()) {
            throw new InvalidUrlException(originalUrl);
        }

        // Генерация уникального короткого кода
        String shortCode = codeGenerationService.generateUniqueCode(
                appConfig.getLink().getLength(),
                linkRepository::existsByShortCode,
                5
        );

        // Создание и сохранение ссылки
        Link link = new Link(originalUrl, shortCode);
        int days = expirationDays != null ? expirationDays : appConfig.getLink().getDefaultExpirationDays();
        link.setExpiresAt(LocalDateTime.now().plusDays(days));
        linkRepository.save(link);

        return shortCode;
    }

    /**
     * Находит ссылку по короткому коду.
     *
     * @param shortCode короткий код ссылки
     * @return найденная ссылка
     * @throws LinkNotFoundException если ссылка не найдена
     * @throws LinkExpiredException  если срок действия ссылки истек
     */
    public Link findByShortCode(String shortCode) {
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));

        if (link.isExpired()) {
            throw new LinkExpiredException(shortCode);
        }
        return link;
    }

    /**
     * Возвращает оригинальный URL по короткому коду и увеличивает счетчик переходов.
     *
     * @param shortCode короткий код ссылки
     * @return оригинальный URL
     */
    @Transactional
    public String getOriginalUrl(String shortCode) {
        Link link = findByShortCode(shortCode);
        linkRepository.incrementClickCount(link.getId());
        link.setClickCount(link.getClickCount() + 1);
        return link.getOriginalUrl();
    }

    /**
     * Очищает просроченные ссылки. Запускается ежедневно в 3:00.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredLinks() {
        int deleted = linkRepository.deleteExpired(LocalDateTime.now());
        log.info("Deleted {} expired links", deleted);
    }
}