package org.codedream.epaper.component.task;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.auth.TimestampExpiredChecker;
import org.codedream.epaper.configure.AppConfigure;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Optional;

/**
 * 用于提供对批处理任务的监控方法。
 * <p>
 * 批处理任务在处理时可能会出现分配的GPU服务端突发故障、网络通信故障等问题，从而造成
 * 当前批处理任务在结果等待队列中的无限制等待。为了避免这种问题的发生，我们设计了一个
 * 批处理任务监听器用于监控其计算等待时间，超时后会放入原就绪队列进行重新分配
 */
@Slf4j
@Component
public class BPTMonitor {

    @Resource
    private LockedBPTs lockedBPTs;

    @Resource
    private BPTQueue bptQueue;

    @Resource
    private BatchProcessingTaskRepository bptRepository;

    @Resource
    private TimestampExpiredChecker timestampExpiredChecker;

    @Resource
    private AppConfigure configure;


    private boolean initStatus = true;

    /**
     * 一个定时任务，每15秒检测一次批处理任务的等待情况
     */
    @Scheduled(cron = "0/5 * * ? * *")
    public void monitorBPTs() {
        // 启动自检
        if (initStatus) {
            // 查找未完成的批处理任务
            Iterable<BatchProcessingTask> batchProcessingTasks = bptRepository.findAllByFinished(false);

            for (BatchProcessingTask bpt : batchProcessingTasks) {
                bptQueue.addBPT(bpt.getId());
            }
            initStatus = false;
        }

        log.info("BPT Monitor Started");
        log.info(String.format("Lined BPTs Number For Now: %s", bptQueue.size()));
        log.info(String.format("Locked BPTs Number For Now: %s", lockedBPTs.size()));
        if (lockedBPTs.isEmpty()) {
            return;
        }
        Iterator<Integer> bptIterator = lockedBPTs.iterator();
        while (bptIterator.hasNext()) {
            Integer bptId = bptIterator.next();
            Optional<BatchProcessingTask> oBpt = bptRepository.findById(bptId);
            if (!oBpt.isPresent()) throw new InnerDataTransmissionException();

            BatchProcessingTask bpt = oBpt.get();
            if (timestampExpiredChecker.checkDateBeforeDeterminedTime(
                    bpt.getJoinDate(), configure.gerMaxBPTProcessDelayTime())) {

                bptQueue.addBPT(bpt.getId());
                bptIterator.remove();
            }
        }
    }

}
