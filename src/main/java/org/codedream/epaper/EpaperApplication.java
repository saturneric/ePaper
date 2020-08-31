package org.codedream.epaper;

import org.codedream.epaper.configure.EPApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EpaperApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(EpaperApplication.class);
        // 添加启动检查
        application.addInitializers(new EPApplicationContextInitializer());
        application.run(args);
    }

}
