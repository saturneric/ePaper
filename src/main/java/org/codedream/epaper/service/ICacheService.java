package org.codedream.epaper.service;

import org.codedream.epaper.model.cache.Cache;

import java.util.Optional;

/**
 * 缓存服务接口
 */
public interface ICacheService {

    /**
     * 根据句子的hash值查询cache
     * @param hashCode 缓存代码
     * @return 缓存对象
     */
    Optional<Cache> searchCache(String hashCode);

    /**
     * 创建cache
     * @param text 文本
     * @return 缓存对象
     */
    Cache create(String text);

    /**
     * 清空cache
     */
    void evictCache();

    /**
     * 对传入文本进行hash编码
     * @param text 文本
     * @return 密文
     */
    String encode(String text);
}
