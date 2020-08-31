package org.codedream.epaper.service;

import org.codedream.epaper.component.json.model.JsonableBPTResult;
import org.codedream.epaper.component.json.model.JsonableTaskResult;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.model.task.TaskResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 任务服务接口
 */
public interface ITaskService {

    /**
     * 注册子任务
     * @param userId 用户ID号
     * @param fileId 文件ID号
     * @param type 子任务类型
     * @return 子任务ID号
     */
    Integer registerTask(Integer userId, Integer fileId, String type);

    /**
     *  查询子任务是否结束
     * @param taskId 子任务ID号
     * @return 布尔值
     */
    boolean checkTaskFinished(Integer taskId);

    /**
     * 获得子任务的详细信息
     * @param taskId 子任务ID号
     * @return 子任务对象
     */
    Optional<Task> getTaskInfo(Integer taskId);

    /**
     * 获得子任务结果
     * @param taskId 子任务ID号
     * @return 任务结果对象
     */
    Optional<TaskResult> getTaskResult(Integer taskId);

    /**
     * 获得一个批处理任务
     * @param limit 批处理任务计算量上限
     * @return 处理任务对象
     */
    BatchProcessingTask getBatchProcessingTask(Integer limit);

    /**
     * 获取子任务用于返回前端的结果
     * @param taskId 子任务ID号
     * @return 子任务结果JSON设计对象
     */
    JsonableTaskResult getJsonableTaskResult(Integer taskId);

    /**
     * 标记一个批处理任务已完成
     * @param bptId 批处理任务ID号
     * @param bptResults 批处理任务计算结果
     */
    void markBatchProcessingTaskFinished(Integer bptId, Map<Integer, JsonableBPTResult> bptResults);

    /**
     * 获得一个新的批处理任务
     * @return 批处理任务对象
     */
    BatchProcessingTask getDefaultBPTTask();

    /**
     * 将子任务添加到批处理任务中
     * @param bpt 批处理任务对象
     */
    void registerBPTTask(BatchProcessingTask bpt);


    /**
     * 为当前bpt加锁
     * @param id 批处理任务ID号
     */
    void lockBPT(Integer id);

    /**
     * 为当前bpt解锁
     * @param id 批处理任务ID号
     */
    void unlockBPT(Integer id);


    Iterable<Task> findHistoryTaskId(Integer userId);


}
