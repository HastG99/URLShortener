package ru.hastg99.urlshortener.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitorDTO {
    private Long id;
    private String country;
    private String deviceType;
    private String operatingSystem;
    private String browser;
    private String referrer;
    private LocalDateTime visitedAt;
}
