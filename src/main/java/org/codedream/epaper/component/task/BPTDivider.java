package org.codedream.epaper.component.task;

import org.codedream.epaper.component.article.GetSentenceFromArticle;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 把一个超过limit的BPT按照task数量等分
 */
@Component
public class BPTDivider {

    @Resource
    private GetSentenceFromArticle getSentenceFromArticle;

    /**
     * 将一个批处理任务等分
     * <p>
     * 此方法会将原有的已经持久化的批处理任务从数据库中删除，并换成两个
     * 已经等分了的批处理任务
     *
     * @param bpt 一个批处理任务
     * @return 两个封装在列表中的批处理任务
     */
    public List<BatchProcessingTask> divideBPT(BatchProcessingTask bpt) {

        List<BatchProcessingTask> batchProcessingTasks = new ArrayList<>();
        BatchProcessingTask bpt1 = new BatchProcessingTask(bpt);
        BatchProcessingTask bpt2 = new BatchProcessingTask(bpt);
        List<Task> tasks = bpt.getTasks();
        List<Sentence> sentenceList = new ArrayList<>();
        bpt1.setTasks(tasks.subList(0, tasks.size() / 2));
        bpt2.setTasks(tasks.subList(tasks.size() / 2, tasks.size()));
        Integer sentenceNum = 0;

        divide(batchProcessingTasks, bpt1, sentenceList, sentenceNum);
        sentenceList.clear();
        sentenceNum = 0;

        divide(batchProcessingTasks, bpt2, sentenceList, sentenceNum);
        return batchProcessingTasks;
    }

    private void divide(List<BatchProcessingTask> batchProcessingTasks, BatchProcessingTask bpt, List<Sentence> sentenceList, Integer sentenceNum) {
        for (int i = 0; i < bpt.getTasks().size(); i++) {
            Task task = bpt.getTasks().get(i);
            sentenceList.addAll(getSentenceFromArticle.get(task.getArticle()));
            sentenceNum += task.getArticle().getSentencesNumber();
        }
        bpt.setSentences(sentenceList);
        bpt.setSentencesNumber(sentenceNum);
        bpt.setPriority(sentenceNum);
        bpt.setCreateDate(new Date());
        batchProcessingTasks.add(bpt);
    }
}
