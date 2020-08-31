package org.codedream.epaper.model.record;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_record")
public class UserRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 用户Id
    private Integer userId;

    // 记录创建时间
    private Date time = new Date();

    // 操作类型
    private String operationType;

    // 记录内容
    private String msg = "";
}
