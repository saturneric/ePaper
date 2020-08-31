package org.codedream.epaper.component.batchthread;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.model.task.BatchProcessingTask;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.concurrent.Callable;

@Data
@Slf4j
public class TaskAppointer implements Callable<BatchProcessingTask>, Comparable<TaskAppointer> {

    Comparator<BatchProcessingTask> comparator = new Comparator<BatchProcessingTask>() {
        @Override
        public int compare(BatchProcessingTask o1, BatchProcessingTask o2) {
            return o1.getPriority() - o2.getPriority() < 0 ? -1 : 1;
        }
    };
    @Resource
    private BatchProcessingTask taskToAppoint = new BatchProcessingTask();

    public TaskAppointer(BatchProcessingTask bpt) {
        this.taskToAppoint = bpt;
    }


    @Override
    public int compareTo(TaskAppointer o) {

        if (this.getTaskToAppoint().getPriority() > o.getTaskToAppoint().getPriority()) {
            return 1;
        } else if (this.getTaskToAppoint().getPriority() < o.getTaskToAppoint().getPriority()) {
            return -1;
        }
        return 0;
    }

    @Override
    public BatchProcessingTask call() throws Exception {
        log.info("Thead " + Thread.currentThread().getName() + "bpt" + this.getTaskToAppoint().getId());

        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Thread " + Thread.currentThread().getName() + " executing");
        return getTaskToAppoint();
    }
}
