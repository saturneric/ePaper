package org.codedream.epaper.model.record;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 批处理任务变动记录
 */
@Data
@Entity
@Table(name = "bpt_record")
public class BPTRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 批处理任务序号
    private Integer bptId;

    // 记录时间
    private Date time = new Date();

    // 操作类型
    private String operationType;

    // 记录内容
    private String message = "";

}
