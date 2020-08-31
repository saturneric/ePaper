package org.codedream.epaper.model.task;

import io.swagger.models.auth.In;
import lombok.Data;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.repository.task.TaskRepository;

import javax.persistence.*;
import javax.xml.transform.Result;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 子任务
 */
@Data
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Date createDate = new Date();

    // 处理时间
    private Date dealingDate;

    // 加入队列时间
    private Date joinDate = new Date();

    // 结束时间
    private Date endDate;

    private Integer priority;

    // 关联文件
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private File file = null;

    // 关联文章
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Article article = null;

    // 关联用户
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private User user = null;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private TaskResult result;

    // 异常结束
    private boolean failed = false;

    // 是否结束
    private boolean finished = false;

    // 已加入批处理任务
    private boolean scheduled = false;

    // 任务类型
    private String type;

    // 处理进度标识，五位分别表示：章预处理、段预处理、句预处理、预分析、BERT计算
    private Integer progressRate = 0;
}
