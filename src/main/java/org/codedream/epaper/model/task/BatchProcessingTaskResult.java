package org.codedream.epaper.model.task;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "bpt_result")
public class BatchProcessingTaskResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BatchProcessingTask batchProcessingTask = null;

    // 创建时间
    private Date createDate = new Date();

    // 是否成功
    private boolean success = false;


}
