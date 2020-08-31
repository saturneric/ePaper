package org.codedream.epaper.component.task;

import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 批处理任务优先级队列
 * <p>
 * 根据批处理任务待处理的句子数量设置优先级
 * （参见{@link org.codedream.epaper.model.task.BatchProcessingTask#compareTo(BatchProcessingTask)}）
 */
@Component
public class BPTQueue {

    @Resource
    private BatchProcessingTaskRepository bptRepository;

    // 优先阻塞队列
    private PriorityBlockingQueue<Integer> batchProcessingTasks = new PriorityBlockingQueue<>(1,
            new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    BatchProcessingTask bpt1 = bptRepository.findById(o1).get();
                    BatchProcessingTask bpt2 = bptRepository.findById(o2).get();
                    return bpt1.getPriority() - bpt2.getPriority();
                }
            });

    // 添加批处理任务
    public void addBPT(Integer bptId) {
        batchProcessingTasks.offer(bptId);
    }

    //移除批处理任务
    public void removeBPT(Integer bptId) {
        batchProcessingTasks.remove(bptId);
    }

    // 检查队列是否为空
    public boolean checkEmpty(){
        return batchProcessingTasks.isEmpty();
    }

    // 从队列中获得一个批处理任务
    public Integer getBptId() throws InterruptedException {
        return batchProcessingTasks.take();
    }

    public Integer size(){
        return batchProcessingTasks.size();
    }


}
