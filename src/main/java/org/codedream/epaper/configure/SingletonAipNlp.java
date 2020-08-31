package org.codedream.epaper.configure;

import com.baidu.aip.nlp.AipNlp;
import org.codedream.epaper.configure.NLPConfigure;
import org.springframework.stereotype.Component;

/**
 * 百度{@link AipNlp}的接口单例设计，提供线程安全的单例接口调用
 */
@Component
public class SingletonAipNlp {
    private static AipNlp aipNlp;

    public SingletonAipNlp() {
    }

    /**
     * 调用一个单例{@link AipNlp}
     *
     * @return 一个单例的AipNlp对象
     */
    public static AipNlp getInstance() {
        if (null == aipNlp) {
            synchronized (AipNlp.class) {
                if (null == aipNlp) {
                    aipNlp = new AipNlp(NLPConfigure.getAPPId(), NLPConfigure.getAppKey(), NLPConfigure.getSecretKey());
                }
            }
        }
        return aipNlp;
    }
}
