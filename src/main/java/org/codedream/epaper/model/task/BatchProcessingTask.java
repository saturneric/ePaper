package org.codedream.epaper.model.task;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.model.article.Sentence;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 批处理任务
 */
@Slf4j
@Data
@Entity
@Table(name="batch_processing_task")
public class BatchProcessingTask implements Comparable<BatchProcessingTask> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 任务流水号
    private String serialNumber;

    // 任务号
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    // 句集合
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<Sentence> sentences = new ArrayList<>();


    // 待处理的句的数目
    private Integer sentencesNumber = 0;

    // 加入队列时间
    private Date joinDate = new Date();

    // 创建时间
    private Date createDate = new Date();

    // 完成时间
    private Date finishDate = null;

    // 运行次数
    private Integer tryNumber = 0;

    // 是否成功
    private boolean success = false;

    // 是否完成
    private boolean finished = false;

    // 任务优先级
    private Integer priority = 1;

    @Override
    public int compareTo(BatchProcessingTask o) {
        if (this.getPriority() > o.getPriority()) {
            return 1;
        } else if (this.getPriority() < o.getPriority()) {
            return -1;
        }
        return 0;
    }

    public void setSuccess(boolean ifSuccess) {
        this.success = ifSuccess;
    }

    public void setFinished(boolean ifFinished) {
        this.finished = ifFinished;
    }

    public BatchProcessingTask(BatchProcessingTask batchProcessingTask) {
        batchProcessingTask.setCreateDate(this.createDate);
        batchProcessingTask.setFinishDate(this.finishDate);
        batchProcessingTask.setFinished(this.finished);
        batchProcessingTask.setTryNumber(this.tryNumber);
        batchProcessingTask.setSuccess(this.success);
    }

    public BatchProcessingTask() {
    }

    public void setTaskFinished() {
        for (Task task : this.tasks) {
            task.setFinished(true);
            task.setProgressRate(task.getProgressRate() + 1);
            log.info(String.format("BPT progress finished, task progress for now is: %d", task.getProgressRate()));
            task.setEndDate(new Date());
        }
    }


}
