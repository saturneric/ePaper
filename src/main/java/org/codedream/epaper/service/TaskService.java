package org.codedream.epaper.service;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.article.GetSentenceFromArticle;
import org.codedream.epaper.component.json.model.JsonableBPTResult;
import org.codedream.epaper.component.json.model.JsonableTaskResult;
import org.codedream.epaper.component.task.BPTDivider;
import org.codedream.epaper.component.task.BPTQueue;
import org.codedream.epaper.component.task.JsonableTaskResultGenerator;
import org.codedream.epaper.component.task.LockedBPTs;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.exception.innerservererror.LowBatchLimitException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.task.SentenceResult;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.model.task.TaskResult;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.repository.task.BatchProcessingTaskRepository;
import org.codedream.epaper.repository.task.SentenceResultRepository;
import org.codedream.epaper.repository.task.TaskRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 批处理任务及子任务管理服务层，提供了任务相关的功能服务
 *
 * @see AsyncTaskServer
 * @see UserService
 * @see FileService
 * @see BPTQueue
 */
@Slf4j
@EnableScheduling
@Service
public class TaskService implements ITaskService {

    @Resource
    private IUserService userService;

    @Resource
    private IFileService fileService;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private BatchProcessingTaskRepository bptRepository;

    @Resource
    private BPTQueue bptQueue;

    @Resource
    private BPTDivider bptDivider;

    @Resource
    private SentenceResultRepository sentenceResultRepository;

    @Resource
    private GetSentenceFromArticle getSentenceFromArticle;

    @Resource
    private JsonableTaskResultGenerator resultGenerator;

    @Resource
    private IAsyncTaskServer asyncTaskServer;

    @Resource
    private LockedBPTs lockedBPTs;


    /**
     * 此函数用于创建一个新的任务。此过程中会对任务进行预处理，以得到包括DNN语言模型处理、文本纠错、段落划分等结果并将之持久化。
     *
     * @param userId 用户id
     * @param fileId 文件id
     * @param type   文件类型，目前只接受.doc/.docx
     * @return 创建好的任务的id
     */
    @Override
    public Integer registerTask(Integer userId, Integer fileId, String type) {
        log.info(String.format("Registering task(UserId:%d FileId:%d Type:%s)……", userId, fileId, type));

        Optional<User> user;
        Optional<File> file;

        Task task = new Task();

        if (userId != null) {
            user = userService.findUserById(userId);
            user.ifPresent(task::setUser);
        }

        if (fileId != null) {
            file = Optional.ofNullable(fileService.getFileInfo(fileId));
            file.ifPresent(task::setFile);
        }

        task.setType(type);
        task = taskRepository.save(task);

        // 异步执行子任务预处理与预分析
        asyncTaskServer.preprocessorAndAnalyse(task.getId());

        log.info(String.format("Task registered(UserId:%d FileId:%d Type:%s).", userId, fileId, type));
        return task.getId();
    }

    /**
     * 检查任务是否完成
     *
     * @param taskId 任务id
     * @return 一个布尔值，表示任务是否完成
     */
    @Override
    public boolean checkTaskFinished(Integer taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (!task.isPresent()) {
            throw new NotFoundException(taskId.toString());
        }
        return task.get().isFailed();
    }

    /**
     * 获取任务信息，亦即通过任务id查找任务
     *
     * @param taskId 任务id
     * @return 封装在Optional中的任务
     */
    @Override
    public Optional<Task> getTaskInfo(Integer taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (!task.isPresent()) throw new NotFoundException(taskId.toString());

        return task;
    }

    /**
     * 获取任务结果，亦即通过任务id查找任务结果
     *
     * @param taskId 任务id
     * @return 封装在Optional中的任务结果
     */
    @Override
    public Optional<TaskResult> getTaskResult(Integer taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (!task.isPresent()) throw new NotFoundException(taskId.toString());

        if (task.get().isFailed()) return Optional.empty();
        else return Optional.of(task.get().getResult());
    }

