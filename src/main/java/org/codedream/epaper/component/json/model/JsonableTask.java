package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codedream.epaper.model.task.Task;

@Data
@ApiModel("子任务结果")
@NoArgsConstructor
public class JsonableTask {

    // 任务ID号
    private Integer taskId;

    // 用户openid
    private String openid;

    // 文件ID号
    private Integer fileId;

    // 文本
    private String text;

    // 任务是否完成
    private boolean finished;

    // 任务进度
    private Float progress;

    // 描述
    private String description;

    public JsonableTask(Task task){
        this.taskId = task.getId();
        this.openid = task.getUser().getUsername();
        this.fileId = task.getFile().getId();
        this.finished = task.isFinished();
        this.progress = task.getProgressRate() / 5.0f;
    }

}
