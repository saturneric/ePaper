package org.codedream.epaper.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.codedream.epaper.component.article.GetSentenceFromArticle;
import org.codedream.epaper.component.datamanager.DiffMatchPatch;
import org.codedream.epaper.component.json.model.JsonableSTN;
import org.codedream.epaper.component.json.model.JsonableSTNPage;
import org.codedream.epaper.component.json.model.JsonableTask;
import org.codedream.epaper.component.json.model.JsonableTaskResult;
import org.codedream.epaper.configure.AppConfigure;
import org.codedream.epaper.exception.badrequest.IllegalException;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.model.task.TaskResult;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.repository.task.TaskResultRepository;
import org.codedream.epaper.service.IFileService;
import org.codedream.epaper.service.ITaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@Api("任务类接口")
@RequestMapping("task")
public class TaskController {

    @Resource
    private ITaskService taskService;

    @Resource
    private IFileService fileService;

    @Resource
    private GetSentenceFromArticle getSentenceFromArticle;

    @Resource
    private AppConfigure configure;

    @Resource
    private TaskResultRepository taskResultRepository;

    @PostMapping("")
    @ApiOperation("创建任务")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonableTask createTask(@RequestBody JsonableTask jsonableTask, Authentication authentication) {
        // 获得当前认证用户的身份
        User user = (User) authentication.getPrincipal();

        // 文件序号检查
        File file = fileService.getFileInfo(jsonableTask.getFileId());
        if(file == null) throw new NotFoundException(jsonableTask.getFileId().toString());

        // 注册子任务
        Integer taskId = taskService.registerTask(user.getId(), file.getId(), "normal");
        jsonableTask.setTaskId(taskId);

        return jsonableTask;
    }

    @GetMapping("history")
    @ApiOperation("查询用户历史记录")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> taskHistory(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Iterable<Task> tasks = taskService.findHistoryTaskId(user.getId());
        List<Integer> taskIds = new ArrayList<>();

        for(Task task : tasks){
            taskIds.add(task.getId());
        }

        return taskIds;

    }

    @GetMapping("")
    @ApiOperation("查询任务状态接口")
    @ResponseStatus(HttpStatus.OK)
    public JsonableTask checkTask(@RequestParam(value = "taskId") Integer taskId){
        Optional<Task> task = taskService.getTaskInfo(taskId);
        if(!task.isPresent()) throw new NotFoundException(taskId.toString());

        JsonableTask jsonableTask = new JsonableTask(task.get());

        if (task.get().isFinished()) {
            Article article = task.get().getArticle();
            Iterator<Paragraph> paragraphIterator = article.getParagraphs().iterator();
            StringBuilder builder = new StringBuilder();
            while (builder.length() < 30 && paragraphIterator.hasNext()) {
                builder.append(paragraphIterator.next().getText());
            }
            String str = builder.toString().trim();

            if(str.length() > 30) str = str.substring(0, 30);
            jsonableTask.setDescription(str);
        }

        return jsonableTask;
    }

    @GetMapping("result")
    @ApiOperation("获得任务结果")
    @ResponseStatus(HttpStatus.OK)
    public JsonableTaskResult getTaskResult(@RequestParam(value = "taskId") Integer taskId){
        // 验证子任务Id
        Optional<Task> task = taskService.getTaskInfo(taskId);
        if (!task.isPresent()) throw new NotFoundException(taskId.toString());
        if (!task.get().isFinished())
            throw new NotFoundException(String.format("Task Not Finished : Task Id %d", taskId));


        Optional<TaskResult> taskResult = taskService.getTaskResult(taskId);
        if(!taskResult.isPresent()) throw new NotFoundException(String.format("Result Not Found : Task Id %d",taskId));

        return taskService.getJsonableTaskResult(taskId);
    }

