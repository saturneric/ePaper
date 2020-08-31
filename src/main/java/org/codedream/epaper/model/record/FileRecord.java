package org.codedream.epaper.model.record;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 文件变动记录
 */
@Data
@Entity
@Table(name = "file_record")
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer fileId;

    // 记录生成时间
    private Date time = new Date();

    // 操作类型
    private String operationType;

    // 记录内容
    private String message = "";

}
