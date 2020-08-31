package org.codedream.epaper.component.task;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.auth.TimestampExpiredChecker;
import org.codedream.epaper.configure.AppConfigure;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.codedream.epaper.service.TaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 子任务监视器
 */
@Slf4j
@Component
public class TaskMonitor {

    @Resource
    private TaskQueue taskQueue;

    @Resource
    private TaskService taskService;

    @Resource
    private TimestampExpiredChecker timestampExpiredChecker;

    @Resource
    private BatchProcessingTaskRepository bptRepository;

    @Resource
    private AppConfigure configure;

    @Scheduled(cron = "0/3 * * ? * *")
    public void monitorTasks() {
        log.info("Tasks Monitor Started.");
        log.info(String.format("Tasks Number For Now: %s", taskQueue.size()));
        if (taskQueue.checkEmpty()) {
            return;
        }

        List<Task> tasks = new ArrayList<>();
        Integer sentenceNum = 0;
        Iterator<Task> iterator = taskQueue.getIterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();

            if (null == task) {
                log.error("Task null value.");
                continue;
            }
            //超时MaxTaskDelayTime秒即添加到一个批处理队列中
            if (timestampExpiredChecker.checkDateBeforeDeterminedTime(
                    task.getCreateDate(), configure.gerMaxTaskDelayTime())) {

                tasks.add(task);
                iterator.remove();
                sentenceNum += task.getArticle().getSentencesNumber();
            }
        }

        if (tasks.isEmpty()) return;

        // 对等待超时的task立即新建一个bpt，将之放入就绪队列，等待调用
        BatchProcessingTask bpt = new BatchProcessingTask();
        bpt.setTasks(tasks);
        bpt.setCreateDate(new Date());
        bpt.setPriority(sentenceNum);
        bpt.setSentencesNumber(sentenceNum);
        bpt = bptRepository.save(bpt);

        // 注册批处理任务
        taskService.registerBPTTask(bpt);
    }

}
