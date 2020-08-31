package org.codedream.epaper.service;

import org.codedream.epaper.component.datamanager.SHA256Encoder;
import org.codedream.epaper.component.datamanager.SHA512Encoder;
import org.codedream.epaper.model.cache.Cache;
import org.codedream.epaper.repository.article.CacheRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 文本缓存服务。
 * <p>
 * 用于通过SHA256码寻找当前要持久化的文本（{@link org.codedream.epaper.model.article.Article}、
 * {@link org.codedream.epaper.model.article.Paragraph}）
 */
@Service
public class CacheService implements ICacheService {

    @Resource
    CacheRepository cacheRepository;

    @Resource
    SHA512Encoder sha512Encoder;

    /**
     * 在缓存中搜索是否存在相同文本。
     *
     * @param hashCode 文本散列值
     * @return 被Optional封装的Cache对象
     */
    @Override
    public Optional<Cache> searchCache(String hashCode) {

        return cacheRepository.findByHashCode(hashCode);
    }

    /**
     * 在数据库中创建缓存
     *
     * @param text 待持久化的文本
     * @return 已经持久化的缓存对象
     */
    @Override
    public Cache create(String text) {

        Cache cache = new Cache(text);
        cache.setRecordTime(LocalDateTime.now());
        cache.setHashCode(this.encode(text));
        return cacheRepository.save(cache);
    }

    /**
     * 清空缓存。
     * <p>
     * 用于定时清理
     */
    @Override
    public void evictCache() {
        cacheRepository.deleteAll();
    }

    /**
     * 文本编码（参见{@link SHA256Encoder}）
     *
     * @param text 待编码的文本
     * @return 文本编码
     */
    @Override
    public String encode(String text) {
        return sha512Encoder.encode(text);
    }
}
