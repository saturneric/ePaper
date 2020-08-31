package org.codedream.epaper;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class TaskServiceTest {
    @Test
    public void littleTest() {
        LittleTest littleTest = new LittleTest();
        for (int i = 0; i < 5; i++) {
            littleTest.getCnt().incrementAndGet();
        }
        System.out.println(littleTest.getCnt());
    }
}

@Data
class LittleTest {
    private AtomicInteger cnt = new AtomicInteger(0);
}
