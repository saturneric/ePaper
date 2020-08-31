package org.codedream.epaper.component.batchthread;

import org.codedream.epaper.model.task.BatchProcessingTask;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ComparableFutureTask extends FutureTask implements Comparable<ComparableFutureTask> {

    private BatchProcessingTask taskToAppoint = new BatchProcessingTask();

    public ComparableFutureTask(Callable callable) {
        super(callable);
    }

    @Override
    public int compareTo(ComparableFutureTask o) {

        if (this.getTaskToAppoint().getPriority() > o.getTaskToAppoint().getPriority()) {
            return 1;
        } else if (this.getTaskToAppoint().getPriority() < o.getTaskToAppoint().getPriority()) {
            return -1;
        }
        return 0;
    }

    public BatchProcessingTask getTaskToAppoint() {
        return taskToAppoint;
    }

}
