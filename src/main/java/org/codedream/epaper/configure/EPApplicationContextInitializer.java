package org.codedream.epaper.configure;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.task.BPTQueue;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 服务端程序初始化检查
 */
@Slf4j
public class EPApplicationContextInitializer implements ApplicationContextInitializer {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        log.info("EPApplicationContextInitializer Started");

    }
}
