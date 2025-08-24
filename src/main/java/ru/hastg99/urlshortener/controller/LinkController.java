package ru.hastg99.urlshortener.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.service.LinkService;
import ru.hastg99.urlshortener.service.VisitorService;

@Controller
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private VisitorService visitorService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("shortUrl", null);
        return "index";
    }

    @PostMapping("/create")
    public String createLink(@RequestParam String originalUrl, Model model) {
        try {
            String shortCode = linkService.createShortLink(originalUrl);
            String shortUrl = "http://localhost:8080/" + shortCode;
            model.addAttribute("shortUrl", shortUrl);
            model.addAttribute("message", "Ссылка успешно создана!");
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании ссылки: " + e.getMessage());
        }
        return "index";
    }

    @GetMapping("/{shortCode}")
    public String redirect(@PathVariable String shortCode, HttpServletRequest request) {
        try {
            Link link = linkService.findByShortCode(shortCode);
            visitorService.saveVisitorInfo(link, request);
            String originalUrl = linkService.getOriginalUrl(shortCode);
            return "redirect:" + originalUrl;
        } catch (Exception e) {
            return "redirect:/?error=Link+not+found";
        }
    }

    @GetMapping("/stats/{shortCode}")
    public String showStats(@PathVariable String shortCode, Model model) {
        try {
            Link link = linkService.findByShortCode(shortCode);
            model.addAttribute("link", link);
            model.addAttribute("visitors", visitorService.getVisitorsByLink(link));
            model.addAttribute("visitorCount", visitorService.getVisitorCountByLink(link));
            return "stats";
        } catch (Exception e) {
            return "redirect:/?error=Link+not+found";
        }
    }
}
