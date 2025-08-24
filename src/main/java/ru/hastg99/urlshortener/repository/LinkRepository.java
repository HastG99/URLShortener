package ru.hastg99.urlshortener.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hastg99.urlshortener.model.Link;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);

    @Query("SELECT l FROM Link l WHERE l.expiresAt < :now")
    List<Link> findByExpiresAtBefore(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Link l SET l.clickCount = l.clickCount + 1 WHERE l.id = :id")
    void incrementClickCount(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Link l WHERE l.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
