package org.codedream.epaper.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.json.model.JsonableBPT;
import org.codedream.epaper.component.json.model.JsonableBPTResult;
import org.codedream.epaper.component.json.model.JsonableCSP;
import org.codedream.epaper.component.json.model.JsonableSTN;
import org.codedream.epaper.configure.AppConfigure;
import org.codedream.epaper.exception.badrequest.AuthExpiredException;
import org.codedream.epaper.exception.badrequest.IllegalException;
import org.codedream.epaper.exception.innerservererror.HandlingErrorsException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.server.ChildServerPassport;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.codedream.epaper.service.IChildServerService;
import org.codedream.epaper.service.INeuralNetworkModelService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;


@Slf4j
@RestController
@Api("GPU服务器分布式计算接口")
@RequestMapping("cs")
public class GPUServerController {

    @Resource
    private IChildServerService childServerService;

    @Resource
    private INeuralNetworkModelService modelService;

    @Resource
    private BatchProcessingTaskRepository bptRepository;

    @Resource
    private AppConfigure configure;

    @PostMapping("")
    @ApiOperation("获得子服务器护照")
    @ResponseStatus(HttpStatus.OK)
    public JsonableCSP registerCSP(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        if(!user.getUserAuth().getRole().equals("ChildServer"))
            throw new IllegalException(authentication.getName());

        return new JsonableCSP(childServerService.createCSP(user));
    }

    @PutMapping("")
    @ApiOperation("更新子服务器签证")
    public JsonableCSP updateCSP(@RequestParam(value = "idcode") String idcode){
        if(!childServerService.checkCSP(idcode)) throw new IllegalException(idcode);

        ChildServerPassport csp = childServerService.updateCSP(idcode);

        // 签证过期
        if(csp == null) {
            JsonableCSP jsonableCSP =  new JsonableCSP();
            jsonableCSP.setExpired(true);
            jsonableCSP.setIdentityCode(idcode);
            return jsonableCSP;
        }

        return new JsonableCSP(csp);
    }

    @GetMapping("bpt")
    @ApiOperation("获得一个合适的批处理任务")
    @ResponseStatus(HttpStatus.OK)
    public JsonableBPT getBPT(@RequestParam(value = "idcode") String idcode,
                              @RequestParam(value = "maxStnNum") String maxSTNNumberStr){

        log.info(String.format("Get BPT Request From %s Max Sentence Number %s.", idcode, maxSTNNumberStr));

        if(childServerService.checkCSPExpired(idcode)){
            log.info(String.format("Found CSP %s Expired.", idcode));
            throw new AuthExpiredException(idcode);
        }

        float maxSTNNumber = Float.parseFloat(maxSTNNumberStr);
        if(configure.getBPTMinSentenceNumber() > maxSTNNumber){
            log.info(String.format("Found maxSTNNumber %f is Too Few.", maxSTNNumber));
            throw new IllegalException ("Too Few maxSTNNumber");
        }

        log.info("PreCheck Succeed.");

        // 标记签证地点
        ChildServerPassport csp = childServerService.getCSPInfo(idcode);

        // 存在被该CSP锁住的BPT
        if(csp.getBptId() != null){
            log.info(String.format("Locked BPT Found %d.", csp.getBptId()));

            Optional<BatchProcessingTask> bpt = bptRepository.findById(csp.getBptId());
            if(!bpt.isPresent()) throw new IllegalException();
            modelService.markBPTFailed(bpt.get());
            csp.setBptId(null);

            log.info(String.format("Marked Locked BPT Failed %d.", csp.getBptId()));
        }

        try {
            log.info("Trying To Get New BPT...");
            Optional<BatchProcessingTask> bpt = modelService.getBPTTaskAndLock((int) maxSTNNumber);

            if (!bpt.isPresent()) {
                log.info("Available BPT Not Found.");
                return new JsonableBPT();
            }

            log.info(String.format("New BPT Got %d.", bpt.get().getId()));
            List<Sentence> sentences = modelService.calculateSentenceList(bpt.get());

            // 所有句已处理
            if(sentences.size() == 0){
                log.info("None Sentence Must Be Processing");
                // 标记任务已完成
                modelService.markBPTSuccess(bpt.get(), new ArrayList<>());
                log.info("Marked BPT Successful.");
                log.info("Available BPT Not Found.");
                return new JsonableBPT();
            }

            log.info(String.format("Record BPT %d To CSP %s.", bpt.get().getId(), csp.getIdentityCode()));
            csp.setBptId(bpt.get().getId());
            // 更新数据库
            childServerService.update(csp);

            return modelService.getJsonableBPT(bpt.get(), sentences);

        } catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new HandlingErrorsException(e.getMessage());
        }

    }


    @PutMapping("bpt")
    @ApiOperation("更新BPT状态为已完成")
    @ResponseStatus(HttpStatus.CREATED)
    public void setBPTFinished(@RequestParam(value = "idcode") String idcode,
                                 @RequestParam(value = "bptId") Integer bptId,
                                 @RequestParam(value = "status") boolean status,
                                 @RequestBody List<JsonableBPTResult> results){

        log.info(String.format("Get BPT Result Upload Request From %s For BPT %d", idcode, bptId));

        if(childServerService.checkCSPExpired(idcode)){
            log.info(String.format("Found CSP %s Expired.", idcode));
            throw new AuthExpiredException(idcode);
        }

        ChildServerPassport csp = childServerService.getCSPInfo(idcode);
        if(csp.getBptId() == null || !csp.getBptId().equals(bptId)){
            log.info(String.format("Found CSP Status %s Illegal.", idcode));
            throw new IllegalException(bptId.toString());
        }

        log.info("PreCheck Succeed.");

        Optional<BatchProcessingTask> bpt = bptRepository.findById(bptId);

        if(!bpt.isPresent()){
            log.info(String.format("Found BPT ID %d Illegal.", bptId));
            throw new NotFoundException(bptId.toString());
        }

        if(status) {
            log.info("Child Server Process Status Successful.");
            modelService.markBPTSuccess(bpt.get(), results);
        }
        else{
            log.info("Child Server Process Status Failed.");
            modelService.markBPTFailed(bpt.get());
        }

        csp.setBptId(null);
        // 更新数据库
        childServerService.update(csp);

        log.info(String.format("BPT %d Result Processing Succeed For CSP %s", bptId, idcode));

    }




}