    @GetMapping("stnlist")
    @ApiOperation("获取句子原文接口")
    @ResponseStatus(HttpStatus.OK)
    public JsonableSTNPage getSTNList(@RequestParam(value = "taskId") Integer taskId,
                                        @RequestParam(value = "page") Integer page){
        // 验证子任务Id
        Optional<Task> task = taskService.getTaskInfo(taskId);
        if (!task.isPresent()) throw new NotFoundException(taskId.toString());
        if (!task.get().isFinished())
            throw new NotFoundException(String.format("Task Not Finished : Task Id %d", taskId));
        if (page == null) page = 1;
        if(page < 1) throw new IllegalException();


       Optional<TaskResult> taskResult = taskService.getTaskResult(taskId);
       if(!taskResult.isPresent()) throw new InnerDataTransmissionException();

       if(!taskResult.get().isSuccess()){
           throw new IllegalException("Task Failed");
       }

       List<Sentence> sentences = getSentenceFromArticle.get(task.get().getArticle());


       int stnPrePage = configure.getSentencePrePage();

       // 求所有分页数
       int pageAll = new Double(Math.ceil(sentences.size() / 10.0)).intValue();
       if(page  > pageAll) page = pageAll;


       // 按照ID 排序
       sentences.sort(Comparator.comparing(Sentence::getId));

       List<Sentence> sentencePage = new ArrayList<>();

       // 获取分页数据
       for(int i = (page-1) * stnPrePage ; i < page * stnPrePage && i < sentences.size(); i++){
           sentencePage.add(sentences.get(i));
       }

       List<JsonableSTN> jsonableSTNList = new ArrayList<>();

       for(Sentence sentence : sentencePage){
           JsonableSTN jsonableSTN = new JsonableSTN();
           jsonableSTN.setStnId(sentence.getId());
           jsonableSTN.setText(sentence.getText());
           jsonableSTNList.add(jsonableSTN);
       }

        JsonableSTNPage stnPage = new JsonableSTNPage();
        stnPage.setAll(pageAll);
        stnPage.setPage(page);
        stnPage.setStns(jsonableSTNList);

        return stnPage;
    }

    @GetMapping("test")
    @ResponseStatus(HttpStatus.OK)
    public String test() {
        DiffMatchPatch dmp = new DiffMatchPatch();
        String text1 = "我们预备了缓存数据库功能，将论文种常见的行文错误进行特征编码后缓存。";
        String text2 = "我们预备了缓存数据库功能，将论文中常见的行文错误进行特征编码后缓存。";
        LinkedList<DiffMatchPatch.Diff> diff = dmp.diff_main(text1, text2);
        List<Integer> show = new ArrayList<>();
        System.out.println(diff.toString());
        for (DiffMatchPatch.Diff diff1 : diff) {
            DiffMatchPatch.Operation operation = diff1.operation;
            System.out.println(operation.compareTo(DiffMatchPatch.Operation.INSERT));
            show.add(operation.compareTo(DiffMatchPatch.Operation.INSERT));
        }
        return show.toString() + "\n" + diff.toString();
    }

    @GetMapping("test2")
    @ResponseStatus(HttpStatus.OK)
    public String test2(@RequestParam("str") String test) {
        System.out.println("aha?");
        List<TaskResult> taskResults = taskResultRepository.findAll();
        /*for (TaskResult taskResult : taskResults) {
            System.out.println(taskResult.getTaskId() + ": " + taskResult.getBrokenSentencesCount() +
                    " " + taskResult.getWrongTextCount() + " " +
                    taskResult.getPositiveEmotionsCount().addAndGet(taskResult.getNegativeEmotionsCount().intValue()));
        }*/
        StringTokenizer stringTokenizer = new StringTokenizer(test, ",");
        int cnt = 0;
        int[] args = new int[4];
        while (stringTokenizer.hasMoreTokens()) {
            args[cnt++] = Integer.parseInt(stringTokenizer.nextToken());
        }
        args[1] += 4;
        double e = Math.E;
        double res1 = Math.PI * Math.log(-Math.atan(1.3 * args[1] - 10) + 2) / Math.log(e) + 10;
        res1 *= 6.3489125794718040652210611515526;
        res1 += 12.305831203372289317158476382609;

        //0: 13.949372309773254
        //10: 9.643946813538529
        // times: 5.8066270155791913101469156905197
        /// tmp: 80.998802104301680424681488452429
        // delta: 19.001197895698319575318511547571
        args[0] += 1;
        double res0 = Math.PI * Math.log(-Math.atan(2 * args[0] - 20) + 2) / Math.log(e) + 10;
        res0 *= 5.8066270155791913101469156905197;
        res0 += 19.001197895698319575318511547571;

        //0 :13.918523899939487
        //14:10.053595335568621
        //times:6.4684248579558173553422908337857
        // tmp:90.030925980420725210936987275173
        // delta:9.969074019579274789063012724827
        args[2] += 1;
        double res2 = Math.PI * Math.log(-Math.atan(0.9 * args[2] - 12) + 2) / Math.log(e) + 10;
        res2 *= 6.4684248579558173553422908337857;
        res2 += 9.969074019579274789063012724827;

        return res0 + " " + res1 + " " + res2;
    }


}
