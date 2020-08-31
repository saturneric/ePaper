package org.codedream.epaper.component.task;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 用于提供{@link org.codedream.epaper.model.task.BatchProcessingTask}的计算等待队列以及相关操作
 */
@Component
public class LockedBPTs {
    private List<Integer> bptIdList = new ArrayList<>();

    public void add(Integer bptId){
        bptIdList.add(bptId);
    }

    public Iterator<Integer> iterator(){
        return bptIdList.iterator();
    }

    public boolean isEmpty(){
        return bptIdList.isEmpty();
    }

    public boolean contains(Integer integer){
        return bptIdList.contains(integer);
    }

    public Integer size(){
        return bptIdList.size();
    }
}
