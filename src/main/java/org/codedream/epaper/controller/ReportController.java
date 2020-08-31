package org.codedream.epaper.controller;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.datamanager.ReportGenerator;
import org.codedream.epaper.exception.badrequest.IllegalException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.service.ITaskService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("report")
public class ReportController {

    @Resource
    private ITaskService taskService;

    @Resource
    private ReportGenerator reportGenerator;

    @GetMapping("generate")
    Integer generateReport(Authentication authentication, @RequestParam("taskId") Integer taskId){

        User user = (User) authentication.getPrincipal();
        Optional<Task> taskOptional = taskService.getTaskInfo(taskId);
        if(!taskOptional.isPresent()) throw new NotFoundException(taskId.toString());
        if (taskOptional.get().getUser().getId() != user.getId()) throw new IllegalException(taskId.toString());

        log.info(String.format("Start Generate Report For TaskID %d ...", taskId));

        Integer fileId = reportGenerator.saveByFileService(reportGenerator.generate(taskId));
        log.info(String.format("Generate Report For TaskID %d Done.", taskId));

        return fileId;
    }
}
