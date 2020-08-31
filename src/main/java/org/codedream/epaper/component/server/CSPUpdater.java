package org.codedream.epaper.component.server;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.auth.TimestampExpiredChecker;
import org.codedream.epaper.configure.AppConfigure;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.server.ChildServerPassport;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.repository.server.ChildServerPassportRepository;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.codedream.epaper.service.INeuralNetworkModelService;
import org.codedream.epaper.service.ITaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 子服务器护照管理器
 */
@Slf4j
@Component
public class CSPUpdater {

    @Resource
    private ChildServerPassportRepository cspRepository;

    @Resource
    private BatchProcessingTaskRepository bptRepository;

    @Resource
    private INeuralNetworkModelService neuralNetworkModelService;

    @Resource
    private TimestampExpiredChecker expiredChecker;

    @Resource
    private AppConfigure configure;

    /**
     * 检查护照状态
     */
    @Scheduled(cron = "0/60 * * * * ?")
    public void update(){
        log.info("CSP Updater Started");
        Iterable<ChildServerPassport> childServerPassports = cspRepository.findByExpired(false);
        for(ChildServerPassport csp : childServerPassports){
            if(expiredChecker
                    .checkDateBeforeDeterminedTime(csp.getLastUpdateTime(), configure.gerChildServerRegisterTimeout())){
                if(csp.getBptId() != null){

                    // 释放其占用的批处理任务
                    Optional<BatchProcessingTask> bpt = bptRepository.findById(csp.getBptId());
                    if(!bpt.isPresent()) throw new InnerDataTransmissionException();
                    neuralNetworkModelService.markBPTFailed(bpt.get());

                   log.info(String.format("Unlock BPT: bptId %s", csp.getBptId()));
                   csp.setBptId(null);
                }
                csp.setExpired(true);
                csp = cspRepository.save(csp);
                log.info(String.format("CSP Expired: idcode %s", csp.getIdentityCode()));

            }

        }
    }
}
