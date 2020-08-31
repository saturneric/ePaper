package org.codedream.epaper.configure;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * 批处理任务配置，用于配置线程相关参数
 */
@Configuration
@Data
public class BatchTaskConfiguration {

    //保留的线程池大小
    private static int corePoolSize = 15;

    //线程池最大大小
    private static int maxPoolSize = 30;

    //线程最大空闲时间
    private static int keepAliveTime = 1000;

    //阻塞队列大小
    private static int workQueueSize = 200;

    private static Long limit = 20000L;

    public static int getCorePoolSize() {
        return corePoolSize;
    }

    public static int getMaxPoolSize() {
        return maxPoolSize;
    }

    public static int getKeepAliveTime() {
        return keepAliveTime;
    }

    public static int getWorkQueueSize() {
        return workQueueSize;
    }

    public static Long getLimit() {
        return limit;
    }
}
