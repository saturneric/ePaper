package org.codedream.epaper.model.record;

import io.swagger.models.auth.In;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 子任务变动记录
 */
@Data
@Entity
@Table(name = "task_record")
public class TaskRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 子任务序号
    private Integer taskId;

    // 记录生成时间
    private Date time = new Date();
    
    // 操作类型
    private String operationType;

    // 记录内容
    private String msg = "";

}
