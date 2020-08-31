package org.codedream.epaper.repository.task;

import org.codedream.epaper.model.task.BatchProcessingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

@Repository
public interface BatchProcessingTaskRepository extends JpaRepository<BatchProcessingTask, Integer> {
    Iterable<BatchProcessingTask> findAllByFinished(boolean finished);
}
