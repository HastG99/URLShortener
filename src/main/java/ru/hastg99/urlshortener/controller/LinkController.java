package ru.hastg99.urlshortener.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.service.LinkService;
import ru.hastg99.urlshortener.service.VisitorService;

/**
 * Контроллер для обработки HTTP-запросов, связанных с короткими ссылками.
 */
@Controller
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;
    private final VisitorService visitorService;

    /**
     * Отображает главную страницу с формой создания короткой ссылки.
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы
     */
    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("shortUrl", null);
        return "index";
    }

    /**
     * Создает короткую ссылку для указанного URL.
     *
     * @param originalUrl    оригинальный URL
     * @param expirationDays срок действия в днях (опционально)
     * @param model          модель для передачи данных в представление
     * @return имя шаблона страницы с результатом
     */
    @PostMapping("/create")
    public String createLink(@RequestParam String originalUrl,
                             @RequestParam(required = false) Integer expirationDays,
                             Model model) {
        String shortCode = linkService.createShortLink(originalUrl, expirationDays);
        String shortUrl = "http://localhost:8080/" + shortCode;
        model.addAttribute("shortUrl", shortUrl);
        model.addAttribute("message", "Ссылка успешно создана!");
        return "index";
    }

    /**
     * Выполняет перенаправление по короткой ссылке.
     * Сохраняет информацию о посетителе и увеличивает счетчик переходов.
     *
     * @param shortCode короткий код ссылки
     * @param request   HTTP-запрос
     * @return перенаправление на оригинальный URL
     */
    @GetMapping("/{shortCode}")
    public String redirect(@PathVariable String shortCode, HttpServletRequest request) {
        Link link = linkService.findByShortCode(shortCode);
        visitorService.saveVisitorInfo(link, request);
        String originalUrl = linkService.getOriginalUrl(shortCode);
        return "redirect:" + originalUrl;
    }

    /**
     * Отображает статистику переходов по короткой ссылке.
     *
     * @param shortCode короткий код ссылки
     * @param model     модель для передачи данных в представление
     * @return имя шаблона страницы статистики
     */
    @GetMapping("/stats/{shortCode}")
    public String showStats(@PathVariable String shortCode, Model model) {
        Link link = linkService.findByShortCode(shortCode);

        model.addAttribute("link", link);
        model.addAttribute("recentVisitors", visitorService.getRecentVisitorsWithoutIp(link, 10));
        model.addAttribute("visitorCount", visitorService.getVisitorCountByLink(link));
        model.addAttribute("countryStats", visitorService.getCountryStats(link));
        model.addAttribute("referrerStats", visitorService.getReferrerStats(link));
        model.addAttribute("browserStats", visitorService.getBrowserStats(link));
        model.addAttribute("osStats", visitorService.getOsStats(link));
        model.addAttribute("deviceStats", visitorService.getDeviceStats(link));

        return "stats";
    }
}