package org.codedream.epaper.component.cache;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.repository.article.CacheRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@EnableScheduling
@Component
@Slf4j
public class ScheduleCacheTask {

    @Resource
    CacheRepository cacheRepository;

    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void deleteCache() {

        LocalDateTime deleteTime = LocalDateTime.now().minusDays(7);
        try {
            cacheRepository.deleteByRecordTime(deleteTime);
        } catch (Exception e) {
            log.error("Failed to delete cache.");
        }
    }

}
