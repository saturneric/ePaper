package org.codedream.epaper;

import org.codedream.epaper.repository.task.TaskRepository;
import org.codedream.epaper.service.TaskService;
import org.hibernate.validator.constraints.pl.REGON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ResultGeneratorTest {

    @Resource
    TaskRepository taskRepository;

    @Resource
    TaskService taskService;


    @Test
    public void test() {

    }
}
