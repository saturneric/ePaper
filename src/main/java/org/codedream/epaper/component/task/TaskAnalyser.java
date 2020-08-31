package org.codedream.epaper.component.task;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.article.GetSentenceFromArticle;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.SentenceResult;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.model.task.TaskResult;
import org.codedream.epaper.repository.task.SentenceResultRepository;
import org.codedream.epaper.repository.task.TaskRepository;
import org.codedream.epaper.repository.task.TaskResultRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * 子任务预分析器，用于精确到句子的任务分析以及持久化子任务结果
 *
 * @see SentenceAnalyser
 */
@Slf4j
@Component
public class TaskAnalyser {

    @Resource
    private TaskResultRepository taskResultRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private GetSentenceFromArticle getSentenceFromArticle;

    @Resource
    private SentenceAnalyser sentenceAnalyser;

    @Resource
    private SentenceResultRepository sentenceResultRepository;

    /**
     * 分析一个task，并持久化其结果
     * 持久化结果的时候，会先查找缓存
     *
     * @param taskId 任务id
     * @return 一个TaskResult对象
     */
    public TaskResult analyse(Integer taskId) {

        log.info(String.format("Analysing task: %d", taskId));

        // 查找子任务
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) throw new InnerDataTransmissionException(taskId.toString());

        Task task = taskOptional.get();

        // 构建预处理的任务结果存储结构
        TaskResult taskResult = new TaskResult();
        taskResult.setTaskId(task.getId());

        Map<Integer, Float> dnnRes = new HashMap<>();
        Map<Integer, SentenceResult> sentenceMap = new HashMap<>();
        // 句分析
        for(Sentence sentence : getSentenceFromArticle.get(task.getArticle())){
            SentenceResult sentenceResult;
            log.info(String.format("Analysing sentence: %d", sentence.getId()));
            // 查找缓存
            Optional<SentenceResult> sentenceResultOptional =
                    sentenceResultRepository.findBySentenceId(sentence.getId());

            if (sentenceResultOptional.isPresent()) {
                sentenceResult = sentenceResultOptional.get();

                dnnRes.put(sentence.getId(), sentenceResult.getDnn());
                sentenceMap.put(sentence.getId(), sentenceResult);
                log.info(String.format("DNN result of sentence %d: %f", sentence.getId(), sentenceResult.getDnn()));
                log.info(String.format("Correction result of sentence %d: %s", sentence.getId(),
                        sentenceResult.getCorrectionResults().toString()));
                continue;
            } else {
                sentenceResult = sentenceAnalyser.analyse(sentence);
            }
            log.info(String.format("DNN result of sentence %d: %f", sentence.getId(), sentenceResult.getDnn()));
            log.info(String.format("Correction result of sentence %d: %s", sentence.getId(),
                    sentenceResult.getCorrectionResults().toString()));
            dnnRes.put(sentence.getId(), sentenceResult.getDnn());
            sentenceMap.put(sentence.getId(), sentenceResult);
        }

        taskResult.setDnnMap(dnnRes);
        taskResult.setSentenceResultMap(sentenceMap);

        taskResult = taskResultRepository.save(taskResult);


        // 更新任务
        taskOptional.get().setProgressRate(taskOptional.get().getProgressRate()+1);
        task.setResult(taskResult);
        taskRepository.save(task);
        log.info(String.format("Analyse preprocess finished, task progress for now is: %d", task.getProgressRate()));

        log.info(String.format("Analysing result of task %d: %s", taskId, task.getResult()));
        return taskResult;
    }
}
