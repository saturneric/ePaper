package org.codedream.epaper.repository.task;

import org.codedream.epaper.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Iterable<Task> findByFinished(boolean finish);

    Iterable<Task> findAllByUserId(Integer userId);
}