    /**
     * 线程安全地获取一个批处理任务。确保调用的批处理任务在调用后被标记已锁，并放入另一个队列中等待计算完毕。
     * 若在优先队列队首的任务都无法满足限制，则会将此批处理任务等分，并重新加入队列
     *
     * @param limit 发起调用请求的子服务器所接收的最大待处理的句子数量
     * @return 一个可用的批处理任务
     */
    @Override
    public BatchProcessingTask getBatchProcessingTask(Integer limit) {

        BatchProcessingTask bpt = null;
        try {
            // 检查队列是否为空，防止阻塞
            if (bptQueue.checkEmpty()) return null;
            Optional<BatchProcessingTask> oBpt = bptRepository.findById(bptQueue.getBptId());

            if (!oBpt.isPresent()) throw new InnerDataTransmissionException();
            bpt = oBpt.get();
            while (true) {
                // 批处理任务
                if (bpt.getSentences().size() > limit) {
                    if (bpt.getTasks().size() <= 1) {
                        throw new LowBatchLimitException();
                    } else {
                        divide(bpt);
                    }
                    oBpt = bptRepository.findById(bptQueue.getBptId());
                    if (!oBpt.isPresent()) {
                        throw new NotFoundException("BPT not found.");
                    }
                    bpt = oBpt.get();
                } else {
                    break;
                }
            }
            // 尝试次数递增 1
            bpt.setTryNumber(bpt.getTryNumber() + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != bpt) {
            bpt.setJoinDate(new Date());
            bpt = bptRepository.save(bpt);
            lockedBPTs.add(bpt.getId());
            bptQueue.removeBPT(bpt.getId());
        }
        return bpt;
    }

    /**
     * 生成用于返回前端的任务结果，以支持json格式的数据传递
     *
     * @param taskId 任务id
     * @return 封装好的任务结果
     * @see JsonableTaskResultGenerator#getJsonableTaskResult(Integer)
     */
    @Override
    public JsonableTaskResult getJsonableTaskResult(Integer taskId) {
        log.info(String.format("Getting task(Id: %d) result……", taskId));
        return resultGenerator.getJsonableTaskResult(taskId);
    }


    /**
     * 将一个批处理任务标记为已经完成。此过程将把之前锁住的批处理任务解锁，并持久化返回的结果
     *
     * @param bptId      批处理任务id
     * @param bptResults 批处理任务的结果，键值为句子的id，值为句子的处理结果。
     */
    @Override
    public void markBatchProcessingTaskFinished(Integer bptId, Map<Integer, JsonableBPTResult> bptResults) {

        Optional<BatchProcessingTask> batchProcessingTask = bptRepository.findById(bptId);
        if (!batchProcessingTask.isPresent()) throw new NotFoundException();
        BatchProcessingTask bptTask = batchProcessingTask.get();

        // 处理bpt中每个task的返回结果
        for (Task task : bptTask.getTasks()) {
            List<Sentence> sentences = getSentenceFromArticle.get(task.getArticle());
            task.getResult().setSuccess(true);
            for (Sentence sentence : sentences) {

                Optional<SentenceResult> optionalSentenceResult = sentenceResultRepository.findBySentenceId(sentence.getId());
                if (!optionalSentenceResult.isPresent()) {
                    throw new InnerDataTransmissionException("Sentence result not found");
                }
                SentenceResult sentenceResult = optionalSentenceResult.get();

                // 检查句结果是否是初始值
                if (sentenceResult.isInitStatus()) {
                    JsonableBPTResult sentenceRes = bptResults.get(sentence.getId());
                    int flag = 0;
                    float max = -1;
                    List<Float> res = sentenceRes.getTagPossible();
                    sentenceResult.setPossibilities(res);
                    for (int i = 0; i < 3; i++) {
                        float possibility = res.get(i);
                        if (max < possibility) {
                            max = possibility;
                            flag = i + 1;
                        }
                    }

                    switch (flag) {
                        case 1: {
                            sentenceResult.setNegative(true);
                            break;
                        }
                        case 2: {
                            sentenceResult.setNeutral(true);
                            break;
                        }
                        case 3: {
                            sentenceResult.setPositive(true);
                            break;
                        }
                        default: {
                        }
                    }
                    sentenceResult.setInitStatus(false);
                    sentenceResultRepository.save(sentenceResult);
                }
            }
        }

        bptTask.setTaskFinished();
        // 将批处理任务标记完结
        bptTask.setFinished(true);
        bptTask.setFinishDate(new Date());
        bptRepository.save(bptTask);
    }

    /**
     * 获取一个默认的批处理任务
     *
     * @return 一个默认的批处理任务
     */
    @Override
    public BatchProcessingTask getDefaultBPTTask() {
        return new BatchProcessingTask();
    }

    /**
     * 注册一个批处理任务，将之放入就绪队列中，等待来自计算端的调用
     *
     * @param bpt 需要注册的批处理任务
     */
    @Override
    public void registerBPTTask(BatchProcessingTask bpt) {
        bptQueue.addBPT(bpt.getId());
    }

    /**
     * 为一个批处理任务加锁，并放入处理等待队列，以便监听等待时间以及保证线程安全
     *
     * @param bptId 批处理任务id
     */
    @Override
    public void lockBPT(Integer bptId) {
        Optional<BatchProcessingTask> bpt = bptRepository.findById(bptId);
        if (bpt.isPresent()) {
            if (lockedBPTs.contains(bptId)) return;

            lockedBPTs.add(bpt.get().getId());
        }
    }

    /**
     * 为一个批处理任务解锁，通常要求子服务器先调用{@link #markBatchProcessingTaskFinished(Integer, Map)}，
     * 再调用此方法。如果此批处理任务未完成，并且尝试次数已经超过了3次，会将此批处理任务等分。
     * 参照{@link #divide(BatchProcessingTask)}
     *
     * @param id 批处理任务id
     */
    @Override
    public void unlockBPT(Integer id) {
        Iterator<Integer> iterator = lockedBPTs.iterator();
        while (iterator.hasNext()) {
            Integer bptId = iterator.next();
            if (bptId.equals(id)) {

                // 查找BPT
                Optional<BatchProcessingTask> bptOptional = bptRepository.findById(bptId);
                if (!bptOptional.isPresent()) throw new InnerDataTransmissionException();

                BatchProcessingTask bpt = bptOptional.get();

                // 如果BPT已被完成
                if (bpt.isFinished()) {
                    iterator.remove();
                    bpt.setSuccess(true);
                    bptRepository.save(bpt);
                } else {
                    if (bpt.getTryNumber() > 3) {
                        try {
                            if (bpt.getTasks().size() <= 1) {
                                return;
                            }
                            divide(bpt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public Iterable<Task> findHistoryTaskId(Integer userId) {
        return taskRepository.findAllByUserId(userId);
    }

    // 等分bpt
    private void divide(BatchProcessingTask batchProcessingTask) {
        List<BatchProcessingTask> batchProcessingTasks = bptDivider.divideBPT(batchProcessingTask);
        BatchProcessingTask bpt1 = batchProcessingTasks.get(0);
        BatchProcessingTask bpt2 = batchProcessingTasks.get(1);
        bptRepository.delete(batchProcessingTask);
        bptRepository.save(batchProcessingTasks.get(0));
        bptRepository.save(batchProcessingTasks.get(1));
        bptQueue.addBPT(bpt1.getId());
        bptQueue.addBPT(bpt2.getId());
        bptQueue.removeBPT(batchProcessingTask.getId());
    }

}
