package ru.hastg99.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hastg99.urlshortener.model.Link;
import ru.hastg99.urlshortener.model.Visitor;
import ru.hastg99.urlshortener.model.dto.*;

import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByLink(Link link);
    Long countByLink(Link link);

    @Query("SELECT v FROM Visitor v WHERE v.link = :link ORDER BY v.visitedAt DESC LIMIT :limit")
    List<Visitor> findTop10ByLinkOrderByVisitedAtDesc(@Param("link") Link link, @Param("limit") int limit);

    @Query("SELECT new ru.hastg99.urlshortener.model.dto.CountryStat(v.country, COUNT(v)) " +
            "FROM Visitor v WHERE v.link = :link AND v.country IS NOT NULL GROUP BY v.country")
    List<CountryStat> countByCountry(@Param("link") Link link);

    @Query("SELECT new ru.hastg99.urlshortener.model.dto.BrowserStat(v.browser, COUNT(v)) " +
            "FROM Visitor v WHERE v.link = :link AND v.browser IS NOT NULL GROUP BY v.browser")
    List<BrowserStat> countByBrowser(@Param("link") Link link);

    @Query("SELECT new ru.hastg99.urlshortener.model.dto.OsStat(v.operatingSystem, COUNT(v)) " +
            "FROM Visitor v WHERE v.link = :link AND v.operatingSystem IS NOT NULL GROUP BY v.operatingSystem")
    List<OsStat> countByOs(@Param("link") Link link);

    @Query("SELECT new ru.hastg99.urlshortener.model.dto.DeviceStat(v.deviceType, COUNT(v)) " +
            "FROM Visitor v WHERE v.link = :link AND v.deviceType IS NOT NULL GROUP BY v.deviceType")
    List<DeviceStat> countByDevice(@Param("link") Link link);

    @Query("SELECT new ru.hastg99.urlshortener.model.dto.ReferrerStat(v.referrer, COUNT(v)) " +
            "FROM Visitor v WHERE v.link = :link AND v.referrer IS NOT NULL GROUP BY v.referrer")
    List<ReferrerStat> countByReferrer(@Param("link") Link link);
}