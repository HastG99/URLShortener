package ru.hastg99.urlshortener.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "visitors")
@Data
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String country;
    private String deviceType;
    private String operatingSystem;
    private String browser;

    @Column(nullable = false, updatable = false)
    private Timestamp visitedAt = new Timestamp(System.currentTimeMillis());


}
