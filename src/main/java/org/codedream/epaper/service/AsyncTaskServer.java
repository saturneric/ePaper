package org.codedream.epaper.service;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.task.*;
import org.codedream.epaper.configure.BatchTaskConfiguration;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.codedream.epaper.repository.task.TaskRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * 此类提供任务异步调取相关的服务，用以支持分布式多线程的任务批处理机制，以及处理结果的持久化（参见{@link TaskAnalyser}）
 * 包括对文章（参见{@link ArticlePreprocessor}）、段落（参见{@link ParagraphProcessor}）
 * 以及句子（参见{@link SentencePreprocessor}）的异步预处理和预分析
 */
@Slf4j
@Service
public class AsyncTaskServer implements IAsyncTaskServer {

    @Resource
    private ArticlePreprocessor articlePreprocessor;

    @Resource
    private ParagraphProcessor paragraphProcessor;

    @Resource
    private SentencePreprocessor sentencePreprocessor;

    @Resource
    private TaskAnalyser taskAnalyser;

    @Resource
    private TaskQueue taskQueue;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private TaskService taskService;

    @Resource
    private BatchProcessingTaskRepository bptRepository;

    private BatchProcessingTask newlyCreatedBpt = new BatchProcessingTask();

    /**
     * 子任务预处理以及分析，用于将子任务进行分词分句分段处理，并初步得到文本纠错结果、DNN处理结果
     *
     * @param taskId 任务id
     */
    @Override
    @Async("PaPoolExecutor")
    public void preprocessorAndAnalyse(Integer taskId) {

        // 子任务预处理
        preprocess(taskId);

        // 设定子任务优先级
        Optional<Task> oTask = taskRepository.findById(taskId);
        if (!oTask.isPresent()) throw new InnerDataTransmissionException(taskId.toString());
        Task pTask = oTask.get();
        pTask.setPriority(pTask.getArticle().getSentencesNumber());
        taskRepository.save(pTask);

        // 子任务预分析
        analyse(taskId);
        log.info(String.format("Task analysed: %s", taskId));

        // 查找子任务
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) throw new InnerDataTransmissionException(taskId.toString());

        Task task = taskOptional.get();

        if (taskQueue.getSentenceNumber() + task.getArticle().getSentencesNumber() > BatchTaskConfiguration.getLimit()) {
            newlyCreatedBpt.setTasks(taskQueue.getTaskAsList());
            newlyCreatedBpt.setCreateDate(new Date());
            newlyCreatedBpt.setPriority(taskQueue.getSentenceNumber());
            newlyCreatedBpt.setSentencesNumber(taskQueue.getSentenceNumber());
            newlyCreatedBpt = bptRepository.save(newlyCreatedBpt);
            taskService.registerBPTTask(newlyCreatedBpt);
            newlyCreatedBpt = new BatchProcessingTask();
            taskQueue.clear();
        }

        // 否则继续加入任务等待队列
        task.setJoinDate(new Date());
        task = taskRepository.save(task);
        taskQueue.addTask(task);
    }

    /**
     * 子任务预分析，用于获取并持久化任务初步处理结果
     *
     * @param taskId 任务id
     * @see {@link TaskAnalyser#analyse(Integer)}
     */
    private void analyse(Integer taskId) {
        // 子任务预分析
        taskAnalyser.analyse(taskId);
    }

    /**
     * @param taskId 子任务预处理，用于完成并持久化文章的分段{@link ArticlePreprocessor#parse(Integer)}、
     *               分句{@link ParagraphProcessor#parse(Integer)}、分词{@link SentencePreprocessor#parse(Integer)}结果
     */
    private void preprocess(Integer taskId) {

        // 章预处理
        articlePreprocessor.parse(taskId);

        // 段预处理
        paragraphProcessor.parse(taskId);

        // 句预处理
        sentencePreprocessor.parse(taskId);

    }


}
