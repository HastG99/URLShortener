package ru.hastg99.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.model.Visitor;

import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByLink(Link link);
    Long countByLink(Link link);
    List<Visitor> findByLinkOrderByVisitedAtDesc(Link link);
}