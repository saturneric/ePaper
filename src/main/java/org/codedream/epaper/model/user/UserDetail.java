package org.codedream.epaper.model.user;

import lombok.Data;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.model.task.Task;
import org.hibernate.annotations.CollectionType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 用户相关详细信息
 */
@Data
@Entity
@Table(name = "user_detail")
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // 上传的文件记录
    @ElementCollection
    private List<Integer> recentFileIds = new LinkedList<>();

    // 发起子任务记录
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<Task> recentTasks = new ArrayList<>();

}
