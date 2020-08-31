package org.codedream.epaper.repository.article;

import org.codedream.epaper.model.cache.Cache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CacheRepository extends JpaRepository<Cache, Integer> {

    Optional<Cache> findByHashCode(String hashCode);

    void deleteByRecordTime(LocalDateTime recordTime);

}
