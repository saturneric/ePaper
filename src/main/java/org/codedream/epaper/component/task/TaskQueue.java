package org.codedream.epaper.component.task;

import lombok.Data;
import org.codedream.epaper.model.task.Task;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 任务优先队列，用于给BPT中的任务进行优先级排序
 */
@Data
@Component
public class TaskQueue {

    Comparator<Task> comparator = Comparator.comparingInt(Task::getPriority);

    private Queue<Task> tasks = new PriorityQueue<>(comparator);

    private Integer sentenceNumber = 0;

    public boolean addTask(Task task) {
        if (this.tasks.offer(task)) {
            sentenceNumber += task.getArticle().getSentencesNumber();
            return true;
        } else return false;
    }

    public Integer size(){
        return tasks.size();
    }

    public void removeTask(Task task){
        tasks.remove(task);
    }

    public Iterator<Task> getIterator(){
        return tasks.iterator();
    }

    public List<Task> getTaskAsList() {
        List<Task> taskList = new ArrayList<>(this.tasks);
        return taskList;
    }

    public void clear() {
        this.tasks.clear();
        this.setSentenceNumber(0);
    }

    public boolean checkEmpty() {
        return this.tasks.isEmpty();
    }

}
