package org.codedream.epaper.service;

import org.codedream.epaper.component.json.model.JsonableBPT;
import org.codedream.epaper.component.json.model.JsonableBPTResult;
import org.codedream.epaper.component.json.model.JsonableSTN;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.repository.article.SentenceRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 神经网络服务层
 */
@Service
public class NeuralNetworkModelService implements INeuralNetworkModelService {

    @Resource
    private ITaskService taskService;

    @Resource
    private SentenceRepository sentenceRepository;

    /**
     * 标记批处理任务成功执行
     * @param batchProcessingTask 批处理任务对象
     * @param results 批处理任务结果
     */
    @Override
    public void markBPTSuccess(BatchProcessingTask batchProcessingTask, List<JsonableBPTResult> results) {

        // 标记深处理
        for(JsonableBPTResult jsonableBPTResult : results){
            Optional<Sentence> sentence = sentenceRepository.findById(jsonableBPTResult.getStnid());
            if(!sentence.isPresent()) throw new InnerDataTransmissionException();
            sentence.get().setDeepProcess(true);
        }


        Map<Integer, JsonableBPTResult> bptResultMap = new HashMap<>();
        for(JsonableBPTResult bptResult : results){
            bptResultMap.put(bptResult.getStnid(), bptResult);
        }
        taskService.markBatchProcessingTaskFinished(batchProcessingTask.getId(), bptResultMap);
        // 解锁BPT
        taskService.unlockBPT(batchProcessingTask.getId());
    }

    /**
     * 标记批处理任务执行失败
     * @param batchProcessingTask 批处理任务对象
     */
    @Override
    public void markBPTFailed(BatchProcessingTask batchProcessingTask) {
        taskService.unlockBPT(batchProcessingTask.getId());
    }

    /**
     * 获得批处理任务并锁定
     * @param limit 计算端单次批处理上限
     * @return 批处理任务对象
     */
    @Override
    public Optional<BatchProcessingTask> getBPTTaskAndLock(Integer limit) {
         BatchProcessingTask bpt =  taskService.getBatchProcessingTask(limit);
         if(bpt == null) return Optional.empty();
         else{
             taskService.lockBPT(bpt.getId());
             return Optional.of(bpt);
         }
    }

    /**
     * 计算批处理任务原文句子列表（启用缓存筛选）
     * @param bpt 批处理任务对象
     * @return 原文句子列表
     */
    @Override
    public List<Sentence> calculateSentenceList(BatchProcessingTask bpt) {
        List<Sentence> sentenceList = new ArrayList<>();
        for(Task task : bpt.getTasks()) {
            for (Paragraph paragraph : task.getArticle().getParagraphs()) {
                for (Sentence sentence : paragraph.getSentences()) {
                    // 检查是否已经深处理完毕
                    if (sentence.isDeepProcess()) continue;
                    // 添加到列表
                    sentenceList.add(sentence);
                }
            }
        }
        return sentenceList;
    }

    /**
     * 获得可转换为JSON的批处理任务对象
     * @param bpt 批处理任务对象
     * @param sentences 任务原文句子列表
     * @return 批处理任务JSON设计对象
     */
    @Override
    public JsonableBPT getJsonableBPT(BatchProcessingTask bpt, List<Sentence> sentences) {
        JsonableBPT jsonableBPT = new JsonableBPT();
        jsonableBPT.setId(bpt.getId());
        jsonableBPT.setStnNumber(sentences.size());
        for(Sentence sentence : sentences) {
            JsonableSTN jsonableSTN = new JsonableSTN();
            jsonableSTN.setStnId(sentence.getId());
            jsonableSTN.setText(sentence.getText());
            jsonableBPT.getStns().add(jsonableSTN);
        }
        return jsonableBPT;
    }
}
