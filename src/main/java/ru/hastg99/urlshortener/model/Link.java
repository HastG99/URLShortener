package ru.hastg99.urlshortener.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "links")
@Data
public class Link {

    public Link() {
    }

    public Link(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 10)
    private String shortCode;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Visitor> visitors = new ArrayList<>();

    private Long clickCount = 0L;

    public void addVisitor(Visitor visitor) {
        visitors.add(visitor);
        visitor.setLink(this);
    }
}
