package org.codedream.epaper.service;

/**
 * 异步任务处理服务层
 */
public interface IAsyncTaskServer {
    
    /**
     * 执行预处理与预分析任务
     * @param taskId 子任务ID号
     */
    void preprocessorAndAnalyse(Integer taskId);
}
