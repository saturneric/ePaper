package org.codedream.epaper.repository.task;

import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.model.task.TaskResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskResultRepository extends JpaRepository<TaskResult, Integer> {
    Optional<TaskResult> findByTaskId(Integer taskId);
}
