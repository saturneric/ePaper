package org.codedream.epaper.service;

import org.codedream.epaper.component.json.model.JsonableBPT;
import org.codedream.epaper.component.json.model.JsonableBPTResult;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.BatchProcessingTask;

import java.util.List;
import java.util.Optional;

public interface INeuralNetworkModelService {

    /**
     * 标记批处理任务成功执行
     * @param batchProcessingTask 批处理任务对象
     * @param results 批处理任务结果
     */
    void markBPTSuccess(BatchProcessingTask batchProcessingTask, List<JsonableBPTResult> results);

    /**
     * 标记批处理任务执行失败
     * @param batchProcessingTask 批处理任务对象
     */
    void markBPTFailed(BatchProcessingTask batchProcessingTask);

    /**
     * 获得批处理任务并锁定
     * @param limit 计算端单次批处理上限
     * @return 批处理任务对象
     */
    Optional<BatchProcessingTask> getBPTTaskAndLock(Integer limit);

    /**
     * 计算批处理任务原文句子列表（启用缓存筛选）
     * @param bpt 批处理任务对象
     * @return 原文句子列表
     */
    List<Sentence> calculateSentenceList(BatchProcessingTask bpt);

    /**
     * 获得可转换为JSON的批处理任务对象
     * @param bpt 批处理任务对象
     * @param sentences 任务原文句子列表
     * @return 批处理任务JSON设计对象
     */
    JsonableBPT getJsonableBPT(BatchProcessingTask bpt, List<Sentence> sentences);
}
