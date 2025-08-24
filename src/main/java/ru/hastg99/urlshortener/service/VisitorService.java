package ru.hastg99.urlshortener.service;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.model.Visitor;
import ru.hastg99.urlshortener.repository.VisitorRepository;

import java.util.List;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepository visitorRepository;

    private final UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(10000).build();

    public void saveVisitorInfo(Link link, HttpServletRequest request) {
        Visitor visitor = new Visitor();
        visitor.setLink(link);

        // 1. Получаем IP адрес
        String ipAddress = getClientIpAddress(request);
        visitor.setIpAddress(ipAddress);

        // 2. Получаем User-Agent
        String userAgentString = request.getHeader("User-Agent");
        visitor.setUserAgent(userAgentString);

        // 3. Парсим User-Agent для получения OS, Browser и Device Type
        UserAgent agent = uaa.parse(userAgentString);
        visitor.setOperatingSystem(agent.getValue("OperatingSystemName"));
        visitor.setBrowser(agent.getValue("AgentName"));
        visitor.setDeviceType(agent.getValue("DeviceClass"));

        // 4. Получаем Referrer
        String referrer = request.getHeader("Referer");
        visitor.setReferrer(referrer);

        // 5. (Опционально) Определяем страну по IP. Пока пропустим.
        // visitor.setCountry("Russia");

        visitorRepository.save(visitor);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        // Обход для получения реального IP за прокси (Nginx, Cloudflare)
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public List<Visitor> getVisitorsByLink(Link link) {
        return visitorRepository.findByLinkOrderByVisitedAtDesc(link);
    }

    public Long getVisitorCountByLink(Link link) {
        return visitorRepository.countByLink(link);
    }

}
