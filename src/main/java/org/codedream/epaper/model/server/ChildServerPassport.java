package org.codedream.epaper.model.server;

import lombok.Data;
import org.codedream.epaper.model.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * 分布式计算业务子服务器护照
 */
@Data
@Entity
@Table(name="csp")
public class ChildServerPassport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // 认证代码
    private String identityCode = UUID.randomUUID().toString();

    // 关联用户
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private User user = null;

    // 创建信息
    private Date createTime = new Date();

    // 是否过期
    private boolean expired = false;

    // 子服务器最后一次状态更新时间
    private Date lastUpdateTime = new Date();

    // 正在处理的批处理任务Id
    private Integer bptId = null;

}
