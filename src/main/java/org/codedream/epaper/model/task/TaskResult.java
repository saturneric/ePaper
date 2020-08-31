package org.codedream.epaper.model.task;

import lombok.Data;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务结果
 */
@Data
@Entity
@Table(name = "task_result")
public class TaskResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 结果类型
    private String type;

    // 对应任务id
    private Integer taskId;

    // 文章错误位置数量
    private AtomicInteger wrongTextCount = new AtomicInteger(0);

    // 不通顺的句子数量
    private AtomicInteger brokenSentencesCount = new AtomicInteger(0);

    // 消极情感倾向严重的句子数量
    private AtomicInteger negativeEmotionsCount = new AtomicInteger(0);

    // 积极情感倾向严重的句子数量
    private AtomicInteger positiveEmotionsCount = new AtomicInteger(0);

    @ElementCollection
    private List<Integer> sentenceIds = new ArrayList<>();

    // 句子通顺度
    @ElementCollection
    private Map<Integer, Float> dnnMap = new HashMap<>();

    // 句处理结果查找表
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Map<Integer, SentenceResult> sentenceResultMap = new HashMap<>();


    // 生成日期
    private Date createDate = new Date();

    // 处理成功
    private boolean success = false;
}
