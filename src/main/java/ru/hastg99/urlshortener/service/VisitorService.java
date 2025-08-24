package ru.hastg99.urlshortener.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.model.Visitor;
import ru.hastg99.urlshortener.model.dto.*;
import ru.hastg99.urlshortener.repository.VisitorRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final UserAgentParserService userAgentParserService;
    private final GeoLocationService geoLocationService;
    private final IpAddressService ipAddressService;

    public Visitor saveVisitorInfo(Link link, HttpServletRequest request) {
        Visitor visitor = createVisitorFromRequest(link, request);
        visitor = visitorRepository.save(visitor);

        updateCountryAsync(visitor);
        return visitor;
    }

    private Visitor createVisitorFromRequest(Link link, HttpServletRequest request) {
        Visitor visitor = new Visitor();
        visitor.setLink(link);

        String ipAddress = ipAddressService.getClientIpAddress(request);
        visitor.setIpAddress(ipAddress);

        String userAgentString = request.getHeader("User-Agent");
        visitor.setUserAgent(userAgentString);

        UserAgentParserService.ParsedUserAgent parsedUserAgent = userAgentParserService.parse(userAgentString);
        visitor.setOperatingSystem(parsedUserAgent.operatingSystem());
        visitor.setBrowser(parsedUserAgent.browser());
        visitor.setDeviceType(parsedUserAgent.deviceType());

        String referrer = request.getHeader("Referer");
        visitor.setReferrer(referrer);

        return visitor;
    }

    @Async
    public void updateCountryAsync(Visitor visitor) {
        if (ipAddressService.isLocalIp(visitor.getIpAddress())) {
            visitor.setCountry("Local");
            visitorRepository.save(visitor);
            return;
        }

        CompletableFuture<String> countryFuture = geoLocationService.getCountryCodeByIp(visitor.getIpAddress());
        countryFuture.thenAccept(country -> {
            visitor.setCountry(country);
            visitorRepository.save(visitor);
        });
    }

    public List<VisitorDTO> getRecentVisitorsWithoutIp(Link link, int limit) {
        List<Visitor> visitors = visitorRepository.findTop10ByLinkOrderByVisitedAtDesc(link, 10);

        return visitors.stream().map(visitor -> {
            VisitorDTO dto = new VisitorDTO();
            dto.setId(visitor.getId());
            dto.setCountry(visitor.getCountry());
            dto.setDeviceType(visitor.getDeviceType());
            dto.setOperatingSystem(visitor.getOperatingSystem());
            dto.setBrowser(visitor.getBrowser());
            dto.setReferrer(getDomainName(visitor.getReferrer()));
            dto.setVisitedAt(visitor.getVisitedAt());
            // Не устанавливаем IP!
            return dto;
        }).collect(Collectors.toList());
    }

    public Long getVisitorCountByLink(Link link) {
        return visitorRepository.countByLink(link);
    }

    public Map<String, Long> getCountryStats(Link link) {
        return visitorRepository.countByCountry(link).stream()
                .collect(Collectors.toMap(
                        CountryStat::country,
                        CountryStat::count
                ));
    }

    public Map<String, Long> getBrowserStats(Link link) {
        return visitorRepository.countByBrowser(link).stream()
                .collect(Collectors.toMap(
                        BrowserStat::browser,
                        BrowserStat::count
                ));
    }

    public Map<String, Long> getOsStats(Link link) {
        return visitorRepository.countByOs(link).stream()
                .collect(Collectors.toMap(
                        OsStat::os,
                        OsStat::count
                ));
    }

    public Map<String, Long> getDeviceStats(Link link) {
        return visitorRepository.countByDevice(link).stream()
                .collect(Collectors.toMap(
                        DeviceStat::deviceType,
                        DeviceStat::count
                ));
    }

    public Map<String, Long> getReferrerStats(Link link) {
        return visitorRepository.countByReferrer(link).stream()
                .collect(Collectors.toMap(
                        stat -> getDomainName(stat.referrer()),
                        ReferrerStat::count
                ));
    }

    private String getDomainName(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String domain = uri.getHost();

            return domain != null ? domain.startsWith("www.") ? domain.substring(4) : domain : "Direct";
        } catch (Exception e) {
            return "Direct";
        }
    }


}
